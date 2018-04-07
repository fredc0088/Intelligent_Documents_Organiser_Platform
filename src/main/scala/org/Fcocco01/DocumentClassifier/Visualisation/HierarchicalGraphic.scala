package org.Fcocco01.DocumentClassifier.Visualisation

import java.awt.Toolkit

import org.Fcocco01.DocumentClassifier.{Core,Utils}
import Core.Clustering.HierarchicalClustering.Cluster
import Utils.Constants._
import scalafx.Includes._
import scalafx.scene.control.ScrollPane
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout._
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Circle, Polyline}
import scalafx.scene.text.Text
import scalafx.scene.{Parent, Scene}
import scalafx.stage.{Popup, Stage}

object HierarchicalGraphic {

  class Dendrogram(cluster: Cluster)(panel: (Parent, Int, Int)) extends Scene(panel._1, panel._2, panel._3)
  object Dendrogram {
    def apply(cluster: Cluster) = {
      val screenSize = Toolkit.getDefaultToolkit.getScreenSize
      val (w, h) = ((screenSize.width * ZEROSEVEN).toInt, (screenSize.height * HALF).toInt)

      val root = new ScrollPane
      val center = new StackPane
      root.setContent(center)
      val pane = DrawDendrogram(cluster,(w, h))
      center.children.add(pane)
      new Dendrogram(cluster)(root, w, h)
    }
  }

  object DrawDendrogram {
    def apply(cluster: Cluster, measures: (Double, Double)): Pane = {

      val (width, height) = measures
      val h = cluster.getHeight + height
      val w = width
      val depth = getDepth(cluster)
      val scaling = (w - HUNDREDFIFTY) / depth

      val anchorpane = new AnchorPane
      AnchorPane.setAnchors(anchorpane, TENF ,TENF ,TENF ,TENF)

      def drawNode(node: Cluster, x: Double, y: Double): Unit = {
        if (node.isLeaf) {
           val leaf = new Circle {
              centerX = x
              centerY = y
              radius = THREEHALF
              fill = Color.Silver
              onMouseClicked = (event: MouseEvent) =>
                NodeInfo(node.name).getInfo.show(anchorpane.getScene.getWindow)
            }
        anchorpane.children.add(leaf) }
        else {
          node.getChildren match {
            case None => {}
            case Some((left, right)) => {
              val (h1, h2) = (left.getHeight * TWENTY, right.getHeight * TWENTY)
              val (top, bottom) = (y - (h1 + h2) / TWO, y + (h1 + h2) / TWO)
              val ll = node.distance.getOrElse(ZEROF) * scaling + TEN
              val merge = new Circle {
                  centerX = x
                  centerY = y
                  radius = TWOHALF
                  fill = Color.BlueViolet
                  onMouseClicked = (event: MouseEvent) => focus(node).showAndWait()
                }
              anchorpane.children.addAll(merge, Polyline(
                x, top + h1 / TWO, x + ll, top + h1 / TWO,
                x, top + h1 / TWO, x, bottom - h2 / TWO, // Central connecting line
                x, bottom - h2 / TWO, x + ll, bottom - h2 / TWO))
              drawNode(left, x + ll, top + h1 / TWO)
              drawNode(right, x + ll, bottom - h2 / TWO)
            }
          }
        }
      }
      drawNode(cluster, TEN, h / TWO)
      anchorpane
    }

    def getDepth(c: Cluster): Double =
      c.getChildren match {
        case None => ZERO
        case Some((left, right)) => Math.max(getDepth(left), getDepth(right)) + c.distance.getOrElse(0.0)
      }

    case class NodeInfo(name : String, vectors : Option[Traversable[String]] = None) {
      def getInfo = {
        val popup = new Popup()
        popup.setAutoHide(true)
        popup.setHideOnEscape(true)
        val box = new VBox()
        box.children.add(new Text(box.width.value / TWO, TWENTY, name))
        box.setStyle(
          "-fx-background-color: WHITE;-fx-border-color:#545454;-fx-border-width: 1px;-fx-border-style: solid;fx-font-weight: bold")
        popup.getContent.add(box)
        popup
      }
    }

    private def focus(c: Cluster) = {
      val stage = new Stage
      val scene = Dendrogram(c)
      scene.setOnMouseEntered((event: MouseEvent) =>
        scene.setOnMouseExited((event: MouseEvent) =>
          stage.close()
        )
      )
      stage.setTitle(s"Focus: $c.name")
      stage.setScene(scene)
      stage
    }
  }
}
