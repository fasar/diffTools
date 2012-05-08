package fsart.helper

import name.fraser.neil.plaintext.diff_match_patch
import name.fraser.neil.plaintext.diff_match_patch.Operation
import scala.collection.JavaConversions._

/**
 *
 * User: fabien
 * Date: 24/04/12
 * Time: 00:09
 *
 */

object TextTools {
  private val dmp: diff_match_patch = new diff_match_patch()

  def diffText(txt1: String, txt2: String) = {
    var res = dmp.diff_main(txt1, txt2)
    dmp.diff_cleanupSemantic(res)
    res
  }

  //def toHtml(diffs: LinkedList[diff_match_patch.Diff]): String = dmp.diff_prettyHtml(diffs)


  def toHtml(diffs: List[diff_match_patch.Diff]): String = {
    val html: StringBuilder = new StringBuilder
    import scala.collection.JavaConversions._
    for (aDiff <- diffs) {
      val text: String = escapeHTML(aDiff.text)
      aDiff.operation match {
        case Operation.INSERT =>
          html.append("<ins style=\"background:#e6ffe6;\">").append(text).append("</ins>")
        case Operation.DELETE =>
          html.append("<del style=\"background:#ffe6e6;\">").append(text).append("</del>")
        case Operation.EQUAL =>
          html.append("<span>").append(text).append("</span>")
      }
    }

    return html.toString
  }


  def escapeHTML(s: String): String = {
    val sb: StringBuffer = new StringBuffer();
    var n: Int = s.length();
    for (c <- s) {
      c match {
        case '<' => sb.append("&lt;")
        case '>' => sb.append("&gt;")
        case '&' => sb.append("&amp;")
        case '"' => sb.append("&quot;")
        case 'à' => sb.append("&agrave;")
        case 'À' => sb.append("&Agrave;")
        case 'â' => sb.append("&acirc;")
        case 'Â' => sb.append("&Acirc;")
        case 'ä' => sb.append("&auml;")
        case 'Ä' => sb.append("&Auml;")
        case 'å' => sb.append("&aring;")
        case 'Å' => sb.append("&Aring;")
        case 'æ' => sb.append("&aelig;")
        case 'Æ' => sb.append("&AElig;")
        case 'ç' => sb.append("&ccedil;")
        case 'Ç' => sb.append("&Ccedil;")
        case 'é' => sb.append("&eacute;")
        case 'É' => sb.append("&Eacute;")
        case 'è' => sb.append("&egrave;")
        case 'È' => sb.append("&Egrave;")
        case 'ê' => sb.append("&ecirc;")
        case 'Ê' => sb.append("&Ecirc;")
        case 'ë' => sb.append("&euml;")
        case 'Ë' => sb.append("&Euml;")
        case 'ï' => sb.append("&iuml;")
        case 'Ï' => sb.append("&Iuml;")
        case 'ô' => sb.append("&ocirc;")
        case 'Ô' => sb.append("&Ocirc;")
        case 'ö' => sb.append("&ouml;")
        case 'Ö' => sb.append("&Ouml;")
        case 'ø' => sb.append("&oslash;")
        case 'Ø' => sb.append("&Oslash;")
        case 'ß' => sb.append("&szlig;")
        case 'ù' => sb.append("&ugrave;")
        case 'Ù' => sb.append("&Ugrave;")
        case 'û' => sb.append("&ucirc;")
        case 'Û' => sb.append("&Ucirc;")
        case 'ü' => sb.append("&uuml;")
        case 'Ü' => sb.append("&Uuml;")
        case '®' => sb.append("&reg;")
        case '©' => sb.append("&copy;")
        case '€' => sb.append("&euro;")
        // be carefull with this one (non-breaking whitee space)
        //case ' ' => sb.append("&nbsp;")
        case x => sb.append(c)
      }
    }
    return sb.toString();
  }
}
