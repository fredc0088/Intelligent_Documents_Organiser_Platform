package org.Fcocco01.DocumentClassifier.Visualisation

import java.io.File

import javafx.fxml.{FXML, FXMLLoader}
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.event.ActionEvent
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafxml.core.{FXMLView, NoDependencyResolver}


object Gui extends JFXApp {


  val resource = FXMLLoader.load(
    this.getClass.getClassLoader.getResource("Gui.fxml"))
  if (resource == null) {
    println("Cannot load resource")
  }

  val root = FXMLView(resource, NoDependencyResolver)

  @FXML
  var getDirs : Button = _
  val p = new PrimaryStage {
    scene = Scene(root)
  }
  var dirs : List[File] = _
  val open = new DirectoryChooser("Open directories")
  val exclude = new DirectoryChooser("Exclude directories")
  getDirs.setOnAction( (event : ActionEvent)  => {
    val dir : File = open.dirChooser.showDialog(p)
    if(dir != null) dirs = dirs :+ dir
    }
  )


  p.show()

  class DirectoryChooser(descr: String) {
    val dirChooser = new scalafx.stage.DirectoryChooser()
    dirChooser.setTitle(descr)
    dirChooser.initialDirectory = new File(System.getProperty("user.home"))
    val txtArea = new scalafx.scene.control.TextArea()
    txtArea.minHeight = 70
    //  val stage = new PrimaryStage
    //  val scene = new Scene(dirChooser)
    //  scene()


  }

}
