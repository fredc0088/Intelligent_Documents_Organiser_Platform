package fxmlexample

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.stage

import java.io.IOException
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.layout.Pane
import scalafxml.core.{FXMLView,NoDependencyResolver}
import scalafxml.core.macros.{sfxml, sfxmlMacro}
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
  if (resource == null) {
    throw new IOException("Cannot load resource")
  }

  val root = FXMLView(resource, NoDependencyResolver)

  val p = new PrimaryStage() {
    Scene(root)
  }
  val open = new DirectoryChooser(p, "Open directories")
  val exclude = new DirectoryChooser(p, "Exclude directories")


  p.show()

}

import java.io.File


class DirectoryChooser(stage: PrimaryStage, descr: String) {
  val dirChooser = new scalafx.stage.DirectoryChooser()
  dirChooser.setTitle(descr)
  dirChooser.initialDirectory = new File(System.getProperty("user.home"))
  val txtArea = new scalafx.scene.control.TextArea()
  txtArea.minHeight = 70


}