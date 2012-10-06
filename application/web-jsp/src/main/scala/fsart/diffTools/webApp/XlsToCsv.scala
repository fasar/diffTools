package fsart.diffTools.webApp



import java.io.IOException
import javax.servlet.http._
import javax.servlet._
import fsart.diffTools.converter.ToCSV
import org.apache.commons.fileupload.FileItemStream
import org.apache.commons.fileupload.FileItemIterator
import org.apache.commons.fileupload.servlet.ServletFileUpload
import java.io.InputStream
import java.io.ByteArrayInputStream
import java.io.IOException
import java.util.logging.Logger
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import java.util.Calendar
import scala.collection.JavaConversions._
import fsart.diffTools.webApp.model.CsvWebData
import fsart.diffTools.webApp.dataHelper.PMF
import javax.jdo.PersistenceManager



class XlsToCsv extends HttpServlet {
  
	private val log:Logger = Logger.getLogger("fsart.diffTools.webApp.XlsToCsv");

  
	private val maxFileSize:Int = 50 * 1024;
	private val maxMemSize:Int = 10 * 1024;
    
	
   
	override
    def doGet(req:HttpServletRequest, resp:HttpServletResponse) {
        throw new ServletException("GET method used with " +
                getClass( ).getName( )+": POST method required.");
	}
	
	
	override
    def doPost(req:HttpServletRequest, resp:HttpServletResponse) {
	  val dateInit = Calendar.getInstance.getTimeInMillis
	  // Check that we have a file upload request
      val isMultipart = ServletFileUpload.isMultipartContent(req);
      
      if( !isMultipart ){
	     resp.setContentType("text/plain");
	     outputError(req, resp, "No file uploaded")
         return;
      }


      try {
	      val upload:ServletFileUpload= new ServletFileUpload();
	      //resp.setContentType("text/plain");
	
	      val iterator:FileItemIterator = upload.getItemIterator(req);
	      while (iterator.hasNext()) {
	        val item:FileItemStream = iterator.next();
	        val stream:InputStream = item.openStream();
	
	        if (item.isFormField()) {
	          log.warning("Got a form field: " + item.getFieldName());
	        } else {
	          // Write something cool
	          log.info("Got an uploaded file: " + item.getFieldName() +
	                      ", name = " + item.getName());
	          val str = "Try to upld file : \n->"+ stream.available() +" octets \n" 
	          log.info(str);
	          val fileName = item.getName()
	          
	          // Open workbook
	          val converter = new ToCSV()
	          converter.openWorkbook(stream)
	          val workBook = converter.getWorkbook()
	          val sheetsNames = getSheetsNames(workBook)
	          
	          // Extract sheetname, convert them in CsvWebData and store 
	          log.info("\nLes sheets sont :");
	          for(name <- sheetsNames) {
	            log.info("  " + name  + " :")
	            converter.convertToCSV(name)
	            val csvData = converter.getCsvData()
	            val csvDataJdo = new CsvWebData
	            csvDataJdo.setFileName(fileName)
	            csvDataJdo.setName(name)
	            csvDataJdo.setData(csvData)
	            
	            //Store them
        		val pm:PersistenceManager = PMF.get().getPersistenceManager();
	            try {
		            pm.makePersistent(csvDataJdo);
		        } finally {
		            pm.close();
		        }
		      }
	          
	        }
	      }
	    } catch {
	      case ex:IOException => 
	        outputError(req, resp, "No file uploaded")
	    	return;
	      case ex:Exception => throw new ServletException(ex);
	    }
	    
	    // forward to the selectSheet page
		val jsp = "/selectSheets"
		val dispatch:RequestDispatcher = req.getRequestDispatcher(jsp);
		dispatch.forward(req, resp); 
	    
      	// calc the time
	    val dateEnd = Calendar.getInstance.getTimeInMillis
	    log.info("It takes " + (dateEnd - dateInit) + " secondes do the job")
	}
	
		    
    private def getSheetsNames(wb:Workbook):Array[String] = {
	  val sheetNb:Int = wb.getNumberOfSheets();
	  var sheetnames:Array[String] = Array.empty[String]
      if(sheetNb > 0 ) {
        for(i:Int <- 0 until sheetNb) {
        	val sheet:Sheet = wb.getSheetAt(i);
        	sheetnames +:= sheet.getSheetName();
        	log.config("I find : " + sheet.getSheetName());
        }
      }
	  sheetnames
    }
    
    private def outputError(req:HttpServletRequest, resp:HttpServletResponse, error:String) {
      	 resp.setContentType("text/html");
	     resp.setCharacterEncoding("UTF-8");
	     val out = resp.getWriter( ); 
	     out.println("<html>");
         out.println("<head>");
         out.println("<title>Servlet upload</title>");  
         out.println("</head>");
         out.println("<body><p>");
         out.println(error); 
         out.println("</p></body>");
         out.println("</p><p><a href=\"/\">return</a></p></body>");
         out.println("</html>");
    }
	    
}
