package fsart.diffTools.model

import fsart.helper.TextTools
import org.apache.commons.logging.{LogFactory, Log}

/**
 *
 * User: fabien
 * Date: 23/04/12
 * Time: 22:07
 *
 */

object CsvTools {
  private val log: Log = LogFactory.getLog(this.getClass)


  def compare(csv1: CsvData, csv2: CsvData): CsvData = {
    val arrayRes =
      for (lineF1 <- csv1.array)
      yield {
        val key = lineF1(0)
        val linesF2 = csv2.getLinesOfKey(key)
        "a"
      }

    csv1
  }

  def concat(csv1: CsvData, csv2: CsvData): CsvData = {
    new CsvData() {
      override val headers = csv1.headers
      override val array = csv1.array ++ csv2.array
      override val separator = csv1.separator
    }
  }


  def getDifferenceLines(csv1: CsvData, csv2: CsvData): CsvData = {
    log.debug("Generate list of difference between file1 and file2")
    val listDiffLines: List[List[String]] =
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
            val resHtml = TextTools.toHtml(resDiffs)
            resHtml
          }).toList
        res
      }
    new CsvData() {
      override val headers = csv1.headers
      override val array = listDiffLines
      override val separator = csv1.separator
    }
  }

  def getAddedLines(csv1: CsvData, csv2: CsvData): CsvData = {
    log.debug("Generate list of added lines in file1")
    val listAddedLines: List[List[String]] =
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
            val resHtml = TextTools.toHtml(resDiffs)
            resHtml
          }).toList
        res
      }
    new CsvData() {
      override val headers = csv1.headers
      override val array = listAddedLines
      override val separator = csv1.separator
    }
  }

  def getASupprimedLines(csv1: CsvData, csv2: CsvData): CsvData = {
    log.debug("Generate list of supprimed lines in file1")
    val listSupprimedLines: List[List[String]] =
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
            val resHtml = TextTools.toHtml(resDiffs)
            resHtml
          }).toList
        res
      }
    new CsvData() {
      override val headers = csv1.headers
      override val array = listSupprimedLines
      override val separator = csv1.separator
    }
  }
}
