package org.Fcocco01.DocumentClassifier.Test

import org.Fcocco01.DocumentClassifier._
import Core.DataSetMorph.{Dictionary, buildTokenSuite, createVector, tokenizeDocument}
import Core.Tokenization.TokenizedText
import Essentials.Util.I_O.GetDocContent
import TestingResources.{Paths, Regexes, stopWords}
import Paths._
import Regexes.words1gram
import Essentials.Types.TypeClasses.{Document, TokenSuite}
import Essentials.Types.TypeClasses.Vectors.{RealVector, DocumentVector, EmptyVector}
import Core.Features.{IDF,Bag_Of_Words_Models}
import Bag_Of_Words_Models.{IDFValue, rawBag, tf, compose_weighting_Fun}
import Essentials.Types.Tokens
import IDF.simpleIdf

class DataSetMorphTest extends UnitTest("DataSetMorph") {

  var vectors : Array[DocumentVector] = _
  val tests = Array(testPath1,testPath2,testPath3,testPath4,testPath5,testPath6)
  var tokens : Array[Option[Document]] = _
  var dictionary : Option[Tokens] = _
  var idfValues : Array[IDFValue] = _
  var realVectors : Array[RealVector] = _
  var emptyVectors : Array[DocumentVector] = _
  var tknTool : TokenSuite = _


  override def beforeAll(): Unit = {

    tknTool = buildTokenSuite(TokenizedText(words1gram, stopWords))(GetDocContent)
    tokens = tests.par.map(x => tokenizeDocument(tknTool)(x)).toArray
    dictionary = Dictionary(tokens)
    idfValues = dictionary.getOrElse(Vector("")).par.map(IDF.IDFValue(_)(simpleIdf)(Option(tokens))).toArray
    vectors = tokens.par.map(x => createVector(compose_weighting_Fun(tf)(Some(idfValues)), dictionary)(x)).toArray

    super.beforeAll()
  }

  /********************* Test Dictionary *****************/
  "Creating dictionaries with 2 different constructors" should "produce the same result" in {
    val dictionary1 = Dictionary(tokens.take(3))
    val dictionary2 = Dictionary(tests.take(3),buildTokenSuite(TokenizedText(words1gram, stopWords))(GetDocContent))
    assert(dictionary1.get == dictionary2.get)
  }

  "Creating a dictionary using vectors" should "produce same as a base dictionary (but not same order) if that dictionary was used to create the vectors" in {
    val dictionary1 = Dictionary(tokens.take(3))
    val dictionary3 = Dictionary(tokens.take(3).map(d => createVector(rawBag, dictionary1)(d)): _*)
    assert(dictionary1.get.size == dictionary3.get.size)
    assert(dictionary3.get.forall(dictionary1.get.toList.contains(_)))
  }

  "Empty dictionary" should "be generated from empty documents" in {
    val dictionary = Dictionary(Array(None,None))
    assert(dictionary.isEmpty)
  }

  "\"Empty\" input" should "create empty dictionary" in {
    val dictionary = Dictionary(EmptyVector)
    assert(dictionary.isEmpty)
  }

  /********************* Test tokenizeDocument *****************/
  "TokenizeDocument with a empty document" should "return None when tokenised" in {
    val doc = tokenizeDocument(tknTool)("./test/resources/1/1.4/empty.txt")
    doc shouldEqual None
  }

  "Tokenisation of a non-empty file but all words falling into chosen stopwords'list" should "return None" in {
    val doc = tokenizeDocument(tknTool)("./test/resources/1/1.4/text.txt")
    doc shouldEqual None
  }

  "Tokenisation of a non-empty file containing only number or special charaters" should "return None" in {
    val doc = tokenizeDocument(tknTool)("./test/resources/1/1.4/text2.txt")
    doc shouldEqual None
  }

  /********************* Test Vector creation *****************/
  "Creating new vectors" should "create non-normalised vectors when dictionary is not provided" in {
    val vector1 = createVector(rawBag _)(tokens(0))
    val vector2 = createVector(rawBag _)(tokens(1))
    val vector3 = createVector(rawBag, dictionary)(tokens(1))
    assert(vector1.size !== vector2.size)
    assert(vector2.size !== vector3.size)
    assert(vector1.size !== vector3.size)
    assert(vector2 !== vector3)

  }

  "Providing a None as Document" should "produce an empty vector" in {
    val v = createVector(compose_weighting_Fun(tf)(Some(idfValues)), dictionary)(None)
    v shouldBe EmptyVector
  }

  "Empty vectors" should "prove useless in analysis" in {
    assert(EmptyVector.size == 0)
    assert(EmptyVector.id == "")
    assert(EmptyVector.isEmpty)
  }

  "Creating vectors non empty" should "have correct properties" in {
    val v = createVector(compose_weighting_Fun(tf)(Some(idfValues)))(tokens(0))
    assert(v.features.isInstanceOf[Map[String,Double]])
    v.id shouldNot be(null)
    v.id shouldNot be("")
    assert(v.size.isInstanceOf[Int])
    v.size shouldNot be(0)
  }

  "Non-empty vectors (normalised)" should "have the same terms of the dictionary used to normalise them" in {
    val v = createVector(compose_weighting_Fun(tf)(Some(idfValues)), dictionary)(tokens(0))
    assert(dictionary.get.size == v.size)
    assert(dictionary.get.forall(v.features.keys.toVector.contains(_)))
  }

  "Vectors created from the same base dictionary" should "contain same terms in same order" in {
    val vector1 = createVector(rawBag, dictionary)(tokens(1)).features.keys
    val vector2 = createVector(rawBag, dictionary)(tokens(3)).features.keys
    assert(vector1 == vector2)
  }
}
