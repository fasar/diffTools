package fsart.diffTools.consoleApp

import scala.Predef._
import scala.tools.nsc.interpreter._
import scala.tools.nsc._
/**
 *
 * User: fabien
 * Date: 19/07/12
 * Time: 21:11
 *
 */

object Interpreter {

      val out = System.out
      val flusher = new java.io.PrintWriter(out)

      lazy val interpreter = {
        val settings = new GenericRunnerSettings( println _ )
        settings.usejavacp.value = true
        val int = new IMain(settings, flusher)
        int
      }


}
