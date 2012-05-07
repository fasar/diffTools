package fsart.diffTools.model

/**
 *
 * User: fabien
 * Date: 23/04/12
 * Time: 22:07
 *
 */

object CsvTools {

  def compare(csv1: CsvFile, csv2: CsvFile): CsvFile = {
    val arrayRes =
      for (lineF1 <- csv1.array)
      yield {
        val key = lineF1(0)
        val linesF2 = csv2.getLinesOfKey(key)
        "a"
      }

    csv1
  }


}
