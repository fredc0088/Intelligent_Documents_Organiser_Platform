package org.Fcocco01.DocumentClassifier.Visualisation

import java.awt.Toolkit

import org.Fcocco01.DocumentClassifier.Core.Clustering.HierarchicalClustering.Cluster
import scalafx.Includes._
import scalafx.scene.control.ScrollPane
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout._
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Circle, Polyline}
import scalafx.scene.text.Text
import scalafx.scene.{Parent, Scene}
import scalafx.stage.{Popup, Stage, WindowEvent}

object HierarchicalGraphic_Test {

  class Tree(cluster: Cluster)(panel: (Parent, Int, Int)) extends Scene(panel._1, panel._2, panel._3)
  object Tree {
    def apply(cluster: Cluster)(stage: Stage): Stage = {
      val screenSize = Toolkit.getDefaultToolkit.getScreenSize
      val (w, h) = ((screenSize.width * 0.9).toInt, (screenSize.height * 0.7).toInt)

      val root = new ScrollPane
      val center = new StackPane
      root.setContent(center)
      val pane = DrawDendrogram(cluster,(w, h))
      center.children.add(pane)
      val newP = new Tree(cluster)(root, w, h)
      stage.setOnCloseRequest((event: WindowEvent) => println("Close"))
      stage.setScene(newP)
      stage
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
      AnchorPane.setAnchors(anchorpane, 10.0 ,10.0 ,10.0 ,10.0)

      def drawNode(node: Cluster, x: Double, y: Double): Unit = {
        if (node.isLeaf) {
           val leaf = new Circle {
              centerX = x
              centerY = y
              radius = 3.5
              fill = Color.Silver
              onMouseClicked = (event: MouseEvent) =>
                NodeInfo(node.name).getInfo.show(anchorpane.getScene.getWindow)
            }
        anchorpane.children.add(leaf) }
        else {
          node.getChildren match {
            case None => {}
            case Some((left, right)) => {
              val (h1, h2) = (left.getHeight * 20, right.getHeight * 20)
              val (top, bottom) = (y - (h1 + h2) / 2, y + (h1 + h2) / 2)
              val ll = node.distance.getOrElse(0.0) * scaling + 10
              val merge = new Circle {
                  centerX = x
                  centerY = y
                  radius = 2
                  fill = Color.BlueViolet
                  onMouseClicked = (event: MouseEvent) =>
                    NodeInfo(node.name,Option(node.vectors.map(_.id))).getInfo.show(anchorpane.getScene.getWindow)
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

    case class NodeInfo(name : String, vectors : Option[Traversable[String]] = None) {
      def getInfo = {
        val popup = new Popup()
        popup.setAutoHide(true)
        popup.setHideOnEscape(true)
        val box = new VBox()
        box.children.addAll(new Text(box.width.value / 2, 20, name))
        vectors match {
          case Some(x) => box.children.add(new VBox(new ScrollPane() { content = new Text(x.foldRight("") { (a, b) => s"$a\n" + b })}))
          case None => {}
          }
        box.setStyle("-fx-background-color: SILVER;")
        popup.getContent.add(box)
        popup
      }
    }
  }
}
