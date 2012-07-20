package fsart.diffTools.script

import fsart.helper.Loader
import java.util.regex.{Matcher, Pattern}
import scala.collection.JavaConversions._
import org.apache.commons.logging.{LogFactory, Log}
import scriptEngineImpl.{ScriptFileEngine, DefaultScriptEngine}
import java.io._

/**
 *
 * User: fabien
 * Date: 20/07/12
 * Time: 17:56
 *
 */

object ScriptDescriptor {
  private val log: Log = LogFactory.getLog(this.getClass)

  def getScriptsNames():java.util.List[String] = {

    val scriptsDir = Loader.getResource("scripts")
    var scriptsName = List.empty[String]
    if(scriptsDir != null) {
      log.debug("Scripts dir found at : " + scriptsDir)
      val scriptsFile = new File(scriptsDir.getFile)
      if(scriptsFile != null && scriptsFile.isDirectory) {
        val ficfilter:FilenameFilter = new FilenameFilter(){
          def accept(dir:File, name:String):Boolean =  {
            val p:Pattern = Pattern.compile(".*\\.dts");
            val m:Matcher = p.matcher(name);
            m.matches();
          }
        };
        scriptsName =
          for(file <- scriptsFile.listFiles(ficfilter).toList) yield {
            log.trace("Script file : " + file.getName)
            file.getName
          }
      }
    } else {
      log.debug("Scripts dir not found")
    }
    "default"::scriptsName
  }

  def getScriptEngine(scriptName:String): ScriptEngine = {
    scriptName match {
      case "default" =>
        log.debug("Use default build-in script")
        DefaultScriptEngine
      case _ =>
        log.debug("Use file script : " + scriptName)
        getScriptFileEngine(scriptName)
    }
  }

  def getScriptFileEngine(scriptName:String): ScriptEngine = {
    val scriptUrl = Loader.getResource("scripts/" + scriptName)
    if(scriptUrl == null) throw new FileNotFoundException("can't get resource : scripts/" + scriptName)
    val scriptReader = new FileReader(scriptUrl.getFile)
    if(scriptReader == null) throw new FileNotFoundException("can't get file : scripts/" + scriptName)
    val reader = new BufferedReader(scriptReader)
    if(reader == null) throw new FileNotFoundException("can't get file reader : scripts/" + scriptName)

    val data = new StringBuilder
    while(reader.ready) {
      data.append(reader.readLine)
      data.append("\n")
    }

    log.debug("I get script : scripts/" + scriptName )
    new ScriptFileEngine(data.toString)
  }

}
