package com.ibm.ws.svt.quicksec.EJB;

import jakarta.ejb.Remote;

/**
 * Remote interface for Enterprise Bean: Messenger
 */
@Remote
public interface Messenger  {
	public String getMessage () ;
	public String getMessageMgrOnly();
}
