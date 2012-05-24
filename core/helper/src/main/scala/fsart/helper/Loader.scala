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

package fsart.helper


import java.lang.reflect.{Method, InvocationTargetException}
import java.net.URL
import org.apache.commons.logging.{LogFactory, Log}
import java.io.{File, InterruptedIOException}

object Loader {

  private val log:Log = LogFactory.getLog(this.getClass)

  val TSTR:String = "Caught Exception while in Loader.getResource. This may be innocuous.";


  def getResource(resource:String):URL = {
    var classLoader:ClassLoader = null;
    var url:URL = null;

    try {
      classLoader = getTCL();
      log.debug("Trying to find ["+resource+"] using context classloader " + classLoader + ".");
      url = classLoader.getResource(resource);
      if(url != null) {
        return url;
      }

      // We could not find resource. Ler us now try with the
      // classloader that loaded this class.
      classLoader = this.getClass.getClassLoader
      if(classLoader != null) {
        log.debug("Trying to find ["+resource+"] using "+classLoader +" class loader.");
        url = classLoader.getResource(resource);
        if(url != null) {
          return url;
        }
  	  }
    } catch {
      case t:IllegalAccessException  =>
        log.warn(TSTR, t);
      case t:InvocationTargetException =>
        if (t.getTargetException().isInstanceOf[InterruptedException]
                || t.getTargetException().isInstanceOf[InterruptedIOException] ) {
            Thread.currentThread().interrupt();
        }
        log.warn(TSTR, t);
      case t:Throwable =>
      //
      //  can't be InterruptedException or InterruptedIOException
      //    since not declared, must be error or RuntimeError.
      log.warn(TSTR, t);
    }

    // Last ditch attempt: get the resource from the class path. It
    // may be the case that clazz was loaded by the Extentsion class
    // loader which the parent of the system class loader. Hence the
    // code below.
    log.debug("Trying to find ["+resource+"] using ClassLoader.getSystemResource().");

    return ClassLoader.getSystemResource(resource);
  }


  def getFile(filePath:String): URL = {
    val resUri = getResource(filePath)
    if(resUri == null) {
      try {
        val fic = new File(filePath)
        if(fic!=null && fic.exists()) {
          fic.toURI.toURL
        } else {
          null
        }
      } catch {
        case e:Exception =>
          log.error("Can't get file " + filePath + ".\n" + e.toString + "\n" + e.getStackTraceString)
          null
      }
    } else {
      resUri
    }
  }

    /**
    * Get the Thread Context Loader which is a JDK 1.2 feature. If we
    * are running under JDK 1.1 or anything else goes wrong the method
    * returns <code>null<code>.
    *
    *  */
  private def getTCL():ClassLoader  = {
    Thread.currentThread.getContextClassLoader
  }

}
