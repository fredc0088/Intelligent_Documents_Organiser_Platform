package org.Fcocco01.DocumentClassifier.Processes

import org.Fcocco01.DocumentClassifier.{Core,Utils}
import Core.Clustering.{DVector, Distance, FlatClustering, Similarity, HierarchicalClustering}
import FlatClustering.{K_Means, printClusters}
import HierarchicalClustering._
import Utils.Util.Time.currentTimeMins

case class ClusteringProcess(clusteringMode: String, comparison: String) extends BaseProcess {

  private type Return = Either[HierarchicalClustering.Cluster, Traversable[FlatClustering.Cluster]]
  private type Returner = Traversable[DVector] => Return

  def start(linkStrategy: String, clustersNumber: Int): Returner =
    (vectors: Traversable[DVector]) => {

      val time = System.nanoTime

      println("Begin corpus creation")

      val compareFun = comparison match {
        case "Cosine Sim" => Similarity.cosine(_, _)
        case "Euclidean Dist" => Distance.euclidean(_, _)
        case "Manhattan Dist" => Distance.manhattan(_, _)
      }

      val result : Return = if (clusteringMode == "Hierarchical") {
        val matrix = createSimMatrix(vectors, compareFun)

        println("Similarity matrix generated in  " + currentTimeMins(time))

        val docWrappedInCluster = (x: Seq[DVector]) => x.map(SingleCluster(_)).toList
        val clusters = linkStrategy match {
          case "Single Link" => HAC(matrix, docWrappedInCluster, Single_Link, vectors.toSeq: _*)
          case "Complete Link" => HAC(matrix, docWrappedInCluster, Complete_Link, vectors.toSeq: _*)
        }

        Left(clusters)
      } else {

        val numberOfClusters = clustersNumber

        val clusters: Vector[FlatClustering.Cluster] = K_Means(numberOfClusters)(compareFun)(vectors.toSeq: _*)

        printClusters(clusters: _*)

        Right(clusters)
      }

      println("Clustering in  " + currentTimeMins(time))

      setProgress(Utils.Constants.NINE)

      result
    }
}
