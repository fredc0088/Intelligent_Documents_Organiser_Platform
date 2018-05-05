package org.Fcocco01.DocumentClassifier.Visualisation.Plotting

import java.awt.Toolkit
import java.io.File

import javafx.scene.control.Alert
import javafx.stage.DirectoryChooser
import org.Fcocco01.DocumentClassifier.Essentials
import Essentials.Types.TypeClasses.Clusters.Hierarchical.Cluster
import Essentials.Constants._
import Essentials.Types.TypeClasses.Vectors.EmptyVector
import Essentials.Util.ErrorHandling.logAwayErrorsAndExceptions
import org.apache.commons.io.FileUtils
import scalafx.Includes._
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Button, ScrollPane, TextArea}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout._
import scalafx.scene.paint.{Color, Paint}
import scalafx.scene.shape.{Circle, Polyline}
import scalafx.scene.text.Text
import scalafx.scene.{Group, Parent, Scene}
import scalafx.stage.{Popup, Stage}

import scala.util.{Failure, Success, Try}

object HierarchicalPlot {

  /**
    *
    * @param cluster the root [[Cluster]]
    * @param panel
    */
  class Dendrogram(cluster: Cluster)(panel: (Parent, Int, Int)) extends Scene(panel._1, panel._2, panel._3)
  object Dendrogram {
    def apply(cluster: Cluster): Dendrogram = {
      val screenSize = Toolkit.getDefaultToolkit.getScreenSize
      val (w, h) = ((screenSize.width * ZERO_NINE).toInt, (screenSize.height * ZERO_SEVEN).toInt)

      val root = new ScrollPane
      val center = new StackPane
      center.autosize
      val addPane = new AnchorPane
      addPane.children.add(center)
      addPane.children.add(ControlNewFileSystem(cluster))
      addPane.autosize
      root.autosize
      root.setContent(addPane)
      val pane = DrawDendrogram(cluster,(w, h))
      pane.setAutoSizeChildren(true)
      center.children.add(pane)
      new Dendrogram(cluster)(root, w, h)
    }

    /**
      * Button to create a file system starting from the root cluster given.
      *
      * @param cluster the root [[Cluster]]
      */
    private class ControlNewFileSystem(cluster: Cluster)
    object ControlNewFileSystem {
      def apply(cluster: Cluster): Button = {
        val create : Button = new Button("Generate file system")
        create.setOnMouseClicked((event: MouseEvent) =>
          if(event.isStillSincePress) {
            val directoryChooser = new DirectoryChooser
            val selectedDirectory = directoryChooser.showDialog(null)
            if (selectedDirectory != null) {
              val result = Try {
                createFileSystem(cluster, selectedDirectory.getCanonicalPath)
              }
              result match {
                case Success(_) => new Alert(
                  AlertType.Information, s"New file system created in ${selectedDirectory.getCanonicalPath}.")
                  .showAndWait
                case Failure(y) =>
                  logAwayErrorsAndExceptions(y)
                  new Alert(AlertType.Error, "Some error occured. Please check error log")
              }
            }
          }
        )
        create.setOnMouseDragged((e : MouseEvent) => {
          create.setLayoutX(e.getSceneX)
          create.setLayoutY(e.getSceneY)
        })
        create
      }

      private def createFileSystem(cluster: Cluster, root: String): Unit = {
        def help(cluster: Cluster,dir: String): Unit = {
          cluster.getChildren match {
            case None =>
              val path = new File(dir)
              path.mkdirs
              FileUtils.copyFileToDirectory(new File(cluster.name),path)
            case Some((left,right)) =>
              help(left, s"$dir/${cluster.name}")
              help(right, s"$dir/${cluster.name}")
          }
        }
        help(cluster,root + "/" + cluster.name)
      }
    }
    
  }

  /**
    * Contains a mean to draw a dendrogram plot.
    */
  object DrawDendrogram {
    /**
      * This will draw the dendrogram based on the parameters given.
      *
      * @param cluster the root cluster node
      * @param measures initial width and height
      * @return a [[Group]] containing the elements forming the dendrogram.
      */
    def apply(cluster: Cluster, measures: (Double, Double)): Group = {

      val (width, height) = measures
      val h = cluster.getHeight + height
      val w = width
      val depth = getDepth(cluster)
      val scaling = (w - THREE_HUNDRED) / depth

      val pane = new Group
      pane.setAutoSizeChildren(true)
      /**
        * Recursively draw the dendrogram.
        *
        * @param node the current cluster node
        * @param x
        * @param y
        */
      //noinspection ScalaUnusedSymbol
      def drawNode(node: Cluster, x: Double, y: Double): Unit = {
        node.getChildren match {
          case None => pane.children.add(
            CircleNode(node, x, y, FIVE, (event: MouseEvent) =>
              NodeInfo(node).getInfo.show(pane.getScene.getWindow), Color.Silver))
          case Some((left,right)) =>
            val (h1, h2) = (left.getHeight * THIRTY, right.getHeight * THIRTY)
            val (top, bottom) = (y - (h1 + h2) / TWO, y + (h1 + h2) / TWO)
            val ll = node.distance.getOrElse(ZERO.toDouble) * scaling + TWENTY
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
      drawNode(cluster, TWENTY, h / TWO)
      pane.autosize
      pane
    }

    /**
      * Get the max depth from the bottom to the top
      * of the cluster's tree structure, to calculate how deep
      * the diagram can go.
      * Used for scaling.
      *
      * @param c top [[Cluster]]
      * @return a [[Double]] value as the max depth
      */
    def getDepth(c: Cluster): Double =
      c.getChildren match {
        case None => ZERO
        case Some((left, right)) => Math.max(getDepth(left), getDepth(right)) + c.distance.getOrElse(0.0)
      }

    /**
      * Create a popup window with information about the [[Cluster]] node,
      * to use with a leaf.
      *
      * @param node the [[Cluster]] leaf in exam
      * @param vectors if not [[None]], the path of the single vector inside a collection
      */
    //noinspection ScalaUnusedSymbol
    case class NodeInfo(node : Cluster, vectors : Option[Traversable[String]] = None) {

      def getInfo: Popup = {
        val popup = new Popup
        popup.setHideOnEscape(true)
        val box = new VBox
        val t = new Text(box.width.value / TWO, TWENTY, node.name){
          //noinspection ScalaUnusedSymbol
          onMouseClicked = (event: MouseEvent) => Essentials.Util.I_O.openFromPath(node.name)
        }
        t.setStyle("-fx-border-style: solid;-fx-font-weight: bold")
        val buttonView = new Button("View Vector") {
          onMouseClicked = (event: MouseEvent) => {
            val vector = node.vectors.headOption.getOrElse(EmptyVector).features
            val vectorRepresentation = new TextArea
            vectorRepresentation.setText(vector.map(v => s"${v._1} ----> ${v._2}").mkString("\n"))
            val hbox = new HBox { children = vectorRepresentation }
            val scrollPane = new ScrollPane {content = hbox}
            box.children.add(scrollPane)
            disable = true
          }
        }
        val buttonTopic = new Button("Main topic") {
          onMouseClicked = (event: MouseEvent) =>
            new Alert(AlertType.Information, s"${node.vectors.head.mainTopic}")
          .showAndWait
        }
        val buttonClose = new Button("Close") { onMouseClicked = (event: MouseEvent) => popup.hide }
        val topButtons = new HBox
        topButtons.children.addAll(buttonView,buttonTopic)
        val buttonsBox = new VBox
        buttonsBox.children.addAll(topButtons,buttonClose)
        box.children.addAll(t,buttonsBox)
        box.setStyle("-fx-background-color: WHITE;-fx-border-color:#545454;-fx-border-width: 1px;")
        box.setOnMouseDragged((event : MouseEvent) => {
          box.setLayoutX(event.getSceneX)
          box.setLayoutY(event.getSceneY)
        })
        popup.getContent.add(box)
        popup
      }
    }

    /**
      * Will open a window to show the part of the dendrogram cut from the selected node to
      * the bottom.
      *
      * @param c the selected cluster
      * @return a new window
      */
    private def focus(c: Cluster) = {
      val stage = new Stage
      val scene = Dendrogram(c)
      scene.setOnMouseEntered((event: MouseEvent) =>
        scene.setOnMouseExited((event: MouseEvent) =>
          stage.close
        )
      )
      stage.setTitle(s"Focus: $c.name")
      stage.setScene(scene)
      stage
    }

    /**
      * Draw a filled circle.
      *
      * @param node the current node
      * @param x
      * @param y
      * @param r the radius
      */
    class CircleNode(node: Cluster, x: Int, y: Int, r: Int )
    object CircleNode {
      /**
        * apply method for [[CircleNode]]
        *
        * @param node the current node
        * @param x
        * @param y
        * @param r the radius
        * @param behaviousOnClick action if the circle is clicked
        * @param color filling
        * @return a new circle with the set properties
        */
      def apply(node : Cluster, x: Double, y: Double, r: Double,
                behaviousOnClick: MouseEvent => Unit, color : Paint): Circle =
        new Circle { centerX = x; centerY = y; radius = r; fill = color; onMouseClicked = behaviousOnClick }
    }
  }
}
