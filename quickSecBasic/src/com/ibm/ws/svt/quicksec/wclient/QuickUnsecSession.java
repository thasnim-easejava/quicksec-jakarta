package com.ibm.ws.svt.quicksec.wclient;


import jakarta.servlet.annotation.ServletSecurity;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name="QuickUnsecSession",urlPatterns={"/unsecSession"})
@ServletSecurity

public class QuickUnsecSession extends QuickSecSession {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
