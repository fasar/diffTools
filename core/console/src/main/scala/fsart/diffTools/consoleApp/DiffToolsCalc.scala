/**
 * Copyright Fabien Sartor 
 * Contributors: Fabien Sartor (fabien.sartor@gmail.com)
 *  
 * This software is a computer program whose purpose to compate two 
 * files.
 *
 */
/**
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
 */
package fsart.diffTools.consoleApp

import org.apache.commons.logging.{Log, LogFactory}
import java.util.Properties
import annotation.tailrec
import io.Source
import name.fraser.neil.plaintext.diff_match_patch
import scala.collection.JavaConversions._
import java.net.URL
import java.io._
import fsart.diffTools.model.CsvFactory
import fsart.diffTools.model.helper.CsvTools
import fsart.diffTools.helper.{CsvView, HtmlPages}
import fsart.diffTools.helper.Impl.{CsvExcelView, CsvHtmlView}
import fsart.helper.{TextTools, Loader}
import fsart.diffTools.converter.ToCSV



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
    val file1URL = getFile(file1String)
    val file2String = prop.getProperty("file2")
    val file2Type = getType(file2String)
    val file2URL = getFile(file2String)
    log.trace("file 1 is " + file1URL)
    log.trace("file 2 is " + file2URL)

    testFile(file1URL)
    testFile(file2URL)

    val csv1 =
    if(file1Type == "xls") {
      val toCsv = new ToCSV()
      toCsv.openWorkbook(file1URL)
      var sheetName = getSheetName(file1String)
      toCsv.convertToCSV(sheetName)
      CsvFactory.getCsvData(toCsv.getCsvData.map{_.toList}.toList, firstLineAsHeader)
    } else {
      CsvFactory.getCsvFile(file1URL, firstLineAsHeader)
    }
    val csv2 =
    if(file2Type == "xls") {
      val toCsv = new ToCSV()
      toCsv.openWorkbook(file2URL)
      var sheetName = getSheetName(file2String)
      toCsv.convertToCSV(sheetName)
      CsvFactory.getCsvData(toCsv.getCsvData.map{_.toList}.toList, firstLineAsHeader)
    } else {
      CsvFactory.getCsvFile(file2URL, firstLineAsHeader)
    }


//
//
//    log.debug("Process duplicate lines in file1")
//    val dupLineF1 = csv1.getDuplicatedKeys
//    if (dupLineF1.size > 0) {
//      println("Duplicated lines in file1 are ")
//    }
//    for ((key, nbDup) <- dupLineF1.toList.sortWith {
//      (elem1, elem2) => elem1._1 < elem2._1
//    }) {
//      print("   ")
//      print(key + "   duplicated " + nbDup + " times")
//      val distinctLines = csv1.getLinesOfKey(key).distinct
//      if (distinctLines.size > 1) {
//        print(" with " + distinctLines.size + " different lines")
//      }
//      print("\n")
//      for (line <- distinctLines) {
//        println("     " + line.mkString(csv1.separator))
//      }
//      if (distinctLines.size >= 2)
//        println("  Differences are : ")
//      for (line <- distinctLines.drop(1)) {
//        val resDiffs = TextTools.diffText(distinctLines(0).mkString(csv1.separator), line.mkString(csv1.separator))
//        println(TextTools.toHtml(resDiffs))
//      }
//
//    }
//
//
//
//
//    log.debug("Process duplicate lines in file2")
//    val dupLineF2 = csv2.getDuplicatedKeys
//    if (dupLineF2.size > 0)
//      println("Duplicated lines in file2 are ")
//    for ((key, nbDup) <- dupLineF2.toList.sortWith {
//      (elem1, elem2) => elem1._1 < elem2._1
//    }) {
//      print("   ")
//      print(key + "   duplicated " + nbDup + " times")
//      val distinctLines2 = csv2.getLinesOfKey(key).distinct
//      if (distinctLines2.size > 1) {
//        print(" with " + distinctLines2.size + " different lines")
//      }
//      print("\n")
//      for (line <- distinctLines2) {
//        println("     " + line.mkString(csv2.separator))
//      }
//      if (distinctLines2.size >= 2)
//        println("  Differences are : ")
//      for (line <- distinctLines2.drop(1)) {
//        val resDiffs = TextTools.diffText(distinctLines2(0).mkString(csv1.separator), line.mkString(csv1.separator))
//        println(TextTools.toHtml(resDiffs))
//      }
//    }

    log.debug("Generate differences between two files")
    val csvDiff = CsvTools.getDifferenceLines(csv1, csv2)
    val csvAdd = CsvTools.getAddedLines(csv1, csv2)
    val csvSuppr = CsvTools.getASupprimedLines(csv1, csv2)


    val csvRes = CsvTools.concat(CsvTools.concat(csvDiff, csvSuppr ), csvAdd)
    if(extOutFic == "html") {
      log.debug("Generate the html output")
      val htmlPage = CsvHtmlView.getView(csvRes)
      out.write(htmlPage)
    }else if (extOutFic == "xls") {
      log.debug("Generate the excel output")
      val excelView = new CsvExcelView
      val excelData = excelView.getView(csvRes)
      out.write(excelData)
    } else {
      log.error("No output selected")
    }

    out.close

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
      System.err.println("Can't get file1 " + fileURL.getFile)
      usage(Array(""))
      throw new DiffToolsApplicationException("Can't get file1 " + fileURL.getFile)
    }
  }


  private def getFile(str:String):URL = {
    val file = str.split(':')(0)
    Loader.getFile(file)
  }


  private def getType(str:String): String = {
    val file = str.split(':')(0)
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
      sep(1)
    } else {
      null
    }
  }
}
