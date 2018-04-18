package org.Fcocco01.DocumentClassifier

import java.io.File

import Utils.Util.I_O._
import Core.DocumentDataSetMorph.Dictionary
import Utils.Util.Time.currentTimeMins
import Core.DocGathering.DocumentFinder
import Core.TokenPackage.Tokenizer.{StopWords, TokenizedText}
import Core.DocumentDataSetMorph._
import Core.Weight.{IDF, ModelFunctions}
import IDF._
import ModelFunctions.{tf, compose_weighting_Fun}
import Core.Clustering._
import FlatClustering._
import Core.Clustering.Distance._

object TestingFlat extends App {
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


  }

  import TestingResources._
  import Regexes.words1gram


  val time = System.nanoTime

  println("Start initialisation after " + currentTimeMins(time))

  val tests = DocumentFinder(Array(
    //            "./src"
    //            ,
    //            "./src/test/resources/3/3.2"
    //            ,"./src/test/resources/1/1.3"
    //            ,
    "C:/Users/USER/Documents/Projects/Git_Repos/Document_Clusterizer_Notes"
    //            ,"C:/Users/USER/odrive"
    //            ,"C:/Users/USER/Documents/Important docs"
    //            ,"C:/Users/USER/Desktop"
    //            //      ,"C:/Users/USER"
    //                      ,"C:/Users/USER/Downloads"
  ), Array(
    new File("./src/main/resources").getCanonicalPath
  )
  )

  val stopWords = StopWords("./src/main/resources/stop-word-list.txt")

  val tknTool = buildTokenSuite(TokenizedText(words1gram, stopWords))(GetDocContent)

  val tknFun = tokenizeDocument(tknTool)

  implicit val docs = tests.par.map(x => tknFun(x)).toArray

  println("Documents tokenised in " + currentTimeMins(time))

  val dictionary = Dictionary(docs)

  println("Dictionary created in " + currentTimeMins(time))

  val idfWeightedTerms = dictionary.getOrElse(Vector("")).par.map(IDF.IDFValue(_)(simpleIdf)(Option(docs))).toVector

  println("Terms weighted to idf in " + currentTimeMins(time))

  val tfidfFun = compose_weighting_Fun(tf(_,_))(Some(idfWeightedTerms))

  val vectorFun = createVector(tfidfFun, dictionary)

  val vectors = docs.par.map(x => vectorFun(x)).filterNot(_.isEmpty).toArray

  println("Vectors obtained in " + currentTimeMins(time))

  println("Clustering after  " + currentTimeMins(time))

  val c = K_Means(3)(euclidean)(vectors:_*)
  printClusters(c:_*)

}
