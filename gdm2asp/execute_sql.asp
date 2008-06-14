<!-- METADATA TYPE="TypeLib" FILE="C:\Program Files\Common Files\system\ado\msado15.dll" -->
<%
function execute_sql(sql)
{
	var cnx = Server.CreateObject("ADODB.Connection");
	cnx.Open("provider=sqloledb;server=mosherdesktop;database=play;trusted_connection=yes");
	var c = 0;
	cnx.Execute(sql,c,adCmdText|adExecuteNoRecords);
	cnx.Close();
	cnx = null;
	return c;
}
%>
