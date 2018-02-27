package org.Fcocco01.DocumentClassifier

import org.Fcocco01.DocumentClassifier.Classify.NormalisedVector
import Util.Operators.|>

package object Clustering {

  object Similarity {
    type Vector = NormalisedVector

    def cosine(vector1: Vector, vector2: Vector) = {
      val a: Double = getAbsoluteValue(vector1) |> Math.sqrt
      val b: Double = getAbsoluteValue(vector2) |> Math.sqrt
      getDocProduct(vector1, vector2).map(_._2).reduce(_ + _) / (a * b)
    }

    def getDocProduct(v1: Vector, v2: Vector) =
      v1.vector.map { case (k, v) => k -> (v * v2.vector.getOrElse(k, 0.0)) }

    def getAbsoluteValue(vector: Vector): Double = {
      vector.vector.toVector.map(x => Math.pow(x._2, 2)).reduce(_ + _)
    }
  }

  object HierarchicalClust {

  }
}
