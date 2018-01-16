import java.io.File

import scala.io.Source
import scala.util.{Try, Success, Failure}


object analysis {

  type Tokenizer = (String => Array[String])

  def readDocWithTry(file: String) = {
    Try {
      val lines = using(io.Source.fromFile(file)) {
        source =>
          (for (line <- source.getLines) yield line).toList
      }
      lines
    }

    def DocContent(filePath: String) =

    case class TokenizerForAnalysis(doc: String, regex: String) extends Tokenizer {

    }
  }