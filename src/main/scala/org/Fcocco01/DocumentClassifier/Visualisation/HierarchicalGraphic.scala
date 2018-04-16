package org.Fcocco01.DocumentClassifier.Visualisation

import java.awt.Toolkit

import org.Fcocco01.DocumentClassifier.{Core, Utils}
import Core.Clustering.HierarchicalClustering.Cluster
import Utils.Constants._
import Utils.Types.TypeClasses.Vectors.EmptyV
import scalafx.Includes._
import scalafx.scene.control.{Button, ScrollPane, TextArea}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout._
import scalafx.scene.paint.{Color, Paint}
import scalafx.scene.shape.{Circle, Polyline}
import scalafx.scene.text.Text
import scalafx.scene.{Group, Parent, Scene}
import scalafx.stage.{Popup, Stage}

object HierarchicalGraphic {

  class Dendrogram(cluster: Cluster)(panel: (Parent, Int, Int)) extends Scene(panel._1, panel._2, panel._3)
  object Dendrogram {
    def apply(cluster: Cluster) = {
      val screenSize = Toolkit.getDefaultToolkit.getScreenSize
      val (w, h) = ((screenSize.width * ZERO_NINE).toInt, (screenSize.height * ZERO_SEVEN).toInt)

      val root = new ScrollPane
      val center = new StackPane
      center.autosize
      root.autosize
      root.setContent(center)
      val pane = DrawDendrogram(cluster,(w, h))
      center.children.add(pane)
      val x = new Dendrogram(cluster)(root, w, h)
      x
    }
  }

  object DrawDendrogram {
    def apply(cluster: Cluster, measures: (Double, Double)): Group = {

      val (width, height) = measures
      val h = cluster.getHeight + height
      val w = width
      val depth = getDepth(cluster)
      val scaling = (w - THREE_HUNDRED) / depth

      val pane = new Group()
      pane.setAutoSizeChildren(true)

//      AnchorPane.setAnchors(anchorpane, TENF ,TENF ,TENF ,TENF)

      def drawNode(node: Cluster, x: Double, y: Double): Unit = {
        node.getChildren match {
          case None => pane.children.add(
            CircleNode(node, x, y, FIVE, (event: MouseEvent) =>
              NodeInfo(node).getInfo.show(pane.getScene.getWindow), Color.Silver))
          case Some((left,right)) => {
            val (h1, h2) = (left.getHeight * THIRTY, right.getHeight * THIRTY)
            val (top, bottom) = (y - (h1 + h2) / TWO, y + (h1 + h2) / TWO)
            val ll = node.distance.getOrElse(ZERO_F) * scaling + TWENTY
            val mergePoint =
              CircleNode(node, x, y, FOUR, (event: MouseEvent) => focus(node).showAndWait(), Color.BlueViolet)
            pane.children.addAll(mergePoint, Polyline(
              x, top + h1 / TWO, x + ll, top + h1 / TWO,
              x, top + h1 / TWO, x, bottom - h2 / TWO, // Central connecting line
              x, bottom - h2 / TWO, x + ll, bottom - h2 / TWO))
            drawNode(left, x + ll, top + h1 / TWO)
            drawNode(right, x + ll, bottom - h2 / TWO)
          }
        }
      }
      drawNode(cluster, TEN, h / TWO)
      pane.autosize
      pane
    }

    def getDepth(c: Cluster): Double =
      c.getChildren match {
        case None => ZERO
        case Some((left, right)) => Math.max(getDepth(left), getDepth(right)) + c.distance.getOrElse(0.0)
      }

    case class NodeInfo(node : Cluster, vectors : Option[Traversable[String]] = None) {

      def getInfo = {
        val popup = new Popup
        popup.setHideOnEscape(true)
        val box = new VBox
        val t = new Text(box.width.value / TWO, TWENTY, node.name)
        t.setStyle("-fx-border-style: solid;-fx-font-weight: bold")
        val buttonView = new Button("View Vector") {
          onMouseClicked = (event: MouseEvent) => {
            val vector = node.vectors.headOption.getOrElse(EmptyV).apply
            val vectorRepresentation = new TextArea
            vectorRepresentation.setText(vector.map(v => s"${v._1} ----> ${v._2}").mkString("\n"))
            val hbox = new HBox { children = vectorRepresentation }
            val scrollPane = new ScrollPane {content = hbox}
            box.children.add(scrollPane)
            disable = true
          }
        }
        val buttonClose = new Button("Close") { onMouseClicked = (event: MouseEvent) => popup.hide }
        val buttonsBox = new VBox
        buttonsBox.children.addAll(buttonView,buttonClose)
        box.children.addAll(t,buttonsBox)
        box.setStyle("-fx-background-color: WHITE;-fx-border-color:#545454;-fx-border-width: 1px;")
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

    class CircleNode(node: Cluster, x: Int, y: Int, r: Int )
    object CircleNode {
      def apply(node : Cluster, x: Double, y: Double, r: Double,
                behaviousOnClick: MouseEvent => Unit, color : Paint): Circle =
        new Circle { centerX = x; centerY = y; radius = r; fill = color; onMouseClicked = behaviousOnClick }
    }
  }
}
