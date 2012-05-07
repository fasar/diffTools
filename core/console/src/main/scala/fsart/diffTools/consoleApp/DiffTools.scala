package fsart.diffTools.consoleApp

import org.apache.commons.logging.{Log, LogFactory}
import java.io.{File, FileWriter, BufferedWriter, PrintWriter}
import fsart.helper.{TextTools, Loader}


/**
 * Hello world!
 *
 */
object DiffTools
{
  private val log:Log = LogFactory.getLog(this.getClass)

  def main(args: Array[String]):Unit={
    try {
      DiffToolsCalc.main(args)
    } catch {
      case e:Throwable => log.error(e.toString + "\n" + e.getStackTraceString)
    }
  }

}
