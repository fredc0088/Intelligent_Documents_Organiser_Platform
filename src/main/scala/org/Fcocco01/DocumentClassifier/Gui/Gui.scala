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
import scalafx.embed.swing.SwingNode

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
    new Scene(root)
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




//object Gui extends JFXApp {
//  stage = new JFXApp.PrimaryStage {
//    title = "IDCP"
//    scene = new Scene(800,600)  {
//      val btnOpen = new Button("Choose directories")
//      val btnExclude = new Button("Exclude directories")
//      btnOpen.layoutX = 100
//      btnOpen.layoutY = 300
//
//      val dirchoose = new DirectoryChooser()
//      dirchoose.title = "Select a directory"
//      dirchoose.initialDirectory
//
//      //      btnOpen.setOnAction(EventHandler[ActionEvent] {
//      //        def handle(Event: ActionEvent) : Unit = {
//      //          val dir = dirchoose.showDialog(this.getWindow)
//      //          //if(dir != null)
//      //        }
//      //      } )
//
//
//      //      btnOpen.onAction = (event: ActionEvent) => {
//      //
//      //
//      //      }
//
//
//      //val button = new Button("Click me")
//      //      button.layoutX = 100
//      //      button.layoutY = 100
//      //      val rect = Rectangle(400,200,100,150)
//      //      rect.fill = Color.Bisque
//      //      content = List(button,rect)
//
//      val pn = new BorderPane
//      root = pn
//    }
//  }
//  //
//  //  class DirectoryChooserDemo extends Application {
//  //    @throws[Exception]
//  //    override def start(primaryStage: Stage): Unit = {
//  //      val directoryChooser = new DirectoryChooser
//  //      configuringDirectoryChooser(directoryChooser)
//  //      val textArea = new TextArea
//  //      textArea.setMinHeight(70)
//  //      val button = new Button("Open DirectoryChooser and select a directory")
//  //      button.setOnAction(new EventHandler[ActionEvent]() {
//  //        override def handle(event: ActionEvent): Unit = {
//  //          val dir = directoryChooser.showDialog(primaryStage)
//  //          if (dir != null) textArea.setText(dir.getAbsolutePath)
//  //          else textArea.setText(null)
//  //        }
//  //      })
//  //      val root = new VBox
//  //      root.setPadding(new Insets(10))
//  //      root.setSpacing(5)
//  //      root.getChildren.addAll(textArea, button)
//  //      val scene = new Scene(root, 400, 200)
//  //      primaryStage.setTitle("JavaFX DirectoryChooser (o7planning.org)")
//  //      primaryStage.setScene(scene)
//  //      primaryStage.show()
//  //    }
//  //
//  //    private def configuringDirectoryChooser(directoryChooser: DirectoryChooser) = { // Set title for DirectoryChooser
//  //      directoryChooser.setTitle("Select Some Directories")
//  //      // Set Initial Directory
//  //      directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")))
//  //    }
//  //  }
//
//}