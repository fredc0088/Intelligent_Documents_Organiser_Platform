package org.Fcocco01.DocumentClassifier.Visualisation.Gui

import java.io.File
import java.net.URL
import java.util

import javafx.application.Platform
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import javafx.concurrent.Task
import javafx.event.ActionEvent
import javafx.scene.control._
import javafx.scene.{control => jfxsc, layout => jfxsl}
import javafx.stage.{DirectoryChooser, FileChooser, Stage}
import javafx.{fxml => jfxf}
import org.Fcocco01.DocumentClassifier.Processes._
import scalafx.Includes._
import scalafx.scene.Scene
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.layout.AnchorPane

import scala.collection.immutable.HashMap

//noinspection VarCouldBeVal,VarCouldBeVal,VarCouldBeVal,VarCouldBeVal,VarCouldBeVal,VarCouldBeVal,VarCouldBeVal,VarCouldBeVal,VarCouldBeVal,VarCouldBeVal,VarCouldBeVal,VarCouldBeVal,VarCouldBeVal,VarCouldBeVal,VarCouldBeVal,VarCouldBeVal,VarCouldBeVal,VarCouldBeVal,VarCouldBeVal,VarCouldBeVal,VarCouldBeVal,ScalaUnusedSymbol,ScalaUnusedSymbol,ScalaUnusedSymbol,ScalaUnusedSymbol,ScalaUnusedSymbol,ScalaUnusedSymbol,ScalaUnusedSymbol,ScalaUnusedSymbol,ScalaUnusedSymbol,ScalaUnusedSymbol,ScalaUnusedSymbol,ScalaUnusedSymbol,ScalaUnusedSymbol,ScalaUnusedSymbol,ScalaUnusedSymbol,EmptyParenMethodAccessedAsParameterless
class Controller extends jfxf.Initializable {

  @jfxf.FXML
  var anchorDelegate: jfxsl.AnchorPane = _
  var anchor: AnchorPane = _
  @jfxf.FXML
  var inclusionList: jfxsc.ListView[String] = _
  @jfxf.FXML
  var exclusionList: jfxsc.ListView[String] = _
  var group = new ToggleGroup
  @jfxf.FXML
  var customOption: jfxsc.RadioButton = _
  @jfxf.FXML
  var defaultOption: jfxsc.RadioButton = _
  @jfxf.FXML
  var regexOption: jfxsc.TextField = _
  @jfxf.FXML
  var clusteringType: jfxsc.ChoiceBox[String] = _
  @jfxf.FXML
  var weightingList: jfxsc.ChoiceBox[String] = _
  @jfxf.FXML
  var IDFlist: jfxsc.ChoiceBox[String] = _
  @jfxf.FXML
  var strategyList: jfxsc.ChoiceBox[String] = _
  @jfxf.FXML
  var linkageBox: jfxsl.HBox = _
  @jfxf.FXML
  var linkageList: jfxsc.ChoiceBox[String] = _
  @jfxf.FXML
  var noOfClusterBox: jfxsl.HBox = _
  @jfxf.FXML
  var progressBar: jfxsc.ProgressBar = new ProgressBar()
  @jfxf.FXML
  var startButton: jfxsc.Button = _
  @jfxf.FXML
  var stopButton: jfxsc.Button = _
  @jfxf.FXML
  var noOfClusters: jfxsc.TextField = _
  @jfxf.FXML
  var viewResultButton: jfxsc.Button = _
  @jfxf.FXML
  var viewError: jfxsc.Button = _

  protected var inclusionListProperty = new SimpleListProperty[String]
  protected var exclusionListProperty = new SimpleListProperty[String]
  protected var customFile: File = _
  private var copyWorker: Task[_] = _

  private var currentOutput : Scene = _
  private var running: Thread = _

  @jfxf.FXML
  private def loadDirectories(event: ActionEvent): Unit = {
    val btn = event.getSource.asInstanceOf[Button]
    val id = btn.getId
    if (id == "directoryFileChooser") {
      val directoryChooser = new DirectoryChooser
      val selectedDirectory = directoryChooser.showDialog(null)
      if (selectedDirectory != null) {
        // debug and test
        System.out.println("event = [" + event + "], selectedFile = [" + selectedDirectory.getAbsolutePath + "]")
        val inclusionDirItems = new util.ArrayList[String]
        if (inclusionListProperty.getValue != null && !inclusionListProperty.getValue.isEmpty) inclusionDirItems.addAll(inclusionListProperty.getValue)
        if(!inclusionDirItems.contains(selectedDirectory.getCanonicalPath)) inclusionDirItems.add(selectedDirectory.getCanonicalPath)
        inclusionListProperty.set(FXCollections.observableArrayList(inclusionDirItems))
      }
      else System.out.println("event = [" + event + "], selectedFile = [No Directory selected]")
    }
    else if (id == "exclusionFileChooser") {
      val dirChooser = new DirectoryChooser
      dirChooser.setTitle("Open Resource File")
      val selectedDir = dirChooser.showDialog(null)
      if (selectedDir != null) {
        System.out.println("event = [" + event + "], selectedFile = [" + selectedDir + "]")
        val exclusionItems = new util.ArrayList[String]
        if (exclusionListProperty.getValue != null && !exclusionListProperty.getValue.isEmpty) exclusionItems.addAll(exclusionListProperty.getValue)
        if(!exclusionItems.contains(selectedDir.getCanonicalPath))exclusionItems.add(selectedDir.getCanonicalPath)
        exclusionListProperty.set(FXCollections.observableArrayList(exclusionItems))
      }
      // debug and test
      else System.out.println("event = [" + event + "], selectedFile = [ File selection cancelled. ]")
    }
  }

  @jfxf.FXML
  private def radioOptionChange(event: ActionEvent): Unit = {
    System.out.println("event = [" + event + "]") // debug and test
    val btn = event.getSource.asInstanceOf[RadioButton]
    val id = btn.getId
    if (id == "customOption") {
      val fileChooser = new FileChooser
      fileChooser.setTitle("Open Resource File")
      customFile = fileChooser.showOpenDialog(null)
      if (customFile == null) defaultOption.setSelected(true)
    }
    else if (id == "defaultOption") if (customFile != null) customFile = null
  }

  @jfxf.FXML
  private def changeOnClustering(event: ActionEvent): Unit= {
    println("Called on Clustering change") // debug and test
    println(clusteringType.selectionModel().getSelectedItem)
    val clusteringValue = clusteringType.selectionModel().getSelectedItem
    if (clusteringValue == "Flat") {
      linkageBox.setVisible(false)
      noOfClusterBox.setVisible(true)
      strategyList.getItems.remove("Cosine Sim")
      strategyList.setValue("Euclidean Dist")
      IDFlist.getItems.remove("No Dictionary")
      IDFlist.setValue("Normal")
    }
    else {
      linkageBox.setVisible(true)
      noOfClusterBox.setVisible(false)
      strategyList.getItems.add("Cosine Sim")
      IDFlist.getItems.add("No Dictionary")
    }
  }


  @jfxf.FXML
  private def changeOnWeighting(event: ActionEvent): Unit= {
    if(weightingList.getValue == "Bag-Of-Words") {
      if(IDFlist.getItems.size > 2)IDFlist.getItems.removeAll("Idf","Smooth Idf")
      IDFlist.setValue("Normal")
    }
    else {
      if(IDFlist.getItems.size <= 2) IDFlist.getItems.addAll("Idf","Smooth Idf")
    }
    println("Called on Weighting change") // debug and test
  }

  @jfxf.FXML
  private def changeOnIdf(event: ActionEvent): Unit= {
    println("Called on Weighting change") // debug and test
  }

  @jfxf.FXML
  private def changeOnStrategy(event: ActionEvent): Unit= {
    println("Called on Strategy change") // debug and test
  }

  @jfxf.FXML
  private def openLog(event: ActionEvent) : Unit = {
    println("Called on Strategy change") // debug and test
    org.Fcocco01.DocumentClassifier.Essentials.Util.I_O.openFromPath("./Error_Logs")
  }

  @jfxf.FXML
  private def onStartClick(event: ActionEvent): Unit = {
    if(inclusionList.getItems == null || inclusionList.getItems.size == 0) {
      new Alert(AlertType.Error, "No directory was selected.").showAndWait
    } else {
      viewResultButton.setDisable(true)
      startButton.setDisable(true)
      stopButton.setDisable(false)
      copyWorker = createWorker
      // debug and test
      System.out.println("event = [" + event + "], inclusionListProperty [" + inclusionListProperty.getValue + "]")
      progressBar.setVisible(true)
      progressBar.progressProperty.unbind
      progressBar.progressProperty.bind(copyWorker.progressProperty)
      new Thread(copyWorker).start
    }
  }

  /* Start a different thread for the main process */
  def createWorker: Task[_] = new Task[Boolean]() {
    @throws[Exception]
    override protected def call: Boolean = {
        val inclusions = if (inclusionListProperty.getValue != null) {
          inclusionListProperty.getValue.map(_.toString).toArray
        } else Array("")
        val exclusions = if (exclusionListProperty.getValue != null) {
          exclusionListProperty.getValue.map(_.toString).toArray
        } else Array("")

        val processes: Map[String,BaseProcess] = HashMap(
          "Corpus" -> CorpusPreparation(inclusions,exclusions,if (customFile != null) Some(customFile.getAbsolutePath) else None,
            if (regexOption.getText != "") Some(regexOption.getText) else None),
          "DataSet" -> DataSetPreparation(weightingList.getValue, IDFlist.getValue),
          "Clustering" -> ClusteringProcess(clusteringType.getValue, strategyList.getValue),
          "View" -> GraphicalResult()
        )
        processes.foreach(x => x._2.setExternalHandler((observable, oldValue, newValue) =>
          updateProgress(newValue.doubleValue(), 10.0)))

        val corpus = processes("Corpus").asInstanceOf[CorpusPreparation].start
        val vectors = processes("DataSet").asInstanceOf[DataSetPreparation].start(corpus)
        val clusteringProcess = processes("Clustering").asInstanceOf[ClusteringProcess]
        val clustNum = if (noOfClusters.getText != null && noOfClusters.getText != "") noOfClusters.getText.toInt else 1
        val clusteringReady = clusteringType.getValue match {
          case "Hierarchical" => clusteringProcess.start(linkageList.getValue, 0)
          case _ => clusteringProcess.start("", clustNum)
        }
        val clusters = clusteringReady(vectors)

        currentOutput = processes("View").asInstanceOf[GraphicalResult].start(clusters)
        startButton.setDisable(false)
        stopButton.setDisable(true)
        viewResultButton.setDisable(false)
        true
    }
  }

  @jfxf.FXML
  private def onStopClick(event: ActionEvent): Unit = {
    new Alert(AlertType.Error, "Feature not yet implemented.").showAndWait
    startButton.setDisable(false)
    stopButton.setDisable(true)
//    copyWorker.cancel(true)
//    progressBar.progressProperty.unbind
//    progressBar.setProgress(0)
  }

  @jfxf.FXML
  private def onCloseButtonAction(event: ActionEvent): Unit = {
    Platform.exit()
  }

  @jfxf.FXML
  def viewResults(event: ActionEvent): Unit = {
    val window = new Stage
    window.setScene(currentOutput)
    window.sizeToScene
    window.show
  }

  def initialize(url: URL, rb: util.ResourceBundle) {
    anchor = new AnchorPane(anchorDelegate)
    inclusionList.itemsProperty.bind(inclusionListProperty)
    exclusionList.itemsProperty.bind(exclusionListProperty)
    inclusionList.setCellFactory((param: Any) => new XCell)
    exclusionList.setCellFactory((param: Any) => new XCell)

    customOption.setToggleGroup(group)
    customOption.setUserData("custom")
    defaultOption.setToggleGroup(group)
    defaultOption.setUserData("default")
    defaultOption.setSelected(true)
    regexOption.setDisable(false)

    /* Filter for number of cluster field to accept only whole numbers*/
    noOfClusters.setTextFormatter(new TextFormatter[String]((change) => {
      val text = change.getText
      if (text.matches("[0-9]*")) change
      else null
    }))
  }
}
