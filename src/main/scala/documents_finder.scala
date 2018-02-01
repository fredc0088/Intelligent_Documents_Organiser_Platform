package org.Fcocco01.DocumentClassifier

import java.io.File
import collection.mutable

 object DocumentsFinder {

   class DocumentFinder(directories: Array[String],directoriesToExclude: Array[String]) {

     def recursiveTreeDocsSearch(dir: String): List[File] = {
       val files = new File(dir).listFiles().toList
       val list = mutable.ArrayBuffer[File]()
       files.filter{ x => x.isDirectory }.foreach{ j => list ++= recursiveTreeDocsSearch(j.getAbsolutePath)}
       list.toList ++ files.filter {x => x.isFile } .filter {
         f => (f.getName endsWith ".docx") || (f.getName endsWith ".doc") || (f.getName endsWith ".txt")
       }
     }

     def getPaths(docs: List[File]) = docs.map(_.getAbsolutePath)

     def apply() = {
        val documents = (for {
          d <- directories
        } yield this.recursiveTreeDocsSearch(d)).flatten.toList
       this.getPaths(documents)
     }
   }
   object DocumentFinder {
     def apply(directories: Array[String],directoriesToExclude: Array[String]): DocumentFinder =
       new DocumentFinder(directories,directoriesToExclude)
   }



   def main(args: Array[String]): Unit = {
     val docs = recursiveTreeDocsSearch("C:\\Users\\USER\\odrive\\Google Drive (2)")
     getPaths(docs)
   }
}

