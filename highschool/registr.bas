1	!
	!		C o n f e r e n c e   R e g i s t r a t i o n
	!
	!		author: Chris Mosher
	!		date  : October 13, 1982
	!
	!	!	On Error goto 19000
	!	!	Null.% = CtrlC
	!
	!
100	!		P r o g r a m   D e s c r i p t i o n
	!
	!		This program registers people  at  a  one-day  computer
	!		conference.   The registration data is stored on a disk
	!		file.  Two classes are offered  at  the conference.   A
	!		class  in  BASIC  casts $150.00 to attend;  a  class in
	!		COBOL costs $100.00  to attend.   After  all people are
	!		registeres, the user can select processing from a menu.
	!
	!
400	!		V a r i a b l e s   a n d   A r r a y s   U s e d
	!
	Declare	Long	Function	Instructions
	Declare	Long	Function	Alphabetize
	Declare	String	Function	Cap
	Declare	String	Constant	Data.File = "Registr.Dat;1"
	Declare	String	Constant	KB = "Sys$Input"
	!
	!
900	!		D i m e n s i o n   D e c l a r a t i o n s
	!
	!	!	Dimension	Line.$(100%) &
				,	Name.$(100%) &
				,	Cour.$(100%) &
				,	Fee.$(100%) &
				,	Fee.%(100%) &
				,	Name.Bas.$(100%) &
				,	Name.Cob.$(100%) &
				,	Char.$(256%) &
				,	Name.First.$(100%) &
				,	Name.A.$(10%)
	!
	!
1000	!		M a i n   P r o g r a m
	!
	Open KB as file #1%
	Print Tab(20%); "Conference Registration"
	Print Tab(20%); String$(10%, 45%); " "; String$(12%, 45%)
	Print
	Linput #1%, "Do you want instructions and menu?", Resp.$
	Call.% = Instructions If Seg$(Edit$(Resp.$, 239%), 1%, 1%) = "Y"
	!	Print instructions and menu if requested
	!
1100	Print
	Linput #1%, "Command: "; Com.$
	Goto 1100 Unless Len(Com.$)
	On Pos("ELCI.", Seg$(Edit$(Com.$, 239%), 1%, 1%), 1%) goto 2000, 3000, 4000, 5000, 32000
	!	Parse Command
	!
2000	Print
	Print Tab(10%); "Entry of Data"
	Open Data.File as file #2%, Access append
	Margin #2%, 256%
2100	Print
	Linput #1%, "Name: "; Name.$
	Name.$ = Edit$(Name.$, 253%)
	If Name.$ = "" then
		Close #2%
		Goto 1100
2200	Linput #1%, "Course: "; Cour.$
	Cour.$ = Edit$(Cour.$, 253%)
	Goto 2100 Unless Len(Cour.$)
	If not (Cour.$ = "B" or Cour.$ = "C") then
		Print Cap(Seg$(Cour.$, 1%, 10%)); " is not a vlid course."
		Print "Please re-enter either B or C."
		Goto 2200
2300	Print #2%, Name.$; "~"; Cour.$
	Goto 2100
	!	"Enter data" command
	!
3000	Print
	Print Tab(10%); "List of Names"
	Print
	Print Tab(5%); "Name"; Tab(30%); "Course"; Tab(45%); "Fee"
	Print
	Open Data.File as file #2%
	Execute.Flag.% = -1%
	For C% = 1% while Execute.Flag.%
	Linput #2%, Line.$(C%)
3100	Next C%
	C% = C%-2%
	Fee.Total.% = 0%
	For F% = 1% to C%
	Name.$(F%) = Cap(Seg$(Line.$(F%), Pos(Line.$(F%), " ", 1%)+1%, Pos(Line.$(F%), "~", 1%)-1%))
	Name.$(F%) = Name.$(F%)+", "
	Name.First.$(F%) = Cap(Seg$(Line.$(F%), 1%, Pos(Line.$(F%), " ", 1%)-1%))
	Name.$(F%) = Name.$(F%)+Name.First.$(F%)
	If Seg$(Line.$(F%), Len(Line.$(F%)), Len(Line.$(F%))) = "B" then
		Cour.$(F%) = "BASIC"
	else	Cour.$(F%) = "COBOL"
3200	If Cour.$(F%) = "BASIC" then
		Fee.$(F%) = "$150.00"
		Fee.%(F%) = 150%
	else	Fee.$(F%) = "$100.00"
		Fee.%(F%) = 100%
3300	Fee.Total.% = Fee.Total.%+Fee.%(F%)
	Next F%
	Name.A.$(I%) = "" For I% = 1% to C%+2%
	Name.A.$(F%) = Name.$(F%) For I% = 1% to C%
	Call.% = Alphabetize(C%)
	Name.$(F%) = Name.A.$(F%) For F% = 1% to C%
	Print Tab(5%); "Name.$(P%); Tab(30%); Cour.$(P%); Tab(45%); Fee.$(P%) For P% = 1% to F%
	Print
	Print Tab(5%); "Total enrollment: "; Num1$(F%); Tab(33%); "Total feed: $"; Num1$(Fee.Total.%); ".00"
	Goto 1100
	!	"List names" command
	!
4000	Print
	Print Tab(10%); "Course Registrations"
	Print
	Print Tab(5%); "Course"; Tab(20%); "Name"; Tab(45%); "Fee"
	Print
	Open Data.File as file #2%
	Execute.Flag.% = -1%
	For C% = 1% while Execute.Flag.%
	Linput #2%, Line.$(C%)
4100	Next C%
	C% = C%-2%
	C.B% = 0%
	C.C% = 0%
	For F% = 1% to C%
	Name.$(F%) = Cap(Seg$(Line.$(F%), Pos(Line.$(F%), " ", 1%)+1%, Pos(Line.$(F%), "~", 1%)-1%))
	Name.$(F%) = Name.$(F%)+", "
	Name.First.$(F%) = Cap(Seg$(Line.$(F%), 1%, Pos(Line.$(F%), " ", 1%)-1%))
	Name.$(F%) = Name.$(F%)+Name.First.$(F%)
	If Seg$(Line.$(F%), Len(Line.$(F%)), Len(Line.$(F%))) = "B" then
		C.B% = C.B%+1%
		Name.Bas.$(C.B%) = Name.$(F%)
	else	C.C% = C.C%+1%
		Name.Cob.$(C.C%) = Name.$(F%)
4200	Next F%
	Name.A.$(F%) = Name.Bas.$(F%) For I% = 1% to C.B%
	Call.% = Alphabetize(C.B%)
	Name.Bas.$(F%) = Name.A.$(F%) For F% = 1% to C.B%
	Name.A.$(F%) = Name.Cob.$(F%) For I% = 1% to C.C%
	Call.% = Alphabetize(C.C%)
	Name.Cob.$(F%) = Name.A.$(F%) For F% = 1% to C.C%
	Print Tab(5%); "BASIC";
	Print Tab(20%); Name.Bas.$(P%); Tab(45%); "$150.00" For P% = 1% to C.B%
	Print Tab(8%); "Total enrollment: "; Num1$(C.B%); Tab(33%); "Total fees: $"; Num1$(150%*C.B%); ".00"
	Print
	Print Tab(5%); "COBOL";
	Print Tab(20%); Name.Cob.$(P%); Tab(45%); "$100.00" For P% = 1% to C.C%
	Print Tab(8%); "Total enrollment: "; Num1$(C.C%); Tab(33%); "Total fees: $"; Num1$(100%*C.C%); ".00"
	Print
	Print Tab(5%); "Total enrollment: "; Num1$(C.B%+C.C%); Tab(33%); "Total fees: $"; Num1$(150%*C.B%+100%*C.C%); ".00"
	Goto 1100
	!	"Course registrations" command
	!
5000	Print
	Print Tab(10%); "Inquiry of Registrants"
5010	Print
	Linput #1%, "Name: "; Name.I.$
	Name.I.$ = Edit$(Name.I.$, 495%)
	Goto 1100 Unless Len(Name.I.$)
	Open Data.File as file #2%
	Execute.Flag.% = -1%
	For C% = 1% while Execute.Flag.%
	Linput #2%, Line.$(C%)
5100	Next C%
	C% = C%-2%
	Print.Flag.% = 0%
	For P% = 1% to C%
	If Edit$(Name.I.$, 495%) = Seg$(Line.$(P%), Pos(Line.$(P%), " ", 1%)+1%, Pos(Line.$(P%), "|", 1%)-1%) then
		Print.Flag.% = -1%
		Print Tab(5%); Cap(Seg$(Line.$(P%), 1%, Pos(Line.$(P%), " ", 1%)-1%)); " "; &
			       Cap(Seg$(Line.$(P%), Pos(Line.$(P%), " ", 1%)+1%, Pos(Line.$(P%), "|", 1%)-1%));
		If Seg$(Line.$(P%), Len(Line.$(P%)), Len(Line.$(P%))) = "B" then
			Print Tab(30%); "BASIC"
		else	Print Tab(30%); "COBOL"
5300	Next P%
	Print "No one registered by the name of "; Cap(Name.I.$); "." Unless Print.Flag.%
	Close #2%
	Goto 5010
	!	"Inquire on registrants" command
	!
	!
15000	!		L o c a l   F u n c t i o n s
	!
	!	Instructions
	Def Instructions
	Print
	Print Tab(10%); "Menu"
	Print
	Print Tab(10%); "Entry"; Tab(20%); "Function"
	Print Tab(10%); "E"; Tab(20%); "Enter data"
	Print Tab(10%); "L"; Tab(20%); "List names"
	Print Tab(10%); "C"; Tab(20%); "Course registrations"
	Print Tab(10%); "I"; Tab(20%); "Inquire on registrants"
	Print Tab(10%); "."; Tab(20%); "Exit program"
	Print
	Print "Enter data:"
	Print "Allows the user to enter the registrants' names and courses."
	Print " prompt          entry"
	Print " Name:           first-name surname  or  press <RETURN> to exit"
	Print " Course:         B (for BASIC)  or  C (for COBOL)"
	Print "If a name is inputted incorrectly,  just press  <RETURN>  at"
	Print "the Course: prompt, and retype the name at the Name: prompt."
	Print
	Print "Inquire on registrants:"
	Print "Allows the user to inquire as to the status of a registrant."
	Print "At the Name: prompt, type the last name of the registrant(s)"
	Print "whose  status  is  to  be  printed."
	FnEnd
	!
	!	Cap
	Def Cap(Arg.1.$)
	Word.$ = Edit$(Arg.1.$, 32%)
	Char.$(FF%) = Seg$(Word.$, FF%, FF%) For FF% = 1% to Len(Word.$)
	Char.$(FF%) = Chr$(Ascii(Char.$(FF%))+32% For FF% = 2% to Len(Word.$)
	Word.New.$ = ""
	Word.New.$ = Word.New.$+Char.$(FF%) For FF% = 1% to Len(Word.$)
	Cap = Word.New.$
	FnEnd
	!
	!	Alphabetize
	Def Alphabetize(Arg.1.%)
	Iterate.Flag.% = -1%
	For I% = 1% while Iterate.Flag.%
	Change.Flag.% = 0%
		For N% = 1% to Arg.1.%-1%
		If Name.A.$(N%) > Name.A.$(N%+1%) then
			Store.$ = Name.A.$(N%)
			Name.A.$(N%) = Name.A.$(N%+1%)
			Name.A.$(N%+1%) = Store.$
			Store.$ = Cour.$(N%)
			Cour.$(N%) = Cour.$(N%+1%)
			Cour.$(N%+1%) = Store.$
			Store.$ = Fee.$(N%)
			Fee.$(N%) = Fee.$(N%+1%)
			Fee.$(N%+1%) = Store.$
			Store.% = Fee.%(N%)
			Fee.%(N%) = Fee.%(N%+1%)
			Fee.%(N%+1%) = Store.%
			Change.Flag.% = -1%
15035		Next N%
	Iterate.Flag.% = 0% Unless Change.Flag.%
	Next I%
	FnEnd
	!
	!
19000	!		S t a n d a r d   E r r o r   H a n d l i n g
	!
19010	If Err = 28% then
		Resume 32000
	!
19020	If Erl = 1100% then
		Print "Illegal entry"
		Resume 1100
	!
19030	If Erl = 3000% then
		Close #2%
		Execute.Flag.% = 0%
		Resume 3100
	!
19040	If Erl = 4000% then
		Close #2%
		Execute.Flag.% = 0%
		Resume 4100
	!
19050	If Erl = 5010% then
		Close #2%
		Execute.Flag.% = 0%
		Resume 5100
	!
19999	On Error goto 0
	!
	!
32000	!		E n d   o f   P r o c e s s i n g
	!
	!
32767	!		E n d   o f   P r o g r a m
	!
	!	!	End
