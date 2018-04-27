package org.Fcocco01.DocumentClassifier.Test

import org.Fcocco01.DocumentClassifier._
import Core.Features.{IDF, Bag_Of_Words_Models}
import IDF._
import Bag_Of_Words_Models._
import Core.DataSetMorph.{Dictionary, buildTokenSuite, tokenizeDocument}
import Core.Tokenization.TokenizedText
import Test.TestingResources.Paths.{testPath1, testPath2, testPath3, testPath5, testPath8, testPath9, testPath10}
import Test.TestingResources.Regexes.words1gram
import Test.TestingResources.stopWords
import Utils.Types.TypeClasses.{Document, TermWeighted}
import Utils.Util.I_O.GetDocContent

class FeaturesTest extends UnitTest ("Features"){

  var idfValues : Array[IDFValue] = _
  var tokens : Array[Option[Document]] = _

  val tests = Array(testPath1,testPath2,testPath3)

  override def beforeAll : Unit = {
    val tknTool = buildTokenSuite(TokenizedText(words1gram, stopWords))(GetDocContent)
    tokens = tests.par.map(x => tokenizeDocument(tknTool)(x)).toArray
    val dictionary = Dictionary(tokens)
    idfValues = dictionary.getOrElse(Vector("")).par.map(IDFValue(_)(simpleIdf)(Option(tokens))).toArray
  }

  "A terms's idf value (using simple IDF)" should "be calculated correctly according to given documents" in {
    val tknTool = buildTokenSuite(TokenizedText(words1gram, stopWords))(GetDocContent)
    val tokens = Array(testPath3,testPath5).par.map(x => tokenizeDocument(tknTool)(x)).toArray
    assertResult(0.3010299956639812){ (IDFValue("doc")(simpleIdf)(Some(tokens))).apply }
    assertResult(1.3010299956639812){ (IDFValue("doc")(smootherIdf)(Some(tokens))).apply }
  }

  "Idf from a set of empty documents" should "return 0" in {
    val x = IDFValue("doc")(simpleIdf)(Some(Array(None,None)))
    x.apply shouldBe 0.0
  }

  "All features scoring functions" should "return a TermWeighted" in {
    val a = rawBag("text",tokens(1).get.tokens)
    val b = tf("text",tokens(1).get.tokens)
    val c = wdf("text",tokens(1).get.tokens)
    val d = tfLog("text",tokens(1).get.tokens)
    assert(Array(a,b,c,d).forall(_.isInstanceOf[TermWeighted]))
  }

  "TfLog" should "return right result" in
    assertResult(0.0276747222) {
      val x = tfLog("text",tokens(1).get.tokens)
      BigDecimal(x.weight).setScale(10, BigDecimal.RoundingMode.HALF_UP).toDouble
    }

  "TfLog" should "return 0 if term is not in the document" in
    assertResult(0) {
      val x = tfLog("ewffs",tokens(1).get.tokens)
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

  "Tf" should "return 1 if  a term happen to be the only term in a document" in {
    val tknTool = buildTokenSuite(TokenizedText(words1gram, stopWords))(GetDocContent)
    val tokens = tokenizeDocument(tknTool)(testPath10)
    assertResult(1.0){tf("HiMissing".toLowerCase,tokens.get.tokens).weight}
    }

  "Bag" should "return right result" in
    assertResult(22) {
      val x = rawBag("text",tokens(1).get.tokens)
      BigDecimal(x.weight).setScale(10, BigDecimal.RoundingMode.HALF_UP).toDouble
    }

  "TfIdf combination" should "return 0 if the term is in all documents" in {
    val tknTool = buildTokenSuite(TokenizedText(words1gram, stopWords))(GetDocContent)
    val tokens = Array(testPath8,testPath9,testPath10).par.map(x => tokenizeDocument(tknTool)(x)).toArray
    val dictionary = Dictionary(tokens)
    val idfValues = dictionary.getOrElse(Vector("")).par.map(IDFValue(_)(simpleIdf)(Option(tokens))).toArray
    assertResult(0.0){compose_weighting_Fun(tf)(Some(idfValues))("himissing",tokens(0).get.tokens).weight}
  }

  "The functions" should "return a 0 weight if the document provided is empty" in {
    val a = rawBag("example",Array.empty[String])
    val b = tf("example",Array.empty[String])
    val c = wdf("example",Array.empty[String])
    val d = tfLog("example",Array.empty[String])
    assert(Array(a,b,c,d).forall(_.weight == 0))
  }

  "All functions" should "return the right result when composed with IDF values" in {
    val a = compose_weighting_Fun(tf)(Some(idfValues))
    val b = compose_weighting_Fun(wdf)(Some(idfValues))
    val c = compose_weighting_Fun(tfLog)(Some(idfValues))
    val tokens = this.tokens.map(_.get.tokens)
    assertResult(0.0012089708834031455){a("example",tokens(0)).weight}
    assertResult(0.0){a("example",Array.empty[String]).weight}
    assertResult(2.031241956110586){b("example",tokens(0)).weight}
    assertResult(0.0012048396307164518){c("example",tokens(0)).weight}
  }
}
