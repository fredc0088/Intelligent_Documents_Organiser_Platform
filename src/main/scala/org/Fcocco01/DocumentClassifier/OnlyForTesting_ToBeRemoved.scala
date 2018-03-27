package org.Fcocco01.DocumentClassifier

import Util.I_O.GetDocContent
import Analysis.IDF
import Analysis.ModelFunctions.tfidf
import Classify._
import Clustering.DVector
import Token.Tokenizer.{StopWords, TokenizedText}
import Clustering.HierarchicalClustering._
import Clustering.Similarity.cosine
import DocGathering._
import Types.Token

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

  def main(args: Array[String]): Unit = {

    val time = System.nanoTime

    def currentTimeMins(t: Double) = (((System.nanoTime - t) / 1E9) / 60) + " mins"

    println("Start initialisation after " + currentTimeMins(time))

    val tests = DocumentFinder(Array(
      "./src"
      ,"./Notes"
            ,"C:/Users/USER/Documents/Important docs"
      ,"C:/Users/USER/Desktop"
//      ,"C:/Users/USER"
          ,"C:/Users/USER/Downloads"
      ), Array(
            "./src/main/resources"
      )
    )

    val tknTool = buildTokenSuite(TokenizedText(words1gram, stopWords))(GetDocContent)

    val tknFun = tokenizeDocument(tknTool)

    implicit val docs = tests.par.map(x => tknFun(x)).toArray

    println("Documents tokenised in " + currentTimeMins(time))

    val dictionary = Dictionary(docs)

    println("Dictionary created in " + currentTimeMins(time))

    val idfWeightedTerms = dictionary.par.map(IDF.IDFValue(_)(Option(docs.map(_.getOrElse("", Vector.empty[Token]))))).toVector

    println("Terns weighted to idf in " + currentTimeMins(time))

    val tfidfFun = tfidf(idfWeightedTerms)

    val vectorFun = createVector(tfidfFun)(Some(dictionary))

    val vectors = docs.par.map(x => vectorFun(x)).filterNot(_.isEmpty).toArray

    println("Vectors obtained in " + currentTimeMins(time))

    println("Clustering after  " + currentTimeMins(time))
    val m = createSimMatrix(vectors, cosine)
    val t = (x: Seq[DVector]) => x.map(SingleCluster(_)).toList
    val o = HAC(m, t, Single_Link, vectors: _*)
    println("Finished after " + currentTimeMins(time))
    o.vectors.map(_.id).foreach(println)
  }
}
