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
