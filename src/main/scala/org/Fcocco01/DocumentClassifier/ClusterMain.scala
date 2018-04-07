package org.Fcocco01.DocumentClassifier

import javafx.{fxml => jfxf, scene => jfxs}
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene

object ClusterMain extends JFXApp {

  val resource = getClass.getClassLoader.getResource("Gui.fxml")


  val root: jfxs.Parent = jfxf.FXMLLoader.load(resource)

  stage = new PrimaryStage() {
    title = "Document Clusterizer"
    scene = new Scene(root)
  }

}
