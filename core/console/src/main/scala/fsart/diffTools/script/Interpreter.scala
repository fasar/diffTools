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

      inter.bindValue("data1", ioData.file1)
      inter.bindValue("data2", ioData.file2)

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
