package fsart.diffTools.model

import model.{CsvBuilder, CsvData}
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
    assertTrue(cvsBuilder.getCvsData().array.size == 0)
    cvsBuilder.appendLine("1;2;3;4")
    assertTrue(cvsBuilder.getCvsData().array.size == 1)
    assertTrue(cvsBuilder.getCvsData().array(0).size == 4)
  }


}
