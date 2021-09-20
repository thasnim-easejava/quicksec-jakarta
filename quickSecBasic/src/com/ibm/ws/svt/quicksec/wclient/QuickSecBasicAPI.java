package com.ibm.ws.svt.quicksec.wclient;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ResourceBundle;

import jakarta.annotation.security.DeclareRoles;
import jakarta.ejb.EJB;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletConfig;
import com.ibm.ws.svt.quicksec.EJB.Delegate;
//import com.ibm.ws.webcontainer.servlet.ServletConfig;


@DeclareRoles({"BasicLogin_WebUsers"})
@WebServlet(name="QuickSecBaiscUnP",urlPatterns={"/webclientUnP","/QuickSecBasicUnP"})
/**
 * Servlet implementation class for Servlet: QuickSecBasic
 *
 */
public class QuickSecBasicAPI extends HttpServlet implements Servlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * @see HttpServlet#HttpServlet()
	 */

	String rtnString = "Msg: <br>";
	boolean print_out = false;
	@EJB 
	private Delegate myDelegate;

	public void doGet (HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{			

		PrintWriter out;

		//Accessing the LOGINTIME attribute set during the LoginFilter.
		String successMsg = null;
		String timeStamp = (String) req.getSession().getAttribute("LOGINTIME");

		if (timeStamp != null) {
			successMsg = "Successful login on " + timeStamp;
		}
		else {
			successMsg = "Successful login";
		} 	

		res.setContentType("text/html");
		res.setHeader("Pragma", "No-cache");
		res.setHeader("Cache-Control", "no-cache");
		res.setDateHeader("Expires",0);

		out = res.getWriter();
		try {
			out.println("<HTML><TITLE>QuickSec Security Test App</TITLE><BODY>");
			out.println("<H1>QuickSec Security Test App</H1>");


			out.println("<font size=\"2\"><strong> Welcome user: " + req.getRemoteUser()+ "</strong></font>");
			out.println("<p>" + successMsg);
			out.println("</p></FORM>");
			out.println("<FORM METHOD=POST ACTION=\""+ req.getRequestURI() + "\">");
			out.println("<p>Click Below to call EJB methods");
			out.println("<p><INPUT TYPE=SUBMIT VALUE=\"Go & Get..\">");
			out.println("<p><p>"+rtnString);
			//reset the rtnString
			rtnString = "Msg: <br>";
			out.println("</FORM>");

			String securl =res.encodeURL("/QuickSecBasic855L/secSession");
			String unsecurl =res.encodeURL("/QuickSecBasic855L/unsecSession");
			out.println("<A href=\"" + securl + "\">Get Session Info by calling servlet with secure URI</A>");
			out.println("<br><A href=\"" + unsecurl + "\">Get Session Info by calling servlet with unsecure URI</A>");
			out.println("</BODY></HTML>");

		} catch ( Exception e )	{
			rtnString = rtnString + "<p> Exception: " + e.toString();
			out.println("<p><p>"+rtnString);
			System.out.println("Inside the catch"+rtnString);
			out.close();
			e.printStackTrace();
		}
		out.close();           
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		

		//System.out.println("I am in doPost");
		rtnString = "Msg: <br>";

		String user, password;
		try {
			final ResourceBundle BUNDLE = ResourceBundle.getBundle("com.ibm.ws.svt.quicksec.wclient30.secPersona");
			user = BUNDLE.getString("login_user");
			password= BUNDLE.getString("login_password");
			//System.out.println("user is "+user);
			//System.out.println("password is "+password);
		} catch ( java.util.MissingResourceException e ) {
			System.out.println("Error getting the properties ");
			user = "persona2";
			password = "ppersona2";
		//	e.printStackTrace();
		}  
		try
		{
			Boolean authcheck= req.authenticate(res);
			if (!authcheck) {
				//System.out.println("First authenticate() returned false returning");
				return;
			}

			rtnString = rtnString + "<br> User log-in: " + req.getRemoteUser();                 
			rtnString = rtnString + "<br> <B>Below userid should be same as authenticated user  </B>";
			//System.out.println("getRemoteUser " + req.getRemoteUser()+ "</BR>");

			rtnString = rtnString + "<br>    " + myDelegate.getMessage();		

			req.logout();
			req.login(user, password); 
			/*System.out.println("login(user2,puser2) call");
				System.out.println("getRemoteUser " + req.getRemoteUser()+ "</BR>");
				System.out.println("getUserPrincipal " + req.getUserPrincipal()+ "</BR>");
				System.out.println("getAuthType " + req.getAuthType()+ "</BR>");*/ 
			rtnString = rtnString + "<br> <B> After logging in using login()API, below userid should be picked from secPersona.properties file or default is persona2 </B>";
			rtnString = rtnString + "<br>    " + myDelegate.getMessageMgrOnly();		

			req.logout();
			/*System.out.println("logout call");
				System.out.println("getRemoteUser " + req.getRemoteUser()+ "</BR>");
				System.out.println("getUserPrincipal " + req.getUserPrincipal()+ "</BR>");
				System.out.println("getAuthType " + req.getAuthType()+ "</BR>"); */

			authcheck = req.authenticate(res);
			if (!authcheck) {
				System.out.println("Second authenticate() returned false returning");
				return;
			}
			/*System.out.println("Second authenticate() call");
				System.out.println("getRemoteUser " + req.getRemoteUser()+ "</BR>");
				System.out.println("getUserPrincipal " + req.getUserPrincipal()+ "</BR>");
				System.out.println("getAuthType " + req.getAuthType()+ "</BR>"); */

			rtnString = rtnString + "<br> <B>After second authenticate() call, Below userid should be same as the authenticated user above </B>";
			rtnString = rtnString + "<br>    " + myDelegate.getMessage();	

		}
		catch ( Exception e )
		{
			rtnString = rtnString + "<p> Exception: " + e.toString();
			e.printStackTrace();
		}

		try
		{
			String is_out = System.getProperty("QuickSec_PRINT_OUT");
			if (is_out != null) 	print_out = true;

			if (print_out) {	
				rtnString = rtnString + "<br> <br> <B> Calling getMessageDenyAll: This call should fail </B>";
				rtnString = rtnString + "<p><br>" + myDelegate.getMessageDenyAll ();         			
			}
		} catch (jakarta.ejb.EJBAccessException e){
			rtnString = rtnString + "<br> Call failed: Not able to call denied method as expected.";
		}catch ( Exception e )	{
			rtnString = rtnString + "<p> Exception: " + e.toString();
			e.printStackTrace();
		}
		doGet(req,res);
	}

public void init(ServletConfig config) throws ServletException
{
	super.init(config);
	//System.out.println("I am in init");

}

}
