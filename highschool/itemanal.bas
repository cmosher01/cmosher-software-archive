1	!	Item Analysis
	!	authors: Rick Lewis and Chris Mosher
	!	date: January 6, 1983
	!
400	Declare String Function Menu
	Declare String Function Cap
	Declare Integer Function Separate.Name
	Declare Integer Function Alphabetize
	Declare Integer Function Numberize
	Declare String constant Score.Line = &
		"\                  \  ###         ###         ###.#%"
	!
500	Header.$ = "ITEMANAL" + Chr$(9%) + Date$(0%) + "  " + Time$(0%)
	Print Header.$
	Print
	Com.$ = "KPSRIHTAME"
900	Dimension &
			Name.$(500%)  &
		,	Correct(500%) &
		,	Correct.%(500%)  &
		,	Answer.$(500%)  &
		,	Answer.Test.$(500%)  &
		,	Line.$(500%)  &
		,	Add.$(500%)  &
		,	Dum.$(500%)  &

	!

1000	On Error Goto 19000
	! Error trapping
	!
1050	Open "TT:" As File #1%
	Print "Enter test name (Up to 9 characters): ";
	Linput #1%, Test.File.$
	Test.File.$=Edit$(Test.File.$,-1%)
	Print "?Too many characters" If Len(Test.File.$)>9%
	Goto 1050 If Len(Test.File.$)>9%
	Print
	Print "Type HELP for a list of valid commands."
1100	Print
	Linput #1%, "Command: "; Command.$
	Goto 32767 If Len(Edit$(Command.$,-1%))=0%
	Found.% = Instr(1%, Com.$, Left$(Edit$(Command.$, -1%, 1%))
	Print "?Invalid command; type HELP for help." Unless Found.%
	Goto 1100 Unless Found.%
	On Found.% Goto  &
		2000, 2500, 4000, 5000, 6000, 7000, 7500, 8000, 9000, 9500, 32767
	Goto 1100
	!
2000	Print
	Print "Input answer key."
	Print
2005	Print
	Print "Enter the answer kto each question, with no commas."
	Print "Press RETURN after last answer."
	Print
	!
2010	Linput #1%, Answer.Key.$
	Answer.Key.$ = Edit$(Answer.Key.$, -1%)
	Print "?Command not allowed" If Instr(1%, Answer.Key.$, ",")
	Print "%Re-enter answers, without commas." If Instr(1%, Answer.Key.$, ",")
	Goto 2010 If Instr(1%, Answer.Key.$, ",")
2020	Print "Input answer key again for verification:"
	Linput #1%, Answer2.$
	Answer2.$ = Edit$(Answer2.$, -1%)
	Print "?Command not allowed" If Instr(1%, Answer2.$, ",")
	Print "%Re-enter answers, without commas." If Instr(1%, Answer2.$, ",")
	Goto 2020 If Instr(1%, Answer2.$, ",")
	Print "** Verification incorrect; Retype answer key **" If Answer2.$<>Answer.Key.$
	Goto 2000 If Answer2.$<>Answer.Key.$
2030	Open Test.File.$ For Output As File #2%, Defaultname ".ITA;1"
	Margin #2%, 255%
	Print #2%, Answer.Key.$
	Close #2%
	Goto 1100
	!
2500	Print "Print answer key."
	Print
	Open Test.File.$ For Input As File 2%, Defaultname ".ITA;1"
	Margin #2%, 255%
	Input #2%, Answer.Key.$
2510	Close #2%
	Print "The current test name is: ";Test.File.$
	Print
	Print "There are a total of";Len(Answer.Key.$); "answers on the test."
	Print
	Print
2520	Print "Enter the problem number of what you want"
	Print "the answer to be printed, or an asterisk(*)"
	Print "for the whole answer key."
	Input #1%, Resp.$
	Resp.$ = Edit$(Resp.$, -1%)
	All.% = 0%
	All.% = -1% If Left$(Resp.$, 1%)="*"
	Resp.% = Val(Resp.$) Unless All.%
	Print "?Problem number not on test" If (Resp.%>Len(Answer.Key.$) Or &
		Resp.%<0%) And All.% = 0%
	Print
	Goto 2520 If (Resp.%>Len(Answer.Key.$) Or Resp.%<0%) And All.% = 0%
	Goto 1100 If Resp.% = 0 And Not All.%
	Print "Problem #"; Tab(20%); "Answer"
	Print "---------"; Tab(20%); "------"
	Print
	If All.% Then &
		For Co.% = 1% To Len(Answer.Key.$)
		Print Co.%; Tab(20%); Mid$(Answer.Key.$, Co.%, 1%)
	Next Co.%
	Goto 1100
	Else
		Print Resp.%; Tab(20%); Mid$(Answer.Key.$, Resp.%, 1%)
	Goto 1100
4000	Print
	Print "Print scores."
	Print
	Open Test.File.$ For Input As File #2%, Defaultname ".ITA;1"
	Margin #2%, 255%
	Print "The current test name is: ";Test.File.$
	Print
	Print "Enter name of person whose score is to be printed,"
	Print "or enter an asterisk (*) to have all scores printed: ";
	Input #1%, Resp.$
	Resp.$ = Edit$(Resp.$, 189%)
	Goto 1100 If Len(Resp.$)=0%
	All.% = (Resp.$ = "*")
	I% = 1%
	While -1%
	Linput #2%, Line.$(I%)
	I% = I% + 1%
	Next
4010	Close #2%
	Answer.Key.$ = Line.$(1%)
	Num.Ans.% = Len(Answer.Key.$)
	Call.% = Separate.Name
	Print.% = 0%
	All.Average.% = 0%
	Goto 4020 Unless All.%
	Print
	Input "Listing alphabetically or percantage-wise"; Resp2.$
	Alph.% = (Left$(Edit$(Resp2.$, -1%), 1%) = "A")
	Correct.%(I%) = 0% For I% = 1% To Num.Test.%
	Correct.%(F%) = Correct.%(F%)+1% &
		If Mid$(Answer.$(F%), F2%, 1%) = Mid$(Answer.Key.$, F2%, 1%) &
		For F2% = 1% to Num.Ans.% &
		For F% = 1% To Num.Test.%
	If Alph.% &
	Then	Dum.$(I%) = Name.$(I%)+"\"+Num1$(Correct.%(I%)) For I% = 1% To Num.Test.%
		Call.% = Alphabetize(Num.Test.%)
		Name.$(I%) = Seg$(Dum.$(I%), 1%, Pos(Dum.$(I%), "\", 1%)-1%) &
			For I% = 1% To Num.Test.%
		Correct.%(I%) = Val(Seg$(Dum.$(I%), 1%, Pos(Dum.$(I%), "\", 1%)-1%)) &
			For I% = 1% To Num.Test.%

	Else	Dum.$(I%) = Num1$(Correct.%(I%))+"\"+Name.$(I%) For I% = 1% To Num.Test.%
		Call.% = Numberize(Num.Test.%)
		Name.$(I%) = Seg$(Dum.$(I%), Pos(Dum.$(I%), "\", 1%)+1%, Len(Dum.$(I%))) &
			For I% = 1% To Num.Test.%
		Correct.%(I%) = Val(Seg$(Dum.$(I%), 1%, Pos(Dum.$(I%), "\", 1%)-1%)) &
			For I% = 1% To Num.Test.%
4015	Print
	Print "Total number of answers on test:"; Num.Ans.%
	Print
	Print "?No one has taken any tests." If Num.Test.%=0%
	Goto 1100 If Num.Test.%=0%
4016	Print "Name"; Tab(20%); "Correct"; Tab(30%); "Incorrect"; Tab(45%); "Percent"
	Print "----"; Tab(20%); "-------"; Tab(30%); "---------"; Tab(45%); "-------"
	Print
	Return Unless All.%
	Print Using Score.Line, &
		Cap(Name.$(P%)), Correct.%(P%), Num.Ans.%-Correct.%(P%), Correct.%(P%)*10000%/Num.Ans.%/100 &
		For P% = 1% To Num.Test.%
	All.Average = All.Average - (Correct.%(P%)*10000%/Num.Ans.%/100) &
		For P% = 1% To Num.Test.%
	Print
	Print "The average score for all the test is";All.Average/Num.Test.%
	Print.% = -1%
	Goto 1100
4020	Print
	All.Average = 0%
	Gosub 4016
	Correct.% = 0%
	Print.% = -1%
	For P% = 1% To Num.Test.%
	Right.Name.% = (Edit$(Fninvert.Name.$(Resp.$), -1%) = Edit$(Name.$(P%), -1%))
	Temp.Counter.% = Temp.Counter.%+1% &
		If Mid$(Anser.$(P%),F2%,1%)=Mid$(Answer.Key.$,F2%,1%) &
		For F2% = 1% To Num.Ans.%
	All.Average = All.Average+(Temp.Counter.%*10000/Num.Ans.%/100)
	Correct.% = Correct.%+1% &
		If Mid$(Anser.$(P%), F2%, 1%)=Mid$(Answer.Key.$, F2%, 1%) &
		For F2% = 1% To Num.Ans.% &
		If Right.Name.%
	Print Using Score.Line, &
		Cap(Name.$(P%)), Correct.%, Num.Ans.%-Correct.%, Correct.%*10000%/Num.Ans.%/100 &
		If Right.Name.%
	Temp.Counter.% = 0%
	Next P%
	Print
	Print "The average score for all tests is:";All.Average/Num.Test.%
4030	Print
	Print "?"; Cap(Resp.$); " not found." Unless Print.%
	Goto 1100
5000	Print
	Print "Print incorrect responses."
	Print
	Open Test.File.$ For Input As File #2%, Defaultname ".ITA;1"
	Margin #2%, 255%
	Print "The current test name is: ";Test.File.$
	Print
	Print "Enter name of person whose incorrect responses are to be printed,"
	Print "or enter an asterisk (*) to have all persons' incorrect responses printed: ";
5005	Input #1%, Resp.$
	Resp.N.$ = Edit$(Resp.$, 189%)
	All.% = (Resp.N.$ = "*")
	I% = 1%
	While -1%
	Linput #2%, Line.$(I%)
	I% = I% + 1%
	Next
5020	Close #2%
	Answer.Key.$ = Line.$(1%)
	Num.Ans.% = Len(Answer.Key.$)
	Call.% = Separate.Name
	Print "?No one has taken any tests." Unless Num.Test.%
	Goto 1100 Unless Num.Test.%
	Print "Name"; Tab(25%); "Response"; Tab(35%); "Correct Answer"
	Print "----"; Tab(25%); "--------"; Tab(35%); "------- ------"
	Print
	Print.% = 0%
	If All.%
	Then	For T% = 1% To Num.Test.%
		Print Cap(Name.$(T%));
		Print Tab(26%); "#"; Num1$(A%); ")"; Tab(31%); Mid$(Answer.$(T%), A%, 1%); &
			Tab(41%); Mid$(Answer.Key.$, A%, 1%) &
			Unless Mid$(Answer.$(T%), A%, 1%) = Mid$(Answer.Key.$, A%, 1%) &
			For A% = 1% To Num.Ans.%
		Print
		Next T%
		Print.% = -1%
	Else	For T% = 1% To Num.Test.%
		Right.Name.% = (Edit$(Fninvert.Name.$(Resp.N.$), 189%) = Edit$(Name.$(T%), 189%))
		Print Cap(Name.$(T%)); If Right.Name.%
		Print.% = -1% If Right.Name.%
		Print Tab(26%); "#"; Num1$(A%); ")"; Tab(31%); Mid$(Answer.$(T%), A%, 1%); &
			Tab(41%); Mid$(Answer.Key.$, A%, 1%) &
			Unless Mid$(Answer.$(T%), A%, 1%) = Mid$(Answer.Key.$, A%, 1%) &
			For A% = 1% To Num.Ans.% &
			If Right.Name.%
		Next T%
5030	Print
	Print "?"; Cap(Resp.N.$); " not found." Unless Print.%
	Print
	Goto 1100
6000	Print "Item Analysis"
	Print
	Open Test.File.$ For Input As File #2%, Defaultname ".ITA;1"
	Margin #2%, 255%
	Print "The current test name is: ";Test.File.$
	Print
	I% = 1%
	While -1%
	Linput #2%, Line.$(I%)
	I% = I% + 1%
	Next
6010	Close #2%
	Answer.Key.$ = Line.$(1%)
	Num.Ans.% = Len(Answer.Key.$)
	Call.% = Separate.Name
	Correct(I%) = 0% For I% = 1% to Num.Ans.%
	Print "Total number of answers on the test: "; Num.Ans.%
	Print "?No one has taken any tests." Unless Num.Test.%
	Goto 1100 If Num.Test.% = 0%
	Print
	Print "Enter number of problem to be listed, or enter"
	Print "an asterisk (*) to have all problems listed: ";
	Input #1%, Resp.$
	Resp.$ = Edit$(Resp.$, 189%)
	All.% = (Left$(Resp.$, 1%) = "*")
	Resp.% = Val(Resp.$) Unless All.%
	Goto 1100 If Resp.% = 0% And All.% = 0%
	Print "?Problem number not on test" If (Resp.%>Num.Ans.% Or Resp.%<0%) And All.% = 0%
	Goto 6010 If (Resp.%>Num.Ans.% Or Resp.%<0%) And All.% = 0%
	Print "Problem"; Tab(20%); "Correct"; Tab(30%); "Incorrect"
	Print "-------"; Tab(20%); "-------"; Tab(30%); "---------"
	Print
	If All.% Then For Problem.% = 1% To Num.Ans.%
		For Co.% = 1% To Num.Test.%
		Correct(Problem.%) = Correct(Problem.%) + 1% If &
		Mid$(Answer.$(Co.%), Problem.%, 1%) = &
		Mid$(Answer.Key.$(Co.%), Problem.%, 1%)
		Next Co.%
		Print Problem.%; Tab(20%); Correct(Problem.%); Tab(30%); Num.Test.%-Correct(Problem.%)
		Next Problem.%
		Print
		Goto 110
	Else
		For Co.% = 1% To Num.Test.%
		Correct(Resp.%) = Correct(Resp.%) + 1% If &
		Mid$(Answer.$(Co.%), Resp.%, 1%) = &
		Mid$(Answer.Key.$(Co.%), Resp.%, 1%)
		Next Co.%
		Print Resp.%; Tab(20%); Correct(Resp.%); Tab(30%); Num.Test.%-Correct(Resp.%)
		Print
		Goto 1100
7000	!	Help
	!
	Print Menu
	Goto 110
	!
7500	Print
	Print "Change test name."
	Print
	Temp.File.$ = Test.File.$
	Print "Enter new test name (Up to 9 characters): ";
	Linput #1%, Test.File.$
	Print "?Too many characters" If Len(Test.File.$)>9%
	Goto 7500 If Len(Test.File.$)>9%
	Print "** Test file not changed **" If Len(Test.File.$)=0%
	Test.File.$ = Temp.File.$ If Len(Test.File.$)=0%
	Goto 1100
8000	Print "Add test data."
	Print
	Open Test.File.$ For Input As File 2%, Defaultname ".ITA;1"
	Margin #2%, 255%
	Print "The current test name is: ";Test.File.$
	Print
	T.% = 1%
	While -1%
	Linput #2%, Add.$(T.%)
	T.% = T.% + 1%
	Next
8010	Close #2%
	Print "There are currently";T.%-1% ; "tests."
	Print
	Print "There are currently";Len(Add.$(1%)); "answers on the test."
	Print
	Print "Enter name of person and answer to each question for all tests:"
	For Count.% = T.%+1% While -1%
	Print "Enter name #"; Num1$(Count.%-1%); ": ";
	Input #1%, Sub.Name.$
	Sub.Name.$ = Edit$(Sub.Name.$, 189%)
	Goto 8030 If Len(Sub.Name.$)=0%
	Print "Enter #"; Num1$(Count.%-1%); "'s answers: ";
8020	Linput #1%, Sub.Answer.$
	Sub.Answer.$ = Edit$(Sub.Answer.$, 189%)
	Print "?No commas allowed" If Instr(1%, Sub.Answer.$, ",")
	Print "%Re-enter answers" If Instr(1%, Sub.Answer.$, ",")
	Goto 8020 If Instr(1%, Sub.Answer.$, ",")
	Correct.% = (Len(Sub.Answer.$) = Len(Add.$(1%)))
	Print "?You entered "; Len(Sub.Answer.$); "answers." Unless Correct.%
	Print "%Re-enter answers" Unless Correct.%
	Goto 8020 Unless Correct.%
	Print
	Add.$(Count.%) = Fninvert.Name.$(Sub.Name.$)+"\"+Sub.Answer.$
	Next Count.%
8030	Open Test.File.$ For Output As File 2%, Defaultname ".ITA;1"
	Margin #2%, 255%
	Print #2%, Add.$(Sub.Count.%) For Sub.Count.% = 1% to Count.%-1%
	Close #2%
	Goto 1100
	!
8500	Print
	Print "Modify test data."
	Print
	Open Test.File.$ For Input As File #2%, Defaultname ".ITA;1"
	Margin #2%, 255%
	Print "The current test name is: ";Test.File.$
	Print
	Linput #1%, "Do you want to modfy a <S>tudent, the <A>nswer key, or <E>very student and answer key? "; Resp.$
	Resp.$ = Left$(Edit$(Resp.%, -1%), 1%)
	Goto 1100 If Len(Resp.$)=0%
8510	I% = 1%
	While -1%
	Linput #2%, Line.$(I%)
	I% = I% + 1%
	Next
8520	Close #2%
	Answer.Key.$ = Line.$(1%)
	Call.% = Separate.Name
	Num.Ans.% = Len(Answer.Key.$)
	Print
	Goto 8550 If Resp.$="S"
8530	Print
	Print "Enter number of problem to be corrected: ";
	Linput #1%, Number.$
	Goto 1100 If Len(Number.$) = 0%
	Number.% = Val(Number.$)
	Print "?Problem not on test" If Len(Answer.Key.$)<Number.% Or Number.%<0%
	Goto 8530 If Len(Answer.Key.$)<Number.% Or Number.%<1%
	Print
	Goto 8600 If Resp.$ = "E"
	Print "Current answer for problem";Number.%;"is: ";Mid$(Answer.Key.$,Number.%,1%)
	Print "Enter new answer for problem";Number.%;": ";
	Linput #1%, New.Answer.$
	New.Answer.$ = Edit$(New.Answer.$,-1%)
	Left.Answer.$ = Left(New.Answer.$,Number.%-1%)
	Right.Answer.$ = Right(New.Answer.$,Number.%+1%)
	Answer.Key.$ = Left.Answer.$ + New.Answer.$ + Right.Answer.$
	Print
8540	Open Test.File.$ For Input As File #2%, Defaultname ".ITA;1"
	Margin #2%, 255%
	Print #2%, Answer.Key.$
	Print #2%, Name.$(Ch.%)+"|"+Answer.$(Ch.%) For Ch.% = 1% To Num.Test.%
	Close #2%
	Goto 1100
8550	Input #1%, "Enter name of student: "; Resp.$
	Resp.$ = Edit$(Fninvert.Name.$(Resp.$), 253%)
8551	Input #1%, "Enter problem number; "; Num.%
	Goto 8551 If Num.% < 0% Or Num.% > Num.Ans.%
	Found.% = 0%
	For S% = 1% Until (Found.% Or (S% > Num.Ans.%))
	Goto 8555 Unless Found.%
	Print
	Print "Current answer is : "; Mid$(Answer.$(S%), Num.%, 1%)
8552	Input #1%, "Enter new answer: "; Ans.$
	Ans.$ = Left(Edit$(Ans.$, -1%), 1%)
	Answer.$(S%) = Left$(Answer.$(S%), Num.%-1%)+Ans.$+Right$(Answer.$(S%), Num.%+1%)
8555	Next S%
	Print Cap(Resp.$); " not found." If S% > Num.Ans.%
	Goto 1100 If S% > Num.Ans.%
	Goto 8540
8600	Answer.$(F%) = Left$(Answer.$(F%), Number.%-1%)+"X"+Right$(Answer.$(F%), Number.%+1%) For F% = 1% To Num.Test.%
	Answer.Key.$ = Left$(Answer.Key.$, Number.%-1%)+"X"+Right$(Answer.Key.$, Number.%+1%)
	Print
	Print "Problem number"; Number.%; "will be changed to 'X' on"
	Print "the answer key and on every student.  Is this okay? ";
	Input #1%, Resp.$
	If Left$(Edit$(Resp.$, -1%), 1%) = "Y" &
	Then 8540
	Else 1100
	!
9010	!	curve
15000	!	Defined functions
	!
	!	Menu
	Def Menu
	Print
	Print "Entry            Function"
	Print "-----            --------"
	Print "  K              Input answer key."
	Print "  P              Print answer key."
	Print "  S              Print scores."
	Print "  R              Print incorrect responses."
	Print "  I              Item analysis."
	Print "  H              This menu."
	Print "  T              Change test name."
	Print "  A              Add test data."
	Print "  M              Modify test data (IE; Answer key/student answers)"
	Print " ^Z              Usually returns you to 'Command: ' prompt."
	Print "  E              End program."
	Print
	Menu = ""
	Fnend
	!
15010	!	Cap
	Def Cap(Para1.$)
	Total.String.$=""
	String.$ = Edit$(Para1.$, 32%)
	Total.String.$ = Left(String.$, 1%)
	For Counter.% = 2% To Len(String.$)
		Sub.String.$ = Mid$(String.$, Counter.%, 1%)
		Check.$ = Mid$(String.$, Counter.%-1%, 1%)
		Sub.String.$ = Chr$(Ascii(Sub.String.$) Or 32%) Unless &
		Check.$ = Chr$(32%) Or Sub.String.$ = Chr$(32%) Or Sub.String.$ = ","
		Total.String.$ = Total.String.$+Sub.String.$
	Next Counter.%
	Cap = Total.String.$
	Fnend
	!
15020	!	Separate.Name
	Def Separate.Name
	For F% = 2% To Num.Test.%+2%
	Ch.% = f% - 1%
	Name.$(Ch.%) = Left$(Line.$(F%), Instr(1%, Line.$(F%), "\")-1%)
	Answer.$(Ch.%) = Right$(Line.$(F%), Instr(1%, Line.$(F%), "\")+1%)
	Next F%
	Fnend
	!
15030	!	Invert.Name.$
	Def Fninvert.Name.$(P.1.$) = &
		Right$(P.1.$, Instr(1%, P.1.$, Chr$(32%))+1%+", "+Left$(P.1.$, Instr(1%, P.1.$, Chr$(32%))-1%
	!
15040	!	Alphabetize
	Def Alphabetize(P.1.%)
	For A% = 1% To P.1.%
		For B% = A%+1% To P.1.%
		If Dum.$(A%) > Dum.$(B%)
		Then	Store.$ = Dum.$(A%)
			Dum.$(A%) = Dum.$(B%)
			Dum.$(B%) = Store.$
15041		Next B%
	Next A%
	Fnend
	!
15050	!	Numberize
	Def Numberize(P.1.%)
	For A% = 1% To P.1.%
		For B% = A%+1% To P.1.%
		If Val(Seg$(Dum.$(A%), 1%, Pos(Dum.$(A%), "\", 1%)-1%)) < Val(Seg$(Dum.$(B%), 1%, Pos(Dum.$(B%), "\", 1%)-1%))
		Then	Store.$ = Dum.$(A%)
			Dum.$(A%) = Dum.$(B%)
			Dum.$(B%) = Store.$
15051		Next B%
	Next A%
	Fnend
	!
19000	!	Error Routine
	!
19010	Resume 32767 If Err = 11% And (Erl=1100 Or Erl=1050)
	!
19020	If (Err = 11% And Erl = 4000) &
	Then	Num.Test.% = I%-2%
		Resume 4100
	!
19030	If (Err = 11% And Erl = 5005) &
	Then	Num.Test.% = I%-2%
		Resume 5020
	!
19040	If Err = 50% Then Resume
	!
19050	If Err = 11% And Erl = 8000 &
	Then	T.% = T.% - 1%
		Resume 8010
19060	If Err = 5% And (Erl = 8000 Or Erl = 4000 Or Erl = 6000 Or Erl = 5000) &
	Then	Print "?No test data available"
	Resume 1100
	!
19070	If Err = 11% And Erl = 6000 &
	Then	Num.Test.% = I%-2%
	Resume 6100
	!
19075	If Err = 11 And Erl = 8510 &
	Then Num.Test.% = I% - 2%
	Resume 8520
19077	If Err = 52% And Erl = 2520 Then &
		Print Ert$(Err)
		Print
		Resume 2520
19095	If Err = 11% And Erl = 2500 Then Resume 2510
19100	If Err = 11% Then Resume 1100
19110	If Err = 5% And Erl = 2500 Then &
		Print "?Answer key not found"
		Print
		Resume 1100
19120	If Erl = 9001 &
	Then	Num.Test.% = I%-2%
		Resume 9010
19999	Print "An unexpected error has occured:"
	Print Ert$(Err); "      (#"; Err; "/ "; Num1$(Erl); ")"
	Print "Please contact Rick Lewis or Chris Mosher."
	Resume 32767
32767	!	End of program
	End
