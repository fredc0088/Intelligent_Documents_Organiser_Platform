package object Util {

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

  object Types {
    type Tokenizer = (String => Array[String])
  }


}
