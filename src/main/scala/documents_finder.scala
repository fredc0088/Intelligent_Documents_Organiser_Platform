import java.io.File
import collection.mutable

 object DocumentsFinder {

   def recursiveTreeDocsSearch(dir: String): List[File] = {
     val files = new File(dir).listFiles().toList
     val list = mutable.ArrayBuffer[File]()
     files.filter(x => x.isDirectory).foreach(j => list ++= recursiveTreeDocsSearch(j.getAbsolutePath))
     list.toList ++ files.filter(x => x.isFile).filter(
       f => (f.getName endsWith ".docx") || (f.getName endsWith ".doc") || (f.getName endsWith ".txt")
     )
   }

   def getPaths(docs: List[File]) = docs.flatMap(d => d.getAbsolutePath)

   def main(args: Array[String]): Unit = {
     val docs = recursiveTreeDocsSearch("/Users/Fred88/odrive/Google Drive (2)")
     print(getPaths(docs))
   }
}

