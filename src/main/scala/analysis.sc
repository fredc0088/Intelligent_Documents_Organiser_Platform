
object analysis {

  import Util.Types._
  import Util.I_O.{GetDocContent,readDocWithTry}
  import Util.Operators.|>

  def ExtractDocContent(docPath: String): String = {
    GetDocContent(docPath)
  }

  class TokenizedText(regex: String, stopWordsFilePath: String) extends Tokenizer {
    val stopwords = GetDocContent(stopWordsFilePath)
    def apply(text: String) = text.toLowerCase.split(regex).filter(!stopwords.contains(_)).toVector
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

  class DocumentVector(dictionary: TextModel, private val tokenizer: Tokenizer, docPath: String) {
    val completeText = ExtractDocContent(docPath)
    val tokens = tokenizer(completeText)
    val size = tokens.size
    val docId= docPath
  }
  object DocumentVector {
    def apply(dictionary: TextModel, tokenizer: Tokenizer, docPath: String): DocumentVector =
      new DocumentVector(dictionary, tokenizer, docPath)
  }



  class BagOfWordsOneDocument[A](docs: A, tokenizer: Tokenizer ) extends TF[Word] {
    val text = tokenizer(ExtractDocContent(docs.toString))
    def apply() = (for {
      w <- text

    } yield this.tf(w, text)).distinct
  }



  class BagOfWordsDictionary[Paths](docsPathsList: Paths) extends TextModel
  object BagOfWordsDictionary {
    def apply(d: Paths, tokenizer: Tokenizer) = {
      d.flatMap(x => tokenizer(ExtractDocContent(x))).toVector.distinct
    }
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