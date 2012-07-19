package fsart.diffTools.outputDriver

import fsart.diffTools.csvModel.CsvData
import org.apache.poi.ss.usermodel.RichTextString

/**
 *
 * User: fabien
 * Date: 19/07/12
 * Time: 10:49
 *
 */

trait CsvView2 {

  def getData():Array[Byte]

  def addCsvTable(tableName:String, csv: CsvData[List[FormattedText]])

}
