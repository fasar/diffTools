package fsart.diffTools.Translate.Impl

import fsart.diffTools.Translate.Translator
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

class NextToEachOtherTranslator extends Translator {
  private val log: Log = LogFactory.getLog(this.getClass)


  def translate(csv: CsvData[List[diff_match_patch.Diff]]): CsvDataImpl[List[FormattedText]] = {
    var array: Queue[Queue[List[FormattedText]]] = Queue.empty

    for (lines <- csv.array) {
      var line: Queue[List[FormattedText]] = Queue.empty
      for (cell <- lines) {
        if (cell.size > 0 ) {
          line :+= getReferenceData(cell)
          line :+= getModifiedData(cell)
        } else {
          line :+= List(FormattedText("", TextKind.Normal))
          line :+= List(FormattedText("", TextKind.Normal))
        }
      }
      array :+= line
    }
    val array1: List[List[List[FormattedText]]] = array.map {
      _.toList
    }.toList

    val headers = csv.headers.flatMap{List(_, "")}
    val res = CsvDataImpl[List[FormattedText]](array1, headers, csv.separator)
    res

  }

  private def getReferenceData(diffs: List[diff_match_patch.Diff]): List[FormattedText] = {
    var res: Queue[FormattedText] = Queue.empty
    var operation = 0
    var resSuppr: Queue[FormattedText] = Queue.empty
    var resAdded: Queue[FormattedText] = Queue.empty
    for (aDiff <- diffs) {
      val text: String = aDiff.text
      aDiff.operation match {
        case Operation.INSERT =>
          resAdded :+= FormattedText(aDiff.text, TextKind.Add)
          operation += 1
        case Operation.DELETE =>
          resSuppr :+= FormattedText(aDiff.text, TextKind.Suppress)
          operation += 1
        case Operation.EQUAL =>
          resSuppr :+= FormattedText(aDiff.text, TextKind.Normal)
          resAdded :+= FormattedText(aDiff.text, TextKind.Normal)
      }
    }

    resSuppr.toList

  }

    private def getModifiedData(diffs: List[diff_match_patch.Diff]): List[FormattedText] = {
    var res: Queue[FormattedText] = Queue.empty
    var operation = 0
    var resSuppr: Queue[FormattedText] = Queue.empty
    var resAdded: Queue[FormattedText] = Queue.empty
    for (aDiff <- diffs) {
      val text: String = aDiff.text
      aDiff.operation match {
        case Operation.INSERT =>
          resAdded :+= FormattedText(aDiff.text, TextKind.Add)
          operation += 1
        case Operation.DELETE =>
          resSuppr :+= FormattedText(aDiff.text, TextKind.Suppress)
          operation += 1
        case Operation.EQUAL =>
          resSuppr :+= FormattedText(aDiff.text, TextKind.Normal)
          resAdded :+= FormattedText(aDiff.text, TextKind.Normal)
      }
    }

    if(operation == 0) {
      List(FormattedText("", TextKind.Normal))
    } else {
      resAdded.toList
    }
  }

}
