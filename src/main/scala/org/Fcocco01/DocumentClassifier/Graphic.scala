package org.Fcocco01.DocumentClassifier

import scalafx.Includes._
import scalafx.stage._
import java.awt.{Color, Graphics2D, Panel, Point}
import java.awt.geom._
import javax.swing.JPanel

import scalafx.application.JFXApp

object Graphic extends {

  object Hierarchy {

    class Dendrogram extends JFXApp {

    }

    class D extends JPanel {
      def paintComponent(g: Graphics2D): Unit = {
        super.paintComponent(g)
        this.setBackground(Color.BLACK)

        g.setColor(Color.white)
        g.fillRect(25, 25, 100, 30)
        g.setColor(new Color(190, 81, 215))


      }

      class Node extends Point {


      }

    }

  }

}
