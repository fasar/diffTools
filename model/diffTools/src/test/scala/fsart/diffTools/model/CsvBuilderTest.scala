package fsart.diffTools.model

import org.apache.commons.logging.{LogFactory, Log}
import org.junit.Test
import org.junit.Assert._

/**
 *
 * User: fabien
 * Date: 07/05/12
 * Time: 17:39
 *
 */

class CsvBuilderTest {
  private val log: Log = LogFactory.getLog(this.getClass)

  @Test
  def csvFile_test1 {
    val cvsBuilder = new CsvBuilder()
    var csv:CsvData[String] = cvsBuilder.getCvsData
    assertTrue(csv.array.size == 0)

    cvsBuilder.appendLine("1;2;3;4")
    csv = cvsBuilder.getCvsData
    assertTrue(csv.array.size == 1)
    assertTrue(csv.array(0).size == 4)

    cvsBuilder.appendLine("1;2;3;4")
    assertTrue(csv.array.size == 1)
    csv = cvsBuilder.getCvsData
    assertTrue(csv.array.size == 2)
    assertTrue(csv.array(0).size == 4)
    assertTrue(csv.array(1).size == 4)

    cvsBuilder.appendLine("1;2;3;4;5")
    assertTrue(csv.array.size == 2)
    csv = cvsBuilder.getCvsData
    assertTrue(csv.array.size == 3)
    assertTrue(csv.array(0).size == 5)
    assertTrue(csv.array(1).size == 5)
    assertTrue(csv.array(2).size == 5)
  }
  @Test
  def csvFile_test2 {
    val cvsBuilder = new CsvBuilder()
    cvsBuilder.setHeaders("a;b;c;d")

    var csv:CsvData[String] = cvsBuilder.getCvsData
    assertTrue(csv.array.size == 0)
    assertTrue(csv.headers == List("a", "b", "c", "d"))
  }


}
