package fsart.diffTools.model

/**
 *
 * User: fabien
 * Date: 07/05/12
 * Time: 17:28
 *
 */

class CsvBuilder(var separator:String = ";")  {myObj=>

  var headers:List[String] = List.empty

  private var array:List[List[String]] = List.empty

  def appendLine(line:String) {
    val split:List[String] = line.split(separator).toList
    array :+= split
  }

  def appendLines(lines:List[String], firstLineAsHeader:Boolean = true) {
    if(firstLineAsHeader) {
      appendLinesWithHeaders(lines)
    }else {
      appendLinesWithoutHeaders(lines)
    }
  }
  def appendLinesWithoutHeaders(lines:List[String]) {
    for(line <- lines) {
      appendLine(line)
    }
  }
  def appendLinesWithHeaders(lines:List[String]) {
    setHeaders(lines(0))
    for(line <- lines.drop(1)) {
      appendLine(line)
    }
  }

  def setHeaders(line:String) {
    headers = line.split(separator).toList
  }


  def getCvsData(): CsvData = {
    val nbMaxCol = array.foldLeft(0) {
      (nbCol, elem) => scala.math.max(nbCol, elem.size)
    }
    val resArray = array.map {
      elem => elem ++ List.fill[String](nbMaxCol - elem.size) {
        ""
      }
    }
    new CsvData() {
      override val headers = myObj.headers
      override val array = resArray
      override val separator = myObj.separator
    }
  }
}
