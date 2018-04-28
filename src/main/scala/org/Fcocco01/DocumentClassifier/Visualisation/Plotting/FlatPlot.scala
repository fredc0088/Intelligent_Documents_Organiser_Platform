package org.Fcocco01.DocumentClassifier.Visualisation.Plotting

import java.awt.Toolkit

import org.Fcocco01.DocumentClassifier.Core.Clustering.FlatClustering.Cluster
import org.Fcocco01.DocumentClassifier.Utils.Constants.{ZERO_NINE, ZERO_SEVEN}
import scalafx.scene.control.ScrollPane
import scalafx.scene.text.Text
import scalafx.scene.{Group, Parent, Scene}

object FlatPlot {

  class SparseGraph(clusters: Cluster*)(panel: (Parent, Int, Int)) extends Scene(panel._1, panel._2, panel._3)
  object SparseGraph {
    def apply(cluster: Cluster*) = {
      val screenSize = Toolkit.getDefaultToolkit.getScreenSize
      val (w, h) = ((screenSize.width * ZERO_NINE).toInt, (screenSize.height * ZERO_SEVEN).toInt)

      val root = new ScrollPane
      root.setContent(TempRepresentation(cluster: _*))
      new SparseGraph(cluster: _*)(root,w,h)
    }
  }

  class TempRepresentation(clusters: Cluster*)
  object TempRepresentation {
    def apply(clusters: Cluster*) = {
        val visual : String = clusters.zipWithIndex.map {
          x => s"Cluster ${x._2} with name ${x._1.name}:\n ${
            x._1.vectorsID.map { y => s"            ${y}\n"
            }.reduce(_ + _)}\n"
        }.reduce(_ + _)
      val node = new Group
      node.setAutoSizeChildren(true)
      node.getChildren.add(new Text(visual))
      node
    }
  }
}
