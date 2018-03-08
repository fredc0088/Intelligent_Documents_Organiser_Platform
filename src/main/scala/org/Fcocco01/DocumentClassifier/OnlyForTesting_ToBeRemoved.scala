package org.Fcocco01.DocumentClassifier

import Util.I_O.GetDocContent
import org.Fcocco01.DocumentClassifier.Analysis.IDF
import org.Fcocco01.DocumentClassifier.Analysis.ModelFunctions.tfidf
import org.Fcocco01.DocumentClassifier.Classify.{Dictionary, NormalisedVector, SingleVector}
import org.Fcocco01.DocumentClassifier.Token.Tokenizer.{StopWords, TokenizedText}

object OnlyForTesting_ToBeRemoved {

  def g = {
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

      val stopWords = StopWords("./src/main/resources/stop-word-list.txt")
    }





    import TestingResources._
    import Regexes.words1gram

    val tests = Array(testPath1, testPath2, testPath3
      //,testPath4
      ,testPath5,testPath6,testPath7,testPath8
    )


    val dictionary =
      Dictionary(tests, TokenizedText(words1gram, stopWords))
    val idfWeightedTerms = for {
      s <- dictionary
    } yield IDF.IDFValue(s, tests, GetDocContent)
    println("Ready at " + (System.currentTimeMillis() / 60000) )
    val testsPar = tests.par
    val vectors = testsPar.map{ a =>
      val tfidfFun = tfidf(idfWeightedTerms) // Assign idf results to tfidf function to be used as modeller
      NormalisedVector(dictionary, SingleVector(TokenizedText(words1gram, stopWords), a, Option(GetDocContent)), tfidfFun)
    }(collection.breakOut)

    testsPar.foreach{e => Thread.sleep(100000); println(e)}
    vectors
  }

}
