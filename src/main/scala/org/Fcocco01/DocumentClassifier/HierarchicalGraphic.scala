package org.Fcocco01.DocumentClassifier

import scalafx.Includes._
import java.awt.Toolkit

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

        val drawer = new DendrogramDrawer(cluster, center,(w, h))
        drawer.draw

        root.setFitToHeight(true);
        val newP = new Tree(cluster)(root, w, h)
        stage.setOnCloseRequest((t: WindowEvent) => println("Close"))
        stage.setScene(newP)
        stage
      }
    }

  class DendrogramDrawer(cluster: Cluster, n: Pane, measures: (Double,Double)) {
    def draw = {
      val anchorpane = new AnchorPane
      anchorpane.setMaxSize(1000000000,1000000000)
//      anchorpane.autosize
      val (newW,newH) = (measures._1 * 0.7, measures._2 * 0.5)
      anchorpane.setMinSize(measures._1,measures._2)
      anchorpane.setPrefSize(measures._1,measures._2)

      drawTree(anchorpane, cluster,measures._1 ,measures._2)
//      n.setContent(anchorpane)
      n.autosize
      n.children.add(anchorpane)
    }

    def drawTree(n: AnchorPane, node: Cluster, width : Double, height: Double) = {
      val h = node.getHeight + height
      val w = width
      val depth = getDepth(node)
      val scaling = (w-1500)/depth
      var it = 1
      println("")
      println(s"Iteration ${it -1}: For node ${node.hashCode} the x is ${10} and y is ${h/2} and ll is ${node.distance.getOrElse(0.0) * scaling}")
      def drawnode(node : Cluster, x : Double, y : Int): Unit = {
        it += 1
        println(s"Iteration ${it -1}: For node ${node.hashCode} the x is ${x} and y is ${y} and ll is ${node.distance.getOrElse(0.0) * scaling}")
        if(node.isLeaf) {
          n.children.add({
//            val t = new Text(x+5,y-7,node.name)
            println(s"It is a leaf for ${node.name}")
            println("")
            val c = Circle(x,y, 5,Color.Red)
            c.onMouseClicked = (event: MouseEvent) => new Alert(AlertType.Information, node.name).showAndWait
            c})
        }
        else {
          node.getChildren match {
            case None => {}
            case Some((left,right)) => {
              println(s"Children are ${left.hashCode} and ${right.hashCode}")
              println("")
              val (h1,h2) = (left.getHeight * 20,right.getHeight * 20)
              val (top,bottom) = (y-(h1+h2)/2,y+(h1+h2)/2)
              val ll = node.distance.getOrElse(0.0) * (scaling) + 10
              val merge = {
                val c = Circle(x, y, 3,Color.BlueViolet)
//                val s = s"${node.name}\nVectors in the clusters:\n${node.vectors.map(x => s"${x.id}\n")}"
                val s = node.hashCode.toString
                c.onMouseClicked = (event: MouseEvent) => new Alert(AlertType.Information, s).showAndWait
                c}
//              val nextX = (b: Cluster) => b.isLeaf match {case true => (deepest * scaling) + x; case false => x+ll}
//              val nextX = (b:Cluster) => x+ll
              n.children.addAll(merge, Polyline(
                                  x, top+h1/2, x+ll, top+h1/2,
                                  x, top+h1/2, x, bottom-h2/2, // Central connecting line
                                  x, bottom-h2/2, x+ll, bottom-h2/2))
              drawnode(left, x+ll, (top+h1/2).toInt)
              drawnode(right, x+ll, (bottom-h2/2).toInt)
            }
          }
        }
      }
      drawnode(node, 10, (h/2).toInt)
    }

    def getDepth(c : Cluster) : Double = {
      c.getChildren match {
        case None => 0
        case Some((left,right)) => Math.max(getDepth(left),getDepth(right)) + c.distance.getOrElse(0.0)
      }
    }

    def getDeepestDistance(c: Cluster) = {
      c.getChildren match {
        case None => c.distance.getOrElse(0.0)
        case Some((left,right)) => Math.max(left.distance.getOrElse(0.0),right.distance.getOrElse(0.0))
      }
    }
  }

  class Info(a: AlertType, s : String) extends Dialog{

  }

  class Focus (c: Cluster, w: Double,h: Double) {
    val stage = new Stage
    val scene = new Scene
    val scroll = new ScrollPane
    val anchorPane = new AnchorPane
    anchorPane.setMaxSize(1000000000,1000000000)
    anchorPane.setMinSize(w,h)
    anchorPane.setPrefSize(w,h)
    //stage.setScene()
  }
}