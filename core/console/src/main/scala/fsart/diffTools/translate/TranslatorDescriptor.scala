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

package fsart.diffTools.translate


import impl.{NextToEachOtherTranslator, OneAboveOtherTranslator, OneLineTranslator}
import java.lang.reflect.{Method, InvocationTargetException}
import java.net.URL
import java.io.File
import scala.collection.JavaConversions._

/**
 *
 * User: fabien
 * Date: 20/07/12
 * Time: 09:33
 *
 */

/**
 * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
 *
 * @param packageName The base package
 * @return The classes
 * @throws ClassNotFoundException
 * @throws IOException
 */
object TranslatorDescriptor {

  def getTranslatorsNames:java.util.List[String] = {
    List("One line", "One above other", "Next to each other")
  }


  def getTranslator(name:String):Translator = {
    name match {
      case "One line" => new OneLineTranslator
      case "OneLine" => new OneLineTranslator
      case "One above other" => new OneAboveOtherTranslator
      case "OneAboveOther" => new OneAboveOtherTranslator
      case "Next to each other" => new NextToEachOtherTranslator
      case "NextToEachOther" => new NextToEachOtherTranslator
      case _ => new OneLineTranslator
    }
  }


}
