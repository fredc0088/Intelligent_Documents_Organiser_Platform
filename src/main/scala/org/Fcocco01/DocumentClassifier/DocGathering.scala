package org.Fcocco01.DocumentClassifier

import java.io.File
import collection.mutable

 object DocGathering {

   type DocumentsPaths = () => List[String]

   class DocumentFinder(directories: Array[String],directoriesToExclude: Array[String]) extends DocumentsPaths{

     def recursiveTreeDocsSearch(dir: String): List[File] = {
       try {
         val files = new File(dir).listFiles().toList
         val list = mutable.ArrayBuffer[File]()
         files.filter { x => x.isDirectory }.foreach { j => list ++= recursiveTreeDocsSearch(j.getAbsolutePath) }
         list.toList ++ files.filter { x => x.isFile }.filter {
           f => (f.getName endsWith ".docx") || (f.getName endsWith ".doc") || (f.getName endsWith ".txt")
         }
       }
       catch {
         case e : NullPointerException => List[File]()
       }
     }

     private def getPaths(docs: List[File]) = docs.map(_.getAbsolutePath)

     def apply() = {
        val documents = (for {
          d <- directories
        } yield this.recursiveTreeDocsSearch(d)).flatten.toList
       this.getPaths(documents).distinct
     }
   }
   object DocumentFinder {
    def apply(directories: Array[String],directoriesToExclude: Array[String] = Array()): List[String] = {
      val instance = new DocumentFinder(directories, directoriesToExclude)
      instance()
    }
   }
}

