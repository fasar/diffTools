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

package fsart.diffTools.script.scriptEngineImpl

import fsart.diffTools.csvModel.CsvData
import java.util.Calendar
import org.apache.commons.logging.{LogFactory, Log}
import fsart.diffTools.script.{Interpreter, InputOutputData, ScriptEngine}


/**
 *
 * User: fabien
 * Date: 20/07/12
 * Time: 18:42
 *
 */

object DefaultScriptEngine extends ScriptEngine {

  private val log: Log = LogFactory.getLog(this.getClass)


  def process(ioData: InputOutputData) {
    val file1 = ioData.file1
    val file2 = ioData.file2
    val trans = ioData.translator
    val outputDriver = ioData.outputDriver

    val dateInitFile = Calendar.getInstance.getTimeInMillis

    import fsart.diffTools.csvDsl.CsvBuilderDsl._
    val csv1:CsvData[String] = file1 toCsv() firstLineAsHeader(true)//(firstLineAsHeader)
    val csv2:CsvData[String] = file2 toCsv() firstLineAsHeader(true)//(firstLineAsHeader)

    val dateGenerateCsvData = Calendar.getInstance.getTimeInMillis
    log.debug("It takes " + (dateGenerateCsvData - dateInitFile) + " secondes to create csv data")

    import fsart.diffTools.csvDsl.CsvRulesDsl._
    log.debug("Generate differences between two files")

    val csvDiff:DiffData = modificationsMade by csv2 withRef csv1
    val csvAdd:DiffData = additionsMade by csv2 withRef csv1
    val csvSuppr:DiffData = suppressionsMade by csv2 withRef csv1
    val csvRes =  csvDiff concatWith csvSuppr concatWith csvAdd

    val dateGenerateCsvDiffData = Calendar.getInstance.getTimeInMillis
    log.debug("It takes " + (dateGenerateCsvDiffData - dateGenerateCsvData) + " secondes to generate differences")

    val myres = trans.translate(csvRes)
    outputDriver.addCsvTable("Comparison1", myres)

  }
}
