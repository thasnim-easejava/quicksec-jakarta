package com.ibm.ws.svt.quicksec.jclient;

import java.util.*;
import java.io.*;

import jakarta.ejb.EJB;
import javax.naming.*;
import javax.rmi.PortableRemoteObject;



import java.security.PrivilegedAction;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.security.auth.callback.CallbackHandler;

import com.ibm.websphere.security.auth.callback.WSCallbackHandlerImpl;
import com.ibm.websphere.security.auth.WSSubject;

import com.ibm.ws.svt.quicksec.EJB.*;



public class ClientThread extends Thread {

   private String uid=null;
   private String pwd=null;
   private static String hostname, mhost = null;
   private String loginmodule=null;
   private String newlookup=null;
   private String realmnname=null;
   private int hits=1;
   private int clientid=0;
   private int sleeptime=0;
   private static boolean debug=true;
 

   
   /*
   private char OK_CHAR = '-';
   private char ERROR_CHAR = '*';
   private char ERROR_LOGIN_CHAR = '%';
   private String oneLineOut="no";
   private String tmpString, outString = null;

   */
 

   public ClientThread(String uid, String pwd, String shost, String meshost, String loginmodule, String realmn, String lookup, String hits, int clientid, int sleeptime) {
      this.uid=uid;
      this.pwd=pwd;
      hostname=shost;
      mhost=meshost;
      this.loginmodule=loginmodule;
      this.newlookup=lookup;
      this.realmnname=realmn;
      this.hits=Integer.parseInt(hits);
      this.clientid=clientid;
	  this.sleeptime=sleeptime;
      //this.oneLineOut=OneLineOut;

   }
   
   
  private Delegate getQuickHome(){
        Delegate myDelegate  = null;
            try{
            	//Updated the context from tWAS to Liberty for 2Q15 JEE Client support
                //Hashtable env = new Hashtable();
               // env.put(Context.INITIAL_CONTEXT_FACTORY, "com.ibm.websphere.naming.WsnInitialContextFactory");
               // env.put(Context.PROVIDER_URL, "corbaloc:iiop:"+hostname);
				
              //  Context ctx = new InitialContext(env);
            	
            	String iiopAddress="corbaloc:iiop:"+hostname+"/NameService";
            	//System.out.println("Before context Lookup: iiopAdress is " + iiopAddress );
            	/*A CosNaming Service Provider.  This is comparable to the server root naming service on tWAS.  This name service is provided specifically to do direct lookups of EJBs from remote clients.  When performing lookups in CosNaming, you cannot use a schema prefix, like java:.  CosNaming knows nothing about the java: name contexts, and will always fail if you try to lookup something if you use a java: prefix.   The only think that I'm aware of that is available via CosNaming are EJBs, and they will be found using "ejb/global/<app name>/<mod name>/<bean name>!<bean interface>.   The way you access CosNaming is to lookup the CosNaming NameService.....
Java EE JNDI Name Service.  This is the name service that handles Java EE naming lookups in java:.  Everything you lookup via this naming service must begin with java:.  The naming service is not aware of what is bound into CosNaming, and will fail if you attempt to lookup anything that doesn't begin with java:. */

            	Context jndiContext = new InitialContext();
            	Context cosContext = (Context) (jndiContext.lookup(iiopAddress));

                System.out.println("obtained initial context for Remote EJB lookup: " + cosContext);
            	
                Object objref = cosContext.lookup("ejb/global/QuickSec/delegateEJB/DelegateBean!com.ibm.ws.svt.quicksec.EJB.Delegate");
                
                //   Below lookup will  work to lookup EJB reference but will not work here because of multi threading
                
                myDelegate = (Delegate)javax.rmi.PortableRemoteObject.narrow(objref, Delegate.class);
                if (debug) System.out.println("QuickSecLongRunClient: lookup successfuls " + (myDelegate.toString()).substring(0, 40) + "..... <p>");
                
            }catch ( Exception e ){
            	System.out.println("Exception occurred during look up of EJB");
                    e.printStackTrace();
            }
            return myDelegate;
            
    }



    public void run() {
    
      try {
			//if (debug) System.out.println("Inside run method..");
            javax.security.auth.login.LoginContext lc = null;

            try {
            	//lc = new javax.security.auth.login.LoginContext("WSLogin",new com.ibm.websphere.security.auth.callback.WSCallbackHandlerImpl(uid, realmnname, pwd));
            	lc = new javax.security.auth.login.LoginContext(loginmodule,new com.ibm.websphere.security.auth.callback.WSCallbackHandlerImpl(uid, realmnname, pwd));
            } catch (javax.security.auth.login.LoginException e) {
            	System.err.println("ERROR: failed to instantiate a LoginContext and the exception: " + e.getMessage());
                System.out.println("ERROR: Client# "+clientid + "Uid : " + uid + " login failed. ");
                //e.printStackTrace();
                // may be javax.security.auth.AuthPermission "createLoginContext" is not granted
                //   to the application, or the JAAS Login Configuration is not defined.
            }

            if (lc != null){
            	try {
            		lc.login();  // perform login
                    javax.security.auth.Subject s = lc.getSubject();
                    // get the authenticated subject
                    // Invoke a J2EE resources using the authenticated subject
                    com.ibm.websphere.security.auth.WSSubject.doAs(s,new java.security.PrivilegedAction() {
                    	public Object run() {
                           try {
                        	   Delegate QuickSession = getQuickHome();
                                while (hits > 0) {
	                               	String tmpString, outString = null;
	                                tmpString = QuickSession.getMessage();
				           			outString = tmpString.substring(tmpString.indexOf(". "));
	                                //System.out.println("2: " + outString);
	                                //tmpString = QuickSession.getMessageForSystem (mhost, newlookup);
						           	//outString = outString + "==" + tmpString.substring(tmpString.indexOf(". "));
                                    //tmpString = QuickSession.getMessageForClient (mhost, newlookup);
	                                //outString = outString + tmpString.substring(tmpString.indexOf(". "));
                                    tmpString =  QuickSession.getMessageRunAsBeanLevel();
	                                outString = outString + "  [" + tmpString.substring(tmpString.indexOf(". "));
	                                //System.out.println("4: " + outString);
	                                //tmpString = QuickSession.getMessageForAnotherSpecifiedUser (mhost, newlookup);
                                    //outString = outString + "] [" + tmpString.substring(tmpString.indexOf(". "));
									try {
											Thread.sleep(sleeptime);
										}catch(InterruptedException interruptedexception){
											interruptedexception.printStackTrace();
										}
										
								    if (debug) {
								    	System.out.println(clientid+ "-"+ hits + ") " +outString + "]");
						 			} else{
										System.out.print("- ");
								    }
			 					    //}else {
									//System.out.print("- ");
					   				//}
	                                tmpString = null;
	                                outString = null;
	                                hits--;
	                                javatestclient.addSuccCount();
	                               }//end of while
				  				//sQuickSession.remove();
				  				//Thread.currentThread().destroy();
                           }catch (Exception e) {
							System.out.println("ERROR: error while accessing EJB resource, exception: " + e.getMessage());
                            System.out.println("ERROR: Client# "+clientid + "Uid : " + uid + " -Hit: "+ hits +" failed. ");
                           }
                               return null;
                               }
                       }
                   );//doAS ends
                   } catch (javax.security.auth.login.LoginException e) {
                       System.err.println("ERROR: login failed with exception: " + e.getMessage());
                       System.out.println("ERROR: Client# "+clientid + "Uid : " + uid + " login failed. ");
					   // e.printStackTrace();
                       // login failed, might want to provide relogin logic
                   }

  			  }// end of if
  			  
           }catch ( Exception e ){
                    System.out.println(e.toString());
           }// end of try 
   		   //System.out.println("End of Run() for this thread");

    }//end of run
    

   
}
