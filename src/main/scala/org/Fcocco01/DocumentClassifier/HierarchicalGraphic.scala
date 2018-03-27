package org.Fcocco01.DocumentClassifier

import java.awt.{BorderLayout, Font, FontMetrics, Graphics}
import javax.swing.border.EmptyBorder
import javax.swing.{JFrame, JPanel}

import org.Fcocco01.DocumentClassifier.Clustering.HierarchicalClustering.{Cluster, MultiCluster, SingleCluster}

object HierarchicalGraphic {

  class Tree(cluster: Cluster)(panel: JPanel)(drawer: DendrogramDrawer) extends JFrame

  object Tree {
    def apply(cluster: Cluster): Tree = {
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
      DrawTree(g, 0, getWidth, 0, getHeight / cluster.getHeight.toInt, cluster)
    }

    //    def drawNode(g: Graphics, n: Node[Cluster], w: Int, h: Int, q: Int) = {
    //      if(!n.isLeaf) {
    //        val (left,right) = n.c.getChildren
    //        if(left)
    //      }
    //    }

    def DrawTree(g: Graphics, StartWidth: Int, EndWidth: Int, StartHeight: Int, Level: Int, node: Cluster): Unit = {
      val data = cluster.name
      g.setFont(new Font("Tahoma", Font.BOLD, 20))
      val fm = g.getFontMetrics
      val dataWidth = fm.stringWidth(data)
      //g.drawString(data, (StartWidth + EndWidth) / 2 - dataWidth / 2, StartHeight + Level / 2)
      if (cluster.isLeaf) {
        g.drawString(data, (StartWidth + EndWidth) / 2 - dataWidth / 2, 1)
      } else {
        drawCenteredCircle(g, (StartWidth + EndWidth) / 2, StartHeight + Level / 2, 5)
        node.getChildren match {
          case Some(x) => {
            DrawTree(g, StartWidth, (StartWidth + EndWidth) / 2, StartHeight + Level, Level, x._1)
            DrawTree(g, EndWidth, (StartWidth + EndWidth) / 2, StartHeight + Level, Level, x._2)
          }
          case None => println("")
        }
      }
    }

    import java.awt.Graphics2D

    def drawCenteredCircle(g: Graphics, x: Int, y: Int, r: Int): Unit = {
      g.fillOval(x - (r / 2), y - (r / 2), r, r)
    }
  }

  object DendrogramDrawer {

  }

  //
  //  trait Node[T] {
  //    val c : T
  //    def isLeaf : Boolean
  //    val height : Double
  //    val uniqueId : String
  //    val parentId : Option[Int]
  //    val childLId : Option[Int]
  //    val childRId : Option[Int]
  //  }
  //  class Merge(c: MultiCluster, x: Int, y: Int, parent: Option[Cluster] ) extends Node[MultiCluster] {
  //
  //    override def isLeaf: Boolean = c.isLeaf
  //
  //    val height = c.getHeight
  //    private val (childL,childR) = c.getChildren.get
  //
  //    val (parentId, childLId, childRId) =
  //      (Option(parent.hashCode), Some(childL.hashCode), Some(childR.hashCode))
  //
  //    override val uniqueId  = c.hashCode
  //  }
  //
  //  class Leaf(c: SingleCluster, x: Int, y: Int, parent: Cluster) extends Node[SingleCluster] {
  //    override def isLeaf: Boolean = c.isLeaf
  //    override val height: Double = 0.0
  //    override val uniqueId: Int = c.hashCode
  //    val parentId = Option(parent.hashCode)
  //    val (childLId, childRId) = None
  //  }
  //
  //  class Point(private val cluster: Cluster, parent : Option[Cluster]){
  //    val heigth = cluster.getHeight
  //    val uniqueId = cluster.hashCode
  //    val parentId = parent
  //  }
  //  class Trees(private val top: Cluster) {
  //    val root = new Point(top, )
  //    val left,right = top.getChildren.getOrElse(None)
  //  }




}
