package org.Fcocco01.DocumentClassifier

import Core.Classify.{Dictionary, buildTokenSuite, createVector, tokenizeDocument}
import Core.Clustering.{DVector,HierarchicalClustering, Distance, Similarity}
import HierarchicalClustering.{createSimMatrix, HAC, SingleCluster, Single_Link, Complete_Link}
import Core.TokenPackage.Tokenizer.{StopWords, TokenizedText}
import Core.DocGathering.DocumentFinder
import Utils.Types.Token
import Utils.Util.I_O.GetDocContent
import Core.Analysis.{ModelFunctions,IDF}
import ModelFunctions._
import Utils.Types.TypeClasses.Document
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage

object Main {

  val indipendentVectors : Boolean = false

  val directoriesChosen : Array[String] = Array("")

  val exclusions : Array[String] = Array("")

  val stopwords : String = "./src/main/resources/stop-word-list.txt"

  val regex : String = "[^a-z0-9]"

  val clusteringMode = ""

  val chooseFun = ""

  val comparison = ""

  val linkStrategy = ""

  val tests = DocumentFinder(directoriesChosen, exclusions)

  val stopWords = StopWords(stopwords)

  val tknTool = buildTokenSuite(TokenizedText(regex, stopWords))(GetDocContent)

  val tknFun = tokenizeDocument(tknTool)

  implicit val docs = tests.par.map(x => tknFun(x)).toArray

  val vectorFun = chooseFun match {
    case "TfIdf" if(!indipendentVectors) => {
      val dictionary = Dictionary(docs)
      val idfWeightedTerms = dictionary.par.map(IDF.IDFValue(_)(Option(docs.map(_.getOrElse(Document("", Vector.empty[Token])))))).toVector
      val weightFun = tfidf(idfWeightedTerms)
      createVector(weightFun)(Some(dictionary))
    }
    case "WfIdf" if(!indipendentVectors) => {
      val dictionary = Dictionary(docs)
      val idfWeightedTerms = dictionary.par.map(IDF.IDFValue(_)(Option(docs.map(_.getOrElse(Document("", Vector.empty[Token])))))).toVector
      val weightFun = wdfidf(idfWeightedTerms)
      createVector(weightFun)(Some(dictionary))
    }
    case "Tf" => {
      if(!indipendentVectors) {
        val dictionary = Dictionary(docs)
        createVector(tf(_,_))(Some(dictionary))
      } else {
        createVector(tf(_,_))()
      }}
    case "wdf" => {
      if(!indipendentVectors) {
        val dictionary = Dictionary(docs)
        createVector(wdf(_,_))(Some(dictionary))
      } else {
        createVector(wdf(_,_))()
      }}
    case "Log Normalisation" => {
      if(!indipendentVectors) {
        val dictionary = Dictionary(docs)
        createVector(tfLogNorm(_,_))(Some(dictionary))
      } else {
        createVector(tfLogNorm(_,_))()
      }}
    case "Bag-Of-Words" => {
      if(!indipendentVectors) {
        val dictionary = Dictionary(docs)
        createVector(bag(_,_))(Some(dictionary))
      } else {
        createVector(bag(_,_))()
      }}
  }

  val vectors = docs.par.map(x => vectorFun(x)).filterNot(_.isEmpty).toArray

  object View extends JFXApp {
    stage = new PrimaryStage
    if(clusteringMode == "Hierarchical") {
      val matrix = comparison match {
        case "Cosine Sim" => createSimMatrix(vectors, Similarity.cosine)
        case "Euclidean Dist" => createSimMatrix(vectors, Distance.euclidean)
        case "Manhattan Dist" => createSimMatrix(vectors, Distance.manhattan)
      }
      val docWrappedInCluster = (x: Seq[DVector]) => x.map(SingleCluster(_)).toList
      val clusters = linkStrategy match {
        case "Single Link" => HAC(matrix, docWrappedInCluster, Single_Link, vectors: _*)
        case "Complete Link" => HAC(matrix, docWrappedInCluster, Complete_Link, vectors: _*)
      }
    } else {


    }
  }

}
