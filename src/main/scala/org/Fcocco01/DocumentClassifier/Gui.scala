package org.Fcocco01.DocumentClassifier

import java.util
import javax.swing.plaf.RootPaneUI

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.control.{Button, TextArea}
import scalafx.scene.layout.BorderPane
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.scene.input
import scalafx.stage.{DirectoryChooser, Stage}
import java.awt.Desktop
import scalafx.Includes._
import scalafx.event._


object Gui extends JFXApp {
  var a: util.ArrayList[String] = new util.ArrayList[String]()
  //val desktop = Desktop



  stage = new JFXApp.PrimaryStage {
    title = "IDCP"
    scene = new Scene(800,600)  {
      val btnOpen = new Button("Choose directories")
      val btnExclude = new Button("Exclude directories")
      btnOpen.layoutX = 100
      btnOpen.layoutY = 300
      //btnOpen.setOnAction(new )

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

  def selectDirs(mainStage: Stage) = {
    val dirchoose = new DirectoryChooser()
    dirchoose.title = "Select a directory"
    dirchoose.setInitialDirectory(new java.io.File(System.getProperty("user.home")))
    val txtarea = new TextArea()
    txtarea.setMinHeight(50)
    val btn = new Button("Open")
    //btn.setOnAction(new scala)
  }



}
