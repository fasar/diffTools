package fsart.diffTools.script.scriptEngineImpl

import fsart.diffTools.script.{Interpreter, ScriptEngine, InputOutputData}
import fsart.diffTools.CsvDsl.CsvBuilderDsl._
import fsart.diffTools.CsvDsl.CsvRulesDsl._

/**
 *
 * User: fabien
 * Date: 20/07/12
 * Time: 18:52
 *
 */

class ScriptFileEngine(val script:String) extends ScriptEngine {

  def process(ioData: InputOutputData) {
    val interpreter = new Interpreter(ioData)
    interpreter.eval(script)
  }
}