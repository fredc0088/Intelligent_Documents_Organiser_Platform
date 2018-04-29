package org.Fcocco01.DocumentClassifier

import java.net.URL

import javafx.{fxml => jfxf, scene => jfxs}
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene

object Main extends JFXApp {

  val resource: URL = getClass.getClassLoader.getResource("Gui.fxml")

  val root: jfxs.Parent = jfxf.FXMLLoader.load(resource)

  stage = new PrimaryStage() {
    title = "Documents Clusterizer"
    scene = new Scene(root)
    resizable = false
  }

}
