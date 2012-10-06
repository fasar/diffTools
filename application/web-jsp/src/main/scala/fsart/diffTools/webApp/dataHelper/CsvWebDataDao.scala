package fsart.diffTools.webApp.dataHelper

import javax.jdo.Query
import fsart.diffTools.webApp.model.CsvWebData

object CsvWebDataDao {

  private val pm = PMF.get.getPersistenceManager()
  
  def raz() {
	  val query:Query = pm.newQuery(classOf[CsvWebData]);
	  query.deletePersistentAll();
	  query.execute()
  }
  
  def getCsvWebData(sheetId:java.lang.Long):CsvWebData = {
    val csv:CsvWebData = pm.getObjectById(classOf[CsvWebData], sheetId);
    csv
  }
}