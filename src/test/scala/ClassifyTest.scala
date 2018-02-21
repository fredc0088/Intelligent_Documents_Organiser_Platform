package org.Fcocco01.DocumentClassifier

import Util.I_O.GetDocContent
import org.Fcocco01.DocumentClassifier.Classify.Dictionary
import org.Fcocco01.DocumentClassifier.DocumentFrequency.IDF
import org.Fcocco01.DocumentClassifier.Token.Tokenizer.{StopWords, TokenizedText}
import Classify._


class ClassifyTest() extends UnitTest("Classify") {
  import Resources._


  it should "Produce a normalised vector with tf-idf weighting" in {

    val tests = Array(testPath1, testPath2, testPath3
      //,testPath4
    )
    val dictionary =
      Dictionary(tests, TokenizedText("\\s+", stopWords))
    val idfWeightedTerms = for {
      s <- dictionary
    } yield IDF.IDFValue(s, tests, GetDocContent)
    val original = DocumentVector(TokenizedText("\\s+", stopWords), testPath2, Option(GetDocContent))
    val test = TfIdfVector(dictionary,original,idfWeightedTerms)
    it should "Return right size" in {
      test.size shouldBe(2110)
    }
    it should "vector should be map of right type" in {
      test.vector.foreach{_._2 shouldBe Double }
    }
    it should "vector values should never be above 1" in {
      test.vector.foreach{x => assert(x._2 < 1)}
    }

  }

}



object Resources {
  val demoDocxMock=
    ""
  val testPath1 =
    ".\\src\\test\\resources\\1\\1.2\\Java - Generics by Oracle.docx"
  val testPath2 =
    ".\\src\\test\\resources\\1\\1.1\\demo.docx"
  val testPath3 =
    ".\\src\\test\\resources\\3\\3.2\\test.docx"
  val testPath4 =
    ".\\src\\test\\resources\\1\\1.2\\clustering.pdf"

  val stopWords = StopWords(".\\src\\main\\resources\\stop-word-list.txt")
}
