package org.Fcocco01.DocumentClassifier.Test

import org.Fcocco01.DocumentClassifier._
import Core.Weight.{GetFrequency, IDF, ModelFunctions}
import IDF._
import ModelFunctions._
import Core.DocumentDataSetMorph.{Dictionary, buildTokenSuite, tokenizeDocument}
import Core.TokenPackage.Tokenizer.TokenizedText
import Test.TestingResources.Paths.{testPath1, testPath2, testPath3, testPath5}
import Test.TestingResources.Regexes.words1gram
import Test.TestingResources.stopWords
import Utils.Types.TypeClasses.{Document, TermWeighted}
import Utils.Util.I_O.GetDocContent

class WeightTest extends UnitTest ("Weight"){

  var idfValues : Array[IDFValue] = _
  var tokens : Array[Option[Document]] = _

  val tests = Array(testPath1,testPath2,testPath3)

  override def beforeAll : Unit = {
    val tknTool = buildTokenSuite(TokenizedText(words1gram, stopWords))(GetDocContent)
    tokens = tests.par.map(x => tokenizeDocument(tknTool)(x)).toArray
    val dictionary = Dictionary(tokens)
    idfValues = dictionary.getOrElse(Vector("")).par.map(IDFValue(_)(Option(tokens))).toArray
  }

  "A terms's idf value" should "be calculated correctly according to given documents" in {
    val tknTool = buildTokenSuite(TokenizedText(words1gram, stopWords))(GetDocContent)
    val tokens = Array(testPath3,testPath5).par.map(x => tokenizeDocument(tknTool)(x)).toArray
    assertResult(0.47712125471966244){ (IDFValue("doc")(Some(tokens))).apply }
  }

//  "idf weighting" should "accept any extractor function from path to text content" in {
  //    val extractorMock = mockFunction[String,String]
  //    extractorMock expects ("C:/me/a.txt")
  //    val c = IDF.IDFValue.apply
  //    c expects extractorMock
  //  }

  "Idf from a set of empty documents" should "return 0" in {
    val x = IDFValue("doc")(Some(Array(None,None)))
    x.apply shouldBe 0.0
  }

  "All weighting functions" should "return a TermWeighted" in {
    val a = bag("text",tokens(1).get.tokens)
    val b = tf("text",tokens(1).get.tokens)
    val c = wdf("text",tokens(1).get.tokens)
    val d = tfLogNorm("text",tokens(1).get.tokens)
    val e = tfidf(idfValues)("text",tokens(1).get.tokens)
    val f = wdfidf(idfValues)("text",tokens(1).get.tokens)
    assert(Array(a,b,c,d,e,f).forall(_.isInstanceOf[TermWeighted]))
  }

  "Frequency of a term in a document" should "return expected count" in {
    assertResult(22) { GetFrequency(tokens(1).get.tokens,"text")}
  }

  "Frequency of a term in a document" should "be 0 if the term does not exists" in {
    assertResult(0) { GetFrequency(tokens(1).get.tokens,"Asfeb")}
  }

  "Tfidf" should "return right result" in {
    val fun = tfidf(idfValues)
    assertResult(0.0168945406) {
      val x = fun("text",tokens(1).get.tokens)
      BigDecimal(x.weight).setScale(10, BigDecimal.RoundingMode.HALF_UP).toDouble
    }
  }

  "Wdfidf" should "return right result" in {
    val fun = wdfidf(idfValues)
    assertResult(5.9001296574) {
      val x = fun("text",tokens(1).get.tokens)
      BigDecimal(x.weight).setScale(10, BigDecimal.RoundingMode.HALF_UP).toDouble
    }
  }

  "TfLogNorm" should "return right result" in
    assertResult(2.3424226808) {
      val x = tfLogNorm("text",tokens(1).get.tokens)
      BigDecimal(x.weight).setScale(10, BigDecimal.RoundingMode.HALF_UP).toDouble
    }

  "Wdf" should "return right result" in
    assertResult(9.7999032363) {
      val x = wdf("text",tokens(1).get.tokens)
      BigDecimal(x.weight).setScale(10, BigDecimal.RoundingMode.HALF_UP).toDouble
    }

  "Tf" should "return right result" in
    assertResult(0.0280612245) {
      val x = tf("text",tokens(1).get.tokens)
      BigDecimal(x.weight).setScale(10, BigDecimal.RoundingMode.HALF_UP).toDouble
    }

  "Bag" should "return right result" in
    assertResult(22) {
      val x = bag("text",tokens(1).get.tokens)
      BigDecimal(x.weight).setScale(10, BigDecimal.RoundingMode.HALF_UP).toDouble
    }

  "The functions" should "return a 0 weight if the document provided is empty" in {
    val a = bag("example",Array.empty[String])
    val b = tf("example",Array.empty[String])
    val c = wdf("example",Array.empty[String])
    val d = tfLogNorm("example",Array.empty[String])
    val e = tfidf(idfValues)("example",Array.empty[String])
    val f = wdfidf(idfValues)("example",Array.empty[String])
    assert(Array(a,b,c,d,e,f).forall(_.weight == 0))
  }
}
