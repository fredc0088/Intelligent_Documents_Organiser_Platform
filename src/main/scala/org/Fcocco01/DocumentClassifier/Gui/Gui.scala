package fxmlexample

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.fxml.FXML
import javafx.stage

import java.io.IOException
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.layout.Pane
import scalafxml.core.{FXMLView,NoDependencyResolver}
import scalafx.event.ActionEvent

import scalafx.scene.text.Text


class FXMLExampleController extends JFXApp{//Application{

//  @FXML
//  override def start(primaryStage: stage.Stage): Unit = {
//    primaryStage.setTitle("Intelligent Document Classificator Platform")
//    val myPane = FXMLLoader.load(getClass.getResource("Gui.fxml")).asInstanceOf[Pane]
//    val myScene = Scene(myPane)
//
//
//    primaryStage.setScene(myScene)
//    primaryStage.show
//  }

//  primaryStage.setTitle("Intelligent Document Classificator Platform")
//  val myPane = FXMLLoader.load(getClass.getResource("Gui.fxml")).asInstanceOf[Pane]
//  val myScene = Scene(myPane)

  val resource = FXMLLoader.load(getClass.getResource("Gui.fxml"))
  val root = FXMLView(resource, NoDependencyResolver)
  new PrimaryStage() { Scene(root)}

}
