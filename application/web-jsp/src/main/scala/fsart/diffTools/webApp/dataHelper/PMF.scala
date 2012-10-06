package fsart.diffTools.webApp.dataHelper



import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

// this is the PersistanceManagerFactory
object PMF {
    private val pmfInstance:PersistenceManagerFactory =
        JDOHelper.getPersistenceManagerFactory("transactions-optional");

    def get():PersistenceManagerFactory = pmfInstance

}