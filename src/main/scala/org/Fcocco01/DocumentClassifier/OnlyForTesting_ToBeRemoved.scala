package org.Fcocco01.DocumentClassifier

import Util.I_O.GetDocContent
import org.Fcocco01.DocumentClassifier.Analysis.IDF
import org.Fcocco01.DocumentClassifier.Analysis.ModelFunctions.tfidf
import org.Fcocco01.DocumentClassifier.Classify.{Dictionary, DocumentVector, NormalisedVector, SingleVector}
import org.Fcocco01.DocumentClassifier.Clustering.DVector
import org.Fcocco01.DocumentClassifier.Token.Tokenizer.{StopWords, TokenizedText}

object OnlyForTesting_ToBeRemoved {

  object TestingResources {

    object Regexes {
      val words1gram = "[^a-z0-9]"
    }

      val demoDocxMock =
        ""
      val testPath1 =
        "./src/test/resources/1/1.2/Java - Generics by Oracle.docx"
      val testPath2 =
        "./src/test/resources/1/1.1/demo.docx"
      val testPath3 =
        "./src/test/resources/3/3.2/test.docx"
      val testPath4 =
        "./src/test/resources/1/1.2/clustering.pdf"
      val testPath5 =
        "./src/test/resources/3/3.1/SampleDOCFile_100kb.doc"
      val testPath6 =
        "./src/test/resources/3/3.1/3.1.1/TestWordDoc.doc"
      val testPath7 =
        "./src/test/resources/1/Dfr.doc"
      val testPath8 =
        "./src/test/resources/2/a.docx"
    val testPath9 =
      "./Notes/Vectorisation and tfidf.docx"
    val testPath10 =
      "./Notes/Vector space representation and similarity.docx"
    val testPath11 =
      "./Notes/Tf.docx"
    val testPath12 =
      "./Notes/Term frequency and weighting.docx"
    val testPath13 =
      "./Notes/Note On Developing.docx"

      val stopWords = StopWords("./src/main/resources/stop-word-list.txt")
    }



    import TestingResources._
    import Regexes.words1gram
  import Clustering.Similarity.cosine
  import Clustering.HierarchicalClustering._

  def main(args: Array[String]): Unit = {
    val time = System.nanoTime
    println("Start initialisation after " + (((System.nanoTime - time) / 1E9) / 60) + " mins")

    val tests = Array(testPath1, testPath2, testPath3
      //,testPath4
      , testPath5, testPath6, testPath7, testPath8, testPath9,
      testPath10, testPath11, testPath12, testPath13
    )

    implicit val vectorsSingle: Option[Traversable[DocumentVector]] =
      Option(tests.toVector.map(x => SingleVector(TokenizedText(words1gram, stopWords), x, Option(GetDocContent))))
    implicit val documents: Option[Traversable[String]] =
      Option(tests.toVector.map(GetDocContent(_).replace("\n", " ").toLowerCase))
    val dictionary =
      Dictionary(tests, TokenizedText(words1gram, stopWords))
    val idfWeightedTerms = for {
      s <- dictionary
    } yield IDF.IDFValue(s, tests, GetDocContent)
    val parVectorsSingle = vectorsSingle.getOrElse(
      tests.map(SingleVector(TokenizedText(words1gram, stopWords), _, Option(GetDocContent))))
      .asInstanceOf[Vector[SingleVector]].par
    val vectors = parVectorsSingle.map { a =>
      val tfidfFun = tfidf(idfWeightedTerms) // Assign idf results to tfidf function to be used as modeller
      NormalisedVector(dictionary, a, tfidfFun)
    }(collection.breakOut)
    parVectorsSingle.foreach { e => Thread.sleep(100000); println(e) }
    val p: Array[DVector] = vectors.toArray
    println("Clustering after  " + (((System.nanoTime - time) / 1E9) / 60) + " mins")
    val m = createSimMatrix(p, cosine)
    val t = (x: Seq[DVector]) => x.map(SingleCluster(_)).toList
    val o = HAC(m, t, Single_Link, p: _*)
    println((((System.nanoTime - time) / 1E9) / 60) + " mins")
    o.getVectors.map(_.docId).foreach(println)
  }
}
