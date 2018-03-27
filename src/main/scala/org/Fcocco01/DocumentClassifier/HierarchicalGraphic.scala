package org.Fcocco01.DocumentClassifier

import java.awt.{BorderLayout, Graphics}

import javax.swing.border.EmptyBorder
import javax.swing.{JFrame, JPanel}
import org.Fcocco01.DocumentClassifier.Clustering.HierarchicalClustering.Cluster

import scala.reflect.runtime.universe._

object HierarchicalGraphic {

  class Tree(cluster: Cluster)(panel: JPanel)(drawer: DendrogramDrawer) extends JFrame {


  }

  object Tree {
    def apply(cluster: Cluster)(panel: JPanel)(drawer: DendrogramDrawer): Tree = {
      val contentPane = new JPanel()
      contentPane.setBorder(new EmptyBorder(5, 5, 5, 5))
      contentPane.setLayout(new BorderLayout(0, 0))
      val drawer = new DendrogramDrawer(cluster)
      contentPane.add(drawer)
      val newP = new Tree(cluster)(contentPane)(drawer)
      newP.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
      newP.setBounds(100, 100, 500, 500)
      newP.setContentPane(contentPane)
      newP.setVisible(true)
      newP
    }
  }

  class DendrogramDrawer(cluster: Cluster) extends JPanel {
    override def paintComponent(g: Graphics): Unit = {
      super.paintComponent(g)
    }

    def drawMerge(g: Graphics, n: Node, w: Int, h: Int, q: Int)
  }

  object DendrogramDrawer {

  }
  class Node

}
