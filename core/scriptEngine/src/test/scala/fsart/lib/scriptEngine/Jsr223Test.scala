package fsart.lib.scriptEngine


import org.junit.Test
import org.junit.Assert._

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;


/**
 *
 * User: fabien
 * Date: 04/05/12
 * Time: 01:34
 *
 */


class Jsr223Test {

  @Test
  def testHelloWorld() {
    //throws
    def manager: ScriptEngineManager = new ScriptEngineManager();
    def engine: ScriptEngine = manager.getEngineByName("jruby");
    engine.eval("puts \"pipo\"; puts \"poil\"");
  }
}
