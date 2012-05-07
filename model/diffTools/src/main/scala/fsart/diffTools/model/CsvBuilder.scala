package fsart.diffTools.model

import fsart.diffTools.model.CsvData

/**
 *
 * User: fabien
 * Date: 07/05/12
 * Time: 17:28
 *
 */

class CsvBuilder(var separator:String = ";")  {

  var headers:List[String] = List.empty

  private var array:List[List[String]] = List.empty

  def appendLine(line:String) {
    val split:List[String] = line.split(separator).toList
    array :+= split
  }

  def appendLines(list:List[String]) {
    for(line <- list) {
      appendLine(line)
    }
  }

  def getCvsData(csvData: CsvData) = {
    new CsvData() {
      override val headers = this.headers
      override val array = this.array
      override val separator = this.separator
    }
  }
}
