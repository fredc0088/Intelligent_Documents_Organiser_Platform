package org.Fcocco01.DocumentClassifier.Processes

import org.Fcocco01.DocumentClassifier.{Core,Essentials}
import Core.Clustering.{DVector, Distance, FlatClustering, Similarity, HierarchicalClustering}
import FlatClustering.{K_Means, printClusters}
import HierarchicalClustering._
import Essentials.Util.Time.currentTimeMins
import Essentials.Types.TypeClasses.Clusters.{Flat,Hierarchical}
import Hierarchical.SingleCluster

case class ClusteringProcess(clusteringMode: String, comparison: String) extends BaseProcess {

  private type Return = Either[Hierarchical.Cluster, Traversable[Flat.Cluster]]
  private type Returner = Traversable[DVector] => Return

  def start(linkStrategy: String, clustersNumber: Int): Returner =
    (vectors: Traversable[DVector]) => {

      val time = System.nanoTime

      println("Begin corpus creation")

      val compareFun = comparison match {
        case "Cosine Sim" => Similarity.cosine _
        case "Euclidean Dist" => Distance.euclidean _
        case "Manhattan Dist" => Distance.manhattan _
      }

      val result : Return = if (clusteringMode == "Hierarchical") {
        val matrix = createSimMatrix(vectors, compareFun)

        println("Similarity matrix generated in  " + currentTimeMins(time))

        val docWrappedInCluster = (x: Seq[DVector]) => x.map(SingleCluster).toList
        val clusters = linkStrategy match {
          case "Single Link" =>
            if(comparison.contains("Dist"))agglomerative_HC(matrix, docWrappedInCluster, Complete_Link)
            else agglomerative_HC(matrix, docWrappedInCluster, Single_Link)
          case "Complete Link" =>
            if(comparison.contains("Dist"))agglomerative_HC(matrix, docWrappedInCluster, Single_Link)
            else agglomerative_HC(matrix, docWrappedInCluster, Complete_Link)
        }

        Left(clusters)
      } else {

        val numberOfClusters = clustersNumber

        val clusters: Vector[Flat.Cluster] = K_Means(numberOfClusters)(compareFun)(vectors.toSeq: _*)

        printClusters(clusters: _*)

        Right(clusters)
      }

      println("Clustering in  " + currentTimeMins(time))

      setProgress(Essentials.Constants.NINE)

      result
    }
}
