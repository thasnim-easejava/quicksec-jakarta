package com.ibm.ws.svt.quicksec.EJB;


import jakarta.annotation.Resource;
import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.annotation.security.RunAs;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
//import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;

import com.ibm.ws.svt.quicksec.EJB.Messenger;

@DeclareRoles("delegateEJBBeanLevelRunAs")
@RolesAllowed("delegateUsers")
@RunAs("delegateEJBBeanLevelRunAs")
@Stateless
//@Local(Delegate31.class)
@Remote(Delegate.class)

// Bean implementation class for Enterprise Bean: Delegate
public class DelegateBean implements Delegate {

	@Resource private jakarta.ejb.SessionContext mySessionCtx;
	//@EJB 
	//private	Messenger myMessenger;
	@EJB (name="messengerRef") private
	Messenger myMessenger;
	Delegate myquickSession = null;
	boolean print_out = false;

	public String getMessage ()
	{

		String prinicipalname = null;

		try {
			prinicipalname = " In getMessage of DelegateEJB: Caller Identity is ... " + mySessionCtx.getCallerPrincipal().getName();

		}
		catch ( Exception e )
		{   e.printStackTrace();
		prinicipalname = "error...";
		}

		return prinicipalname;
	}

	public String getMessageMgrOnly()
	{
		String ejbMsg = "";
		String MessengerMsg = "";
		ejbMsg = "In getMessageMgrOnly of DelegateEJB: ";
		try
		{
			//MessengerMsg = MessengerMsg + myMessenger.getMessage();
			MessengerMsg = MessengerMsg + getMessage();
		}
		catch ( Exception e ){
			MessengerMsg = "Error calling MessengerEJB <br>";
			e.printStackTrace();
		} 
		if ( myMessenger == null )
			MessengerMsg = "MessengerEJB is NULL<br>";

		return ejbMsg + MessengerMsg;  
	}

	public String getMessageRunAsBeanLevel() {

		String ejbMsg = "";
		String MessengerMsg = "";
		ejbMsg = "In getMessageRunAsBeanLevel of DelegateEJB: ";
		try
		{
			MessengerMsg = MessengerMsg + myMessenger.getMessage();
		}
		catch ( Exception e ){
			MessengerMsg = "Error calling MessengerEJB: " + e.getMessage() + "<br>";
			e.printStackTrace();
		} 
		if ( myMessenger == null )
			MessengerMsg = "MessengerEJB is NULL<br>";

		return ejbMsg + MessengerMsg; 
	}
	@DenyAll  
	public String getMessageDenyAll() {

		String ejbMsg = "";
		String MessengerMsg = "";
		String DenyMsg = "";
		ejbMsg = "In getMessageDenyAll of DelegateEJB: ";
		try
		{
			DenyMsg = getMessageForSystem();
		}
		catch (jakarta.ejb.EJBAccessException e){
			MessengerMsg = "Not able to call denied method as expected.";
		}

		return ejbMsg + DenyMsg +MessengerMsg; 
	}

	public String getMessageForSystem () 
	{
		String ejbMsg = "";
		String MessengerMsg = "";
		ejbMsg = "In getMessageforSystem (of DelegateEJB)";
		try
		{
			//MessengerMsg = MessengerMsg + myMessenger.getMessage();
			MessengerMsg = " caller Identity is ... " + mySessionCtx.getCallerPrincipal().getName()+MessengerMsg;
		}
		catch ( Exception e ){
			MessengerMsg = " Error calling MessengerEJB <br>";
			e.printStackTrace();
		} 
		if ( myMessenger == null )
			MessengerMsg = "MessengerEJB is NULL";

		return ejbMsg + MessengerMsg; 

	}

	@PermitAll
	public String getMessagePermitAll () 
	{
		String ejbMsg="";
	try{
		 ejbMsg = "In getMessagePermitAll of DelegateEJB : Caller Identity is ... " + mySessionCtx.getCallerPrincipal().getName();
		;
	}catch ( Exception e ){
		ejbMsg = " In getMessagePermitAll of DelegateEJB : Caller Identity is ... Error getting principal name  <br>";
		//e.printStackTrace();
	} 
		return ejbMsg ; 

	}


	public String getMessageForClient () 
	{
		String ejbMsg = "";
		String MessengerMsg = "";
		ejbMsg = "In getMessageforClient (of DelegateEJB)";
		try
		{
			MessengerMsg = MessengerMsg + myMessenger.getMessage();
		}
		catch ( Exception e ){
			MessengerMsg = " Error calling MessengerEJB <br>";
			e.printStackTrace();
		} 
		if ( myMessenger == null )
			MessengerMsg = "MessengerEJB is NULL";

		return ejbMsg + MessengerMsg; 
	}


	public String getMessageForSpecifiedUser () 
	{
		String ejbMsg = "";
		String MessengerMsg = "";
		ejbMsg = "In getMessageforSpecifiedUser (of DelegateEJB)";
		try
		{
			MessengerMsg = MessengerMsg + myMessenger.getMessage();
			//MessengerMsg = " caller Identity is ... " + mySessionCtx.getCallerPrincipal().getName()+MessengerMsg;
		}
		catch ( Exception e ){
			MessengerMsg = " Error calling MessengerEJB <br>";
			e.printStackTrace();
		} 
		if ( myMessenger == null )
			MessengerMsg = "MessengerEJB is NULL";

		return ejbMsg + MessengerMsg;  
	}

	public String getMessageForAnotherSpecifiedUser () 
	{
		String ejbMsg = "";
		String MessengerMsg = "";

		ejbMsg = "In getMessageforAnotherSpecifiedUser (of DelegateEJB). This Method is Excluded in DD, so it should always fail.";
		try
		{
			MessengerMsg = MessengerMsg + myMessenger.getMessage() + ".<br>";
		}
		catch ( Exception e ){
			MessengerMsg = " Error calling MessengerEJB <br>";
			e.printStackTrace();
		} 
		if ( myMessenger == null )
			MessengerMsg = "MessengerEJB is NULL";

		return ejbMsg + MessengerMsg;         
	}


	/**
	 * ejbCreate
	 */
	@jakarta.annotation.PostConstruct
	public void ejbCreate() 
	{
		String is_out = System.getProperty("QuickSec.PrintOut");
		if (is_out != null)
			print_out = true;

	}



}
