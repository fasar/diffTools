package fsart.diffTools

import fsart.diffTools.model.CsvFile
import org.junit.Test
import org.junit.Assert._
import org.apache.commons.logging.{LogFactory, Log}
import scala.collection.JavaConversions._

/**
 *
 * User: fabien
 * Date: 23/04/12
 * Time: 19:56
 *
 */

class CsvFileTest {

  private val log: Log = LogFactory.getLog(this.getClass)

  @Test
  def csvFile_test1 {
    val cvsFile = new CsvFile(List("a;b;c;d;e", "f;g;h;i;j"))
    assertTrue(cvsFile.array.size == 2)
    assertTrue(cvsFile.array(0).size == 5)
    assertTrue(cvsFile.array(1).size == 5)
  }

  @Test
  def csvFile_test2 {
    val cvsFile = new CsvFile(List())
    assertTrue(cvsFile.array.size == 0)
  }

  @Test
  def csvFile_test3 {
    val cvsFile = new CsvFile(List("a;b;c;d;e", "f;g;h;i;j;zzz"))
    assertTrue(cvsFile.array(1).size == 6)
    assertTrue(cvsFile.array(0).size == 6)
  }

  @Test
  def csvFile_test4 {
    val cvsFile = new CsvFile(List("a;b;c;d;e", "f;g;h;i;j"))
    log.debug(" list des clefs " + cvsFile.getKeys)
    assertTrue(cvsFile.getKeys == List("a", "f"))
  }

  @Test
  def csvFile_test5 {
    val cvsFile = new CsvFile(List())
    assertTrue(cvsFile.getKeys == List())
  }

  @Test
  def csvFile_duplicatedKey {
    val cvsFile = new CsvFile(List("a;b;c;d;e", "f;g;h;i;j", "a;b;c;d;e", "a;b;c;d;e"))
    assertTrue(cvsFile.getDuplicatedKeys("a") == 3)
  }

  @Test
  def csvFile_duplicatedKey2 {
    val cvsFile = new CsvFile(List())
    //    log.debug(" list des clefs " + cvsFile.getKeys)
    //    log.debug(" list des clefs dup " +  cvsFile.getDuplicatedKeys)
    log.debug(cvsFile.getDuplicatedKeys)
    assertTrue(cvsFile.getDuplicatedKeys == Map())
  }

  @Test
  def csvFile_getLinesOfKey {
    val cvsFile = new CsvFile(List("a;b;c;d;e", "f;g;h;i;j", "a;b;c;d;f", "a;b;c;d;g"))
    val linesOfKeys = cvsFile.getLinesOfKey("a")
    assertTrue(linesOfKeys.size == 3)

  }
}
