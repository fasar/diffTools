package fsart.thirdParty


import java.io.FileOutputStream
import java.util.{Date, Calendar}

import org.apache.poi.ss.util.WorkbookUtil
import org.junit.Test
import org.junit.Assert._
import org.apache.poi.ss.usermodel._
import org.apache.poi.hssf.usermodel.{HSSFFont, HSSFWorkbook}
import org.apache.poi.hssf.util.HSSFColor


/**
 *
 * User: fabien
 * Date: 08/05/12
 * Time: 14:40
 *
 */

class ExcelCreateTest {

  @Test
  def createWorkBook {
    val wb: Workbook = new HSSFWorkbook();
    val fileOut: FileOutputStream = new FileOutputStream("workbook.xls");
    val sheet:Sheet = wb.createSheet(WorkbookUtil.createSafeSheetName("new sheet"));
    val sheet2:Sheet = wb.createSheet(WorkbookUtil.createSafeSheetName("second sheet"));

    val createHelper: CreationHelper = wb.getCreationHelper();

    var row: Row = sheet.createRow(0)
    var cell: Cell = row.createCell(0);
    cell.setCellValue(1);
    row.createCell(1).setCellValue(1.2);
    row.createCell(2).setCellValue(
      createHelper.createRichTextString("This is a string"));
    row.createCell(3).setCellValue(true);

    row.createCell(4).setCellValue(1.1);
    row.createCell(5).setCellValue(new Date());
    row.createCell(6).setCellValue(Calendar.getInstance());
    row.createCell(7).setCellValue("a string");
    row.createCell(8).setCellValue(true);
    row.createCell(9).setCellType(Cell.CELL_TYPE_ERROR);


    // Aqua background
    row = sheet.createRow(1)
    var style:CellStyle = wb.createCellStyle();
    style.setFillBackgroundColor(IndexedColors.AQUA.getIndex());
    style.setFillPattern(CellStyle.BIG_SPOTS);
    cell = row.createCell(1);
    cell.setCellValue("X");
    cell.setCellStyle(style);

    // Orange "foreground", foreground being the fill foreground not the font color.
    style = wb.createCellStyle();
    style.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
    style.setFillPattern(CellStyle.SOLID_FOREGROUND);
    cell = row.createCell(2);
    cell.setCellValue("X");
    cell.setCellStyle(style);

    // Create a row and put some cells in it. Rows are 0 based.
    row = sheet.createRow(2);

    // Create a new font and alter it.
    var font:Font = wb.createFont();
    font.setFontHeightInPoints(24);
    font.setFontName("Courier New");
    font.setItalic(true);
    font.setStrikeout(true);

    // Fonts are set into a style so create a new one to use.
    style = wb.createCellStyle();
    style.setFont(font);

    // Create a cell and put a value in it.
    cell = row.createCell(1);
    cell.setCellValue("This is a test of fonts");
    cell.setCellStyle(style);

    wb.write(fileOut);
    fileOut.close();
  }


  @Test
  def createWorkBook2 {
    val wb: Workbook = new HSSFWorkbook();
    val fileOut: FileOutputStream = new FileOutputStream("workbook2.xls");
    val sheet:Sheet = wb.createSheet(WorkbookUtil.createSafeSheetName("new sheet"));
    val sheet2:Sheet = wb.createSheet(WorkbookUtil.createSafeSheetName("second sheet"));

    val createHelper: CreationHelper = wb.getCreationHelper();

    var row: Row = sheet.createRow(0)
    var cell: Cell = row.createCell(0);

    // Aqua background
    row = sheet.createRow(1)
    var fontSuppr:Font = wb.createFont()
    fontSuppr.setColor(HSSFColor.DARK_RED.index)
    fontSuppr.setStrikeout(true)
    var fontAdd:Font = wb.createFont()
    fontAdd.setColor(HSSFColor.GREEN.index)

    cell = row.createCell(1);
    val content = createHelper.createRichTextString("This is \n a string")
    content.applyFont(0, content.length(), fontSuppr)
    content.applyFont(10, content.length(), fontAdd)
    cell.setCellValue(content);


    // Orange "foreground", foreground being the fill foreground not the font color.
    val style = wb.createCellStyle();
    style.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
    style.setFillPattern(CellStyle.SOLID_FOREGROUND);
    cell = row.createCell(2);
    cell.setCellValue("X");
    cell.setCellStyle(style);

    wb.write(fileOut);
    fileOut.close();
  }

}
