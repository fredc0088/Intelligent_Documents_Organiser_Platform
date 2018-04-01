package org.Fcocco01.DocumentClassifier

import scalafx.Includes._
import java.awt.Toolkit

import javafx.scene.transform.Rotate
import org.Fcocco01.DocumentClassifier.Clustering.HierarchicalClustering.Cluster
import scalafx.geometry.Pos
import scalafx.scene._
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.layout._
import scalafx.scene.control.{Alert, Dialog, ScrollPane}
import scalafx.scene.input.MouseEvent
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Circle, Line, Polyline}
import scalafx.stage.{Stage, WindowEvent}
import scalafx.scene.text.Text


object HierarchicalGraphic {

    class Tree(cluster: Cluster)(panel: (Parent, Int, Int)) extends Scene(panel._1, panel._2, panel._3)
    object Tree {
      def apply(cluster: Cluster)(stage: Stage): Stage = {
        val screenSize = Toolkit.getDefaultToolkit.getScreenSize
        val (w, h) = ((screenSize.width * 0.9).toInt, (screenSize.height * 0.7).toInt)

        val root = new ScrollPane
        val center = new StackPane
        root.setContent(center)

        DrawDendrogram(cluster, center,(w, h))
        root.setFitToHeight(true);
        val newP = new Tree(cluster)(root, w, h)
        stage.setOnCloseRequest((t: WindowEvent) => println("Close"))
        stage.setScene(newP)
        stage
      }
    }

  object DrawDendrogram {
    def apply(cluster: Cluster, n: Pane, measures: (Double, Double)): Unit = {

      val (width, height) = measures
      val h = cluster.getHeight + height
      val w = width
      val depth = getDepth(cluster)
      val scaling = (w - 500) / depth

      val anchorpane = new AnchorPane
      AnchorPane.setTopAnchor(anchorpane, 100.0)
      AnchorPane.setLeftAnchor(anchorpane, 10.0)
      AnchorPane.setRightAnchor(anchorpane, 10.0)
      AnchorPane.setBottomAnchor(anchorpane, 10.0)

      //      anchorpane.setMaxSize(1000000000,1000000000)
      //      anchorpane.setMinSize(measures._1,measures._2)
      //      anchorpane.setPrefSize(measures._1,measures._2
      //      val (newW,newH) = (measures._1 * 0.7, measures._2 * 0.5)


      var it = 0

      def drawNode(node: Cluster, x: Double, y: Double): Unit = {
        it += 1
        println(s"Iteration ${it - 1}: For node ${node.hashCode} the x is ${x} and y is ${y} and ll is ${node.distance.getOrElse(0.0) * scaling}")
        if (node.isLeaf) {
          anchorpane.children.add({
            //            val t = new Text(x+5,y-7,node.name)
            println(s"It is a leaf for ${node.name}")
            println("")
            val c = Circle(x, y, 3.5, Color.Red)
            c.onMouseClicked = (event: MouseEvent) => new Alert(AlertType.Information, node.name).showAndWait
            c
          })
        }
        else {
          node.getChildren match {
            case None => {}
            case Some((left, right)) => {
              println(s"Children are ${left.hashCode} and ${right.hashCode}")
              println("")
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

      //      n.setMinHeight(anchorpane.minHeight.value)
      //      n.setMaxWidth(anchorpane.minWidth.value)
      n.children.add(anchorpane)
      //      n.transforms.add(new Rotate(90, Rotate.X_AXIS))
    }


    def getDepth(c: Cluster): Double =
      c.getChildren match {
        case None => 0
        case Some((left, right)) => Math.max(getDepth(left), getDepth(right)) + c.distance.getOrElse(0.0)
      }
  }

}