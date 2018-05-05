package org.Fcocco01.DocumentClassifier.Test

import org.Fcocco01.DocumentClassifier._
import Core.Features.{IDF, Ranking_Modellers}
import IDF._
import Ranking_Modellers._
import Core.DataSetMorph.{Dictionary, buildTokenSuite, tokenizeDocument}
import Core.Tokenization.TokenizedText
import Test.TestingResources.Paths.{testPath1, testPath2, testPath3, testPath5, testPath8, testPath9, testPath10}
import Test.TestingResources.Regexes.words1gram
import Test.TestingResources.stopWords
import Essentials.Types.TypeClasses.{Document, TermWeighted}
import Essentials.Util.I_O.GetDocContent

class FeaturesTest extends UnitTest ("Core.Features"){

  var idfValues : Array[IDFValue] = _
  var tokens : Array[Option[Document]] = _

  val tests = Array(testPath1,testPath2,testPath3)

  override def beforeAll : Unit = {
    val tknTool = buildTokenSuite(TokenizedText(words1gram, stopWords))(GetDocContent)
    tokens = tests.par.map(x => tokenizeDocument(tknTool)(x)).toArray
    val dictionary = Dictionary(tokens)
    idfValues = dictionary.getOrElse(Vector("")).par.map(IDFValue(_)(simpleIdf)(Option(tokens))).toArray
  }

  /********************************************** Idf *****************************************************/

  "A terms's idf value (using simple IDF)" should "be calculated correctly according to given documents" in {
    val tknTool = buildTokenSuite(TokenizedText(words1gram, stopWords))(GetDocContent)
    val tokens = Array(testPath3,testPath5).par.map(x => tokenizeDocument(tknTool)(x)).toArray
    assertResult(0.3010299956639812){ IDFValue("doc")(simpleIdf)(Some(tokens)).apply }
    assertResult(1.3010299956639812){ IDFValue("doc")(smootherIdf)(Some(tokens)).apply }
  }

  "Idf from a set of empty documents" should "return 0" in {
    val x = IDFValue("doc")(simpleIdf)(Some(Array(None,None)))
    x.apply shouldBe 0.0
  }

  "simpleIdf" should "have a value 0 to 1" in {
    val tknTool = buildTokenSuite(TokenizedText(words1gram, stopWords))(GetDocContent)
    val tokens = Array(testPath3,testPath5).par.map(x => tokenizeDocument(tknTool)(x)).toArray
    val idfs = tokens.map(_.get.tokens).reduce(_ ++ _)
      .toVector.distinct.map(x => IDFValue(x)(simpleIdf)(Some(tokens)).apply)
    assert(idfs.forall(x => { x <= 1 && x >= 0} ))
  }

  "smootherIdf" should "be a value 1 to 2" in {
    val tknTool = buildTokenSuite(TokenizedText(words1gram, stopWords))(GetDocContent)
    val tokens = Array(testPath3,testPath5).par.map(x => tokenizeDocument(tknTool)(x)).toArray
    val idfs = tokens.map(_.get.tokens).reduce(_ ++ _)
      .toVector.distinct.map(x => IDFValue(x)(smootherIdf)(Some(tokens)).apply)
    assert(idfs.forall(x => { x <= 2 && x >= 1} ))
  }

  /********************************** Tf ********************************/

  "Tf" should "return a TermWeighted" in
    assert(
      tf("text",tokens(1).get.tokens).isInstanceOf[TermWeighted]
    )

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

  "Tf" should "return a 0 weight if the Document provided is empty or only an empty string" in {
    assertResult(0) {tf("example", Array.empty[String]).weight}
    assertResult(0) {tf("example", Array("")).weight}
  }

  /********************* Logarithmic Tf ********************************/

  "TfLog" should "return a TermWeighted" in
    assert(
      tfLog("text",tokens(1).get.tokens).isInstanceOf[TermWeighted]
    )

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

  "TfLog" should "return a 0 weight if the Document provided is empty or only an empty string" in {
    assertResult(0) {tfLog("example", Array.empty[String]).weight}
    assertResult(0) {tfLog("example", Array("")).weight}
  }

  /********************* Augmented Tf ********************************/

  "Augmented Tf" should "return a TermWeighted" in
    assert(
      augmented_tf("text",tokens(1).get.tokens).isInstanceOf[TermWeighted]
    )

  "Augmented Tf" should "return right result" in
    assertResult(0.9230769231) {
      val x = augmented_tf("text",tokens(1).get.tokens)
      BigDecimal(x.weight).setScale(10, BigDecimal.RoundingMode.HALF_UP).toDouble
    }

  "Augmented Tf" should "return 0.5 if term is not in the document" in
    assertResult(0.5) {
      val x = augmented_tf("sdfaggaw3",tokens(1).get.tokens)
      BigDecimal(x.weight).setScale(10, BigDecimal.RoundingMode.HALF_UP).toDouble
    }

  "Augmented Tf" should "return a 0.5 weight if the Document provided is an empty string" in {
    assertResult(0.5) {augmented_tf("example", Array("")).weight}
  }

  "Augmented Tf" should "return a 0.5 weight if the Document provided is empty" in {
    assertResult(0.5) {augmented_tf("example", Array.empty[String]).weight}
  }

  /********************* Raw Bag ********************************/

  "Bag" should "return a TermWeighted" in
    assert(
      rawBag("text",tokens(1).get.tokens).isInstanceOf[TermWeighted]
    )

  "Bag" should "return right result" in
    assertResult(22) {
      val x = rawBag("text",tokens(1).get.tokens)
      BigDecimal(x.weight).setScale(10, BigDecimal.RoundingMode.HALF_UP).toDouble
    }

  "Bag" should "return a 0 weight if the Document provided is empty or only an empty string" in {
    assertResult(0) {rawBag("example", Array.empty[String]).weight}
    assertResult(0) {rawBag("example", Array("")).weight}
  }

  /********************* compositions ********************************/

  "TfIdf combination" should "return 0 if the term is in all documents" in {
    val tknTool = buildTokenSuite(TokenizedText(words1gram, stopWords))(GetDocContent)
    val tokens = Array(testPath8,testPath9,testPath10).par.map(x => tokenizeDocument(tknTool)(x)).toArray
    val dictionary = Dictionary(tokens)
    val idfValues = dictionary.getOrElse(Vector("")).par.map(IDFValue(_)(simpleIdf)(Option(tokens))).toArray
    assertResult(0.0){compose_weighting_Fun(tf)(Some(idfValues))("himissing",tokens(0).get.tokens).weight}
  }

  /********************* compose_weighting_function ********************************/

  "All functions" should "return the right result when composed with IDF values" in {
    val a = compose_weighting_Fun(tf)(Some(idfValues))
    val b = compose_weighting_Fun(augmented_tf)(Some(idfValues))
    val c = compose_weighting_Fun(tfLog)(Some(idfValues))
    val tokens = this.tokens.map(_.get.tokens)
    assertResult(0.0012089708834031455){a("example",tokens(0)).weight}
    assertResult(0.0){a("example",Array.empty[String]).weight}
    assertResult(0.10120187302050646){b("example",tokens(0)).weight}
    assertResult(0.0012048396307164518){c("example",tokens(0)).weight}
  }
}
