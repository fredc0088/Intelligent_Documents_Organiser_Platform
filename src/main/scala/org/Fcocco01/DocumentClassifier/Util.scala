
import scala.util.{Failure, Success, Try}
import scala.language.reflectiveCalls

package object Util {

  import scala.io.Codec


  /**
    * Object's name: Control
    * Note: From the book, Beginning Scala, by David Pollak.
    * This makes use of the Loan Pattern ensuring the closing of
    * a resource after its use.
    */
  object Control {

    def using[A <: {def close() : Unit}, B](param: A)(f: A => B): B =
      try {
        f(param)
      } finally {
        param.close()
      }
  }

  object I_O {

    import org.apache.poi.xwpf.usermodel.{XWPFDocument, XWPFParagraph}


    def readDocWithTry(file: String) = {
      Try {
        val lines = Control.using(io.Source.fromFile(file, Codec.ISO8859.name)) {
          source => (for (
            line <- source.getLines
          ) yield line).toList
        }
        lines
      }
    }

    def readDoc(file: String) : String = {
      val f = scala.io.Source.fromFile(file,Codec.ISO8859.name)
      try {
        var content : List[String]= List()
        for (line <- f.getLines())
          content = content :+ line
        f.close()
        content.mkString(" ")
      } catch {
        case e : Exception => e.getMessage
      }
      finally {
        f.close()
      }
    }

    def GetDocContent(filePath: String) : String = {
      readDocWithTry(filePath) match {
        case Success(lines) => lines.mkString(" ")
        case Failure(t) => throw new Exception(t) { println(t.getStackTrace) }
      }
    }
  }


  object Operators {
    implicit class |>[A](val a: A) extends AnyVal {
      def |>[B](op: A => B) : B = op(a)
    }
  }


}
