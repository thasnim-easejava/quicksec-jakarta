package com.ibm.ws.svt.quicksec.wclient;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.Date;
import java.util.Enumeration;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.HttpConstraint;
import jakarta.servlet.annotation.ServletSecurity;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
//SecurityContext class below is an internal class
//import com.ibm.ws.security.core.SecurityContext;

@WebServlet(name="QuickSecSession",urlPatterns={"/secSession"})
@ServletSecurity(value=@HttpConstraint(rolesAllowed={"BasicLogin_WebUsers"}))

public class QuickSecSession extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void service (HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		res.setContentType("text/html");
		PrintWriter out=res.getWriter();
		out.println("<HTML><TITLE>HTTP Session Servlet</TITLE><BODY>");
		getHttpSessionInfo(req,res,out);
		out.println("<BODY><HTML>");
		out.close();
	}
	
	public void getHttpSessionInfo(HttpServletRequest req, HttpServletResponse res, PrintWriter out) throws ServletException, IOException
	{
		out.println("<H1>This is servlet to get HTTPSession information</H1>");
			
	try{
		out.println("<B> User name using getRemoteUser: </B>" + req.getRemoteUser()+ "<BR>");	
		//out.println("<B> User name using SecurityContext.getUser(): </B>" + SecurityContext.getUser()+ "<BR>");	
		Principal princ = req.getUserPrincipal();
		if (princ != null) {
			String acctId = princ.getName();
			//session.setAttribute("uid",acctId);
		    out.println("<B> User name using getUserPrinicipal(): </B>" + acctId + "<BR>");
		    
		    if (req.isUserInRole("BasicLogin_WebUsers")){
				out.println( acctId + "<B> is part of BasicLogin_WebUsers role </B>" +"<BR> <BR>");
			
			}
		} else {
			out.println("<B> getUserPrincipal API returned: </B>null  <BR> <BR>");
		}
		
		HttpSession session = req.getSession(true);
		if(session !=null)
		{
			
			out.println("<B>HTTPSession information</B><BR>");
			out.println("<B> Session ID: </B> " + session.getId() + "<BR>");
			out.println("<B> Last accessed time: </B> " + new Date(session.getLastAccessedTime()).toString() + "<BR>");
			out.println("<B> Creation time: </B> " + new Date(session.getCreationTime()).toString() + "<BR>");
		//	@SuppressWarnings("unchecked")
			Enumeration<String> e =  session.getAttributeNames();
			out.println("<b>Session values: </B><BR>");
			String name;
			while(e.hasMoreElements()){
				name=(String)e.nextElement();
				out.println("<B>"+  name + ": </B> "+ session.getAttribute(name) + "</BR> <BR>");
			}
			
				
		}
		else out.println("Session object is null");
		out.println("<HR>)");
		out.close();
	} catch (Exception e ){
		//System.out.println("I am in catch");
		out.println( "<B> Exception: </B>" + e.toString());
		e.printStackTrace();
		//res.encodeRedirectURL("/SessionSecViolation.jsp");
		//getServletContext().getRequestDispatcher("/SessionSecViolation.jsp").forward(req, res); 
	  }		
	}
}
