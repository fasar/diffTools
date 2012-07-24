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
    val data1 = ioData.data1
    val data2 = ioData.data2
    val trans = ioData.translator
    val outputDriver = ioData.outputDriver

    val dateInitFile = Calendar.getInstance.getTimeInMillis

    import fsart.diffTools.csvDsl.CsvBuilderDsl._
    val csv1:CsvData[String] = data1 toCsv() firstLineAsHeader(true)//(firstLineAsHeader)
    val csv2:CsvData[String] = data2 toCsv() firstLineAsHeader(true)//(firstLineAsHeader)

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
