package com.ibm.ws.svt.quicksec.EJB;
import jakarta.ejb.Remote;

/**
 * Remote interface for Enterprise Bean: Delegate
 */
@Remote
public interface Delegate  {
		public String getMessage();
		public String getMessagePermitAll();
		public String getMessageMgrOnly();
		public String getMessageRunAsBeanLevel();
		public String getMessageForSystem();
		public String getMessageDenyAll();	
		public String getMessageForClient();
		
		public String getMessageForSpecifiedUser();
			
		public String getMessageForAnotherSpecifiedUser();
		
}
