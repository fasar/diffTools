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

package fsart.diffTools.helper

import scala.collection.mutable.StringBuilder
import fsart.helper.TextTools

/**
 *
 * User: fabien
 * Date: 24/04/12
 * Time: 00:28
 *
 */

class HtmlPages {

  val headers = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" +
    "<html xmlns=\"http://www.w3.org/1999/xhtml\">" +
    "<html>\n" +
    "<head>\n" +
    "  <script type=\"text/javascript\" src=\"js/mootools.v1.11.js\"></script>\n" +
    "  <script type=\"text/javascript\" src=\"js/mootable.js\"></script>\n" +
    "  <link rel=\"stylesheet\" type=\"text/css\" href=\"js/mootable.css\">\n"

  val endHeader = "</head><body>"

  val foot = "</body></html>\n"

  val body = {
    //    new StringBuilder("  <script>\n  window.addEvent(\"domready\", function(){\n          " +
    //    "    obj = new MooTable( $(\"id_table1\"), {sortable: true, resizable: true, height: '500px', footer:true,  filter:'' });\n  " +
    //    "    //obj.div.setStyle('height', obj.tbody.getSize().scrollSize.y + obj.thead.getSize().scrollSize.y ); \n" +
    //    "    //obj.tbody.setStyle('height', obj.tbody.getSize().scrollSize.y); // don't show a frame inside the windows\n" +
    //    "    //obj.div.setStyle('width', obj.tbody.getSize().scrollSize.x);\n" +
    //    "})\n  </script>\n")
    new StringBuilder
  }


  def toHtml: String = {
    headers + endHeader + body.toString() + foot
  }

  def appendToBodyEscapeChar(str: String) {
    body.append(TextTools.escapeHTML(str))
  }

  def appendToBodyDirect(str: String) {
    body.append(str)
  }
}
