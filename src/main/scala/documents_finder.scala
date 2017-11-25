import java.io.File
 object DocumentsFinder {

   def getListOfFiles(dir: String){
     var d = new File(dir)
     if(d.exists && d.isDirectory) {
       d.listFiles
      .filter(f =>
            f.isFile &&
            f.getName.endsWith(".docx") || 
            f.getName.endsWith(".doc") || 
            f.getName.endsWith(".txt")
            ).
      .toList //  .map(_.getAbsolutePath)
      // d.listFiles
      // .filter(_.isDirectory)
      // .map(_.getPath)
      // .flatMap(getListOfFiles(_, l)).toList ++ l
      // l
     }
    else
      List[File]()
  }


  def recursiveTreeDocsSearch(dir: String /**, l: List[File]*/) : List[File] = {
    var l = List[File]()
    l ++ new File(dir).listFiles.forall(
      _.isDirectory match {
        case true => recursiveTreeDocsSearch(_.getAbsolutePath) :: l
        case false => _.isFile match {
          case true => getListOfFiles(_.getAbsolutePath)
        }
      }
    )
    l
  }

   def main(args: Array[String]): Unit = {
    print(recursiveTreeDocsSearch("C:/Users/USER/odrive/Google Drive (2)"/**, List[File]()*/))
   }
}

