<%@ include file="htmlhead.jsp" %>
<head>
<title>Genealogy Research Organizer</title>
<link rel="stylesheet" href="gro.css" type="text/css" media="all" />
</head>
<body>

<%@ include file="grohead.jsp" %>

<div class="textblock">
<h3>Upgrading to V1.4 from an Earlier Version</h3>
<h4>Easiest way</h4>
<p>
The easiest way to upgrade to V1.4 is to simply install V1.4
over top of the old version. This is the simplest, but will
leave some unnecessary files around. An advantage to this method
is that a registered version will not have to be re-registered.
</p>
<p>
In case you choose this method, and want to remove the old unneeded
files, here is a list of the files you can delete manually:
</p>
<ul>
    <li>groinproc.dll</li>
    <li>default.asp</li>
    <li>bib.rtf</li>
    <li>head.rtf</li>
    <li>indi.rtf</li>
    <li>sour.rtf</li>
    <li>indi.tpl</li>
    <li>sour.tpl</li>
</ul>
<h4>Cleanest way</h4>
<p>
With this method, you first uninstall the old version, reboot, then
install the new version. This is the cleanest way, but you will lose
all your registry settings. The registry settings include the list
of recently opened files, the current font, and the submitter
name and address. They also include the registration information,
so if you have a registered version, you must re-enter your name and key. (Your key
was emailed to you when you bought the program. If you lost your key,
<a href="contact.jsp">contact the Support Department</a>.)
</p>
<ol>
<li>Run the old version of GRO</li>
<li>Choose from the menu: File|Setup.</li>
<li>Click Uninstall and click OK.</li>
<li>Reboot your computer. (This is important.)</li>
<li>Go to the <a href="download.jsp">download</a> page and follow the instructions.</li>
<li>When you run GRO, you will get a warning that the program is not registered.</li>
<li>If you have a registration key, choose from the menu: File|Register, and enter your name and key.</li>
</ol>
<h3>Start Menu item for GRO</h3>
<p>
You should double-check the Start Menu item for Genealogy Research Orgainzer
to make sure it is set to launch the correct version.
</p>
<p>To check this,
click on the start menu and locate the item "Genealogy Research Organizer" and
<i>right-click</i> on it. Then click Properties and look at the "Target" file.
Make sure the target is gro.exe in the directory you installed it in.
</p>
<p>
If you need to fix the start menu item for any reason, you can do so from
with the program itself.
Run the correct version of the program (note that you must do this from the Windows
Explorer if the start menu item isn't correct).
Choose from the menu: File|Setup, click the Remove button and then click the Add button.
This will attempt to remove the current start menu item for GRO, and will
add a new item that will launch the current program (which is presumably the
one you want it to launch).
</p>
</div>

<%@ include file="grofoot.jsp" %>



</body>
<%@ include file="htmlfoot.jsp" %>
