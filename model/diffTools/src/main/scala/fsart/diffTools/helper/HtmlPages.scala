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

  val body = new StringBuilder("  <script>\n  window.addEvent(\"domready\", function(){\n          " +
    "    obj = new MooTable( $(\"id_table1\"), {sortable: true, resizable: true, height: '500px', footer:true,  filter:'' });\n  " +
    "    //obj.div.setStyle('height', obj.tbody.getSize().scrollSize.y + obj.thead.getSize().scrollSize.y ); \n" +
    "    //obj.tbody.setStyle('height', obj.tbody.getSize().scrollSize.y); // don't show a frame inside the windows\n" +
    "    //obj.div.setStyle('width', obj.tbody.getSize().scrollSize.x);\n" +
    "})\n  </script>\n")


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
