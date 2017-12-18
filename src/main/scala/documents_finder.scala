import java.io.File

import scala.language.postfixOps

 object DocumentsFinder {

  def recursiveTreeDocsSearch(dir: String /**, l: List[File]*/) : List[File] = {
    val these = new File(dir)
    List[File]() ++ these.listFiles.map(
      x => x match {
          case _ if x.isDirectory => recursiveTreeDocsSearch(x.getAbsolutePath)
          case _ if x.isFile && x.getName.endsWith(".docx") || x.getName.endsWith(".doc") || x.getName.endsWith(".txt") => x
          case _ => Array[File]()
        }
    )
  }

   def main(args: Array[String]): Unit = {
    print(recursiveTreeDocsSearch("C:/Users/USER/odrive/Google Drive (2)"/**, List[File]()*/))
   }
}

