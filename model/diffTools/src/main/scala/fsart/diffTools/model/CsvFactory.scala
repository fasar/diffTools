package fsart.diffTools.model

import java.net.URL
import java.io.File
import io.Source
import fsart.helper.Loader

/**
 *
 * User: fabien
 * Date: 04/05/12
 * Time: 09:34
 *
 */

object CsvFactory {

  def getCsvFile(file:String, firstLineAsHeader:Boolean):CsvFile = {
    //val file1URL = Loader.getFile(file)
    val bufSrc:Source = Source.fromString(file)
    getCsvFile(bufSrc, firstLineAsHeader)
  }
  def getCsvFile(file:URL, firstLineAsHeader:Boolean):CsvFile = {
    val bufSrc:Source = Source.fromURL(file)
    getCsvFile(bufSrc, firstLineAsHeader)
  }
  def getCsvFile(file:File, firstLineAsHeader:Boolean):CsvFile = {
    val bufSrc:Source = Source.fromFile(file)
    getCsvFile(bufSrc, firstLineAsHeader)
  }
  def getCsvFile(src:Source, firstLineAsHeader:Boolean):CsvFile = {
    val linesF1 = src.getLines.toList
    val csv1 = new CsvFile(linesF1, firstLineAsHeader)
    csv1
  }


}
