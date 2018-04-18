package org.Fcocco01.DocumentClassifier

import Core._
import Weight.IDF.{simpleIdf, IDFValue}
import Weight.ModelFunctions._
import DocumentDataSetMorph.{Dictionary, buildTokenSuite, createVector, tokenizeDocument}
import Clustering._
import HierarchicalClustering._
import FlatClustering._
import DocGathering.DocumentFinder
import TokenPackage.Tokenizer.{StopWords, TokenizedText}
import org.Fcocco01.DocumentClassifier.Utils._
import Util.I_O.GetDocContent
import Util.Time.currentTimeMins
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
      if (paths.size == 0) {
        progress.set(10.0)
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

        progress.set(1.0)

        implicit val docs = paths.par.map(x => tknFun(x)).toArray

        println("Documents tokenised in " + currentTimeMins(time))

        progress.set(4.0)

        val dictionary = if (idfChoice == "No Dictionary") None else Dictionary(docs)

        progress.set(5.5)

        import org.scalameter._
        val timea = measure {
          Some(dictionary.getOrElse(Vector("")).par
            .map(IDFValue(_)(simpleIdf)((Option(docs)))).toVector)
        }
        println(s"$timea ms")

        val idfWeightedTerms =
          if(idfChoice == "" || dictionary == None)
            None
          else Some(dictionary.getOrElse(Vector("")).par
                .map(IDFValue(_)(simpleIdf)((Option(docs)))).toVector)

        val vectorFun = weightFun match {
          case "Tf" => createVector(compose_weighting_Fun(tf(_,_))(idfWeightedTerms),dictionary)
          case "wdf" => createVector(compose_weighting_Fun(wdf(_,_))(idfWeightedTerms),dictionary)
          case "TFLog" => createVector(compose_weighting_Fun(tfLog(_,_))(idfWeightedTerms),dictionary)
          case "Bag-Of-Words" => createVector(compose_weighting_Fun(bag(_,_))(idfWeightedTerms),dictionary)
        }

        println("Terms weighted to idf in " + currentTimeMins(time))

        val vectors = docs.par.map(x => vectorFun(x)).filterNot(_.isEmpty).toArray

        println("Vectors obtained in " + currentTimeMins(time))

        progress.set(7.0)

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

          progress.set(9.0)

          Dendrogram(clusters)

        } else {

          val numberOfClusters = clustersNumber

          val clustering: Vector[FlatClustering.Cluster] = K_Means(numberOfClusters)(compareFun)(vectors: _*)

          println("Clustering after  " + currentTimeMins(time))

          printClusters(clustering: _*)

          new Scene(new HBox)
        }
        progress.set(10.0)
        result
      }
    }
}
