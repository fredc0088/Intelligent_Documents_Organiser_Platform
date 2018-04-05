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
import org.Fcocco01.DocumentClassifier.Visualisation.HierarchicalGraphic_Test.Dendrogram
import java.awt.Toolkit

import javafx.fxml.FXMLLoader
import javafx.stage.StageStyle
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.stage.{Stage, WindowEvent}
import scalafxml.core.{FXMLLoader, FXMLView, NoDependencyResolver}

object Main extends JFXApp {

  val primaryStage = new PrimaryStage
  val resource = FXMLLoader.load(this.getClass.getClassLoader.getResource("Gui.fxml"))
  if (resource == null) println("Cannot load resource")

  val root = FXMLView(resource, NoDependencyResolver)
  //  val scene = new Scene(root, 500, 500)
  //  primaryStage.setScene(scene)
  primaryStage.title = "Document Clusterizer"
  primaryStage.initStyle(StageStyle.DECORATED)
  primaryStage.show()


  val screenSize = Toolkit.getDefaultToolkit.getScreenSize
  val (w, h) = (screenSize.width * 0.9, screenSize.height * 0.7)


  class MainProcess(indipendentVectors: Boolean, directoriesChosen: Array[String] = Array(""), exclusions: Array[String] = Array(""),
                    stopwords: String = "./src/main/resources/stop-word-list.txt", regex: String = "[^a-z0-9]",
                    clusteringMode: String = "", chooseFun: String = "", comparison: String = "", linkStrategy: String = "") {
    def getGraphic() = {

      var progress = 0.0

      val time = System.nanoTime

      println("Start initialisation after " + currentTimeMins(time))

      val paths = DocumentFinder(directoriesChosen, exclusions)

      val stopWords = StopWords(stopwords)

      val tknTool = buildTokenSuite(TokenizedText(regex, stopWords))(GetDocContent)

      val tknFun = tokenizeDocument(tknTool)

      progress = 0.1

      implicit val docs = paths.par.map(x => tknFun(x)).toArray

      println("Documents tokenised in " + currentTimeMins(time))

      progress = 0.4

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
        case "Tf" => createVector(tf(_,_))(dictionary)
        case "wdf" => createVector(wdf(_,_))(dictionary)
        case "Log Normalisation" => createVector((tfLogNorm(_,_)))(dictionary)
        case "Bag-Of-Words" => createVector(bag(_,_))(dictionary)
      }

      println("Terms weighted to idf in " + currentTimeMins(time))

      val vectors = docs.par.map(x => vectorFun(x)).filterNot(_.isEmpty).toArray

      println("Vectors obtained in " + currentTimeMins(time))

      progress = 0.7

      if (clusteringMode == "Hierarchical") {
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

      progress = 0.7

      println("Clustering after  " + currentTimeMins(time))

      val newP = new Dendrogram(cluster)(root, w, h)

      println("Finished after " + currentTimeMins(time) )


      val result = new Stage()
      result.setOnCloseRequest((event: WindowEvent) => println("Close"))
      result.setScene(newP)

    }
  }

}