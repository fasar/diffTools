package fsart.diffTools.script

/**
 *
 * User: fabien
 * Date: 20/07/12
 * Time: 18:38
 *
 */

trait ScriptEngine {

  def process(ioData:InputOutputData): Unit
}
