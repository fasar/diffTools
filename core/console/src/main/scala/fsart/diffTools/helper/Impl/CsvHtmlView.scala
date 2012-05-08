package fsart.diffTools.helper.Impl

import fsart.diffTools.model.CsvData
import fsart.diffTools.helper.{HtmlPages, CsvView}
import name.fraser.neil.plaintext.diff_match_patch
import name.fraser.neil.plaintext.diff_match_patch.{Operation, Diff}
import fsart.helper.TextTools

/**
 *
 * User: fabien
 * Date: 08/05/12
 * Time: 17:21
 *
 */

object CsvHtmlView extends CsvView {

  def getView(csv:CsvData[List[diff_match_patch.Diff]]): Array[Byte] = {
    val htmlPage = new HtmlPages()
    htmlPage.body.append("  <TABLE id=\"id_table1\" cellpadding='0' cellspacing='0' border='1'>")
    if (csv.headers != null) {
      htmlPage.body.append(" <thead>\n  <TR>\n")
      for (elem <- csv.headers) {
        htmlPage.body.append("  <TD>")
        htmlPage.appendToBodyEscapeChar(elem)
        htmlPage.body.append("</TD>\n")
      }
      htmlPage.body.append("  </TR>\n </thead>\n")
    }
    htmlPage.body.append(" <tbody>\n")
    for (lines <- csv.array ) {
      htmlPage.body.append("  <TR>\n")
      for (col <- lines) {
        htmlPage.body.append("    <TD>")
        if (col.size > 0)
          htmlPage.body.append(TextTools.toHtml(col))
        else
          htmlPage.body.append("&nbsp;")
        htmlPage.body.append("</TD>\n")
      }
      htmlPage.body.append("  </TR>\n")
    }
    htmlPage.body.append(" </tbody>\n")
    htmlPage.body.append("</Table>")

    var resString = htmlPage.toHtml
    resString.getBytes
  }


}
