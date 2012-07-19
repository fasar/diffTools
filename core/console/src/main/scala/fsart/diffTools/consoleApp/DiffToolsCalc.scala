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
import fsart.diffTools.CsvBuilder.CsvFactory
import fsart.diffTools.csvAlgo.CsvTools
import fsart.diffTools.view.CsvView
import fsart.diffTools.helper.HtmlPages
import fsart.diffTools.view.CsvView
import fsart.diffTools.view.Impl.{CsvExcelView, CsvHtmlView, CsvExcelDoubleCellView}
import fsart.helper.Loader
import fsart.diffTools.converter.ToCSV
import fsart.diffTools.csvModel.CsvData
import java.util.{Calendar, Date, Properties}


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
      case _ =>
    }

    val prop = parseCommandLine(args)
    val firstLineAsHeader =
      prop.getProperty("firstLineIsHeader", System.getProperty("firstLineIsHeader", "true")) == "true"

    val dateInit = Calendar.getInstance.getTimeInMillis

    val outFic = getOutFic(prop)
    val out = new FileOutputStream(outFic)
    val tbOutFic = outFic.getName.split('.')
    val extOutFic =
      if(tbOutFic.size>1) {
        tbOutFic(tbOutFic.size - 1).toLowerCase
      } else {
        "html"
      }

    val file1String = prop.getProperty("file1")
    val file1Type = getType(file1String)
    val file1URL = getFileUrl(file1String)
    val file2String = prop.getProperty("file2")
    val file2Type = getType(file2String)
    val file2URL = getFileUrl(file2String)
    log.trace("file 1 property is " + file1String)
    log.trace("file 1 url is " + file1URL)
    log.trace("file 2 property  is " + file2String)
    log.trace("file 2 url is " + file2URL)

    testFile(file1URL)
    testFile(file2URL)

    val datas1:List[List[String]] =
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
    val datas2 =
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

    //get duplicated keys and duplicated lines

    val dateInitFile = Calendar.getInstance.getTimeInMillis
    log.debug("It takes " + (dateInitFile - dateInitFile) + " secondes to open files")

    import fsart.diffTools.CsvDsl.CsvBuilderDsl._
    val csv1 = datas1 toCsv() firstLineAsHeader(firstLineAsHeader)
    val csv2 = datas2 toCsv() firstLineAsHeader(firstLineAsHeader)

    val dateGenerateCsvData = Calendar.getInstance.getTimeInMillis
    log.debug("It takes " + (dateGenerateCsvData - dateInitFile) + " secondes to create csv datas")

    import fsart.diffTools.CsvDsl.CsvRulesDsl._
    log.debug("Generate differences between two files")

    val csvDiff:DiffData = modificationsMade by csv2 withRef csv1
    val csvAdd:DiffData = additionsMade by csv2 withRef csv1
    val csvSuppr:DiffData = suppressionsMade by csv2 withRef csv1
    val csvRes =  csvDiff concatWith csvSuppr concatWith csvAdd

    val dateGenerateCsvDiffData = Calendar.getInstance.getTimeInMillis
    log.debug("It takes " + (dateGenerateCsvDiffData - dateGenerateCsvData) + " secondes to generate differences")

    if(extOutFic == "html") {
      log.debug("Generate the html output at "  + outFic.getCanonicalPath)
      val htmlPage = CsvHtmlView.getView(csvRes)
      out.write(htmlPage)
    } else if (extOutFic == "xls") {
      log.debug("Generate the excel output at " + outFic.getCanonicalPath)
      val excelView = new CsvExcelDoubleCellView // CsvExcelView
      val excelData = excelView.getView(csvRes)
      out.write(excelData)
    } else {
      log.error("No output selected")
    }

    val dateOutputData = Calendar.getInstance.getTimeInMillis
    log.debug("It takes " + (dateOutputData - dateGenerateCsvDiffData) + " secondes to generate output")

    out.close
    val dateEnd = Calendar.getInstance.getTimeInMillis

    log.debug("It takes " + (dateInitFile - dateInit) + " secondes to init files")
    log.debug("It takes " + (dateGenerateCsvData - dateInitFile) + " secondes to create csv datas")
    log.debug("It takes " + (dateGenerateCsvDiffData - dateGenerateCsvData) + " secondes to generate differences")
    log.debug("It takes " + (dateOutputData - dateGenerateCsvDiffData) + " secondes to generate output in memory")
    log.debug("It takes " + (dateEnd - dateOutputData) + " secondes to write datas in file")
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
    System.out.println("  -o, --output          output file (can be file.html or file.xls)")
    System.out.println("                         if the output is not set, it uses 'outputDefault' in properties file" )
    System.out.println("  -nh, --noheaders      if csv files doesn't have headers")
    System.out.println("  -h, --headers         if csv files have headers")
    System.out.println("                         if the header is not set, it uses 'firstLineIsHeader' in properties file")
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
        case "-nh" :: path :: tail =>
          prop.setProperty("firstLineIsHeader", "false")
          parseCmdRec(tail, prop)
        case "--noheaders" :: path :: tail =>
          prop.setProperty("firstLineIsHeader", "false")
          parseCmdRec(tail, prop)
        case "-h" :: path :: tail =>
          prop.setProperty("firstLineIsHeader", "true")
          parseCmdRec(tail, prop)
        case "--headers" :: path :: tail =>
          prop.setProperty("firstLineIsHeader", "true")
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
