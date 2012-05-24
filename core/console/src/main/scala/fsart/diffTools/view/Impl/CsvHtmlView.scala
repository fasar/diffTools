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
import fsart.diffTools.view.CsvView
import fsart.diffTools.helper.HtmlPages
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
