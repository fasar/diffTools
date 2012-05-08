package fsart.diffTools.helper.Impl

import fsart.diffTools.model.CsvData
import name.fraser.neil.plaintext.diff_match_patch
import name.fraser.neil.plaintext.diff_match_patch.{Operation, Diff}
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.util.WorkbookUtil
import fsart.diffTools.helper.CsvView
import java.io.{OutputStream, ByteArrayOutputStream}
import org.apache.poi.ss.usermodel._
import org.apache.poi.hssf.util.HSSFColor

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

    wb.write(out)
    out.flush()
    out.toByteArray
  }


  def toExcelCell(diffs: List[diff_match_patch.Diff]): RichTextString = {
    var resSuppr: List[diff_match_patch.Diff] = List.empty
    var resAdded: List[diff_match_patch.Diff] = List.empty
    for (aDiff <- diffs) {
      val text: String = aDiff.text
      aDiff.operation match {
        case Operation.INSERT =>
          resAdded ::= aDiff
        case Operation.DELETE =>
          resSuppr ::= aDiff
        case Operation.EQUAL =>
          resSuppr ::= aDiff
          resAdded ::= aDiff
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

    setFont(res, resSuppr, 0)
    setFont(res, resAdded, res.length() - resAddedString.size)
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
