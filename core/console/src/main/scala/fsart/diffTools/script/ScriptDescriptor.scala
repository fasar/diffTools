/****************************************************************************
 * Copyright Fabien Sartor 
 * Contributors: Fabien Sartor (fabien.sartor@gmail.com)
 *               http://fasar.fr
 *  
 * This software is a computer program whose purpose to compute differences 
 * between two files.
 *
 ****************************************************************************
 *
 *  This software is governed by the CeCILL license under French law and
 *  abiding by the rules of distribution of free software.  You can  use, 
 *  modify and/ or redistribute the software under the terms of the CeCILL
 *  license as circulated by CEA, CNRS and INRIA at the following URL: 
 *  "http://www.cecill.info". 
 *  
 *  As a counterpart to the access to the source code and  rights to copy,
 *  modify and redistribute granted by the license, users are provided only
 *  with a limited warranty  and the software's author,  the holder of the
 *  economic rights,  and the successive licensors  have only  limited
 *  liability. 
 *  
 *  In this respect, the user's attention is drawn to the risks associated
 *  with loading,  using,  modifying and/or developing or reproducing the
 *  software by the user in light of its specific status of free software,
 *  that may mean  that it is complicated to manipulate,  and  that  also
 *  therefore means  that it is reserved for developers  and  experienced
 *  professionals having in-depth computer knowledge. Users are therefore
 *  encouraged to load and test the software's suitability as regards their
 *  requirements in conditions enabling the security of their systems and/or 
 *  data to be ensured and,  more generally, to use and operate it in the 
 *  same conditions as regards security. 
 *  
 *  The fact that you are presently reading this means that you have had
 *  knowledge of the CeCILL license and that you accept its terms. 
 *
 ****************************************************************************
 */

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
