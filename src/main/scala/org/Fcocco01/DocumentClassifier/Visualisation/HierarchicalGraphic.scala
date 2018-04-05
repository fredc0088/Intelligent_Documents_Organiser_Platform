package org.Fcocco01.DocumentClassifier.Visualisation

import java.awt.Toolkit

import org.Fcocco01.DocumentClassifier.Core.Clustering.HierarchicalClustering.Cluster
import scalafx.Includes._
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, ScrollPane}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{AnchorPane, Pane, StackPane}
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Circle, Polyline}
import scalafx.scene.{Parent, Scene}
import scalafx.stage.{Stage, WindowEvent}

object HierarchicalGraphic {

    class Tree(cluster: Cluster)(panel: (Parent, Int, Int)) extends Scene(panel._1, panel._2, panel._3)
    object Tree {
      def apply(cluster: Cluster)(stage: Stage): Scene = {
        val screenSize = Toolkit.getDefaultToolkit.getScreenSize
        val (w, h) = ((screenSize.width * 0.9).toInt, (screenSize.height * 0.7).toInt)

        val root = new ScrollPane
        val center = new StackPane
        root.setContent(center)
        val pane = DrawDendrogram(cluster,(w, h))
        center.children.add(pane)
//        root.setFitToHeight(true);
        new Tree(cluster)(root, w, h)
//        stage.setOnCloseRequest((event: WindowEvent) => println("Close"))
//        stage.setScene(newP)
//        stage
      }
    }

  object DrawDendrogram {
    def apply(cluster: Cluster, measures: (Double, Double)): Pane = {

      val (width, height) = measures
      val h = cluster.getHeight + height
      val w = width
      val depth = getDepth(cluster)
      val scaling = (w - 800) / depth

      val anchorpane = new AnchorPane
      AnchorPane.setTopAnchor(anchorpane, 100.0)
      AnchorPane.setLeftAnchor(anchorpane, 10.0)
      AnchorPane.setRightAnchor(anchorpane, 10.0)
      AnchorPane.setBottomAnchor(anchorpane, 10.0)

      def drawNode(node: Cluster, x: Double, y: Double): Unit = {
        if (node.isLeaf) {
          anchorpane.children.add({
            val c = Circle(x, y, 3.5, Color.Red)
            c.onMouseClicked = (event: MouseEvent) => new Alert(AlertType.Information, node.name).showAndWait
            c
          })
        }
        else {
          node.getChildren match {
            case None => {}
            case Some((left, right)) => {
              val (h1, h2) = (left.getHeight * 20, right.getHeight * 20)
              val (top, bottom) = (y - (h1 + h2) / 2, y + (h1 + h2) / 2)
              val ll = node.distance.getOrElse(0.0) * scaling + 10
              val merge = {
                val c = Circle(x, y, 3.5, Color.BlueViolet)
                val s = node.hashCode.toString
                c.onMouseClicked = (event: MouseEvent) => new Alert(AlertType.Information, s).showAndWait
                c
              }
              anchorpane.children.addAll(merge, Polyline(
                x, top + h1 / 2, x + ll, top + h1 / 2,
                x, top + h1 / 2, x, bottom - h2 / 2, // Central connecting line
                x, bottom - h2 / 2, x + ll, bottom - h2 / 2))
              drawNode(left, x + ll, top + h1 / 2)
              drawNode(right, x + ll, bottom - h2 / 2)
            }
          }
        }
      }

      drawNode(cluster, 10, h / 2)
      anchorpane
    }


    def getDepth(c: Cluster): Double =
      c.getChildren match {
        case None => 0
        case Some((left, right)) => Math.max(getDepth(left), getDepth(right)) + c.distance.getOrElse(0.0)
      }
  }

}
