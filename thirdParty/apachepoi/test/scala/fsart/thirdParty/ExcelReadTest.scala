package fsart.thirdParty

import org.apache.poi.ss.usermodel.{Workbook, Sheet, Row, Cell}
import java.io.{FileOutputStream, InputStream, FileInputStream}

import org.junit.Test
import org.junit.Assert._
import org.apache.poi.hssf.usermodel.HSSFWorkbook


/**
 *
 * User: fabien
 * Date: 08/05/12
 * Time: 15:21
 *
 */

class ExcelReadTest {


  @Test
  def readAndModifyTest {
    val inp:InputStream = new FileInputStream("workbook3.xls");
    //InputStream inp = new FileInputStream("workbook.xlsx");
    //val wb:Workbook = WorkbookFactory.create(inp);
    val wb = new HSSFWorkbook(inp)

    val sheet:Sheet = wb.getSheetAt(0);
    var row:Row = sheet.createRow(2);
    var cell:Cell = row.createCell(3);
    if (cell == null)
        cell = row.createCell(3);
    cell.setCellType(Cell.CELL_TYPE_STRING);
    cell.setCellValue("This is a read test")

    // Write the output to a file
    var fileOut:FileOutputStream = new FileOutputStream("workbook4.xls");
    wb.write(fileOut);
    fileOut.close();
  }
}
