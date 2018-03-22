package org.Fcocco01.DocumentClassifier.OOP

import Util.I_O.GetDocContent
import org.Fcocco01.DocumentClassifier.DocGathering
import org.Fcocco01.DocumentClassifier.OOP.Classify._
import org.Fcocco01.DocumentClassifier.OOP.Clustering.DVector
import org.Fcocco01.DocumentClassifier.OOP.Analysis.IDF
import org.Fcocco01.DocumentClassifier.OOP.Analysis.ModelFunctions.tfidf
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



  import Clustering.HierarchicalClustering._
  import Clustering.Similarity.cosine
  import DocGathering._
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

    implicit val vectorsSingle: Option[Traversable[DocumentVector]] =
      Option(tests.par.map(x => VectorFactory(TokenizedText(words1gram, stopWords), x, GetDocContent)).filter(!_.isEmpty).toVector)

    implicit val documents: Option[Traversable[String]] =
      Option(tests.par.map(GetDocContent(_).replace("\n", " ").toLowerCase).toVector)

    println("Vectors and docs obtained in " + currentTimeMins(time))

    val dictionary =
      Dictionary(tests, TokenizedText(words1gram, stopWords))(vectorsSingle)

    println("Created dictionary in " + currentTimeMins(time))

    val idfWeightedTerms = dictionary.par.map(IDF.IDFValue(_)(tests, GetDocContent)(documents)).toVector

    println("IDF values in " + currentTimeMins(time))

    val parVectorsSingle = vectorsSingle
      .getOrElse(getDefaultVectors(tests,words1gram,stopWords)).par

    println("Start to normalise after " + currentTimeMins(time))

    val vectors = parVectorsSingle.map { a =>
      val tfidfFun = tfidf(idfWeightedTerms) // Assign idf results to tfidf function to be used as modeller
      NormalisedVector(dictionary, a.asInstanceOf[SingleVector], tfidfFun)
    }(collection.breakOut)
    parVectorsSingle.foreach { e => Thread.sleep(10000); println(e.docId) }

    val p: Array[DVector] = vectors.toArray
    println("Clustering after  " + currentTimeMins(time))
    val m = createSimMatrix(p, cosine)
    val t = (x: Seq[DVector]) => x.map(SingleCluster(_)).toList
    val o = HAC(m, t, Single_Link, p: _*)
    println("Finished after " + currentTimeMins(time))
    o.getVectors.map(_.docId).foreach(println)
  }
}