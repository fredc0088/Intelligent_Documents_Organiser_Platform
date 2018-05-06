package org.Fcocco01.DocumentClassifier.Test

import org.Fcocco01.DocumentClassifier._
import Core.DataSetMorph.{Dictionary, buildTokenSuite, createVector, tokenizeDocument}
import Core.Tokenization.{StopWords, TokenizedText}
import Essentials.Util.I_O.GetDocContent
import TestingResources.{Paths, Regexes, stopWords}
import Paths._
import Regexes.words1gram
import Essentials.Types.TypeClasses.{DocPath, Document, TokenSuite}
import Essentials.Types.TypeClasses.Vectors.{DocumentVector, EmptyVector, RealVector}
import Core.Features.{IDF, Ranking_Modellers}
import Ranking_Modellers.{IDFValue, compose_weighting_Fun, rawBag, tf}
import Essentials.Types.Tokens
import IDF.simpleIdf

class DataSetMorphTest extends UnitTest("Core.DataSetMorph") {

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
    tokens = tests.par.map(x => tokenizeDocument(tknTool)(DocPath(x))).toArray
    dictionary = Dictionary(tokens)
    idfValues = dictionary.getOrElse(Vector("")).par.map(IDF.IDFValue(_)(simpleIdf)(Option(tokens))).toArray
    vectors = tokens.par.map(x => createVector(compose_weighting_Fun(tf)(Some(idfValues)), dictionary)(x)).toArray

    super.beforeAll()
  }

  /**************************Test StopWords*************************/
  "A StopWords object" should "return the right result" in {
    assertResult("a about above across after afterwards again against all almost alone along already also although always am among amongst amoungst amount an and another any anyhow anyone anything anyway anywhere are around as at back be became because become becomes becoming been before beforehand behind being below beside besides between beyond bill both bottom but by call can cannot cant co computer con could couldnt cry de describe detail do done down due during each eg eight either eleven else elsewhere empty enough etc even ever every everyone everything everywhere except few fifteen fify fill find fire first five for former formerly forty found four from front full further get give go had has hasnt have he hence her here hereafter hereby herein hereupon hers herse\" him himse\" his how however hundred i ie if in inc indeed interest into is it its itse\" keep last latter latterly least less ltd made many may me meanwhile might mill mine more moreover most mostly move much must my myse\" name namely neither never nevertheless next nine no nobody none noone nor not nothing now nowhere of off often on once one only onto or other others otherwise our ours ourselves out over own part per perhaps please put rather re same see seem seemed seeming seems serious several she should show side since sincere six sixty so some somehow someone something sometime sometimes somewhere still such system take ten than that the their them themselves then thence there thereafter thereby therefore therein thereupon these they thick thin third this those though three through throughout thru thus to together too top toward towards twelve twenty two un under until up upon us very via was we well were what whatever when whence whenever where whereafter whereas whereby wherein whereupon wherever whether which while whither who whoever whole whom whose why will with within without would yet you your yours yourself yourselves")
    {stopWords}
  }

  "A StopWords object" should "return an empty string if path is non existent or not readable" in {
    assertResult("")
    {StopWords(DocPath("nonExistent.txt"))}
  }

  /********************* Test Dictionary **************************/
  "Creating dictionaries with 2 different constructors" should "produce the same result" in {
    val dictionary1 = Dictionary(tokens.take(3))
    val dictionary2 = Dictionary(tests.take(3).map(DocPath),buildTokenSuite(TokenizedText(words1gram, stopWords))(GetDocContent))
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
  "TokenizeDocument with a empty document" should "return None when tokenized" in {
    val doc = tokenizeDocument(tknTool)(DocPath("./test/resources/1/1.4/empty.txt"))
    doc shouldEqual None
  }

  "Tokenization of a non-empty file but all words falling into chosen stopwords'list" should "return None" in {
    val doc = tokenizeDocument(tknTool)(DocPath("./test/resources/1/1.4/text.txt"))
    doc shouldEqual None
  }

  "Tokenization of a non-empty file containing only numbers or special characters" should "return None" in {
    val doc = tokenizeDocument(tknTool)(DocPath("./test/resources/1/1.4/text2.txt"))
    doc shouldEqual None
  }

  /********************* Test Vector creation *****************/
  "Creating new vectors" should "create non-normalised vectors (non dependent from the corpus) when dictionary is not provided" in {
    val vector1 = createVector(rawBag _)(tokens(0))
    val vector2 = createVector(rawBag _)(tokens(1))
    val vector3 = createVector(rawBag, dictionary)(tokens(1))
    assert(vector1.size !== vector2.size)
    assert(vector2.size !== vector3.size)
    assert(vector1.size !== vector3.size)
    assert(vector2 !== vector3)

  }

  "createVector: Providing a None as Document" should "produce an EmptyVector" in {
    val v = createVector(compose_weighting_Fun(tf)(Some(idfValues)), dictionary)(None)
    v shouldBe EmptyVector
  }

  "createVector: Providing an empty tokenized document" should "return an EmptyVector" in {
    val v = createVector(compose_weighting_Fun(tf)(Some(idfValues)), dictionary)(Some(Document("?", Vector())))
    v shouldBe EmptyVector
  }

  "createVector: Providing a tokenized document with only an empty token" should "return an EmptyVector" in {
    val v = createVector(compose_weighting_Fun(tf)(Some(idfValues)), dictionary)(Some(Document("?", Vector(""))))
    v shouldBe EmptyVector
  }

  "createVector: Vectors created from the same base dictionary" should "contain same terms in same order" in {
    val vector1 = createVector(rawBag, dictionary)(tokens(1)).features.keys
    val vector2 = createVector(rawBag, dictionary)(tokens(3)).features.keys
    assert(vector1 == vector2)
  }

  "Empty vectors" should "prove useless in analysis" in {
    assert(EmptyVector.size == 0)
    assert(EmptyVector.id == "")
    assert(EmptyVector.isEmpty)
    assert(EmptyVector.features == Map.empty)
  }

  "A RealVector" should "have correct properties" in {
    val v = createVector(compose_weighting_Fun(tf)(Some(idfValues)))(tokens(0))
    assert(v.features.isInstanceOf[Map[String,Double]])
    v.id shouldNot be(null)
    v.id shouldNot be("")
    assert(v.size.isInstanceOf[Int])
    v.size shouldNot be(0)
  }

  "A RealVector" should "return the correct weight for a term" in {
    val v = createVector(compose_weighting_Fun(tf)(Some(idfValues)))(tokens(0))
    v("force") should be(0.0002054796013687995)
  }

  "A RealVector" should "result empty like an empty vector if instantiated with empty values" in {
    val v = RealVector("", Map.empty)
    v.isEmpty should be(true)
    v.size should be(0)
  }

}
