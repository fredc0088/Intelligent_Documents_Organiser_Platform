package org.Fcocco01.DocumentClassifier

import Util.I_O.GetDocContent
import org.Fcocco01.DocumentClassifier.Classify.Dictionary
import org.Fcocco01.DocumentClassifier.Analysis.IDF
import org.Fcocco01.DocumentClassifier.Token.Tokenizer.{TokenizedText}
import Classify._
import org.Fcocco01.DocumentClassifier.Analysis.ModelFunctions.tfidf

class ClassifyTest extends UnitTest("Classify") {
  import Resources._
  var vectors : Vector[NormalisedVector] = Vector[NormalisedVector]()
  var singleVector : DocumentVector = _
  var timeTaken: Double = _

  override def beforeAll(): Unit = {
    timeTaken = System.nanoTime()
    println("Starting instantiation")
    val timeInstantiation = System.nanoTime()
    val tests = Array(testPath1, testPath2, testPath3
      //,testPath4
    )
    val dictionary =
      Dictionary(tests, TokenizedText("\\s+", stopWords))
    val idfWeightedTerms = for {
      s <- dictionary
    } yield IDF.IDFValue(s, tests, GetDocContent)
    println("Ready at " + (System.currentTimeMillis() / 6000) )
    val testsPar = tests.par
    vectors = testsPar.map{ a =>
          val tfidfFun = tfidf(idfWeightedTerms) // Assign idf results to tfidf function to be used as modeller
          NormalisedVector(dictionary,DocumentVector(TokenizedText("\\s+", stopWords), a, Option(GetDocContent)),tfidfFun)
        }(collection.breakOut)

    testsPar.foreach{e => Thread.sleep(100000); println(e)}
    singleVector = DocumentVector(TokenizedText("(\\s+)(\\()(+)(-)(*)(\\))", stopWords), testPath2, Option(GetDocContent))
    println(("Instatiating time was " + ((System.nanoTime() - timeInstantiation) / 1000000000.0) / 60) + " mins")
    super.beforeAll()
  }


  override def afterAll() : Unit = {
    timeTaken = (System.nanoTime() - timeTaken) / 1000000000.0
    println("The whole process has taken " + (timeTaken / 60) + " mins")
  }

  it should "Return no empty vectors and no skip any" in {
    vectors.size shouldBe 3
  }

  it should "Return right size" in {
    vectors(0).size shouldBe 2110
  }

  it should "vector values should never be above 1" in {
    vectors(0).vector.foreach{x => assert(x._2 < 1)}
  }

  it should "All its objects are same size" in {
    vectors.foreach(x => x.size shouldBe 2110)
  }

  it should "Having a single vector of right size" in {
    singleVector.size
  }

  it should "have a total test that exceed 10 minutes execution" in {
    assert((((System.nanoTime() - timeTaken) / 1000000000.0) / 60) < 10)
  }

}
