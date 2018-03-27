//package org.Fcocco01.DocumentClassifier
//
//import Util.I_O.GetDocContent
//import Util.Time.currentTimeMins
//import org.Fcocco01.DocumentClassifier.OOP.Classify.Dictionary
//import org.Fcocco01.DocumentClassifier.OOP.Analysis.IDF
//import org.Fcocco01.DocumentClassifier.Token.Tokenizer.TokenizedText
//import org.Fcocco01.DocumentClassifier.OOP.Classify._
//import org.Fcocco01.DocumentClassifier.OOP.Analysis.ModelFunctions.tfidf
//import org.Fcocco01.DocumentClassifier.DocGathering.DocumentFinder
//
//class ClassifyTest extends UnitTest("Classify") {
//
//  import TestingResources._
//  import TestingResources.stopWords
//  import Regexes.words1gram
//
//  var vectors: Vector[DocumentVector] = Vector[DocumentVector]()
//  var singleVector: DocumentVector = _
//  private var timeTaken: Double = _
//  val tests = Array(testPath1, testPath2, testPath3
//    //,testPath4
//    , testPath5, testPath6
//  )
//
//  override def beforeAll(): Unit = {
//    timeTaken = System.nanoTime
//
//    println("Start initialisation after " + currentTimeMins(timeTaken))
//
//    implicit val vectorsSingle: Option[Traversable[DocumentVector]] =
//      Option(tests.par.map(x => VectorFactory(TokenizedText(words1gram, stopWords), x, GetDocContent)).filter(_.isInstanceOf[SingleVector]).toVector)
//
//    implicit val documents: Option[Traversable[String]] =
//      Option(tests.par.map(GetDocContent(_).replace("\n", " ").toLowerCase).toVector)
//
//    println("Vectors and docs obtained in " + currentTimeMins(timeTaken))
//
//    val dictionary =
//      Dictionary(tests, TokenizedText(words1gram, stopWords))(vectorsSingle)
//
//    println("Created dictionary in " + currentTimeMins(timeTaken))
//
//    val idfWeightedTerms = dictionary.par.map(IDF.IDFValue(_) (tests, GetDocContent)(documents)).toVector
//
//    println("IDF values in " + currentTimeMins(timeTaken))
//
//    val parVectorsSingle = vectorsSingle
//      .getOrElse(getDefaultVectors(tests, words1gram, stopWords)).par
//
//    println("Start to normalise after " + currentTimeMins(timeTaken))
//
//    val vectors = parVectorsSingle.map { a =>
//      val tfidfFun = tfidf(idfWeightedTerms) // Assign idf results to tfidf function to be used as modeller
//      NormalisedVector(dictionary, a.asInstanceOf[SingleVector], tfidfFun)
//    }(collection.breakOut)
//    parVectorsSingle.foreach { e => Thread.sleep(10000); println(e.docId) }
//
//    val p: Array[DocumentVector] = vectors.toArray
//    println("Instantiating time was " + currentTimeMins(timeTaken))
//    super.beforeAll()
//  }
//
//
//  override def afterAll(): Unit = {
//    println("The whole process has taken " + currentTimeMins(timeTaken))
//    vectors.foreach(el => println(el.toString))
//  }
//
//  it should "Return no empty vectors and no skip any" in {
//    vectors.size shouldBe 5
//  }
//
//  it should "Return right size" in {
//    vectors(0).size shouldBe 1210
//  }
//
//  it should "vector values should never be above 1" in {
//    vectors(0).vector.foreach { x => assert(x._2 < 1) }
//  }
//
//  it should "All its objects are same size" in {
//    vectors.foreach(x => x.size shouldBe 1210)
//  }
//
//  //  it should "Having a single vector of right size" in {
//  //    singleVector.size
//  //  }
//
//  it should "have a total test that exceed 10 minutes execution" in {
//    val time = ((System.nanoTime() - timeTaken) / 1E9) / 60
//    tests.length match {
//      case x if 0 until 8 contains x => assert(time < 10)
//
//    }
//
//  }
//
//}
