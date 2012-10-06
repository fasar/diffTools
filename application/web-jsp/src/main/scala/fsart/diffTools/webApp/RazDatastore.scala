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
import javax.servlet.RequestDispatcher


class RazDatastore extends HttpServlet {
   
	private val log:Logger = Logger.getLogger("fsart.diffTools.webApp.XlsToCsv");

	
  	override
    def doGet(req:HttpServletRequest, resp:HttpServletResponse) {
  	  CsvWebDataDao.raz  
  	  val jsp = "/"
      val dispatch:RequestDispatcher = req.getRequestDispatcher(jsp);
      dispatch.forward(req, resp); 
  	}
  	
}