<!-- METADATA TYPE="TypeLib" FILE="C:\Program Files\Common Files\system\ado\msado15.dll" -->
<%
function select_rs(sql)
{
	var cnx = Server.CreateObject("ADODB.Connection");
	cnx.Open("provider=sqloledb;server=mosherdesktop;database=play;trusted_connection=yes");

	var rs = Server.CreateObject("ADODB.Recordset");
	rs.CursorLocation = adUseClient;
	rs.Open(sql,cnx,adOpenStatic,adLockOptimistic,adCmdText);

	var cnull = Server.CreateObject("ADODB.Connection");
	cnull.Open("provider=MSDAOSP");
	rs.ActiveConnection = cnull; // this is to disconnect the recordset

	cnx.Close();
	cnx = null;

	return rs;
}

function select_one(sql)
{
	var rs = select_rs(sql);

	var s = null;
	if (!rs.EOF)
		s = rs.Fields(0).Value;

	rs.Close();
	rs = null;

	return s;
}
%>
