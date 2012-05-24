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

package fsart.diffTools.helper.Impl

import fsart.diffTools.csvModel.CsvData
import name.fraser.neil.plaintext.diff_match_patch
import name.fraser.neil.plaintext.diff_match_patch.{Operation, Diff}
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.util.WorkbookUtil
import fsart.diffTools.view.CsvView
import java.io.{OutputStream, ByteArrayOutputStream}
import org.apache.poi.ss.usermodel._
import org.apache.poi.hssf.util.HSSFColor
import scala.collection.JavaConversions._
import collection.immutable.Queue

/**
 *
 * User: fabien
 * Date: 08/05/12
 * Time: 17:21
 *
 */

class CsvExcelView extends CsvView {

  val wb: Workbook = new HSSFWorkbook();
  val createHelper: CreationHelper = wb.getCreationHelper();

  var fontNrm:Font = wb.createFont()
  var fontSuppr:Font = wb.createFont()
  var fontAdd:Font = wb.createFont()


  {
    fontSuppr.setColor(HSSFColor.DARK_RED.index)
    fontSuppr.setStrikeout(true)
    fontAdd.setColor(HSSFColor.GREEN.index)
  }


  def getView(csv: CsvData[List[diff_match_patch.Diff]]): Array[Byte] = {
    val out: ByteArrayOutputStream = new ByteArrayOutputStream();


    val sheet:Sheet = wb.createSheet(WorkbookUtil.createSafeSheetName("Sheet1"));
    val createHelper: CreationHelper = wb.getCreationHelper();

    var rowPos = 0
    var cellPos = 0
    if (csv.headers != null) {
      var row: Row = sheet.createRow(rowPos)
      rowPos += 1
      cellPos = 0
      for (elem <- csv.headers) {
        row.createCell(cellPos).setCellValue(elem)
        cellPos+=1
      }
    }

    for (lines <- csv.array ) {
      var row: Row = sheet.createRow(rowPos)
      rowPos += 1
      cellPos = 0
      for (col <- lines) {
        val cell = row.createCell(cellPos)
        if (col.size > 0) {
          cell.setCellValue(toExcelCell(col))
        } else {
          cell.setCellValue("")
        }
        cellPos+=1
      }
    }

    // adjust row
    for (i <- 0 until csv.array.size ) {
      sheet.autoSizeColumn(i)
    }
    //adjust line
    for (row:Row <- sheet.rowIterator() ) {
      row.setHeightInPoints((2*sheet.getDefaultRowHeightInPoints()));
    }


    wb.write(out)
    out.flush()
    out.toByteArray
  }


  def toExcelCell(diffs: List[diff_match_patch.Diff]): RichTextString = {
    var resSuppr: Queue[diff_match_patch.Diff] = Queue.empty
    var resAdded: Queue[diff_match_patch.Diff] = Queue.empty
    for (aDiff <- diffs) {
      val text: String = aDiff.text
      aDiff.operation match {
        case Operation.INSERT =>
          resAdded :+= aDiff
        case Operation.DELETE =>
          resSuppr :+= aDiff
        case Operation.EQUAL =>
          resSuppr :+= aDiff
          resAdded :+= aDiff
      }
    }
    val resSupprString = resSuppr.map{_.text}.mkString("")
    val resAddedString = resAdded.map{_.text}.mkString("")
    val res =
    if(resSupprString.size == 0) {
      createHelper.createRichTextString(resAddedString)
    } else if(resAddedString.size == 0) {
      createHelper.createRichTextString(resSupprString)
    } else if(diffs.size == 1 && diffs(0).operation== Operation.EQUAL) {
      createHelper.createRichTextString(resAddedString)
    } else {
      createHelper.createRichTextString(resSupprString + "\n" + resAddedString)
    }

    setFont(res, resSuppr.toList, 0)
    setFont(res, resAdded.toList, res.length() - resAddedString.size)
    res
  }

  private def setFont(str: RichTextString, diff:List[diff_match_patch.Diff], startPosp:Int) {
    var startPos = startPosp
    for(aDiff <- diff) {
      val size = aDiff.text.size
      val endPos = startPos + size
      aDiff.operation match {
        case Operation.INSERT =>
          str.applyFont(startPos, endPos, fontAdd)
        case Operation.DELETE =>
          str.applyFont(startPos, endPos, fontSuppr)
        case Operation.EQUAL =>
          str.applyFont(startPos, endPos, fontNrm)
      }
      startPos += size
    }
  }
}
