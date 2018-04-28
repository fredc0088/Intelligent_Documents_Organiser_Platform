package org.Fcocco01.DocumentClassifier.Processes

import org.Fcocco01.DocumentClassifier.{Core, Utils,Visualisation}
import Core.Clustering.{FlatClustering, HierarchicalClustering}
import Visualisation.Plotting.HierarchicalGraphic.Dendrogram
import Visualisation.Plotting.FlatGraphic.SparseGraph

case class GraphicalResult() extends BaseProcess {
  def start(clusters: Either[HierarchicalClustering.Cluster, Traversable[FlatClustering.Cluster]]) = {
    def result = clusters match {
      case Left(x) => {
        Dendrogram(x)
      }
      case Right(x) => {
        SparseGraph(x.toSeq: _*)
      }
    }

    setProgress(Utils.Constants.TEN)

    result
  }
}
