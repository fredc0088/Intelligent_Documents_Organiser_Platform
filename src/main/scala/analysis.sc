
import scala.util.{Failure, Success, Try}


object analysis {

  import Util.Control.using
  import Util.Types._

  def readDocWithTry(file: String) = {
    Try {
      val lines = using(scala.io.Source.fromFile(file)) {
        source => (for (line <- source.getLines) yield line).toList
      }
      lines.mkString
    }
  }

  def DocContent(filePath: String) = {
    val content = readDocWithTry(filePath)
    content match {
      case Success(lines) => lines.mkString
      case Failure(_) => println(s"Error")
    }

  }

  case class TokenizerForAnalysis(doc: String, regex: String) extends Tokenizer {
    //def apply(s: String) = s.toLowerCase.split(regex).filter( !stopwords.contains(_))
  }

  class Analyser {
    def tf(): Unit = {

    }
  }

}