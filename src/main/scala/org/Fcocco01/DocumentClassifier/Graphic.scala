package org.Fcocco01.DocumentClassifier

import scalafx.Includes._
import scalafx.stage._
import java.awt._
import java.awt.geom._
import javax.swing.{JFrame, JPanel}

import org.Fcocco01.DocumentClassifier.Clustering.HierarchicalClustering.Cluster

import scalafx.application.JFXApp

object Graphic extends {

  object Hierarchy {

//    class Dendrogram extends JFXApp {
//
//    }

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


      //      class VCoord(x: Int, y: Int) {
      //        override def equals(obj: Any): Boolean =
      //          if (obj.isInstanceOf[VCoord]) {
      //            val other = obj.asInstanceOf[VCoord]
      //            this.x == other.x && this.y == other.y
      //          } else false
      //        }
      //
      //
      //

      //      class MergingPoint(c: Cluster,initialXY: VCoord,linkXY: VCoord) {
      //        val namePadding : Int = 6
      //        override def paint(g: Graphics2D, xDisplayOffset: Int, yDisplayOffset: Int, xDisplayFactor: Double, yDisplayFactor: Double, decorated: Boolean): Unit = {
      //          val fontMetrics = g.getFontMetrics
      //          val x1 = (initPoint.getX * xDisplayFactor + xDisplayOffset).toInt
      //          val y1 = (initPoint.getY * yDisplayFactor + yDisplayOffset).toInt
      //          val x2 = (linkPoint.getX * xDisplayFactor + xDisplayOffset).toInt
      //          val y2 = 0
      //          g.fillOval(x1 - dotRadius, y1 - dotRadius, dotRadius * 2, dotRadius * 2)
      //          g.drawLine(x1, y1, x2, y2)
      //          if (c.getHeight == 0) g.drawString(c.n,x1 + namePadding, y1 + (fontMetrics.getHeight / 2) - 2)
      //          if (decorated && cluster.getDistance != null && !cluster.getDistance.isNaN && cluster.getDistance.getDistance > 0) {
      //            val s = String.format("%.2f", cluster.getDistance)
      //            val rect = fontMetrics.getStringBounds(s, g)
      //            g.drawString(s, x1 - rect.getWidth.toInt, y1 - 2)
      //          }
      //          val newx1 = x2
      //          val newy1 = y2
      //          val newy2 = (linkPoint.getY * yDisplayFactor + yDisplayOffset).toInt
      //          g.drawLine(newx1, newy1, x2, newy2)
      //
      //          c.getChildren match {
      //            case None => {}
      //            case Some(x) => x match{
      //              case Left(y) => {
      //                val z = new MergingPoint(y,initialXY,linkXY)
      //                z.paint()
      //              }
      //            }
      //          }
      //          for (child <- children) {
      //            child.paint(g, xDisplayOffset, yDisplayOffset, xDisplayFactor, yDisplayFactor, decorated)
      //          }
      //        }
      //      }
      //
      //      class ClusterComponent(c: Cluster) {
      //        def paint(g: Graphics2D, xDisplayOffset: Int, yDisplayOffset: Int, xDisplayFactor: Double, yDisplayFactor: Double, decorated: Boolean ): Unit = {
      //          print(g)
      //        }
      //      }

      }

  }

}
