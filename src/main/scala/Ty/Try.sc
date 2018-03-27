
import scala.swing._
import scala.math._
import java.awt._
import java.awt.image.BufferedImage
import java.io.FileOutputStream

import javax.imageio.ImageIO

object Dendrogram {
  def apply(clust: BiCluster, labels: Array[String]): Unit = {
    Swing.onEDT { new Dendrogram(clust, labels) startup (Array[String]()) }
  }

  def save(clust: BiCluster, labels: Array[String], filename: String): Unit = {
    // Height and width
    val h = getheight(clust) * 20
    val w = 1200.0
    val depth = getdepth(clust)

    // The weight is fixed so you need to choose accordingly
    val scaling = (w - 150).toDouble / depth

    val image = new BufferedImage(w.toInt, h.toInt, BufferedImage.TYPE_3BYTE_BGR)

    val g = image.getGraphics.asInstanceOf[Graphics2D]
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
    g.setColor(Color.WHITE)
    g.fillRect(0, 0, image.getWidth, image.getHeight)
    drawtree(g, clust, h, scaling, labels)
    g.dispose

    ImageIO.write(image, "png", new FileOutputStream(filename))
  }

  import Graphics._

  def drawtree(g: Graphics2D, clust: BiCluster, h: Double, scaling: Double, labels: Array[String]): Unit = {
    g.setColor(Color.BLUE)
    g.drawLine(0.0, h / 2, 10.0, h / 2)
    drawnode(g, clust, 10, h / 2, scaling, labels)
  }


  def drawnode(g: Graphics2D, clust: BiCluster, x: Double, y: Double, scaling: Double, labels: Array[String]): Unit = {
    (clust.left, clust.right) match {
      case (Some(left), Some(right)) =>
        val h1 = getheight(left) * 20
        val h2 = getheight(right) * 20
        val top = y - (h1 + h2) / 2
        val bottom = y + (h1 + h2) / 2
        // length of straight line
        val ll = clust.distance * scaling

        g.setColor(Color.BLUE)

        // straight vertical line from cluster to child
        g.drawLine(x, top + h1 / 2, x, bottom - h2 / 2)

        // horizontal line to the left item
        g.drawLine(x, top + h1 / 2, x + ll, top + h1 / 2)

        // right
        g.drawLine(x, bottom - h2 / 2, x + ll, bottom - h2 / 2)

        drawnode(g, left, x + ll, top + h1 / 2, scaling, labels)
        drawnode(g, right, x + ll, bottom - h2 / 2, scaling, labels)
      case _ =>
        g.setColor(Color.BLACK)
        // if it is the end point it draws the item label
        g.drawString(labels(clust.id), x + 5, y + 5)
    }
  }

  def getheight(clust: BiCluster): Double = {
    (clust.left, clust.right) match {
      case (Some(left), Some(right)) =>
        // otherwise the heigth is the one of each item
        getheight(left) + getheight(right)
      case _ =>
        // if it is the end point the height become one
        1.0
    }
  }

  def getdepth(clust: BiCluster): Double = {
    (clust.left, clust.right) match {
      case (Some(left), Some(right)) =>
        // 枝の距離は二つの方向の大きい方にそれ自身の距離を足したもの
        max(getdepth(left), getdepth(right)) + clust.distance
      case _ =>
        // 終端への距離は0
        0.0
    }
  }
}

class Dendrogram(clust: BiCluster, labels: Array[String]) extends SimpleSwingApplication {
  import Dendrogram._

  // 高さと幅
  val h = getheight(clust) * 20
  val w = 1200.0
  val depth = getdepth(clust)

  // 幅は固定されているため、適宜縮尺する
  val scaling = (w - 150).toDouble / depth

  def top = new MainFrame {
    var background = Color.WHITE
    var resizable = true
    var contents = new ScrollPane() {
      override var preferredSize = new Dimension(w.toInt, 800)
      var viewportView = Some(new Component() {
        override var preferredSize = new Dimension(w.toInt, h.toInt)
        def paint(g: Graphics2D): Unit = {
          drawtree(g, clust, h, scaling, labels)
        }
      })
    }
  }
}

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Map
import scala.io._
import scala.math._
import java.util.Arrays

case class BiCluster(vec: Array[Double], id: Int, left: Option[BiCluster] = None, right: Option[BiCluster] = None, distance: Double = 0.0)

object BiCluster {
  // 評価関数
  type Distance = (Array[Double], Array[Double]) => Double

  def pearson(v1: Array[Double], v2: Array[Double]): Double = {
    val n = v1.size

    // 単純な合計
    val sum1 = v1.sum
    val sum2 = v2.sum

    // 平方を合計
    val sum1Sq = v1.map(pow(_, 2)).sum
    val sum2Sq = v2.map(pow(_, 2)).sum

    // 積の合計
    val pSum = v1.zip(v2).map { case (i1, i2) => i1 * i2 }.sum

    // ピアソンによるスコアを算出
    val num = pSum - (sum1 * sum2 / n)
    val den = sqrt((sum1Sq - pow(sum1, 2) / n) * (sum2Sq - pow(sum2, 2) / n))
    if (den == 0.0) return 0.0

    1.0 - num / den
  }

  def tanimoto(v1: Array[Double], v2: Array[Double]): Double = {
    val c1 = v1.filter(0.0!=).size
    val c2 = v2.filter(0.0!=).size
    val shr = (v1 zip v2).filter { case (i1, i2) => i1 != 0.0 && i2 != 0.0 }.size

    1.0 - (shr.toDouble / (c1 + c2 - shr))
  }

  def hcluster(rows: Array[Array[Double]], distance: Distance = pearson): BiCluster = {
    val distances = Map[(Int, Int), Double]()
    var currentclusterid = -1

    // クラスタは最初は行たち
    val clust = ArrayBuffer(rows.zipWithIndex.map { case (row, i) => BiCluster(row, i) }: _*)

    while (clust.size > 1) {
      var lowestpair = (0, 1)
      var closest = Double.MaxValue

      // すべての組をループし、最も距離の近い組を探す
      clust.zipWithIndex.combinations(2).foreach {
        case ArrayBuffer((ci, i), (cj, j)) =>
          // 距離をキャッシュしてあればそれを使う
          val d = distances.getOrElseUpdate((ci.id, cj.id), distance(ci.vec, cj.vec))
          if (d < closest) {
            closest = d
            lowestpair = (i, j)
          }
      }

      // 二つのクラスタの平均を計算する
      val mergevec = (clust(lowestpair._1).vec zip clust(lowestpair._2).vec).map { case (i1, i2) => (i1 + i2) / 2.0 }

      // 新たなクラスタを作る
      val newcluster = BiCluster(mergevec, currentclusterid,
        left = Some(clust(lowestpair._1)),
        right = Some(clust(lowestpair._2)),
        distance = closest)

      // 元のセットではないクラスタのIDは負にする
      currentclusterid -= 1
      clust.remove(lowestpair._2)
      clust.remove(lowestpair._1)
      clust.append(newcluster)
    }

    clust(0)
  }

  def kcluster(rows: Array[Array[Double]], distance: Distance = pearson, k: Int = 4): Array[Array[Int]] = {
    val centers = 0 until k
    // それぞれのポイントの最小値と最大値を決める
    val ranges = rows(0).indices.map { i => (rows.map(_(i)).min, rows.map(_(i)).max) }.toArray
    // 重心をランダムにk個配置する
    val clusters = centers.map { j => ranges.map { case (min, max) => random * (max - min) + min }.toArray }.toArray

    var lastmatches = Array[ArrayBuffer[Int]]()

    for (t <- 0 until 100) {
      println("Iteration %d".format(t))
      val bestmatches = centers.map(i => ArrayBuffer[Int]()).toArray

      // それぞれの行に対して、もっとも近い重心を探し出す
      rows.zipWithIndex.foreach {
        case (row, j) =>
          var bestmatch = clusters.zipWithIndex.minBy { case (cluster, _) => distance(cluster, row) }._2
          bestmatches(bestmatch) += j
      }

      // 結果が前回と同じであれば終了
      if (bestmatches.sameElements(lastmatches))
        return lastmatches.map(_.toArray)
      lastmatches = bestmatches

      // 重心をそのメンバーの平均に移動する
      centers.foreach { i =>
        val cluster = bestmatches(i).map(rows)
        if (cluster.size > 0) {
          clusters(i) = rows(0).indices.map { j => cluster.map(_(j)).sum / cluster.size }.toArray
        }
      }
    }

    lastmatches.map(_.toArray)
  }

  def printclust(clust: BiCluster, labels: Option[Array[String]] = None, n: Int = 0): Unit = {
    // 階層型のレイアウトにするためにインデントする
    print(" " * n)
    if (clust.id < 0) {
      // 負のidの時はこれが枝であることを示している
      println("-")
    } else {
      // 正のidはこれが終端だということを示している
      labels match {
        case Some(labels) =>
          println(labels(clust.id))
        case None =>
          println(clust.id)
      }
    }

    // 右と左の枝を表示する
    clust.left.foreach(printclust(_, labels, n + 1))
    clust.right.foreach(printclust(_, labels, n + 1))
  }
}


import java.awt.Graphics2D

object Graphics {
  class WrappedGraphics2D(g: Graphics2D) {
    def drawLine(x1: Double, y1: Double, x2: Double, y2: Double): Unit =
      g.drawLine(x1.toInt, y1.toInt, x2.toInt, y2.toInt)

    def drawString(str: String, x: Double, y: Double): Unit =
      g.drawString(str, x.toInt, y.toInt)

    def drawStringCenter(str: String, x: Double, y: Double): Unit = {
      val width = g.getFontMetrics().stringWidth(str)
      val height = g.getFontMetrics().getHeight()
      drawString(str, x - width / 2.0, y - height / 2.0)
    }
  }

  implicit def wrapGraphics2D(g: Graphics2D): WrappedGraphics2D = new WrappedGraphics2D(g)
}

