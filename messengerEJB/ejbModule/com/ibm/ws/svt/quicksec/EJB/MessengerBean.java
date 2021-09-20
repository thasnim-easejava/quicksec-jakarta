package com.ibm.ws.svt.quicksec.EJB;
import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.TransactionManagement;
import jakarta.inject.Inject;
//import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.security.enterprise.SecurityContext;
import jakarta.transaction.UserTransaction;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;
import jakarta.persistence.LockModeType;
/**
 * Bean implementation class for Enterprise Bean: Messenger
 */
@RolesAllowed({"messengerUsers","manager"})
@Stateless
//@Local (Messenger.class)
@Remote (Messenger.class)
@TransactionManagement(jakarta.ejb.TransactionManagementType.BEAN)
@Resource(name="jdbc/Messenger", type=javax.sql.DataSource.class, mappedName="jdbc/Messenger")
public class MessengerBean implements Messenger {
	
	/**   getSessionContext  **/
	
	//Inject security context JEE8 update
	@Inject
	private SecurityContext securityContext;
	
    @Resource
	private jakarta.ejb.SessionContext mySessionCtx;
	
	/*@PersistenceUnit(unitName="MessengerEnt")
	private EntityManagerFactory emf; */
    // This is using container Managed Persistence
	@PersistenceContext (unitName="MessengerEnt")
	private EntityManager em;
	boolean isManager;
	long i= 0;
	boolean print_out = false;
	boolean db_disable = false;

public String getMessageMgrOnly()
{
	String principalname;
	if (mySessionCtx.isCallerInRole("manager")) 
		 principalname= getMessage();
	else 
		 //principalname=mySessionCtx.getCallerPrincipal().getName()+ "not Manager";
		 principalname=securityContext.getCallerPrincipal().getName()+ "not Manager";
	return (principalname);
}

 public String getMessage ()
 {
	 String userId = null;
	 String userId1 = null;
	 String principalname = null;
     String result;
	 try {
		 
		 if(print_out) System.out.println("quickSecMessengerEJB: In getMsg of messengerEJB.. count" + ++i + " User: " + mySessionCtx.getCallerPrincipal().getName());
		// userId1 = mySessionCtx.getCallerPrincipal().getName();
		 userId=securityContext.getCallerPrincipal().getName();
		//if (mySessionCtx.isCallerInRole("manager"))
		if (securityContext.isCallerInRole("manager"))
			isManager = true;
		else
			isManager = false;

		principalname = "MessengerBean: getMessage: Caller Identity is ..." + userId;
		//principalname = "MessengerBean: getMessage: Caller Identity is ...securityContext.getCallerPrincipal().getName()    " + userId + "... mySessionCtx.getCallerPrincipal().getName()  "+ userId1;

	 }catch ( Exception e ) {
		 e.printStackTrace();
		 System.out.println("MessengerBean: getMessage: Exception occured in getMessage.");
	 }
	if (!db_disable) {
		try {
		  //initConnection();
			MessengerEnt messngr= em.find(MessengerEnt.class,userId);
		 if (messngr == null) {
			 if(print_out) System.out.println("MessengerBean: getMessage: Logging the new user in DB  " + userId);
			 result = insert(userId, isManager);
		 } else {
			 if(print_out) System.out.println("MessengerBean: getMessage: Updating the userCount for user in DB " + userId);
			 result = update(userId, isManager);
		 }
		 if(print_out) System.out.println(result +" for " + userId);
		 em.clear();
		}
		catch ( Exception e )
		{   e.printStackTrace();
		   System.out.println("MessengerBean: getMessage: Exception occured in getMessage.");
		   principalname = principalname +  " , Database Exception: "+ e.getMessage();
		}
	 }
	   return principalname;
 }

 public String insert(String userId, Boolean isManager) throws Exception {
		String result = "Entity inserted into DB";
		UserTransaction ut=(UserTransaction) getSessionContext().getUserTransaction();
		
		try {
		
			// make the Entity.
			MessengerEnt messngr = new MessengerEnt();
			messngr.setuserId(userId);
			messngr.setuserCount(1);
			messngr.setisManager(isManager);
			
			// start a transaction.
			//EntityTransaction tx = em.getTransaction();
			ut.begin();
			em.joinTransaction();
			
			// save the Entity
			em.persist(messngr);
			// Added the 2 lines below for debugging the defect 89762
			 // org.apache.openjpa.kernel.Broker b = ((org.apache.openjpa.persistence.EntityManagerImpl)em.getDelegate()).getBroker(); ;
			  //System.out.println( "*** broker="+b + ", managed="+b.getManagedObjects()+", loaded=" + b.getStateManager(messngr).getLoaded());
			ut.commit();
			em.clear();

		} catch(EntityNotFoundException e){
			System.out.println("EntityExistsException happened in insert");
		} catch(Exception e) {
			//result = "Failed: Exception :"+e.getLocalizedMessage();
			//e.printStackTrace();
			System.out.println("MessengerBean: insert: Exception occurred in insert.");
			throw e;
		}
		
		return result;
	}
	public String update(String userId, Boolean isManager) throws Exception {
		String result = "Entity updated into DB";
		UserTransaction ut= null;
			ut=(UserTransaction) getSessionContext().getUserTransaction();
			// start a transaction.
			ut.begin();
			MessengerEnt messngr= em.find(MessengerEnt.class,userId);
			em.joinTransaction();
			//Below 2 lines (added em.lock and commented out em.refresh) were updated to resolve the deadlocks when using EclipseLink JPA2.1
            //JPA2.1 does not allow locks to be defined using persistence.xml like JPA2.0
            //Default is to use optimistic locks which was causing deadlocks so needs to be changed to pessimistic
                       
                        em.lock(messngr, LockModeType.PESSIMISTIC_READ);
                        //em.refresh(messngr);

			int count=messngr.getuserCount();
			messngr.setuserCount(count+1);
			messngr.setisManager(isManager);
							
			// save the Entity
			em.persist(messngr);
			// close it up.
			
			ut.commit();
			em.clear();
		return result;
	}

/*	private void initConnection() throws Exception {

		if (em != null) {
			return;
		}

		boolean resume = true;
		int resumeCount = 0;
		while (resume) {
			resumeCount++;
			try {
				emf = Persistence.createEntityManagerFactory("MessengerEnt");
				em = emf.createEntityManager();
			} catch (Exception e) {

			}
			if (em != null) {
				resume = false;
			} else if (resumeCount > 2) {
				throw new Exception("Failure at Entity Manager initialization");
			}
		}

	}*/

	public jakarta.ejb.SessionContext getSessionContext() {
		return mySessionCtx;
	}
	/**
	 * setSessionContext
	 */
	public void setSessionContext(jakarta.ejb.SessionContext ctx) {
		mySessionCtx = ctx;
	}
	/**
	 * ejbCreate
	 * @throws  
	 */
	@jakarta.annotation.PostConstruct
	public void ejbCreate()  {
	 
		 String is_out = System.getProperty("QuickSec_PRINT_OUT");
		 String db_prop = System.getProperty("QuickSec_DB_DISABLE");
		 if (is_out != null) print_out = true;
		 if (db_prop != null) db_disable = true;		
	}
	/**
	 * ejbActivate
	 */
	public void ejbActivate() {
	}
	/**
	 * ejbPassivate
	 */
	public void ejbPassivate() {
	}
	/**
	 * ejbRemove
	 */
	@jakarta.annotation.PreDestroy
	public void ejbRemove() {
		//em.close();
		//emf.close();
	}
}
