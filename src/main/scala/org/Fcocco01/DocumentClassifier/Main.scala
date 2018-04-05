import javafx.fxml.FXMLLoader
import javafx.stage.StageStyle
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafxml.core.{FXMLView, NoDependencyResolver}

object Main extends JFXApp {
  val primaryStage = new PrimaryStage
  val resource = FXMLLoader.load(
    this.getClass.getClassLoader.getResource("Gui.fxml"))
  if (resource == null) {
    println("Cannot load resource")
  }

  val root = FXMLView(resource, NoDependencyResolver)
//  val scene = new Scene(root, 500, 500)
//  primaryStage.setScene(scene)
  primaryStage.title = "Document Clusterizer"
  primaryStage.initStyle(StageStyle.DECORATED)
  primaryStage.show()
  class MainProcess(indipendentVectors : Boolean, directoriesChosen : Array[String] = Array(""),exclusions : Array[String] = Array(""),
                    stopwords : String = "./src/main/resources/stop-word-list.txt", regex : String = "[^a-z0-9]",
                    clusteringMode : String = "", chooseFun : String = "", comparison : String = "", linkStrategy : String= "") {
    def getGraphic() = {

      var progress = 0.0


      progress = 0.1


      progress = 0.4

      //    val vectorFun = chooseFun match {
      //      case "TfIdf" if (!indipendentVectors) => {}
      //      case "WfIdf" if (!indipendentVectors) => {}
      //      case "Tf" => {}
      //      case "wdf" => {}
      //      case "Log Normalisation" => {}
      //      case "Bag-Of-Words" => {}
      //    }


      progress = 0.7


    }
  }

}
