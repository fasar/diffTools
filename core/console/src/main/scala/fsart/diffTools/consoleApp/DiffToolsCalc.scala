package fsart.diffTools.consoleApp

import org.apache.commons.logging.{Log, LogFactory}
import java.util.Properties
import annotation.tailrec
import io.Source
import name.fraser.neil.plaintext.diff_match_patch
import scala.collection.JavaConversions._
import java.net.URL
import java.io.{File, FileWriter, BufferedWriter, PrintWriter}
import fsart.helper.{TextTools, Loader}
import fsart.diffTools.model.CsvFile
import fsart.diffTools.helper.HtmlPages

/**
 * Hello world!
 *
 */
object DiffToolsCalc
{
  private val log:Log = LogFactory.getLog(this.getClass)

  def main(argsa: Array[String]):Unit={
    val args = argsa.toList
    log.debug("Application starting with args " + args)

    loadPrivateProperties()

    val nbArgs = args.size
    if(nbArgs==0) {
      usage(args)
      throw new DiffToolsApplicationException("no arguments")
    }

    args foreach {
      case "-h" => usage(args) ; System.exit(0)
      case _ =>
    }

    val prop = parseCommandLine(args)
    val firstLineAsHeader =
      prop.getProperty("firstLineIsHeader", System.getProperty("firstLineIsHeader", "true")) == "true"

    val file1URL = Loader.getFile(prop.getProperty("file1"))
    val file2URL = Loader.getFile(prop.getProperty("file2"))

    val outFic = getOutFic(prop)
    val out:PrintWriter = new PrintWriter(new BufferedWriter(new FileWriter(outFic)));


    log.trace("file 1 is " +  file1URL)
    log.trace("file 2 is " +  file2URL)

    if(file1URL == null) {
      System.err.println("Can't get file1 " + prop.getProperty("file1"))
      usage(args)
      throw new DiffToolsApplicationException("Can't get file1 " + prop.getProperty("file1"))
    }
    if(file2URL == null) {
      System.err.println("Can't get file2 " + prop.getProperty("file2"))
      usage(args)
      throw new DiffToolsApplicationException("Can't get file2 " + prop.getProperty("file2"))
    }

    val bufSrc1 = Source.fromURL(file1URL)
    val bufSrc2 = Source.fromURL(file2URL)
    val linesF1 = bufSrc1.getLines.toList
    val linesF2 = bufSrc2.getLines.toList

    val csv1 = new CsvFile(linesF1, firstLineAsHeader)
    val csv2 = new CsvFile(linesF2, firstLineAsHeader)


    
    log.debug("Process duplicate lines in file1")
    val dupLineF1 = csv1.getDuplicatedKeys
    if(dupLineF1.size >0) {
      println("Duplicated lines in file1 are ")
    }
    for((key, nbDup) <- dupLineF1.toList.sortWith { (elem1, elem2) => elem1._1 < elem2._1 } ) {
      print("   ")
      print(key + "   duplicated " + nbDup + " times")
      val distinctLines = csv1.getLinesOfKey(key).distinct
      if (distinctLines.size > 1) {
        print(" with " + distinctLines.size + " different lines")
      }
      print("\n")
      for(line <- distinctLines) {
        println("     " + line.mkString(csv1.sep))
      }
      if(distinctLines.size>=2)
        println("  Differences are : ")
      for(line <- distinctLines.drop(1)) {
        val resDiffs = TextTools.diffText(distinctLines(0).mkString(csv1.sep), line.mkString(csv1.sep))
        println(TextTools.toHtml(resDiffs))
      }

    }


    
    
    log.debug("Process duplicate lines in file2")
    val dupLineF2 = csv2.getDuplicatedKeys
    if(dupLineF2.size>0)
      println("Duplicated lines in file2 are ")
    for((key, nbDup) <- dupLineF2.toList.sortWith { (elem1, elem2) => elem1._1 < elem2._1 } ) {
      print("   ")
      print(key + "   duplicated " + nbDup + " times")
      val distinctLines2 = csv2.getLinesOfKey(key).distinct
      if (distinctLines2.size > 1) {
        print(" with " + distinctLines2.size + " different lines")
      }
      print("\n")
      for(line <- distinctLines2) {
        println("     " + line.mkString(csv2.sep))
      }
      if(distinctLines2.size>=2)
        println("  Differences are : ")
      for(line <- distinctLines2.drop(1)) {
        val resDiffs = TextTools.diffText(distinctLines2(0).mkString(csv1.sep), line.mkString(csv1.sep))
        println(TextTools.toHtml(resDiffs))
      }
    }

    log.debug("Compare identical keys between both files")
    val listDiffLines:List[List[String]] =
      for(line1 <- csv1.array;
        val key1 = line1(0);
        val lines2 = csv2.getLinesOfKey(key1).distinct;
        line2 <- lines2
      )
      yield {
        log.debug("  " + key1 + " is in both file")
        val res =
          (for((elem1, elem2) <- line1.zip(line2))
          yield {
            val resDiffs = TextTools.diffText(elem1, elem2)
            val resHtml = TextTools.toHtml(resDiffs)
            resHtml
          }).toList
        res
      }
    log.debug("Generate list of added lines in file1")
    val listAddedLines:List[List[String]] =
      for(line1 <- csv1.array;
        val key1 = line1(0);
        if(!csv2.getKeys.contains(key1))
      )
      yield {
        log.debug("  " + key1 + " is added in file1 ")
        val res =
          (for(elem1 <- line1)
          yield {
            val resDiffs = TextTools.diffText(elem1, "")
            val resHtml = TextTools.toHtml(resDiffs)
            resHtml
          }).toList
        res
      }

    log.debug("Generate list of supprimed lines in file1")
    val listSupprimedLines:List[List[String]] =
      for(line2 <- csv2.array;
        val key2 = line2(0);
        if(!csv1.getKeys.contains(key2))
      )
      yield {
        log.debug("  " + key2 + " is supprimed in file1 ")
        val res =
          (for(elem2 <- line2)
          yield {
            val resDiffs = TextTools.diffText("", elem2)
            val resHtml = TextTools.toHtml(resDiffs)
            resHtml
          }).toList
        res
      }

    log.debug("Generate the output")
    val htmlPage = new HtmlPages()
    htmlPage.body.append("  <TABLE id=\"id_table1\" cellpadding='0' cellspacing='0' >")
    if(firstLineAsHeader) {
      htmlPage.body.append(" <thead>\n  <TR>\n")
      for(elem <- csv1.headers) {
        htmlPage.body.append("  <TD>")
        htmlPage.appendToBodyEscapeChar(elem)
        htmlPage.body.append("</TD>\n")
      }
      htmlPage.body.append("  </TR>\n </thead>\n")
    }
    htmlPage.body.append(" <tbody>\n")
    for(lines <- listAddedLines:::listSupprimedLines:::listDiffLines ) {
      htmlPage.body.append("  <TR>\n")
      for (col <- lines) {
        htmlPage.body.append("    <TD>")
        if(col.size>0)
          htmlPage.body.append(col)
        else
          htmlPage.body.append("&nbsp;")
        htmlPage.body.append("</TD>\n")
      }
      htmlPage.body.append("  </TR>\n")
    }
    htmlPage.body.append(" </tbody>\n")
    htmlPage.body.append("</Table>")
    out.print(htmlPage.toHtml)
    out.close
  }

  private def loadPrivateProperties() {
    try {
      // set up new properties object from file "myProperties.txt"
      val fileUrl = Loader.getResource("diffTools.properties");
      val buff = Source.fromURL(fileUrl)
      val p:Properties = new Properties(System.getProperties());
      p.load(buff.reader);

      // set the system properties
      System.setProperties(p);
      System.getProperties().list(System.out);    // display new properties
      log.debug("System properties diffTools.properties is loaded.")
    } catch {
      case e:java.io.FileNotFoundException =>
        log.warn("Can't find diffTools.properties\n" + e.toString + "\n" + e.getStackTraceString)
      case e:java.io.IOException =>
        log.error("I/O failed. " + e.toString + "\n" + e.getStackTraceString)
      case e =>
        log.error("Can't load diffTools.properties\n" + e.toString + "\n" + e.getStackTraceString)
    }
  }


  private def getOutFic(prop:Properties):File = {
    val outputParam = prop.getProperty("output")
    val outputDefault = System.getProperty("outputDefault")
    val fileOutput:URL =
      if(outputParam!=null) {
        new File(outputParam).toURI.toURL
      } else if(outputDefault!=null) {
        new File(outputDefault).toURI.toURL
      } else {
        new File("output.html").toURI.toURL
      }
    try {
      val outFic = new File(fileOutput.toURI)
      if(outFic.exists() && !outFic.isFile) {
        log.error("output params "+ outFic.getCanonicalFile +" is not a file ")
        null
      }
      if(outFic.exists) {
        outFic.delete
      }
      outFic.createNewFile()
      outFic
    } catch {
      case e:Exception =>
        log.error("Can't load file " + fileOutput.toString + "\n" + e.toString + "\n" + e.getStackTraceString)
        System.err.println("Can't create or modify file " + fileOutput.toString)
        throw new DiffToolsApplicationException("Can't load file ", e)
        null
    }
  }
  private def usage (args: Seq[String] ): Unit = {
    System.out.println ("Usage :  [OPTION] file1 file2")
    System.out.println ("Compare two cvs file\n")
    System.out.println ("  -h, -help             print this message")
    System.out.println ("  -o, --output          output file")
    System.out.println ("  -nh, --noheaders      if csv files doesn't have headers")
    System.out.println ("  -h, --headers         if csv files have headers")
  }

  private def parseCommandLine(args:Seq[String]): Properties = {
    @tailrec
    def parseCmdRec(args:List[String], prop:Properties): Properties = {
      args match {
        case file1::file2::Nil =>
          prop.setProperty("file1", file1)
          prop.setProperty("file2", file2)
          prop
        case "-o"::path::tail =>
          prop.setProperty("output", path)
          parseCmdRec(tail, prop)
        case "--output"::path::tail =>
          prop.setProperty("output", path)
          parseCmdRec(tail, prop)
        case "-nh"::path::tail =>
          prop.setProperty("firstLineIsHeader", "false")
          parseCmdRec(tail, prop)
        case "--noheaders"::path::tail =>
          prop.setProperty("firstLineIsHeader", "false")
          parseCmdRec(tail, prop)
        case "-h"::path::tail =>
          prop.setProperty("firstLineIsHeader", "true")
          parseCmdRec(tail, prop)
        case "--headers"::path::tail =>
          prop.setProperty("firstLineIsHeader", "true")
          parseCmdRec(tail, prop)
        case x::tail =>
          parseCmdRec(tail, prop)
        case Nil => prop
      }
    }
    parseCmdRec(args.toList, new Properties())
  }
}
