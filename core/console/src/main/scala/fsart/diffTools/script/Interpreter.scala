package fsart.diffTools.script

import org.apache.commons.logging.{Log, LogFactory}
import fsart.diffTools.csvModel.CsvData
import name.fraser.neil.plaintext.diff_match_patch.Diff

/**
 *
 * User: fabien
 * Date: 20/07/12
 * Time: 14:46
 *
 */

class Interpreter(val ioData:InputOutputData) {

  private val log: Log = LogFactory.getLog(this.getClass)

  private val out = System.out
  private val flusher = new java.io.PrintWriter(out)



  private val interpreter = {
    val settings = new scala.tools.nsc.GenericRunnerSettings( println _ )
    settings.usejavacp.value = true
    val inter = new scala.tools.nsc.interpreter.IMain(settings, flusher)

    if(ioData!=null) {
      val addTable = new Function2[String, CsvData[List[Diff]], Unit] {
          def apply(tableName:String, data: CsvData[List[Diff]]): Unit = {
            ioData.addTable(tableName, data)
          }
        }
      inter.bind("addTable", addTable)
      inter.bind("endScript", ioData.endScript)

      inter.bindValue("data1", ioData.data1)
      inter.bindValue("data2", ioData.data2)

      inter.bindValue("outputDriver", ioData.outputDriver)
      inter.bindValue("translator", ioData.translator)

      inter.addImports("fsart.diffTools.csvDsl.CsvBuilderDsl._")
      inter.addImports("fsart.diffTools.csvDsl.CsvRulesDsl._")
    }

    inter
  }

  def eval(line:String) {
      interpreter.interpret(line)
  }

}
