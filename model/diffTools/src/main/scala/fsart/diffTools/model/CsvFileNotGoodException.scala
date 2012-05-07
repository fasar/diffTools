package fsart.diffTools.model

/**
 *
 * User: fabien
 * Date: 23/04/12
 * Time: 19:36
 *
 */

class CsvFileNotGoodException(message: String, cause: Throwable) extends Exception(message, cause) {
  def this(s: String) {
    this(s, null)
  }

  def this(cause: Throwable) {
    this("", cause)
  }

  def this() {
    this("", null)
  }
}
