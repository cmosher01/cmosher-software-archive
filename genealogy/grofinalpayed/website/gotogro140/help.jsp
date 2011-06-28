<%@ include file="htmlhead.jsp" %>
<head>
<title>Genealogy Research Organizer</title>
<link rel="stylesheet" href="gro.css" type="text/css" media="all" />
</head>
<body>

<%@ include file="grohead.jsp" %>
<ul>
	<li><a href="#gettingStarted">Getting Started</a>
	<ul>
		<li><a href="#introImportExistingData">Import Existing Data</a></li>
		<li><a href="#introDropLineChart">Drop-Line Chart</a></li>
		<li><a href="#introIndividual">Individual</a></li>
		<li><a href="#introEventsAndAttributes">Events and Attributes</a></li>
		<li><a href="#introPartnerships">Partnerships</a></li>
		<li><a href="#introCitationsAndSources">Citations and Sources</a></li>
	</ul></li>
	<li><a href="#moreDetails">More Details</a>
	<ul>
		<li><a href="#dropLineChart">Drop-line Chart</a>
		<ul>
			<li><a href="#clean">Clean</a></li>
			<li><a href="#copyAndPaste">Copy and Paste</a></li>
			<li><a href="#fontsAndScaling">Fonts and Scaling</a></li>
			<li><a href="#dimAndShow">Dim and Show</a></li>
			<li><a href="#traversalUsingKeyboradShortcuts">Traversal using Keyboard Shortcuts</a></li>
			<li><a href="#selectRelatives">Select Relatives</a></li>
		</ul></li>
		<li><a href="#pedigreeChart">Pedigree Chart</a></li>
		<li><a href="#indexOfIndividuals">Index of Individuals</a></li>
		<li><a href="#listOfSources">List of Sources</a></li>
		<li><a href="#webServer">Web Server</a></li>
		<li><a href="#datesAndCalendars">Dates and Calendars</a></li>
		<li><a href="#holidays">Holidays</a></li>
		<li><a href="#characterSets">Character sets (ANSEL, Unicode, ASCII)</a></li>
		<li><a href="#buildYourOwnCDROM">Build your own CD-ROM</a></li>
	</ul></li>
</ul>

<div class="textblock">
<h3 id="gettingStarted">Getting Started</h3>
<p>
The first time you run GRO, you can either create a new empty gedcom file,
or open an existing one. If you create a new empty tree (menu: File/New),
you can add individuals by choosing the menu Edit/New Individual, or by
right-clicking on the chart and choosing New Individual.
</p>
<h4 id="introImportExistingData">Import Existing Data</h4>
<p>
Alternatively, if you have your family tree data in another
program, you can try to export it from that program in GEDCOM format (remember
where you save it!). Then open that exported file using GRO (menu: File/Open...).
</p>
<h4 id="introDropLineChart">Drop-Line Chart</h4>
<p>The first time you open a GEDCOM file, the individuals will automatically
be positioned on the drop-line chart, but they will probably not be very well
organized. So the first task you may want to perform is to move the
individuals around (by clicking and dragging them) and clean up the chart.
(Note: if you have the DEMO version of GRO, remember you CAN'T SAVE any
of your changes, so don't do too much work. Of course the
full version of GRO lets you save changes.)
</p>
<h4 id="introIndividual">Individual</h4>
<p>
You can double-click on an individual to open up a new window that lets
you modify their genealogical information. Their name is shown in bold;
press the "name" button to edit the person's name (use slashes around the
surname). Use the gender button to edit their gender (male, female, or unknown).
Events and attributes are shown in
different lists (use the tabs to switch between them). 
To add an event or
attribute to the person, double-click on &lt;new event&gt; or &lt;new
attribute&gt;. To edit an existing one, just double-click the row.
The other two
tabs are Ancestors which shows a pedigree list-control, and Partnerships
which lists the individual's spouses and children.
</p>
<h4 id="introEventsAndAttributes">Events and Attributes</h4>
<p>
Choose the type of the event from the drop-down list. Enter the date by
pressing the date button and entering the date information (for normal
dates, choose "exact date", choose the month, and enter the day and year,
and click OK). Enter the place the event happened (include address if
known, city, county, state, and country). The citation button allows you
to cite the source of the event information. You can also enter any notes
about the event you want.
</p>
<h4 id="introPartnerships">Partnerships</h4>
This tab (on the individual window) lists all spouses (or partners) of
the individual. For each spouse, it lists all "partnership events" (like
marriage or engagement) followed by all children (from that partnership).
(Note: you cannot add parters or children from this page, yet. For now,
you go to the drop-line chart and add a new individual, then connect them.)
<h4 id="introCitationsAndSources">Citations and Sources</h4>
<p>
The citation dialog allows you to enter or modify information about a
single citation. The citation is basically a page from a source. A
source is a book, gravestone, census, newspaper, birth certificate,
or other document where you got the information from. The citation
is the exact page in the source where the information is located.
</p>
<p>
When entering a new citation, you can either choose an existing source
(if you already entered it earlier) or add a new source. Click on the
source button to do this. The source information is author, title, and
publication information, as well as a repository (library or other
building) where you found the source. Again, if you already entered the
repository into the system earlier, you can just pick it from the list,
otherwise you must enter a new one. The source dialog also has a place
you can enter the actual text from the source document. This can be used
to enter the entire text of a will, birth certificate, or any other
text directly from the source document. Note that when you do enter
text here, be sure to enter it exactly as it was in the original document.
Don't interpret the data when entering it here. For example,
if the document says "3/1/26" then you should enter 3/1/26, not 1 Mar. 1926.
If you want to add you own comment in the text, be sure to surround them
with brackets [like this].
</p>
<p>
The author and title should be entered in their entirety from the title page if possible.
For sources that aren't books, you should describe the source as
fully as possible. For example, a birth certificate could be entered like this: 
author: "Division of Vital Statistics, Department of Health, New York, USA"
title: "Certificate of death; Smith, John; Number 9226".
Publication information should include the publisher, city, and year for
published sources. For unpublished sources, enter the date the source
was created, and the place it is located.
</p>
<p>
In general, when citing a source, try to be as complete
as possible when describing it. The idea is that anybody else who
reads the citation information you enter should be able to locate
the same exact source by themselves.
</p>
<p>
The citation also contains a field called quality. This is a
rough estimate of the reliability of the source, how well it can
probably be trusted. Birth certificates, for example, are direct
evidence of births. On the other hand, a newspaper article may
by secondary evidence. Or you may be refering to a published genealogy
that you know has many errors, so you might mark that source as
unreliable.
</p>
<h3 id="moreDetails">More Details</h3>
<h4 id="dropLineChart">Drop-Line Chart</h4>
You can use the drop-line chart to add new people to your tree.
When you add a new person and enter his information (and close
the window), you will want to "connect" him to an existing
person on the tree. For example, assume you already have John Smith
on the chart. Through your research, you find out that his father
is James Smith. You'll add James Smith to the tree, and then you'll
want to <i>connect</i> him to John Smith. To do this
select BOTH people (click on one person, then shift-click on
the second person) and right-click (on the second person) and
choose "Connect". This will bring up a dialog asking you how
these two people are related. Choose the correct relationship
and click OK. That's all there is to it! (Note that you must
enter the <i>gender</i> of <i>parents</i> before you connect them; this is
necessary because the GEDCOM standard needs to know whether
to give the person a HUSBAND or WIFE tag in the family.)
<h5 id="clean">Clean</h5>
When GRO opens a GEDCOM file that was created in another application
(like PAF or FTM), it needs to decided how to arrange the individuals
on the drop-line chart, because no chart-positioning information is
stored in the GEDCOM file (indeed, most programs do not even allow the
user to move individuals around on a chart layout at all). Versions of
GRO prior to V1.4 did not do a good job of arranging the people on the
chart. People would overlap to a great extent, and families would not
be logically layed out with respect to each other. Large trees would
become a complete mess that was almost impossible to rearrange manually.
As of V1.4, the automatic layout algorithm as been greatly improved.
This command can be accessed from the menu: Edit, Clean Entire Tree.
(Previous versions had a Clean command that operated on selected people;
V1.4 only has the ability to clean the entire tree).
Since this command rearranges every individual in the entire tree, you
should take the precaution of backing up your current tree before you
clean the entire tree, in case you don't like the way it arranges it.
The command will search for the longest paternal line and arrange the
generations of that family (generally of one surname). Then it will
arrange related families in a similar fashion, off to the right. It will
repeat this process as needed to arrange the entire tree. In practice, this
will expose some unrelated people (or families), and will move them far
away from the rest of the tree. Generally this is the result of a mistake
in the tree, where a person was added but not connected. In this way, the
"Clean Entire Tree" command will help you to find such errors in your tree
(in the hopes you would like to correct them).
Once GRO has arranged the families in the tree, you will, no doubt, want
to rearrange some of the people yourself. This should be much easier than
in prior versions of GRO, because there will be no overlapping people.
<h5 id="copyAndPaste">Copy and Paste</h5>
The drop-line chart allows you to copy and paste individuals (from
one Gedcom document to another). Note that related items will also
be copied and pasted. This includes family records, and all citation,
source, and repository records referenced by the copied individuals.
<h5 id="fontsAndScaling">Fonts and Scaling</h5>
The drop-line chart can be re-scaled when a different font (or size) is chosen.
In versions of GRO prior to V1.4, increasing the font size would not move
people over, and therefore would cause them to overlap. As of V1.4, an option
is provided to re-scale the chart when the font is changed. The checkbox is
on the Options Font dialog (at the bottom), and is turned on by default.
In addition to rescaling the chart, you can simply "zoom-out" to get a better
view of the chart as a whole. This will not permanently change the chart's scaling,
it will simply shrink it on the screen for you to look at. Choose from the menu:
View | Scale, and you can enter the scale you want to zoom out to. For example,
enter 10 (meaning 1:10 scale) to display the chart at one-tenth the actual size.
When zoomed out, you can move individuals around, and double-click on them, just
as if you weren't zoomed out. To return to normal (1 to 1) scale, there is a
convenient command on the menu: View | Normal 1:1 Scale. When returning to 1:1 scale,
the chart will automatically scroll to the selected individual (just as a convenience).
<h5 id="dimAndShow">Dim and Show</h5>
These commands allow you to more easily see the selected set of people
on the drop-line chart by "dimming" the other people. For example, try
selecting two siblings on a chart, then choose from the menu: Edit,
Dim Unselected. The two selected siblings will still be visible, as will
the line connecting them, but the rest of the people on the chart will
be very dim. To restore the original view, choose from the menu: Edit,
Show All.
<h5 id="traversalUsingKeyboradShortcuts">Traversal using Keyboard Shortcuts</h5>
You can navigate through people on the drop-line chart by using
keys. The navigation is accessed by holding down shift, or ctrl and shift,
and pressing the arrow keys. Start by selecting one person on the chart,
then hold down the shift key (or the ctrl and shift keys) to jump to
the relative, as indicated by the following list:
<table border="1" cellpadding="5">
<thead>
<tr><th>hold down:</th><th>and press this arrow:</th><th>to jump to this relative:</th></tr>
</thead>
<tbody>
<tr><td>shift      </td><td>left or right    </td><td>siblings</td></tr>
<tr><td>shift+ctrl </td><td>left or right    </td><td>spouse</td></tr>
<tr><td>shift      </td><td>up               </td><td>father</td></tr>
<tr><td>shift+ctrl </td><td>up               </td><td>mother</td></tr>
<tr><td>shift      </td><td>down             </td><td>child</td></tr>
<tr><td>shift+ctrl </td><td>down             </td><td>child</td></tr>
</tbody>
</table>
Note that when jumping to a child, it will jump to the left-most
child, or the previously selected child.
<h5 id="selectRelatives">Select Relatives</h5>
The "Select Relatives" command in versions prior to V1.4 has proven to be
fairly useless. It used too much memory and so would often fail.
Also, the choices provided were too confusing, and didn't always work
correctly. As of V1.4, the choices have been reduced to the most useful,
and two new choices have been added, ones that concern DNA of related
individuals. One DNA-related selection is the common Y-chromosome. This
will select all individuals (males) that have (or should have) the same
Y-chromosome as the currently selected male individual. The Y-
chromosome is passed (mostly) unchanged through the paternal line, and
therefore follows the family's surname (in most western cultures). The
other DNA selection available is the mitochondrial DNA selection. This will select
all individuals that have (or should have) the same mitochondrial DNA
as the currently selected person.
The mitochondrial DNA passes unchanged through the female line, and is
present in both males and females. These DNA selection options may prove
useful if two relatives might have their DNA tested; the selection will
help show if the two people should have a common Y-chromosome or common
mitochondrial DNA.

<h4 id="pedigreeChart">Pedigree Chart</h4>
From the drop-line chart window, you can right-click on a person
and choose "View Pedigree" (or click the pedigree tool bar button)
and it will open up a new window with that person's pedigree.
You can open a pedigree for any person in your tree. Once you are
on the pedigree window, you can resize the window to change
the size of the chart, and you can type + or - (plus or minus)
on the keyboard to change the number of generations displayed
on the chart.
<h4 id="indexOfIndividuals">Index of Individuals</h4>
You can choose from the menu: View, Index of Individuals to open
up a new window that lists all the individuals in your tree. The
list shows surname, given name, birth date, and death date. Click
on a list heading if you want to sort the list by that item.
Double-click on a person to open that individual's dialog.
<h4 id="listOfSources">List of Sources</h4>
The toolbar has a button with a book icon on it that opens a new
window listing all the sources in the tree (sorted by title).
Double-click a source to bring up the dialog that allows you to change
it (or delete it).
<h4 id="webServer">Web Server</h4>
GRO can act as a web server to allow other users on the Internet to
view all the (non-private) data in all your currently open Gedcom documents.
There is no need for any other web server software (such as Internet
Information Server (IIS), Personal Web Server (PWS), or Apache); GRO will
act as its own server. To turn the server on and off, choose from the
menu: File, Web Server. (Previous versions of GRO were designed to integrate
with PWS and IIS; this functionality no longer exists, as of version 1.4.)
The pages include a web page for each individual, listing all event,
attributes, spouses, and children. There is also a page for each source.
The first page shows all open GEDCOM documents, and allows the user to
view an index of people, sorted by name. New in V1.4 is the ability for
users to see the drop-line chart in their browsers. This feature makes use
of the Java programming language (via an Applet) to display the chart.
The chart will be shown as you've layed it out on your drop-line chart
in the program.
Note that the "Export Tree as HTML" command is still provided, but has
not been updated for the new style web pages. The "generate web index"
command (present in V1.3) has been removed as of V1.4; the web index is
generated automatically, and only when necessary. Private data (i.e.,
data for people under 72 years old) will not be shown.
To run a test to see how your web pages will look, you can follow these
steps:
<ol>
<li>Run GRO and open a small family tree (.ged file) for testing.</li>
<li>Choose from the menu: File, Web Server.</li>
<li>On the dialog, leave the port as 80, and click the Start button.</li>
<li>(Leave GRO running and) start your web browser (for example,
Internet Explorer).</li>
<li>Type the address: http://127.0.0.1 (which means your computer)</li>
<li>You should see a page that says "The following GEDCOM documents
are currently available on this GRO server" and shows the name
of the .ged file you opened.</li>
<li>You can then click on the name of the .ged file to see the family
chart (note that
you may have to wait for Java to be installed if it isn't already
installed on your machine; this happens automatically).</li>
<li>Click on a person to go to his web page.</li>
<li>To go back to the first page, click "home" at the top of a
person's web page.</li>
<li>Click on "(index)" and it will take you to the first page
of the index of people in that chart.</li>
<li>Click to get to the next level of the index, etc., then click
on a person's name to see that person's web page.</li>
</ol>
For other users on the Internet to be able to connect to your running
GRO web server, they must know the address of your computer. When you
are connected to the Internet, you can use your web browser to go to
http://www.whatismyip.com/ or some other similar web site. This will
show you what other Internet users will need to type in to access your
machine. For example, if the page tells you your IP is 23.217.12.3, then
users will type in http://23.217.12.3 in their browsers to connect to
your computer. Note that you must stay connected to the Internet for
users to be able to connect. Also note that when you disconnect and
reconnect to the Internet, your IP address usually changes.
<h4 id="datesAndCalendars">Dates and Calendars</h4>
GRO's date entry dialog allows you to be very specific about the
dates you enter in your genealogy. You can specify any date as
either an exact date, an approximate date, a range of possible dates,
or as a period (span) of time. You enter two dates to specify
a period of time, and GRO will automatically calculate the
number of days long the period is.
<p />
The date entry dialog can also be a very useful
tool in your research. It allows the usual entry of month, day,
and year. Also, it allows you to enter years BC if you so desire.
It automatically calculates the day of the week for any exact date
you enter.
<p />
The date dialog also allows you to specify the calendar (Julian or
Gregorian) of the date, and it allows you to convert between the
two calendars.
<p />
The Gregorian calendar is the calendar in normal use in the United
States of America today. It was first adopted at different times by
different countries; the English colonies adoped it in September
1752. The calendar in use before that was called the Julian
calendar (sometimes called "Old Style" or "O.S.", versus
"New Style" or "N.S." for the Gregorian calendar).
When you enter a date with the Gregorian button chosen, GRO
assumes it to be Gregorian; if you then click the Julian
button, GRO will convert it to the same day on the Julian
calendar.
<p />
Another change to the calendar occurred in September 1752
as well. The beginning of the year was changed from March
25 to the more familiar January 1. It was this March 25
New Year's Day that led to the "slashed year" (for example "Feb 9, 1749/50")
that can be found in many old records. This can led to
much confusion in your research if you're not careful.
The best bet is to interpret slashed years using the
<b>second</b> year shown.
For example, treat 9 Feb 1749/50 as 9 Feb 1750.
<p />
For more information about calendars, see <a href="http://www.genfair.com/dates.htm">GenFair's summary of Old Style and New Style</a>.
Or see the <a href="http://www.tondering.dk/claus/calendar.html">Calendar FAQ</a>.
<h4 id="holidays">Holidays</h4>
Have you ever asked yourself a question like "On what date did Easter fall in 1837?" GRO can help
answer this type of question. GRO can determine the date of
many common holidays in any given year. Choose from the menu Tools,
Holidays. Enter the year and pick the holiday; GRO will
calculate the date of the holiday.
<h4 id="characterSets">Character sets (ANSEL, Unicode, ASCII)</h4>
GRO handles all the ANSEL characters used in GEDCOM files (as indicated by
the GEDCOM 5.5 standard). ANSEL is a particular
character-encoding scheme used primarily by GEDCOM files, and the US
Library of Congress (of all things). It's use is being phased out, in
preference to the Unicode standard. Nevertheless, many GEDCOM files are
encoded in ANSEL, including those downloaded from www.familysearch.org,
so GRO has been designed to import all characters correctly. Versions of
GRO prior to 1.4 only handled a limited subset of ANSEL characters. Note
that GRO uses Unicode internally. Note that when saving your ged file,
you have to option of using ASCII or Unicode. ASCII is the most
popular, but it cannot represent all letters with diacritical marks.
Unicode is a more universal and complete standard, which can represent
literally every single character that has ever existed, but the file may
not be readable by other Genealogy programs or by text editors. Also note
that GRO stores Unicode files with 16-bit characters, so a given file
stored as Unicode will be twice as big as the same file stored as ASCII.
You can choose whether to save file as ASCII or Unicode in the Options
Unicode dialog.
<h4 id="buildYourOwnCDROM">Build your own CD-ROM</h4>
As of V1.4, GRO provides feature to help you create your own family tree
CD-ROM. GRO will automatically open any .GED files that are in the same
folder as the GRO.EXE program when it is started. Also, it will not write
any data to the registry, or the users hard-drive, when run from a CD-ROM.
</div>

<%@ include file="grofoot.jsp" %>



</body>
<%@ include file="htmlfoot.jsp" %>
