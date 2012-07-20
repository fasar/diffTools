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
