<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="javax.jdo.PersistenceManager" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="fsart.diffTools.webApp.model.CsvWebData" %>
<%@ page import="fsart.diffTools.webApp.dataHelper.PMF" %>

<html>
  <body>

  <h1>Chargement du fichier : </h1>

	Select a file to upload: <br />
	<form action="xlsToCsv" method="post" enctype="multipart/form-data">
	<input type="file" name="file" />
	<br />
	<input type="submit" value="Upload File" />
	</form>
	
	<h1>Choose data you want to compare</h1>
<%
    PersistenceManager pm = PMF.get().getPersistenceManager();
    String query = "select from " + CsvWebData.class.getName();
    List<CsvWebData> csvWebData = (List<CsvWebData>) pm.newQuery(query).execute();
    if (csvWebData.isEmpty()) {
%>
<p>DiffTools has no data to compare.</p>

<%
    } else {
%>
<p>
<form id='razDatastore' method='get' action='razDatastore' >
 <input type='submit' value='Raz data' />
</form>
</p>

<form id='selectSheet' method='get' action='compareFiles' >
<p>
Ref data <br />
<select size="8" name="sheetId1">
<%
    	String fileName = null; 
    	String sheetName = null;
        for (CsvWebData g : csvWebData) {
            if (g.getFileName() == null) {
            	fileName = "An anonymous file";
            } else {
            	fileName = g.getFileName();
            }
            
            if (g.getName() == null) {
				sheetName = "An anonymous sheet";
            } else {
				sheetName = g.getName();
            }
            %>
            <option value="<%= g.getKey() %>"><%=sheetName%> - <%=fileName%></option>
            <%
        }

    
%>
</select>
</p>


<p>
New data<br />
<select size="8" name="sheetId2">
<%
        for (CsvWebData g : csvWebData) {
            if (g.getFileName() == null) {
            	fileName = "An anonymous file";
            } else {
            	fileName = g.getFileName();
            }
            
            if (g.getName() == null) {
				sheetName = "An anonymous sheet";
            } else {
				sheetName = g.getName();
            }
            %>
            <option value="<%= g.getKey() %>"><%=sheetName%> - <%=fileName%></option>
            <%
        }
%>
</select>
</p>
 <input type="checkbox" name="razDatastore" value="true" checked="checked">Remove data after processing <br/ >
 <input type='submit' value='Submit' />
</form>
</p>
<%
    }
    pm.close();
%>


  </body>
</html>
