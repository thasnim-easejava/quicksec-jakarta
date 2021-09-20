<%@ page import="java.security.*" %>
<%@ page import="java.io.*"%>

<%
  Principal prince = request.getUserPrincipal();
  if(prince == null){ %> <jsp:forward page="NoSecurityErr.jsp" /> <% }
  String acctId = request.getUserPrincipal().getName();
  session.setAttribute("uid",acctId);
         
  if (request.isUserInRole("BasicLoginDummy_WebUsers")){
	 %>
	<jsp:forward page="/BasicLoginDummyUsers.jsp" />
  	 <%
   }
  if (request.isUserInRole("BasicLogin_WebUsers")){
	%>
	<jsp:forward page="/webclient1" />
	<%
   }
%>
<jsp:forward page="/BasicLoginDummyUsers.jsp" />
