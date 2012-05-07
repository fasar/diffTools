package fsart.diffTools.consoleApp

/**
 *
 * User: fabien
 * Date: 28/04/12
 * Time: 15:07
 *
 */

class DiffToolsApplicationException(message: String, cause: Throwable) extends Exception(message, cause)  {
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
