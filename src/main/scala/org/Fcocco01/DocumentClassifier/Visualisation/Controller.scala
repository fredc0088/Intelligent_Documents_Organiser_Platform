//package org.Fcocco01.DocumentClassifier.Visualisation
//
//import javafx.beans.property.{ListProperty, SimpleListProperty}
//import javafx.collections.FXCollections
//import javafx.event.ActionEvent
//import javafx.scene.control._
//import javafx.scene.layout.HBox
//import javafx.scene.{control => jfxsc}
//import javafx.scene.{layout => jfxsl}
//import javafx.stage.{DirectoryChooser, FileChooser}
//import javafx.{event => jfxe}
//import javafx.{fxml => jfxf}
//import scalafx.Includes._
//import scalafx.scene.layout.GridPane
//import javafx.application.Platform
//import javafx.beans.property.ListProperty
//import javafx.beans.property.SimpleListProperty
//import javafx.beans.value.ChangeListener
//import javafx.beans.value.ObservableValue
//import javafx.collections.FXCollections
//import javafx.collections.ObservableList
//import javafx.concurrent.Task
//import javafx.event.ActionEvent
//import javafx.fxml.FXML
//import javafx.fxml.Initializable
//import javafx.scene.control._
//import javafx.scene.layout.HBox
//import javafx.stage.DirectoryChooser
//import javafx.stage.FileChooser
//import javafx.stage.Stage
//import java.io.File
//import java.net.URL
//import java.util
//import java.util.ResourceBundle
//
//
//class Controller extends Initializable {
//  @jfxf.FXML private var inclusionList: jfxsc.ListView[String] = _
//  @jfxf.FXML private var exclusionList: jfxsc.ListView[String] = _
//  private var group = new ToggleGroup
//  @jfxf.FXML private var customOption: jfxsc.RadioButton = _
//  @jfxf.FXML private var defaultOption: jfxsc.RadioButton = _
//  @jfxf.FXML private var regexOption: jfxsc.TextField = _
//  @jfxf.FXML private var clusteringType: jfxsc.ChoiceBox[String] = _
//  @jfxf.FXML private var weightingList: jfxsc.ChoiceBox[String] = _
//  @jfxf.FXML private var strategyList: jfxsc.ChoiceBox[String] = _
//  @jfxf.FXML private var independentVector: jfxsc.CheckBox  = _
//  @jfxf.FXML private var linkageBox: jfxsl.HBox = _
//  @jfxf.FXML private var linkageList: jfxsc.ChoiceBox[String] = _
//  @jfxf.FXML private var noOfClusterBox: jfxsl.HBox = _
//  @jfxf.FXML private var progressBar: jfxsc.ProgressBar = _
//  @jfxf.FXML private var startButton: jfxsc.Button = _
//  @jfxf.FXML private var stopButton: jfxsc.Button = _
//  protected var inclusionListProperty = new SimpleListProperty[String]
//  protected var exclusionListProperty = new SimpleListProperty[String]
//  protected var customFile: File = null
//  private val clustering : String = null
//  private var copyWorker : Task[_] = null
//
//  @FXML def loadDirectories(event: ActionEvent): Unit = {
//    val btn = event.getSource.asInstanceOf[Button]
//    val id = btn.getId
//    if (id == "directoryFileChooser") {
//      val directoryChooser = new DirectoryChooser
//      val selectedDirectory = directoryChooser.showDialog(null)
//      if (selectedDirectory != null) {
//        System.out.println("event = [" + event + "], selectedFile = [" + selectedDirectory.getAbsolutePath + "]")
//        val inclusionDirItems = new util.ArrayList[String]
//        if (inclusionListProperty.getValue != null && !inclusionListProperty.getValue.isEmpty) inclusionDirItems.addAll(inclusionListProperty.getValue)
//        inclusionDirItems.add(selectedDirectory.getAbsolutePath)
//        inclusionListProperty.set(FXCollections.observableArrayList(inclusionDirItems))
//      }
//      else System.out.println("event = [" + event + "], selectedFile = [No Directory selected]")
//    }
//    else if (id == "exclusionFileChooser") {
//      val fileChooser = new FileChooser
//      fileChooser.setTitle("Open Resource File")
//      val selectedFile = fileChooser.showOpenDialog(null)
//      if (selectedFile != null) {
//        System.out.println("event = [" + event + "], selectedFile = [" + selectedFile + "]")
//        val exclusionItems = new util.ArrayList[String]
//        if (exclusionListProperty.getValue != null && !exclusionListProperty.getValue.isEmpty) exclusionItems.addAll(exclusionListProperty.getValue)
//        exclusionItems.add(selectedFile.getAbsolutePath)
//        exclusionListProperty.set(FXCollections.observableArrayList(exclusionItems))
//      }
//      else System.out.println("event = [" + event + "], selectedFile = [ File selection cancelled. ]")
//    }
//  }
//
//  @FXML def radioOptionChange(event: ActionEvent): Unit = {
//    System.out.println("event = [" + event + "]")
//    val btn = event.getSource.asInstanceOf[RadioButton]
//    val id = btn.getId
//    if (id == "customOption") {
//      val fileChooser = new FileChooser
//      fileChooser.setTitle("Open Resource File")
//      customFile = fileChooser.showOpenDialog(null)
//      if (customFile == null) defaultOption.setSelected(true)
//    }
//    else if (id == "defaultOption") if (customFile != null) customFile = null
//  }
//
//  @FXML private def onStartClick(event: ActionEvent): Unit = {
//    startButton.setDisable(true)
//    stopButton.setDisable(false)
//    copyWorker = createWorker
//    System.out.println("event = [" + event + "], inclusionListProperty [" + inclusionListProperty.getValue + "]")
//    validate()
//    progressBar.progressProperty.unbind()
//    progressBar.progressProperty.bind(copyWorker.progressProperty)
//    new Thread(copyWorker).start()
//  }
//
//  private def validate(): Unit = {
//  }
//
//  @FXML private def onStopClick(event: ActionEvent): Unit = {
//    startButton.setDisable(false)
//    stopButton.setDisable(true)
//    copyWorker.cancel(true)
//    progressBar.progressProperty.unbind()
//    progressBar.setProgress(0)
//  }
//
//  def createWorker: Task[_] = new Task[_]() {
//    @throws[Exception]
//    override protected def call: Any = {
//      var i = 0
//      while ( {
//        i < 10
//      }) {
//        Thread.sleep(200)
//        updateMessage("200 milliseconds")
//        updateProgress(i + 1, 10)
//        System.out.println(progressBar.getProgress)
//
//        {
//          i += 1; i - 1
//        }
//      }
//      startButton.setDisable(false)
//      stopButton.setDisable(true)
//      true
//    }
//  }
//
//  @FXML private def onCloseButtonAction(event: ActionEvent): Unit = {
//    Platform.exit()
//  }
//
//  @FXML def viewResults(event: ActionEvent): Unit = {
//    val window = new Stage
//    val view = event.getSource.asInstanceOf[Button]
//    //results = new Main(indipendentVectors, directoriesChosen, exclusions, stopwordsPath, regex, clustering);
//    //window.setScene(results);
//    window.show()
//    //        class Main(indipendentVectors : Boolean, directoriesChosen : Array[String] = Array(""),exclusions : Array[String] = Array(""),
//    //                stopwords : String = "./src/main/resources/stop-word-list.txt", regex : String = "[^a-z0-9]",
//    //                clusteringMode : String = "", chooseFun : String = "", comparison : String = "", linkStrategy : String= "")
//  }
//
//  override def initialize(location: URL, resources: ResourceBundle): Unit = {
//    System.out.println("location = [" + location + "], resources = [" + resources + "]")
//    inclusionList.itemsProperty.bind(inclusionListProperty)
//    exclusionList.itemsProperty.bind(exclusionListProperty)
//    inclusionList.setCellFactory((param: Any) => new Nothing)
//    exclusionList.setCellFactory((param: Any) => new Nothing)
//    customOption.setToggleGroup(group)
//    customOption.setUserData("custom")
//    defaultOption.setToggleGroup(group)
//    defaultOption.setUserData("default")
//    defaultOption.setSelected(true)
//    regexOption.setDisable(false)
//    val clusteringTypeList = FXCollections.observableArrayList("Flat", "Hierarchical")
//    clusteringType.getItems.addAll(clusteringTypeList)
//    clusteringType.setValue("Hierarchical")
//    noOfClusterBox.setVisible(false)
//    clusteringType.getSelectionModel.selectedIndexProperty.addListener(new ChangeListener[Number]() {
//      override def changed(observableValue: ObservableValue[_ <: Number], number: Number, number2: Number): Unit = {
//        System.out.println(clusteringType.getItems.get(number2.asInstanceOf[Integer]))
//        val clustringValue = clusteringType.getItems.get(number2.asInstanceOf[Integer]).asInstanceOf[String]
//        if (clustringValue == "Flat") {
//          linkageBox.setVisible(false)
//          noOfClusterBox.setVisible(true)
//        }
//        else {
//          linkageBox.setVisible(true)
//          noOfClusterBox.setVisible(false)
//        }
//      }
//    })
//    val linkageTypeList = FXCollections.observableArrayList("Single Link", "Complete Link")
//    linkageList.getItems.addAll(linkageTypeList)
//    linkageList.setValue("Single Link")
//    linkageList.getSelectionModel.selectedIndexProperty.addListener(new ChangeListener[Number]() {
//      override def changed(observableValue: ObservableValue[_ <: Number], number: Number, number2: Number): Unit = {
//        System.out.println(linkageList.getItems.get(number2.asInstanceOf[Integer]))
//      }
//    })
//    val weightingTypeList = FXCollections.observableArrayList("Bag-Of-Words", "TfIdf", "WdIdf", "Wdf", "Tf", "Log Normalisation")
//    weightingList.getItems.addAll(weightingTypeList)
//    weightingList.setValue("Tf")
//    weightingList.getSelectionModel.selectedIndexProperty.addListener(new ChangeListener[Number]() {
//      override def changed(observableValue: ObservableValue[_ <: Number], number: Number, number2: Number): Unit = {
//        System.out.println(weightingList.getItems.get(number2.asInstanceOf[Integer]))
//      }
//    })
//    val strategyTypeList = FXCollections.observableArrayList("Cosine Sim", "Euclidean Dist", "Manhattan Dist")
//    strategyList.getItems.addAll(strategyTypeList)
//    strategyList.setValue("Cosine Sim")
//    strategyList.getSelectionModel.selectedIndexProperty.addListener(new ChangeListener[Number]() {
//      override def changed(observableValue: ObservableValue[_ <: Number], number: Number, number2: Number): Unit = {
//        System.out.println(strategyList.getItems.get(number2.asInstanceOf[Integer]))
//      }
//    })
//    independentVector.selectedProperty.addListener(new ChangeListener[Boolean]() {
//      override def changed(observable: ObservableValue[_ <: Boolean], oldValue: Boolean, newValue: Boolean): Unit = {
//        System.out.println("observable = [" + observable + "], oldValue = [" + oldValue + "], newValue = [" + newValue + "]")
//        //independentVector.setSelected(!newValue);
//        if (newValue) {
//          weightingList.getItems.remove("TfIdf")
//          weightingList.getItems.remove("WdIdf")
//        }
//        else weightingList.getItems.addAll("TfIdf", "WdIdf")
//      }
//    })
//    stopButton.setDisable(true)
//  }
//}
