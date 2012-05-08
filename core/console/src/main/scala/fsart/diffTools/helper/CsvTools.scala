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
