<%-- Ceci est un commentaire JSP --%>
<%@page contentType="text/html"%>
<!--<%@page errorPage="erreur.jsp"%>-->
<%-- Importation d'un paquetage (package) --%>
<%@page import="java.util.*"%>
<html>
 <head><title>Page JSP</title></head>
 <body>
  <%-- Déclaration d'une variable globale à la classe --%>
  <%! int nombreVisites = 0; %>
  <%-- Définition de code Java --%>
  <% //Il est possible d'écrire du code Java ici
    Date date = new Date();
    // On peut incrémenter une variable globale pour compter le nombre
    // d'affichage, par exemple.
    nombreVisites++;
  %>
  <h1>Exemple de page JSP</h1>
  <%-- Impression de variables --%>
  <p>Au moment de l'exécution de ce script, nous sommes le <%= date %>.</p>
  <p>Cette page a été affichée <%= nombreVisites %> fois !</p>
  <h1>Params</h1>
  <p>Param hello = <%= config.getInitParameter("hello") %></p>
  <h1>Chargement du fichier : </h1>

	Select a file to upload: <br />
	<form action="xlsToCsv" method="post" enctype="multipart/form-data">
	<input type="file" name="file" />
	<br />
	<input type="submit" value="Upload File" />
	</form>

</html>