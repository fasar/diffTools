package fsart.diffTools.helper

import fsart.diffTools.model.CsvData

/**
 *
 * User: fabien
 * Date: 07/05/12
 * Time: 23:53
 *
 */

object CsvView {

  def getHtmlView(csv:CsvData[String]): String = {
    val htmlPage = new HtmlPages()
    htmlPage.body.append("  <TABLE id=\"id_table1\" cellpadding='0' cellspacing='0' >")
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
          htmlPage.body.append(col)
        else
          htmlPage.body.append("&nbsp;")
        htmlPage.body.append("</TD>\n")
      }
      htmlPage.body.append("  </TR>\n")
    }
    htmlPage.body.append(" </tbody>\n")
    htmlPage.body.append("</Table>")

    htmlPage.toHtml
  }
}
