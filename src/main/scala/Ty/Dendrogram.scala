package Ty

import java.awt.Color

import org.Fcocco01.DocumentClassifier.Clustering.HierarchicalClustering.Cluster
import smile.plot.{Graphics, Plot, PlotCanvas}


class Dendrogram(p1: Array[Array[Double]], p2 : Array[Array[Double]])
                ( height : Double, color: Color) extends Plot(color) {


  override def paint(painter: Graphics): Unit = {
    val color = painter.getColor
    painter.setColor(getColor)
    for(i <- 0 to p1.length) painter.drawLine(p1[i] , p2[i])
    painter.setColor(color)
  }

  def dfs(): Unit = {

  }


}

object Dendrogram {

  def apply(top: Cluster, height: Array[Double], color: Color) : Dendrogram = {



  }

  def apply(top: Cluster, height: Array[Double]) : Dendrogram = {
    this(top, height, Color.BLACK)
  }

  def plot(c: Cluster, height: Array[Double]) : PlotCanvas = {
    val n = c.getHeight + 1
    val dendrogram = Dendrogram(c, height)

    val lower = Array(-n / 100, 0)
    val upper = Array(n + n / 100, 1.01 * dendrogram.getHeight)

    val canvas = new PlotCanvas(lower, upper, false)
    canvas.getAxis(0).setGridVisible(false)
    canvas.getAxis(0).setLabelVisible(false)

    canvas.add(dendrogram)
    canvas
  }
}
