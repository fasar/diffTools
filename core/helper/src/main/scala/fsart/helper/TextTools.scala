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

package fsart.helper

import name.fraser.neil.plaintext.diff_match_patch
import name.fraser.neil.plaintext.diff_match_patch.Diff
import name.fraser.neil.plaintext.diff_match_patch.Operation


object TextTools {


  //def toHtml(diffs: LinkedList[diff_match_patch.Diff]): String = dmp.diff_prettyHtml(diffs)


  def toHtml(diffs: List[diff_match_patch.Diff]): String = {
    val html: StringBuilder = new StringBuilder
    for (abDiff:diff_match_patch.Diff <- diffs) {
      val aDiff:Diff = abDiff
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

