package fsart.diffTools.webApp

import javax.servlet.http._
import java.io.IOException
import javax.jdo.PersistenceManager
import scala.collection.JavaConversions._
import fsart.diffTools.csvModel.CsvData
import fsart.diffTools.webApp.dataHelper.PMF
import fsart.diffTools.webApp.model.CsvWebData
import fsart.diffTools.csvModel.CsvData
import fsart.diffTools.csvModel.CsvDataImpl
import fsart.diffTools.csvModel.CsvDataFirstLineAsHeaderImpl
import java.util.Calendar
import java.util.logging.Logger
import fsart.diffTools.csvAlgo.CsvTools
import name.fraser.neil.plaintext.diff_match_patch
import fsart.diffTools.csvModel.CsvDataConcatenedImpl
import fsart.diffTools.outputDriver.Impl.CsvExcelView2
import fsart.diffTools.translate.TranslatorDescriptor
import fsart.diffTools.outputDriver.Impl.CsvExcelView2SpecialAppEngine
import javax.jdo.Query
import fsart.diffTools.webApp.dataHelper.CsvWebDataDao


class CompareFiles extends HttpServlet {
   
	private val log:Logger = Logger.getLogger("fsart.diffTools.webApp.XlsToCsv");
	private var pm:PersistenceManager = _ 
  
  	override
    def doGet(req:HttpServletRequest, resp:HttpServletResponse) {
  		val dateInit = Calendar.getInstance.getTimeInMillis
  		
        resp.setContentType("application/vnd.ms-excel");
  		resp.setHeader("Content-Disposition","attachment; filename=\"output.xls\"")
  		val out = resp.getOutputStream();    
      
        // Get sheet id 
        val (sheetId1,sheetId2) = 
        try {
        	val sheetId1 = java.lang.Long.decode(req.getParameter("sheetId1"))
        	val sheetId2 = java.lang.Long.decode(req.getParameter("sheetId2"))
        	(sheetId1, sheetId2)
        } catch {
          case e:NumberFormatException => 
            throw e
        }
        
        // get csv data from jdo
        pm = PMF.get().getPersistenceManager();
        val sheet1 = getSheet(sheetId1)
        val sheet2 = getSheet(sheetId2)
        pm.close()
        
        // Compare data
        
        val modified: CsvData[List[diff_match_patch.Diff]] = CsvTools.getDifferenceLines(sheet1, sheet2)
        val suppressed: CsvData[List[diff_match_patch.Diff]] = CsvTools.getSupprimedLines(sheet1, sheet2)
        val added: CsvData[List[diff_match_patch.Diff]] = CsvTools.getAddedLines(sheet1, sheet2)
        val res = CsvDataConcatenedImpl(CsvDataConcatenedImpl(modified, suppressed), added)
        
        // Set the translator
        val trans = TranslatorDescriptor.getTranslator("NextToEachOther")
        
        
        // Output compared data
        val transRes = trans.translate(res)
        val outputDriver = new CsvExcelView2SpecialAppEngine
        outputDriver.addCsvTable("Comparison1", transRes)
        
        // output the file
        // Write data in file
    	val excelData = outputDriver.getData()
    	//out.println("Data are : ")
    	out.write(excelData)
        //out.println("End")
        
        // Raz datastore if asked
    	val razDS = req.getParameter("razDatastore")
    	if(razDS!=null && !razDS.isEmpty() && razDS == "true") {
    		CsvWebDataDao.raz
    	}
        
        
	    val dateEnd = Calendar.getInstance.getTimeInMillis
	    log.info("It takes " + (dateEnd - dateInit) + " secondes do the job")
	    //out.println("It tooks " + (dateEnd - dateInit) + " secondes do the job")
  	}
  	
  	
  	private def getSheet(sheetId:java.lang.Long): CsvData[String] = {
  	  val csv:CsvWebData = CsvWebDataDao.getCsvWebData(sheetId)
  	  val data:List[List[String]] = csv.getData.map{_.toList}.toList
  	  val csvData1 = CsvDataImpl(data, null)
  	  val csvDataWithHeader = CsvDataFirstLineAsHeaderImpl(csvData1)
  	  csvDataWithHeader
  	}
  	
}