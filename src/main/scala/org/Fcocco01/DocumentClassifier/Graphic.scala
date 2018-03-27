package org.Fcocco01.DocumentClassifier

import scalafx.application.JFXApp
import scalafx.Includes._
import scalafx.stage._
import java.awt._
import java.awt.geom._

import javax.swing.{JFrame, JPanel}
import scalafx.scene.canvas.Canvas
import scalafx.scene.layout._
import scalafx.scene.shape.Circle
import scalafx.scene.shape.Line
import javafx.geometry.Insets
import javafx.scene.paint.{Color => JFXColor}
import javafx.stage.Stage
import org.Fcocco01.DocumentClassifier.Clustering.HierarchicalClustering._
import scalafx.scene.Scene


object Graphic extends {

  object Hierarchy {

//    class Dendrogram extends JFXApp {
//
//    }


    object Main extends JFXApp {
//      stage = new Stage {
//
//      }
    }

    class Dendrogram extends JFrame {



      //def paint() = repaint()

      def paintComponent(g: Graphics2D): Unit = {
        super.paint(g)
        this.setBackground(Color.BLACK)

        g.setColor(Color.white)
        g.fillRect(25, 25, 100, 30)
        g.setColor(new Color(190, 81, 215))


      }

      def paint(g: Graphics2D) : Unit = {

      }


      final case class Coord(x: Double, y: Double) {
        override def equals(obj: Any): Boolean = obj.isInstanceOf[Coord] match {
          case true => x == obj.asInstanceOf[Coord].x && y == obj.asInstanceOf[Coord].y
          case false => false
        }
      }

//      case class MergePoint(override val radius: Double) extends Circle(radius) {
//
//      }

      def mergingPoint(g: Graphics2D, c: Cluster, x: Double, y: Double, scaling: Double) = {
      }

      def drawNode(g: Graphics2D, c: Option[Cluster], x: Double, y: Double, scaling: Double) = {
        c match {
          case None => {}
          case Some(i) => i match {
            case j : MultiCluster => {
              val (left,right) = j.getChildren.get
              val h1 = left.getHeight * 20
              val h2 = right.getHeight * 20
              val top = y - (h1 + h2) / 2
              val bottom = y + (h1 + h2) / 2
              // length of straight line
              val ll = c.get * scaling

              g.setColor(Color.BLUE)

              // straight vertical line from cluster to child
              g.drawLine(x, top + h1 / 2, x, bottom - h2 / 2)

              // horizontal line to the left item
              g.drawLine(x, top + h1 / 2, x + ll, top + h1 / 2)

              // right
              g.drawLine(x, bottom - h2 / 2, x + ll, bottom - h2 / 2)

              drawnode(g, left, x + ll, top + h1 / 2, scaling, labels)
              drawnode(g, right, x + ll, bottom - h2 / 2, scaling, labels)
            }
            case z : SingleCluster => {}
          }
        }
      }



//
//
//
//
//            class MergingPoint(c: Cluster,initialXY: VCoord,linkXY: VCoord) {
//              val namePadding : Int = 6
//              override def paint(g: Graphics2D, xDisplayOffset: Int, yDisplayOffset: Int, xDisplayFactor: Double, yDisplayFactor: Double, decorated: Boolean): Unit = {
//                val fontMetrics = g.getFontMetrics
//                val x1 = (initPoint.getX * xDisplayFactor + xDisplayOffset).toInt
//                val y1 = (initPoint.getY * yDisplayFactor + yDisplayOffset).toInt
//                val x2 = (linkPoint.getX * xDisplayFactor + xDisplayOffset).toInt
//                val y2 = 0
//                g.fillOval(x1 - dotRadius, y1 - dotRadius, dotRadius * 2, dotRadius * 2)
//                g.drawLine(x1, y1, x2, y2)
//                if (c.getHeight == 0) g.drawString(c.n,x1 + namePadding, y1 + (fontMetrics.getHeight / 2) - 2)
//                if (decorated && c.getDistance != null && !c.getDistance.isNaN && c.getDistance > 0) {
//                  val s = String.format("%.2f", c.getDistance)
//                  val rect = fontMetrics.getStringBounds(s, g)
//                  g.drawString(s, x1 - rect.getWidth.toInt, y1 - 2)
//                }
//                val newx1 = x2
//                val newy1 = y2
//                val newy2 = (linkPoint.getY * yDisplayFactor + yDisplayOffset).toInt
//                g.drawLine(newx1, newy1, x2, newy2)
//
//                c.getChildren match {
//                  case None => {}
//                  case Some(x) => x match{
//                    case Left(y) => {
//                      val z = new MergingPoint(y,initialXY,linkXY)
//                      z.paint()
//                    }
//                  }
//                }
//              }
//            }
//
//            class ClusterComponent(c: Cluster) {
//              def paint(g: Graphics2D, xDisplayOffset: Int, yDisplayOffset: Int, xDisplayFactor: Double, yDisplayFactor: Double, decorated: Boolean ): Unit = {
//                print(g)
//              }
//            }




      }

  }

}
