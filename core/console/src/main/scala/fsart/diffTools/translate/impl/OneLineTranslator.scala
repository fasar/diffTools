package fsart.diffTools.translate.impl

import fsart.diffTools.translate.Translator
import name.fraser.neil.plaintext.diff_match_patch
import fsart.diffTools.csvModel.{CsvDataImpl, CsvData}
import collection.immutable.Queue
import name.fraser.neil.plaintext.diff_match_patch.Operation
import fsart.diffTools.outputDriver.{FormattedText, TextKind}
import org.apache.commons.logging.{LogFactory, Log}

/**
 *
 * User: fabien
 * Date: 19/07/12
 * Time: 12:40
 *
 */

class OneLineTranslator extends Translator {
  private val log: Log = LogFactory.getLog(this.getClass)


  def translate(csv: CsvData[List[diff_match_patch.Diff]]): CsvDataImpl[List[FormattedText]] = {
    var array: Queue[Queue[List[FormattedText]]] = Queue.empty

    for (lines <- csv.array) {
      var line: Queue[List[FormattedText]] = Queue.empty
      for (cell <- lines) {
        if (cell.size > 0) {
          line :+= toFormattedText(cell)
        } else {
          line :+= List(FormattedText("", TextKind.Normal))
        }
      }
      array :+= line
    }
    val array1: List[List[List[FormattedText]]] = array.map {
      _.toList
    }.toList
    val res = CsvDataImpl[List[FormattedText]](array1, csv.headers, csv.separator)
    res

  }

  private def toFormattedText(diffs: List[diff_match_patch.Diff]): List[FormattedText] = {
    var res: Queue[FormattedText] = Queue.empty

    for (aDiff <- diffs) {
      val text: String = aDiff.text
      aDiff.operation match {
        case Operation.INSERT =>
          res :+= FormattedText(aDiff.text, TextKind.Add)
        case Operation.DELETE =>
          res :+= FormattedText(aDiff.text, TextKind.Suppress)
        case Operation.EQUAL =>
          res :+= FormattedText(aDiff.text, TextKind.Normal)
      }
    }
    res.toList
  }


}
