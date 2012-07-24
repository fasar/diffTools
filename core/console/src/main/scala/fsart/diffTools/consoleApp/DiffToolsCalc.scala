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

package fsart.diffTools.consoleApp

import org.apache.commons.logging.{Log, LogFactory}
import annotation.tailrec
import io.Source
import name.fraser.neil.plaintext.diff_match_patch
import scala.collection.JavaConversions._
import java.net.URL
import java.io._
import fsart.diffTools.csvAlgo.CsvTools
import fsart.diffTools.helper.HtmlPages
import fsart.helper.Loader
import fsart.diffTools.converter.ToCSV
import fsart.diffTools.csvModel.CsvData
import java.util.{Calendar, Date, Properties}
import fsart.diffTools.translate.impl.{NextToEachOtherTranslator, OneAboveOtherTranslator, OneLineTranslator}
import fsart.diffTools.outputDriver.Impl._
import fsart.diffTools.translate.TranslatorDescriptor
import fsart.diffTools.script.{ScriptDescriptor, InputOutputData, Interpreter}


object DiffToolsCalc {
  private val log: Log = LogFactory.getLog(this.getClass)

  def main(argsa: Array[String]): Unit = {
    val args = argsa.toList
    log.debug("Application starting with args " + args)

    loadPrivateProperties()

    val nbArgs = args.size
    if (nbArgs == 0) {
      usage(args)
      throw new DiffToolsApplicationException("no arguments")
    }

    args foreach {
      case "-h" => usage(args); System.exit(0)
      case "--help" => usage(args); System.exit(0)
      case _ =>
    }

    val prop = parseCommandLine(args)

    val firstLineAsHeader =
      prop.getProperty("firstLineIsHeader", System.getProperty("firstLineIsHeader", "true")) == "true"

    val dateInit = Calendar.getInstance.getTimeInMillis

    val outFic = getOutFic(prop)
    val out = new FileOutputStream(outFic)
    val extOutFic = getType(outFic.getCanonicalPath)

    val file1String = prop.getProperty("file1")
    val file2String = prop.getProperty("file2")
    log.trace("file 1 property is " + file1String)
    log.trace("file 2 property  is " + file2String)

    val file1URL = getFileUrl(file1String)
    val file2URL = getFileUrl(file2String)
    log.trace("file 1 url is " + file1URL)
    log.trace("file 2 url is " + file2URL)

    testFile(file1URL)
    testFile(file2URL)

    val file1Type = getType(file1String)
    val file2Type = getType(file2String)

    // Read data from input files
    val data1:List[List[String]] =
    if(file1Type == "xls") {
      val toCsv = new ToCSV()
      toCsv.openWorkbook(file1URL)
      var sheetName = getSheetName(file1String)
      toCsv.convertToCSV(sheetName)
      toCsv.getCsvData.map{_.toList}.toList
    } else {
      val buff = Source.fromURL(file1URL)
      buff.getLines().map{_.split(";").toList}.toList
    }
    val data2 =
    if(file2Type == "xls") {
      val toCsv = new ToCSV()
      toCsv.openWorkbook(file2URL)
      var sheetName = getSheetName(file2String)
      toCsv.convertToCSV(sheetName)
      toCsv.getCsvData.map{_.toList}.toList
    } else {
      val buff = Source.fromURL(file2URL)
      buff.getLines().map{_.split(";").toList}.toList
    }


    val dateInitFile = Calendar.getInstance.getTimeInMillis
    log.debug("It takes " + (dateInitFile - dateInitFile) + " secondes to open files")


    // Create Translator
    val kindView = prop.getProperty("kindView", System.getProperty("kindView", "OneLine"))
    log.debug("Output view is : " + kindView)
    val trans = TranslatorDescriptor.getTranslator(kindView)

    // Create Output Driver
    val outputDriver =
      if(extOutFic == "xls" || prop.getProperty("outputType", "").equalsIgnoreCase("excel")) {
        log.debug("Generate the excel output at " + outFic.getCanonicalPath)
        new CsvExcelView2
      } else {
        log.debug("Generate the html output at "  + outFic.getCanonicalPath)
        new CsvHtmlView2
      }

    // Select Script
    val scriptName = prop.getProperty("scriptName", System.getProperty("scriptName", "default"))
    val scriptEngine = ScriptDescriptor.getScriptEngine(scriptName)

    // Create structure data to run the script engine
    val ioData = new InputOutputData(data1, data2, trans, outputDriver, out)
    scriptEngine.process(ioData)

    val dateInitInterpreter = Calendar.getInstance.getTimeInMillis


    // Write data in file
    val excelData = outputDriver.getData()
    out.write(excelData)


    val dateOutputData = Calendar.getInstance.getTimeInMillis
    //log.debug("It takes " + (dateOutputData - dateGenerateCsvDiffData) + " secondes to generate output")
    log.debug("It takes " + (dateOutputData - dateInitFile) + " secondes to generate output")

    out.close
    val dateEnd = Calendar.getInstance.getTimeInMillis

    log.debug("It takes " + (dateInitFile - dateInit) + " secondes to init files")
    //log.debug("It takes " + (dateGenerateCsvData - dateInitFile) + " secondes to create csv data")
    //log.debug("It takes " + (dateGenerateCsvDiffData - dateGenerateCsvData) + " secondes to generate differences")
    //log.debug("It takes " + (dateOutputData - dateGenerateCsvDiffData) + " secondes to generate output in memory")
    log.debug("It takes " + (dateInitInterpreter - dateInit) + " secondes to create interpreter")
    log.debug("It takes " + (dateOutputData - dateInitInterpreter) + " secondes to generate output in memory")
    log.debug("It takes " + (dateEnd - dateOutputData) + " secondes to write data in file")
    log.debug("It takes " + (dateEnd - dateInitFile) + " secondes do the job")
  }








  private def loadPrivateProperties() {
    try {
      // set up new properties object from file "myProperties.txt"
      val fileUrl = Loader.getResource("diffTools.properties");

      val buff = Source.fromURL(fileUrl)
      val p: Properties = new Properties(System.getProperties());
      p.load(buff.reader);

      // set the system properties
      System.setProperties(p);

      val out = new ByteArrayOutputStream();
      val pout = new PrintWriter(out);
      System.getProperties().list(pout); // display new properties
      log.debug("System properties diffTools.properties is loaded.")
      log.debug("File loaded is : " + fileUrl.getFile )
      pout.flush
      val str = new String(out.toByteArray)
      log.trace("New System properties is " + str)
    } catch {
      case e: java.io.FileNotFoundException =>
        log.warn("Can't find diffTools.properties\n" + e.toString + "\n" + e.getStackTraceString)
      case e: java.io.IOException =>
        log.error("I/O failed. " + e.toString + "\n" + e.getStackTraceString)
      case e =>
        log.error("Can't load diffTools.properties\n" + e.toString + "\n" + e.getStackTraceString)
    }
  }


  private def getOutFic(prop: Properties): File = {
    val outputParam = prop.getProperty("output")
    val outputDefault = System.getProperty("outputDefault")
    val fileOutput: URL =
      if (outputParam != null) {
        new File(outputParam).toURI.toURL
      } else if (outputDefault != null) {
        new File(outputDefault).toURI.toURL
      } else {
        new File("output.html").toURI.toURL
      }
    try {
      val outFic = new File(fileOutput.toURI)
      if (outFic.exists() && !outFic.isFile) {
        log.error("output params " + outFic.getCanonicalFile + " is not a file ")
        null
      }
      if (outFic.exists) {
        outFic.delete
      }
      outFic.createNewFile()
      outFic
    } catch {
      case e: Exception =>
        log.error("Can't load file " + fileOutput.toString + "\n" + e.toString + "\n" + e.getStackTraceString)
        System.err.println("Can't create or modify file " + fileOutput.toString)
        throw new DiffToolsApplicationException("Can't load file ", e)
        null
    }
  }

  private def usage(args: Seq[String]): Unit = {
    System.out.println("Usage :  [OPTION] file1 file2")
    System.out.println("Compare two cvs file\n")
    System.out.println("  -h, -help             print this message")
    System.out.println("  -o, --output [FILE]   output file (can be file.html or file.xls)")
    System.out.println("                         if the output is not set, it uses 'outputDefault' in properties file" )
    System.out.println("  -s, --script [File]   script file to use to get another behaviour")
    System.out.println("                         can be " + ScriptDescriptor.getScriptsNames().mkString("(\"", "\", \"", "\")"))
    System.out.println("  -v, --view [View]     kind of view")
    System.out.println("                         can be " + TranslatorDescriptor.getTranslatorsNames.mkString("(\"", "\", \"", "\")"))
    System.out.println("   ")
    System.out.println(" fileX   in case of csv file, put the path of the file ")
    System.out.println("         in case of xls file, put the path of the file and add double dots and the sheet name")
    System.out.println("          if you don't specify sheet name, the first in the sheet file is taken")
    System.out.println("         ex1 -> myCsv1.csv  ;  ex2 -> myExcel.xls:Sheet3")
    System.out.println("\n\n" +
                       " properties file is conf/diffTools.properties file")
  }

  private def parseCommandLine(args: Seq[String]): Properties = {
    @tailrec
    def parseCmdRec(args: List[String], prop: Properties): Properties = {
      args match {
        case file1 :: file2 :: Nil =>
          prop.setProperty("file1", file1)
          prop.setProperty("file2", file2)
          prop
        case "-o" :: path :: tail =>
          prop.setProperty("output", path)
          parseCmdRec(tail, prop)
        case "--output" :: path :: tail =>
          prop.setProperty("output", path)
          parseCmdRec(tail, prop)
        case "-t" :: ouputType :: tail =>
          prop.setProperty("outputType", ouputType)
          parseCmdRec(tail, prop)
        case "--type" :: ouputType :: tail =>
          prop.setProperty("outputType", ouputType)
          parseCmdRec(tail, prop)
        case "-v" :: kindView :: tail =>
          prop.setProperty("kindView", kindView)
          parseCmdRec(tail, prop)
        case "--view" :: kindView :: tail =>
          prop.setProperty("kindView", kindView)
          parseCmdRec(tail, prop)
        case "-s" :: scriptName :: tail =>
          prop.setProperty("scriptName", scriptName)
          parseCmdRec(tail, prop)
        case "--script" :: scriptName :: tail =>
          prop.setProperty("scriptName", scriptName)
          parseCmdRec(tail, prop)
        case x :: tail =>
          parseCmdRec(tail, prop)
        case Nil => prop
      }
    }
    parseCmdRec(args.toList, new Properties())
  }


  private def testFile(fileURL:URL) {
    if (fileURL == null) {
      System.err.println("Can't get file")
      usage(Array(""))
      throw new DiffToolsApplicationException("Can't get file")
    } else {
      val file = new File(fileURL.getFile)
      if(!(file.isFile && file.canRead)) {
        throw new DiffToolsApplicationException("Can't get file : " + fileURL.getFile )
      }
    }
  }


  private def getFileUrl(str:String):URL = {

    val file = getFilePath(str)
    Loader.getFile(file)
  }

  private def getFilePath(str:String):String = {
    val p1 = """^(.:\\.*):([^:])*$""".r
    val p2 = """^(.:\\.*)$""".r
    val p3 = """^(.*):([^:]*)$""".r
    str match {
      case p1(file, sheet) =>
        file
      case p2(file) =>
        file
      case p3(file, sheet) =>
        file
      case file =>
        file
    }
  }

  private def getType(str:String): String = {
    val file = getFilePath(str)
    val tbFic = file.split('.')
    val extFic =
      if(tbFic.size>1) {
        tbFic(tbFic.size - 1).toLowerCase
      } else {
        ""
      }
    extFic
  }

  private def getSheetName(str:String): String = {
    val sep = str.split(':')
    if(sep.size >= 2 ) {
      sep(sep.size-1)
    } else {
      null
    }
  }
}
