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

package fsart.diffTools.csvAlgo

import fsart.helper.TextTools
import org.apache.commons.logging.{LogFactory, Log}
import fsart.diffTools.csvModel._
import name.fraser.neil.plaintext.diff_match_patch
import scala.collection.JavaConversions._


object CsvTools {
  private val log: Log = LogFactory.getLog(this.getClass)


  private val dmp: diff_match_patch = new diff_match_patch()

  private def diffText(newText: String, baseTxt: String) = {
    var res = dmp.diff_main(baseTxt, newText)
    dmp.diff_cleanupSemantic(res)
    res
  }


  // Get the difference between datas from version baseDatas to the new version newDatas
  def getDifferenceLines(newDatas: CsvData[String], baseDatas: CsvData[String]): CsvData[List[diff_match_patch.Diff]] = {
    getDifferenceLinesWithMapedDatas(newDatas, baseDatas, List.empty)
  }


  // Get the difference between datas from version baseDatas to the new version newDatas
  // The mappedDatas is a list of couple you want to considere as the same values.
  def getDifferenceLinesWithMapedDatas(newDatas: CsvData[String],
                                       baseDatas: CsvData[String],
                                        mappedDatas: List[(String,String)]): CsvData[List[diff_match_patch.Diff]] = {
    log.debug("Generate list of difference between file1 and file2")
    val listDiffLines: List[List[List[diff_match_patch.Diff]]] =
      for( key1 <- newDatas.getKeys;
           line1 <- newDatas.getLinesOfKey(key1).distinct;
           line2 <- baseDatas.getLinesOfKey(key1).distinct
      )
      yield {
        log.trace("  " + key1 + " is in both file")
        val res =
          (for ((elem1, elem2) <- line1.zip(line2))
          yield {
            val resDiffs =
            if( mappedDatas.contains((elem1, elem2)) || mappedDatas.contains((elem2, elem1)) ) {
              diffText(elem1, elem1)
            }else {
              diffText(elem1, elem2)
            }
            resDiffs.toList
          }).toList
        res
      }
    CsvDataImpl[List[diff_match_patch.Diff]](listDiffLines, newDatas.headers,newDatas.separator)

  }

  // Get lines with keys added in newDatas based on the baseDatas version
  def getAddedLines(newDatas: CsvData[String], baseDatas: CsvData[String]): CsvData[List[diff_match_patch.Diff]] = {
    log.debug("Generate list of added lines in file1")
    val listAddedLines = getAppendedLines(newDatas, baseDatas)
    CsvDataImpl[List[diff_match_patch.Diff]](listAddedLines, newDatas.headers,newDatas.separator)
  }


  // Get lines with keys supprimed in newDatas based on the baseDatas version
  def getSupprimedLines(newDatas: CsvData[String], baseDatas: CsvData[String]): CsvData[List[diff_match_patch.Diff]] = {
    log.debug("Generate list of supprimed lines in file1")
    val listSupprimedLines = getAppendedLines(baseDatas, newDatas)
    CsvDataImpl[List[diff_match_patch.Diff]](listSupprimedLines, newDatas.headers,newDatas.separator)
  }

  // get what lines is appended in appendDatas since version baseDatas
  private def getAppendedLines(appendDatas: CsvData[String], baseDatas: CsvData[String]): List[List[List[diff_match_patch.Diff]]] = {
    type E = List[diff_match_patch.Diff]
    val listAddedLines: List[List[E]] =
      for (key1 <- appendDatas.getKeys;
           val lines:List[List[String]] = appendDatas.getLinesOfKey(key1);
           line1:List[String] <- lines;
           if (!baseDatas.getKeys.contains(key1))
      )
      yield {
        log.debug("  " + key1 + " is not in a  ")
        val res =
          (for (elem1:String <- line1)
          yield {
            val resDiffs = diffText(elem1, "")
            resDiffs.toList
          }).toList
        res
      }
    listAddedLines
  }
}
