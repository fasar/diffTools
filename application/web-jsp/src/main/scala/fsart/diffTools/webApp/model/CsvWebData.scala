package fsart.diffTools.webApp.model

import com.google.appengine.api.datastore.Key
import java.util.Date
import javax.jdo.annotations.IdGeneratorStrategy
import javax.jdo.annotations.PersistenceCapable
import javax.jdo.annotations.Persistent
import javax.jdo.annotations.PrimaryKey
import scala.reflect.BeanProperty
import com.google.appengine.api.datastore.Text
import com.google.appengine.api.datastore.Blob
import java.io.OutputStream
import java.io.ObjectOutputStream
import java.io.ByteArrayOutputStream
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream

@PersistenceCapable
class CsvWebData {

    @BeanProperty
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    var key:java.lang.Long = _

    @BeanProperty
    @Persistent
    var fileName:String = ""
      
    @BeanProperty
    @Persistent
    var name:String = ""
    
    @Persistent
    var data:Blob = _

    def setData(data:java.util.ArrayList[java.util.ArrayList[String]]) {
        val os:ByteArrayOutputStream = new ByteArrayOutputStream();
        val oos:ObjectOutputStream = new ObjectOutputStream(os);

        oos.writeObject(data);
        oos.close();
        os.close
        this.data = new Blob(os.toByteArray)
    }
    
    def setData(value:Blob){
      this.data = value
    }
    
    def getData():java.util.ArrayList[java.util.ArrayList[String]] = {
      if(data != null) {
		val is:ByteArrayInputStream = new ByteArrayInputStream(data.getBytes());
		val ois:ObjectInputStream = new ObjectInputStream(is);
		
		val res= ois.readObject().asInstanceOf[java.util.ArrayList[java.util.ArrayList[String]]]
		
		ois.close();
		is.close
		res
      } else {
        val res = new java.util.ArrayList[java.util.ArrayList[String]]()
        res.add(new java.util.ArrayList[String]())
        res.get(0).add("")
        res
      }
    }
    
}