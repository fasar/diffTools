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

package fsart.lib.scriptEngine


import org.junit.Test
import org.junit.Assert._

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import scala.tools.nsc._
import scala.tools.nsc.interpreter._


/**
 *
 * User: fabien
 * Date: 04/05/12
 * Time: 01:34
 *
 */


class Jsr223Test {

  @Test
  def testHelloWorldRuby() {
    //throws
    def manager: ScriptEngineManager = new ScriptEngineManager();
    def engine: ScriptEngine = manager.getEngineByName("jruby");
    engine.eval("puts \"pipo\"; puts \"poil\"");
  }



  @Test
  def testHelloWorldScalabis() {

    val out = System.out
    val flusher = new java.io.PrintWriter(out)

    val interpreter = {
      val settings:scala.tools.nsc.Settings = new scala.tools.nsc.GenericRunnerSettings( println _ )
      settings.usejavacp.value = true
      new scala.tools.nsc.interpreter.IMain(settings, flusher)
    }

    interpreter.bind("tmp", 10)

    interpreter.interpret("println(2+tmp)");
    // didn't event try to check success or error
    interpreter.close();
  }

  @Test
  def testScalaScript {
      val srcA =
        """
  println("Hello World from srcA")
        """

      val srcB = """ O.foo """

      val srcC = """
  import fsart.lib.scriptEngine.T
  class A extends T{
    def foo = "Hello World from srcC"
    override def toString = "this is A in a src"
  }
                 """


      val out = System.out
      val flusher = new java.io.PrintWriter(out)

      val interpreter = {
        val settings = new scala.tools.nsc.GenericRunnerSettings( println _ )
        settings.usejavacp.value = true
        new scala.tools.nsc.interpreter.IMain(settings, flusher)
      }

      interpreter.interpret(srcA)

      interpreter.bindValue("O", O)

      interpreter.interpret(srcB)

      val func1 = new Function1[String, Unit] {
        def apply(i1:String): Unit = {
          O.bar(i1)
        }
      }
      interpreter.bindValue("Obar", func1 )
      interpreter.interpret("Obar(\"pipo\")")
      //This thing below doesn't work
//      val OBar2 = O.bar(_)
//      interpreter.bindValue("Obar2", OBar2)
//      interpreter.interpret("Obar2(\"pipo\")")

      //interpreter.bind("Trululu", classOf[Trululu])
      //interpreter.bind("T", classOf[T])
      interpreter.compileString(srcC)

      val classA = interpreter.classLoader.findClass("A")

      println(classA)

      val constructors = classA.getDeclaredConstructors
      val myinstance = constructors(0).newInstance()

      println(myinstance)

      myinstance.asInstanceOf[T].foo
  }
}

object O{
  def foo = println("Hello World in object O")
  def bar(tmp:String) = println("Hello World in object O with : " + tmp)
}

class Trululu(val str:String) {
  def pipo { println(str) }
}

trait T{
  def foo:String
}
