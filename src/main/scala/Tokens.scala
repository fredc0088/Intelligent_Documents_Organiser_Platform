class Tokenenizer(regex: String, stopWordsFilePath: String) extends Util.Types.Tokenizer {
  val stopwords = Util.I_O.GetDocContent(stopWordsFilePath)
  def apply(text: String) = text.toLowerCase.split(regex)
    .filter(!stopwords.contains(_)).toVector
}
