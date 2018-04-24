package org.Fcocco01.DocumentClassifier

import Core._
import Features.IDF.{simpleIdf, smootherIdf, IDFValue}
import Features.Bag_Of_Words_Models._
import DocumentDataSetMorph.{Dictionary, buildTokenSuite, createVector, tokenizeDocument}
import Clustering._
import HierarchicalClustering._
import FlatClustering._
import DocGathering.DocumentFinder
import Tokenization.{StopWords, TokenizedText}
import Utils._
import Util.I_O.GetDocContent
import Util.Time.currentTimeMins
import Constants._
import Visualisation.HierarchicalGraphic.Dendrogram
import javafx.beans.property.{ReadOnlyDoubleProperty, ReadOnlyDoubleWrapper}
import scalafx.scene.Scene
import scalafx.scene.layout.HBox


class ProcessHub(directoriesChosen: Array[String] = Array(""),
                   exclusions: Array[String] = Array(""), stopwords: Option[String], regex: Option[String],
                   clusteringMode: String = "", weightFun: String = "", idfChoice: String, comparison: String = "",
                   linkStrategy: String = "", clustersNumber : Int)
object ProcessHub {

  private val progress:ReadOnlyDoubleWrapper = new ReadOnlyDoubleWrapper()

  def getProgress: Double = progressProperty.get

  def progressProperty: ReadOnlyDoubleProperty = progress

  def apply(directoriesChosen: Array[String] = Array(""),
             exclusions: Array[String] = Array(""), stopwords: Option[String], regex: Option[String],
             clusteringMode: String, weightFun: String, idfChoice: String, comparison: String) : (String,Int) => Scene =

    (linkStrategy: String, clustersNumber: Int) => {
      val time = System.nanoTime

      println("Start initialisation after " + currentTimeMins(time))

      val paths = DocumentFinder(directoriesChosen, exclusions)
      if (paths.size == ZERO) {
        progress.set(TEN)
        println("No document found")
        new Scene
      }
      else {

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

        progress.set(ONE)

        implicit val corpus = paths.par.map(x => tknFun(x)).toArray

        println("Documents tokenised in " + currentTimeMins(time))

        progress.set(FOUR)

        val dictionary = if (idfChoice == "No Dictionary") None else Dictionary(corpus)

        progress.set(FIVE_HALF)

        val idfWeightedTerms =
          if(idfChoice == "" || dictionary == None)
            None
          else idfChoice match {
            case "Idf" => Some(dictionary.getOrElse(Vector("")).par
              .map(IDFValue(_)(simpleIdf)((Option(corpus)))).toVector)
            case "Smooth Idf" => Some(dictionary.getOrElse(Vector("")).par
              .map(IDFValue(_)(smootherIdf)((Option(corpus)))).toVector)
          }


        val vectorFun = weightFun match {
          case "Tf" => createVector(compose_weighting_Fun(tf(_,_))(idfWeightedTerms),dictionary)
          case "wdf" => createVector(compose_weighting_Fun(wdf(_,_))(idfWeightedTerms),dictionary)
          case "TFLog" => createVector(compose_weighting_Fun(tfLog(_,_))(idfWeightedTerms),dictionary)
          case "Bag-Of-Words" => createVector(compose_weighting_Fun(rawBag(_,_))(idfWeightedTerms),dictionary)
        }

        println("Terms weighted to idf in " + currentTimeMins(time))

        val vectors = corpus.par.map(x => vectorFun(x)).filterNot(_.isEmpty).toArray

        println("Vectors obtained in " + currentTimeMins(time))

        progress.set(SEVEN)

        val compareFun = comparison match {
          case "Cosine Sim" => Similarity.cosine(_, _)
          case "Euclidean Dist" => Distance.euclidean(_, _)
          case "Manhattan Dist" => Distance.manhattan(_, _)
        }

        val result = if (clusteringMode == "Hierarchical") {
          val matrix = createSimMatrix(vectors, compareFun)

          val docWrappedInCluster = (x: Seq[DVector]) => x.map(SingleCluster(_)).toList
          val clusters = linkStrategy match {
            case "Single Link" => HAC(matrix, docWrappedInCluster, Single_Link, vectors: _*)
            case "Complete Link" => HAC(matrix, docWrappedInCluster, Complete_Link, vectors: _*)
          }
          println("Clustering after  " + currentTimeMins(time))

          progress.set(NINE)

          Dendrogram(clusters)

        } else {

          val numberOfClusters = clustersNumber

          val clustering: Vector[FlatClustering.Cluster] = K_Means(numberOfClusters)(compareFun)(vectors: _*)

          println("Clustering after  " + currentTimeMins(time))

          printClusters(clustering: _*)

          new Scene(new HBox)
        }
        progress.set(TEN)
        result
      }
    }
}
