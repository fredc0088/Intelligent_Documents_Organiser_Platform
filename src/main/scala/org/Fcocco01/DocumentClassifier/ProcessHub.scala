package org.Fcocco01.DocumentClassifier

import Core._
import Analysis.IDF
import Analysis.ModelFunctions._
import Classify.{Dictionary, buildTokenSuite, createVector, tokenizeDocument}
import Clustering._
import HierarchicalClustering._
import DocGathering.DocumentFinder
import TokenPackage.Tokenizer.{StopWords, TokenizedText}
import org.Fcocco01.DocumentClassifier.Utils._
import Types.Token
import Types.TypeClasses.Document
import Util.I_O.GetDocContent
import Util.Time.currentTimeMins
import Visualisation.HierarchicalGraphic.Dendrogram
import scalafx.scene.Scene
import scalafx.scene.layout.HBox


class ProcessHub(indipendentVectors: Boolean, directoriesChosen: Array[String] = Array(""),
                   exclusions: Array[String] = Array(""), stopwords: Option[String], regex: Option[String],
                   clusteringMode: String = "", chooseFun: String = "", comparison: String = "",
                   linkStrategy: String = "", clustersNumber : Int)
object ProcessHub {
  def apply(
             //             bar: Double => Unit,
             indipendentVectors: Boolean, directoriesChosen: Array[String] = Array(""),
             exclusions: Array[String] = Array(""), stopwords: Option[String], regex: Option[String],
             clusteringMode: String, chooseFun: String, comparison: String): (String,Int) => Scene =

    (linkStrategy: String, clustersNumber: Int) => {
        val time = System.nanoTime

        println("Start initialisation after " + currentTimeMins(time))

        val paths = DocumentFinder(directoriesChosen, exclusions)

        val stopWords = stopwords match {
          case Some(s) => StopWords(s)
          case None => StopWords(Utils.Constants.Defaults.stopwordPath)
        }

        val regexToUse = regex match {
          case Some(r) => r
          case None => Utils.Constants.Defaults.regexWord1Gram
        }
        val tknTool = buildTokenSuite(TokenizedText(regexToUse, stopWords))(GetDocContent)

        val tknFun = tokenizeDocument(tknTool)

        //    bar(0.1)

        implicit val docs = paths.par.map(x => tknFun(x)).toArray

        println("Documents tokenised in " + currentTimeMins(time))

        //    bar(0.4)

        val dictionary = if (!indipendentVectors) Some(Dictionary(docs)) else None

        val vectorFun = chooseFun match {
          case "TfIdf" if (!indipendentVectors) => {
            val idfWeightedTerms = dictionary.get.par
              .map(IDF.IDFValue(_)(Option(docs.map(_.getOrElse(Document("", Vector.empty[Token])))))).toVector
            val weightFun = tfidf(idfWeightedTerms)
            createVector(weightFun)(dictionary)
          }
          case "WfIdf" if (!indipendentVectors) => {
            val idfWeightedTerms = dictionary.get.par
              .map(IDF.IDFValue(_)(Option(docs.map(_.getOrElse(Document("", Vector.empty[Token])))))).toVector
            val weightFun = wdfidf(idfWeightedTerms)
            createVector(weightFun)(dictionary)
          }
          case "Tf" => createVector(tf(_, _))(dictionary)
          case "wdf" => createVector(wdf(_, _))(dictionary)
          case "Log Normalisation" => createVector((tfLogNorm(_, _)))(dictionary)
          case "Bag-Of-Words" => createVector(bag(_, _))(dictionary)
        }

        println("Terms weighted to idf in " + currentTimeMins(time))

        val vectors = docs.par.map(x => vectorFun(x)).filterNot(_.isEmpty).toArray

        println("Vectors obtained in " + currentTimeMins(time))

        //    bar(0.7)

        val result = if (clusteringMode == "Hierarchical") {
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
          println("Clustering after  " + currentTimeMins(time))
          //      bar(0.9)
          Dendrogram(clusters)
        } else {
          val numberOfClusters = clustersNumber

          new Scene(new HBox)
        }
        //    bar(1.0)
        result
      }
}
