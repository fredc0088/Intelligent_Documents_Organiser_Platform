

object analysis {

  import Util.Types._
  import Util.I_O.{GetDocContent,readDocWithTry}
  import Util.Operators.|>

  def ExtractDocContent(docPath: String): String = {
    GetDocContent(docPath)
  }

  class TokenizedText(regex: String, stopWordsFilePath: String) extends Tokenizer {
    val stopwords = GetDocContent(stopWordsFilePath)
    def apply(s: String) = s.toLowerCase.split(regex).filter(!stopwords.contains(_)).toVector
  }

  trait TF[A] {

    def tf(term: String, document: Vector[A])= {
      val frequency = () => {
        var f = 0
        for (w <- document if term == w) {
          f += 1
        }
        f
      }
      (term, frequency() / document.length)
    }




  }


  class BagOfWordsOneDocument[A](docs: A, tokenizer: Tokenizer ) extends TF[Word] {
    val text = tokenizer(ExtractDocContent(docs.toString))
    def apply() = (for {
      w <- text

    } yield this.tf(w, text)).distinct
  }

  def main(args: Array[String]): Unit = {
    val testPath = "C:/Users/USER/odrive/Google Drive (2)/Various/Daily Exercises.docx"
    val bOW = new BagOfWordsOneDocument[String](testPath,
      new TokenizedText(raw"""b[a-zA-Z]w+""", "./../resources/stop-word-list.txt"))
    println(bOW.toString)
  }

  def idf(docs: List[String]) {

    Math.log(1.2)
  }
}