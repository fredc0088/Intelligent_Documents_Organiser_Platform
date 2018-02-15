package org.Fcocco01.DocumentClassifier

import javax.swing.plaf.RootPaneUI

import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout.BorderPane
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.scene.input
import scalafx.stage.DirectoryChooser

object Gui extends JFXApp {
  stage = new JFXApp.PrimaryStage {
    title = "IDCP"
    scene = new Scene(800,600)  {
      val btnOpen = new Button("Choose directories")
      val btnExclude = new Button("Exclude directories")
      btnOpen.layoutX = 100
      btnOpen.layoutY = 300
      btnOpen.setOnAction(new )
      val dirchoose = new DirectoryChooser()
      dirchoose.title = "Select a directory"
      dirchoose.initialDirectory

      //val button = new Button("Click me")
//      button.layoutX = 100
//      button.layoutY = 100
//      val rect = Rectangle(400,200,100,150)
//      rect.fill = Color.Bisque
//      content = List(button,rect)

      val pn = new BorderPane
      root = pn
    }
  }


}
