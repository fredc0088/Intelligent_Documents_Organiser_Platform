package org.Fcocco01.DocumentClassifier.Test

import org.Fcocco01.DocumentClassifier._
import Core.Tokenization.StopWords
import Utils.Types.TypeClasses.Vectors.DVector

object TestingResources {

  object Regexes {
    val words1gram = "[^a-z0-9]"
  }
  object Paths {
    val testPath1 =
      "./src/test/resources/1/1.2/Java - Generics by Oracle.docx"
    val testPath2 =
      "./src/test/resources/1/1.1/demo.docx"
    val testPath3 =
      "./src/test/resources/3/3.2/test.docx"
    val testPath4 =
      "./src/test/resources/3/3.1/SampleDOCFile_100kb.doc"
    val testPath5 =
      "./src/test/resources/3/3.1/3.1.1/TestWordDoc.doc"
    val testPath6 =
      "./src/test/resources/1/Dfr.doc"
    val testPath7 =
      "./src/test/resources/2/a.docx"
    val testPath8 =
      "./src/test/resources/4/1.txt"
    val testPath9 =
      "./src/test/resources/4/2.txt"
    val testPath10 =
      "./src/test/resources/4/3.txt"
    val testDirPath1 = "./src/test/resources/1/1.1"
    val testDirPath2 = "./src/test/resources/1/1.2"
    val testDirPath3 = "./src/test/resources/3"
    val testDirPath4 = "./src/test/resources/3/3.1"
    val testDirPath5 = "./src/test/resources/4"
  }

  val stopWords = StopWords("./src/main/resources/stop-word-list.txt")

  object DummyObject {
    val vector1 = DVector("test.docx",Array(("embedded", 0.0),("plugin", 0.0),
      ("test", 0.69897000433601885749368420874816365540027618408203125),("fonts", 0.0),("knows", 0.0),("essential", 0.0),
      ("demonstrates", 0.0),("line", 0.0),("document", 0.0),("compiler", 0.0),("data", 0.0),("inline", 0.0),("programmer", 0.0),
      ("demonstrate", 0.0),("text", 0.0),("particular", 0.0),("guarantee", 0.0),("safe", 0.0),("iterator", 0.0),("object", 0.0),
      ("ensure", 0.0),("returned", 0.0),("various", 0.0),("cast", 0.0),("slightly", 0.0),("annoying", 0.0),("variable", 0.0),
      ("ability", 0.0),("placed", 0.0),("calibre", 0.0),("integer", 0.0),("docx", 0.0),("kind", 0.0),("type", 0.0),("typically", 0.0),
      ("required", 0.0),("assignment", 0.0),("formatting", 0.0),("types", 0.0),("input", 0.0),("list", 0.0)).toMap)

    val vector2 = DVector("demo3.docx", Array(("embedded", 0.0),("plugin", 0.0),("test", 0.0),("fonts", 0.0),("knows", 0.0),
      ("essential", 0.0),("demonstrates", 0.1590404182398874599613236568984575569629669189453125),("line", 0.0),
      ("document", 0.1590404182398874599613236568984575569629669189453125),("compiler", 0.0),("data", 0.0),("inline", 0.0),
      ("programmer", 0.0),("demonstrate", 0.0),("text", 0.0),("particular", 0.0),("guarantee", 0.0),("safe", 0.0),("iterator", 0.0),
      ("object", 0.0),("ensure", 0.0),("returned", 0.0),("various", 0.0),("cast", 0.0),("slightly", 0.0),("annoying", 0.0),
      ("variable", 0.0),("ability", 0.1590404182398874599613236568984575569629669189453125),("placed", 0.0),("calibre", 0.0),
      ("integer", 0.0),("docx", 0.0),("kind", 0.0),("type", 0.0),("typically", 0.0),("required", 0.0),("assignment", 0.0),
      ("formatting", 0.0),("types", 0.0),("input", 0.0),("list", 0.0)).toMap)

    val vector3 = DVector("demo4.docx", Array(("embedded", 0.04659800028906792290772642672891379334032535552978515625),
      ("plugin", 0.04659800028906792290772642672891379334032535552978515625), ("test", 0.0),
      ("fonts", 0.04659800028906792290772642672891379334032535552978515625), ("knows", 0.0), ("essential", 0.0),
      ("demonstrates", 0.0318080836479774919922647313796915113925933837890625), ("line", 0.0),
      ("document", 0.0318080836479774919922647313796915113925933837890625), ("compiler", 0.0), ("data", 0.0),
      ("inline", 0.04659800028906792290772642672891379334032535552978515625), ("programmer", 0.0),
      ("demonstrate", 0.0318080836479774919922647313796915113925933837890625),
      ("text", 0.04659800028906792290772642672891379334032535552978515625), ("particular", 0.0), ("guarantee", 0.0),
      ("safe", 0.0), ("iterator", 0.0), ("object", 0.0), ("ensure", 0.0), ("returned", 0.0),
      ("various", 0.04659800028906792290772642672891379334032535552978515625), ("cast", 0.0), ("slightly", 0.0),
      ("annoying", 0.0), ("variable", 0.0), ("ability", 0.0318080836479774919922647313796915113925933837890625),
      ("placed", 0.0), ("calibre", 0.04659800028906792290772642672891379334032535552978515625), ("integer", 0.0),
      ("docx", 0.04659800028906792290772642672891379334032535552978515625), ("kind", 0.0), ("type", 0.0), ("typically", 0.0),
      ("required", 0.0), ("assignment", 0.0), ("formatting", 0.04659800028906792290772642672891379334032535552978515625),
      ("types", 0.04659800028906792290772642672891379334032535552978515625),
      ("input", 0.04659800028906792290772642672891379334032535552978515625), ("list", 0.0)).toMap)

    val vector4 = DVector("demo5.doc", Array(("embedded", 0.0), ("plugin", 0.0), ("test", 0.0), ("fonts", 0.0),
      ("knows", 0.025887777938371069053058448616866371594369411468505859375),
      ("essential", 0.025887777938371069053058448616866371594369411468505859375), ("demonstrates", 0.0),
      ("line", 0.0176711575822097177734804063220508396625518798828125), ("document", 0.0),
      ("compiler", 0.025887777938371069053058448616866371594369411468505859375),
      ("data", 0.025887777938371069053058448616866371594369411468505859375), ("inline", 0.0),
      ("programmer", 0.025887777938371069053058448616866371594369411468505859375), ("demonstrate", 0.0), ("text", 0.0),
      ("particular", 0.025887777938371069053058448616866371594369411468505859375),
      ("guarantee", 0.025887777938371069053058448616866371594369411468505859375),
      ("safe", 0.025887777938371069053058448616866371594369411468505859375),
      ("iterator", 0.025887777938371069053058448616866371594369411468505859375),
      ("object", 0.025887777938371069053058448616866371594369411468505859375),
      ("ensure", 0.025887777938371069053058448616866371594369411468505859375),
      ("returned", 0.025887777938371069053058448616866371594369411468505859375), ("various", 0.0),
      ("cast", 0.05177555587674213810611689723373274318873882293701171875),
      ("slightly", 0.025887777938371069053058448616866371594369411468505859375),
      ("annoying", 0.025887777938371069053058448616866371594369411468505859375),
      ("variable", 0.025887777938371069053058448616866371594369411468505859375), ("ability", 0.0),
      ("placed", 0.025887777938371069053058448616866371594369411468505859375), ("calibre", 0.0),
      ("integer", 0.025887777938371069053058448616866371594369411468505859375), ("docx", 0.0),
      ("kind", 0.025887777938371069053058448616866371594369411468505859375),
      ("type", 0.035342315164419435546960812644101679325103759765625),
      ("typically", 0.025887777938371069053058448616866371594369411468505859375),
      ("required", 0.025887777938371069053058448616866371594369411468505859375),
      ("assignment", 0.025887777938371069053058448616866371594369411468505859375), ("formatting", 0.0), ("types", 0.0),
      ("input", 0.0), ("list", 0.025887777938371069053058448616866371594369411468505859375)).toMap)

    val vector5 = DVector("demo5.doc", Array(("embedded", 0.24), ("plugin", 0.0), ("test", 0.0),
      ("fonts", 0.0), ("knows", 0.025887777938371), ("essential", 0.02589411468505859375),
      ("demonstrates", 0.0), ("line", 0.017671157588828125), ("document", 0.0),
      ("compiler", 0.369411468505859375), ("data", 0.4258877768505859375), ("inline", 0.0),
      ("programmer", 0.0258877778505859375), ("demonstrate", 0.0), ("text", 0.2352152),
      ("particular", 0.0263715943695)).toMap)

    val vector6 = DVector("demo5.doc", Array(("embedded", 0.5), ("plugin", 0.0), ("test", 0.0),
      ("fonts", 0.0), ("essential", 0.9), ("demonstrates", 0.0), ("demonstrate", 0.0),
      ("line", 0.0176712), ("compiler", 0.02525526), ("data", 0.02588252),
      ("inline", 0.6), ("document", 0.0), ("programmer", 0.235),  ("text", 0.53253),
      ("particular", 0.243566),("knows", 0.0356546565)).toMap)
  }
}
