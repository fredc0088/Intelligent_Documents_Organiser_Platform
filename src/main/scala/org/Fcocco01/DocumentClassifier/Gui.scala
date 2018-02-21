package org.Fcocco01.DocumentClassifier

import javax.swing.plaf.RootPaneUI

import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout.BorderPane
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.scene.input
import scalafx.event.{Event,EventHandler,ActionEvent}
import scalafx.stage.DirectoryChooser


object Gui extends JFXApp {
  stage = new JFXApp.PrimaryStage {
    title = "IDCP"
    scene = new Scene(800,600)  {
      val btnOpen = new Button("Choose directories")
      val btnExclude = new Button("Exclude directories")
      btnOpen.layoutX = 100
      btnOpen.layoutY = 300

      val dirchoose = new DirectoryChooser()
      dirchoose.title = "Select a directory"
      dirchoose.initialDirectory

//      btnOpen.setOnAction(EventHandler[ActionEvent] {
//        def handle(Event: ActionEvent) : Unit = {
//          val dir = dirchoose.showDialog(this.getWindow)
//          //if(dir != null)
//        }
//      } )


//      btnOpen.onAction = (event: ActionEvent) => {
//
//
//      }


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
//
//  class DirectoryChooserDemo extends Application {
//    @throws[Exception]
//    override def start(primaryStage: Stage): Unit = {
//      val directoryChooser = new DirectoryChooser
//      configuringDirectoryChooser(directoryChooser)
//      val textArea = new TextArea
//      textArea.setMinHeight(70)
//      val button = new Button("Open DirectoryChooser and select a directory")
//      button.setOnAction(new EventHandler[ActionEvent]() {
//        override def handle(event: ActionEvent): Unit = {
//          val dir = directoryChooser.showDialog(primaryStage)
//          if (dir != null) textArea.setText(dir.getAbsolutePath)
//          else textArea.setText(null)
//        }
//      })
//      val root = new VBox
//      root.setPadding(new Insets(10))
//      root.setSpacing(5)
//      root.getChildren.addAll(textArea, button)
//      val scene = new Scene(root, 400, 200)
//      primaryStage.setTitle("JavaFX DirectoryChooser (o7planning.org)")
//      primaryStage.setScene(scene)
//      primaryStage.show()
//    }
//
//    private def configuringDirectoryChooser(directoryChooser: DirectoryChooser) = { // Set title for DirectoryChooser
//      directoryChooser.setTitle("Select Some Directories")
//      // Set Initial Directory
//      directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")))
//    }
//  }

}
