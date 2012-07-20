package fsart.diffTools.script

import fsart.diffTools.translate.Translator
import java.io.FileOutputStream
import fsart.diffTools.csvModel.CsvData
import fsart.diffTools.outputDriver.{FormattedText, CsvView2}
import name.fraser.neil.plaintext.diff_match_patch.Diff

/**
 *
 * User: fabien
 * Date: 20/07/12
 * Time: 14:50
 *
 */

class InputOutputData(val data1:List[List[String]],
                      val data2:List[List[String]],
                      val translator:Translator,
                      val outputDriver: CsvView2,
                      val out: FileOutputStream) {



  def addTable(tableName:String, data: CsvData[List[Diff]]) {
    val myres = translator.translate(data)
    outputDriver.addCsvTable(tableName, myres)
  }

  def endScript() {
    val excelData = outputDriver.getData()
    out.write(excelData)
  }

}
