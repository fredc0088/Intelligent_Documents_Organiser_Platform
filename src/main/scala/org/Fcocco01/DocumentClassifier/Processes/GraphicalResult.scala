package org.Fcocco01.DocumentClassifier.Processes

import org.Fcocco01.DocumentClassifier.{Essentials,Visualisation}
import Essentials.Types.TypeClasses.Clusters.{Flat, Hierarchical}
import Visualisation.Plotting.HierarchicalPlot.Dendrogram
import Visualisation.Plotting.FlatPlot.SparseGraph

case class GraphicalResult() extends BaseProcess {
  def start(clusters: Either[Hierarchical.Cluster, Traversable[Flat.Cluster]]) = {
    def result = clusters match {
      case Left(x) => {
        Dendrogram(x)
      }
      case Right(x) => {
        SparseGraph(x.toSeq: _*)
      }
    }

    setProgress(Essentials.Constants.TEN)

    result
  }
}
