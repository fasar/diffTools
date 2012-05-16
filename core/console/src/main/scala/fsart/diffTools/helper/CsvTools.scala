/**
 * Copyright Fabien Sartor 
 * Contributors: Fabien Sartor (fabien.sartor@gmail.com)
 *  
 * This software is a computer program whose purpose to compate two 
 * files.
 *
 */
/**
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
 */
package fsart.diffTools.model.helper

import fsart.helper.TextTools
import org.apache.commons.logging.{LogFactory, Log}
import fsart.diffTools.model._
import name.fraser.neil.plaintext.diff_match_patch
import scala.collection.JavaConversions._

/**
 *
 * User: fabien
 * Date: 23/04/12
 * Time: 22:07
 *
 */

object CsvTools {
  private val log: Log = LogFactory.getLog(this.getClass)


  def concat[E](csv1: CsvData[E], csv2: CsvData[E]): CsvData[E] = {
    new CsvData[E]() {
      override val headers = csv1.headers
      override val array = csv1.array ++ csv2.array
      override val separator = csv1.separator
    }
  }


  def getDifferenceLines(csv1: CsvData[String], csv2: CsvData[String]): CsvData[List[diff_match_patch.Diff]] = {
    log.debug("Generate list of difference between file1 and file2")
    val listDiffLines: List[List[List[diff_match_patch.Diff]]] =
      for (line1 <- csv1.array;
           val key1 = line1(0);
           val lines2 = csv2.getLinesOfKey(key1).distinct;
           line2 <- lines2
           )
      yield {
        log.trace("  " + key1 + " is in both file")
        val res =
          (for ((elem1, elem2) <- line1.zip(line2))
          yield {
            val resDiffs = TextTools.diffText(elem1, elem2)
            resDiffs.toList
          }).toList
        res
      }
    new CsvData[List[diff_match_patch.Diff]]() {
      override val headers = csv1.headers
      override val array = listDiffLines
      override val separator = csv1.separator
    }
  }

  def getAddedLines(csv1: CsvData[String], csv2: CsvData[String]): CsvData[List[diff_match_patch.Diff]] = {
    log.debug("Generate list of added lines in file1")
    val listAddedLines: List[List[List[diff_match_patch.Diff]]] =
      for (line1 <- csv1.array;
           val key1 = line1(0);
           if (!csv2.getKeys.contains(key1))
      )
      yield {
        log.debug("  " + key1 + " is added in file1 ")
        val res =
          (for (elem1 <- line1)
          yield {
            val resDiffs = TextTools.diffText(elem1, "")
            resDiffs.toList
          }).toList
        res
      }
    new CsvData[List[diff_match_patch.Diff]]() {
      override val headers = csv1.headers
      override val array = listAddedLines
      override val separator = csv1.separator
    }
  }

  def getASupprimedLines(csv1: CsvData[String], csv2: CsvData[String]): CsvData[List[diff_match_patch.Diff]] = {
    log.debug("Generate list of supprimed lines in file1")
    val listSupprimedLines: List[List[List[diff_match_patch.Diff]]] =
      for (line2 <- csv2.array;
           val key2 = line2(0);
           if (!csv1.getKeys.contains(key2))
      )
      yield {
        log.debug("  " + key2 + " is supprimed in file1 ")
        val res =
          (for (elem2 <- line2)
          yield {
            val resDiffs = TextTools.diffText("", elem2)
            resDiffs.toList
          }).toList
        res
      }
    new CsvData[List[diff_match_patch.Diff]]() {
      override val headers = csv1.headers
      override val array = listSupprimedLines
      override val separator = csv1.separator
    }
  }
}
