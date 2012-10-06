/****************************************************************************
 * Copyright Fabien Sartor 
 * Contributors: Fabien Sartor (fabien.sartor@gmail.com)
 *               http://fasar.fr
 *  
 * This software is a computer program whose purpose to compute differences 
 * between two files.
 *
 ****************************************************************************
 *
 *  This software is governed by the CeCILL license under French law and
 *  abiding by the rules of distribution of free software.  You can  use, 
 *  modify and/ or redistribute the software under the terms of the CeCILL
 *  license as circulated by CEA, CNRS and INRIA at the following URL: 
 *  "http://www.cecill.info". 
 *  
 *  As a counterpart to the access to the source code and  rights to copy,
 *  modify and redistribute granted by the license, users are provided only
 *  with a limited warranty  and the software's author,  the holder of the
 *  economic rights,  and the successive licensors  have only  limited
 *  liability. 
 *  
 *  In this respect, the user's attention is drawn to the risks associated
 *  with loading,  using,  modifying and/or developing or reproducing the
 *  software by the user in light of its specific status of free software,
 *  that may mean  that it is complicated to manipulate,  and  that  also
 *  therefore means  that it is reserved for developers  and  experienced
 *  professionals having in-depth computer knowledge. Users are therefore
 *  encouraged to load and test the software's suitability as regards their
 *  requirements in conditions enabling the security of their systems and/or 
 *  data to be ensured and,  more generally, to use and operate it in the 
 *  same conditions as regards security. 
 *  
 *  The fact that you are presently reading this means that you have had
 *  knowledge of the CeCILL license and that you accept its terms. 
 *
 ****************************************************************************
 */

package fsart.diffTools.outputDriver.Impl;

import java.io.ByteArrayOutputStream

import scala.collection.JavaConversions._
import collection.immutable.Queue

import fsart.diffTools.csvModel.CsvData
import name.fraser.neil.plaintext.diff_match_patch
import name.fraser.neil.plaintext.diff_match_patch.Operation
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.util.WorkbookUtil

import org.apache.poi.hssf.util.HSSFColor
import fsart.diffTools.outputDriver.{TextKind, FormattedText, CsvView2}
import org.apache.poi.ss.usermodel._
import java.util.logging.Logger
import fsart.diffTools.outputDriver.CsvView2


/**
 *
 * User: fabien
 * Date: 08/05/12
 * Time: 17:21
 *
 * Get a Excel sheet with modified data in the same cell.
 * If a data change, the origin is in the first line of the cell and the modified data
 * is in a second line of the cell.
 *
 */

class CsvExcelView2SpecialAppEngine extends CsvView2 {
  private val log:Logger = Logger.getLogger("fsart.diffTools.webApp.XlsToCsv");

  val wb: Workbook = new HSSFWorkbook();
  val createHelper: CreationHelper = wb.getCreationHelper();

  var fontNrm: Font = wb.createFont()
  var fontSuppr: Font = wb.createFont()
  var fontAdd: Font = wb.createFont()


  {
    fontSuppr.setColor(HSSFColor.DARK_RED.index)
    fontSuppr.setStrikeout(true)
    fontAdd.setColor(HSSFColor.GREEN.index)
  }

  def getData():Array[Byte] = {
    val out: ByteArrayOutputStream = new ByteArrayOutputStream();
    wb.write(out)
    out.flush()
    out.toByteArray
  }


  def addCsvTable(tableName:String, csv: CsvData[List[FormattedText]]) {
    val sheet: Sheet = wb.createSheet(WorkbookUtil.createSafeSheetName(tableName));

    var rowPos = 0
    var cellPos = 0

    // Add headers to current table if CsvData have headers
    if (csv.headers != null) {
      var row: Row = sheet.createRow(rowPos)
      rowPos += 1
      cellPos = 0
      for (elem <- csv.headers) {
        row.createCell(cellPos).setCellValue(elem)
        cellPos += 1
      }
    }

    // Append data to current table
    for (lines <- csv.array) {
      var row: Row = sheet.createRow(rowPos)
      rowPos += 1
      cellPos = 0
      for (col <- lines) {
        val cell = row.createCell(cellPos)
        if (col.size > 0) {
          cell.setCellValue(toExcelCell(col))
          // adjust excel line height
          val nbCR = col.foldLeft(0){ _ + _.text.count{ _ == '\n' } }
          if(nbCR>0) {
            row.setHeightInPoints(((nbCR+1) * sheet.getDefaultRowHeightInPoints()));
          }
        } else {
          cell.setCellValue("")
        }
        cellPos += 1
      }
    }

    // adjust row
    // Adjustement can't be done in google app engine because it can't support java.awt.font._ 
	//    for (i <- 0 until csv.array.size) {
	//      sheet.autoSizeColumn(i)
	//    }

  }


  def toExcelCell(strs: List[FormattedText]): RichTextString = {
    val txt = strs.map{_.text}.mkString
    val res = createHelper.createRichTextString(txt)
    var offset = 0
    for (str <- strs) {
      val txtLength = str.text.size
      setFont(res, str.kind, offset, txtLength)
      offset += txtLength
    }

    res
  }

  private def setFont(str: RichTextString, kind: TextKind.Value, startPos: Int, length: Int) {
    val endPos = startPos + length
    kind match {
      case TextKind.Normal =>   str.applyFont(startPos, endPos, fontNrm)
      case TextKind.Add =>   str.applyFont(startPos, endPos, fontAdd)
      case TextKind.Suppress => str.applyFont(startPos, endPos, fontSuppr)
    }
  }


}
