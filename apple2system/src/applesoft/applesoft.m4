include(`asm.m4h')
                                                    ; --------------------------------
                                                    ;
                                                    ; Applesoft BASIC, V2
                                                    ;
                                                    ; Written by Marc McDonald and Randy Wigginton.
                                                    ;
                                                    ; Original copyright 1976 by Microsoft,
                                                    ; 1977 by Apple Computer.
                                                    ;
                                                    ; Disassembled by (unknown).
                                                    ; Fixed by Chris Mosher.
                                                    ; 
                                                    ; For the cc65.org Assembler (ca65)
                                                    ;
                                                    ; Applesoft BASIC was first written by
                                                    ; Marc McDonald, the first employee of Microsoft,
                                                    ; in mid-1976. That version was bought by Apple
                                                    ; and released (on casette) in Nov. 1977.
                                                    ;
                                                    ; Version 2 was written by Randy Wigginton and
                                                    ; others at Apple in spring 1978. This version
                                                    ; was released in several different forms. The
                                                    ; one reproduced by this source assembly file is
                                                    ; the main-board ROM form, which appeared in the
                                                    ; Apple ][ plus ROM at $D000-$F7FF.
                                                    ;
                                                    ; --------------------------------



                                                    ; --------------------------------
                                                    ; ZERO PAGE LOCATIONS:
                                                    ; --------------------------------
GOWARM              = $00                           ; GETS "JMP RESTART"
GOSTROUT            = $03                           ; GETS "JMP STROUT"
USR                 = $0A                           ; GETS "JMP <USER ADDR>"
                                                    ; (INITIALLY $E199)
CHARAC              = $0D                           ; ALTERNATE STRING TERMINATOR
ENDCHR              = $0E                           ; STRING TERMINATOR
TKN_CNTR            = $0F                           ; USED IN PARSE
EOL_PNTR            = $0F                           ; USED IN NXLIN
NUMDIM              = $0F                           ; USED IN ARRAY ROUTINES
DIMFLG              = $10                           ; 
VALTYP              = $11                           ; $:VALTYP=$FF; %:VALTYP+1=$80
DATAFLG             = $13                           ; USED IN PARSE
GARFLG              = $13                           ; USED IN GARBAG
SUBFLG              = $14                           ; 
INPUTFLG            = $15                           ; = $40 FOR GET, $98 FOR READ
CPRMASK             = $16                           ; RECEIVES CPRTYP IN FRMEVL
SIGNFLG             = $16                           ; FLAGS SIGN IN TAN
HGR_SHAPE           = $1A                           ; 
HGR_BITS            = $1C                           ; 
HGR_COUNT           = $1D                           ; 
MON_CH              = $24                           ; 
MON_GBASL           = $26                           ; 
MON_GBASH           = $27                           ; 
MON_H2              = $2C                           ; 
MON_V2              = $2D                           ; 
MON_HMASK           = $30                           ; 
MON_INVFLG          = $32                           ; 
MON_PROMPT          = $33                           ; 
MON_A1L             = $3C                           ; USED BY TAPE I/O ROUTINES
MON_A1H             = $3D                           ; "
MON_A2L             = $3E                           ; "
MON_A2H             = $3F                           ; "
LINNUM              = $50                           ; CONVERTED LINE #
TEMPPT              = $52                           ; LAST USED TEMP STRING DESC
LASTPT              = $53                           ; LAST USED TEMP STRING PNTR
TEMPST              = $55                           ; HOLDS UP TO 3 DESCRIPTORS
INDEX               = $5E                           ; 
DEST                = $60                           ; 
RESULT              = $62                           ; RESULT OF LAST * OR /
TXTTAB              = $67                           ; START OF PROGRAM TEXT
VARTAB              = $69                           ; START OF VARIABLE STORAGE
ARYTAB              = $6B                           ; START OF ARRAY STORAGE
STREND              = $6D                           ; END OF ARRAY STORAGE
FRETOP              = $6F                           ; START OF STRING STORAGE
FRESPC              = $71                           ; TEMP PNTR, STRING ROUTINES
MEMSIZ              = $73                           ; END OF STRING SPACE (HIMEM)
CURLIN              = $75                           ; CURRENT LINE NUMBER
                                                    ; ( = $FFXX IF IN DIRECT MODE)
OLDLIN              = $77                           ; ADDR. OF LAST LINE EXECUTED
OLDTEXT             = $79                           ; 
DATLIN              = $7B                           ; LINE # OF CURRENT DATA STT.
DATPTR              = $7D                           ; ADDR OF CURRENT DATA STT.
INPTR               = $7F                           ; 
VARNAM              = $81                           ; NAME OF VARIABLE
VARPNT              = $83                           ; ADDR OF VARIABLE
FORPNT              = $85                           ; 
TXPSV               = $87                           ; USED IN INPUT
LASTOP              = $87                           ; SCRATCH FLAG USED IN FRMEVL
CPRTYP              = $89                           ; >,=,< FLAG IN FRMEVL
TEMP3               = $8A                           ; 
FNCNAM              = $8A                           ; 
DSCPTR              = $8C                           ; 
DSCLEN              = $8F                           ; USED IN GARBAG
JMPADRS             = $90                           ; GETS "JMP ...."
LENGTH              = $91                           ; USED IN GARBAG
ARG_EXTENSION       = $92                           ; FP EXTRA PRECISION
TEMP1               = $93                           ; SAVE AREAS FOR FAC
ARYPNT              = $94                           ; USED IN GARBAG
HIGHDS              = $94                           ; PNTR FOR BLTU
HIGHTR              = $96                           ; PNTR FOR BLTU
TEMP2               = $98                           ; 
TMPEXP              = $99                           ; USED IN FIN (EVAL)
INDX                = $99                           ; USED BY ARRAY RTNS
EXPON               = $9A                           ; "
DPFLG               = $9B                           ; FLAGS DEC PNT IN FIN
LOWTR               = $9B                           ; 
EXPSGN              = $9C                           ; 
FAC                 = $9D                           ; MAIN FLT PT ACCUMULATOR
DSCTMP              = $9D                           ; 
VPNT                = $A0                           ; TEMP VAR PTR
FAC_SIGN            = $A2                           ; HOLDS UNPACKED SIGN
SERLEN              = $A3                           ; HOLDS LENGTH OF SERIES-1
SHIFT_SIGN_EXT      = $A4                           ; SIGN EXTENSION, RIGHT SHIFTS
ARG                 = $A5                           ; SECONDARY FP ACC
ARG_SIGN            = $AA                           ; 
SGNCPR              = $AB                           ; FLAGS OPP SIGN IN FP ROUT.
FAC_EXTENSION       = $AC                           ; FAC EXTENSION BYTE
SERPNT              = $AD                           ; PNTR TO SERIES DATA IN FP
STRNG1              = $AB                           ; 
STRNG2              = $AD                           ; 
PRGEND              = $AF                           ; 
CHRGET              = $B1                           ; 
CHRGOT              = $B7                           ; 
TXTPTR              = $B8                           ; 
RNDSEED             = $C9                           ; 
HGR_DX              = $D0                           ; 
HGR_DY              = $D2                           ; 
HGR_QUADRANT        = $D3                           ; 
HGR_E               = $D4                           ; 
LOCK                = $D6                           ; NO USER ACCESS IF > 127
ERRFLG              = $D8                           ; $80 IF ON ERR ACTIVE
ERRLIN              = $DA                           ; LINE # WHERE ERROR OCCURRED
ERRPOS              = $DC                           ; TXTPTR SAVE FOR HANDLERR
ERRNUM              = $DE                           ; WHICH ERROR OCCURRED
ERRSTK              = $DF                           ; STACK PNTR BEFORE ERROR
HGR_X               = $E0                           ; 
HGR_Y               = $E2                           ; 
HGR_COLOR           = $E4                           ; 
HGR_HORIZ           = $E5                           ; BYTE INDEX FROM GBASH,L
HGR_PAGE            = $E6                           ; HGR=$20, HGR2=$40
HGR_SCALE           = $E7                           ; 
HGR_SHAPE_PNTR      = $E8                           ; 
HGR_COLLISIONS      = $EA                           ; 
FIRST               = $F0                           ; 
SPEEDZ              = $F1                           ; OUTPUT SPEED
TRCFLG              = $F2                           ; 
FLASH_BIT           = $F3                           ; = $40 FOR FLASH, ELSE =$00
TXTPSV              = $F4                           ; 
CURLSV              = $F6                           ; 
REMSTK              = $F8                           ; STACK PNTR BEFORE EACH STT.
HGR_ROTATION        = $F9                           ; 
                                                    ; $FF IS ALSO USED BY THE STRING OUT ROUTINES
                                                    ; --------------------------------
STACK               = $0100
INPUT_BUFFER        = $0200
AMPERSAND_VECTOR    = $03F5                         ; - 3F7   GETS "JMP ...."
                                                    ; --------------------------------
                                                    ; I/O & SOFT SWITCHES
                                                    ; --------------------------------
KEYBOARD            = $C000
SW_TXTCLR           = $C050
SW_MIXCLR           = $C052
SW_MIXSET           = $C053
SW_LOWSCR           = $C054
SW_HISCR            = $C055
SW_LORES            = $C056
SW_HIRES            = $C057
                                                    ; --------------------------------
                                                    ; MONITOR SUBROUTINES
                                                    ; --------------------------------
MON_PLOT            = $F800
MON_HLINE           = $F819
MON_VLINE           = $F828
MON_SETCOL          = $F864
MON_SCRN            = $F871
MON_PREAD           = $FB1E
MON_SETTXT          = $FB39
MON_SETGR           = $FB40
MON_TABV            = $FB5B
MON_HOME            = $FC58
MON_WAIT            = $FCA8
MON_RD2BIT          = $FCFA
MON_RDKEY           = $FD0C
MON_GETLN           = $FD6A
MON_COUT            = $FDED
MON_INPORT          = $FE8B
MON_OUTPORT         = $FE95
MON_WRITE           = $FECD
MON_READ            = $FEFD
MON_READ2           = $FF02
                                                    ; --------------------------------
                                                    ; --------------------------------
                                                    ; APPLESOFT TOKENS
                                                    ; --------------------------------
TOKEN_FOR           = $81
TOKENDWTA           = $83
TOKEN_POP           = $A1
TOKEN_GOTO          = $AB
TOKEN_GOSUB         = $B0
TOKEN_REM           = $B2
TOKEN_PRINT         = $BA
TOKEN_TAB           = $C0
TOKEN_TO            = $C1
TOKEN_FN            = $C2
TOKEN_SPC           = $C3
TOKEN_THEN          = $C4
TOKENDB             = $C5
TOKEN_NOT           = $C6
TOKEN_STEP          = $C7
TOKEN_PLUS          = $C8
TOKEN_MINUS         = $C9
TOKEN_GREATER       = $CF
TOKENEQUUAL         = $D0
TOKEN_SGN           = $D2
TOKEN_SCRN          = $D7
TOKEN_LEFTSTR       = $E8
                                                    ; --------------------------------
                                                    ; BRANCH TABLE FOR TOKENS
                                                    ; --------------------------------
TOKEN_ADDRESS_TABLE ASM_ADDR(`ENDX-1')                    ; $80...128...END
                    ASM_ADDR(`FOR-1')                     ; $81...129...FOR
                    ASM_ADDR(`NEXT-1')                    ; $82...130...NEXT
                    ASM_ADDR(`DATA-1')                    ; $83...131..DWTA
                    ASM_ADDR(`INPUT-1')                   ; $84...132...INPUT
                    ASM_ADDR(`DEL-1')                     ; $85...133...DEL
                    ASM_ADDR(`DIM-1')                     ; $86...134...DIM
                    ASM_ADDR(`READ-1')                    ; $87...135...READ
                    ASM_ADDR(`GR-1')                      ; $88...136...GR
                    ASM_ADDR(`TEXT-1')                    ; $89...137...TEXT
                    ASM_ADDR(`PR_NUMBER-1')               ; $8A...138...PR#
                    ASM_ADDR(`IN_NUMBER-1')               ; $8B...139...IN#
                    ASM_ADDR(`CALL-1')                    ; $8C...140...CALL
                    ASM_ADDR(`PLOT-1')                    ; $8D...141...PLOT
                    ASM_ADDR(`HLIN-1')                    ; $8E...142...HLIN
                    ASM_ADDR(`VLIN-1')                    ; $8F...143...VLIN
                    ASM_ADDR(`HGR2-1')                    ; $90...144...HGR2
                    ASM_ADDR(`HGR-1')                     ; $91...145...HGR
                    ASM_ADDR(`HCOLOR-1')                  ; $92...146...HCOLOR=
                    ASM_ADDR(`HPLOT-1')                   ; $93...147...HPLOT
                    ASM_ADDR(`DRAW-1')                    ; $94...148...DRAW
                    ASM_ADDR(`XDRAW-1')                   ; $95...149...XDRAW
                    ASM_ADDR(`HTAB-1')                    ; $96...150...HTAB
                    ASM_ADDR(`MON_HOME-1')                ; $97...151...HOME
                    ASM_ADDR(`ROT-1')                     ; $98...152...ROT=
                    ASM_ADDR(`SCALE-1')                   ; $99...153...SCALE=
                    ASM_ADDR(`SHLOAD-1')                  ; $9A...154...SHLOAD
                    ASM_ADDR(`TRACE-1')                   ; $9B...155...TRACE
                    ASM_ADDR(`NOTRACE-1')                 ; $9C...156...NOTRACE
                    ASM_ADDR(`NORMAL-1')                  ; $9D...157...NORMAL
                    ASM_ADDR(`INVERSE-1')                 ; $9E...158...INVERSE
                    ASM_ADDR(`FLASH-1')                   ; $9F...159...FLASH
                    ASM_ADDR(`COLOR-1')                   ; $A0...160...COLOR=
                    ASM_ADDR(`POP-1')                     ; $A1...161...POP
                    ASM_ADDR(`VTAB-1')                    ; $A2...162...VTAB
                    ASM_ADDR(`HIMEM-1')                   ; $A3...163...HIMEM:
                    ASM_ADDR(`LOMEM-1')                   ; $A4...164...LOMEM:
                    ASM_ADDR(`ONERR-1')                   ; $A5...165...ONERR
                    ASM_ADDR(`RESUME-1')                  ; $A6...166...RESUME
                    ASM_ADDR(`RECALL-1')                  ; $A7...167...RECALL
                    ASM_ADDR(`STORE-1')                   ; $A8...168...STORE
                    ASM_ADDR(`SPEED-1')                   ; $A9...169...SPEED=
                    ASM_ADDR(`LET-1')                     ; $AA...170...LET
                    ASM_ADDR(`GOTO-1')                    ; $AB...171...GOTO
                    ASM_ADDR(`RUN-1')                     ; $AC...172...RUN
                    ASM_ADDR(`IF-1')                      ; $AD...173...IF
                    ASM_ADDR(`RESTORE-1')                 ; $AE...174...RESTORE
                    ASM_ADDR(`AMPERSAND_VECTOR-1')        ; $AF...175...&
                    ASM_ADDR(`GOSUB-1')                   ; $B0...176...GOSUB
                    ASM_ADDR(`POP-1')                     ; $B1...177...RETURN
                    ASM_ADDR(`REM-1')                     ; $B2...178...REM
                    ASM_ADDR(`STOP-1')                    ; $B3...179...STOP
                    ASM_ADDR(`ONGOTO-1')                  ; $B4...180...ON
                    ASM_ADDR(`WAIT-1')                    ; $B5...181...WAIT
                    ASM_ADDR(`LOAD-1')                    ; $B6...182...LOAD
                    ASM_ADDR(`SAVE-1')                    ; $B7...183...SAVE
                    ASM_ADDR(`DEF-1')                     ; $B8...184...DEF
                    ASM_ADDR(`POKE-1')                    ; $B9...185...POKE
                    ASM_ADDR(`PRINT-1')                   ; $BA...186...PRINT
                    ASM_ADDR(`CONT-1')                    ; $BB...187...CONT
                    ASM_ADDR(`LIST-1')                    ; $BC...188...LIST
                    ASM_ADDR(`CLEAR-1')                   ; $BD...189...CLEAR
                    ASM_ADDR(`GET-1')                     ; $BE...190...GET
                    ASM_ADDR(`NEW-1')                     ; $BF...191...NEW
                                                    ; --------------------------------
UNFNC               ASM_ADDR(`SGN')                       ; $D2...210...SGN
                    ASM_ADDR(`INT')                       ; $D3...211...INT
                    ASM_ADDR(`ABS')                       ; $D4...212...ABS
                    ASM_ADDR(`USR')                       ; $D5...213...USR
                    ASM_ADDR(`FRE')                       ; $D6...214...FRE
                    ASM_ADDR(`ERROR')                     ; $D7...215...SCRN(
                    ASM_ADDR(`PDL')                       ; $D8...216...PDL
                    ASM_ADDR(`POS')                       ; $D9...217...POS
                    ASM_ADDR(`SQR')                       ; $DA...218...SQR
                    ASM_ADDR(`RND')                       ; $DB...219...RND
                    ASM_ADDR(`LOG')                       ; $DC...220...LOG
                    ASM_ADDR(`EXP')                       ; $DD...221...EXP
                    ASM_ADDR(`COS')                       ; $DE...222...COS
                    ASM_ADDR(`SIN')                       ; $DF...223...SIN
                    ASM_ADDR(`TAN')                       ; $E0...224...TAN
                    ASM_ADDR(`ATN')                       ; $E1...225...ATN
                    ASM_ADDR(`PEEK')                      ; $E2...226...PEEK
                    ASM_ADDR(`LEN')                       ; $E3...227...LEN
                    ASM_ADDR(`STR')                       ; $E4...228...STR$
                    ASM_ADDR(`VAL')                       ; $E5...229...VAL
                    ASM_ADDR(`ASC')                       ; $E6...230...ASC
                    ASM_ADDR(`CHRSTR')                    ; $E7...231...CHR$
                    ASM_ADDR(`LEFTSTR')                   ; $E8...232...LEFT$
                    ASM_ADDR(`RIGHTSTR')                  ; $E9...233...RIGHT$
                    ASM_ADDR(`MIDSTR')                    ; $EA...234...MID$
                                                    ; --------------------------------
                                                    ; MATH OPERATOR BRANCH TABLE
                                                    ; 
                                                    ; ONE-BYTE PRECEDENCE CODE
                                                    ; TWO-BYTE ADDRESS
                                                    ; --------------------------------
P_OR                = $46                           ; "OR" IS LOWEST PRECEDENCE
P_AND               = $50                           ; 
P_REL               = $64                           ; RELATIONAL OPERATORS
P_ADD               = $79                           ; BINARY + AND -
P_MUL               = $7B                           ; * AND /
P_PWR               = $7D                           ; EXPONENTIATION
P_NEQ               = $7F                           ; UNARY - AND COMPARISON =
                                                    ; --------------------------------
MATHTBL             ASM_DATA(`P_ADD')
                    ASM_ADDR(`FADDT-1')                   ; $C8...200...+
                    ASM_DATA(`P_ADD')
                    ASM_ADDR(`FSUBT-1')                   ; $C9...201...-
                    ASM_DATA(`P_MUL')
                    ASM_ADDR(`FMULTT-1')                  ; $CA...202...*
                    ASM_DATA(`P_MUL')
                    ASM_ADDR(`FDIVT-1')                   ; $CB...203.../
                    ASM_DATA(`P_PWR')
                    ASM_ADDR(`FPWRT-1')                   ; $CC...204...^
                    ASM_DATA(`P_AND')
                    ASM_ADDR(`ANDOP-1')                   ; $CD...205...AND
                    ASM_DATA(`P_OR')
                    ASM_ADDR(`OR-1')                      ; $CE...206...OR
M_NEG               ASM_DATA(`P_NEQ')
                    ASM_ADDR(`NEGOP-1')                   ; $CF...207...>
MEQUU               ASM_DATA(`P_NEQ')
                    ASM_ADDR(`EQUOP-1')                   ; $D0...208...=
M_REL               ASM_DATA(`P_REL')
                    ASM_ADDR(`RELOPS-1')                  ; $D1...209...<

                                                    ; --------------------------------
                                                    ; TOKEN NAME TABLE
                                                    ; --------------------------------
                                                    ;

TOKEN_NAME_TABLE    HIASCII(`END')                   ; $80...128
                    HIASCII(`FOR')                   ; $81...129
                    HIASCII(`NEXT')                  ; $82...130
                    HIASCII(`DATA')                  ; $83...131
                    HIASCII(`INPUT')                 ; $84...132
                    HIASCII(`DEL')                   ; $85...133
                    HIASCII(`DIM')                   ; $86...134
                    HIASCII(`READ')                  ; $87...135
                    HIASCII(`GR')                    ; $88...136
                    HIASCII(`TEXT')                  ; $89...137
                    HIASCII(`PR#')                   ; $8A...138
                    HIASCII(`IN#')                   ; $8B...139
                    HIASCII(`CALL')                  ; $8C...140
                    HIASCII(`PLOT')                  ; $8D...141
                    HIASCII(`HLIN')                  ; $8E...142
                    HIASCII(`VLIN')                  ; $8F...143
                    HIASCII(`HGR2')                  ; $90...144
                    HIASCII(`HGR')                   ; $91...145
                    HIASCII(`HCOLOR=')               ; $92...146
                    HIASCII(`HPLOT')                 ; $93...147
                    HIASCII(`DRAW')                  ; $94...148
                    HIASCII(`XDRAW')                 ; $95...149
                    HIASCII(`HTAB')                  ; $96...150
                    HIASCII(`HOME')                  ; $97...151
                    HIASCII(`ROT=')                  ; $98...152
                    HIASCII(`SCALE=')                ; $99...153
                    HIASCII(`SHLOAD')                ; $9A...154
                    HIASCII(`TRACE')                 ; $9B...155
                    HIASCII(`NOTRACE')               ; $9C...156
                    HIASCII(`NORMAL')                ; $9D...157
                    HIASCII(`INVERSE')               ; $9E...158
                    HIASCII(`FLASH')                 ; $9F...159
                    HIASCII(`COLOR=')                ; $A0...160
                    HIASCII(`POP')                   ; $A1...161
                    HIASCII(`VTAB')                  ; $A2...162
                    HIASCII(`HIMEM:')                ; $A3...163
                    HIASCII(`LOMEM:')                ; $A4...164
                    HIASCII(`ONERR')                 ; $A5...165
                    HIASCII(`RESUME')                ; $A6...166
                    HIASCII(`RECALL')                ; $A7...167
                    HIASCII(`STORE')                 ; $A8...168
                    HIASCII(`SPEED=')                ; $A9...169
                    HIASCII(`LET')                   ; $AA...170
                    HIASCII(`GOTO')                  ; $AB...171
                    HIASCII(`RUN')                   ; $AC...172
                    HIASCII(`IF')                    ; $AD...173
                    HIASCII(`RESTORE')               ; $AE...174
                    HIASCII(`&')                     ; $AF...175
                    HIASCII(`GOSUB')                 ; $B0...176
                    HIASCII(`RETURN')                ; $B1...177
                    HIASCII(`REM')                   ; $B2...178
                    HIASCII(`STOP')                  ; $B3...179
                    HIASCII(`ON')                    ; $B4...180
                    HIASCII(`WAIT')                  ; $B5...181
                    HIASCII(`LOAD')                  ; $B6...182
                    HIASCII(`SAVE')                  ; $B7...183
                    HIASCII(`DEF')                   ; $B8...184
                    HIASCII(`POKE')                  ; $B9...185
                    HIASCII(`PRINT')                 ; $BA...186
                    HIASCII(`CONT')                  ; $BB...187
                    HIASCII(`LIST')                  ; $BC...188
                    HIASCII(`CLEAR')                 ; $BD...189
                    HIASCII(`GET')                   ; $BE...190
                    HIASCII(`NEW')                   ; $BF...191
                    HIASCII(`TAB(')                  ; $C0...192
                    HIASCII(`TO')                    ; $C1...193
                    HIASCII(`FN')                    ; $C2...194
                    HIASCII(`SPC(')                  ; $C3...195
                    HIASCII(`THEN')                  ; $C4...196
                    HIASCII(`AT')                    ; $C5...197
                    HIASCII(`NOT')                   ; $C6...198
                    HIASCII(`STEP')                  ; $C7...199
                    HIASCII(`+')                     ; $C8...200
                    HIASCII(`-')                     ; $C9...201
                    HIASCII(`*')                     ; $CA...202
                    HIASCII(`/')                     ; $CB...203
                    HIASCII(`^')                     ; $CC...204
                    HIASCII(`AND')                   ; $CD...205
                    HIASCII(`OR')                    ; $CE...206
                    HIASCII(`>')                     ; $CF...207
                    HIASCII(`=')                     ; $D0...208
                    HIASCII(`<')                     ; $D1...209
                    HIASCII(`SGN')                   ; $D2...210
                    HIASCII(`INT')                   ; $D3...211
                    HIASCII(`ABS')                   ; $D4...212
                    HIASCII(`USR')                   ; $D5...213
                    HIASCII(`FRE')                   ; $D6...214
                    HIASCII(`SCRN(')                 ; $D7...215
                    HIASCII(`PDL')                   ; $D8...216
                    HIASCII(`POS')                   ; $D9...217
                    HIASCII(`SQR')                   ; $DA...218
                    HIASCII(`RND')                   ; $DB...219
                    HIASCII(`LOG')                   ; $DC...220
                    HIASCII(`EXP')                   ; $DD...221
                    HIASCII(`COS')                   ; $DE...222
                    HIASCII(`SIN')                   ; $DF...223
                    HIASCII(`TAN')                   ; $E0...224
                    HIASCII(`ATN')                   ; $E1...225
                    HIASCII(`PEEK')                  ; $E2...226
                    HIASCII(`LEN')                   ; $E3...227
                    HIASCII(`STR$')                  ; $E4...228
                    HIASCII(`VAL')                   ; $E5...229
                    HIASCII(`ASC')                   ; $E6...230
                    HIASCII(`CHR$')                  ; $E7...231
                    HIASCII(`LEFT$')                 ; $E8...232
                    HIASCII(`RIGHT$')                ; $E9...233
                    HIASCII(`MID$')                  ; $EA...234

                    ASM_DATA(0)                         ; END OF TOKEN NAME TABLE
                                                    ; --------------------------------
                                                    ; --------------------------------
                                                    ; ERROR MESSAGES
                                                    ; --------------------------------
ERROR_MESSAGES
ERR_NOFOR           = *-ERROR_MESSAGES
                    HIASCII(`NEXT WITHOUT FOR')
ERR_SYNTAX          = *-ERROR_MESSAGES
                    HIASCII(`SYNTAX')
ERR_NOGOSUB         = *-ERROR_MESSAGES
                    HIASCII(`RETURN WITHOUT GOSUB')
ERR_NODATA          = *-ERROR_MESSAGES
                    HIASCII(`OUT OF DATA')
ERR_ILLQTY          = *-ERROR_MESSAGES
                    HIASCII(`ILLEGAL QUANTITY')
ERR_OVERFLOW        = *-ERROR_MESSAGES
                    HIASCII(`OVERFLOW')
ERR_MEMFULL         = *-ERROR_MESSAGES
                    HIASCII(`OUT OF MEMORY')
ERR_UNDEFSTAT       = *-ERROR_MESSAGES
                    HIASCII(`UNDEF'D STATEMENT')
ERR_BADSUBS         = *-ERROR_MESSAGES
                    HIASCII(`BAD SUBSCRIPT')
ERR_REDIMD          = *-ERROR_MESSAGES
                    HIASCII(`REDIM'D ARRAY')
ERR_ZERODIV         = *-ERROR_MESSAGES
                    HIASCII(`DIVISION BY ZERO')
ERR_ILLDIR          = *-ERROR_MESSAGES
                    HIASCII(`ILLEGAL DIRECT')
ERR_BADTYPE         = *-ERROR_MESSAGES
                    HIASCII(`TYPE MISMATCH')
ERR_STRLONG         = *-ERROR_MESSAGES
                    HIASCII(`STRING TOO LONG')
ERR_FRMCPX          = *-ERROR_MESSAGES
                    HIASCII(`FORMULA TOO COMPLEX')
ERR_CANTCONT        = *-ERROR_MESSAGES
                    HIASCII(`CAN'T CONTINUE')
ERR_UNDEFFUNC       = *-ERROR_MESSAGES
                    HIASCII(`UNDEF'D FUNCTION')
                                                    ; --------------------------------

QT_ERROR            HIASCII(` ERROR')
                    ASM_DATA($07,0)

QT_IN               HIASCII(` IN ')
                    ASM_DATA(0)

QT_BREAK            ASM_DATA($0D)
                    HIASCII(`BREAK')
                    ASM_DATA($07,0)
                                                    ; --------------------------------
                                                    ; CALLED BY "NEXT" AND "FOR" TO SCAN THROUGH
                                                    ; THE STACK FOR A FRAME WITH THE SAME VARIABLE.
                                                    ; 
                                                    ; (FORPNT) = ADDRESS OF VARIABLE IF "FOR" OR "NEXT"
                                                    ; = $XXFF IF CALLED FROM "RETURN"
                                                    ; <<< BUG: SHOULD BE $FFXX >>>
                                                    ; 
                                                    ; RETURNS .NE. IF VARIABLE NOT FOUND,
                                                    ; (X) = STACK PNTR AFTER SKIPPING ALL FRAMES
                                                    ; 
                                                    ; EQU. IF FOUND
                                                    ; (X) = STACK PNTR OF FRAME FOUND
                                                    ; --------------------------------
GTFORPNT
                    TSX
                    INX
                    INX
                    INX
                    INX
?1                  LDA STACK+1,X                   ; "FOR" FRAME HERE?
                    CMP #TOKEN_FOR                  ; 
                    BNE ?4                          ; NO
                    LDA FORPNT+1                    ; YES -- "NEXT" WITH NO VARIABLE?
                    BNE ?2                          ; NO, VARIABLE SPECIFIED
                    LDA STACK+2,X                   ; YES, SO USE THIS FRAME
                    STA FORPNT                      ; 
                    LDA STACK+3,X                   ; 
                    STA FORPNT+1                    ; 
?2                  CMP STACK+3,X                   ; IS VARIABLE IN THIS FRAME?
                    BNE ?3                          ; NO
                    LDA FORPNT                      ; LOOK AT 2ND BYTE TOO
                    CMP STACK+2,X                   ; SAME VARIABLE?
                    BEQ ?4                          ; YES
?3                  TXA                             ; NO, SO TRY NEXT FRAME (IF ANY)
                    CLC                             ; 18 BYTES PER FRAME
                    ADC #18                         ; 
                    TAX
                    BNE ?1                          ; ...ALWAYS?
?4                  RTS
                                                    ; --------------------------------
                                                    ; MOVE BLOCK OF MEMORY UP
                                                    ; 
                                                    ; ON ENTRY:
                                                    ; (Y,A) = (HIGHDS) = DESTINATION END+1
                                                    ; (LOWTR) = LOWEST ADDRESS OF SOURCE
                                                    ; (HIGHTR) = HIGHEST SOURCE ADDRESS+1
                                                    ; --------------------------------
BLTU                JSR REASON                      ; BE SURE (Y,A) < FRETOP
                    STA STREND                      ; NEW TOP OF ARRAY STORAGE
                    STY STREND+1                    ; 
BLTU2               SEC                             ; 
                    LDA HIGHTR                      ; COMPUTE # OF BYTES TO BE MOVED
                    SBC LOWTR                       ; (FROM LOWTR THRU HIGHTR-1)
                    STA INDEX                       ; PARTIAL PAGE AMOUNT
                    TAY                             ; 
                    LDA HIGHTR+1                    ; 
                    SBC LOWTR+1                     ; 
                    TAX                             ; # OF WHOLE PAGES IN X-REG
                    INX                             ; 
                    TYA                             ; # BYTES IN PARTIAL PAGE
                    BEQ ?4                          ; NO PARTIAL PAGE
                    LDA HIGHTR                      ; BACK UP HIGHTR # BYTES IN PARTIAL PAGE
                    SEC                             ; 
                    SBC INDEX                       ; 
                    STA HIGHTR                      ; 
                    BCS ?1                          ; 
                    DEC HIGHTR+1                    ; 
                    SEC                             ; 
?1                  LDA HIGHDS                      ; BACK UP HIGHDS # BYTES IN PARTIAL PAGE
                    SBC INDEX                       ; 
                    STA HIGHDS                      ; 
                    BCS ?3                          ; 
                    DEC HIGHDS+1                    ; 
                    BCC ?3                          ; ...ALWAYS
?2                  LDA (HIGHTR),Y                  ; MOVE THE BYTES
                    STA (HIGHDS),Y
?3                  DEY
                    BNE ?2                          ; LOOP TO END OF THIS 256 BYTES
                    LDA (HIGHTR),Y                  ; MOVE ONE MORE BYTE
                    STA (HIGHDS),Y
?4                  DEC HIGHTR+1                    ; DOWN TO NEXT BLOCK OF 256
                    DEC HIGHDS+1
                    DEX                             ; ANOTHER BLOCK OF 256 TO MOVE?
                    BNE ?3                          ; YES
                    RTS                             ; NO, FINISHED
                                                    ; --------------------------------
                                                    ; CHECK IF ENOUGH ROOM LEFT ON STACK
                                                    ; FOR "FOR", "GOSUB", OR EXPRESSION EVALUATION
                                                    ; --------------------------------
CHKMEM              ASL
                    ADC #54
                    BCS MEMERR                      ; ...MEM FULL ERR
                    STA INDEX
                    TSX
                    CPX INDEX
                    BCC MEMERR                      ; ...MEM FULL ERR
                    RTS
                                                    ; --------------------------------
                                                    ; CHECK IF ENOUGH ROOM BETWEEN ARRAYS AND STRINGS
                                                    ; (Y,A) = ADDR ARRAYS NEED TO GROW TO
                                                    ; --------------------------------
REASON              CPY FRETOP+1                    ; HIGH BYTE
                    BCC ?4                          ; PLENTY OF ROOM
                    BNE ?1                          ; NOT ENOUGH, TRY GARBAGE COLLECTION
                    CMP FRETOP                      ; LOW BYTE
                    BCC ?4                          ; ENOUGH ROOM
                                                    ; --------------------------------
?1                  PHA                             ; SAVE (Y,A), TEMP1, AND TEMP2
                    LDX #FAC-TEMP1-1
                    TYA
?2                  PHA
                    LDA TEMP1,X
                    DEX
                    BPL ?2
                    JSR GARBAG                      ; MAKE AS MUCH ROOM AS POSSIBLE
                    LDX #TEMP1-FAC+1                ; RESTORE TEMP1 AND TEMP2
?3                  PLA                             ; AND (Y,A)
                    STA FAC,X
                    INX
                    BMI ?3
                    PLA
                    TAY
                    PLA                             ; DID WE FIND ENOUGH ROOM?
                    CPY FRETOP+1                    ; HIGH BYTE
                    BCC ?4                          ; YES, AT LEAST A PAGE
                    BNE MEMERR                      ; NO, MEM FULL ERR
                    CMP FRETOP                      ; LOW BYTE
                    BCS MEMERR                      ; NO, MEM FULL ERR
?4                  RTS                             ; YES, RETURN
                                                    ; --------------------------------
MEMERR              LDX #ERR_MEMFULL
                                                    ; --------------------------------
                                                    ; HANDLE AN ERROR
                                                    ; 
                                                    ; (X)=OFFSET IN ERROR MESSAGE TABLE
                                                    ; (ERRFLG) > 128 IF "ON ERR" TURNED ON
                                                    ; (CURLIN+1) = $FF IF IN DIRECT MODE
                                                    ; --------------------------------
ERROR               BIT ERRFLG                      ; "ON ERR" TURNED ON?
                    BPL ?1                          ; NO
                    JMP HANDLERR                    ; YES
?1                  JSR CRDO                        ; PRINT <RETURN>
                    JSR OUTQUES                     ; PRINT "?"
?2                  LDA ERROR_MESSAGES,X
                    PHA                             ; PRINT MESSAGE
                    JSR OUTDO
                    INX
                    PLA
                    BPL ?2
                    JSR STKINI                      ; FIX STACK, ET AL
                    LDA #<QT_ERROR                  ; PRINT " ERROR" AND BELL
                    LDY #>QT_ERROR
                                                    ; --------------------------------
                                                    ; PRINT STRING AT (Y,A)
                                                    ; PRINT CURRENT LINE # UNLESS IN DIRECT MODE
                                                    ; FALL INTO WARM RESTART
                                                    ; --------------------------------
PRINT_ERROR_LINNUM
                    JSR STROUT                      ; PRINT STRING AT (Y,A)
                    LDY CURLIN+1                    ; RUNNING, OR DIRECT?
                    INY
                    BEQ RESTART                     ; WAS $FF, SO DIRECT MODE
                    JSR INPRT                       ; RUNNING, SO PRINT LINE NUMBER
                                                    ; --------------------------------
                                                    ; WARM RESTART ENTRY
                                                    ; 
                                                    ; COME HERE FROM MONITOR BY CTL-C, 0G, 3D0G, OR E003G
                                                    ; --------------------------------
RESTART
                    JSR CRDO                        ; PRINT <RETURN>
                    LDX #HICHAR(`]')|%10000000              ; PROMPT CHARACTER
                    JSR INLIN2                      ; READ A LINE
                    STX TXTPTR                      ; SET UP CHRGET TO SCAN THE LINE
                    STY TXTPTR+1                    ; 
                    LSR ERRFLG                      ; CLEAR FLAG
                    JSR CHRGET                      ; 
                    TAX                             ; 
                    BEQ RESTART                     ; EMPTY LINE
                    LDX #$FF                        ; $FF IN HI-BYTE OF CURLIN MEANS
                    STX CURLIN+1                    ; WE ARE IN DIRECT MODE
                    BCC NUMBERED_LINE               ; CHRGET SAW DIGIT, NUMBERED LINE
                    JSR PARSE_INPUT_LINE            ; NO NUMBER, SO PARSE IT
                    JMP TRACE_                      ; AND TRY EXECUTING IT
                                                    ; --------------------------------
                                                    ; HANDLE NUMBERED LINE
                                                    ; --------------------------------
NUMBERED_LINE
                    LDX PRGEND                      ; SQUASH VARIABLE TABLE
                    STX VARTAB
                    LDX PRGEND+1
                    STX VARTAB+1
                    JSR LINGET                      ; GET LINE #
                    JSR PARSE_INPUT_LINE            ; AND PARSE THE INPUT LINE
                    STY EOL_PNTR                    ; SAVE INDEX TO INPUT BUFFER
                    JSR FNDLIN                      ; IS THIS LINE # ALREADY IN PROGRAM?
                    BCC PUT_NEW_LINE                ; NO
                    LDY #1                          ; YES, SO DELETE IT
                    LDA (LOWTR),Y                   ; LOWTR POINTS AT LINE
                    STA INDEX+1                     ; GET HIGH BYTE OF FORWARD PNTR
                    LDA VARTAB
                    STA INDEX
                    LDA LOWTR+1
                    STA DEST+1
                    LDA LOWTR
                    DEY
                    SBC (LOWTR),Y
                    CLC
                    ADC VARTAB
                    STA VARTAB
                    STA DEST
                    LDA VARTAB+1
                    ADC #$FF
                    STA VARTAB+1
                    SBC LOWTR+1
                    TAX
                    SEC
                    LDA LOWTR
                    SBC VARTAB
                    TAY
                    BCS ?1
                    INX
                    DEC DEST+1
?1                  CLC
                    ADC INDEX
                    BCC ?2
                    DEC INDEX+1
                    CLC
                                                    ; --------------------------------
?2                  LDA (INDEX),Y                   ; MOVE HIGHER LINES OF PROGRAM
                    STA (DEST),Y                    ; DOWN OVER THE DELETED LINE.
                    INY
                    BNE ?2
                    INC INDEX+1
                    INC DEST+1
                    DEX
                    BNE ?2
                                                    ; --------------------------------
PUT_NEW_LINE
                    LDA INPUT_BUFFER                ; ANY CHARACTERS AFTER LINE #?
                    BEQ FIX_LINKS                   ; NO, SO NOTHING TO INSERT.
                    LDA MEMSIZ                      ; YES, SO MAKE ROOM AND INSERT LINE
                    LDY MEMSIZ+1                    ; WIPE STRING AREA CLEAN
                    STA FRETOP                      ; 
                    STY FRETOP+1                    ; 
                    LDA VARTAB                      ; SET UP BLTU SUBROUTINE
                    STA HIGHTR                      ; INSERT NEW LINE.
                    ADC EOL_PNTR
                    STA HIGHDS
                    LDY VARTAB+1
                    STY HIGHTR+1
                    BCC ?1
                    INY
?1                  STY HIGHDS+1
                    JSR BLTU                        ; MAKE ROOM FOR THE LINE
                    LDA LINNUM                      ; PUT LINE NUMBER IN LINE IMAGE
                    LDY LINNUM+1
                    STA INPUT_BUFFER-2
                    STY INPUT_BUFFER-1
                    LDA STREND
                    LDY STREND+1
                    STA VARTAB
                    STY VARTAB+1
                    LDY EOL_PNTR
                                                    ; ---COPY LINE INTO PROGRAM-------
?2                  LDA INPUT_BUFFER-5,Y
                    DEY
                    STA (LOWTR),Y
                    BNE ?2
                                                    ; --------------------------------
                                                    ; CLEAR ALL VARIABLES
                                                    ; RE-ESTABLISH ALL FORWARD LINKS
                                                    ; --------------------------------
FIX_LINKS
                    JSR SETPTRS                     ; CLEAR ALL VARIABLES
                    LDA TXTTAB                      ; POINT INDEX AT START OF PROGRAM
                    LDY TXTTAB+1
                    STA INDEX
                    STY INDEX+1
                    CLC
?1                  LDY #1                          ; HI-BYTE OF NEXT FORWARD PNTR
                    LDA (INDEX),Y                   ; END OF PROGRAM YET?
                    BNE ?2                          ; NO, KEEP GOING
                    LDA VARTAB                      ; YES
                    STA PRGEND
                    LDA VARTAB+1
                    STA PRGEND+1
                    JMP RESTART
?2                  LDY #4                          ; FIND END OF THIS LINE
?3                  INY                             ; (NOTE MAXIMUM LENGTH < 256)
                    LDA (INDEX),Y                   ; 
                    BNE ?3                          ; 
                    INY                             ; COMPUTE ADDRESS OF NEXT LINE
                    TYA                             ; 
                    ADC INDEX                       ; 
                    TAX                             ; 
                    LDY #0                          ; STORE FORWARD PNTR IN THIS LINE
                    STA (INDEX),Y                   ; 
                    LDA INDEX+1                     ; 
                    ADC #0                          ; (NOTE: THIS CLEARS CARRY)
                    INY                             ; 
                    STA (INDEX),Y                   ; 
                    STX INDEX                       ; 
                    STA INDEX+1                     ; 
                    BCC ?1                          ; ...ALWAYS
                                                    ; --------------------------------
                                                    ; --------------------------------
                                                    ; READ A LINE, AND STRIP OFF SIGN BITS
                                                    ; --------------------------------
INLIN               LDX #$80                        ; NULL PROMPT
INLIN2              STX MON_PROMPT
                    JSR MON_GETLN
                    CPX #239                        ; MAXIMUM LINE LENGTH
                    BCC ?1
                    LDX #239                        ; TRUNCATE AT 239 CHARS
?1                  LDA #0                          ; MARK END OF LINE WITH $00 BYTE
                    STA INPUT_BUFFER,X
                    TXA
                    BEQ ?3                          ; NULL INPUT LINE
?2                  LDA INPUT_BUFFER-1,X            ; DROP SIGN BITS
                    AND #$7F
                    STA INPUT_BUFFER-1,X
                    DEX
                    BNE ?2
?3                  LDA #0                          ; (Y,X) POINTS AT BUFFER-1
                    LDX #<(INPUT_BUFFER-1)
                    LDY #>(INPUT_BUFFER-1)
                    RTS
                                                    ; --------------------------------
INCHR               JSR MON_RDKEY                   ; *** OUGHT TO BE "BIT $C010" ***
                    AND #$7F
                    RTS
                                                    ; --------------------------------
                                                    ; TOKENIZE THE INPUT LINE
                                                    ; --------------------------------
PARSE_INPUT_LINE
                    LDX TXTPTR                      ; INDEX INTO UNPARSED LINE
                    DEX                             ; PREPARE FOR INX AT "PARSE"
                    LDY #4                          ; INDEX TO PARSED OUTPUT LINE
                    STY DATAFLG                     ; CLEAR SIGN-BIT OF DATAFLG
                    BIT LOCK                        ; IS THIS PROGRAM LOCKED?
                    BPL PARSE                       ; NO, GO AHEAD AND PARSE THE LINE
                    PLA                             ; YES, IGNORE INPUT AND "RUN"
                    PLA                             ; THE PROGRAM
                    JSR SETPTRS                     ; CLEAR ALL VARIABLES
                    JMP NEWSTT                      ; START RUNNING
                                                    ; --------------------------------
PARSE               INX                             ; NEXT INPUT CHARACTER
?1                  LDA INPUT_BUFFER,X
                    BIT DATAFLG                     ; IN A "DATA" STATEMENT?
                    BVS ?2                          ; YES (DATAFLG = $49)
                    CMP #HICHAR(` ')                        ; IGNORE BLANKS
                    BEQ PARSE                       ; 
?2                  STA ENDCHR                      ; 
                    CMP #HICHAR(`"')                        ; START OF QUOTATION?
                    BEQ ?13                         ; 
                    BVS ?9                          ; BRANCH IF IN "DATA" STATEMENT
                    CMP #HICHAR(`?')                        ; SHORTHAND FOR "PRINT"?
                    BNE ?3                          ; NO
                    LDA #TOKEN_PRINT                ; YES, REPLACE WITH "PRINT" TOKEN
                    BNE ?9                          ; ...ALWAYS
?3                  CMP #HICHAR(`0')                        ; IS IT A DIGIT, COLON, OR SEMI-COLON?
                    BCC ?4                          ; NO, PUNCTUATION !"#$%&'()*+,-./
                    CMP #HICHAR(`;')+1
                    BCC ?9                          ; YES, NOT A TOKEN
                                                    ; --------------------------------
                                                    ; SEARCH TOKEN NAME TABLE FOR MATCH STARTING
                                                    ; WITH CURRENT CHAR FROM INPUT LINE
                                                    ; --------------------------------
?4                  STY STRNG2                      ; SAVE INDEX TO OUTPUT LINE
                    LDA #<(TOKEN_NAME_TABLE-$100)
                    STA FAC                         ; MAKE PNTR FOR SEARCH
                    LDA #>(TOKEN_NAME_TABLE-$100)
                    STA FAC+1
                    LDY #0                          ; USE Y-REG WITH (FAC) TO ADDRESS TABLE
                    STY TKN_CNTR                    ; HOLDS CURRENT TOKEN-$80
                    DEY                             ; PREPARE FOR "INY" A FEW LINES DOWN
                    STX TXTPTR                      ; SAVE POSITION IN INPUT LINE
                    DEX                             ; PREPARE FOR "INX" A FEW LINES DOWN
?5                  INY                             ; ADVANCE POINTER TO TOKEN TABLE
                    BNE ?6                          ; Y=Y+1 IS ENOUGH
                    INC FAC+1                       ; ALSO NEED TO BUMP THE PAGE
?6                  INX                             ; ADVANCE POINTER TO INPUT LINE
?7                  LDA INPUT_BUFFER,X              ; NEXT CHAR FROM INPUT LINE
                    CMP #HICHAR(` ')                        ; THIS CHAR A BLANK?
                    BEQ ?6                          ; YES, IGNORE ALL BLANKS
                    SEC                             ; NO, COMPARE TO CHAR IN TABLE
                    SBC (FAC),Y                     ; SAME AS NEXT CHAR OF TOKEN NAME?
                    BEQ ?5                          ; YES, CONTINUE MATCHING
                    CMP #$80                        ; MAYBE; WAS IT SAME EXCEPT FOR BIT 7?
                    BNE ?14                         ; NO, SKIP TO NEXT TOKEN
                    ORA TKN_CNTR                    ; YES, END OF TOKEN; GET TOKEN #
                    CMP #TOKENDB                    ; DID WE MATCH "AT"?
                    BNE ?8                          ; NO, SO NO AMBIGUITY
                    LDA INPUT_BUFFER+1,X            ; "AT" COULD BE "ATN" OR "A TO"
                    CMP #HICHAR(`N')                        ; "ATN" HAS PRECEDENCE OVER "AT"
                    BEQ ?14                         ; IT IS "ATN", FIND IT THE HARD WAY
                    CMP #HICHAR(`O')                        ; "TO" HAS PRECEDENCE OVER "AT"
                    BEQ ?14                         ; IT IS "A TO", FIN IT THE HARD WAY
                    LDA #TOKENDB                    ; NOT "ATN" OR "A TO", SO USE "AT"
                                                    ; --------------------------------
                                                    ; STORE CHARACTER OR TOKEN IN OUTPUT LINE
                                                    ; --------------------------------
?8                  LDY STRNG2                      ; GET INDEX TO OUTPUT LINE IN Y-REG
?9                  INX                             ; ADVANCE INPUT INDEX
                    INY                             ; ADVANCE OUTPUT INDEX
                    STA INPUT_BUFFER-5,Y            ; STORE CHAR OR TOKEN
                    LDA INPUT_BUFFER-5,Y            ; TEST FOR EOL OR EOS
                    BEQ ?17                         ; END OF LINE
                    SEC                             ; 
                    SBC #HICHAR(`:')                        ; END OF STATEMENT?
                    BEQ ?10                         ; YES, CLEAR DATAFLG
                    CMP #TOKENDWTA-':'              ; "DATA" TOKEN?
                    BNE ?11                         ; NO, LEAVE DATAFLG ALONE
?10                 STA DATAFLG                     ; DATAFLG = 0 OR $83-$3A = $49
?11                 SEC                             ; IS IT A "REM" TOKEN?
                    SBC #TOKEN_REM-':'
                    BNE ?1                          ; NO, CONTINUE PARSING LINE
                    STA ENDCHR                      ; YES, CLEAR LITERAL FLAG
                                                    ; --------------------------------
                                                    ; HANDLE LITERAL (BETWEEN QUOTES) OR REMARK,
                                                    ; BY COPYING CHARS UP TO ENDCHR.
                                                    ; --------------------------------
?12                 LDA INPUT_BUFFER,X
                    BEQ ?9                          ; END OF LINE
                    CMP ENDCHR
                    BEQ ?9                          ; FOUND ENDCHR
?13                 INY                             ; NEXT OUTPUT CHAR
                    STA INPUT_BUFFER-5,Y
                    INX                             ; NEXT INPUT CHAR
                    BNE ?12                         ; ...ALWAYS
                                                    ; --------------------------------
                                                    ; ADVANCE POINTER TO NEXT TOKEN NAME
                                                    ; --------------------------------
?14                 LDX TXTPTR                      ; GET POINTER TO INPUT LINE IN X-REG
                    INC TKN_CNTR                    ; BUMP (TOKEN # - $80)
?15                 LDA (FAC),Y                     ; SCAN THROUGH TABLE FOR BIT7 = 1
                    INY                             ; NEXT TOKEN ONE BEYOND THAT
                    BNE ?16                         ; ...USUALLY ENOUGH TO BUMP Y-REG
                    INC FAC+1                       ; NEXT SET OF 256 TOKEN CHARS
?16                 ASL                             ; SEE IF SIGN BIT SET ON CHAR
                    BCC ?15                         ; NO, MORE IN THIS NAME
                    LDA (FAC),Y                     ; YES, AT NEXT NAME.  END OF TABLE?
                    BNE ?7                          ; NO, NOT END OF TABLE
                    LDA INPUT_BUFFER,X              ; YES, SO NOT A KEYWORD
                    BPL ?8                          ; ...ALWAYS, COPY CHAR AS IS
                                                    ; ---END OF LINE------------------
?17                 STA INPUT_BUFFER-3,Y            ; STORE ANOTHER 00 ON END
                    DEC TXTPTR+1                    ; SET TXTPTR = INPUT.BUFFER-1
                    LDA #<(INPUT_BUFFER-1)
                    STA TXTPTR
                    RTS
                                                    ; --------------------------------
                                                    ; SEARCH FOR LINE
                                                    ; 
                                                    ; (LINNUM) = LINE # TO FIND
                                                    ; IF NOT FOUND:  CARRY = 0
                                                    ; LOWTR POINTS AT NEXT LINE
                                                    ; IF FOUND:      CARRY = 1
                                                    ; LOWTR POINTS AT LINE
                                                    ; --------------------------------
FNDLIN              LDA TXTTAB                      ; SEARCH FROM BEGINNING OF PROGRAM
                    LDX TXTTAB+1                    ; 
FL1                 LDY #1                          ; SEARCH FROM (X,A)
                    STA LOWTR                       ; 
                    STX LOWTR+1                     ; 
                    LDA (LOWTR),Y                   ; 
                    BEQ ?3                          ; END OF PROGRAM, AND NOT FOUND
                    INY                             ; 
                    INY                             ; 
                    LDA LINNUM+1                    ; 
                    CMP (LOWTR),Y                   ; 
                    BCC RTS_1                       ; IF NOT FOUND
                    BEQ ?1                          ; 
                    DEY                             ; 
                    BNE ?2                          ; 
?1                  LDA LINNUM                      ; 
                    DEY                             ; 
                    CMP (LOWTR),Y                   ; 
                    BCC RTS_1                       ; PAST LINE, NOT FOUND
                    BEQ RTS_1                       ; IF FOUND
?2                  DEY                             ; 
                    LDA (LOWTR),Y                   ; 
                    TAX                             ; 
                    DEY                             ; 
                    LDA (LOWTR),Y                   ; 
                    BCS FL1                         ; ALWAYS
?3                  CLC                             ; RETURN CARRY = 0
RTS_1               RTS
                                                    ; --------------------------------
                                                    ; "NEW" STATEMENT
                                                    ; --------------------------------
NEW                 BNE RTS_1                       ; IGNORE IF MORE TO THE STATEMENT
SCRTCH              LDA #0
                    STA LOCK
                    TAY
                    STA (TXTTAB),Y
                    INY
                    STA (TXTTAB),Y
                    LDA TXTTAB
                    ADC #2                          ; (CARRY WASN'T CLEARED, SO "NEW" USUALLY
                    STA VARTAB                      ; ADDS 3, WHEREAS "FP" ADDS 2.)
                    STA PRGEND
                    LDA TXTTAB+1
                    ADC #0
                    STA VARTAB+1
                    STA PRGEND+1
                                                    ; --------------------------------
SETPTRS
                    JSR STXTPT                      ; SET TXTPTR TO TXTTAB - 1
                    LDA #0                          ; (THIS COULD HAVE BEEN ".HS 2C")
                                                    ; --------------------------------
                                                    ; "CLEAR" STATEMENT
                                                    ; --------------------------------
CLEAR               BNE RTS_2                       ; IGNORE IF NOT AT END OF STATEMENT
CLEARC              LDA MEMSIZ                      ; CLEAR STRING AREA
                    LDY MEMSIZ+1                    ; 
                    STA FRETOP                      ; 
                    STY FRETOP+1                    ; 
                    LDA VARTAB                      ; CLEAR ARRAY AREA
                    LDY VARTAB+1                    ; 
                    STA ARYTAB                      ; 
                    STY ARYTAB+1                    ; 
                    STA STREND                      ; LOW END OF FREE SPACE
                    STY STREND+1                    ; 
                    JSR RESTORE                     ; SET "DATA" POINTER TO BEGINNING
                                                    ; --------------------------------
STKINI              LDX #TEMPST
                    STX TEMPPT
                    PLA                             ; SAVE RETURN ADDRESS
                    TAY                             ; 
                    PLA                             ; 
                    LDX #$F8                        ; START STACK AT $F8,
                    TXS                             ; LEAVING ROOM FOR PARSING LINES
                    PHA                             ; RESTORE RETURN ADDRESS
                    TYA
                    PHA
                    LDA #0
                    STA OLDTEXT+1
                    STA SUBFLG
RTS_2               RTS
                                                    ; --------------------------------
                                                    ; SET TXTPTR TO BEGINNING OF PROGRAM
                                                    ; --------------------------------
STXTPT              CLC                             ; TXTPTR = TXTTAB - 1
                    LDA TXTTAB
                    ADC #$FF
                    STA TXTPTR
                    LDA TXTTAB+1
                    ADC #$FF
                    STA TXTPTR+1
                    RTS
                                                    ; --------------------------------
                                                    ; "LIST" STATEMENT
                                                    ; --------------------------------
LIST                BCC ?1                          ; NO  LINE # SPECIFIED
                    BEQ ?1                          ; ---DITTO---
                    CMP #TOKEN_MINUS                ; IF DASH OR COMMA, START AT LINE 0
                    BEQ ?1                          ; IS IS A DASH
                    CMP #HICHAR(`,')                        ; COMMA?
                    BNE RTS_2                       ; NO, ERROR
?1                  JSR LINGET                      ; CONVERT LINE NUMBER IF ANY
                    JSR FNDLIN                      ; POINT LOWTR TO 1ST LINE
                    JSR CHRGOT                      ; RANGE SPECIFIED?
                    BEQ ?3                          ; NO
                    CMP #TOKEN_MINUS
                    BEQ ?2
                    CMP #HICHAR(`,')
                    BNE RTS_1
?2                  JSR CHRGET                      ; GET NEXT CHAR
                    JSR LINGET                      ; CONVERT SECOND LINE #
                    BNE RTS_2                       ; BRANCH IF SYNTAX ERR
?3                  PLA                             ; POP RETURN ADRESS
                    PLA                             ; (GET BACK BY "JMP NEWSTT")
                    LDA LINNUM                      ; IF NO SECOND NUMBER, USE $FFFF
                    ORA LINNUM+1                    ; 
                    BNE LIST_0                      ; THERE WAS A SECOND NUMBER
                    LDA #$FF                        ; MAX END RANGE
                    STA LINNUM                      ; 
                    STA LINNUM+1                    ; 
LIST_0              LDY #1                          ; 
                    LDA (LOWTR),Y                   ; HIGH BYTE OF LINK
                    BEQ LIST_3                      ; END OF PROGRAM
                    JSR ISCNTC                      ; CHECK IF CONTROL-C HAS BEEN TYPED
                    JSR CRDO                        ; NO, PRINT <RETURN>
                    INY                             ; 
                    LDA (LOWTR),Y                   ; GET LINE #, COMPARE WITH END RANGE
                    TAX                             ; 
                    INY                             ; 
                    LDA (LOWTR),Y                   ; 
                    CMP LINNUM+1                    ; 
                    BNE ?5                          ; 
                    CPX LINNUM                      ; 
                    BEQ ?6                          ; ON LAST LINE OF RANGE
?5                  BCS LIST_3                      ; FINISHED THE RANGE
                                                    ; ---LIST ONE LINE----------------
?6                  STY FORPNT                      ; 
                    JSR LINPRT                      ; PRINT LINE # FROM X,A
                    LDA #HICHAR(` ')                        ; PRINT SPACE AFTER LINE #
LIST_1              LDY FORPNT                      ; 
                    AND #$7F                        ; 
LIST_2              JSR OUTDO                       ; 
                    LDA MON_CH                      ; IF PAST COLUMN 33, START A NEW LINE
                    CMP #33                         ; 
                    BCC ?1                          ; < 33
                    JSR CRDO                        ; PRINT <RETURN>
                    LDA #5                          ; AND TAB OVER 5
                    STA MON_CH                      ; 
?1                  INY                             ; 
                    LDA (LOWTR),Y                   ; 
                    BNE LIST_4                      ; NOT END OF LINE YET
                    TAY                             ; END OF LINE
                    LDA (LOWTR),Y                   ; GET LINK TO NEXT LINE
                    TAX                             ; 
                    INY                             ; 
                    LDA (LOWTR),Y                   ; 
                    STX LOWTR                       ; POINT TO NEXT LINE
                    STA LOWTR+1                     ; 
                    BNE LIST_0                      ; BRANCH IF NOT END OF PROGRAM
LIST_3              LDA #$0D                        ; PRINT <RETURN>
                    JSR OUTDO                       ; 
                    JMP NEWSTT                      ; TO NEXT STATEMENT
                                                    ; --------------------------------
GETCHR              INY                             ; PICK UP CHAR FROM TABLE
                    BNE ?1                          ; 
                    INC FAC+1                       ; 
?1                  LDA (FAC),Y                     ; 
                    RTS                             ; 
                                                    ; --------------------------------
LIST_4              BPL LIST_2                      ; BRANCH IF NOT A TOKEN
                    SEC                             ; 
                    SBC #$7F                        ; CONVERT TOKEN TO INDEX
                    TAX                             ; 
                    STY FORPNT                      ; SAVE LINE POINTER
                    LDY #<(TOKEN_NAME_TABLE-$100)
                    STY FAC                         ; POINT FAC TO TABLE
                    LDY #>(TOKEN_NAME_TABLE-$100)
                    STY FAC+1
                    LDY #-1
?1                  DEX                             ; SKIP KEYWORDS UNTIL REACH THIS ONE
                    BEQ ?3                          ; 
?2                  JSR GETCHR                      ; BUMP Y, GET CHAR FROM TABLE
                    BPL ?2                          ; NOT AT END OF KEYWORD YET
                    BMI ?1                          ; END OF KEYWORD, ALWAYS BRANCHES
?3                  LDA #HICHAR(` ')                        ; FOUND THE RIGHT KEYWORD
                    JSR OUTDO                       ; PRINT LEADING SPACE
?4                  JSR GETCHR                      ; PRINT THE KEYWORD
                    BMI ?5                          ; LAST CHAR OF KEYWORD
                    JSR OUTDO                       ; 
                    BNE ?4                          ; ...ALWAYS
?5                  JSR OUTDO                       ; PRINT LAST CHAR OF KEYWORD
                    LDA #HICHAR(` ')                        ; PRINT TRAILING SPACE
                    BNE LIST_1                      ; ...ALWAYS, BACK TO ACTUAL LINE
                                                    ; --------------------------------
                                                    ; "FOR" STATEMENT
                                                    ; 
                                                    ; FOR PUSHES 18 BYTES ON THE STACK:
                                                    ; 2 -- TXTPTR
                                                    ; 2 -- LINE NUMBER
                                                    ; 5 -- INITIAL (CURRENT)  FOR VARIABLE VALUE
                                                    ; 1 -- STEP SIGN
                                                    ; 5 -- STEP VALUE
                                                    ; 2 -- ADDRESS OF FOR VARIABLE IN VARTAB
                                                    ; 1 -- FOR TOKEN ($81)
                                                    ; --------------------------------
FOR                 LDA #$80                        ; 
                    STA SUBFLG                      ; SUBSCRIPTS NOT ALLOWED
                    JSR LET                         ; DO <VAR> = <EXP>, STORE ADDR IN FORPNT
                    JSR GTFORPNT                    ; IS THIS FOR VARIABLE ACTIVE?
                    BNE ?1                          ; NO
                    TXA                             ; YES, CANCEL IT AND ENCLOSED LOOPS
                    ADC #15                         ; CARRY=1, THIS ADDS 16
                    TAX                             ; X WAS ALREADY S+2
                    TXS                             ; 
?1                  PLA                             ; POP RETURN ADDRESS TOO
                    PLA                             ; 
                    LDA #9                          ; BE CERTAIN ENOUGH ROOM IN STACK
                    JSR CHKMEM                      ; 
                    JSR DATAN                       ; SCAN AHEAD TO NEXT STATEMENT
                    CLC                             ; PUSH STATEMENT ADDRESS ON STACK
                    TYA                             ; 
                    ADC TXTPTR                      ; 
                    PHA                             ; 
                    LDA TXTPTR+1                    ; 
                    ADC #0                          ; 
                    PHA                             ; 
                    LDA CURLIN+1                    ; PUSH LINE NUMBER ON STACK
                    PHA                             ; 
                    LDA CURLIN                      ; 
                    PHA                             ; 
                    LDA #TOKEN_TO                   ; 
                    JSR SYNCHR                      ; REQUIRE "TO"
                    JSR CHKNUM                      ; <VAR> = <EXP> MUST BE NUMERIC
                    JSR FRMNUM                      ; GET FINAL VALUE, MUST BE NUMERIC
                    LDA FAC_SIGN                    ; PUT SIGN INTO VALUE IN FAC
                    ORA #$7F                        ; 
                    AND FAC+1                       ; 
                    STA FAC+1                       ; 
                    LDA #<STEP                      ; SET UP FOR RETURN
                    LDY #>STEP                      ; TO STEP
                    STA INDEX
                    STY INDEX+1
                    JMP FRM_STACK_3                 ; RETURNS BY "JMP (INDEX)"
                                                    ; --------------------------------
                                                    ; "STEP" PHRASE OF "FOR" STATEMENT
                                                    ; --------------------------------
STEP                LDA #<CON_ONE                   ; STEP DEFAULT=1
                    LDY #>CON_ONE
                    JSR LOAD_FAC_FROM_YA
                    JSR CHRGOT
                    CMP #TOKEN_STEP
                    BNE ?1                          ; USE DEFAULT VALUE OF 1.0
                    JSR CHRGET                      ; STEP SPECIFIED, GET IT
                    JSR FRMNUM
?1                  JSR SIGN
                    JSR FRM_STACK_2
                    LDA FORPNT+1
                    PHA
                    LDA FORPNT
                    PHA
                    LDA #TOKEN_FOR
                    PHA
                                                    ; --------------------------------
                                                    ; PERFORM NEXT STATEMENT
                                                    ; --------------------------------
NEWSTT              TSX                             ; REMEMBER THE STACK POSITION
                    STX REMSTK                      ; 
                    JSR ISCNTC                      ; SEE IF CONTROL-C HAS BEEN TYPED
                    LDA TXTPTR                      ; NO, KEEP EXECUTING
                    LDY TXTPTR+1                    ; 
                    LDX CURLIN+1                    ; =$FF IF IN DIRECT MODE
                    INX                             ; $FF TURNS INTO $00
                    BEQ ?1                          ; IN DIRECT MODE
                    STA OLDTEXT                     ; IN RUNNING MODE
                    STY OLDTEXT+1                   ; 
?1                  LDY #0                          ; 
                    LDA (TXTPTR),Y                  ; END OF LINE YET?
                    BNE COLON_                      ; NO
                    LDY #2                          ; YES, SEE IF END OF PROGRAM
                    LDA (TXTPTR),Y                  ; 
                    CLC                             ; 
                    BEQ GOEND                       ; YES, END OF PROGRAM
                    INY                             ; 
                    LDA (TXTPTR),Y                  ; GET LINE # OF NEXT LINE
                    STA CURLIN                      ; 
                    INY                             ; 
                    LDA (TXTPTR),Y                  ; 
                    STA CURLIN+1                    ; 
                    TYA                             ; ADJUST TXTPTR TO START
                    ADC TXTPTR                      ; OF NEW LINE
                    STA TXTPTR
                    BCC ?2
                    INC TXTPTR+1
?2
                                                    ; --------------------------------
TRACE_              BIT TRCFLG                      ; IS TRACE ON?
                    BPL ?1                          ; NO
                    LDX CURLIN+1                    ; YES, ARE WE RUNNING?
                    INX                             ; 
                    BEQ ?1                          ; NOT RUNNING, SO DON'T TRACE
                    LDA #HICHAR(`#')                        ; PRINT "#"
                    JSR OUTDO                       ; 
                    LDX CURLIN                      ; 
                    LDA CURLIN+1                    ; 
                    JSR LINPRT                      ; PRINT LINE NUMBER
                    JSR OUTSP                       ; PRINT TRAILING SPACE
?1                  JSR CHRGET                      ; GET FIRST CHR OF STATEMENT
                    JSR EXECUTE_STATEMENT           ; AND START PROCESSING
                    JMP NEWSTT                      ; BACK FOR MORE
                                                    ; --------------------------------
GOEND               BEQ END4
                                                    ; --------------------------------
                                                    ; EXECUTE A STATEMENT
                                                    ; 
                                                    ; (A) IS FIRST CHAR OF STATEMENT
                                                    ; CARRY IS SET
                                                    ; --------------------------------
EXECUTE_STATEMENT
                    BEQ RTS_3                       ; END OF LINE, NULL STATEMENT
EXECUTE_STATEMENT_1                                 ; 
                    SBC #$80                        ; FIRST CHAR A TOKEN?
                    BCC ?1                          ; NOT TOKEN, MUST BE "LET"
                    CMP #$40                        ; STATEMENT-TYPE TOKEN?
                    BCS SYNERR_1                    ; NO, SYNTAX ERROR
                    ASL                             ; DOUBLE TO GET INDEX
                    TAY                             ; INTO ADDRESS TABLE
                    LDA TOKEN_ADDRESS_TABLE+1,Y
                    PHA                             ; PUT ADDRESS ON STACK
                    LDA TOKEN_ADDRESS_TABLE,Y
                    PHA
                    JMP CHRGET                      ; GET NEXT CHR & RTS TO ROUTINE
                                                    ; --------------------------------
?1                  JMP LET                         ; MUST BE <VAR> = <EXP>
                                                    ; --------------------------------
COLON_              CMP #HICHAR(`:')
                    BEQ TRACE_
SYNERR_1            JMP SYNERR
                                                    ; --------------------------------
                                                    ; "RESTORE" STATEMENT
                                                    ; --------------------------------
RESTORE
                    SEC                             ; SET DATPTR TO BEGINNING OF PROGRAM
                    LDA TXTTAB
                    SBC #1
                    LDY TXTTAB+1
                    BCS SETDA
                    DEY
                                                    ; ---SET DATPTR TO Y,A------------
SETDA               STA DATPTR
                    STY DATPTR+1
RTS_3               RTS
                                                    ; --------------------------------
                                                    ; SEE IF CONTROL-C TYPED
                                                    ; --------------------------------
ISCNTC              LDA KEYBOARD
                    CMP #$83
                    BEQ ?1
                    RTS
?1                  JSR INCHR                       ; <<< SHOULD BE "BIT $C010" >>>
CONTROL_C_TYPED
                    LDX #$FF                        ; CONTROL C ATTEMPTED
                    BIT ERRFLG                      ; "ON ERR" ENABLED?
                    BPL ?2                          ; NO
                    JMP HANDLERR                    ; YES, RETURN ERR CODE = 255
?2                  CMP #3                          ; SINCE IT IS CTRL-C, SET Z AND C BITS
                                                    ; --------------------------------
                                                    ; "STOP" STATEMENT
                                                    ; --------------------------------
STOP                BCS END2                        ; CARRY=1 TO FORCE PRINTING "BREAK AT.."
                                                    ; --------------------------------
                                                    ; "END" STATEMENT
                                                    ; --------------------------------
ENDX                CLC                             ; CARRY=0 TO AVOID PRINTING MESSAGE
END2                BNE RTS_4                       ; IF NOT END OF STATEMENT, DO NOTHING
                    LDA TXTPTR
                    LDY TXTPTR+1
                    LDX CURLIN+1
                    INX                             ; RUNNING?
                    BEQ ?1                          ; NO, DIRECT MODE
                    STA OLDTEXT
                    STY OLDTEXT+1
                    LDA CURLIN
                    LDY CURLIN+1
                    STA OLDLIN
                    STY OLDLIN+1
?1                  PLA
                    PLA
END4                LDA #<QT_BREAK                  ; " BREAK" AND BELL
                    LDY #>QT_BREAK
                    BCC ?1
                    JMP PRINT_ERROR_LINNUM
?1                  JMP RESTART
                                                    ; --------------------------------
                                                    ; "CONT" COMMAND
                                                    ; --------------------------------
CONT                BNE RTS_4                       ; IF NOT END OF STATEMENT, DO NOTHING
                    LDX #ERR_CANTCONT
                    LDY OLDTEXT+1                   ; MEANINGFUL RE-ENTRY?
                    BNE ?1                          ; YES
                    JMP ERROR                       ; NO
?1                  LDA OLDTEXT                     ; RESTORE TXTPTR
                    STA TXTPTR                      ; 
                    STY TXTPTR+1                    ; 
                    LDA OLDLIN                      ; RESTORE LINE NUMBER
                    LDY OLDLIN+1
                    STA CURLIN
                    STY CURLIN+1
RTS_4               RTS
                                                    ; --------------------------------
                                                    ; "SAVE" COMMAND
                                                    ; WRITES PROGRAM ON CASSETTE TAPE
                                                    ; --------------------------------
SAVE                SEC
                    LDA PRGEND                      ; COMPUTE PROGRAM LENGTH
                    SBC TXTTAB
                    STA LINNUM
                    LDA PRGEND+1
                    SBC TXTTAB+1
                    STA LINNUM+1
                    JSR VARTIO                      ; SET UP TO WRITE 3 BYTE HEADER
                    JSR MON_WRITE                   ; WRITE 'EM
                    JSR PROGIO                      ; SET UP TO WRITE THE PROGRAM
                    JMP MON_WRITE                   ; WRITE IT
                                                    ; --------------------------------
                                                    ; "LOAD" COMMAND
                                                    ; READS A PROGRAM FROM CASSETTE TAPE
                                                    ; --------------------------------
LOAD                JSR VARTIO                      ; SET UP TO READ 3 BYTE HEADER
                    JSR MON_READ                    ; READ LENGTH, LOCK BYTE
                    CLC                             ; 
                    LDA TXTTAB                      ; COMPUTE END ADDRESS
                    ADC LINNUM                      ; 
                    STA VARTAB                      ; 
                    LDA TXTTAB+1                    ; 
                    ADC LINNUM+1                    ; 
                    STA VARTAB+1                    ; 
                    LDA TEMPPT                      ; LOCK BYTE
                    STA LOCK                        ; 
                    JSR PROGIO                      ; SET UP TO READ PROGRAM
                    JSR MON_READ                    ; READ IT
                    BIT LOCK                        ; IF LOCKED, START RUNNING NOW
                    BPL ?1                          ; NOT LOCKED
                    JMP SETPTRS                     ; LOCKED, START RUNNING
?1                  JMP FIX_LINKS                   ; JUST FIX FORWARD POINTERS
                                                    ; --------------------------------
VARTIO              LDA #LINNUM                     ; SET UP TO READ/WRITE 3 BYTE HEADER
                    LDY #0
                    STA MON_A1L
                    STY MON_A1H
                    LDA #TEMPPT
                    STA MON_A2L
                    STY MON_A2H
                    STY LOCK
                    RTS
                                                    ; --------------------------------
PROGIO              LDA TXTTAB                      ; SET UP TO READ/WRITE PROGRAM
                    LDY TXTTAB+1
                    STA MON_A1L
                    STY MON_A1H
                    LDA VARTAB
                    LDY VARTAB+1
                    STA MON_A2L
                    STY MON_A2H
                    RTS
                                                    ; --------------------------------
                                                    ; --------------------------------
                                                    ; "RUN" COMMAND
                                                    ; --------------------------------
RUN                 PHP                             ; SAVE STATUS WHILE SUBTRACTING
                    DEC CURLIN+1                    ; IF WAS $FF (MEANING DIRECT MODE)
                                                    ; MAKE IT "RUNNING MODE"
                    PLP                             ; GET STATUS AGAIN (FROM CHRGET)
                    BNE ?1                          ; PROBABLY A LINE NUMBER
                    JMP SETPTRS                     ; START AT BEGINNING OF PROGRAM
?1                  JSR CLEARC                      ; CLEAR VARIABLES
                    JMP GO_TO_LINE                  ; JOIN GOSUB STATEMENT
                                                    ; --------------------------------
                                                    ; "GOSUB" STATEMENT
                                                    ; 
                                                    ; LEAVES 7 BYTES ON STACK:
                                                    ; 2 -- RETURN ADDRESS (NEWSTT)
                                                    ; 2 -- TXTPTR
                                                    ; 2 -- LINE #
                                                    ; 1 -- GOSUB TOKEN ($B0)
                                                    ; --------------------------------
GOSUB               LDA #3                          ; BE SURE ENOUGH ROOM ON STACK
                    JSR CHKMEM
                    LDA TXTPTR+1
                    PHA
                    LDA TXTPTR
                    PHA
                    LDA CURLIN+1
                    PHA
                    LDA CURLIN
                    PHA
                    LDA #TOKEN_GOSUB
                    PHA
GO_TO_LINE
                    JSR CHRGOT
                    JSR GOTO
                    JMP NEWSTT
                                                    ; --------------------------------
                                                    ; "GOTO" STATEMENT
                                                    ; ALSO USED BY "RUN" AND "GOSUB"
                                                    ; --------------------------------
GOTO                JSR LINGET                      ; GET GOTO LINE
                    JSR REMN                        ; POINT Y TO EOL
                    LDA CURLIN+1                    ; IS CURRENT PAGE < GOTO PAGE?
                    CMP LINNUM+1                    ; 
                    BCS ?1                          ; SEARCH FROM PROG START IF NOT
                    TYA                             ; OTHERWISE SEARCH FROM NEXT LINE
                    SEC                             ; 
                    ADC TXTPTR                      ; 
                    LDX TXTPTR+1                    ; 
                    BCC ?2                          ; 
                    INX                             ; 
                    BCS ?2                          ; 
?1                  LDA TXTTAB                      ; GET PROGRAM BEGINNING
                    LDX TXTTAB+1                    ; 
?2                  JSR FL1                         ; SEARCH FOR GOTO LINE
                    BCC UNDERR                      ; ERROR IF NOT THERE
                    LDA LOWTR                       ; TXTPTR = START OF THE DESTINATION LINE
                    SBC #1                          ; 
                    STA TXTPTR                      ; 
                    LDA LOWTR+1                     ; 
                    SBC #0                          ; 
                    STA TXTPTR+1                    ; 
RTS_5               RTS                             ; RETURN TO NEWSTT OR GOSUB
                                                    ; --------------------------------
                                                    ; "POP" AND "RETURN" STATEMENTS
                                                    ; --------------------------------
POP                 BNE RTS_5
                    LDA #$FF
                    STA FORPNT                      ; <<< BUG: SHOULD BE FORPNT+1 >>>
                                                    ; <<< SEE "ALL ABOUT APPLESOFT", PAGES 100,101 >>>
                    JSR GTFORPNT                    ; TO CANCEL FOR/NEXT IN SUB
                    TXS
                    CMP #TOKEN_GOSUB                ; LAST GOSUB FOUND?
                    BEQ RETURN
                    LDX #ERR_NOGOSUB
                    ASM_DATA($2C)                       ; FAKE
UNDERR              LDX #ERR_UNDEFSTAT
                    JMP ERROR
                                                    ; --------------------------------
SYNERR_2            JMP SYNERR
                                                    ; --------------------------------
RETURN              PLA                             ; DISCARD GOSUB TOKEN
                    PLA
                    CPY #<(TOKEN_POP*2)
                    BEQ PULL3                       ; BRANCH IF A POP
                    STA CURLIN                      ; PULL LINE #
                    PLA
                    STA CURLIN+1
                    PLA
                    STA TXTPTR                      ; PULL TXTPTR
                    PLA
                    STA TXTPTR+1
                                                    ; --------------------------------
                                                    ; "DATA" STATEMENT
                                                    ; EXECUTED BY SKIPPING TO NEXT COLON OR EOL
                                                    ; --------------------------------
DATA                JSR DATAN                       ; MOVE TO NEXT STATEMENT
                                                    ; --------------------------------
                                                    ; ADD (Y) TO TXTPTR
                                                    ; --------------------------------
ADDON               TYA
                    CLC
                    ADC TXTPTR
                    STA TXTPTR
                    BCC ?1
                    INC TXTPTR+1
?1
RTS_6               RTS
                                                    ; --------------------------------
                                                    ; SCAN AHEAD TO NEXT ":" OR EOL
                                                    ; --------------------------------
DATAN               LDX #HICHAR(`:')                        ; GET OFFSET IN Y TO EOL OR ":"
                    ASM_DATA($2C)                       ; FAKE
                                                    ; --------------------------------
REMN                LDX #0                          ; TO EOL ONLY
                    STX CHARAC
                    LDY #0
                    STY ENDCHR
?1                  LDA ENDCHR                      ; TRICK TO COUNT QUOTE PARITY
                    LDX CHARAC
                    STA CHARAC
                    STX ENDCHR
?2                  LDA (TXTPTR),Y
                    BEQ RTS_6                       ; END OF LINE
                    CMP ENDCHR
                    BEQ RTS_6                       ; COLON IF LOOKING FOR COLONS
                    INY
                    CMP #HICHAR(`"')
                    BNE ?2
                    BEQ ?1                          ; ...ALWAYS
                                                    ; --------------------------------
PULL3               PLA
                    PLA
                    PLA
                    RTS
                                                    ; --------------------------------
                                                    ; "IF" STATEMENT
                                                    ; --------------------------------
IF                  JSR FRMEVL
                    JSR CHRGOT
                    CMP #TOKEN_GOTO
                    BEQ ?1
                    LDA #TOKEN_THEN
                    JSR SYNCHR
?1                  LDA FAC                         ; CONDITION TRUE OR FALSE?
                    BNE IF_TRUE                     ; BRANCH IF TRUE
                                                    ; --------------------------------
                                                    ; "REM" STATEMENT, OR FALSE "IF" STATEMENT
                                                    ; --------------------------------
REM                 JSR REMN                        ; SKIP REST OF LINE
                    BEQ ADDON                       ; ...ALWAYS
                                                    ; --------------------------------
IF_TRUE
                    JSR CHRGOT                      ; COMMAND OR NUMBER?
                    BCS ?1                          ; COMMAND
                    JMP GOTO                        ; NUMBER
?1                  JMP EXECUTE_STATEMENT
                                                    ; --------------------------------
                                                    ; "ON" STATEMENT
                                                    ; 
                                                    ; ON <EXP> GOTO <LIST>
                                                    ; ON <EXP> GOSUB <LIST>
                                                    ; --------------------------------
ONGOTO              JSR GETBYT                      ; EVALUATE <EXP>, AS BYTE IN FAC+4
                    PHA                             ; SAVE NEXT CHAR ON STACK
                    CMP #TOKEN_GOSUB
                    BEQ ON_2
ON_1                CMP #TOKEN_GOTO
                    BNE SYNERR_2
ON_2                DEC FAC+4                       ; COUNTED TO RIGHT ONE YET?
                    BNE ?3                          ; NO, KEEP LOOKING
                    PLA                             ; YES, RETRIEVE CMD
                    JMP EXECUTE_STATEMENT_1         ; AND GO.
?3                  JSR CHRGET                      ; PRIME CONVERT SUBROUTINE
                    JSR LINGET                      ; CONVERT LINE #
                    CMP #HICHAR(`,')                        ; TERMINATE WITH COMMA?
                    BEQ ON_2                        ; YES
                    PLA                             ; NO, END OF LIST, SO IGNORE
RTS_7               RTS
                                                    ; --------------------------------
                                                    ; CONVERT LINE NUMBER
                                                    ; --------------------------------
LINGET              LDX #0                          ; ASC # TO HEX ADDRESS
                    STX LINNUM                      ; IN LINNUM.
                    STX LINNUM+1                    ; 
?1                  BCS RTS_7                       ; NOT A DIGIT
                    SBC #HICHAR(`0')-1                      ; CONVERT DIGIT TO BINARY
                    STA CHARAC                      ; SAVE THE DIGIT
                    LDA LINNUM+1                    ; CHECK RANGE
                    STA INDEX                       ; 
                    CMP #>6400                      ; LINE # TOO LARGE?
                    BCS ON_1                        ; YES, > 63999, GO INDIRECTLY TO
                                                    ; "SYNTAX ERROR".
                                                    ; <<<<<DANGEROUS CODE>>>>>
                                                    ; NOTE THAT IF (A) = $AB ON THE LINE ABOVE,
                                                    ; ON_1 WILL COMPARE = AND CAUSE A CATASTROPHIC
                                                    ; JUMP TO $22D9 (FOR GOTO), OR OTHER LOCATIONS
                                                    ; FOR OTHER CALLS TO LINGET.
                                                    ; 
                                                    ; YOU CAN SEE THIS IS YOU FIRST PUT "BRK" IN $22D9,
                                                    ; THEN TYPE "GO TO 437761".
                                                    ; 
                                                    ; ANY VALUE FROM 437760 THROUGH 440319 WILL CAUSE
                                                    ; THE PROBLEM.  ($AB00 - $ABFF)
                                                    ; <<<<<DANGEROUS CODE>>>>>
                    LDA LINNUM                      ; MULTIPLY BY TEN
                    ASL
                    ROL INDEX
                    ASL
                    ROL INDEX
                    ADC LINNUM
                    STA LINNUM
                    LDA INDEX
                    ADC LINNUM+1
                    STA LINNUM+1
                    ASL LINNUM
                    ROL LINNUM+1
                    LDA LINNUM
                    ADC CHARAC                      ; ADD DIGIT
                    STA LINNUM
                    BCC ?2
                    INC LINNUM+1
?2                  JSR CHRGET                      ; GET NEXT CHAR
                    JMP ?1                          ; MORE CONVERTING
                                                    ; --------------------------------
                                                    ; "LET" STATEMENT
                                                    ; 
                                                    ; LET <VAR> = <EXP>
                                                    ; <VAR> = <EXP>
                                                    ; --------------------------------
LET                 JSR PTRGET                      ; GET <VAR>
                    STA FORPNT
                    STY FORPNT+1
                    LDA #TOKENEQUUAL
                    JSR SYNCHR
                    LDA VALTYP+1                    ; SAVE VARIABLE TYPE
                    PHA
                    LDA VALTYP
                    PHA
                    JSR FRMEVL                      ; EVALUATE <EXP>
                    PLA
                    ROL
                    JSR CHKVAL
                    BNE LET_STRING
                    PLA
                                                    ; --------------------------------
LET2                BPL ?1                          ; REAL VARIABLE
                    JSR ROUND_FAC                   ; INTEGER VAR: ROUND TO 32 BITS
                    JSR AYINT                       ; TRUNCATE TO 16-BITS
                    LDY #0
                    LDA FAC+3
                    STA (FORPNT),Y
                    INY
                    LDA FAC+4
                    STA (FORPNT),Y
                    RTS
                                                    ; --------------------------------
                                                    ; REAL VARIABLE = EXPRESSION
                                                    ; --------------------------------
?1                  JMP SETFOR
                                                    ; --------------------------------
LET_STRING
                    PLA
                                                    ; --------------------------------
                                                    ; INSTALL STRING, DESCRIPTOR ADDRESS IS AT FAC+3,4
                                                    ; --------------------------------
PUTSTR              LDY #2                          ; STRING DATA ALREADY IN STRING AREA?
                    LDA (FAC+3),Y                   ; (STRING AREA IS BTWN FRETOP
                    CMP FRETOP+1                    ; HIMEM)
                    BCC ?2                          ; YES, DATA ALREADY UP THERE
                    BNE ?1                          ; NO
                    DEY                             ; MAYBE, TEST LOW BYTE OF POINTER
                    LDA (FAC+3),Y                   ; 
                    CMP FRETOP                      ; 
                    BCC ?2                          ; YES, ALREADY THERE
?1                  LDY FAC+4                       ; NO. DESCRIPTOR ALREADY AMONG VARIABLES?
                    CPY VARTAB+1                    ; 
                    BCC ?2                          ; NO
                    BNE ?3                          ; YES
                    LDA FAC+3                       ; MAYBE, COMPARE LO-BYTE
                    CMP VARTAB                      ; 
                    BCS ?3                          ; YES, DESCRIPTOR IS AMONG VARIABLES
?2                  LDA FAC+3                       ; EITHER STRING ALREADY ON TOP, OR
                    LDY FAC+4                       ; DESCRIPTOR IS NOT A VARIABLE
                    JMP ?4                          ; SO JUST STORE THE DESCRIPTOR
                                                    ; --------------------------------
                                                    ; STRING NOT YET IN STRING AREA,
                                                    ; AND DESCRIPTOR IS A VARIABLE
                                                    ; --------------------------------
?3                  LDY #0                          ; POINT AT LENGTH IN DESCRIPTOR
                    LDA (FAC+3),Y                   ; GET LENGTH
                    JSR STRINI                      ; MAKE A STRING THAT LONG UP ABOVE
                    LDA DSCPTR                      ; SET UP SOURCE PNTR FOR MONINS
                    LDY DSCPTR+1                    ; 
                    STA STRNG1                      ; 
                    STY STRNG1+1                    ; 
                    JSR MOVINS                      ; MOVE STRING DATA TO NEW AREA
                    LDA #<FAC                       ; ADDRESS OF DESCRIPTOR IS IN FAC
                    LDY #>FAC                       ; 
?4                  STA DSCPTR                      ; 
                    STY DSCPTR+1                    ; 
                    JSR FRETMS                      ; DISCARD DESCRIPTOR IF 'TWAS TEMPORARY
                    LDY #0                          ; COPY STRING DESCRIPTOR
                    LDA (DSCPTR),Y
                    STA (FORPNT),Y
                    INY
                    LDA (DSCPTR),Y
                    STA (FORPNT),Y
                    INY
                    LDA (DSCPTR),Y
                    STA (FORPNT),Y
                    RTS
                                                    ; --------------------------------
PR_STRING
                    JSR STRPRT
                    JSR CHRGOT
                                                    ; --------------------------------
                                                    ; "PRINT" STATEMENT
                                                    ; --------------------------------
PRINT               BEQ CRDO                        ; NO MORE LIST, PRINT <RETURN>
                                                    ; --------------------------------
PRINT2              BEQ RTS_8                       ; NO MORE LIST, DON'T PRINT <RETURN>
                    CMP #TOKEN_TAB
                    BEQ PR_TAB_OR_SPC               ; C=1 FOR TAB(
                    CMP #TOKEN_SPC
                    CLC
                    BEQ PR_TAB_OR_SPC               ; C=0 FOR SPC(
                    CMP #HICHAR(`,')
                    CLC                             ; <<< NO PURPOSE TO THIS >>>
                    BEQ PR_COMMA                    ; 
                    CMP #HICHAR(`;')
                    BEQ PR_NEXT_CHAR                ; 
                    JSR FRMEVL                      ; EVALUATE EXPRESSION
                    BIT VALTYP                      ; STRING OR FP VALUE?
                    BMI PR_STRING                   ; STRING
                    JSR FOUT                        ; FP: CONVERT INTO BUFFER
                    JSR STRLIT                      ; MAKE BUFFER INTO STRING
                    JMP PR_STRING                   ; PRINT THE STRING
                                                    ; --------------------------------
CRDO                LDA #$0D                        ; PRINT <RETURN>
                    JSR OUTDO
NEGATE              EOR #$FF                        ; <<< WHY??? >>>
RTS_8               RTS
                                                    ; --------------------------------
                                                    ; TAB TO NEXT COMMA COLUMN
                                                    ; <<< NOTE BUG IF WIDTH OF WINDOW LESS THAN 33 >>>
PR_COMMA
                    LDA MON_CH
                    CMP #24                         ; <<< BUG:  IT SHOULD BE 32 >>>
                    BCC ?1                          ; NEXT COLUMN, SAME LINE
                    JSR CRDO                        ; FIRST COLUMN, NEXT LINT
                    BNE PR_NEXT_CHAR                ; ...ALWAYS
?1                  ADC #16
                    AND #$F0                        ; ROUND TO 16 OR 32
                    STA MON_CH
                    BCC PR_NEXT_CHAR                ; ...ALWAYS
                                                    ; --------------------------------
PR_TAB_OR_SPC
                    PHP                             ; C=0 FOR SPC(, C=1 FOR TAB(
                    JSR GTBYTC                      ; GET VALUE
                    CMP #HICHAR(`)')                        ; TRAILING PARENTHESIS
                    BEQ ?1                          ; GOOD
                    JMP SYNERR                      ; NO, SYNTAX ERROR
?1                  PLP                             ; TAB( OR SPC(
                    BCC ?2                          ; SPC(
                    DEX                             ; TAB(
                    TXA                             ; CALCULATE SPACES NEEDED FOR TAB(
                    SBC MON_CH
                    BCC PR_NEXT_CHAR                ; ALREADY PAST THAT COLUMN
                    TAX                             ; NOW DO A SPC( TO THE SPECIFIED COLUMN
?2                  INX
NXSPC               DEX
                    BNE DOSPC                       ; MORE SPACES TO PRINT
                                                    ; --------------------------------
PR_NEXT_CHAR
                    JSR CHRGET
                    JMP PRINT2                      ; CONTINUE PARSING PRINT LIST
                                                    ; --------------------------------
DOSPC               JSR OUTSP
                    BNE NXSPC                       ; ...ALWAYS
                                                    ; --------------------------------
                                                    ; PRINT STRING AT (Y,A)
STROUT              JSR STRLIT                      ; MAKE (Y,A) PRINTABLE
                                                    ; --------------------------------
                                                    ; PRINT STRING AT (FACMO,FACLO)
                                                    ; --------------------------------
STRPRT              JSR FREFAC                      ; GET ADDRESS INTO INDEX, (A)=LENGTH
                    TAX                             ; USE X-REG FOR COUNTER
                    LDY #0                          ; USE Y-REG FOR SCANNER
                    INX                             ; 
?1                  DEX                             ; 
                    BEQ RTS_8                       ; FINISHED
                    LDA (INDEX),Y                   ; NEXT CHAR FROM STRING
                    JSR OUTDO                       ; PRINT THE CHAR
                    INY                             ; 
                                                    ; <<< NEXT THREE LINES ARE USELESS >>>
                    CMP #$0D                        ; WAS IT <RETURN>?
                    BNE ?1                          ; NO
                    JSR NEGATE                      ; EOR #$FF WOULD DO IT, BUT WHY?
                                                    ; <<< ABOVE THREE LINES ARE USELESS >>>
                    JMP ?1
                                                    ; --------------------------------
OUTSP               LDA #HICHAR(` ')                        ; PRINT A SPACE
                    ASM_DATA($2C)                       ; SKIP OVER NEXT LINE
OUTQUES             LDA #HICHAR(`?')                        ; PRINT QUESTION MARK
                                                    ; --------------------------------
                                                    ; PRINT CHAR FROM (A)
                                                    ; 
                                                    ; NOTE: POKE 243,32 ($20 IN $F3) WILL CONVERT
                                                    ; OUTPUT TO LOWER CASE.  THIS CAN BE CANCELLED
                                                    ; BY NORMAL, INVERSE, OR FLASH OR POKE 243,0.
                                                    ; --------------------------------
OUTDO               ORA #$80                        ; PRINT (A)
                    CMP #$A0                        ; CONTROL CHR?
                    BCC ?1                          ; SKIP IF SO
                    ORA FLASH_BIT                   ; =$40 FOR FLASH, ELSE $00
?1                  JSR MON_COUT                    ; "AND"S WITH $3F (INVERSE), $7F (FLASH)
                    AND #$7F                        ; 
                    PHA                             ; 
                    LDA SPEEDZ                      ; COMPLEMENT OF SPEED #
                    JSR MON_WAIT                    ; SO SPEED=255 BECOMES (A)=1
                    PLA
                    RTS
                                                    ; --------------------------------
                                                    ; INPUT CONVERSION ERROR:  ILLEGAL CHARACTER
                                                    ; IN NUMERIC FIELD.  MUST DISTINGUISH
                                                    ; BETWEEN INPUT, READ, AND GET
                                                    ; --------------------------------
INPUTERR
                    LDA INPUTFLG
                    BEQ RESPERR                     ; TAKEN IF INPUT
                    BMI READERR                     ; TAKEN IF READ
                    LDY #$FF                        ; FROM A GET
                    BNE ERLIN                       ; ...ALWAYS
                                                    ; --------------------------------
READERR
                    LDA DATLIN                      ; TELL WHERE THE "DATA" IS, RATHER
                    LDY DATLIN+1                    ; THAN THE "READ"
                                                    ; --------------------------------
ERLIN               STA CURLIN
                    STY CURLIN+1
                    JMP SYNERR
                                                    ; --------------------------------
INPERR              PLA
                                                    ; --------------------------------
RESPERR
                    BIT ERRFLG                      ; "ON ERR" TURNED ON?
                    BPL ?1                          ; NO, GIVE REENTRY A TRY
                    LDX #254                        ; ERROR CODE = 254
                    JMP HANDLERR
?1                  LDA #<ERR_REENTRY               ; "?REENTER"
                    LDY #>ERR_REENTRY
                    JSR STROUT
                    LDA OLDTEXT                     ; RE-EXECUTE THE WHOLE INPUT STATEMENT
                    LDY OLDTEXT+1
                    STA TXTPTR
                    STY TXTPTR+1
                    RTS
                                                    ; --------------------------------
                                                    ; "GET" STATEMENT
                                                    ; --------------------------------
GET                 JSR ERRDIR                      ; ILLEGAL IF IN DIRECT MODE
                    LDX #<(INPUT_BUFFER+1)          ; SIMULATE INPUT
                    LDY #>(INPUT_BUFFER+1)
                    LDA #0
                    STA INPUT_BUFFER+1
                    LDA #$40                        ; SET UP INPUTFLG
                    JSR PROCESS_INPUT_LIST          ; <<< CAN SAVE 1 BYTE HERE>>>
                    RTS                             ; <<<BY "JMP PROCESS.INPUT.LIST">>>
                                                    ; --------------------------------
                                                    ; "INPUT" STATEMENT
                                                    ; --------------------------------
INPUT               CMP #HICHAR(`"')                        ; CHECK FOR OPTIONAL PROMPT STRING
                    BNE ?1                          ; NO, PRINT "?" PROMPT
                    JSR STRTXT                      ; MAKE A PRINTABLE STRING OUT OF IT
                    LDA #HICHAR(`;')                        ; MUST HAVE ; NOW
                    JSR SYNCHR                      ; 
                    JSR STRPRT                      ; PRINT THE STRING
                    JMP ?2                          ; 
?1                  JSR OUTQUES                     ; NO STRING, PRINT "?"
?2                  JSR ERRDIR                      ; ILLEGAL IF IN DIRECT MODE
                    LDA #HICHAR(`,')                        ; PRIME THE BUFFER
                    STA INPUT_BUFFER-1
                    JSR INLIN
                    LDA INPUT_BUFFER
                    CMP #$03                        ; CONTROL C?
                    BNE INPUT_FLAG_ZERO             ; NO
                    JMP CONTROL_C_TYPED
                                                    ; --------------------------------
NXIN                JSR OUTQUES                     ; PRINT "?"
                    JMP INLIN
                                                    ; --------------------------------
                                                    ; "READ" STATEMENT
                                                    ; --------------------------------
READ                LDX DATPTR                      ; Y,X POINTS AT NEXT DATA STATEMENT
                    LDY DATPTR+1                    ; 
                    LDA #$98                        ; SET INPUTFLG = $98
                    ASM_DATA($2C)                       ; TRICK TO PROCESS.INPUT.LIST
                                                    ; --------------------------------
INPUT_FLAG_ZERO     LDA #0                          ; SET INPUTFLG = $00
                                                    ; --------------------------------
                                                    ; PROCESS INPUT LIST
                                                    ; 
                                                    ; (Y,X) IS ADDRESS OF INPUT DATA STRING
                                                    ; (A) = VALUE FOR INPUTFLG:  $00 FOR INPUT
                                                    ; $40 FOR GET
                                                    ; $98 FOR READ
                                                    ; --------------------------------
PROCESS_INPUT_LIST  STA INPUTFLG
                    STX INPTR                       ; ADDRESS OF INPUT STRING
                    STY INPTR+1
                                                    ; --------------------------------
PROCESS_INPUT_ITEM  JSR PTRGET                      ; GET ADDRESS OF VARIABLE
                    STA FORPNT                      ; 
                    STY FORPNT+1                    ; 
                    LDA TXTPTR                      ; SAVE CURRENT TXTPTR,
                    LDY TXTPTR+1                    ; WHICH POINTS INTO PROGRAM
                    STA TXPSV                       ; 
                    STY TXPSV+1                     ; 
                    LDX INPTR                       ; SET TXTPTR TO POINT AT INPUT BUFFER
                    LDY INPTR+1                     ; OR "DATA" LINE
                    STX TXTPTR                      ; 
                    STY TXTPTR+1                    ; 
                    JSR CHRGOT                      ; GET CHAR AT PNTR
                    BNE INSTART                     ; NOT END OF LINE OR COLON
                    BIT INPUTFLG                    ; DOING A "GET"?
                    BVC ?1                          ; NO
                    JSR MON_RDKEY                   ; YES, GET CHAR
                    AND #$7F
                    STA INPUT_BUFFER
                    LDX #<(INPUT_BUFFER-1)
                    LDY #>(INPUT_BUFFER-1)
                    BNE ?2                          ; ...ALWAYS
                                                    ; --------------------------------
?1                  BMI FINDATA                     ; DOING A "READ"
                    JSR OUTQUES                     ; DOING AN "INPUT", PRINT "?"
                    JSR NXIN                        ; PRINT ANOTHER "?", AND INPUT A LINE
?2                  STX TXTPTR
                    STY TXTPTR+1
                                                    ; --------------------------------
INSTART
                    JSR CHRGET                      ; GET NEXT INPUT CHAR
                    BIT VALTYP                      ; STRING OR NUMERIC?
                    BPL ?5                          ; NUMERIC
                    BIT INPUTFLG                    ; STRING -- NOW WHAT INPUT TYPE?
                    BVC ?1                          ; NOT A "GET"
                    INX                             ; "GET"
                    STX TXTPTR
                    LDA #0
                    STA CHARAC                      ; NO OTHER TERMINATORS THAN $00
                    BEQ ?2                          ; ...ALWAYS
                                                    ; --------------------------------
?1                  STA CHARAC
                    CMP #HICHAR(`"')                        ; TERMINATE ON $00 OR QUOTE
                    BEQ ?3
                    LDA #HICHAR(`:')                        ; TERMINATE ON $00, COLON, OR COMMA
                    STA CHARAC
                    LDA #HICHAR(`,')
?2                  CLC
?3                  STA ENDCHR
                    LDA TXTPTR
                    LDY TXTPTR+1
                    ADC #0                          ; SKIP OVER QUOTATION MARK, IF
                    BCC ?4                          ; THERE WAS ONE
                    INY
?4                  JSR STRLT2                      ; BUILD STRING STARTING AT (Y,A)
                                                    ; TERMINATED BY $00, (CHARAC), OR (ENDCHR)
                    JSR POINT                       ; SET TXTPTR TO POINT AT STRING
                    JSR PUTSTR                      ; STORE STRING IN VARIABLE
                    JMP INPUT_MORE
                                                    ; --------------------------------
?5                  PHA
                    LDA INPUT_BUFFER                ; ANYTHING IN BUFFER?
                    BEQ INPFIN                      ; NO, SEE IF READ OR INPUT
                                                    ; --------------------------------
INPUTDWTA
                    PLA                             ; "READ"
                    JSR FIN                         ; GET FP NUMBER AT TXTPTR
                    LDA VALTYP+1                    ; 
                    JSR LET2                        ; STORE RESULT IN VARIABLE
                                                    ; --------------------------------
INPUT_MORE
                    JSR CHRGOT
                    BEQ ?1                          ; END OF LINE OR COLON
                    CMP #HICHAR(`,')                        ; COMMA IN INPUT?
                    BEQ ?1                          ; YES
                    JMP INPUTERR                    ; NOTHING ELSE WILL DO
?1                  LDA TXTPTR                      ; SAVE POSITION IN INPUT BUFFER
                    LDY TXTPTR+1                    ; 
                    STA INPTR                       ; 
                    STY INPTR+1                     ; 
                    LDA TXPSV                       ; RESTORE PROGRAM POINTER
                    LDY TXPSV+1                     ; 
                    STA TXTPTR                      ; 
                    STY TXTPTR+1                    ; 
                    JSR CHRGOT                      ; NEXT CHAR FROM PROGRAM
                    BEQ INPDONE                     ; END OF STATEMENT
                    JSR CHKCOM                      ; BETTER BE A COMMA THEN
                    JMP PROCESS_INPUT_ITEM
                                                    ; --------------------------------
INPFIN              LDA INPUTFLG                    ; "INPUT" OR "READ"
                    BNE INPUTDWTA                   ; "READ"
                    JMP INPERR
                                                    ; --------------------------------
FINDATA
                    JSR DATAN                       ; GET OFFSET TO NEXT COLON OR EOL
                    INY                             ; TO FIRST CHAR OF NEXT LINE
                    TAX                             ; WHICH:  EOL OR COLON?
                    BNE ?1                          ; COLON
                    LDX #ERR_NODATA                 ; EOL: MIGHT BE OUT OF DATA
                    INY                             ; CHECK HI-BYTE OF FORWARD PNTR
                    LDA (TXTPTR),Y                  ; END OF PROGRAM?
                    BEQ GERR                        ; YES, WE ARE OUT OF DATA
                    INY                             ; PICK UP THE LINE #
                    LDA (TXTPTR),Y
                    STA DATLIN
                    INY
                    LDA (TXTPTR),Y
                    INY                             ; POINT AT FIRST TEXT CHAR IN LINE
                    STA DATLIN+1
?1                  LDA (TXTPTR),Y                  ; GET 1ST TOKEN OF STATEMENT
                    TAX                             ; SAVE TOKEN IN X-REG
                    JSR ADDON                       ; ADD (Y) TO TXTPTR
                    CPX #TOKENDWTA                  ; DID WE FIND A "DATA" STATEMENT?
                    BNE FINDATA                     ; NOT YET
                    JMP INSTART                     ; YES, READ IT
                                                    ; ---NO MORE INPUT REQUESTED------
INPDONE
                    LDA INPTR                       ; GET POINTER IN CASE IT WAS "READ"
                    LDY INPTR+1
                    LDX INPUTFLG                    ; "READ" OR "INPUT"?
                    BPL ?1                          ; "INPUT"
                    JMP SETDA                       ; "DATA", SO STORE (Y,X) AT DATPTR
?1                  LDY #0                          ; "INPUT":  ANY MORE CHARS ON LINE?
                    LDA (INPTR),Y
                    BEQ ?2                          ; NO, ALL IS WELL
                    LDA #<ERR_EXTRA                 ; YES, ERROR
                    LDY #>ERR_EXTRA                 ; "EXTRA IGNORED"
                    JMP STROUT
?2                  RTS
                                                    ; --------------------------------
ERR_EXTRA           HIASCII(`?EXTRA IGNORED')
                    ASM_DATA($0D,0)

ERR_REENTRY         HIASCII(`?REENTER')
                    ASM_DATA($0D,0)
                                                    ; --------------------------------
                                                    ; --------------------------------
                                                    ; "NEXT" STATEMENT
                                                    ; --------------------------------
NEXT                BNE NEXT_1                      ; VARIABLE AFTER "NEXT"
                    LDY #0                          ; FLAG BY SETTING FORPNT+1 = 0
                    BEQ NEXT_2                      ; ...ALWAYS
                                                    ; --------------------------------
NEXT_1              JSR PTRGET                      ; GET PNTR TO VARIABLE IN (Y,A)
NEXT_2              STA FORPNT
                    STY FORPNT+1
                    JSR GTFORPNT                    ; FIND FOR-FRAME FOR THIS VARIABLE
                    BEQ NEXT_3                      ; FOUND IT
                    LDX #ERR_NOFOR                  ; NOT THERE, ABORT
GERR                BEQ JERROR                      ; ...ALWAYS
NEXT_3              TXS                             ; SET STACK PTR TO POINT TO THIS FRAME,
                    INX                             ; WHICH TRIMS OFF ANY INNER LOOPS
                    INX
                    INX
                    INX
                    TXA                             ; LOW BYTE OF ADRS OF STEP VALUE
                    INX
                    INX
                    INX
                    INX
                    INX
                    INX
                    STX DEST                        ; LOW BYTE ADRS OF FOR VAR VALUE
                    LDY #>STACK                     ; (Y,A) IS ADDRESS OF STEP VALUE
                    JSR LOAD_FAC_FROM_YA            ; STEP TO FAC
                    TSX
                    LDA STACK+9,X
                    STA FAC_SIGN
                    LDA FORPNT
                    LDY FORPNT+1
                    JSR FADD                        ; ADD TO FOR VALUE
                    JSR SETFOR                      ; PUT NEW VALUE BACK
                    LDY #>STACK                     ; (Y,A) IS ADDRESS OF END VALUE
                    JSR FCOMP2                      ; COMPARE TO END VALUE
                    TSX
                    SEC
                    SBC STACK+9,X                   ; SIGN OF STEP
                    BEQ ?2                          ; BRANCH IF FOR COMPLETE
                    LDA STACK+15,X                  ; OTHERWISE SET UP
                    STA CURLIN                      ; FOR LINE #
                    LDA STACK+16,X
                    STA CURLIN+1
                    LDA STACK+18,X                  ; AND SET TXTPTR TO JUST
                    STA TXTPTR                      ; AFTER FOR STATEMENT
                    LDA STACK+17,X
                    STA TXTPTR+1
?1                  JMP NEWSTT
?2                  TXA                             ; POP OFF FOR-FRAME, LOOP IS DONE
                    ADC #17                         ; CARRY IS SET, SO ADDS 18
                    TAX
                    TXS
                    JSR CHRGOT                      ; CHAR AFTER VARIABLE
                    CMP #HICHAR(`,')                        ; ANOTHER VARIABLE IN NEXT_
                    BNE ?1                          ; NO, GO TO NEXT STATEMENT
                    JSR CHRGET                      ; YES, PRIME FOR NEXT VARIABLE
                    JSR NEXT_1                      ; (DOES NOT RETURN)
                                                    ; --------------------------------
                                                    ; EVALUATE EXPRESSION, MAKE SURE IT IS NUMERIC
                                                    ; --------------------------------
FRMNUM              JSR FRMEVL
                                                    ; --------------------------------
                                                    ; MAKE SURE (FAC) IS NUMERIC
                                                    ; --------------------------------
CHKNUM              CLC
                    ASM_DATA($24)                       ; DUMMY FOR SKIP
                                                    ; --------------------------------
                                                    ; MAKE SURE (FAC) IS STRING
                                                    ; --------------------------------
CHKSTR              SEC
                                                    ; --------------------------------
                                                    ; MAKE SURE (FAC) IS CORRECT TYPE
                                                    ; IF C=0, TYPE MUST BE NUMERIC
                                                    ; IF C=1, TYPE MUST BE STRING
                                                    ; --------------------------------
CHKVAL              BIT VALTYP                      ; $00 IF NUMERIC, $FF IF STRING
                    BMI ?2                          ; TYPE IS STRING
                    BCS ?3                          ; NOT STRING, BUT WE NEED STRING
?1                  RTS                             ; TYPE IS CORRECT
?2                  BCS ?1                          ; IS STRING AND WE WANTED STRING
?3                  LDX #ERR_BADTYPE                ; TYPE MISMATCH
JERROR              JMP ERROR
                                                    ; --------------------------------
                                                    ; EVALUATE THE EXPRESSION AT TXTPTR, LEAVING THE
                                                    ; RESULT IN FAC.  WORKS FOR BOTH STRING AND NUMERIC
                                                    ; EXPRESSIONS.
                                                    ; --------------------------------
FRMEVL              LDX TXTPTR                      ; DECREMENT TXTPTR
                    BNE ?1
                    DEC TXTPTR+1
?1                  DEC TXTPTR
                    LDX #0                          ; START WITH PRECEDENCE = 0
                    ASM_DATA($24)                       ; TRICK TO SKIP FOLLOWING "PHA"
                                                    ; --------------------------------
FRMEVL_1
                    PHA                             ; PUSH RELOPS FLAGS
                    TXA                             ; 
                    PHA                             ; SAVE LAST PRECEDENCE
                    LDA #1                          ; 
                    JSR CHKMEM                      ; CHECK IF ENOUGH ROOM ON STACK
                    JSR FRM_ELEMENT                 ; GET AN ELEMENT
                    LDA #0
                    STA CPRTYP                      ; CLEAR COMPARISON OPERATOR FLAGS
                                                    ; --------------------------------
FRMEVL_2
                    JSR CHRGOT                      ; CHECK FOR RELATIONAL OPERATORS
?1                  SEC                             ; > IS $CF, = IS $D0, < IS $D1
                    SBC #TOKEN_GREATER              ; > IS 0, = IS 1, < IS 2
                    BCC ?2                          ; NOT RELATIONAL OPERATOR
                    CMP #3                          ; 
                    BCS ?2                          ; NOT RELATIONAL OPERATOR
                    CMP #1                          ; SET CARRY IF "=" OR "<"
                    ROL                             ; NOW > IS 0, = IS 3, < IS 5
                    EOR #1                          ; NOW > IS 1, = IS 2, < IS 4
                    EOR CPRTYP                      ; SET BITS OF CPRTYP:  00000<=>
                    CMP CPRTYP                      ; CHECK FOR ILLEGAL COMBINATIONS
                    BCC SNTXERR                     ; IF LESS THAN, A RELOP WAS REPEATED
                    STA CPRTYP                      ; 
                    JSR CHRGET                      ; ANOTHER OPERATOR?
                    JMP ?1                          ; CHECK FOR <,=,> AGAIN
                                                    ; --------------------------------
?2                  LDX CPRTYP                      ; DID WE FIND A RELATIONAL OPERATOR?
                    BNE FRM_RELATIONAL              ; YES
                    BCS NOTMATH                     ; NO, AND NEXT TOKEN IS > $D1
                    ADC #$CF-TOKEN_PLUS             ; NO, AND NEXT TOKEN < $CF
                    BCC NOTMATH                     ; IF NEXT TOKEN < "+"
                    ADC VALTYP                      ; + AND LAST RESULT A STRING?
                    BNE ?3                          ; BRANCH IF NOT
                    JMP CAT                         ; CONCATENATE IF SO.
                                                    ; --------------------------------
?3                  ADC #$FF                        ; +-*/ IS 0123
                    STA INDEX
                    ASL                             ; MULTIPLY BY 3
                    ADC INDEX                       ; +-*/ IS 0,3,6,9
                    TAY
                                                    ; --------------------------------
FRM_PRECEDENCE_TEST
                    PLA                             ; GET LAST PRECEDENCE
                    CMP MATHTBL,Y
                    BCS FRM_PERFORM_1               ; DO NOW IF HIGHER PRECEDENCE
                    JSR CHKNUM                      ; WAS LAST RESULT A #?
NXOP                PHA                             ; YES, SAVE PRECEDENCE ON STACK
SAVOP               JSR FRM_RECURSE                 ; SAVE REST, CALL FRMEVL RECURSIVELY
                    PLA
                    LDY LASTOP
                    BPL PREFNC
                    TAX
                    BEQ GOEX                        ; EXIT IF NO MATH IN EXPRESSION
                    BNE FRM_PERFORM_2               ; ...ALWAYS
                                                    ; --------------------------------
                                                    ; FOUND ONE OR MORE RELATIONAL OPERATORS <,=,>
                                                    ; --------------------------------
FRM_RELATIONAL
                    LSR VALTYP                      ; (VALTYP) = 0 (NUMERIC), = $FF (STRING)
                    TXA                             ; SET CPRTYP TO 0000<=>C
                    ROL                             ; WHERE C=0 IF #, C=1 IF STRING
                    LDX TXTPTR                      ; BACK UP TXTPTR
                    BNE ?1
                    DEC TXTPTR+1
?1                  DEC TXTPTR
                    LDY #M_REL-MATHTBL              ; POINT AT RELOPS ENTRY
                    STA CPRTYP
                    BNE FRM_PRECEDENCE_TEST         ; ...ALWAYS
                                                    ; --------------------------------
PREFNC              CMP MATHTBL,Y
                    BCS FRM_PERFORM_2               ; DO NOW IF HIGHER PRECEDENCE
                    BCC NXOP                        ; ...ALWAYS
                                                    ; --------------------------------
                                                    ; STACK THIS OPERATION AND CALL FRMEVL FOR
                                                    ; ANOTHER ONE
                                                    ; --------------------------------
FRM_RECURSE
                    LDA MATHTBL+2,Y
                    PHA                             ; PUSH ADDRESS OF OPERATION PERFORMER
                    LDA MATHTBL+1,Y
                    PHA
                    JSR FRM_STACK_1                 ; STACK FAC.SIGN AND FAC
                    LDA CPRTYP                      ; A=RELOP FLAGS, X=PRECEDENCE BYTE
                    JMP FRMEVL_1                    ; RECURSIVELY CALL FRMEVL
                                                    ; --------------------------------
SNTXERR             JMP SYNERR
                                                    ; --------------------------------
                                                    ; STACK (FAC)
                                                    ; 
                                                    ; THREE ENTRY POINTS:
                                                    ; ?1, FROM FRMEVL
                                                    ; ?2, FROM "STEP"
                                                    ; ?3, FROM "FOR"
                                                    ; --------------------------------
FRM_STACK_1
                    LDA FAC_SIGN                    ; GET FAC.SIGN TO PUSH IT
                    LDX MATHTBL,Y                   ; PRECEDENCE BYTE FROM MATHTBL
                                                    ; --------------------------------
                                                    ; ENTER HERE FROM "STEP", TO PUSH STEP SIGN AND VALUE
                                                    ; --------------------------------
FRM_STACK_2
                    TAY                             ; FAC.SIGN OR SGN(STEP VALUE)
                    PLA                             ; PULL RETURN ADDRESS AND ADD 1
                    STA INDEX                       ; <<< ASSUMES NOT ON PAGE BOUNDARY! >>>
                    INC INDEX                       ; PLACE BUMPED RETURN ADDRESS IN
                    PLA                             ; INDEX,INDEX+1
                    STA INDEX+1                     ; 
                    TYA                             ; FAC.SIGN OR SGN(STEP VALUE)
                    PHA                             ; PUSH FAC.SIGN OR SGN(STEP VALUE)
                                                    ; --------------------------------
                                                    ; ENTER HERE FROM "FOR", WITH (INDEX) = STEP,
                                                    ; TO PUSH INITIAL VALUE OF "FOR" VARIABLE
                                                    ; --------------------------------
FRM_STACK_3
                    JSR ROUND_FAC                   ; ROUND TO 32 BITS
                    LDA FAC+4                       ; PUSH (FAC)
                    PHA
                    LDA FAC+3
                    PHA
                    LDA FAC+2
                    PHA
                    LDA FAC+1
                    PHA
                    LDA FAC
                    PHA
                    JMP (INDEX)                     ; DO RTS FUNNY WAY
                                                    ; --------------------------------
                                                    ; 
                                                    ; --------------------------------
NOTMATH             LDY #$FF                        ; SET UP TO EXIT ROUTINE
                    PLA
GOEX                BEQ EXIT                        ; EXIT IF NO MATH TO DO
                                                    ; --------------------------------
                                                    ; PERFORM STACKED OPERATION
                                                    ; 
                                                    ; (A) = PRECEDENCE BYTE
                                                    ; STACK:  1 -- CPRMASK
                                                    ; 5 -- (ARG)
                                                    ; 2 -- ADDR OF PERFORMER
                                                    ; --------------------------------
FRM_PERFORM_1
                    CMP #P_REL                      ; WAS IT RELATIONAL OPERATOR?
                    BEQ ?1                          ; YES, ALLOW STRING COMPARE
                    JSR CHKNUM                      ; MUST BE NUMERIC VALUE
?1                  STY LASTOP                      ; 
                                                    ; --------------------------------
FRM_PERFORM_2                                       ; 
                    PLA                             ; GET 0000<=>C FROM STACK
                    LSR                             ; SHIFT TO 00000<=> FORM
                    STA CPRMASK                     ; 00000<=>
                    PLA                             ; 
                    STA ARG                         ; GET FLOATING POINT VALUE OFF STACK,
                    PLA                             ; AND PUT IT IN ARG
                    STA ARG+1                       ; 
                    PLA                             ; 
                    STA ARG+2                       ; 
                    PLA                             ; 
                    STA ARG+3                       ; 
                    PLA                             ; 
                    STA ARG+4                       ; 
                    PLA                             ; 
                    STA ARG+5                       ; 
                    EOR FAC_SIGN                    ; SAVE EOR OF SIGNS OF THE OPERANDS,
                    STA SGNCPR                      ; IN CASE OF MULTIPLY OR DIVIDE
EXIT                LDA FAC                         ; FAC EXPONENT IN A-REG
                    RTS                             ; STATUS EQU. IF (FAC)=0
                                                    ; RTS GOES TO PERFORM OPERATION
                                                    ; --------------------------------
                                                    ; GET ELEMENT IN EXPRESSION
                                                    ; 
                                                    ; GET VALUE OF VARIABLE OR NUMBER AT TXTPNT, OR POINT
                                                    ; TO STRING DESCRIPTOR IF A STRING, AND PUT IN FAC.
                                                    ; --------------------------------
FRM_ELEMENT                                         ; 
                    LDA #0                          ; ASSUME NUMERIC
                    STA VALTYP                      ; 
?1                  JSR CHRGET                      ; 
                    BCS ?3                          ; NOT A DIGIT
?2                  JMP FIN                         ; NUMERIC CONSTANT
?3                  JSR ISLETC                      ; VARIABLE NAME?
                    BCS FRM_VARIABLE                ; YES
                    CMP #HICHAR(`.')                        ; DECIMAL POINT
                    BEQ ?2                          ; YES, NUMERIC CONSTANT
                    CMP #TOKEN_MINUS                ; UNARY MINUS?
                    BEQ MIN                         ; YES
                    CMP #TOKEN_PLUS                 ; UNARY PLUS
                    BEQ ?1                          ; YES
                    CMP #HICHAR(`"')                        ; STRING CONSTANT?
                    BNE NOT_                        ; NO
                                                    ; --------------------------------
                                                    ; STRING CONSTANT ELEMENT
                                                    ; 
                                                    ; SET Y,A = (TXTPTR)+CARRY
                                                    ; --------------------------------
STRTXT              LDA TXTPTR                      ; ADD (CARRY) TO GET ADDRESS OF 1ST CHAR
                    LDY TXTPTR+1                    ; OF STRING IN Y,A
                    ADC #0                          ; 
                    BCC ?1                          ; 
                    INY                             ; 
?1                  JSR STRLIT                      ; BUILD DESCRIPTOR TO STRING
                                                    ; GET ADDRESS OF DESCRIPTOR IN FAC
                    JMP POINT                       ; POINT TXTPTR AFTER TRAILING QUOTE
                                                    ; --------------------------------
                                                    ; "NOT" FUNCTION
                                                    ; IF FAC=0, RETURN FAC=1
                                                    ; IF FAC<>0, RETURN FAC=0
                                                    ; --------------------------------
NOT_                CMP #TOKEN_NOT
                    BNE FN_                         ; NOT "NOT", TRY "FN"
                    LDY #MEQUU-MATHTBL              ; POINT AT = COMPARISON
                    BNE EQUL                        ; ...ALWAYS
                                                    ; --------------------------------
                                                    ; COMPARISON FOR EQUALITY (= OPERATOR)
                                                    ; ALSO USED TO EVALUATE "NOT" FUNCTION
                                                    ; --------------------------------
EQUOP               LDA FAC                         ; SET "TRUE" IF (FAC) = ZERO
                    BNE ?1                          ; FALSE
                    LDY #1                          ; TRUE
                    ASM_DATA($2C)                       ; TRICK TO SKIP NEXT 2 BYTES
?1                  LDY #0                          ; FALSE
                    JMP SNGFLT                      ; 
                                                    ; --------------------------------
FN_                 CMP #TOKEN_FN
                    BNE SGN_
                    JMP FUNCT
                                                    ; --------------------------------
SGN_                CMP #TOKEN_SGN
                    BCC PARCHK
                    JMP UNARY
                                                    ; --------------------------------
                                                    ; EVALUATE "(EXPRESSION)"
                                                    ; --------------------------------
PARCHK              JSR CHKOPN                      ; IS THERE A '(' AT TXTPTR?
                    JSR FRMEVL                      ; YES, EVALUATE EXPRESSION
                                                    ; --------------------------------
CHKCLS              LDA #HICHAR(`)')                        ; CHECK FOR ')'
                    ASM_DATA($2C)                       ; TRICK
                                                    ; --------------------------------
CHKOPN              LDA #HICHAR(`(')                        ; 
                    ASM_DATA($2C)                       ; TRICK
                                                    ; --------------------------------
CHKCOM              LDA #HICHAR(`,')                        ; COMMA AT TXTPTR?
                                                    ; --------------------------------
                                                    ; UNLESS CHAR AT TXTPTR = (A), SYNTAX ERROR
                                                    ; --------------------------------
SYNCHR              LDY #0
                    CMP (TXTPTR),Y
                    BNE SYNERR
                    JMP CHRGET                      ; MATCH, GET NEXT CHAR & RETURN
                                                    ; --------------------------------
SYNERR              LDX #ERR_SYNTAX
                    JMP ERROR
                                                    ; --------------------------------
MIN                 LDY #M_NEG-MATHTBL              ; POINT AT UNARY MINUS
EQUL                PLA
                    PLA
                    JMP SAVOP
                                                    ; --------------------------------
FRM_VARIABLE
                    JSR PTRGET
FRM_VARIABLE_CALL   = *-1                           ; SO PTRGET CAN TELL WE CALLED
                    STA VPNT                        ; ADDRESS OF VARIABLE
                    STY VPNT+1                      ; 
                    LDX VALTYP                      ; NUMERIC OR STRING?
                    BEQ ?1                          ; NUMERIC
                    LDX #0                          ; STRING
                    STX STRNG1+1                    ; 
                    RTS                             ; 
?1                  LDX VALTYP+1                    ; NUMERIC, WHICH TYPE?
                    BPL ?2                          ; FLOATING POINT
                    LDY #0                          ; INTEGER
                    LDA (VPNT),Y                    ; 
                    TAX                             ; GET VALUE IN A,Y
                    INY                             ; 
                    LDA (VPNT),Y                    ; 
                    TAY                             ; 
                    TXA                             ; 
                    JMP GIVAYF                      ; CONVERT A,Y TO FLOATING POINT
?2                  JMP LOAD_FAC_FROM_YA
                                                    ; --------------------------------
                                                    ; --------------------------------
                                                    ; "SCRN(" FUNCTION
                                                    ; --------------------------------
SCREEN              JSR CHRGET
                    JSR PLOTFNS                     ; GET COLUMN AND ROW
                    TXA                             ; ROW
                    LDY FIRST                       ; COLUMN
                    JSR MON_SCRN                    ; GET 4-BIT COLOR THERE
                    TAY                             ; 
                    JSR SNGFLT                      ; CONVERT (Y) TO REAL IN FAC
                    JMP CHKCLS                      ; REQUIRE ")"
                                                    ; --------------------------------
                                                    ; PROCESS UNARY OPERATORS (FUNCTIONS)
                                                    ; --------------------------------
UNARY               CMP #TOKEN_SCRN                 ; NOT UNARY, DO SPECIAL
                    BEQ SCREEN
                    ASL                             ; DOUBLE TOKEN TO GET INDEX
                    PHA
                    TAX
                    JSR CHRGET
                    CPX #<(TOKEN_LEFTSTR*2-1)       ; LEFT$, RIGHT$, AND MID$
                    BCC ?1                          ; NOT ONE OF THE STRING FUNCTIONS
                    JSR CHKOPN                      ; STRING FUNCTION, NEED "("
                    JSR FRMEVL                      ; EVALUATE EXPRESSION FOR STRING
                    JSR CHKCOM                      ; REQUIRE A COMMA
                    JSR CHKSTR                      ; MAKE SURE EXPRESSION IS A STRING
                    PLA                             ; 
                    TAX                             ; RETRIEVE ROUTINE POINTER
                    LDA VPNT+1                      ; STACK ADDRESS OF STRING
                    PHA                             ; 
                    LDA VPNT                        ; 
                    PHA                             ; 
                    TXA                             ; 
                    PHA                             ; STACK DOUBLED TOKEN
                    JSR GETBYT                      ; CONVERT NEXT EXPRESSION TO BYTE IN X-REG
                    PLA                             ; GET DOUBLED TOKEN OFF STACK
                    TAY                             ; USE AS INDEX TO BRANCH
                    TXA                             ; VALUE OF SECOND PARAMETER
                    PHA                             ; PUSH 2ND PARAM
                    JMP ?2                          ; JOIN UNARY FUNCTIONS
?1                  JSR PARCHK                      ; REQUIRE "(EXPRESSION)"
                    PLA
                    TAY                             ; INDEX INTO FUNCTION ADDRESS TABLE
?2                  LDA UNFNC-TOKEN_SGN-TOKEN_SGN+$100,Y
                    STA JMPADRS+1                   ; PREPARE TO JSR TO ADDRESS
                    LDA UNFNC-TOKEN_SGN-TOKEN_SGN+$101,Y
                    STA JMPADRS+2
                    JSR JMPADRS                     ; DOES NOT RETURN FOR
                                                    ; CHR$, LEFT$, RIGHT$, OR MID$
                    JMP CHKNUM                      ; REQUIRE NUMERIC RESULT
                                                    ; --------------------------------
OR                  LDA ARG                         ; "OR" OPERATOR
                    ORA FAC                         ; IF RESULT NONZERO, IT IS TRUE
                    BNE TRUE                        ; 
                                                    ; --------------------------------
ANDOP               LDA ARG                         ; "AND" OPERATOR
                    BEQ FALSE                       ; IF EITHER IS ZERO, RESULT IS FALSE
                    LDA FAC                         ; 
                    BNE TRUE                        ; 
                                                    ; --------------------------------
FALSE               LDY #0                          ; RETURN FAC=0
                    ASM_DATA($2C)                       ; TRICK
                                                    ; --------------------------------
TRUE                LDY #1                          ; RETURN FAC=1
                    JMP SNGFLT                      ; 
                                                    ; --------------------------------
                                                    ; PERFORM RELATIONAL OPERATIONS
                                                    ; --------------------------------
RELOPS              JSR CHKVAL                      ; MAKE SURE FAC IS CORRECT TYPE
                    BCS STRCMP                      ; TYPE MATCHES, BRANCH IF STRINGS
                    LDA ARG_SIGN                    ; NUMERIC COMPARISON
                    ORA #$7F                        ; RE-PACK VALUE IN ARG FOR FCOMP
                    AND ARG+1                       ; 
                    STA ARG+1                       ; 
                    LDA #<ARG                       ; 
                    LDY #>ARG                       ; 
                    JSR FCOMP                       ; RETURN A-REG = -1,0,1
                    TAX                             ; AS ARG <,=,> FAC
                    JMP NUMCMP                      ; 
                                                    ; --------------------------------
                                                    ; STRING COMPARISON
                                                    ; --------------------------------
STRCMP              LDA #0                          ; SET RESULT TYPE TO NUMERIC
                    STA VALTYP                      ; 
                    DEC CPRTYP                      ; MAKE CPRTYP 0000<=>0
                    JSR FREFAC                      ; 
                    STA FAC                         ; STRING LENGTH
                    STX FAC+1
                    STY FAC+2
                    LDA ARG+3
                    LDY ARG+4
                    JSR FRETMP
                    STX ARG+3
                    STY ARG+4
                    TAX                             ; LEN (ARG) STRING
                    SEC                             ; 
                    SBC FAC                         ; SET X TO SMALLER LEN
                    BEQ ?1                          ; 
                    LDA #1                          ; 
                    BCC ?1                          ; 
                    LDX FAC                         ; 
                    LDA #$FF                        ; 
?1                  STA FAC_SIGN                    ; FLAG WHICH SHORTER
                    LDY #$FF                        ; 
                    INX                             ; 
STRCMP_1                                            ; 
                    INY                             ; 
                    DEX                             ; 
                    BNE STRCMP_2                    ; MORE CHARS IN BOTH STRINGS
                    LDX FAC_SIGN                    ; IF = SO FAR, DECIDE BY LENGTH
                                                    ; --------------------------------
NUMCMP              BMI CMPDONE                     ; 
                    CLC                             ; 
                    BCC CMPDONE                     ; ...ALWAYS
                                                    ; --------------------------------
STRCMP_2                                            ; 
                    LDA (ARG+3),Y                   ; 
                    CMP (FAC+1),Y                   ; 
                    BEQ STRCMP_1                    ; SAME, KEEP COMPARING
                    LDX #$FF                        ; IN CASE ARG GREATER
                    BCS CMPDONE                     ; IT IS
                    LDX #1                          ; FAC GREATER
                                                    ; --------------------------------
CMPDONE                                             ; 
                    INX                             ; CONVERT FF,0,1 TO 0,1,2
                    TXA                             ; 
                    ROL                             ; AND TO 0,2,4 IF C=0, ELSE 1,2,5
                    AND CPRMASK                     ; 00000<=>
                    BEQ ?1                          ; IF NO MATCH: FALSE
                    LDA #1                          ; AT LEAST ONE MATCH: TRUE
?1                  JMP FLOAT                       ; 
                                                    ; --------------------------------
                                                    ; "PDL" FUNCTION
                                                    ; <<< NOTE: ARG<4 IS NOT CHECKED >>>
                                                    ; --------------------------------
PDL                 JSR CONINT                      ; GET # IN X
                    JSR MON_PREAD                   ; READ PADDLE
                    JMP SNGFLT                      ; FLOAT RESULT
                                                    ; --------------------------------
                                                    ; "DIM" STATEMENT
                                                    ; --------------------------------
NXDIM               JSR CHKCOM                      ; SEPARATED BY COMMAS
DIM                 TAX                             ; NON-ZERO, FLAGS PTRGET DIM CALLED
                    JSR PTRGET2                     ; ALLOCATE THE ARRAY
                    JSR CHRGOT                      ; NEXT CHAR
                    BNE NXDIM                       ; NOT END OF STATEMENT
                    RTS                             ; 
                                                    ; --------------------------------
                                                    ; PTRGET -- GENERAL VARIABLE SCAN
                                                    ; 
                                                    ; SCANS VARIABLE NAME AT TXTPTR, AND SEARCHES THE
                                                    ; VARTAB AND ARYTAB FOR THE NAME.
                                                    ; IF NOT FOUND, CREATE VARIABLE OF APPROPRIATE TYPE.
                                                    ; RETURN WITH ADDRESS IN VARPNT AND Y,A
                                                    ; 
                                                    ; ACTUAL ACTIVITY CONTROLLED SOMEWHAT BY TWO FLAGS:
                                                    ; DIMFLG -- NONZERO IF CALLED FROM "DIM"
                                                    ; ELSE = 0
                                                    ; 
                                                    ; SUBFLG -- = $00
                                                    ; = $40 IF CALLED FROM "GETARYPT"
                                                    ; = $80 IF CALLED FROM "DEF FN"
                                                    ; = $C1-DA IF CALLED FROM "FN"
                                                    ; --------------------------------
PTRGET              LDX #0                          ; 
                    JSR CHRGOT                      ; GET FIRST CHAR OF VARIABLE NAME
                                                    ; --------------------------------
PTRGET2                                             ; 
                    STX DIMFLG                      ; X IS NONZERO IF FROM DIM
                                                    ; --------------------------------
PTRGET3                                             ; 
                    STA VARNAM                      ; 
                    JSR CHRGOT                      ; 
                    JSR ISLETC                      ; IS IT A LETTER?
                    BCS NAMOK                       ; YES, OKAY SO FAR
BADNAM              JMP SYNERR                      ; NO, SYNTAX ERROR
NAMOK               LDX #0                          ; 
                    STX VALTYP                      ; 
                    STX VALTYP+1                    ; 
                    JMP PTRGET4                     ; TO BRANCH ACROSS $E000 VECTORS
                                                    ; --------------------------------
                                                    ; DOS AND MONITOR CALL BASIC AT $E000 AND $E003
                                                    ; --------------------------------
BASIC               JMP COLD_START
BASIC2              JMP RESTART
                    BRK                             ; <<< WASTED BYTE >>>
                                                    ; --------------------------------
PTRGET4
                    JSR CHRGET                      ; SECOND CHAR OF VARIABLE NAME
                    BCC ?1                          ; NUMERIC
                    JSR ISLETC                      ; LETTER?
                    BCC ?3                          ; NO, END OF NAME
?1                  TAX                             ; SAVE SECOND CHAR OF NAME IN X
?2                  JSR CHRGET                      ; SCAN TO END OF VARIABLE NAME
                    BCC ?2                          ; NUMERIC
                    JSR ISLETC                      ; 
                    BCS ?2                          ; ALPHA
?3                  CMP #HICHAR(`$')                        ; STRING?
                    BNE ?4                          ; NO
                    LDA #$FF                        ; 
                    STA VALTYP                      ; 
                    BNE ?5                          ; ...ALWAYS
?4                  CMP #HICHAR(`%')                        ; INTEGER?
                    BNE ?6                          ; NO
                    LDA SUBFLG                      ; YES; INTEGER VARIABLE ALLOWED?
                    BMI BADNAM                      ; NO, SYNTAX ERROR
                    LDA #$80                        ; YES
                    STA VALTYP+1                    ; FLAG INTEGER MODE
                    ORA VARNAM                      ; 
                    STA VARNAM                      ; SET SIGN BIT ON VARNAME
?5                  TXA                             ; SECOND CHAR OF NAME
                    ORA #$80                        ; SET SIGN
                    TAX                             ; 
                    JSR CHRGET                      ; GET TERMINATING CHAR
?6                  STX VARNAM+1                    ; STORE SECOND CHAR OF NAME
                    SEC                             ; 
                    ORA SUBFLG                      ; $00 OR $40 IF SUBSCRIPTS OK, ELSE $80
                    SBC #HICHAR(`(')                        ; IF SUBFLG=$00 AND CHAR="("...
                    BNE ?8                          ; NOPE
?7                  JMP ARRAY                       ; YES
?8                  BIT SUBFLG                      ; CHECK TOP TWO BITS OF SUBFLG
                    BMI ?9                          ; $80
                    BVS ?7                          ; $40, CALLED FROM GETARYPT
?9                  LDA #0                          ; CLEAR SUBFLG
                    STA SUBFLG                      ; 
                    LDA VARTAB                      ; START LOWTR AT SIMPLE VARIABLE TABLE
                    LDX VARTAB+1                    ; 
                    LDY #0                          ; 
?10                 STX LOWTR+1                     ; 
?11                 STA LOWTR                       ; 
                    CPX ARYTAB+1                    ; END OF SIMPLE VARIABLES?
                    BNE ?12                         ; NO, GO ON
                    CMP ARYTAB                      ; YES; END OF ARRAYS?
                    BEQ NAME_NOT_FOUND              ; YES, MAKE ONE
?12                 LDA VARNAM                      ; SAME FIRST LETTER?
                    CMP (LOWTR),Y                   ; 
                    BNE ?13                         ; NOT SAME FIRST LETTER
                    LDA VARNAM+1                    ; SAME SECOND LETTER?
                    INY
                    CMP (LOWTR),Y
                    BEQ SET_VARPNT_AND_YA           ; YES, SAME VARIABLE NAME
                    DEY                             ; NO, BUMP TO NEXT NAME
?13                 CLC
                    LDA LOWTR
                    ADC #7
                    BCC ?11
                    INX
                    BNE ?10                         ; ...ALWAYS
                                                    ; --------------------------------
                                                    ; CHECK IF (A) IS ASCII LETTER A-Z
                                                    ; 
                                                    ; RETURN CARRY = 1 IF A-Z
                                                    ; = 0 IF NOT
                                                    ; 
                                                    ; <<<NOTE FASTER AND SHORTER CODE:    >>>
                                                    ; <<<    CMP #HICHAR(`Z')+1  COMPARE HI END
                                                    ; <<<    BCS ?1      ABOVE A-Z
                                                    ; <<<    CMP #HICHAR(`A')    COMPARE LO END
                                                    ; <<<    RTS         C=0 IF LO, C=1 IF A-Z
                                                    ; <<<?1  CLC         C=0 IF HI
                                                    ; <<<    RTS
                                                    ; --------------------------------
ISLETC              CMP #HICHAR(`A')                        ; COMPARE LO END
                    BCC ?1                          ; C=0 IF LOW
                    SBC #HICHAR(`Z')+1                      ; PREPARE HI END TEST
                    SEC                             ; TEST HI END, RESTORING (A)
                    SBC #-1-'Z'                     ; C=0 IF LO, C=1 IF A-Z
?1                  RTS
                                                    ; --------------------------------
                                                    ; VARIABLE NOT FOUND, SO MAKE ONE
                                                    ; --------------------------------
NAME_NOT_FOUND
                    PLA                             ; LOOK AT RETURN ADDRESS ON STACK TO
                    PHA                             ; SEE IF CALLED FROM FRM.VARIABLE
                    CMP #<FRM_VARIABLE_CALL
                    BNE MAKE_NEW_VARIABLE           ; NO
                    TSX
                    LDA STACK+2,X
                    CMP #>FRM_VARIABLE_CALL
                    BNE MAKE_NEW_VARIABLE           ; NO
                    LDA #<C_ZERO                    ; YES, CALLED FROM FRM.VARIABLE
                    LDY #>C_ZERO                    ; POINT TO A CONSTANT ZERO
                    RTS                             ; NEW VARIABLE USED IN EXPRESSION = 0
                                                    ; --------------------------------
C_ZERO              ASM_DATA(00,00)                     ; INTEGER OR REAL ZERO, OR NULL STRING
                                                    ; --------------------------------
                                                    ; MAKE A NEW SIMPLE VARIABLE
                                                    ; 
                                                    ; MOVE ARRAYS UP 7 BYTES TO MAKE ROOM FOR NEW VARIABLE
                                                    ; ENTER 7-BYTE VARIABLE DATA IN THE HOLE
                                                    ; --------------------------------
MAKE_NEW_VARIABLE
                    LDA ARYTAB                      ; SET UP CALL TO BLTU TO
                    LDY ARYTAB+1                    ; TO MOVE FROM ARYTAB THRU STREND-1
                    STA LOWTR                       ; 7 BYTES HIGHER
                    STY LOWTR+1                     ; 
                    LDA STREND                      ; 
                    LDY STREND+1                    ; 
                    STA HIGHTR                      ; 
                    STY HIGHTR+1                    ; 
                    CLC                             ; 
                    ADC #7                          ; 
                    BCC ?1                          ; 
                    INY                             ; 
?1                  STA ARYPNT                      ; 
                    STY ARYPNT+1                    ; 
                    JSR BLTU                        ; MOVE ARRAY BLOCK UP
                    LDA ARYPNT                      ; STORE NEW START OF ARRAYS
                    LDY ARYPNT+1                    ; 
                    INY                             ; 
                    STA ARYTAB                      ; 
                    STY ARYTAB+1                    ; 
                    LDY #0                          ; 
                    LDA VARNAM                      ; FIRST CHAR OF NAME
                    STA (LOWTR),Y                   ; 
                    INY                             ; 
                    LDA VARNAM+1                    ; SECOND CHAR OF NAME
                    STA (LOWTR),Y                   ; 
                    LDA #0                          ; SET FIVE-BYTE VALUE TO 0
                    INY                             ; 
                    STA (LOWTR),Y                   ; 
                    INY                             ; 
                    STA (LOWTR),Y                   ; 
                    INY                             ; 
                    STA (LOWTR),Y                   ; 
                    INY                             ; 
                    STA (LOWTR),Y                   ; 
                    INY                             ; 
                    STA (LOWTR),Y                   ; 
                                                    ; --------------------------------
                                                    ; PUT ADDRESS OF VALUE OF VARIABLE IN VARPNT AND Y,A
                                                    ; --------------------------------
SET_VARPNT_AND_YA                                   ; 
                    LDA LOWTR                       ; LOWTR POINTS AT NAME OF VARIABLE,
                    CLC                             ; SO ADD 2 TO GET TO VALUE
                    ADC #2                          ; 
                    LDY LOWTR+1                     ; 
                    BCC ?1                          ; 
                    INY                             ; 
?1                  STA VARPNT                      ; ADDRESS IN VARPNT AND Y,A
                    STY VARPNT+1                    ; 
                    RTS                             ; 
                                                    ; --------------------------------
                                                    ; COMPUTE ADDRESS OF FIRST VALUE IN ARRAY
                                                    ; ARYPNT = (LOWTR) + #DIMS*2 + 5
                                                    ; --------------------------------
GETARY              LDA NUMDIM                      ; GET # OF DIMENSIONS
                                                    ; --------------------------------
GETARY2                                             ; 
                    ASL                             ; #DIMS*2 (SIZE OF EACH DIM IN 2 BYTES)
                    ADC #5                          ; + 5 (2 FOR NAME, 2 FOR OFFSET TO NEXT
                                                    ; ARRAY, AND 1 FOR #DIMS
                    ADC LOWTR                       ; ADDRESS OF TH IS ARRAY IN ARYTAB
                    LDY LOWTR+1                     ; 
                    BCC ?1                          ; 
                    INY                             ; 
?1                  STA ARYPNT                      ; ADDRESS OF FIRST VALUE IN ARRAY
                    STY ARYPNT+1                    ; 
                    RTS                             ; 
                                                    ; --------------------------------

NEG32768            ASM_DATA($90,$80,$00,$00)           ; -32768.00049 IN FLOATING POINT
                                                    ; <<<  MEANT TO BE -32768, WHICH WOULD BE 9080000000 >>>
                                                    ; <<<  1 BYTE SHORT, SO PICKS UP $20 FROM NEXT INSTRUCTION
                                                    ; --------------------------------
                                                    ; EVALUATE NUMERIC FORMULA AT TXTPTR
                                                    ; CONVERTING RESULT TO INTEGER 0 <= X <= 32767
                                                    ; IN FAC+3,4
                                                    ; --------------------------------
MAKINT              JSR CHRGET
                    JSR FRMNUM
                                                    ; --------------------------------
                                                    ; CONVERT FAC TO INTEGER
                                                    ; MUST BE POSITIVE AND LESS THAN 32768
                                                    ; --------------------------------
MKINT               LDA FAC_SIGN                    ; ERROR IF -
                    BMI MI1
                                                    ; --------------------------------
                                                    ; CONVERT FAC TO INTEGER
                                                    ; MUST BE -32767 <= FAC <= 32767
                                                    ; --------------------------------
AYINT               LDA FAC                         ; EXPONENT OF VALUE IN FAC
                    CMP #$90                        ; ABS(VALUE) < 32768?
                    BCC MI2                         ; YES, OK FOR INTEGER
                    LDA #<NEG32768                  ; NO; NEXT FEW LINES ARE SUPPOSED TO
                    LDY #>NEG32768                  ; ALLOW -32768 ($8000), BUT DO NOT!
                    JSR FCOMP                       ; BECAUSE COMPARED TO -32768.00049
                                                    ; <<< BUG:  A=-32768.00049:A%=A IS ACCEPTED >>>
                                                    ; <<<       BUT PRINT A,A% SHOWS THAT       >>>
                                                    ; <<<       A=-32768.0005 (OK), A%=32767    >>>
                                                    ; <<<       WRONG! WRONG! WRONG!            >>>
                                                    ; --------------------------------
MI1                 BNE IQERR                       ; ILLEGAL QUANTITY
MI2                 JMP QINT                        ; CONVERT TO INTEGER
                                                    ; --------------------------------
                                                    ; LOCATE ARRAY ELEMENT OR CREATE AN ARRAY
                                                    ; --------------------------------
ARRAY               LDA SUBFLG                      ; SUBSCRIPTS GIVEN?
                    BNE ?2                          ; NO
                                                    ; --------------------------------
                                                    ; PARSE THE SUBSCRIPT LIST
                                                    ; --------------------------------
                    LDA DIMFLG                      ; YES
                    ORA VALTYP+1                    ; SET HIGH BIT IF %
                    PHA                             ; SAVE VALTYP AND DIMFLG ON STACK
                    LDA VALTYP                      ; 
                    PHA                             ; 
                    LDY #0                          ; COUNT # DIMENSIONS IN Y-REG
?1                  TYA                             ; SAVE #DIMS ON STACK
                    PHA                             ; 
                    LDA VARNAM+1                    ; SAVE VARIABLE NAME ON STACK
                    PHA                             ; 
                    LDA VARNAM                      ; 
                    PHA                             ; 
                    JSR MAKINT                      ; EVALUATE SUBSCRIPT AS INTEGER
                    PLA                             ; RESTORE VARIABLE NAME
                    STA VARNAM                      ; 
                    PLA                             ; 
                    STA VARNAM+1                    ; 
                    PLA                             ; RESTORE # DIMS TO Y-REG
                    TAY                             ; 
                    TSX                             ; COPY VALTYP AND DIMFLG ON STACK
                    LDA STACK+2,X                   ; TO LEAVE ROOM FOR THE SUBSCRIPT
                    PHA                             ; 
                    LDA STACK+1,X                   ; 
                    PHA                             ; 
                    LDA FAC+3                       ; GET SUBSCRIPT VALUE AND PLACE IN THE
                    STA STACK+2,X                   ; STACK WHERE VALTYP & DIMFLG WERE
                    LDA FAC+4                       ; 
                    STA STACK+1,X                   ; 
                    INY                             ; COUNT THE SUBSCRIPT
                    JSR CHRGOT                      ; NEXT CHAR
                    CMP #HICHAR(`,')                        ; 
                    BEQ ?1                          ; COMMA, PARSE ANOTHER SUBSCRIPT
                    STY NUMDIM                      ; NO MORE SUBSCRIPTS, SAVE #
                    JSR CHKCLS                      ; NOW NEED ")"
                    PLA                             ; RESTORE VALTYPE AND DIMFLG
                    STA VALTYP                      ; 
                    PLA                             ; 
                    STA VALTYP+1                    ; 
                    AND #$7F                        ; ISOLATE DIMFLG
                    STA DIMFLG                      ; 
                                                    ; --------------------------------
                                                    ; SEARCH ARRAY TABLE FOR THIS ARRAY NAME
                                                    ; --------------------------------
?2                  LDX ARYTAB                      ; (A,X) = START OF ARRAY TABLE
                    LDA ARYTAB+1                    ; 
?3                  STX LOWTR                       ; USE LOWTR FOR RUNNING POINTER
                    STA LOWTR+1                     ; 
                    CMP STREND+1                    ; DID WE REACH THE END OF ARRAYS YET?
                    BNE ?4                          ; NO, KEEP SEARCHING
                    CPX STREND                      ; 
                    BEQ MAKE_NEW_ARRAY              ; YES, THIS IS A NEW ARRAY NAME
?4                  LDY #0                          ; POINT AT 1ST CHAR OF ARRAY NAME
                    LDA (LOWTR),Y                   ; GET 1ST CHAR OF NAME
                    INY                             ; POINT AT 2ND CHAR
                    CMP VARNAM                      ; 1ST CHAR SAME?
                    BNE ?5                          ; NO, MOVE TO NEXT ARRAY
                    LDA VARNAM+1                    ; YES, TRY 2ND CHAR
                    CMP (LOWTR),Y                   ; SAME?
                    BEQ USE_OLD_ARRAY               ; YES, ARRAY FOUND
?5                  INY                             ; POINT AT OFFSET TO NEXT ARRAY
                    LDA (LOWTR),Y                   ; ADD OFFSET TO RUNNING POINTER
                    CLC
                    ADC LOWTR
                    TAX
                    INY
                    LDA (LOWTR),Y
                    ADC LOWTR+1
                    BCC ?3                          ; ...ALWAYS
                                                    ; --------------------------------
                                                    ; ERROR:  BAD SUBSCRIPTS
                                                    ; --------------------------------
SUBERR              LDX #ERR_BADSUBS
                    ASM_DATA($2C)                       ; TRICK TO SKIP NEXT LINE
                                                    ; --------------------------------
                                                    ; ERROR:  ILLEGAL QUANTITY
                                                    ; --------------------------------
IQERR               LDX #ERR_ILLQTY
JER                 JMP ERROR
                                                    ; --------------------------------
                                                    ; FOUND THE ARRAY
                                                    ; --------------------------------
USE_OLD_ARRAY
                    LDX #ERR_REDIMD                 ; SET UP FOR REDIM'D ARRAY ERROR
                    LDA DIMFLG                      ; CALLED FROM "DIM" STATEMENT?
                    BNE JER                         ; YES, ERROR
                    LDA SUBFLG                      ; NO, CHECK IF ANY SUBSCRIPTS
                    BEQ ?1                          ; YES, NEED TO CHECK THE NUMBER
                    SEC                             ; NO, SIGNAL ARRAY FOUND
                    RTS
                                                    ; --------------------------------
?1                  JSR GETARY                      ; SET (ARYPNT) = ADDR OF FIRST ELEMENT
                    LDA NUMDIM                      ; COMPARE NUMBER OF DIMENSIONS
                    LDY #4
                    CMP (LOWTR),Y
                    BNE SUBERR                      ; NOT SAME, SUBSCRIPT ERROR
                    JMP FIND_ARRAY_ELEMENT
                                                    ; --------------------------------
                                                    ; --------------------------------
                                                    ; CREATE A NEW ARRAY, UNLESS CALLED FROM GETARYPT
                                                    ; --------------------------------
MAKE_NEW_ARRAY
                    LDA SUBFLG                      ; CALLED FROM GETARYPT?
                    BEQ ?1                          ; NO
                    LDX #ERR_NODATA                 ; YES, GIVE "OUT OF DATA" ERROR
                    JMP ERROR
?1                  JSR GETARY                      ; PUT ADDR OF 1ST ELEMENT IN ARYPNT
                    JSR REASON                      ; MAKE SURE ENOUGH MEMORY LEFT
                                                    ; --------------------------------
                                                    ; <<< NEXT 3 LINES COULD BE WRITTEN:   >>>
                                                    ; LDY #0
                                                    ; STY STRNG2+1
                                                    ; --------------------------------
                    LDA #0                          ; POINT Y-REG AT VARIABLE NAME SLOT
                    TAY                             ; 
                    STA STRNG2+1                    ; START SIZE COMPUTATION
                    LDX #5                          ; ASSUME 5-BYTES PER ELEMENT
                    LDA VARNAM                      ; STUFF VARIABLE NAME IN ARRAY
                    STA (LOWTR),Y                   ; 
                    BPL ?2                          ; NOT INTEGER ARRAY
                    DEX                             ; INTEGER ARRAY, DECR. SIZE TO 4-BYTES
?2                  INY                             ; POINT Y-REG AT NEXT CHAR OF NAME
                    LDA VARNAM+1                    ; REST OF ARRAY NAME
                    STA (LOWTR),Y                   ; 
                    BPL ?3                          ; REAL ARRAY, STICK WITH SIZE = 5 BYTES
                    DEX                             ; INTEGER OR STRING ARRAY, ADJUST SIZE
                    DEX                             ; TO INTEGER=3, STRING=2 BYTES
?3                  STX STRNG2                      ; STORE LOW-BYTE OF ARRAY ELEMENT SIZE
                    LDA NUMDIM                      ; STORE NUMBER OF DIMENSIONS
                    INY                             ; IN 5TH BYTE OF ARRAY
                    INY                             ; 
                    INY                             ; 
                    STA (LOWTR),Y                   ; 
?4                  LDX #11                         ; DEFAULT DIMENSION = 11 ELEMENTS
                    LDA #0                          ; FOR HI-BYTE OF DIMENSION IF DEFAULT
                    BIT DIMFLG                      ; DIMENSIONED ARRAY?
                    BVC ?5                          ; NO, USE DEFAULT VALUE
                    PLA                             ; GET SPECIFIED DIM IN A,X
                    CLC                             ; # ELEMENTS IS 1 LARGER THAN
                    ADC #1                          ; DIMENSION VALUE
                    TAX                             ; 
                    PLA                             ; 
                    ADC #0                          ; 
?5                  INY                             ; ADD THIS DIMENSION TO ARRAY DESCRIPTOR
                    STA (LOWTR),Y
                    INY
                    TXA
                    STA (LOWTR),Y
                    JSR MULTIPLY_SUBSCRIPT          ; MULTIPLY THIS
                                                    ; DIMENSION BY RUNNING SIZE
                                                    ; ((LOWTR)) * (STRNG2) --> A,X
                    STX STRNG2                      ; STORE RUNNING SIZE IN STRNG2
                    STA STRNG2+1                    ; 
                    LDY INDEX                       ; RETRIEVE Y SAVED BY MULTIPLY.SUBSCRIPT
                    DEC NUMDIM                      ; COUNT DOWN # DIMS
                    BNE ?4                          ; LOOP TILL DONE
                                                    ; --------------------------------
                                                    ; NOW A,X HAS TOTAL # BYTES OF ARRAY ELEMENTS
                                                    ; --------------------------------
                    ADC ARYPNT+1                    ; COMPUTE ADDRESS OF END OF THIS ARRAY
                    BCS GME                         ; ...TOO LARGE, ERROR
                    STA ARYPNT+1                    ; 
                    TAY                             ; 
                    TXA                             ; 
                    ADC ARYPNT                      ; 
                    BCC ?6                          ; 
                    INY                             ; 
                    BEQ GME                         ; ...TOO LARGE, ERROR
?6                  JSR REASON                      ; MAKE SURE THERE IS ROOM UP TO Y,A
                    STA STREND                      ; THERE IS ROOM SO SAVE NEW END OF TABLE
                    STY STREND+1                    ; AND ZERO THE ARRAY
                    LDA #0                          ; 
                    INC STRNG2+1                    ; PREPARE FOR FAST ZEROING LOOP
                    LDY STRNG2                      ; # BYTES MOD 256
                    BEQ ?8                          ; FULL PAGE
?7                  DEY                             ; CLEAR PAGE FULL
                    STA (ARYPNT),Y
                    BNE ?7
?8                  DEC ARYPNT+1                    ; POINT TO NEXT PAGE
                    DEC STRNG2+1                    ; COUNT THE PAGES
                    BNE ?7                          ; STILL MORE TO CLEAR
                    INC ARYPNT+1                    ; RECOVER LAST DEC, POINT AT 1ST ELEMENT
                    SEC                             ; 
                    LDA STREND                      ; COMPUTE OFFSET TO END OF ARRAYS
                    SBC LOWTR                       ; AND STORE IN ARRAY DESCRIPTOR
                    LDY #2                          ; 
                    STA (LOWTR),Y                   ; 
                    LDA STREND+1                    ; 
                    INY                             ; 
                    SBC LOWTR+1                     ; 
                    STA (LOWTR),Y                   ; 
                    LDA DIMFLG                      ; WAS THIS CALLED FROM "DIM" STATEMENT?
                    BNE RTS_9                       ; YES, WE ARE FINISHED
                    INY                             ; NO, NOW NEED TO FIND THE ELEMENT
                                                    ; --------------------------------
                                                    ; FIND SPECIFIED ARRAY ELEMENT
                                                    ; 
                                                    ; (LOWTR),Y POINTS AT # OF DIMS IN ARRAY DESCRIPTOR
                                                    ; THE SUBSCRIPTS ARE ALL ON THE STACK AS INTEGERS
                                                    ; --------------------------------
FIND_ARRAY_ELEMENT
                    LDA (LOWTR),Y                   ; GET # OF DIMENSIONS
                    STA NUMDIM                      ; 
                    LDA #0                          ; ZERO SUBSCRIPT ACCUMULATOR
                    STA STRNG2                      ; 
FAE_1               STA STRNG2+1                    ; 
                    INY                             ; 
                    PLA                             ; PULL NEXT SUBSCRIPT FROM STACK
                    TAX                             ; SAVE IN FAC+3,4
                    STA FAC+3                       ; AND COMPARE WITH DIMENSIONED SIZE
                    PLA                             ; 
                    STA FAC+4                       ; 
                    CMP (LOWTR),Y                   ; 
                    BCC FAE_2                       ; SUBSCRIPT NOT TOO LARGE
                    BNE GSE                         ; SUBSCRIPT IS TOO LARGE
                    INY                             ; CHECK LOW-BYTE OF SUBSCRIPT
                    TXA                             ; 
                    CMP (LOWTR),Y                   ; 
                    BCC FAE_3                       ; NOT TOO LARGE
                                                    ; --------------------------------
GSE                 JMP SUBERR                      ; BAD SUBSCRIPTS ERROR
GME                 JMP MEMERR                      ; MEM FULL ERROR
                                                    ; --------------------------------
FAE_2               INY                             ; BUMP POINTER INTO DESCRIPTOR
FAE_3               LDA STRNG2+1                    ; BYPASS MULTIPLICATION IF VALUE SO
                    ORA STRNG2                      ; FAR = 0
                    CLC                             ; 
                    BEQ ?1                          ; IT IS ZERO SO FAR
                    JSR MULTIPLY_SUBSCRIPT          ; NOT ZERO, SO MULTIPLY
                    TXA                             ; ADD CURRENT SUBSCRIPT
                    ADC FAC+3                       ; 
                    TAX                             ; 
                    TYA                             ; 
                    LDY INDEX                       ; RETRIEVE Y SAVED BY MULTIPLY.SUBSCRIPT
?1                  ADC FAC+4                       ; FINISH ADDING CURRENT SUBSCRIPT
                    STX STRNG2                      ; STORE ACCUMULATED OFFSET
                    DEC NUMDIM                      ; LAST SUBSCRIPT YET?
                    BNE FAE_1                       ; NO, LOOP TILL DONE
                    STA STRNG2+1                    ; YES, NOW MULTIPLY BE ELEMENT SIZE
                    LDX #5                          ; START WITH SIZE = 5
                    LDA VARNAM                      ; DETERMINE VARIABLE TYPE
                    BPL ?2                          ; NOT INTEGER
                    DEX                             ; INTEGER, BACK DOWN SIZE TO 4 BYTES
?2                  LDA VARNAM+1                    ; DISCRIMINATE BETWEEN REAL AND STR
                    BPL ?3                          ; IT IS REAL
                    DEX                             ; SIZE = 3 IF STRING, =2 IF INTEGER
                    DEX                             ; 
?3                  STX RESULT+2                    ; SET UP MULTIPLIER
                    LDA #0                          ; HI-BYTE OF MULTIPLIER
                    JSR MULTIPLY_SUBS_1             ; (STRNG2) BY ELEMENT SIZE
                    TXA                             ; ADD ACCUMULATED OFFSET
                    ADC ARYPNT                      ; TO ADDRESS OF 1ST ELEMENT
                    STA VARPNT                      ; TO GET ADDRESS OF SPECIFIED ELEMENT
                    TYA                             ; 
                    ADC ARYPNT+1                    ; 
                    STA VARPNT+1                    ; 
                    TAY                             ; RETURN WITH ADDR IN VARPNT
                    LDA VARPNT                      ; AND IN Y,A
RTS_9               RTS                             ; 
                                                    ; --------------------------------
                                                    ; MULTIPLY (STRNG2) BY ((LOWTR),Y)
                                                    ; LEAVING PRODUCT IN A,X.  (HI-BYTE ALSO IN Y.)
                                                    ; USED ONLY BY ARRAY SUBSCRIPT ROUTINES
                                                    ; --------------------------------
MULTIPLY_SUBSCRIPT
                    STY INDEX                       ; SAVE Y-REG
                    LDA (LOWTR),Y                   ; GET MULTIPLIER
                    STA RESULT+2                    ; SAVE IN RESULT+2,3
                    DEY                             ; 
                    LDA (LOWTR),Y                   ; 
                                                    ; --------------------------------
MULTIPLY_SUBS_1                                     ; 
                    STA RESULT+3                    ; LOW BYTE OF MULTIPLIER
                    LDA #16                         ; MULTIPLY 16 BITS
                    STA INDX                        ; 
                    LDX #0                          ; PRODUCT = 0 INITIALLY
                    LDY #0                          ; 
?1                  TXA                             ; DOUBLE PRODUCT
                    ASL                             ; LOW BYTE
                    TAX                             ; 
                    TYA                             ; HIGH BYTE
                    ROL                             ; IF TOO LARGE, SET CARRY
                    TAY                             ; 
                    BCS GME                         ; TOO LARGE, "MEM FULL ERROR"
                    ASL STRNG2                      ; NEXT BIT OF MUTLPLICAND
                    ROL STRNG2+1                    ; INTO CARRY
                    BCC ?2                          ; BIT=0, DON'T NEED TO ADD
                    CLC                             ; BIT=1, ADD INTO PARTIAL PRODUCT
                    TXA                             ; 
                    ADC RESULT+2                    ; 
                    TAX                             ; 
                    TYA                             ; 
                    ADC RESULT+3                    ; 
                    TAY                             ; 
                    BCS GME                         ; TOO LARGE, "MEM FULL ERROR"
?2                  DEC INDX                        ; 16-BITS YET?
                    BNE ?1                          ; NO, KEEP SHUFFLING
                    RTS                             ; YES, PRODUCT IN Y,X AND A,X
                                                    ; --------------------------------
                                                    ; "FRE" FUNCTION
                                                    ; 
                                                    ; COLLECTS GARBAGE AND RETURNS # BYTES OF MEMORY LEFT
                                                    ; --------------------------------
FRE                 LDA VALTYP                      ; LOOK AT VALUE OF ARGUMENT
                    BEQ ?1                          ; =0 MEANS REAL, =$FF MEANS STRING
                    JSR FREFAC                      ; STRING, SO SET IT FREE IS TEMP
?1                  JSR GARBAG                      ; COLLECT ALL THE GARBAGE IN SIGHT
                    SEC                             ; COMPUTE SPACE BETWEEN ARRAYS AND
                    LDA FRETOP                      ; STRING TEMP AREA
                    SBC STREND                      ; 
                    TAY                             ; 
                    LDA FRETOP+1                    ; 
                    SBC STREND+1                    ; FREE SPACE IN Y,A
                                                    ; FALL INTO GIVAYF TO FLOAT THE VALUE
                                                    ; NOTE THAT VALUES OVER 32767 WILL RETURN AS NEGATIVE
                                                    ; --------------------------------
                                                    ; FLOAT THE SIGNED INTEGER IN A,Y
                                                    ; --------------------------------
GIVAYF              LDX #0                          ; MARK FAC VALUE TYPE REAL
                    STX VALTYP                      ; 
                    STA FAC+1                       ; SAVE VALUE FROM A,Y IN MANTISSA
                    STY FAC+2                       ; 
                    LDX #$90                        ; SET EXPONENT TO 2^16
                    JMP FLOAT_1                     ; CONVERT TO SIGNED FP
                                                    ; --------------------------------
                                                    ; "POS" FUNCTION
                                                    ; 
                                                    ; RETURNS CURRENT LINE POSITION FROM MON.CH
                                                    ; --------------------------------
POS                 LDY MON_CH                      ; GET A,Y = (MON.CH, GO TO GIVAYF
                                                    ; --------------------------------
                                                    ; FLOAT (Y) INTO FAC, GIVING VALUE 0-255
                                                    ; --------------------------------
SNGFLT              LDA #0                          ; MSB = 0
                    SEC                             ; <<< NO PURPOSE WHATSOEVER >>>
                    BEQ GIVAYF                      ; ...ALWAYS
                                                    ; --------------------------------
                                                    ; CHECK FOR DIRECT OR RUNNING MODE
                                                    ; GIVING ERROR IF DIRECT MODE
                                                    ; --------------------------------
ERRDIR              LDX CURLIN+1                    ; =$FF IF DIRECT MODE
                    INX                             ; MAKES $FF INTO ZERO
                    BNE RTS_9                       ; RETURN IF RUNNING MODE
                    LDX #ERR_ILLDIR                 ; DIRECT MODE, GIVE ERROR
                    ASM_DATA($2C)                       ; TRICK TO SKIP NEXT 2 BYTES
                                                    ; --------------------------------
UNDFNC              LDX #ERR_UNDEFFUNC              ; UNDEFINDED FUNCTION ERROR
                    JMP ERROR
                                                    ; --------------------------------
                                                    ; "DEF" STATEMENT
                                                    ; --------------------------------
DEF                 JSR FNC_                        ; PARSE "FN", FUNCTION NAME
                    JSR ERRDIR                      ; ERROR IF IN DIRECT MODE
                    JSR CHKOPN                      ; NEED "("
                    LDA #$80                        ; FLAG PTRGET THAT CALLED FROM "DEF FN"
                    STA SUBFLG                      ; ALLOW ONLY SIMPLE FP VARIABLE FOR ARG
                    JSR PTRGET                      ; GET PNTR TO ARGUMENT
                    JSR CHKNUM                      ; MUST BE NUMERIC
                    JSR CHKCLS                      ; MUST HAVE ")" NOW
                    LDA #TOKENEQUUAL                ; NOW NEED "="
                    JSR SYNCHR                      ; OR ELSE SYNTAX ERROR
                    PHA                             ; SAVE CHAR AFTER "="
                    LDA VARPNT+1                    ; SAVE PNTR TO ARGUMENT
                    PHA
                    LDA VARPNT
                    PHA
                    LDA TXTPTR+1                    ; SAVE TXTPTR
                    PHA
                    LDA TXTPTR
                    PHA
                    JSR DATA                        ; SCAN TO NEXT STATEMENT
                    JMP FNCDATA                     ; STORE ABOVE 5 BYTES IN "VALUE"
                                                    ; --------------------------------
                                                    ; COMMON ROUTINE FOR "DEFFN" AND "FN", TO
                                                    ; PARSE "FN" AND THE FUNCTION NAME
                                                    ; --------------------------------
FNC_                LDA #TOKEN_FN                   ; MUST NOW SEE "FN" TOKEN
                    JSR SYNCHR                      ; OR ELSE SYNTAX ERROR
                    ORA #$80                        ; SET SIGN BIT ON 1ST CHAR OF NAME,
                    STA SUBFLG                      ; MAKING $C0 < SUBFLG < $DB
                    JSR PTRGET3                     ; WHICH TELLS PTRGET WHO CALLED
                    STA FNCNAM                      ; FOUND VALID FUNCTION NAME, SO
                    STY FNCNAM+1                    ; SAVE ADDRESS
                    JMP CHKNUM                      ; MUST BE NUMERIC
                                                    ; --------------------------------
                                                    ; "FN" FUNCTION CALL
                                                    ; --------------------------------
FUNCT               JSR FNC_                        ; PARSE "FN", FUNCTION NAME
                    LDA FNCNAM+1                    ; STACK FUNCTION ADDRESS
                    PHA                             ; IN CASE OF A NESTED FN CALL
                    LDA FNCNAM                      ; 
                    PHA                             ; 
                    JSR PARCHK                      ; MUST NOW HAVE "(EXPRESSION)"
                    JSR CHKNUM                      ; MUST BE NUMERIC EXPRESSION
                    PLA                             ; GET FUNCTION ADDRESS BACK
                    STA FNCNAM                      ; 
                    PLA                             ; 
                    STA FNCNAM+1                    ; 
                    LDY #2                          ; POINT AT ADD OF ARGUMENT VARIABLE
                    LDA (FNCNAM),Y
                    STA VARPNT
                    TAX
                    INY
                    LDA (FNCNAM),Y
                    BEQ UNDFNC                      ; UNDEFINED FUNCTION
                    STA VARPNT+1
                    INY                             ; Y=4 NOW
?1                  LDA (VARPNT),Y                  ; SAVE OLD VALUE OF ARGUMENT VARIABLE
                    PHA                             ; ON STACK, IN CASE ALSO USED AS
                    DEY                             ; A NORMAL VARIABLE!
                    BPL ?1
                    LDY VARPNT+1                    ; (Y,X)= ADDRESS, STORE FAC IN VARIABLE
                    JSR STORE_FACDB_YX_ROUNDED
                    LDA TXTPTR+1                    ; REMEMBER TXTPTR AFTER FN CALL
                    PHA
                    LDA TXTPTR
                    PHA
                    LDA (FNCNAM),Y                  ; Y=0 FROM MOVMF
                    STA TXTPTR                      ; POINT TO FUNCTION DEF'N
                    INY
                    LDA (FNCNAM),Y
                    STA TXTPTR+1
                    LDA VARPNT+1                    ; SAVE ADDRESS OF ARGUMENT VARIABLE
                    PHA                             ; 
                    LDA VARPNT                      ; 
                    PHA                             ; 
                    JSR FRMNUM                      ; EVALUATE THE FUNCTION EXPRESSION
                    PLA                             ; GET ADDRESS OF ARGUMENT VARIABLE
                    STA FNCNAM                      ; AND SAVE IT
                    PLA                             ; 
                    STA FNCNAM+1                    ; 
                    JSR CHRGOT                      ; MUST BE AT ":" OR EOL
                    BEQ ?2                          ; WE ARE
                    JMP SYNERR                      ; WE ARE NOT, SLYNTAX ERROR
?2                  PLA                             ; RETRIEVE TXTPTR AFTER "FN" CALL
                    STA TXTPTR
                    PLA
                    STA TXTPTR+1
                                                    ; STACK NOW HAS 5-BYTE VALUE
                                                    ; OF THE ARGUMENT VARIABLE,
                                                    ; AND FNCNAM POINTS AT THE VARIABLE
                                                    ; --------------------------------
                                                    ; STORE FIVE BYTES FROM STACK AT (FNCNAM)
                                                    ; --------------------------------
FNCDATA
                    LDY #0
                    PLA
                    STA (FNCNAM),Y
                    PLA
                    INY
                    STA (FNCNAM),Y
                    PLA
                    INY
                    STA (FNCNAM),Y
                    PLA
                    INY
                    STA (FNCNAM),Y
                    PLA
                    INY
                    STA (FNCNAM),Y
                    RTS
                                                    ; --------------------------------
                                                    ; "STR$" FUNCTION
                                                    ; --------------------------------
STR                 JSR CHKNUM                      ; EXPRESSION MUST BE NUMERIC
                    LDY #0                          ; START STRING AT STACK-1 ($00FF)
                                                    ; SO STRLIT CAN DIFFRENTIATE STR$ CALLS
                    JSR FOUT_1                      ; CONVERT FAC TO STRING
                    PLA                             ; POP RETURN OFF STACK
                    PLA                             ; 
                    LDA #<STACK-1                   ; POINT TO STACK-1
                    LDY #>STACK-1                   ; (WHICH=0)
                    BEQ STRLIT                      ; ...ALWAYS, CREATE DESC & MOVE STRING
                                                    ; --------------------------------
                                                    ; GET SPACE AND MAKE DESCRIPTOR FOR STRING WHOSE
                                                    ; ADDRESS IS IN FAC+3,4 AND WHOSE LENGTH IS IN A-REG
                                                    ; --------------------------------
STRINI              LDX FAC+3                       ; Y,X = STRING ADDRESS
                    LDY FAC+4                       ; 
                    STX DSCPTR                      ; 
                    STY DSCPTR+1                    ; 
                                                    ; --------------------------------
                                                    ; GET SPACE AND MAKE DESCRIPTOR FOR STRING WHOSE
                                                    ; ADDRESS IS IN Y,X AND WHOSE LENGTH IS IN A-REG
                                                    ; --------------------------------
STRSPA              JSR GETSPA                      ; A HOLDS LENGTH
                    STX FAC+1                       ; SAVE DESCRIPTOR IN FAC
                    STY FAC+2                       ; ---FAC--- --FAC+1-- --FAC+2--
                    STA FAC                         ; <LENGTH>  <ADDR-LO> <ADDR-HI>
                    RTS                             ; 
                                                    ; --------------------------------
                                                    ; BUILD A DESCRIPTOR FOR STRING STARTING AT Y,A
                                                    ; AND TERMINATED BY $00 OR QUOTATION MARK
                                                    ; RETURN WITH DESCRIPTOR IN A TEMPORARY
                                                    ; AND ADDRESS OF DESCRIPTOR IN FAC+3,4
                                                    ; --------------------------------
STRLIT              LDX #HICHAR(`"')                        ; SET UP LITERAL SCAN TO STOP ON
                    STX CHARAC                      ; QUOTATION MARK OR $00
                    STX ENDCHR                      ; 
                                                    ; --------------------------------
                                                    ; BUILD A DESCRIPTOR FOR STRING STARTING AT Y,A
                                                    ; AND TERMINATED BY $00, (CHARAC), OR (ENDCHR)
                                                    ; 
                                                    ; RETURN WITH DESCRIPTOR IN A TEMPORARY
                                                    ; AND ADDRESS OF DESCRIPTOR IN FAC+3,4
                                                    ; --------------------------------
STRLT2              STA STRNG1                      ; SAVE ADDRESS OF STRING
                    STY STRNG1+1                    ; 
                    STA FAC+1                       ; ...AGAIN
                    STY FAC+2                       ; 
                    LDY #$FF                        ; 
?1                  INY                             ; FIND END OF STRING
                    LDA (STRNG1),Y                  ; NEXT STRING CHAR
                    BEQ ?3                          ; END OF STRING
                    CMP CHARAC                      ; ALTERNATE TERMINATOR # 1?
                    BEQ ?2                          ; YES
                    CMP ENDCHR                      ; ALTERNATE TERMINATOR # 2?
                    BNE ?1                          ; NO, KEEP SCANNING
?2                  CMP #HICHAR(`"')                        ; IS STRING ENDED WITH QUOTE MARK?
                    BEQ ?4                          ; YES, C=1 TO INCLUDE " IN STRING
?3                  CLC                             ; 
?4                  STY FAC                         ; SAVE LENGTH
                    TYA                             ; 
                    ADC STRNG1                      ; COMPUTE ADDRESS OF END OF STRING
                    STA STRNG2                      ; (OF 00 BYTE, OR JUST AFTER ")
                    LDX STRNG1+1                    ; 
                    BCC ?5                          ; 
                    INX                             ; 
?5                  STX STRNG2+1                    ; 
                    LDA STRNG1+1                    ; WHERE DOES THE STRING START?
                    BEQ ?6                          ; PAGE 0, MUST BE FROM STR$ FUNCTION
                    CMP #2                          ; PAGE 2?
                    BNE PUTNEW                      ; NO, NOT PAGE 0 OR 2
?6                  TYA                             ; LENGTH OF STRING
                    JSR STRINI                      ; MAKE SPACE FOR STRING
                    LDX STRNG1                      ; 
                    LDY STRNG1+1                    ; 
                    JSR MOVSTR                      ; MOVE IT IN
                                                    ; --------------------------------
                                                    ; STORE DESCRIPTOR IN TEMPORARY DESCRIPTOR STACK
                                                    ; 
                                                    ; THE DESCRIPTOR IS NOW IN FAC, FAC+1, FAC+2
                                                    ; PUT ADDRESS OF TEMP DESCRIPTOR IN FAC+3,4
                                                    ; --------------------------------
PUTNEW              LDX TEMPPT                      ; POINTER TO NEXT TEMP STRING SLOT
                    CPX #TEMPST+9                   ; MAX OF 3 TEMP STRINGS
                    BNE PUTEMP                      ; ROOM FOR ANOTHER ONE
                    LDX #ERR_FRMCPX                 ; TOO MANY, FORMULA TOO COMPLEX
JERR                JMP ERROR
                                                    ; --------------------------------
PUTEMP              LDA FAC                         ; COPY TEMP DESCRIPTOR INTO TEMP STACK
                    STA 0,X
                    LDA FAC+1
                    STA 1,X
                    LDA FAC+2
                    STA 2,X
                    LDY #0
                    STX FAC+3                       ; ADDRESS OF TEMP DESCRIPTOR
                    STY FAC+4                       ; IN Y,X AND FAC+3,4
                    DEY                             ; Y=$FF
                    STY VALTYP                      ; FLAG (FAC ) AS STRING
                    STX LASTPT                      ; INDEX OF LAST POINTER
                    INX                             ; UPDATE FOR NEXT TEMP ENTRY
                    INX
                    INX
                    STX TEMPPT
                    RTS
                                                    ; --------------------------------
                                                    ; MAKE SPACE FOR STRING AT BOTTOM OF STRING SPACE
                                                    ; (A)=# BYTES SPACE TO MAKE
                                                    ; 
                                                    ; RETURN WITH (A) SAME,
                                                    ; AND Y,X = ADDRESS OF SPACE ALLOCATED
                                                    ; --------------------------------
GETSPA              LSR GARFLG                      ; CLEAR SIGNBIT OF FLAG
?1                  PHA                             ; A HOLDS LENGTH
                    EOR #$FF                        ; GET -LENGTH
                    SEC                             ; 
                    ADC FRETOP                      ; COMPUTE STARTING ADDRESS OF SPACE
                    LDY FRETOP+1                    ; FOR THE STRING
                    BCS ?2                          ; 
                    DEY                             ; 
?2                  CPY STREND+1                    ; SEE IF FITS IN REMAINING MEMORY
                    BCC ?4                          ; NO, TRY GARBAGE
                    BNE ?3                          ; YES, IT FITS
                    CMP STREND                      ; HAVE TO CHECK LOWER BYTES
                    BCC ?4                          ; NOT ENUF ROOM YET
?3                  STA FRETOP                      ; THERE IS ROOM SO SAVE NEW FRETOP
                    STY FRETOP+1                    ; 
                    STA FRESPC                      ; 
                    STY FRESPC+1                    ; 
                    TAX                             ; ADDR IN Y,X
                    PLA                             ; LENGTH IN A
                    RTS
?4                  LDX #ERR_MEMFULL
                    LDA GARFLG                      ; GARBAGE DONE YET?
                    BMI JERR                        ; YES, MEMORY IS REALLY FULL
                    JSR GARBAG                      ; NO, TRY COLLECTING NOW
                    LDA #$80                        ; FLAG THAT COLLECTED GARBAGE ALREADY
                    STA GARFLG                      ; 
                    PLA                             ; GET STRING LENGTH AGAIN
                    BNE ?1                          ; ...ALWAYS
                                                    ; --------------------------------
                                                    ; SHOVE ALL REFERENCED STRINGS AS HIGH AS POSSIBLE
                                                    ; IN MEMORY (AGAINST HIMEM), FREEING UP SPACE
                                                    ; BELOW STRING AREA DOWN TO STREND.
                                                    ; --------------------------------
GARBAG              LDX MEMSIZ                      ; COLLECT FROM TOP DOWN
                    LDA MEMSIZ+1                    ; 
FIND_HIGHEST_STRING                                 ; 
                    STX FRETOP                      ; ONE PASS THROUGH ALL VARS
                    STA FRETOP+1                    ; FOR EACH ACTIVE STRING!
                    LDY #0                          ; 
                    STY FNCNAM+1                    ; FLAG IN CASE NO STRINGS TO COLLECT
                    LDA STREND                      ; 
                    LDX STREND+1                    ; 
                    STA LOWTR                       ; 
                    STX LOWTR+1                     ; 
                                                    ; --------------------------------
                                                    ; START BY COLLECTING TEMPORARIES
                                                    ; --------------------------------
                    LDA #<TEMPST                    ; 
                    LDX #>TEMPST                    ; 
                    STA INDEX                       ; 
                    STX INDEX+1                     ; 
?1                  CMP TEMPPT                      ; FINISHED WITH TEMPS YET?
                    BEQ ?2                          ; YES, NOW DO SIMPLE VARIABLES
                    JSR CHECK_VARIABLE              ; DO A TEMP
                    BEQ ?1                          ; ...ALWAYS
                                                    ; --------------------------------
                                                    ; NOW COLLECT SIMPLE VARIABLES
                                                    ; --------------------------------
?2                  LDA #7                          ; LENGTH OF EACH VARIABLE IS 7 BYTES
                    STA DSCLEN                      ; 
                    LDA VARTAB                      ; START AT BEGINNING OF VARTAB
                    LDX VARTAB+1
                    STA INDEX
                    STX INDEX+1
?3                  CPX ARYTAB+1                    ; FINISHED WITH SIMPLE VARIABLES?
                    BNE ?4                          ; NO
                    CMP ARYTAB                      ; MAYBE, CHECK LO-BYTE
                    BEQ ?5                          ; YES, NOW DO ARRAYS
?4                  JSR CHECK_SIMPLE_VARIABLE
                    BEQ ?3                          ; ...ALWAYS
                                                    ; --------------------------------
                                                    ; NOW COLLECT ARRAY VARIABLES
                                                    ; --------------------------------
?5                  STA ARYPNT
                    STX ARYPNT+1
                    LDA #3                          ; DESCRIPTORS IN ARRAYS ARE 3-BYTES EACH
                    STA DSCLEN                      ; 
?6                  LDA ARYPNT                      ; COMPARE TO END OF ARRAYS
                    LDX ARYPNT+1                    ; 
?7                  CPX STREND+1                    ; FINISHED WITH ARRAYS YET?
                    BNE ?8                          ; NOT YET
                    CMP STREND                      ; MAYBE, CHECK LO-BYTE
                    BNE ?8                          ; NOT FINISHED YET
                    JMP MOVE_HIGHEST_STRING_TO_TOP  ; FINISHED
?8                  STA INDEX                       ; SET UP PNTR TO START OF ARRAY
                    STX INDEX+1                     ; 
                    LDY #0                          ; POINT AT NAME OF ARRAY
                    LDA (INDEX),Y                   ; 
                    TAX                             ; 1ST LETTER OF NAME IN X-REG
                    INY                             ; 
                    LDA (INDEX),Y                   ; 
                    PHP                             ; STATUS FROM SECOND LETTER OF NAME
                    INY                             ; 
                    LDA (INDEX),Y                   ; OFFSET TO NEXT ARRAY
                    ADC ARYPNT                      ; (CARRY ALWAYS CLEAR)
                    STA ARYPNT                      ; CALCULATE START OF NEXT ARRAY
                    INY                             ; 
                    LDA (INDEX),Y                   ; HI-BYTE OF OFFSET
                    ADC ARYPNT+1                    ; 
                    STA ARYPNT+1                    ; 
                    PLP                             ; GET STATUS FROM 2ND CHAR OF NAME
                    BPL ?6                          ; NOT A STRING ARRAY
                    TXA                             ; SET STATUS WITH 1ST CHAR OF NAME
                    BMI ?6                          ; NOT A STRING ARRAY
                    INY                             ; 
                    LDA (INDEX),Y                   ; # OF DIMENSIONS FOR THIS ARRAY
                    LDY #0                          ; 
                    ASL                             ; PREAMBLE SIZE = 2*#DIMS + 5
                    ADC #5                          ; 
                    ADC INDEX                       ; MAKE INDEX POINT AT FIRST ELEMENT
                    STA INDEX                       ; IN THE ARRAY
                    BCC ?9                          ; 
                    INC INDEX+1                     ; 
?9                                                  ; 
                    LDX INDEX+1                     ; STEP THRU EACH STRING IN THIS ARRAY
?10                 CPX ARYPNT+1                    ; ARRAY DONE?
                    BNE ?11                         ; NO, PROCESS NEXT ELEMENT
                    CMP ARYPNT                      ; MAYBE, CHECK LO-BYTE
                    BEQ ?7                          ; YES, MOVE TO NEXT ARRAY
?11                 JSR CHECK_VARIABLE              ; PROCESS THE ARRAY
                    BEQ ?10                         ; ...ALWAYS
                                                    ; --------------------------------
                                                    ; PROCESS A SIMPLE VARIABLE
                                                    ; --------------------------------
CHECK_SIMPLE_VARIABLE
                    LDA (INDEX),Y                   ; LOOK AT 1ST CHAR OF NAME
                    BMI CHECK_BUMP                  ; NOT A STRING VARIABLE
                    INY                             ; 
                    LDA (INDEX),Y                   ; LOOK AT 2ND CHAR OF NAME
                    BPL CHECK_BUMP                  ; NOT A STRING VARIABLE
                    INY                             ; 
                                                    ; --------------------------------
                                                    ; IF STRING IS NOT EMPTY, CHECK IF IT IS HIGHEST
                                                    ; --------------------------------
CHECK_VARIABLE                                      ; 
                    LDA (INDEX),Y                   ; GET LENGTH OF STRING
                    BEQ CHECK_BUMP                  ; IGNORE STRING IF LENGTH IS ZERO
                    INY                             ; 
                    LDA (INDEX),Y                   ; GET ADDRESS OF STRING
                    TAX                             ; 
                    INY                             ; 
                    LDA (INDEX),Y                   ; 
                    CMP FRETOP+1                    ; CHECK IF ALREADY COLLECTED
                    BCC ?1                          ; NO, BELOW FRETOP
                    BNE CHECK_BUMP                  ; YES, ABOVE FRETOP
                    CPX FRETOP                      ; MAYBE, CHECK LO-BYTE
                    BCS CHECK_BUMP                  ; YES, ABOVE FRETOP
?1                  CMP LOWTR+1                     ; ABOVE HIGHEST STRING FOUND?
                    BCC CHECK_BUMP                  ; NO, IGNORE FOR NOW
                    BNE ?2                          ; YES, THIS IS THE NEW HIGHEST
                    CPX LOWTR                       ; MAYBE, TRY LO-BYTE
                    BCC CHECK_BUMP                  ; NO, IGNORE FOR NOW
?2                  STX LOWTR                       ; MAKE THIS THE HIGHEST STRING
                    STA LOWTR+1
                    LDA INDEX                       ; SAVE ADDRESS OF DESCRIPTOR TOO
                    LDX INDEX+1
                    STA FNCNAM
                    STX FNCNAM+1
                    LDA DSCLEN
                    STA LENGTH
                                                    ; --------------------------------
                                                    ; ADD (DSCLEN) TO PNTR IN INDEX
                                                    ; RETURN WITH Y=0, PNTR ALSO IN X,A
                                                    ; --------------------------------
CHECK_BUMP
                    LDA DSCLEN                      ; BUMP TO NEXT VARIABLE
                    CLC
                    ADC INDEX
                    STA INDEX
                    BCC CHECK_EXIT
                    INC INDEX+1
                                                    ; --------------------------------
CHECK_EXIT
                    LDX INDEX+1
                    LDY #0
                    RTS
                                                    ; --------------------------------
                                                    ; FOUND HIGHEST NON-EMPTY STRING, SO MOVE IT
                                                    ; TO TOP AND GO BACK FOR ANOTHER
                                                    ; --------------------------------
MOVE_HIGHEST_STRING_TO_TOP
                    LDX FNCNAM+1                    ; ANY STRING FOUND?
                    BEQ CHECK_EXIT                  ; NO, RETURN
                    LDA LENGTH                      ; GET LENGTH OF VARIABLE ELEMENT
                    AND #4                          ; WAS 7 OR 3, MAKE 4 OR 0
                    LSR                             ; 2 0R 0; IN SIMPLE VARIABLES,
                    TAY                             ; NAME PRECEDES DESCRIPTOR
                    STA LENGTH                      ; 2 OR 0
                    LDA (FNCNAM),Y                  ; GET LENGTH FROM DESCRIPTOR
                    ADC LOWTR                       ; CARRY ALREADY CLEARED BY LSR
                    STA HIGHTR                      ; STRING IS BTWN (LOWTR) AND (HIGHTR)
                    LDA LOWTR+1                     ; 
                    ADC #0                          ; 
                    STA HIGHTR+1                    ; 
                    LDA FRETOP                      ; HIGH END DESTINATION
                    LDX FRETOP+1                    ; 
                    STA HIGHDS                      ; 
                    STX HIGHDS+1                    ; 
                    JSR BLTU2                       ; MOVE STRING UP
                    LDY LENGTH                      ; FIX ITS DESCRIPTOR
                    INY                             ; POINT AT ADDRESS IN DESCRIPTOR
                    LDA HIGHDS                      ; STORE NEW ADDRESS
                    STA (FNCNAM),Y
                    TAX
                    INC HIGHDS+1                    ; CORRECT BLTU'S OVERSHOOT
                    LDA HIGHDS+1
                    INY
                    STA (FNCNAM),Y
                    JMP FIND_HIGHEST_STRING
                                                    ; --------------------------------
                                                    ; --------------------------------
                                                    ; CONCATENATE TWO STRINGS
                                                    ; --------------------------------
CAT                 LDA FAC+4                       ; SAVE ADDRESS OF FIRST DESCRIPTOR
                    PHA
                    LDA FAC+3
                    PHA
                    JSR FRM_ELEMENT                 ; GET SECOND STRING ELEMENT
                    JSR CHKSTR                      ; MUST BE A STRING
                    PLA                             ; RECOVER ADDRES OF 1ST DESCRIPTOR
                    STA STRNG1
                    PLA
                    STA STRNG1+1
                    LDY #0
                    LDA (STRNG1),Y                  ; ADD LENGTHS, GET CONCATENATED SIZE
                    CLC
                    ADC (FAC+3),Y
                    BCC ?1                          ; OK IF < $100
                    LDX #ERR_STRLONG
                    JMP ERROR
?1                  JSR STRINI                      ; GET SPACE FOR CONCATENATED STRINGS
                    JSR MOVINS                      ; MOVE 1ST STRING
                    LDA DSCPTR                      ; 
                    LDY DSCPTR+1                    ; 
                    JSR FRETMP                      ; 
                    JSR MOVSTR_1                    ; MOVE 2ND STRING
                    LDA STRNG1                      ; 
                    LDY STRNG1+1                    ; 
                    JSR FRETMP                      ; 
                    JSR PUTNEW                      ; SET UP DESCRIPTOR
                    JMP FRMEVL_2                    ; FINISH EXPRESSION
                                                    ; --------------------------------
                                                    ; GET STRING DESCRIPTOR POINTED AT BY (STRNG1)
                                                    ; AND MOVE DESCRIBED STRING TO (FRESPC)
                                                    ; --------------------------------
MOVINS              LDY #0
                    LDA (STRNG1),Y
                    PHA                             ; LENGTH
                    INY
                    LDA (STRNG1),Y
                    TAX                             ; PUT STRING POINTER IN X,Y
                    INY
                    LDA (STRNG1),Y
                    TAY
                    PLA                             ; RETRIEVE LENGTH
                                                    ; --------------------------------
                                                    ; MOVE STRING AT (Y,X) WITH LENGTH (A)
                                                    ; TO DESTINATION WHOSE ADDRESS IS IN FRESPC,FRESPC+1
                                                    ; --------------------------------
MOVSTR              STX INDEX                       ; PUT POINTER IN INDEX
                    STY INDEX+1                     ; 
MOVSTR_1                                            ; 
                    TAY                             ; LENGTH TO Y-REG
                    BEQ ?2                          ; IF LENGTH IS ZERO, FINISHED
                    PHA                             ; SAVE LENGTH ON STACK
?1                  DEY                             ; MOVE BYTES FROM (INDEX) TO (FRESPC)
                    LDA (INDEX),Y
                    STA (FRESPC),Y
                    TYA                             ; TEST IF ANY LEFT TO MOVE
                    BNE ?1                          ; YES, KEEP MOVING
                    PLA                             ; NO, FINISHED.  GET LENGTH
?2                  CLC                             ; AND ADD TO FRESPC, SO
                    ADC FRESPC                      ; FRESPC POINTS TO NEXT HIGHER
                    STA FRESPC                      ; BYTE.  (USED BY CONCATENATION)
                    BCC ?3
                    INC FRESPC+1
?3                  RTS
                                                    ; --------------------------------
                                                    ; IF (FAC) IS A TEMPORARY STRING, RELEASE DESCRIPTOR
                                                    ; --------------------------------
FRESTR              JSR CHKSTR                      ; LAST RESULT A STRING?
                                                    ; --------------------------------
                                                    ; IF STRING DESCRIPTOR POINTED TO BY FAC+3,4 IS
                                                    ; A TEMPORARY STRING, RELEASE IT.
                                                    ; --------------------------------
FREFAC              LDA FAC+3                       ; GET DESCRIPTOR POINTER
                    LDY FAC+4
                                                    ; --------------------------------
                                                    ; IF STRING DESCRIPTOR WHOSE ADDRESS IS IN Y,A IS
                                                    ; A TEMPORARY STRING, RELEASE IT.
                                                    ; --------------------------------
FRETMP              STA INDEX                       ; SAVE THE ADDRESS OF THE DESCRIPTOR
                    STY INDEX+1                     ; 
                    JSR FRETMS                      ; FREE DESCRIPTOR IF IT IS TEMPORARY
                    PHP                             ; REMEMBER IF TEMP
                    LDY #0                          ; POINT AT LENGTH OF STRING
                    LDA (INDEX),Y                   ; 
                    PHA                             ; SAVE LENGTH ON STACK
                    INY                             ; 
                    LDA (INDEX),Y                   ; 
                    TAX                             ; GET ADDRESS OF STRING IN Y,X
                    INY                             ; 
                    LDA (INDEX),Y                   ; 
                    TAY                             ; 
                    PLA                             ; LENGTH IN A
                    PLP                             ; RETRIEVE STATUS, Z=1 IF TEMP
                    BNE ?2                          ; NOT A TEMPORARY STRING
                    CPY FRETOP+1                    ; IS IT THE LOWEST STRING?
                    BNE ?2                          ; NO
                    CPX FRETOP                      ; 
                    BNE ?2                          ; NO
                    PHA                             ; YES, PUSH LENGTH AGAIN
                    CLC                             ; RECOVER THE SPACE USED BY
                    ADC FRETOP                      ; THE STRING
                    STA FRETOP                      ; 
                    BCC ?1                          ; 
                    INC FRETOP+1                    ; 
?1                  PLA                             ; RETRIEVE LENGTH AGAIN
?2                  STX INDEX                       ; ADDRESS OF STRING IN Y,X
                    STY INDEX+1                     ; LENGTH OF STRING IN A-REG
                    RTS                             ; 
                                                    ; --------------------------------
                                                    ; RELEASE TEMPORARY DESCRIPTOR IF Y,A = LASTPT
                                                    ; --------------------------------
FRETMS              CPY LASTPT+1                    ; COMPARE Y,A TO LATEST TEMP
                    BNE ?1                          ; NOT SAME ONE, CANNOT RELEASE
                    CMP LASTPT                      ; 
                    BNE ?1                          ; NOT SAME ONE, CANNOT RELEASE
                    STA TEMPPT                      ; UPDATE TEMPT FOR NEXT TEMP
                    SBC #3                          ; BACK OFF LASTPT
                    STA LASTPT                      ; 
                    LDY #0                          ; NOW Y,A POINTS TO TOP TEMP
?1                  RTS                             ; Z=0 IF NOT TEMP, Z=1 IF TEMP
                                                    ; --------------------------------
                                                    ; "CHR$" FUNCTION
                                                    ; --------------------------------
CHRSTR              JSR CONINT                      ; CONVERT ARGUMENT TO BYTE IN X
                    TXA                             ; 
                    PHA                             ; SAVE IT
                    LDA #1                          ; GET SPACE FOR STRING OF LENGTH 1
                    JSR STRSPA                      ; 
                    PLA                             ; RECALL THE CHARACTER
                    LDY #0                          ; PUT IN STRING
                    STA (FAC+1),Y                   ; 
                    PLA                             ; POP RETURN ADDRESS
                    PLA                             ; 
                    JMP PUTNEW                      ; MAKE IT A TEMPORARY STRING
                                                    ; --------------------------------
                                                    ; "LEFT$" FUNCTION
                                                    ; --------------------------------
LEFTSTR
                    JSR SUBSTRING_SETUP
                    CMP (DSCPTR),Y                  ; COMPARE 1ST PARAMETER TO LENGTH
                    TYA                             ; Y=A=0
SUBSTRING_1                                         ; 
                    BCC ?1                          ; 1ST PARAMETER SMALLER, USE IT
                    LDA (DSCPTR),Y                  ; 1ST IS LONGER, USE STRING LENGTH
                    TAX                             ; IN X-REG
                    TYA                             ; Y=A=0 AGAIN
?1                  PHA                             ; PUSH LEFT END OF SUBSTRING
SUBSTRING_2                                         ; 
                    TXA                             ; 
SUBSTRING_3                                         ; 
                    PHA                             ; PUSH LENGTH OF SUBSTRING
                    JSR STRSPA                      ; MAKE ROOM FOR STRING OF (A) BYTES
                    LDA DSCPTR                      ; RELEASE PARAMETER STRING IF TEMP
                    LDY DSCPTR+1                    ; 
                    JSR FRETMP                      ; 
                    PLA                             ; GET LENGTH OF SUBSTRING
                    TAY                             ; IN Y-REG
                    PLA                             ; GET LEFT END OF SUBSTRING
                    CLC                             ; ADD TO POINTER TO STRING
                    ADC INDEX                       ; 
                    STA INDEX                       ; 
                    BCC ?1                          ; 
                    INC INDEX+1                     ; 
?1                  TYA                             ; LENGTH
                    JSR MOVSTR_1                    ; COPY STRING INTO SPACE
                    JMP PUTNEW                      ; ADD TO TEMPS
                                                    ; --------------------------------
                                                    ; "RIGHT$" FUNCTION
                                                    ; --------------------------------
RIGHTSTR
                    JSR SUBSTRING_SETUP
                    CLC                             ; COMPUTE LENGTH-WIDTH OF SUBSTRING
                    SBC (DSCPTR),Y                  ; TO GET STARTING POINT IN STRING
                    EOR #$FF
                    JMP SUBSTRING_1                 ; JOIN LEFT$
                                                    ; --------------------------------
                                                    ; "MID$" FUNCTION
                                                    ; --------------------------------
MIDSTR              LDA #$FF                        ; FLAG WHETHER 2ND PARAMETER
                    STA FAC+4                       ; 
                    JSR CHRGOT                      ; SEE IF ")" YET
                    CMP #HICHAR(`)')                        ; 
                    BEQ ?1                          ; YES, NO 2ND PARAMETER
                    JSR CHKCOM                      ; NO, MUST HAVE COMMA
                    JSR GETBYT                      ; GET 2ND PARAM IN X-REG
?1                  JSR SUBSTRING_SETUP
                    DEX                             ; 1ST PARAMETER - 1
                    TXA
                    PHA
                    CLC
                    LDX #0
                    SBC (DSCPTR),Y
                    BCS SUBSTRING_2
                    EOR #$FF
                    CMP FAC+4                       ; USE SMALLER OF TWO
                    BCC SUBSTRING_3
                    LDA FAC+4
                    BCS SUBSTRING_3                 ; ...ALWAYS
                                                    ; --------------------------------
                                                    ; COMMON SETUP ROUTINE FOR LEFT$, RIGHT$, MID$:
                                                    ; REQUIRE ")"; POP RETURN ADRS, GET DESCRIPTOR
                                                    ; ADDRESS, GET 1ST PARAMETER OF COMMAND
                                                    ; --------------------------------
SUBSTRING_SETUP
                    JSR CHKCLS                      ; REQUIRE ")"
                    PLA                             ; SAVE RETURN ADDRESS
                    TAY                             ; IN Y-REG AND LENGTH
                    PLA                             ; 
                    STA LENGTH                      ; 
                    PLA                             ; POP PREVIOUS RETURN ADDRESS
                    PLA                             ; (FROM GOROUT).
                    PLA                             ; RETRIEVE 1ST PARAMETER
                    TAX                             ; 
                    PLA                             ; GET ADDRESS OF STRING DESCRIPTOR
                    STA DSCPTR                      ; 
                    PLA                             ; 
                    STA DSCPTR+1                    ; 
                    LDA LENGTH                      ; RESTORE RETURN ADDRESS
                    PHA                             ; 
                    TYA                             ; 
                    PHA                             ; 
                    LDY #0                          ; 
                    TXA                             ; GET 1ST PARAMETER IN A-REG
                    BEQ GOIQ                        ; ERROR IF 0
                    RTS
                                                    ; --------------------------------
                                                    ; "LEN" FUNCTION
                                                    ; --------------------------------
LEN                 JSR GETSTR                      ; GET LENTGH IN Y-REG, MAKE FAC NUMERIC
                    JMP SNGFLT                      ; FLOAT Y-REG INTO FAC
                                                    ; --------------------------------
                                                    ; IF LAST RESULT IS A TEMPORARY STRING, FREE IT
                                                    ; MAKE VALTYP NUMERIC, RETURN LENGTH IN Y-REG
                                                    ; --------------------------------
GETSTR              JSR FRESTR                      ; IF LAST RESULT IS A STRING, FREE IT
                    LDX #0                          ; MAKE VALTYP NUMERIC
                    STX VALTYP                      ; 
                    TAY                             ; LENGTH OF STRING TO Y-REG
                    RTS
                                                    ; --------------------------------
                                                    ; "ASC" FUNCTION
                                                    ; --------------------------------
ASC                 JSR GETSTR                      ; GET STRING, GET LENGTH IN Y-REG
                    BEQ GOIQ                        ; ERROR IF LENGTH 0
                    LDY #0                          ; 
                    LDA (INDEX),Y                   ; GET 1ST CHAR OF STRING
                    TAY                             ; 
                    JMP SNGFLT                      ; FLOAT Y-REG INTO FAC
                                                    ; --------------------------------
GOIQ                JMP IQERR                       ; ILLEGAL QUANTITY ERROR
                                                    ; --------------------------------
                                                    ; SCAN TO NEXT CHARACTER AND CONVERT EXPRESSION
                                                    ; TO SINGLE BYTE IN X-REG
                                                    ; --------------------------------
GTBYTC              JSR CHRGET
                                                    ; --------------------------------
                                                    ; EVALUATE EXPRESSION AT TXTPTR, AND
                                                    ; CONVERT IT TO SINGLE BYTE IN X-REG
                                                    ; --------------------------------
GETBYT              JSR FRMNUM
                                                    ; --------------------------------
                                                    ; CONVERT (FAC) TO SINGLE BYTE INTEGER IN X-REG
                                                    ; --------------------------------
CONINT              JSR MKINT                       ; CONVERT IF IN RANGE -32767 TO +32767
                    LDX FAC+3                       ; HI-BYTE MUST BE ZERO
                    BNE GOIQ                        ; VALUE > 255, ERROR
                    LDX FAC+4                       ; VALUE IN X-REG
                    JMP CHRGOT                      ; GET NEXT CHAR IN A-REG
                                                    ; --------------------------------
                                                    ; "VAL" FUNCTION
                                                    ; --------------------------------
VAL                 JSR GETSTR                      ; GET POINTER TO STRING IN INDEX
                    BNE ?1                          ; LENGTH NON-ZERO
                    JMP ZERO_FAC                    ; RETURN 0 IF LENGTH=0
?1                  LDX TXTPTR                      ; SAVE CURRENT TXTPTR
                    LDY TXTPTR+1                    ; 
                    STX STRNG2                      ; 
                    STY STRNG2+1                    ; 
                    LDX INDEX                       ; 
                    STX TXTPTR                      ; POINT TXTPTR TO START OF STRING
                    CLC                             ; 
                    ADC INDEX                       ; ADD LENGTH
                    STA DEST                        ; POINT DEST TO END OF STRING + 1
                    LDX INDEX+1                     ; 
                    STX TXTPTR+1                    ; 
                    BCC ?2                          ; 
                    INX                             ; 
?2                  STX DEST+1                      ; 
                    LDY #0                          ; SAVE BYTE THAT FOLLOWS STRING
                    LDA (DEST),Y                    ; ON STACK
                    PHA                             ; 
                    LDA #0                          ; AND STORE $00 IN ITS PLACE
                    STA (DEST),Y                    ; 
                                                    ; <<< THAT CAUSES A BUG IF HIMEM = $BFFF, >>>
                                                    ; <<< BECAUSE STORING $00 AT $C000 IS NO  >>>
                                                    ; <<< USE; $C000 WILL ALWAYS BE LAST CHAR >>>
                                                    ; <<< TYPED, SO FIN WON'T TERMINATE UNTIL >>>
                                                    ; <<< IT SEES A ZERO AT $C010!            >>>
                    JSR CHRGOT                      ; PRIME THE PUMP
                    JSR FIN                         ; EVALUATE STRING
                    PLA                             ; GET BYTE THAT SHOULD FOLLOW STRING
                    LDY #0                          ; AND PUT IT BACK
                    STA (DEST),Y                    ; 
                                                    ; RESTORE TXTPTR
                                                    ; --------------------------------
                                                    ; COPY STRNG2 INTO TXTPTR
                                                    ; --------------------------------
POINT               LDX STRNG2                      ; 
                    LDY STRNG2+1                    ; 
                    STX TXTPTR                      ; 
                    STY TXTPTR+1                    ; 
                    RTS                             ; 
                                                    ; --------------------------------
                                                    ; EVALUATE "EXP1,EXP2"
                                                    ; 
                                                    ; CONVERT EXP1 TO 16-BIT NUMBER IN LINNUM
                                                    ; CONVERT EXP2 TO 8-BIT NUMBER IN X-REG
                                                    ; --------------------------------
GTNUM               JSR FRMNUM                      ; 
                    JSR GETADR                      ; 
                                                    ; --------------------------------
                                                    ; EVALUATE ",EXPRESSION"
                                                    ; CONVERT EXPRESSION TO SINGLE BYTE IN X-REG
                                                    ; --------------------------------
COMBYTE                                             ; 
                    JSR CHKCOM                      ; MUST HAVE COMMA FIRST
                    JMP GETBYT                      ; CONVERT EXPRESSION TO BYTE IN X-REG
                                                    ; --------------------------------
                                                    ; CONVERT (FAC) TO A 16-BIT VALUE IN LINNUM
                                                    ; --------------------------------
GETADR              LDA FAC                         ; FAC < 2^16?
                    CMP #$91                        ; 
                    BCS GOIQ                        ; NO, ILLEGAL QUANTITY
                    JSR QINT                        ; CONVERT TO INTEGER
                    LDA FAC+3                       ; COPY IT INTO LINNUM
                    LDY FAC+4                       ; 
                    STY LINNUM                      ; TO LINNUM
                    STA LINNUM+1                    ; 
                    RTS                             ; 
                                                    ; --------------------------------
                                                    ; "PEEK" FUNCTION
                                                    ; --------------------------------
PEEK                LDA LINNUM                      ; SAVE (LINNUM) ON STACK DURING PEEK
                    PHA                             ; 
                    LDA LINNUM+1                    ; 
                    PHA                             ; 
                    JSR GETADR                      ; GET ADDRESS PEEKING AT
                    LDY #0
                    LDA (LINNUM),Y                  ; TAKE A QUICK LOOK
                    TAY                             ; VALUE IN Y-REG
                    PLA                             ; RESTORE LINNUM FROM STACK
                    STA LINNUM+1                    ; 
                    PLA                             ; 
                    STA LINNUM                      ; 
                    JMP SNGFLT                      ; FLOAT Y-REG INTO FAC
                                                    ; --------------------------------
                                                    ; "POKE" STATEMENT
                                                    ; --------------------------------
POKE                JSR GTNUM                       ; GET THE ADDRESS AND VALUE
                    TXA                             ; VALUE IN A,
                    LDY #0                          ; 
                    STA (LINNUM),Y                  ; STORE IT AWAY,
                    RTS                             ; AND THAT'S ALL FOR TODAY
                                                    ; --------------------------------
                                                    ; "WAIT" STATEMENT
                                                    ; --------------------------------
WAIT                JSR GTNUM                       ; GET ADDRESS IN LINNUM, MASK IN X
                    STX FORPNT                      ; SAVE MASK
                    LDX #0                          ; 
                    JSR CHRGOT                      ; ANOTHER PARAMETER?
                    BEQ ?1                          ; NO, USE $00 FOR EXCLUSIVE-OR
                    JSR COMBYTE                     ; GET XOR-MASK
?1                  STX FORPNT+1                    ; SAVE XOR-MASK HERE
                    LDY #0
?2                  LDA (LINNUM),Y                  ; GET BYTE AT ADDRESS
                    EOR FORPNT+1                    ; INVERT SPECIFIED BITS
                    AND FORPNT                      ; SELECT SPECIFIED BITS
                    BEQ ?2                          ; LOOP TILL NOT 0
RTS_10              RTS
                                                    ; --------------------------------
                                                    ; ADD 0?5 TO FAC
                                                    ; --------------------------------
FADDH               LDA #<CON_HALF                  ; FAC+1/2 -> FAC
                    LDY #>CON_HALF
                    JMP FADD
                                                    ; --------------------------------
                                                    ; FAC = (Y,A) - FAC
                                                    ; --------------------------------
FSUB                JSR LOAD_ARG_FROM_YA
                                                    ; --------------------------------
                                                    ; FAC = ARG - FAC
                                                    ; --------------------------------
FSUBT               LDA FAC_SIGN                    ; COMPLEMENT FAC AND ADD
                    EOR #$FF                        ; 
                    STA FAC_SIGN                    ; 
                    EOR ARG_SIGN                    ; FIX SGNCPR TOO
                    STA SGNCPR                      ; 
                    LDA FAC                         ; MAKE STATUS SHOW FAC EXPONENT
                    JMP FADDT                       ; JOIN FADD
                                                    ; --------------------------------
                                                    ; SHIFT SMALLER ARGUMENT MORE THAN 7 BITS
                                                    ; --------------------------------
FADD_1              JSR SHIFT_RIGHT                 ; ALIGN RADIX BY SHIFTING
                    BCC FADD_3                      ; ...ALWAYS
                                                    ; --------------------------------
                                                    ; FAC = (Y,A) + FAC
                                                    ; --------------------------------
FADD                JSR LOAD_ARG_FROM_YA
                                                    ; --------------------------------
                                                    ; FAC = ARG + FAC
                                                    ; --------------------------------
FADDT               BNE ?1                          ; FAC IS NON-ZERO
                    JMP COPY_ARG_TO_FAC             ; FAC = 0 + ARG
?1                  LDX FAC_EXTENSION
                    STX ARG_EXTENSION
                    LDX #ARG                        ; SET UP TO SHIFT ARG
                    LDA ARG                         ; EXPONENT
                                                    ; --------------------------------
FADD_2              TAY
                    BEQ RTS_10                      ; IF ARG=0, WE ARE FINISHED
                    SEC                             ; 
                    SBC FAC                         ; GET DIFFNCE OF EXP
                    BEQ FADD_3                      ; GO ADD IF SAME EXP
                    BCC ?1                          ; ARG HAS SMALLER EXPONENT
                    STY FAC                         ; EXP HAS SMALLER EXPONENT
                    LDY ARG_SIGN                    ; 
                    STY FAC_SIGN                    ; 
                    EOR #$FF                        ; COMPLEMENT SHIFT COUNT
                    ADC #0                          ; CARRY WAS SET
                    LDY #0
                    STY ARG_EXTENSION
                    LDX #FAC                        ; SET UP TO SHIFT FAC
                    BNE ?2                          ; ...ALWAYS
?1                  LDY #0
                    STY FAC_EXTENSION
?2                  CMP #$F9                        ; SHIFT MORE THAN 7 BITS?
                    BMI FADD_1                      ; YES
                    TAY                             ; INDEX TO # OF SHIFTS
                    LDA FAC_EXTENSION
                    LSR 1,X                         ; START SHIFTING...
                    JSR SHIFT_RIGHT_4               ; ...COMPLETE SHIFTING
FADD_3              BIT SGNCPR                      ; DO FAC AND ARG HAVE SAME SIGNS?
                    BPL FADD_4                      ; YES, ADD THE MANTISSAS
                    LDY #FAC                        ; NO, SUBTRACT SMALLER FROM LARGER
                    CPX #ARG                        ; WHICH WAS ADJUSTED?
                    BEQ ?1                          ; IF ARG, DO FAC-ARG
                    LDY #ARG                        ; IF FAC, DO ARG-FAC
?1                  SEC                             ; SUBTRACT SMALLER FROM LARGER (WE HOPE)
                    EOR #$FF                        ; (IF EXPONENTS WERE EQUAL, WE MIGHT BE
                    ADC ARG_EXTENSION               ; SUBTRACTING LARGER FROM SMALLER)
                    STA FAC_EXTENSION
                    LDA 4,Y
                    SBC 4,X
                    STA FAC+4
                    LDA 3,Y
                    SBC 3,X
                    STA FAC+3
                    LDA 2,Y
                    SBC 2,X
                    STA FAC+2
                    LDA 1,Y
                    SBC 1,X
                    STA FAC+1
                                                    ; --------------------------------
                                                    ; NORMALIZE VALUE IN FAC
                                                    ; --------------------------------
NORMALIZE_FAC_1
                    BCS NORMALIZE_FAC_2
                    JSR COMPLEMENT_FAC
                                                    ; --------------------------------
NORMALIZE_FAC_2
                    LDY #0                          ; SHIFT UP SIGNIF DIGIT
                    TYA                             ; START A=0, COUNT SHIFTS IN A-REG
                    CLC
?1                  LDX FAC+1                       ; LOOK AT MOST SIGNIFICANT BYTE
                    BNE NORMALIZE_FAC_4             ; SOME 1-BITS HERE
                    LDX FAC+2                       ; HI-BYTE OF MANTISSA STILL ZERO,
                    STX FAC+1                       ; SO DO A FAST 8-BIT SHUFFLE
                    LDX FAC+3
                    STX FAC+2
                    LDX FAC+4
                    STX FAC+3
                    LDX FAC_EXTENSION
                    STX FAC+4
                    STY FAC_EXTENSION               ; ZERO EXTENSION BYTE
                    ADC #8                          ; BUMP SHIFT COUNT
                    CMP #32                         ; DONE 4 TIMES YET?
                    BNE ?1                          ; NO, STILL MIGHT BE SOME 1'S
                                                    ; YES, VALUE OF FAC IS ZERO
                                                    ; --------------------------------
                                                    ; SET FAC = 0
                                                    ; (ONLY NECESSARY TO ZERO EXPONENT AND SIGN CELLS)
                                                    ; --------------------------------
ZERO_FAC
                    LDA #0
                                                    ; --------------------------------
STA_IN_FAC_SIGN_AND_EXP
                    STA FAC
                                                    ; --------------------------------
STA_IN_FAC_SIGN
                    STA FAC_SIGN
                    RTS
                                                    ; --------------------------------
                                                    ; ADD MANTISSAS OF FAC AND ARG INTO FAC
                                                    ; --------------------------------
FADD_4              ADC ARG_EXTENSION
                    STA FAC_EXTENSION
                    LDA FAC+4
                    ADC ARG+4
                    STA FAC+4
                    LDA FAC+3
                    ADC ARG+3
                    STA FAC+3
                    LDA FAC+2
                    ADC ARG+2
                    STA FAC+2
                    LDA FAC+1
                    ADC ARG+1
                    STA FAC+1
                    JMP NORMALIZE_FAC_5
                                                    ; --------------------------------
                                                    ; FINISH NORMALIZING FAC
                                                    ; --------------------------------
NORMALIZE_FAC_3
                    ADC #1                          ; COUNT BITS SHIFTED
                    ASL FAC_EXTENSION
                    ROL FAC+4
                    ROL FAC+3
                    ROL FAC+2
                    ROL FAC+1
                                                    ; --------------------------------
NORMALIZE_FAC_4
                    BPL NORMALIZE_FAC_3             ; UNTIL TOP BIT = 1
                    SEC
                    SBC FAC                         ; ADJUST EXPONENT BY BITS SHIFTED
                    BCS ZERO_FAC                    ; UNDERFLOW, RETURN ZERO
                    EOR #$FF                        ; 
                    ADC #1                          ; 2'S COMPLEMENT
                    STA FAC                         ; CARRY=0 NOW
                                                    ; --------------------------------
NORMALIZE_FAC_5                                     ; 
                    BCC RTS_11                      ; UNLESS MANTISSA CARRIED
                                                    ; --------------------------------
NORMALIZE_FAC_6                                     ; 
                    INC FAC                         ; MANTISSA CARRIED, SO SHIFT RIGHT
                    BEQ OVERFLOW                    ; OVERFLOW IF EXPONENT TOO BIG
                    ROR FAC+1
                    ROR FAC+2
                    ROR FAC+3
                    ROR FAC+4
                    ROR FAC_EXTENSION
RTS_11              RTS
                                                    ; --------------------------------
                                                    ; 2'S COMPLEMENT OF FAC
                                                    ; --------------------------------
COMPLEMENT_FAC
                    LDA FAC_SIGN
                    EOR #$FF
                    STA FAC_SIGN
                                                    ; --------------------------------
                                                    ; 2'S COMPLEMENT OF FAC MANTISSA ONLY
                                                    ; --------------------------------
COMPLEMENT_FAC_MANTISSA
                    LDA FAC+1
                    EOR #$FF
                    STA FAC+1
                    LDA FAC+2
                    EOR #$FF
                    STA FAC+2
                    LDA FAC+3
                    EOR #$FF
                    STA FAC+3
                    LDA FAC+4
                    EOR #$FF
                    STA FAC+4
                    LDA FAC_EXTENSION
                    EOR #$FF
                    STA FAC_EXTENSION
                    INC FAC_EXTENSION               ; START INCREMENTING MANTISSA
                    BNE RTS_12
                                                    ; --------------------------------
                                                    ; INCREMENT FAC MANTISSA
                                                    ; --------------------------------
INCREMENT_FAC_MANTISSA
                    INC FAC+4                       ; ADD CARRY FROM EXTRA
                    BNE RTS_12
                    INC FAC+3
                    BNE RTS_12
                    INC FAC+2
                    BNE RTS_12
                    INC FAC+1
RTS_12              RTS
                                                    ; --------------------------------
OVERFLOW
                    LDX #ERR_OVERFLOW
                    JMP ERROR
                                                    ; --------------------------------
                                                    ; SHIFT 1,X THRU 5,X RIGHT
                                                    ; (A) = NEGATIVE OF SHIFT COUNT
                                                    ; (X) = POINTER TO BYTES TO BE SHIFTED
                                                    ; 
                                                    ; RETURN WITH (Y)=0, CARRY=0, EXTENSION BITS IN A-REG
                                                    ; --------------------------------
SHIFT_RIGHT_1
                    LDX #RESULT-1                   ; SHIFT RESULT RIGHT
SHIFT_RIGHT_2                                       ; 
                    LDY 4,X                         ; SHIFT 8 BITS RIGHT
                    STY FAC_EXTENSION               ; 
                    LDY 3,X                         ; 
                    STY 4,X                         ; 
                    LDY 2,X                         ; 
                    STY 3,X                         ; 
                    LDY 1,X                         ; 
                    STY 2,X                         ; 
                    LDY SHIFT_SIGN_EXT              ; $00 IF +, $FF IF -
                    STY 1,X
                                                    ; --------------------------------
                                                    ; MAIN ENTRY TO RIGHT SHIFT SUBROUTINE
                                                    ; --------------------------------
SHIFT_RIGHT
                    ADC #8
                    BMI SHIFT_RIGHT_2               ; STILL MORE THAN 8 BITS TO GO
                    BEQ SHIFT_RIGHT_2               ; EXACTLY 8 MORE BITS TO GO
                    SBC #8                          ; UNDO ADC ABOVE
                    TAY                             ; REMAINING SHIFT COUNT
                    LDA FAC_EXTENSION               ; 
                    BCS SHIFT_RIGHT_5               ; FINISHED SHIFTING
SHIFT_RIGHT_3                                       ; 
L                   ASL 1,X                         ; SIGN -> CARRY (SIGN EXTENSION)
                    BCC ?1                          ; SIGN +
                    INC 1,X                         ; PUT SIGN IN LSB
?1                  ROR 1,X                         ; RESTORE VALUE, SIGN STILL IN CARRY
                    ROR 1,X                         ; START RIGHT SHIFT, INSERTING SIGN
                                                    ; --------------------------------
                                                    ; ENTER HERE FOR SHORT SHIFTS WITH NO SIGN EXTENSION
                                                    ; --------------------------------
SHIFT_RIGHT_4
                    ROR 2,X
                    ROR 3,X
                    ROR 4,X
                    ROR                             ; EXTENSION
                    INY                             ; COUNT THE SHIFT
                    BNE SHIFT_RIGHT_3               ; 
SHIFT_RIGHT_5                                       ; 
                    CLC                             ; RETURN WITH CARRY CLEAR
                    RTS
                                                    ; --------------------------------
                                                    ; --------------------------------

CON_ONE             ASM_DATA($81,$00,$00,$00,$00)
                                                    ; --------------------------------
POLY_LOG            ASM_DATA(3)                         ; # OF COEFFICIENTS - 1
                    ASM_DATA($7F,$5E,$56,$CB,$79)       ; * X^7 +
                    ASM_DATA($80,$13,$9B,$0B,$64)       ; * X^5 +
                    ASM_DATA($80,$76,$38,$93,$16)       ; * X^3 +
                    ASM_DATA($82,$38,$AA,$3B,$20)       ; * X
                                                    ; --------------------------------

CON_SQR_HALF        ASM_DATA($80,$35,$04,$F3,$34)
CON_SQR_TWO         ASM_DATA($81,$35,$04,$F3,$34)
CON_NEG_HALF        ASM_DATA($80,$80,$00,$00,$00)
CON_LOG_TWO         ASM_DATA($80,$31,$72,$17,$F8)
                                                    ; --------------------------------
                                                    ; "LOG" FUNCTION
                                                    ; --------------------------------
LOG                 JSR SIGN                        ; GET -1,0,+1 IN A-REG FOR FAC
                    BEQ GIQ                         ; LOG (0) IS ILLEGAL
                    BPL LOG_2                       ; >0 IS OK
GIQ                 JMP IQERR                       ; <= 0 IS NO GOOD
LOG_2               LDA FAC                         ; FIRST GET LOG BASE 2
                    SBC #$7F                        ; SAVE UNBIASED EXPONENT
                    PHA                             ; 
                    LDA #$80                        ; NORMALIZE BETWEEN ?5 AND 1
                    STA FAC
                    LDA #<CON_SQR_HALF
                    LDY #>CON_SQR_HALF
                    JSR FADD                        ; COMPUTE VIA SERIES OF ODD
                    LDA #<CON_SQR_TWO               ; POWERS OF
                    LDY #>CON_SQR_TWO               ; (SQR(2)X-1)/(SQR(2)X+1)
                    JSR FDIV
                    LDA #<CON_ONE
                    LDY #>CON_ONE
                    JSR FSUB
                    LDA #<POLY_LOG
                    LDY #>POLY_LOG
                    JSR POLYNOMIAL_ODD
                    LDA #<CON_NEG_HALF
                    LDY #>CON_NEG_HALF
                    JSR FADD
                    PLA
                    JSR ADDACC                      ; ADD ORIGINAL EXPONENT
                    LDA #<CON_LOG_TWO               ; MULTIPLY BY LOG(2) TO FORM
                    LDY #>CON_LOG_TWO               ; NATURAL LOG OF X
                                                    ; --------------------------------
                                                    ; FAC = (Y,A) * FAC
                                                    ; --------------------------------
FMULT               JSR LOAD_ARG_FROM_YA
                                                    ; --------------------------------
                                                    ; FAC = ARG * FAC
                                                    ; --------------------------------
FMULTT              BNE ?1                          ; FAC .NE. ZERO
                    JMP RTS_13                      ; FAC = 0 * ARG = 0
                                                    ; <<< WHY IS LINE ABOVE JUST "RTS"? >>>
                                                    ; --------------------------------
                                                    ; 
                                                    ; --------------------------------
?1                  JSR ADD_EXPONENTS
                    LDA #0
                    STA RESULT                      ; INIT PRODUCT = 0
                    STA RESULT+1
                    STA RESULT+2
                    STA RESULT+3
                    LDA FAC_EXTENSION
                    JSR MULTIPLY_1
                    LDA FAC+4
                    JSR MULTIPLY_1
                    LDA FAC+3
                    JSR MULTIPLY_1
                    LDA FAC+2
                    JSR MULTIPLY_1
                    LDA FAC+1
                    JSR MULTIPLY_2
                    JMP COPY_RESULT_INTO_FAC
                                                    ; --------------------------------
                                                    ; MULTIPLY ARG BY (A) INTO RESULT
                                                    ; --------------------------------
MULTIPLY_1
                    BNE MULTIPLY_2                  ; THIS BYTE NON-ZERO
                    JMP SHIFT_RIGHT_1               ; (A)=0, JUST SHIFT ARG RIGHT 8
                                                    ; --------------------------------
MULTIPLY_2                                          ; 
                    LSR                             ; SHIFT BIT INTO CARRY
                    ORA #$80                        ; SUPPLY SENTINEL BIT
?1                  TAY                             ; REMAINING MULTIPLIER TO Y
                    BCC ?2                          ; THIS MULTIPLIER BIT = 0
                    CLC                             ; = 1, SO ADD ARG TO RESULT
                    LDA RESULT+3
                    ADC ARG+4
                    STA RESULT+3
                    LDA RESULT+2
                    ADC ARG+3
                    STA RESULT+2
                    LDA RESULT+1
                    ADC ARG+2
                    STA RESULT+1
                    LDA RESULT
                    ADC ARG+1
                    STA RESULT
?2                  ROR RESULT                      ; SHIFT RESULT RIGHT 1
                    ROR RESULT+1                    ; 
                    ROR RESULT+2                    ; 
                    ROR RESULT+3                    ; 
                    ROR FAC_EXTENSION               ; 
                    TYA                             ; REMAINING MULTIPLIER
                    LSR                             ; LSB INTO CARRY
                    BNE ?1                          ; IF SENTINEL STILL HERE, MULTIPLY
RTS_13              RTS                             ; 8 X 32 COMPLETED
                                                    ; --------------------------------
                                                    ; UNPACK NUMBER AT (Y,A) INTO ARG
                                                    ; --------------------------------
LOAD_ARG_FROM_YA
                    STA INDEX                       ; USE INDEX FOR PNTR
                    STY INDEX+1                     ; 
                    LDY #4                          ; FIVE BYTES TO MOVE
                    LDA (INDEX),Y                   ; 
                    STA ARG+4                       ; 
                    DEY                             ; 
                    LDA (INDEX),Y                   ; 
                    STA ARG+3                       ; 
                    DEY                             ; 
                    LDA (INDEX),Y                   ; 
                    STA ARG+2                       ; 
                    DEY                             ; 
                    LDA (INDEX),Y                   ; 
                    STA ARG_SIGN                    ; 
                    EOR FAC_SIGN                    ; SET COMBINED SIGN FOR MULT/DIV
                    STA SGNCPR                      ; 
                    LDA ARG_SIGN                    ; TURN ON NORMALIZED INVISIBLE BIT
                    ORA #$80                        ; TO COMPLETE MANTISSA
                    STA ARG+1                       ; 
                    DEY                             ; 
                    LDA (INDEX),Y                   ; 
                    STA ARG                         ; EXPONENT
                    LDA FAC                         ; SET STATUS BITS ON FAC EXPONENT
                    RTS                             ; 
                                                    ; --------------------------------
                                                    ; ADD EXPONENTS OF ARG AND FAC
                                                    ; (CALLED BY FMULT AND FDIV)
                                                    ; 
                                                    ; ALSO CHECK FOR OVERFLOW, AND SET RESULT SIGN
                                                    ; --------------------------------
ADD_EXPONENTS
                    LDA ARG
                                                    ; --------------------------------
ADD_EXPONENTS_1
                    BEQ ZERO                        ; IF ARG=0, RESULT IS ZERO
                    CLC                             ; 
                    ADC FAC                         ; 
                    BCC ?1                          ; IN RANGE
                    BMI JOV                         ; OVERFLOW
                    CLC                             ; 
                    ASM_DATA($2C)                       ; TRICK TO SKIP
?1                  BPL ZERO                        ; OVERFLOW
                    ADC #$80                        ; RE-BIAS
                    STA FAC                         ; RESULT
                    BNE ?2
                    JMP STA_IN_FAC_SIGN             ; RESULT IS ZERO
                                                    ; <<< CRAZY TO JUMP WAY BACK THERE! >>>
                                                    ; <<< SAME IDENTICAL CODE IS BELOW! >>>
                                                    ; <<< INSTEAD OF BNE ?2, JMP STA.IN.FAC.SIGN   >>>
                                                    ; <<< ONLY NEEDED BEQ ?3            >>>
?2                  LDA SGNCPR                      ; SET SIGN OF RESULT
?3                  STA FAC_SIGN
                    RTS
                                                    ; --------------------------------
                                                    ; IF (FAC) IS POSITIVE, GIVE "OVERFLOW" ERROR
                                                    ; IF (FAC) IS NEGATIVE, SET FAC=0, POP ONE RETURN, AND RTS
                                                    ; CALLED FROM "EXP" FUNCTION
                                                    ; --------------------------------
OUTOFRNG
                    LDA FAC_SIGN
                    EOR #$FF
                    BMI JOV                         ; ERROR IF POSITIVE #
                                                    ; --------------------------------
                                                    ; POP RETURN ADDRESS AND SET FAC=0
                                                    ; --------------------------------
ZERO                PLA
                    PLA
                    JMP ZERO_FAC
                                                    ; --------------------------------
JOV                 JMP OVERFLOW
                                                    ; --------------------------------
                                                    ; MULTIPLY FAC BY 10
                                                    ; --------------------------------
MUL10               JSR COPY_FAC_TO_ARG_ROUNDED
                    TAX                             ; TEXT FAC EXPONENT
                    BEQ ?1                          ; FINISHED IF FAC=0
                    CLC                             ; 
                    ADC #2                          ; ADD 2 TO EXPONENT GIVES (FAC)*4
                    BCS JOV                         ; OVERFLOW
                    LDX #0                          ; 
                    STX SGNCPR                      ; 
                    JSR FADD_2                      ; MAKES (FAC)*5
                    INC FAC                         ; *2, MAKES (FAC)*10
                    BEQ JOV                         ; OVERFLOW
?1                  RTS
                                                    ; --------------------------------

CON_TEN             ASM_DATA($84,$20,$00,$00,$00)
                                                    ; --------------------------------
                                                    ; DIVIDE FAC BY 10
                                                    ; --------------------------------
DIV10               JSR COPY_FAC_TO_ARG_ROUNDED
                    LDA #<CON_TEN                   ; SET UP TO PUT
                    LDY #>CON_TEN                   ; 10 IN FAC
                    LDX #0
                                                    ; --------------------------------
                                                    ; FAC = ARG / (Y,A)
                                                    ; --------------------------------
DIV                 STX SGNCPR
                    JSR LOAD_FAC_FROM_YA
                    JMP FDIVT                       ; DIVIDE ARG BY FAC
                                                    ; --------------------------------
                                                    ; FAC = (Y,A) / FAC
                                                    ; --------------------------------
FDIV                JSR LOAD_ARG_FROM_YA
                                                    ; --------------------------------
                                                    ; FAC = ARG / FAC
                                                    ; --------------------------------
FDIVT               BEQ ?8                          ; FAC = 0, DIVIDE BY ZERO ERROR
                    JSR ROUND_FAC                   ; 
                    LDA #0                          ; NEGATE FAC EXPONENT, SO
                    SEC                             ; ADD.EXPONENTS FORMS DIFFERENCE
                    SBC FAC
                    STA FAC
                    JSR ADD_EXPONENTS
                    INC FAC
                    BEQ JOV                         ; OVERFLOW
                    LDX #-4                         ; INDEX FOR RESULT
                    LDA #1                          ; SENTINEL
?1                  LDY ARG+1                       ; SEE IF FAC CAN BE SUBTRACTED
                    CPY FAC+1
                    BNE ?2
                    LDY ARG+2
                    CPY FAC+2
                    BNE ?2
                    LDY ARG+3
                    CPY FAC+3
                    BNE ?2
                    LDY ARG+4
                    CPY FAC+4
?2                  PHP                             ; SAVE THE ANSWER, AND ALSO ROLL THE
                    ROL                             ; BIT INTO THE QUOTIENT, SENTINEL OUT
                    BCC ?3                          ; NO SENTINEL, STILL NOT 8 TRIPS
                    INX                             ; 8 TRIPS, STORE BYTE OF QUOTIENT
                    STA RESULT+3,X
                    BEQ ?6                          ; 32-BITS COMPLETED
                    BPL ?7                          ; FINAL EXIT WHEN X=1
                    LDA #1                          ; RE-START SENTINEL
?3                  PLP                             ; GET ANSWER, CAN FAC BE SUBTRACTED?
                    BCS ?5                          ; YES, DO IT
?4                  ASL ARG+4                       ; NO, SHIFT ARG LEFT
                    ROL ARG+3                       ; 
                    ROL ARG+2                       ; 
                    ROL ARG+1                       ; 
                    BCS ?2                          ; ANOTHER TRIP
                    BMI ?1                          ; HAVE TO COMPARE FIRST
                    BPL ?2                          ; ...ALWAYS
?5                  TAY                             ; SAVE QUOTIENT/SENTINEL BYTE
                    LDA ARG+4                       ; SUBTRACT FAC FROM ARG ONCE
                    SBC FAC+4                       ; 
                    STA ARG+4                       ; 
                    LDA ARG+3                       ; 
                    SBC FAC+3                       ; 
                    STA ARG+3                       ; 
                    LDA ARG+2                       ; 
                    SBC FAC+2                       ; 
                    STA ARG+2                       ; 
                    LDA ARG+1                       ; 
                    SBC FAC+1                       ; 
                    STA ARG+1                       ; 
                    TYA                             ; RESTORE QUOTIENT/SENTINEL BYTE
                    JMP ?4                          ; GO TO SHIFT ARG AND CONTINUE
                                                    ; --------------------------------
?6                  LDA #$40                        ; DO A FEW EXTENSION BITS
                    BNE ?3                          ; ...ALWAYS
                                                    ; --------------------------------
?7                  ASL                             ; LEFT JUSTIFY THE EXTENSION BITS WE DID
                    ASL
                    ASL
                    ASL
                    ASL
                    ASL
                    STA FAC_EXTENSION
                    PLP
                    JMP COPY_RESULT_INTO_FAC
                                                    ; --------------------------------
?8                  LDX #ERR_ZERODIV
                    JMP ERROR
                                                    ; --------------------------------
                                                    ; COPY RESULT INTO FAC MANTISSA, AND NORMALIZE
                                                    ; --------------------------------
COPY_RESULT_INTO_FAC
                    LDA RESULT
                    STA FAC+1
                    LDA RESULT+1
                    STA FAC+2
                    LDA RESULT+2
                    STA FAC+3
                    LDA RESULT+3
                    STA FAC+4
                    JMP NORMALIZE_FAC_2
                                                    ; --------------------------------
                                                    ; UNPACK (Y,A) INTO FAC
                                                    ; --------------------------------
LOAD_FAC_FROM_YA
                    STA INDEX                       ; USE INDEX FOR PNTR
                    STY INDEX+1                     ; 
                    LDY #4                          ; PICK UP 5 BYTES
                    LDA (INDEX),Y                   ; 
                    STA FAC+4                       ; 
                    DEY                             ; 
                    LDA (INDEX),Y                   ; 
                    STA FAC+3                       ; 
                    DEY                             ; 
                    LDA (INDEX),Y                   ; 
                    STA FAC+2                       ; 
                    DEY                             ; 
                    LDA (INDEX),Y                   ; 
                    STA FAC_SIGN                    ; FIRST BIT IS SIGN
                    ORA #$80                        ; SET NORMALIZED INVISIBLE BIT
                    STA FAC+1                       ; 
                    DEY                             ; 
                    LDA (INDEX),Y                   ; 
                    STA FAC                         ; EXPONENT
                    STY FAC_EXTENSION               ; Y=0
                    RTS
                                                    ; --------------------------------
                                                    ; ROUND FAC, STORE IN TEMP2
                                                    ; --------------------------------
STORE_FAC_IN_TEMP2_ROUNDED
                    LDX #TEMP2                      ; PACK FAC INTO TEMP2
                    ASM_DATA($2C)                       ; TRICK TO BRANCH
                                                    ; --------------------------------
                                                    ; ROUND FAC, STORE IN TEMP1
                                                    ; --------------------------------
STORE_FAC_IN_TEMP1_ROUNDED
                    LDX #<TEMP1                     ; PACK FAC INTO TEMP1
                    LDY #>TEMP1                     ; HI-BYTE OF TEMP1 SAME AS TEMP2
                    BEQ STORE_FACDB_YX_ROUNDED      ; ...ALWAYS
                                                    ; --------------------------------
                                                    ; ROUND FAC, AND STORE WHERE FORPNT POINTS
                                                    ; --------------------------------
SETFOR              LDX FORPNT
                    LDY FORPNT+1
                                                    ; --------------------------------
                                                    ; ROUND FAC, AND STORE AT (Y,X)
                                                    ; --------------------------------
STORE_FACDB_YX_ROUNDED
                    JSR ROUND_FAC                   ; ROUND VALUE IN FAC USING EXTENSION
                    STX INDEX                       ; USE INDEX FOR PNTR
                    STY INDEX+1                     ; 
                    LDY #4                          ; STORING 5 PACKED BYTES
                    LDA FAC+4                       ; 
                    STA (INDEX),Y                   ; 
                    DEY                             ; 
                    LDA FAC+3                       ; 
                    STA (INDEX),Y                   ; 
                    DEY                             ; 
                    LDA FAC+2                       ; 
                    STA (INDEX),Y                   ; 
                    DEY                             ; 
                    LDA FAC_SIGN                    ; PACK SIGN IN TOP BIT OF MANTISSA
                    ORA #$7F                        ; 
                    AND FAC+1                       ; 
                    STA (INDEX),Y                   ; 
                    DEY                             ; 
                    LDA FAC                         ; EXPONENT
                    STA (INDEX),Y                   ; 
                    STY FAC_EXTENSION               ; ZERO THE EXTENSION
                    RTS
                                                    ; --------------------------------
                                                    ; COPY ARG INTO FAC
                                                    ; --------------------------------
COPY_ARG_TO_FAC
                    LDA ARG_SIGN                    ; COPY SIGN
MFA                 STA FAC_SIGN                    ; 
                    LDX #5                          ; MOVE 5 BYTES
?1                  LDA ARG-1,X                     ; 
                    STA FAC-1,X                     ; 
                    DEX                             ; 
                    BNE ?1                          ; 
                    STX FAC_EXTENSION               ; ZERO EXTENSION
                    RTS                             ; 
                                                    ; --------------------------------
                                                    ; ROUND FAC AND COPY TO ARG
                                                    ; --------------------------------
COPY_FAC_TO_ARG_ROUNDED
                    JSR ROUND_FAC                   ; ROUND FAC USING EXTENSION
MAF                 LDX #6                          ; COPY 6 BYTES, INCLUDES SIGN
?1                  LDA FAC-1,X                     ; 
                    STA ARG-1,X                     ; 
                    DEX                             ; 
                    BNE ?1                          ; 
                    STX FAC_EXTENSION               ; ZERO FAC EXTENSION
RTS_14              RTS                             ; 
                                                    ; --------------------------------
                                                    ; ROUND FAC USING EXTENSION BYTE
                                                    ; --------------------------------
ROUND_FAC
                    LDA FAC
                    BEQ RTS_14                      ; FAC = 0, RETURN
                    ASL FAC_EXTENSION               ; IS FAC.EXTENSION >= 128?
                    BCC RTS_14                      ; NO, FINISHED
                                                    ; --------------------------------
                                                    ; INCREMENT MANTISSA AND RE-NORMALIZE IF CARRY
                                                    ; --------------------------------
INCREMENT_MANTISSA
                    JSR INCREMENT_FAC_MANTISSA      ; YES, INCREMENT FAC
                    BNE RTS_14                      ; HIGH BYTE HAS BITS, FINISHED
                    JMP NORMALIZE_FAC_6             ; HI-BYTE=0, SO SHIFT LEFT
                                                    ; --------------------------------
                                                    ; TEST FAC FOR ZERO AND SIGN
                                                    ; 
                                                    ; FAC > 0, RETURN +1
                                                    ; FAC = 0, RETURN  0
                                                    ; FAC < 0, RETURN -1
                                                    ; --------------------------------
SIGN                LDA FAC                         ; CHECK SIGN OF FAC AND
                    BEQ RTS_15                      ; RETURN -1,0,1 IN A-REG
                                                    ; --------------------------------
SIGN1               LDA FAC_SIGN                    ; 
                                                    ; --------------------------------
SIGN2               ROL                             ; MSBIT TO CARRY
                    LDA #$FF                        ; -1
                    BCS RTS_15                      ; MSBIT = 1
                    LDA #1                          ; +1
RTS_15              RTS                             ; 
                                                    ; --------------------------------
                                                    ; "SGN" FUNCTION
                                                    ; --------------------------------
SGN                 JSR SIGN                        ; CONVERT FAC TO -1,0,1
                                                    ; --------------------------------
                                                    ; CONVERT (A) INTO FAC, AS SIGNED VALUE -128 TO +127
                                                    ; --------------------------------
FLOAT               STA FAC+1                       ; PUT IN HIGH BYTE OF MANTISSA
                    LDA #0                          ; CLEAR 2ND BYTE OF MANTISSA
                    STA FAC+2                       ; 
                    LDX #$88                        ; USE EXPONENT 2^9
                                                    ; --------------------------------
                                                    ; FLOAT UNSIGNED VALUE IN FAC+1,2
                                                    ; (X) = EXPONENT
                                                    ; --------------------------------
FLOAT_1                                             ; 
                    LDA FAC+1                       ; MSBIT=0, SET CARRY; =1, CLEAR CARRY
                    EOR #$FF                        ; 
                    ROL                             ; 
                                                    ; --------------------------------
                                                    ; FLOAT UNSIGNED VALUE IN FAC+1,2
                                                    ; (X) = EXPONENT
                                                    ; C=0 TO MAKE VALUE NEGATIVE
                                                    ; C=1 TO MAKE VALUE POSITIVE
                                                    ; --------------------------------
FLOAT_2                                             ; 
                    LDA #0                          ; CLEAR LOWER 16-BITS OF MANTISSA
                    STA FAC+4                       ; 
                    STA FAC+3                       ; 
                    STX FAC                         ; STORE EXPONENT
                    STA FAC_EXTENSION               ; CLEAR EXTENSION
                    STA FAC_SIGN                    ; MAKE SIGN POSITIVE
                    JMP NORMALIZE_FAC_1             ; IF C=0, WILL NEGATE FAC
                                                    ; --------------------------------
                                                    ; "ABS" FUNCTION
                                                    ; --------------------------------
ABS                 LSR FAC_SIGN                    ; CHANGE SIGN TO +
                    RTS
                                                    ; --------------------------------
                                                    ; COMPARE FAC WITH PACKED # AT (Y,A)
                                                    ; RETURN A=1,0,-1 AS (Y,A) IS <,=,> FAC
                                                    ; --------------------------------
FCOMP               STA DEST                        ; USE DEST FOR PNTR
                                                    ; --------------------------------
                                                    ; SPECIAL ENTRY FROM "NEXT" PROCESSOR
                                                    ; "DEST" ALREADY SET UP
                                                    ; --------------------------------
FCOMP2              STY DEST+1                      ; 
                    LDY #0                          ; GET EXPONENT OF COMPARAND
                    LDA (DEST),Y                    ; 
                    INY                             ; POINT AT NEXT BYTE
                    TAX                             ; EXPONENT TO X-REG
                    BEQ SIGN                        ; IF COMPARAND=0, "SIGN" COMPARES FAC
                    LDA (DEST),Y                    ; GET HI-BYTE OF MANTISSA
                    EOR FAC_SIGN                    ; COMPARE WITH FAC SIGN
                    BMI SIGN1                       ; DIFFERENT SIGNS, "SIGN" GIVES ANSWER
                    CPX FAC                         ; SAME SIGN, SO COMPARE EXPONENTS
                    BNE ?1                          ; DIFFERENT, SO SUFFICIENT TEST
                    LDA (DEST),Y                    ; SAME EXPONENT, COMPARE MANTISSA
                    ORA #$80                        ; SET INVISIBLE NORMALIZED BIT
                    CMP FAC+1                       ; 
                    BNE ?1                          ; NOT SAME, SO SUFFICIENT
                    INY                             ; SAME, COMPARE MORE MANTISSA
                    LDA (DEST),Y                    ; 
                    CMP FAC+2                       ; 
                    BNE ?1                          ; NOT SAME, SO SUFFICIENT
                    INY                             ; SAME, COMPARE MORE MANTISSA
                    LDA (DEST),Y                    ; 
                    CMP FAC+3                       ; 
                    BNE ?1                          ; NOT SAME, SO SUFFICIENT
                    INY                             ; SAME, COMPARE REST OF MANTISSA
                    LDA #$7F                        ; ARTIFICIAL EXTENSION BYTE FOR COMPARAND
                    CMP FAC_EXTENSION
                    LDA (DEST),Y
                    SBC FAC+4
                    BEQ RTS_16                      ; NUMBERS ARE EQUAL, RETURN (A)=0
?1                  LDA FAC_SIGN                    ; NUMBERS ARE DIFFERENT
                    BCC ?2                          ; FAC IS LARGER MAGNITUDE
                    EOR #$FF                        ; FAC IS SMALLER MAGNITUDE
                                                    ; <<<  NOTE THAT ABOVE THREE LINES CAN BE SHORTENED: >>>
                                                    ; <<<  ?1  ROR              PUT CARRY INTO SIGN BIT  >>>
                                                    ; <<<      EOR FAC.SIGN     TOGGLE WITH SIGN OF FAC  >>>
?2                  JMP SIGN2                       ; CONVERT +1 OR -1
                                                    ; --------------------------------
                                                    ; QUICK INTEGER FUNCTION
                                                    ; 
                                                    ; CONVERTS FP VALUE IN FAC TO INTEGER VALUE
                                                    ; IN FAC+1...FAC+4, BY SHIFTING RIGHT WITH SIGN
                                                    ; EXTENSION UNTIL FRACTIONAL BITS ARE OUT.
                                                    ; 
                                                    ; THIS SUBROUTINE ASSUMES THE EXPONENT < 32.
                                                    ; --------------------------------
QINT                LDA FAC                         ; LOOK AT FAC EXPONENT
                    BEQ QINT_3                      ; FAC=0, SO FINISHED
                    SEC                             ; GET -(NUMBER OF FRACTIONAL BITS)
                    SBC #$A0                        ; IN A-REG FOR SHIFT COUNT
                    BIT FAC_SIGN                    ; CHECK SIGN OF FAC
                    BPL ?1                          ; POSITIVE, CONTINUE
                    TAX                             ; NEGATIVE, SO COMPLEMENT MANTISSA
                    LDA #$FF                        ; AND SET SIGN EXTENSION FOR SHIFT
                    STA SHIFT_SIGN_EXT
                    JSR COMPLEMENT_FAC_MANTISSA
                    TXA                             ; RESTORE BIT COUNT TO A-REG
?1                  LDX #FAC                        ; POINT SHIFT SUBROUTINE AT FAC
                    CMP #$F9                        ; MORE THAN 7 BITS TO SHIFT?
                    BPL QINT_2                      ; NO, SHORT SHIFT
                    JSR SHIFT_RIGHT                 ; YES, USE GENERAL ROUTINE
                    STY SHIFT_SIGN_EXT              ; Y=0, CLEAR SIGN EXTENSION
RTS_16              RTS
                                                    ; --------------------------------
QINT_2              TAY                             ; SAVE SHIFT COUNT
                    LDA FAC_SIGN                    ; GET SIGN BIT
                    AND #$80                        ; 
                    LSR FAC+1                       ; START RIGHT SHIFT
                    ORA FAC+1                       ; AND MERGE WITH SIGN
                    STA FAC+1
                    JSR SHIFT_RIGHT_4               ; JUMP INTO MIDDLE OF SHIFTER
                    STY SHIFT_SIGN_EXT              ; Y=0, CLEAR SIGN EXTENSION
                    RTS
                                                    ; --------------------------------
                                                    ; "INT" FUNCTION
                                                    ; 
                                                    ; USES QINT TO CONVERT (FAC) TO INTEGER FORM,
                                                    ; AND THEN REFLOATS THE INTEGER.
                                                    ; <<< A FASTER APPROACH WOULD SIMPLY CLEAR >>>
                                                    ; <<< THE FRACTIONAL BITS BY ZEROING THEM  >>>
                                                    ; --------------------------------
INT                 LDA FAC                         ; CHECK IF EXPONENT < 32
                    CMP #$A0                        ; BECAUSE IF > 31 THERE IS NO FRACTION
                    BCS RTS_17                      ; NO FRACTION, WE ARE FINISHED
                    JSR QINT                        ; USE GENERAL INTEGER CONVERSION
                    STY FAC_EXTENSION               ; Y=0, CLEAR EXTENSION
                    LDA FAC_SIGN                    ; GET SIGN OF VALUE
                    STY FAC_SIGN                    ; Y=0, CLEAR SIGN
                    EOR #$80                        ; TOGGLE ACTUAL SIGN
                    ROL                             ; AND SAVE IN CARRY
                    LDA #$A0                        ; SET EXPONENT TO 32
                    STA FAC                         ; BECAUSE 4-BYTE INTEGER NOW
                    LDA FAC+4                       ; SAVE LOW 8-BITS OF INTEGER FORM
                    STA CHARAC                      ; FOR EXP AND POWER
                    JMP NORMALIZE_FAC_1             ; NORMALIZE TO FINISH CONVERSION
                                                    ; --------------------------------
QINT_3              STA FAC+1                       ; FAC=0, SO CLEAR ALL 4 BYTES FOR
                    STA FAC+2                       ; INTEGER VERSION
                    STA FAC+3                       ; 
                    STA FAC+4                       ; 
                    TAY                             ; Y=0 TOO
RTS_17              RTS                             ; 
                                                    ; --------------------------------
                                                    ; CONVERT STRING TO FP VALUE IN FAC
                                                    ; 
                                                    ; STRING POINTED TO BY TXTPTR
                                                    ; FIRST CHAR ALREADY SCANNED BY CHRGET
                                                    ; (A) = FIRST CHAR, C=0 IF DIGIT.
                                                    ; --------------------------------
FIN                 LDY #0                          ; CLEAR WORKING AREA ($99...$A3)
                    LDX #10                         ; TMPEXP, EXPON, DPFLG, EXPSGN, FAC, SERLEN
?1                  STY TMPEXP,X
                    DEX
                    BPL ?1
                                                    ; --------------------------------
                    BCC FIN_2                       ; FIRST CHAR IS A DIGIT
                    CMP #HICHAR(`-')                        ; CHECK FOR LEADING SIGN
                    BNE ?2                          ; NOT MINUS
                    STX SERLEN                      ; MINUS, SET SERLEN = $FF FOR FLAG
                    BEQ FIN_1                       ; ...ALWAYS
?2                  CMP #HICHAR(`+')                        ; MIGHT BE PLUS
                    BNE FIN_3                       ; NOT PLUS EITHER, CHECK DECIMAL POINT
                                                    ; --------------------------------
FIN_1               JSR CHRGET                      ; GET NEXT CHAR OF STRING
                                                    ; --------------------------------
FIN_2               BCC FIN_9                       ; INSERT THIS DIGIT
                                                    ; --------------------------------
FIN_3               CMP #HICHAR(`.')                        ; CHECK FOR DECIMAL POINT
                    BEQ FIN_10                      ; YES
                    CMP #HICHAR(`E')                        ; CHECK FOR EXPONENT PART
                    BNE FIN_7                       ; NO, END OF NUMBER
                    JSR CHRGET                      ; YES, START CONVERTING EXPONENT
                    BCC FIN_5                       ; EXPONENT DIGIT
                    CMP #TOKEN_MINUS                ; NEGATIVE EXPONENT?
                    BEQ ?1                          ; YES
                    CMP #HICHAR(`-')                        ; MIGHT NOT BE TOKENIZED YET
                    BEQ ?1                          ; YES, IT IS NEGATIVE
                    CMP #TOKEN_PLUS                 ; OPTIONAL "+"
                    BEQ FIN_4                       ; YES
                    CMP #HICHAR(`+')                        ; MIGHT NOT BE TOKENIZED YET
                    BEQ FIN_4                       ; YES, FOUND "+"
                    BNE FIN_6                       ; ...ALWAYS, NUMBER COMPLETED
?1                  ROR EXPSGN                      ; C=1, SET FLAG NEGATIVE
                                                    ; --------------------------------
FIN_4               JSR CHRGET                      ; GET NEXT DIGIT OF EXPONENT
                                                    ; --------------------------------
FIN_5               BCC GETEXP                      ; CHAR IS A DIGIT OF EXPONENT
                                                    ; --------------------------------
FIN_6               BIT EXPSGN                      ; END OF NUMBER, CHECK EXP SIGN
                    BPL FIN_7                       ; POSITIVE EXPONENT
                    LDA #0                          ; NEGATIVE EXPONENT
                    SEC                             ; MAKE 2'S COMPLEMENT OF EXPONENT
                    SBC EXPON                       ; 
                    JMP FIN_8                       ; 
                                                    ; --------------------------------
                                                    ; FOUND A DECIMAL POINT
                                                    ; --------------------------------
FIN_10              ROR DPFLG                       ; C=1, SET DPFLG FOR DECIMAL POINT
                    BIT DPFLG                       ; CHECK IF PREVIOUS DEC. PT.
                    BVC FIN_1                       ; NO PREVIOUS DECIMAL POINT
                                                    ; A SECOND DECIMAL POINT IS TAKEN AS A TERMINATOR
                                                    ; TO THE NUMERIC STRING.
                                                    ; "A=11..22" WILL GIVE A SYNTAX ERROR, BECAUSE
                                                    ; IT IS TWO NUMBERS WITH NO OPERATOR BETWEEN.
                                                    ; "PRINT 11..22" GIVES NO ERROR, BECAUSE IT IS
                                                    ; JUST THE CONCATENATION OF TWO NUMBERS.
                                                    ; --------------------------------
                                                    ; NUMBER TERMINATED, ADJUST EXPONENT NOW
                                                    ; --------------------------------
FIN_7               LDA EXPON                       ; E-VALUE
FIN_8               SEC                             ; MODIFY WITH COUNT OF DIGITS
                    SBC TMPEXP                      ; AFTER THE DECIMAL POINT
                    STA EXPON                       ; COMPLETE CURRENT EXPONENT
                    BEQ ?15                         ; NO ADJUST NEEDED IF EXP=0
                    BPL ?14                         ; EXP>0, MULTIPLY BY TEN
?13                 JSR DIV10                       ; EXP<0, DIVIDE BY TEN
                    INC EXPON                       ; UNTIL EXP=0
                    BNE ?13                         ; 
                    BEQ ?15                         ; ...ALWAYS, WE ARE FINISHED
?14                 JSR MUL10                       ; EXP>0, MULTIPLY BKY TEN
                    DEC EXPON                       ; UNTIL EXP=0
                    BNE ?14                         ; 
?15                 LDA SERLEN                      ; IS WHOLE NUMBER NEGATIVE?
                    BMI ?16                         ; YES
                    RTS                             ; NO, RETURN, WHOLE JOB DONE!
?16                 JMP NEGOP                       ; NEGATIVE NUMBER, SO NEGATE FAC
                                                    ; --------------------------------
                                                    ; ACCUMULATE A DIGIT INTO FAC
                                                    ; --------------------------------
FIN_9               PHA                             ; SAVE DIGIT
                    BIT DPFLG                       ; SEEN A DECIMAL POINT YET?
                    BPL ?1                          ; NO, STILL IN INTEGER PART
                    INC TMPEXP                      ; YES, COUNT THE FRACTIONAL DIGIT
?1                  JSR MUL10                       ; FAC = FAC * 10
                    PLA                             ; CURRENT DIGIT
                    SEC                             ; <<<SHORTER HERE TO JUST "AND #$0F">>>
                    SBC #HICHAR(`0')                        ; <<<TO CONVERT ASCII TO BINARY FORM>>>
                    JSR ADDACC                      ; ADD THE DIGIT
                    JMP FIN_1                       ; GO BACK FOR MORE
                                                    ; --------------------------------
                                                    ; ADD (A) TO FAC
                                                    ; --------------------------------
ADDACC              PHA                             ; SAVE ADDEND
                    JSR COPY_FAC_TO_ARG_ROUNDED
                    PLA                             ; GET ADDEND AGAIN
                    JSR FLOAT                       ; CONVERT TO FP VALUE IN FAC
                    LDA ARG_SIGN                    ; 
                    EOR FAC_SIGN                    ; 
                    STA SGNCPR                      ; 
                    LDX FAC                         ; TO SIGNAL IF FAC=0
                    JMP FADDT                       ; PERFORM THE ADDITION
                                                    ; --------------------------------
                                                    ; ACCUMULATE DIGIT OF EXPONENT
                                                    ; --------------------------------
GETEXP              LDA EXPON                       ; CHECK CURRENT VALUE
                    CMP #10                         ; FOR MORE THAN 2 DIGITS
                    BCC ?1                          ; NO, THIS IS 1ST OR 2ND DIGIT
                    LDA #100                        ; EXPONENT TOO BIG
                    BIT EXPSGN                      ; UNLESS IT IS NEGATIVE
                    BMI ?2                          ; LARGE NEGATIVE EXPONENT MAKES FAC=0
                    JMP OVERFLOW                    ; LARGE POSITIVE EXPONENT IS ERROR
?1                  ASL                             ; EXPONENT TIMES 10
                    ASL                             ; 
                    CLC                             ; 
                    ADC EXPON                       ; 
                    ASL                             ; 
                    CLC                             ; <<< ASL ALREADY DID THIS! >>>
                    LDY #0                          ; ADD THE NEW DIGIT
                    ADC (TXTPTR),Y                  ; BUT THIS IS IN ASCII,
                    SEC                             ; SO ADJUST BACK TO BINARY
                    SBC #HICHAR(`0')
?2                  STA EXPON                       ; NEW VALUE
                    JMP FIN_4                       ; BACK FOR MORE
                                                    ; --------------------------------
                                                    ; --------------------------------

CON_99999999P9      ASM_DATA($9B,$3E,$BC,$1F,$FD)       ; 99,999,999.9
CON_999999999       ASM_DATA($9E,$6E,$6B,$27,$FD)       ; 999,999,999
CON_BILLION         ASM_DATA($9E,$6E,$6B,$28,$00)       ; 1,000,000,000
                                                    ; --------------------------------
                                                    ; PRINT "IN <LINE #>"
                                                    ; --------------------------------
INPRT               LDA #<QT_IN                     ; PRINT " IN "
                    LDY #>QT_IN
                    JSR GO_STROUT
                    LDA CURLIN+1
                    LDX CURLIN
                                                    ; --------------------------------
                                                    ; PRINT A,X AS DECIMAL INTEGER
                                                    ; --------------------------------
LINPRT              STA FAC+1                       ; PRINT A,X IN DECIMAL
                    STX FAC+2                       ; 
                    LDX #$90                        ; EXPONENT = 2^16
                    SEC                             ; CONVERT UNSIGNED
                    JSR FLOAT_2                     ; CONVERT LINE # TO FP
                                                    ; --------------------------------
                                                    ; CONVERT (FAC) TO STRING, AND PRINT IT
                                                    ; --------------------------------
PRINT_FAC                                           ; 
                    JSR FOUT                        ; CONVERT (FAC) TO STRING AT STACK
                                                    ; --------------------------------
                                                    ; PRINT STRING STARTING AT Y,A
                                                    ; --------------------------------
GO_STROUT                                           ; 
                    JMP STROUT                      ; PRINT STRING AT A,Y
                                                    ; --------------------------------
                                                    ; CONVERT (FAC) TO STRING STARTING AT STACK
                                                    ; RETURN WITH (Y,A) POINTING AT STRING
                                                    ; --------------------------------
FOUT                LDY #1                          ; NORMAL ENTRY PUTS STRING AT STACK...
                                                    ; --------------------------------
                                                    ; "STR$" FUNCTION ENTERS HERE, WITH (Y)=0
                                                    ; SO THAT RESULT STRING STARTS AT STACK-1
                                                    ; (THIS IS USED AS A FLAG)
                                                    ; --------------------------------
FOUT_1              LDA #HICHAR(`-')                        ; IN CASE VALUE NEGATIVE
                    DEY                             ; BACK UP PNTR
                    BIT FAC_SIGN                    ; 
                    BPL ?1                          ; VALUE IS +
                    INY                             ; VALUE IS -
                    STA STACK-1,Y                   ; EMIT "-"
?1                  STA FAC_SIGN                    ; MAKE FAC.SIGN POSITIVE ($2D)
                    STY STRNG2                      ; SAVE STRING PNTR
                    INY                             ; 
                    LDA #HICHAR(`0')                        ; IN CASE (FAC)=0
                    LDX FAC                         ; NUMBER=0?
                    BNE ?2                          ; NO, (FAC) NOT ZERO
                    JMP FOUT_4                      ; YES, FINISHED
                                                    ; --------------------------------
?2                  LDA #0                          ; STARTING VALUE FOR TMPEXP
                    CPX #$80                        ; ANY INTEGER PART?
                    BEQ ?3                          ; NO, BTWN ?5 AND ?999999999
                    BCS ?4                          ; YES
                                                    ; --------------------------------
?3                  LDA #<CON_BILLION               ; MULTIPLY BY 1E9
                    LDY #>CON_BILLION               ; TO GIVE ADJUSTMENT A HEAD START
                    JSR FMULT                       ; 
                    LDA #-9                         ; EXPONENT ADJUSTMENT
?4                  STA TMPEXP                      ; 0 OR -9
                                                    ; --------------------------------
                                                    ; ADJUST UNTIL 1E8 <= (FAC) <1E9
                                                    ; --------------------------------
?5                  LDA #<CON_999999999
                    LDY #>CON_999999999
                    JSR FCOMP                       ; COMPARE TO 1E9-1
                    BEQ ?10                         ; (FAC) = 1E9-1
                    BPL ?8                          ; TOO LARGE, DIVIDE BY TEN
?6                  LDA #<CON_99999999P9            ; COMPARE TO 1E8-?1
                    LDY #>CON_99999999P9
                    JSR FCOMP                       ; COMPARE TO 1E8-?1
                    BEQ ?7                          ; (FAC) = 1E8-?1
                    BPL ?9                          ; IN RANGE, ADJUSTMENT FINISHED
?7                  JSR MUL10                       ; TOO SMALL, MULTIPLY BY TEN
                    DEC TMPEXP                      ; KEEP TRACK OF MULTIPLIES
                    BNE ?6                          ; ...ALWAYS
?8                  JSR DIV10                       ; TOO LARGE, DIVIDE BY TEN
                    INC TMPEXP                      ; KEEP TRACK OF DIVISIONS
                    BNE ?5                          ; ...ALWAYS
                                                    ; --------------------------------
?9                  JSR FADDH                       ; ROUND ADJUSTED RESULT
?10                 JSR QINT                        ; CONVERT ADJUSTED VALUE TO 32-BIT INTEGER
                                                    ; --------------------------------
                                                    ; FAC+1...FAC+4 IS NOW IN INTEGER FORM
                                                    ; WITH POWER OF TEN ADJUSTMENT IN TMPEXP
                                                    ; 
                                                    ; IF -10 < TMPEXP > 1, PRINT IN DECIMAL FORM
                                                    ; OTHERWISE, PRINT IN EXPONENTIAL FORM
                                                    ; --------------------------------
FOUT_2              LDX #1                          ; ASSUME 1 DIGIT BEFORE "."
                    LDA TMPEXP                      ; CHECK RANGE
                    CLC                             ; 
                    ADC #10                         ; 
                    BMI ?1                          ; < .01, USE EXPONENTIAL FORM
                    CMP #11                         ; 
                    BCS ?2                          ; >= 1E10, USE EXPONENTIAL FORM
                    ADC #$FF                        ; LESS 1 GIVES INDEX FOR "."
                    TAX                             ; 
                    LDA #2                          ; SET REMAINING EXPONENT = 0
?1                  SEC                             ; COMPUTE REMAINING EXPONENT
?2                  SBC #2                          ; 
                    STA EXPON                       ; VALUE FOR "E+XX" OR "E-XX"
                    STX TMPEXP                      ; INDEX FOR DECIMAL POINT
                    TXA                             ; SEE IF "." COMES FIRST
                    BEQ ?3                          ; YES
                    BPL ?5                          ; NO, LATER
?3                  LDY STRNG2                      ; GET INDEX INTO STRING BEING BUILT
                    LDA #HICHAR(`.')                        ; STORE A DECIMAL POINT
                    INY                             ; 
                    STA STACK-1,Y                   ; 
                    TXA                             ; SEE IF NEED ".0"
                    BEQ ?4                          ; NO
                    LDA #HICHAR(`0')                        ; YES, STORE "0"
                    INY                             ; 
                    STA STACK-1,Y                   ; 
?4                  STY STRNG2                      ; SAVE OUTPUT INDEX AGAIN
                                                    ; --------------------------------
                                                    ; NOW DIVIDE BY POWERS OF TEN TO GET SUCCESSIVE DIGITS
                                                    ; --------------------------------
?5                  LDY #0                          ; INDEX TO TABLE OF POWERS OF TEN
                    LDX #$80                        ; STARTING VALUE FOR DIGIT WITH DIRECTION
?6                  LDA FAC+4                       ; START BY ADDING -100000000 UNTIL
                    CLC                             ; OVERSHOOT.  THEN ADD +10000000,
                    ADC DECTBL+3,Y                  ; THEN ADD -1000000, THEN ADD
                    STA FAC+4                       ; +100000, AND SO ON.
                    LDA FAC+3                       ; THE # OF TIMES EACH POWER IS ADDED
                    ADC DECTBL+2,Y                  ; IS 1 MORE THAN CORRESPONDING DIGIT
                    STA FAC+3
                    LDA FAC+2
                    ADC DECTBL+1,Y
                    STA FAC+2
                    LDA FAC+1
                    ADC DECTBL,Y
                    STA FAC+1
                    INX                             ; COUNT THE ADD
                    BCS ?7                          ; IF C=1 AND X NEGATIVE, KEEP ADDING
                    BPL ?6                          ; IF C=0 AND X POSITIVE, KEEP ADDING
                    BMI ?8                          ; IF C=0 AND X NEGATIVE, WE OVERSHOT
?7                  BMI ?6                          ; IF C=1 AND X POSITIVE, WE OVERSHOT
?8                  TXA                             ; OVERSHOT, SO MAKE X INTO A DIGIT
                    BCC ?9                          ; HOW DEPENDS ON DIRECTION WE WERE GOING
                    EOR #$FF                        ; DIGIT = 9-X
                    ADC #10                         ; 
?9                  ADC #HICHAR(`0')-1                      ; MAKE DIGIT INTO ASCII
                    INY                             ; ADVANCE TO NEXT SMALLER POWER OF TEN
                    INY                             ; 
                    INY                             ; 
                    INY                             ; 
                    STY VARPNT                      ; SAVE PNTR TO POWERS
                    LDY STRNG2                      ; GET OUTPUT PNTR
                    INY                             ; STORE THE DIGIT
                    TAX                             ; SAVE DIGIT, HI-BIT IS DIRECTION
                    AND #$7F                        ; MAKE SURE $30...$39 FOR STRING
                    STA STACK-1,Y                   ; 
                    DEC TMPEXP                      ; COUNT THE DIGIT
                    BNE ?10                         ; NOT TIME FOR "." YET
                    LDA #HICHAR(`.')                        ; TIME, SO STORE THE DECIMAL POINT
                    INY                             ; 
                    STA STACK-1,Y                   ; 
?10                 STY STRNG2                      ; SAVE OUTPUT PNTR AGAIN
                    LDY VARPNT                      ; GET PNTR TO POWERS
                    TXA                             ; GET DIGIT WITH HI-BIT = DIRECTION
                    EOR #$FF                        ; CHANGE DIRECTION
                    AND #$80                        ; $00 IF ADDING, $80 IF SUBTRACTING
                    TAX
                    CPY #DECTBL_END-DECTBL
                    BNE ?6                          ; NOT FINISHED YET
                                                    ; --------------------------------
                                                    ; NINE DIGITS HAVE BEEN STORED IN STRING.  NOW LOOK
                                                    ; BACK AND LOP OFF TRAILING ZEROES AND A TRAILING
                                                    ; DECIMAL POINT.
                                                    ; --------------------------------
FOUT_3              LDY STRNG2                      ; POINTS AT LAST STORED CHAR
?1                  LDA STACK-1,Y                   ; SEE IF LOPPABLE
                    DEY                             ; 
                    CMP #HICHAR(`0')                        ; SUPPRESS TRAILING ZEROES
                    BEQ ?1                          ; YES, KEEP LOOPING
                    CMP #HICHAR(`.')                        ; SUPPRESS TRAILING DECIMAL POINT
                    BEQ ?2                          ; ".", SO WRITE OVER IT
                    INY                             ; NOT ".", SO INCLUDE IN STRING AGAIN
?2                  LDA #HICHAR(`+')                        ; PREPARE FOR POSITIVE EXPONENT "E+XX"
                    LDX EXPON                       ; SEE IF ANY E-VALUE
                    BEQ FOUT_5                      ; NO, JUST MARK END OF STRING
                    BPL ?3                          ; YES, AND IT IS POSITIVE
                    LDA #0                          ; YES, AND IT IS NEGATIVE
                    SEC                             ; COMPLEMENT THE VALUE
                    SBC EXPON                       ; 
                    TAX                             ; GET MAGNITUDE IN X
                    LDA #HICHAR(`-')                        ; E SIGN
?3                  STA STACK+1,Y                   ; STORE SIGN IN STRING
                    LDA #HICHAR(`E')                        ; STORE "E" IN STRING BEFORE SIGN
                    STA STACK,Y                     ; 
                    TXA                             ; EXPONENT MAGNITUDE IN A-REG
                    LDX #HICHAR(`0')-1                      ; SEED FOR EXPONENT DIGIT
                    SEC                             ; CONVERT TO DECIMAL
?4                  INX                             ; COUNT THE SUBTRACTION
                    SBC #10                         ; TEN'S DIGIT
                    BCS ?4                          ; MORE TENS TO SUBTRACT
                    ADC #HICHAR(`0')+10                     ; CONVERT REMAINDER TO ONE'S DIGIT
                    STA STACK+3,Y                   ; STORE ONE'S DIGIT
                    TXA                             ; 
                    STA STACK+2,Y                   ; STORE TEN'S DIGIT
                    LDA #0                          ; MARK END OF STRING WITH $00
                    STA STACK+4,Y                   ; 
                    BEQ FOUT_6                      ; ...ALWAYS
FOUT_4              STA STACK-1,Y                   ; STORE "0" IN ASCII
FOUT_5              LDA #0                          ; STORE $00 ON END OF STRING
                    STA STACK,Y                     ; 
FOUT_6              LDA #<STACK                     ; POINT Y,A AT BEGINNING OF STRING
                    LDY #>STACK                     ; (STR$ STARTED STRING AT STACK-1, BUT
                    RTS                             ; STR$ DOESN'T USE Y,A ANYWAY.)
                                                    ; --------------------------------

CON_HALF            ASM_DATA($80,$00,$00,$00,$00)       ; FP CONSTANT 0?5
                                                    ; --------------------------------
                                                    ; POWERS OF 10 FROM 1E8 DOWN TO 1,
                                                    ; AS 32-BIT INTEGERS, WITH ALTERNATING SIGNS
                                                    ; --------------------------------

DECTBL              ASM_DATA($FA,$0A,$1F,$00)           ; -100000000
                    ASM_DATA($00,$98,$96,$80)           ; 10000000
                    ASM_DATA($FF,$F0,$BD,$C0)           ; -1000000
                    ASM_DATA($00,$01,$86,$A0)           ; 100000
                    ASM_DATA($FF,$FF,$D8,$F0)           ; -10000
                    ASM_DATA($00,$00,$03,$E8)           ; 1000
                    ASM_DATA($FF,$FF,$FF,$9C)           ; -100
                    ASM_DATA($00,$00,$00,$0A)           ; 10
                    ASM_DATA($FF,$FF,$FF,$FF)           ; -1
DECTBL_END
                                                    ; --------------------------------
                                                    ; --------------------------------
                                                    ; "SQR" FUNCTION
                                                    ; 
                                                    ; <<< UNFORTUNATELY, RATHER THAN A NEWTON-RAPHSON >>>
                                                    ; <<< ITERATION, APPLESOFT USES EXPONENTIATION    >>>
                                                    ; <<< SQR(X) = X^?5                               >>>
                                                    ; --------------------------------
SQR                 JSR COPY_FAC_TO_ARG_ROUNDED
                    LDA #<CON_HALF                  ; SET UP POWER OF 0?5
                    LDY #>CON_HALF
                    JSR LOAD_FAC_FROM_YA
                                                    ; --------------------------------
                                                    ; EXPONENTIATION OPERATION
                                                    ; 
                                                    ; ARG ^ FAC  =  EXP( LOG(ARG) * FAC )
                                                    ; --------------------------------
FPWRT               BEQ EXP                         ; IF FAC=0, ARG^FAC=EXP(0)
                    LDA ARG                         ; IF ARG=0, ARG^FAC=0
                    BNE ?1                          ; NEITHER IS ZERO
                    JMP STA_IN_FAC_SIGN_AND_EXP     ; SET FAC = 0
?1                  LDX #TEMP3                      ; SAVE FAC IN TEMP3
                    LDY #0
                    JSR STORE_FACDB_YX_ROUNDED
                    LDA ARG_SIGN                    ; NORMALLY, ARG MUST BE POSITIVE
                    BPL ?2                          ; IT IS POSITIVE, SO ALL IS WELL
                    JSR INT                         ; NEGATIVE, BUT OK IF INTEGRAL POWER
                    LDA #TEMP3                      ; SEE IF INT(FAC)=FAC
                    LDY #0                          ; 
                    JSR FCOMP                       ; IS IT AN INTEGER POWER?
                    BNE ?2                          ; NOT INTEGRAL,  WILL CAUSE ERROR LATER
                    TYA                             ; MAKE ARG SIGN + AS IT IS MOVED TO FAC
                    LDY CHARAC                      ; INTEGRAL, SO ALLOW NEGATIVE ARG
?2                  JSR MFA                         ; MOVE ARGUMENT TO FAC
                    TYA                             ; SAVE FLAG FOR NEGATIVE ARG (0=+)
                    PHA                             ; 
                    JSR LOG                         ; GET LOG(ARG)
                    LDA #TEMP3                      ; MULTIPLY BY POWER
                    LDY #0                          ; 
                    JSR FMULT                       ; 
                    JSR EXP                         ; E ^ LOG(FAC)
                    PLA                             ; GET FLAG FOR NEGATIVE ARG
                    LSR                             ; <<<LSR,BCC COULD BE MERELY BPL>>>
                    BCC RTS_18                      ; NOT NEGATIVE, FINISHED
                                                    ; NEGATIVE ARG, SO NEGATE RESULT
                                                    ; --------------------------------
                                                    ; NEGATE VALUE IN FAC
                                                    ; --------------------------------
NEGOP               LDA FAC                         ; IF FAC=0, NO NEED TO COMPLEMENT
                    BEQ RTS_18                      ; YES, FAC=0
                    LDA FAC_SIGN                    ; NO, SO TOGGLE SIGN
                    EOR #$FF
                    STA FAC_SIGN
RTS_18              RTS
                                                    ; --------------------------------

CON_LOG_E           ASM_DATA($81,$38,$AA,$3B,$29)       ; LOG(E) TO BASE 2
                                                    ; --------------------------------
POLY_EXP            ASM_DATA(7)                         ; ( # OF TERMS IN POLYNOMIAL) - 1
                    ASM_DATA($71,$34,$58,$3E,$56)       ; (LOG(2)^7)/8!
                    ASM_DATA($74,$16,$7E,$B3,$1B)       ; (LOG(2)^6)/7!
                    ASM_DATA($77,$2F,$EE,$E3,$85)       ; (LOG(2)^5)/6!
                    ASM_DATA($7A,$1D,$84,$1C,$2A)       ; (LOG(2)^4)/5!
                    ASM_DATA($7C,$63,$59,$58,$0A)       ; (LOG(2)^3)/4!
                    ASM_DATA($7E,$75,$FD,$E7,$C6)       ; (LOG(2)^2)/3!
                    ASM_DATA($80,$31,$72,$18,$10)       ; LOG(2)/2!
                    ASM_DATA($81,$00,$00,$00,$00)       ; 1
                                                    ; --------------------------------
                                                    ; "EXP" FUNCTION
                                                    ; 
                                                    ; FAC = E ^ FAC
                                                    ; --------------------------------
EXP                 LDA #<CON_LOG_E                 ; CONVERT TO POWER OF TWO PROBLEM
                    LDY #>CON_LOG_E                 ; E^X = 2^(LOG2(E)*X)
                    JSR FMULT                       ; 
                    LDA FAC_EXTENSION               ; NON-STANDARD ROUNDING HERE
                    ADC #$50                        ; ROUND UP IF EXTENSION > $AF
                    BCC ?1                          ; NO, DON'T ROUND UP
                    JSR INCREMENT_MANTISSA
?1                  STA ARG_EXTENSION               ; STRANGE VALUE
                    JSR MAF                         ; COPY FAC INTO ARG
                    LDA FAC                         ; MAXIMUM EXPONENT IS < 128
                    CMP #$88                        ; WITHIN RANGE?
                    BCC ?3                          ; YES
?2                  JSR OUTOFRNG                    ; OVERFLOW IF +, RETURN 0.0 IF -
?3                  JSR INT                         ; GET INT(FAC)
                    LDA CHARAC                      ; THIS IS THE INETGRAL PART OF THE POWER
                    CLC                             ; ADD TO EXPONENT BIAS + 1
                    ADC #$81                        ; 
                    BEQ ?2                          ; OVERFLOW
                    SEC                             ; BACK OFF TO NORMAL BIAS
                    SBC #1                          ; 
                    PHA                             ; SAVE EXPONENT
                                                    ; --------------------------------
                    LDX #5                          ; SWAP ARG AND FAC
?4                  LDA ARG,X                       ; <<< WHY SWAP? IT IS DOING      >>>
                    LDY FAC,X                       ; <<< -(A-B) WHEN (B-A) IS THE   >>>
                    STA FAC,X                       ; <<< SAME THING!                >>>
                    STY ARG,X
                    DEX
                    BPL ?4
                    LDA ARG_EXTENSION
                    STA FAC_EXTENSION
                    JSR FSUBT                       ; POWER-INT(POWER) --> FRACTIONAL PART
                    JSR NEGOP
                    LDA #<POLY_EXP
                    LDY #>POLY_EXP
                    JSR POLYNOMIAL                  ; COMPUTE F(X) ON FRACTIONAL PART
                    LDA #0
                    STA SGNCPR
                    PLA                             ; GET EXPONENT
                    JSR ADD_EXPONENTS_1
                    RTS                             ; <<< WASTED BYTE HERE, COULD HAVE >>>
                                                    ; <<< JUST USED "JMP ADD.EXPO..."  >>>
                                                    ; --------------------------------
                                                    ; ODD POLYNOMIAL SUBROUTINE
                                                    ; 
                                                    ; F(X) = X * P(X^2)
                                                    ; 
                                                    ; WHERE:  X IS VALUE IN FAC
                                                    ; Y,A POINTS AT COEFFICIENT TABLE
                                                    ; FIRST BYTE OF COEFF. TABLE IS N
                                                    ; COEFFICIENTS FOLLOW, HIGHEST POWER FIRST
                                                    ; 
                                                    ; P(X^2) COMPUTED USING NORMAL POLYNOMIAL SUBROUTINE
                                                    ; 
                                                    ; --------------------------------
POLYNOMIAL_ODD
                    STA SERPNT                      ; SAVE ADDRESS OF COEFFICIENT TABLE
                    STY SERPNT+1
                    JSR STORE_FAC_IN_TEMP1_ROUNDED
                    LDA #TEMP1                      ; Y=0 ALREADY, SO Y,A POINTS AT TEMP1
                    JSR FMULT                       ; FORM X^2
                    JSR SERMAIN                     ; DO SERIES IN X^2
                    LDA #<TEMP1                     ; GET X AGAIN
                    LDY #>TEMP1                     ; 
                    JMP FMULT                       ; MULTIPLY X BY P(X^2) AND EXIT
                                                    ; --------------------------------
                                                    ; NORMAL POLYNOMIAL SUBROUTINE
                                                    ; 
                                                    ; P(X) = C(0)*X^N + C(1)*X^(N-1) + ... + C(N)
                                                    ; 
                                                    ; WHERE:  X IS VALUE IN FAC
                                                    ; Y,A POINTS AT COEFFICIENT TABLE
                                                    ; FIRST BYTE OF COEFF. TABLE IS N
                                                    ; COEFFICIENTS FOLLOW, HIGHEST POWER FIRST
                                                    ; 
                                                    ; --------------------------------
POLYNOMIAL
                    STA SERPNT                      ; POINTER TO COEFFICIENT TABLE
                    STY SERPNT+1
                                                    ; --------------------------------
SERMAIN
                    JSR STORE_FAC_IN_TEMP2_ROUNDED
                    LDA (SERPNT),Y                  ; GET N
                    STA SERLEN                      ; SAVE N
                    LDY SERPNT                      ; BUMP PNTR TO HIGHEST COEFFICIENT
                    INY                             ; AND GET PNTR INTO Y,A
                    TYA
                    BNE ?1
                    INC SERPNT+1
?1                  STA SERPNT
                    LDY SERPNT+1
?2                  JSR FMULT                       ; ACCUMULATE SERIES TERMS
                    LDA SERPNT                      ; BUMP PNTR TO NEXT COEFFICIENT
                    LDY SERPNT+1
                    CLC
                    ADC #5
                    BCC ?3
                    INY
?3                  STA SERPNT
                    STY SERPNT+1
                    JSR FADD                        ; ADD NEXT COEFFICIENT
                    LDA #TEMP2                      ; POINT AT X AGAIN
                    LDY #0                          ; 
                    DEC SERLEN                      ; IF SERIES NOT FINISHED,
                    BNE ?2                          ; THEN ADD ANOTHER TERM
RTS_19              RTS                             ; FINISHED
                                                    ; --------------------------------

CON_RND_1           ASM_DATA($98,$35,$44,$7A)           ; <<< THESE ARE MISSING ONE BYTE >>>
CON_RND_2           ASM_DATA($68,$28,$B1,$46)           ; <<< FOR FP VALUES              >>>
                                                    ; --------------------------------
                                                    ; "RND" FUNCTION
                                                    ; --------------------------------
RND                 JSR SIGN                        ; REDUCE ARGUMENT TO -1, 0, OR +1
                    TAX                             ; SAVE ARGUMENT
                    BMI ?1                          ; = -1, USE CURRENT ARGUMENT FOR SEED
                    LDA #<RNDSEED                   ; USE CURRENT SEED
                    LDY #>RNDSEED
                    JSR LOAD_FAC_FROM_YA
                    TXA                             ; RECALL SIGN OF ARGUMENT
                    BEQ RTS_19                      ; =0, RETURN SEED UNCHANGED
                    LDA #<CON_RND_1                 ; VERY POOR RND ALGORITHM
                    LDY #>CON_RND_1
                    JSR FMULT
                    LDA #<CON_RND_2                 ; ALSO, CONSTANTS ARE TRUNCATED
                    LDY #>CON_RND_2                 ; <<<THIS DOES NOTHING, DUE TO >>>
                                                    ; <<<SMALL EXPONENT            >>>
                    JSR FADD
?1                  LDX FAC+4                       ; SHUFFLE HI AND LO BYTES
                    LDA FAC+1                       ; TO SUPPOSEDLY MAKE IT MORE RANDOM
                    STA FAC+4                       ; 
                    STX FAC+1                       ; 
                    LDA #0                          ; MAKE IT POSITIVE
                    STA FAC_SIGN                    ; 
                    LDA FAC                         ; A SOMEWHAT RANDOM EXTENSION
                    STA FAC_EXTENSION
                    LDA #$80                        ; EXPONENT TO MAKE VALUE < 1.0
                    STA FAC
                    JSR NORMALIZE_FAC_2
                    LDX #<RNDSEED                   ; MOVE FAC TO RND SEED
                    LDY #>RNDSEED
GO_MOVMF            JMP STORE_FACDB_YX_ROUNDED
                                                    ; --------------------------------
                                                    ; --------------------------------
                                                    ; "COS" FUNCTION
                                                    ; --------------------------------
COS                 LDA #<CON_PI_HALF               ; COS(X)=SIN(X + PI/2)
                    LDY #>CON_PI_HALF
                    JSR FADD
                                                    ; --------------------------------
                                                    ; "SIN" FUNCTION
                                                    ; --------------------------------
SIN                 JSR COPY_FAC_TO_ARG_ROUNDED
                    LDA #<CON_PI_DOUB               ; REMOVE MULTIPLES OF 2*PI
                    LDY #>CON_PI_DOUB               ; BY DIVIDING AND SAVING
                    LDX ARG_SIGN                    ; THE FRACTIONAL PART
                    JSR DIV                         ; USE SIGN OF ARGUMENT
                    JSR COPY_FAC_TO_ARG_ROUNDED
                    JSR INT                         ; TAKE INTEGER PART
                    LDA #0                          ; <<< WASTED LINES, BECAUSE FSUBT >>>
                    STA SGNCPR                      ; <<< CHANGES SGNCPR AGAIN        >>>
                    JSR FSUBT                       ; SUBTRACT TO GET FRACTIONAL PART
                                                    ; --------------------------------
                                                    ; (FAC) = ANGLE AS A FRACTION OF A FULL CIRCLE
                                                    ; 
                                                    ; NOW FOLD THE RANGE INTO A QUARTER CIRCLE
                                                    ; 
                                                    ; <<< THERE ARE MUCH SIMPLER WAYS TO DO THIS >>>
                                                    ; --------------------------------
                    LDA #<QUARTER                   ; 1/4 - FRACTION MAKES
                    LDY #>QUARTER                   ; -3/4 <= FRACTION < 1/4
                    JSR FSUB                        ; 
                    LDA FAC_SIGN                    ; TEST SIGN OF RESULT
                    PHA                             ; SAVE SIGN FOR LATER UNFOLDING
                    BPL SIN_1                       ; ALREADY 0...1/4
                    JSR FADDH                       ; ADD 1/2 TO SHIFT TO -1/4...1/2
                    LDA FAC_SIGN                    ; TEST SIGN
                    BMI SIN_2                       ; -1/4...0
                                                    ; 0...1/2
                    LDA SIGNFLG                     ; SIGNFLG INITIALIZED = 0 IN "TAN"
                    EOR #$FF                        ; FUNCTION
                    STA SIGNFLG                     ; "TAN" IS ONLY USER OF SIGNFLG TOO
                                                    ; --------------------------------
                                                    ; IF FALL THRU, RANGE IS 0...1/2
                                                    ; IF BRANCH HERE, RANGE IS 0...1/4
                                                    ; --------------------------------
SIN_1               JSR NEGOP
                                                    ; --------------------------------
                                                    ; IF FALL THRU, RANGE IS -1/2...0
                                                    ; IF BRANCH HERE, RANGE IS -1/4...0
                                                    ; --------------------------------
SIN_2               LDA #<QUARTER                   ; ADD 1/4 TO SHIFT RANGE
                    LDY #>QUARTER                   ; TO -1/4...1/4
                    JSR FADD                        ; 
                    PLA                             ; GET SAVED SIGN FROM ABOVE
                    BPL ?1                          ; 
                    JSR NEGOP                       ; MAKE RANGE 0...1/4
?1                  LDA #<POLY_SIN                  ; DO STANDARD SIN SERIES
                    LDY #>POLY_SIN                  ; 
                    JMP POLYNOMIAL_ODD              ; 
                                                    ; --------------------------------
                                                    ; "TAN" FUNCTION
                                                    ; 
                                                    ; COMPUTE TAN(X) = SIN(X) / COS(X)
                                                    ; --------------------------------
TAN                 JSR STORE_FAC_IN_TEMP1_ROUNDED
                    LDA #0                          ; SIGNFLG WILL BE TOGGLED IF 2ND OR 3RD
                    STA SIGNFLG                     ; QUADRANT
                    JSR SIN                         ; GET SIN(X)
                    LDX #<TEMP3                     ; SAVE SIN(X) IN TEMP3
                    LDY #>TEMP3                     ; 
                    JSR GO_MOVMF                    ; <<<FUNNY WAY TO CALL MOVMF! >>>
                    LDA #<TEMP1                     ; RETRIEVE X
                    LDY #>TEMP1                     ; 
                    JSR LOAD_FAC_FROM_YA
                    LDA #0                          ; AND COMPUTE COS(X)
                    STA FAC_SIGN                    ; 
                    LDA SIGNFLG                     ; 
                    JSR TAN_1                       ; WEIRD & DANGEROUS WAY TO GET INTO SIN
                    LDA #<TEMP3                     ; NOW FORM SIN/COS
                    LDY #>TEMP3                     ; 
                    JMP FDIV                        ; 
                                                    ; --------------------------------
TAN_1               PHA                             ; SHAME, SHAME!
                    JMP SIN_1
                                                    ; --------------------------------

CON_PI_HALF         ASM_DATA($81,$49,$0F,$DA,$A2)
CON_PI_DOUB         ASM_DATA($83,$49,$0F,$DA,$A2)
QUARTER             ASM_DATA($7F,$00,$00,$00,$00)
                                                    ; --------------------------------
POLY_SIN            ASM_DATA(5)                         ; POWER OF POLYNOMIAL
                    ASM_DATA($84,$E6,$1A,$2D,$1B)       ; (2PI)^11/11!
                    ASM_DATA($86,$28,$07,$FB,$F8)       ; (2PI)^9/9!
                    ASM_DATA($87,$99,$68,$89,$01)       ; (2PI)^7/7!
                    ASM_DATA($87,$23,$35,$DF,$E1)       ; (2PI)^5/5!
                    ASM_DATA($86,$A5,$5D,$E7,$28)       ; (2PI)^3/3!
                    ASM_DATA($83,$49,$0F,$DA,$A2)       ; 2PI
                                                    ; --------------------------------
                                                    ; <<< NEXT TEN BYTES ARE NEVER REFERENCED >>>
                                                    ; OBFUSCATED "MICROSOFT!" BY BILL GATES
                                                    ; (REVERSED, HIGH BIT SET, XOR 7)
                                                    ; --------------------------------

.MACRO .GATES_OBFUSCATE STR
    .REPEAT .STRLEN(STR),ISTR
        ASM_DATA(`(.STRAT(STR,.STRLEN(STR)-1-ISTR)') | %10000000) ^ %00000111
    .ENDREP
.ENDMACRO

                    .GATES_OBFUSCATE "MICROSOFT!"
                                                    ; --------------------------------
                                                    ; "ATN" FUNCTION
                                                    ; --------------------------------
ATN                 LDA FAC_SIGN                    ; FOLD THE ARGUMENT RANGE FIRST
                    PHA                             ; SAVE SIGN FOR LATER UNFOLDING
                    BPL ?1                          ; .GE. 0
                    JSR NEGOP                       ; .LT. 0, SO COMPLEMENT
?1                  LDA FAC                         ; IF .GE. 1, FORM RECIPROCAL
                    PHA                             ; SAVE FOR LATER UNFOLDING
                    CMP #$81                        ; (EXPONENT FOR .GE. 1
                    BCC ?2                          ; X < 1
                    LDA #<CON_ONE                   ; FORM 1/X
                    LDY #>CON_ONE
                    JSR FDIV
                                                    ; --------------------------------
                                                    ; 0 <= X <= 1
                                                    ; 0 <= ATN(X) <= PI/8
                                                    ; --------------------------------
?2                  LDA #<POLY_ATN                  ; COMPUTE POLYNOMIAL APPROXIMATION
                    LDY #>POLY_ATN
                    JSR POLYNOMIAL_ODD
                    PLA                             ; START TO UNFOLD
                    CMP #$81                        ; WAS IT .GE. 1?
                    BCC ?3                          ; NO
                    LDA #<CON_PI_HALF               ; YES, SUBTRACT FROM PI/2
                    LDY #>CON_PI_HALF               ; 
                    JSR FSUB                        ; 
?3                  PLA                             ; WAS IT NEGATIVE?
                    BPL RTS_20                      ; NO
                    JMP NEGOP                       ; YES, COMPLEMENT
RTS_20              RTS
                                                    ; --------------------------------
POLY_ATN            ASM_DATA(11)                        ; POWER OF POLYNOMIAL
                    ASM_DATA($76,$B3,$83,$BD,$D3)
                    ASM_DATA($79,$1E,$F4,$A6,$F5)
                    ASM_DATA($7B,$83,$FC,$B0,$10)
                    ASM_DATA($7C,$0C,$1F,$67,$CA)
                    ASM_DATA($7C,$DE,$53,$CB,$C1)
                    ASM_DATA($7D,$14,$64,$70,$4C)
                    ASM_DATA($7D,$B7,$EA,$51,$7A)
                    ASM_DATA($7D,$63,$30,$88,$7E)
                    ASM_DATA($7E,$92,$44,$99,$3A)
                    ASM_DATA($7E,$4C,$CC,$91,$C7)
                    ASM_DATA($7F,$AA,$AA,$AA,$13)
                    ASM_DATA($81,$00,$00,$00,$00)
                                                    ; --------------------------------
                                                    ; GENERIC COPY OF CHRGET SUBROUTINE, WHICH
                                                    ; IS COPIED INTO $00B1...$00C8 DURING INITIALIZATION
                                                    ; 
                                                    ; CORNELIS BONGERS DESCRIBED SEVERAL IMPROVEMENTS
                                                    ; TO CHRGET IN MICRO MAGAZINE OR CALL A.P.P.L.E.
                                                    ; (I DON'T REMEMBER WHICH OR EXACTLY WHEN)
                                                    ; --------------------------------
GENERIC_CHRGET
                    INC TXTPTR
                    BNE ?1
                    INC TXTPTR+1
?1                  LDA $EA60                       ; <<< ACTUAL ADDRESS FILLED IN LATER >>>
                    CMP #HICHAR(`:')                        ; EOS, ALSO TOP OF NUMERIC RANGE
                    BCS ?2                          ; NOT NUMBER, MIGHT BE EOS
                    CMP #HICHAR(` ')                        ; IGNORE BLANKS
                    BEQ GENERIC_CHRGET
                    SEC                             ; TEST FOR NUMERIC RANGE IN WAY THAT
                    SBC #HICHAR(`0')                        ; CLEARS CARRY IF CHAR IS DIGIT
                    SEC                             ; AND LEAVES CHAR IN A-REG
                    SBC #-'0'
?2                  RTS
                                                    ; --------------------------------
                                                    ; INITIAL VALUE FOR RANDOM NUMBER, ALSO COPIED
                                                    ; IN ALONG WITH CHRGET, BUT ERRONEOUSLY:
                                                    ; <<< THE LAST BYTE IS NOT COPIED >>>
                                                    ; --------------------------------

                    ASM_DATA($80,$4F,$C7,$52,$58)       ; APPROX. = ?811635157
GENERIC_END
                                                    ; --------------------------------
COLD_START
                    LDX #$FF                        ; SET DIRECT MODE FLAG
                    STX CURLIN+1                    ; 
                    LDX #$FB                        ; SET STACK POINTER, LEAVING ROOM FOR
                    TXS                             ; LINE BUFFER DURING PARSING
                    LDA #<COLD_START                ; SET RESTART TO COLD.START
                    LDY #>COLD_START                ; UNTIL COLDSTART IS COMPLETED
                    STA GOWARM+1                    ; 
                    STY GOWARM+2                    ; 
                    STA GOSTROUT+1                  ; ALSO SECOND USER VECTOR...
                    STY GOSTROUT+2                  ; ..WE SIMPLY MUST FINISH COLD.START!
                    JSR NORMAL                      ; SET NORMAL DISPLAY MODE
                    LDA #$4C                        ; "JMP" OPCODE FOR 4 VECTORS
                    STA GOWARM                      ; WARM START
                    STA GOSTROUT                    ; ANYONE EVER USE THIS ONE?
                    STA JMPADRS                     ; USED BY FUNCTIONS (JSR JMPADRS)
                    STA USR                         ; "USR" FUNCTION VECTOR
                    LDA #<IQERR                     ; POINT "USR" TO ILLEGAL QUANTITY
                    LDY #>IQERR                     ; ERROR, UNTIL USER SETS IT UP
                    STA USR+1
                    STY USR+2
                                                    ; --------------------------------
                                                    ; MOVE GENERIC CHRGET AND RANDOM SEED INTO PLACE
                                                    ; 
                                                    ; <<< NOTE THAT LOOP VALUE IS WRONG!          >>>
                                                    ; <<< THE LAST BYTE OF THE RANDOM SEED IS NOT >>>
                                                    ; <<< COPIED INTO PAGE ZERO!                  >>>
                                                    ; --------------------------------
                    LDX #GENERIC_END-GENERIC_CHRGET-1
?1                  LDA GENERIC_CHRGET-1,X
                    STA CHRGET-1,X
                    STX SPEEDZ                      ; ON LAST PASS STORES $01)
                    DEX
                    BNE ?1
                                                    ; --------------------------------
                    STX TRCFLG                      ; X=0, TURN OFF TRACING
                    TXA                             ; A=0
                    STA SHIFT_SIGN_EXT
                    STA LASTPT+1
                    PHA                             ; PUT $00 ON STACK (WHAT FOR?)
                    LDA #3                          ; SET LENGTH OF TEMP. STRING DESCRIPTORS
                    STA DSCLEN                      ; FOR GARBAGE COLLECTION SUBROUTINE
                    JSR CRDO                        ; PRINT <RETURN>
                    LDA #1                          ; SET UP FAKE FORWARD LINK
                    STA INPUT_BUFFER-3
                    STA INPUT_BUFFER-4
                    LDX #TEMPST                     ; INIT INDEX TO TEMP STRING DESCRIPTORS
                    STX TEMPPT
                                                    ; --------------------------------
                                                    ; FIND HIGH END OF RAM
                                                    ; --------------------------------
                    LDA #<$0800                     ; SET UP POINTER TO LOW END OF RAM
                    LDY #>$0800
                    STA LINNUM
                    STY LINNUM+1
                    LDY #0
?2                  INC LINNUM+1                    ; TEST FIRST BYTE OF EACH PAGE
                    LDA (LINNUM),Y                  ; BY COMPLEMENTING IT AND WATCHING
                    EOR #$FF                        ; IT CHANGE THE SAME WAY
                    STA (LINNUM),Y                  ; 
                    CMP (LINNUM),Y                  ; ROM OR EMPTY SOCKETS WON'T TRACK
                    BNE ?3                          ; NOT RAM HERE
                    EOR #$FF                        ; RESTORE ORIGINAL VALUE
                    STA (LINNUM),Y                  ; 
                    CMP (LINNUM),Y                  ; DID IT TRACK AGAIN?
                    BEQ ?2                          ; YES, STILL IN RAM
?3                  LDY LINNUM                      ; NO, END OF RAM
                    LDA LINNUM+1                    ; 
                    AND #$F0                        ; FORCE A MULTIPLE OF 4096 BYTES
                    STY MEMSIZ                      ; (BAD RAM MAY HAVE YIELDED NON-MULTIPLE)
                    STA MEMSIZ+1                    ; 
                    STY FRETOP                      ; SET HIMEM AND BOTTOM OF STRINGS
                    STA FRETOP+1                    ; 
                    LDX #<$0800                     ; SET PROGRAM POINTER TO $0800
                    LDY #>$0800                     ; 
                    STX TXTTAB                      ; 
                    STY TXTTAB+1                    ; 
                    LDY #0                          ; TURN OFF SEMI-SECRET LOCK FLAG
                    STY LOCK                        ; 
                    TYA                             ; A=0 TOO
                    STA (TXTTAB),Y                  ; FIRST BYTE IN PROGRAM SPACE = 0
                    INC TXTTAB                      ; ADVANCE PAST THE $00
                    BNE ?4                          ; 
                    INC TXTTAB+1                    ; 
?4                  LDA TXTTAB                      ; 
                    LDY TXTTAB+1                    ; 
                    JSR REASON                      ; SET REST OF POINTERS UP
                    JSR SCRTCH                      ; MORE POINTERS
                    LDA #<STROUT                    ; PUT CORRECT ADDRESSES IN TWO
                    LDY #>STROUT                    ; USER VECTORS
                    STA GOSTROUT+1
                    STY GOSTROUT+2
                    LDA #<RESTART
                    LDY #>RESTART
                    STA GOWARM+1
                    STY GOWARM+2
                    JMP (GOWARM+1)                  ; SILLY, WHY NOT JUST "JMP RESTART"
                                                    ; --------------------------------
                                                    ; --------------------------------
                                                    ; "CALL" STATEMENT
                                                    ; 
                                                    ; EFFECTIVELY PERFORMS A "JSR" TO THE SPECIFIED
                                                    ; ADDRESS, WITH THE FOLLOWING REGISTER CONTENTS:
                                                    ; (A,Y) = CALL ADDRESS
                                                    ; (X)   = $9D
                                                    ; 
                                                    ; THE CALLED ROUTINE CAN RETURN WITH "RTS",
                                                    ; AND APPLESOFT WILL CONTINUE WITH THE NEXT
                                                    ; STATEMENT.
                                                    ; --------------------------------
CALL                JSR FRMNUM                      ; EVALUATE EXPRESSION FOR CALL ADDRESS
                    JSR GETADR                      ; CONVERT EXPRESSION TO 16-BIT INTEGER
                    JMP (LINNUM)                    ; IN LINNUM, AND JUMP THERE.
                                                    ; --------------------------------
                                                    ; "IN#" STATEMENT
                                                    ; 
                                                    ; NOTE:  NO CHECK FOR VALID SLOT #, AS LONG
                                                    ; AS VALUE IS < 256 IT IS ACCEPTED.
                                                    ; MONITOR MASKS VALUE TO 4 BITS (0-15).
                                                    ; --------------------------------
IN_NUMBER
                    JSR GETBYT                      ; GET SLOT NUMBER IN X-REG
                    TXA                             ; MONITOR WILL INSTALL IN VECTOR
                    JMP MON_INPORT                  ; AT $38,39.
                                                    ; --------------------------------
                                                    ; "PR#" STATEMENT
                                                    ; 
                                                    ; NOTE:  NO CHECK FOR VALID SLOT #, AS LONG
                                                    ; AS VALUE IS < 256 IT IS ACCEPTED.
                                                    ; MONITOR MASKS VALUE TO 4 BITS (0-15).
                                                    ; --------------------------------
PR_NUMBER
                    JSR GETBYT                      ; GET SLOT NUMBER IN X-REG
                    TXA                             ; MONITOR WILL INSTALL IN VECTOR
                    JMP MON_OUTPORT                 ; AT $36,37
                                                    ; --------------------------------
                                                    ; GET TWO VALUES < 48, WITH COMMA SEPARATOR
                                                    ; 
                                                    ; CALLED FOR "PLOT X,Y"
                                                    ; AND "HLIN A,B AT Y"
                                                    ; AND "VLIN A,B AT X"
                                                    ; 
                                                    ; --------------------------------
PLOTFNS
                    JSR GETBYT                      ; GET FIRST VALUE IN X-REG
                    CPX #48                         ; MUST BE < 48
                    BCS GOERR                       ; TOO LARGE
                    STX FIRST                       ; SAVE FIRST VALUE
                    LDA #HICHAR(`,')                        ; MUST HAVE A COMMA
                    JSR SYNCHR                      ; 
                    JSR GETBYT                      ; GET SECOND VALUE IN X-REG
                    CPX #48                         ; MUST BE < 48
                    BCS GOERR                       ; TOO LARGE
                    STX MON_H2                      ; SAVE SECOND VALUE
                    STX MON_V2                      ; 
                    RTS                             ; SECOND VALUE STILL IN X-REG
                                                    ; --------------------------------
GOERR               JMP IQERR                       ; ILLEGAL QUANTITY ERROR
                                                    ; --------------------------------
                                                    ; GET "A,B AT C" VALUES FOR "HLIN" AND "VLIN"
                                                    ; 
                                                    ; PUT SMALLER OF (A,B) IN FIRST,
                                                    ; AND LARGER  OF (A,B) IN H2 AND V2.
                                                    ; RETURN WITH (X) = C-VALUE.
                                                    ; --------------------------------
LINCOOR
                    JSR PLOTFNS                     ; GET A,B VALUES
                    CPX FIRST                       ; IS A < B?
                    BCS ?1                          ; YES, IN RIGHT ORDER
                    LDA FIRST                       ; NO, INTERCHANGE THEM
                    STA MON_H2                      ; 
                    STA MON_V2                      ; 
                    STX FIRST                       ; 
?1                  LDA #TOKENDB                    ; MUST HAVE "AT" NEXT
                    JSR SYNCHR                      ; 
                    JSR GETBYT                      ; GET C-VALUE IN X-REG
                    CPX #48                         ; MUST BE < 48
                    BCS GOERR                       ; TOO LARGE
                    RTS                             ; C-VALUE IN X-REG
                                                    ; --------------------------------
                                                    ; "PLOT" STATEMENT
                                                    ; --------------------------------
PLOT                JSR PLOTFNS                     ; GET X,Y VALUES
                    TXA                             ; Y-COORD TO A-REG FOR MONITOR
                    LDY FIRST                       ; X-COORD TO Y-YEG FOR MONITOR
                    CPY #40                         ; X-COORD MUST BE < 40
                    BCS GOERR                       ; X-COORD IS TOO LARGE
                    JMP MON_PLOT                    ; PLOT!
                                                    ; --------------------------------
                                                    ; "HLIN" STATEMENT
                                                    ; --------------------------------
HLIN                JSR LINCOOR                     ; GET "A,B AT C"
                    TXA                             ; Y-COORD IN A-REG
                    LDY MON_H2                      ; RIGHT END OF LINE
                    CPY #40                         ; MUST BE < 40
                    BCS GOERR                       ; TOO LARGE
                    LDY FIRST                       ; LEFT END OF LINE IN Y-REG
                    JMP MON_HLINE                   ; LET MONITOR DRAW LINE
                                                    ; --------------------------------
                                                    ; "VLIN" STATEMENT
                                                    ; --------------------------------
VLIN                JSR LINCOOR                     ; GET "A,B AT C"
                    TXA                             ; X-COORD IN Y-REG
                    TAY                             ; 
                    CPY #40                         ; X-COORD MUST BE < 40
                    BCS GOERR                       ; TOO LARGE
                    LDA FIRST                       ; TOP END OF LINE IN A-REG
                    JMP MON_VLINE                   ; LET MONITOR DRAW LINE
                                                    ; --------------------------------
                                                    ; "COLOR=" STATEMENT
                                                    ; --------------------------------
COLOR               JSR GETBYT                      ; GET COLOR VALUE IN X-REG
                    TXA                             ; 
                    JMP MON_SETCOL                  ; LET MONITOR STORE COLOR
                                                    ; --------------------------------
                                                    ; "VTAB" STATEMENT
                                                    ; --------------------------------
VTAB                JSR GETBYT                      ; GET LINE # IN X-REG
                    DEX                             ; CONVERT TO ZERO BASE
                    TXA                             ; 
                    CMP #24                         ; MUST BE 0-23
                    BCS GOERR                       ; TOO LARGE, OR WAS "VTAB 0"
                    JMP MON_TABV                    ; LET MONITOR COMPUTE BASE
                                                    ; --------------------------------
                                                    ; "SPEED=" STATEMENT
                                                    ; --------------------------------
SPEED               JSR GETBYT                      ; GET SPEED SETTING IN X-REG
                    TXA                             ; SPEEDZ = $100-SPEED
                    EOR #$FF                        ; SO "SPEED=255" IS FASTEST
                    TAX                             ; 
                    INX                             ; 
                    STX SPEEDZ                      ; 
                    RTS                             ; 
                                                    ; --------------------------------
                                                    ; "TRACE" STATEMENT
                                                    ; SET SIGN BIT IN TRCFLG
                                                    ; --------------------------------
TRACE               SEC                             ; 
                    ASM_DATA($90)                       ; FAKE BCC TO SKIP NEXT OPCODE
                                                    ; --------------------------------
                                                    ; "NOTRACE" STATEMENT
                                                    ; CLEAR SIGN BIT IN TRCFLG
                                                    ; --------------------------------
NOTRACE                                             ; 
                    CLC                             ; 
                    ROR TRCFLG                      ; SHIFT CARRY INTO TRCFLG
                    RTS                             ; 
                                                    ; --------------------------------
                                                    ; "NORMAL" STATEMENT
                                                    ; --------------------------------
NORMAL              LDA #$FF                        ; SET INVFLG = $FF
                    BNE N_I_                        ; AND FLASH.BIT = $00
                                                    ; --------------------------------
                                                    ; "INVERSE" STATEMENT
                                                    ; --------------------------------
INVERSE                                             ; 
                    LDA #$3F                        ; SET INVFLG = $3F
N_I_                LDX #0                          ; AND FLASH.BIT = $00
N_I_F_              STA MON_INVFLG
                    STX FLASH_BIT
                    RTS
                                                    ; --------------------------------
                                                    ; "FLASH" STATEMENT
                                                    ; --------------------------------
FLASH               LDA #$7F                        ; SET INVFLG = $7F
                    LDX #$40                        ; AND FLASH.BIT = $40
                    BNE N_I_F_                      ; ...ALWAYS
                                                    ; --------------------------------
                                                    ; "HIMEM:" STATEMENT
                                                    ; --------------------------------
HIMEM               JSR FRMNUM                      ; GET VALUE SPECIFIED FOR HIMEM
                    JSR GETADR                      ; AS 16-BIT INTEGER
                    LDA LINNUM                      ; MUST BE ABOVE VARIABLES AND ARRAYS
                    CMP STREND                      ; 
                    LDA LINNUM+1                    ; 
                    SBC STREND+1                    ; 
                    BCS SETHI                       ; IT IS ABOVE THEM
JMM                 JMP MEMERR                      ; NOT ENOUGH MEMORY
SETHI               LDA LINNUM                      ; STORE NEW HIMEM: VALUE
                    STA MEMSIZ                      ; 
                    STA FRETOP                      ; <<<NOTE THAT "HIMEM:" DOES NOT>>>
                    LDA LINNUM+1                    ; <<<CLEAR STRING VARIABLES.    >>>
                    STA MEMSIZ+1                    ; <<<THIS COULD BE DISASTROUS.  >>>
                    STA FRETOP+1                    ; 
                    RTS                             ; 
                                                    ; --------------------------------
                                                    ; "LOMEM:" STATEMENT
                                                    ; --------------------------------
LOMEM               JSR FRMNUM                      ; GET VALUE SPECIFIED FOR LOMEM
                    JSR GETADR                      ; AS 16-BIT INTEGER IN LINNUM
                    LDA LINNUM                      ; MUST BE BELOW HIMEM
                    CMP MEMSIZ                      ; 
                    LDA LINNUM+1                    ; 
                    SBC MEMSIZ+1                    ; 
                    BCS JMM                         ; ABOVE HIMEM, MEMORY ERROR
                    LDA LINNUM                      ; MUST BE ABOVE PROGRAM
                    CMP VARTAB                      ; 
                    LDA LINNUM+1                    ; 
                    SBC VARTAB+1                    ; 
                    BCC JMM                         ; NOT ABOVE PROGRAM, ERROR
                    LDA LINNUM                      ; STORE NEW LOMEM VALUE
                    STA VARTAB                      ; 
                    LDA LINNUM+1                    ; 
                    STA VARTAB+1                    ; 
                    JMP CLEARC                      ; LOMEM CLEARS VARIABLES AND ARRAYS
                                                    ; --------------------------------
                                                    ; "ON ERR GO TO" STATEMENT
                                                    ; --------------------------------
ONERR               LDA #TOKEN_GOTO                 ; MUST BE "GOTO" NEXT
                    JSR SYNCHR
                    LDA TXTPTR                      ; SAVE TXTPTR FOR HANDLERR
                    STA TXTPSV                      ; 
                    LDA TXTPTR+1                    ; 
                    STA TXTPSV+1                    ; 
                    SEC                             ; SET SIGN BIT OF ERRFLG
                    ROR ERRFLG                      ; 
                    LDA CURLIN                      ; SAVE LINE # OF CURRENT LINE
                    STA CURLSV                      ; 
                    LDA CURLIN+1                    ; 
                    STA CURLSV+1                    ; 
                    JSR REMN                        ; IGNORE REST OF LINE <<<WHY?>>>
                    JMP ADDON                       ; CONTINUE PROGRAM
                                                    ; --------------------------------
                                                    ; ROUTINE TO HANDLE ERRORS IF ONERR GOTO ACTIVE
                                                    ; --------------------------------
HANDLERR                                            ; 
                    STX ERRNUM                      ; SAVE ERROR CODE NUMBER
                    LDX REMSTK                      ; GET STACK PNTR SAVED AT NEWSTT
                    STX ERRSTK                      ; REMEMBER IT
                                                    ; <<<COULD ALSO HAVE DONE TXS  >>>
                                                    ; <<<HERE; SEE ONERR CORRECTION>>>
                                                    ; <<<IN APPLESOFT MANUAL.      >>>
                    LDA CURLIN                      ; GET LINE # OF OFFENDING STATEMENT
                    STA ERRLIN                      ; SO USER CAN SEE IT IF DESIRED
                    LDA CURLIN+1                    ; 
                    STA ERRLIN+1                    ; 
                    LDA OLDTEXT                     ; ALSO THE POSITION IN THE LINE
                    STA ERRPOS                      ; IN CASE USER WANTS TO "RESUME"
                    LDA OLDTEXT+1                   ; 
                    STA ERRPOS+1                    ; 
                    LDA TXTPSV                      ; SET UP TXTPTR TO READ TARGET LINE #
                    STA TXTPTR                      ; IN "ON ERR GO TO XXXX"
                    LDA TXTPSV+1                    ; 
                    STA TXTPTR+1                    ; 
                    LDA CURLSV                      ; 
                    STA CURLIN                      ; LINE # OF "ON ERR" STATEMENT
                    LDA CURLSV+1                    ; 
                    STA CURLIN+1                    ; 
                    JSR CHRGOT                      ; START CONVERSION
                    JSR GOTO                        ; GOTO SPECIFIED ONERR LINE
                    JMP NEWSTT                      ; 
                                                    ; --------------------------------
                                                    ; "RESUME" STATEMENT
                                                    ; --------------------------------
RESUME              LDA ERRLIN                      ; RESTORE LINE # AND TXTPTR
                    STA CURLIN                      ; TO RE-TRY OFFENDING LINE
                    LDA ERRLIN+1                    ; 
                    STA CURLIN+1                    ; 
                    LDA ERRPOS                      ; 
                    STA TXTPTR                      ; 
                    LDA ERRPOS+1                    ; 
                    STA TXTPTR+1                    ; 
                                                    ; <<< ONERR CORRECTION IN MANUAL IS EASILY >>>
                                                    ; <<< BY "CALL -3288", WHICH IS $F328 HERE >>>
                    LDX ERRSTK                      ; RETRIEVE STACK PNTR AS IT WAS
                    TXS                             ; BEFORE STATEMENT SCANNED
                    JMP NEWSTT                      ; DO STATEMENT AGAIN
                                                    ; --------------------------------
JSYN                JMP SYNERR                      ; 
                                                    ; --------------------------------
                                                    ; "DEL" STATEMENT
                                                    ; --------------------------------
DEL                 BCS JSYN                        ; ERROR IF # NOT SPECIFIED
                    LDX PRGEND                      ; 
                    STX VARTAB                      ; 
                    LDX PRGEND+1                    ; 
                    STX VARTAB+1                    ; 
                    JSR LINGET                      ; GET BEGINNING OF RANGE
                    JSR FNDLIN                      ; FIND THIS LINE OR NEXT
                    LDA LOWTR                       ; UPPER PORTION OF PROGRAM WILL
                    STA DEST                        ; BE MOVED DOWN TO HERE
                    LDA LOWTR+1                     ; 
                    STA DEST+1                      ; 
                    LDA #HICHAR(`,')                        ; MUST HAVE A COMMA NEXT
                    JSR SYNCHR                      ; 
                    JSR LINGET                      ; GET END RANGE
                                                    ; (DOES NOTHING IF END RANGE
                                                    ; IS NOT SPECIFIED)
                    INC LINNUM                      ; POINT ONE PAST IT
                    BNE ?1                          ; 
                    INC LINNUM+1                    ; 
?1                  JSR FNDLIN                      ; FIND START LINE AFTER SPECIFIED LINE
                    LDA LOWTR                       ; WHICH IS BEGINNING OF PORTION
                    CMP DEST                        ; TO BE MOVED DOWN
                    LDA LOWTR+1                     ; IT MUST BE ABOVE THE TARGET
                    SBC DEST+1                      ; 
                    BCS ?2                          ; IT IS OKAY
                    RTS                             ; NOTHING TO DELETE
?2                  LDY #0                          ; MOVE UPPER PORTION DOWN NOW
?3                  LDA (LOWTR),Y                   ; SOURCE . . .
                    STA (DEST),Y                    ; ...TO DESTINATION
                    INC LOWTR                       ; BUMP SOURCE PNTR
                    BNE ?4                          ; 
                    INC LOWTR+1                     ; 
?4                  INC DEST                        ; BUMP DESTINATION PNTR
                    BNE ?5                          ; 
                    INC DEST+1                      ; 
?5                  LDA VARTAB                      ; REACHED END OF PROGRAM YET?
                    CMP LOWTR                       ; 
                    LDA VARTAB+1                    ; 
                    SBC LOWTR+1                     ; 
                    BCS ?3                          ; NO, KEEP MOVING
                    LDX DEST+1                      ; STORE NEW END OF PROGRAM
                    LDY DEST                        ; MUST SUBTRACT 1 FIRST
                    BNE ?6                          ; 
                    DEX                             ; 
?6                  DEY                             ; 
                    STX VARTAB+1                    ; 
                    STY VARTAB                      ; 
                    JMP FIX_LINKS                   ; RESET LINKS AFTER A DELETE
                                                    ; --------------------------------
                                                    ; "GR" STATEMENT
                                                    ; --------------------------------
GR                  LDA SW_LORES
                    LDA SW_MIXSET
                    JMP MON_SETGR
                                                    ; --------------------------------
                                                    ; "TEXT" STATEMENT
                                                    ; --------------------------------
TEXT                LDA SW_LOWSCR                   ; JMP $FB36 WOULD HAVE
                    JMP MON_SETTXT                  ; DONE BOTH OF THESE
                                                    ; <<<       BETTER CODE WOULD BE:   >>>
                                                    ; <<<  LDA SW.MIXSET                >>>
                                                    ; <<<  JMP $FB33                    >>>
                                                    ; --------------------------------
                                                    ; "STORE" STATEMENT
                                                    ; --------------------------------
STORE               JSR GETARYPT                    ; GET ADDRESS OF ARRAY TO BE SAVED
                    LDY #3                          ; FORWARD OFFSET - 1 IS SIZE OF
                    LDA (LOWTR),Y                   ; THIS ARRAY
                    TAX
                    DEY
                    LDA (LOWTR),Y
                    SBC #1
                    BCS ?1
                    DEX
?1                  STA LINNUM
                    STX LINNUM+1
                    JSR MON_WRITE
                    JSR TAPEPNT
                    JMP MON_WRITE
                                                    ; --------------------------------
                                                    ; "RECALL" STATEMENT
                                                    ; --------------------------------
RECALL              JSR GETARYPT                    ; FIND ARRAY IN MEMORY
                    JSR MON_READ                    ; READ HEADER
                    LDY #2                          ; MAKE SURE THE NEW DATA FITS
                    LDA (LOWTR),Y                   ; 
                    CMP LINNUM                      ; 
                    INY                             ; 
                    LDA (LOWTR),Y                   ; 
                    SBC LINNUM+1                    ; 
                    BCS ?1                          ; IT FITS
                    JMP MEMERR                      ; DOESN'T FIT
?1                  JSR TAPEPNT                     ; READ THE DATA
                    JMP MON_READ                    ; 
                                                    ; --------------------------------
                                                    ; "HGR" AND "HGR2" STATEMENTS
                                                    ; --------------------------------
HGR2                BIT SW_HISCR                    ; SELECT PAGE 2 ($4000-5FFF)
                    BIT SW_MIXCLR                   ; DEFAULT TO FULL SCREEN
                    LDA #>$4000                     ; SET STARTING PAGE FOR HIRES
                    BNE SETHPG                      ; ...ALWAYS
HGR                 LDA #>$2000                     ; SET STARTING PAGE FOR HIRES
                    BIT SW_LOWSCR                   ; SELECT PAGE 1 ($2000-3FFF)
                    BIT SW_MIXSET                   ; DEFAULT TO MIXED SCREEN
SETHPG              STA HGR_PAGE                    ; BASE PAGE OF HIRES BUFFER
                    LDA SW_HIRES                    ; TURN ON HIRES
                    LDA SW_TXTCLR                   ; TURN ON GRAPHICS
                                                    ; --------------------------------
                                                    ; CLEAR SCREEN
                                                    ; --------------------------------
HCLR                LDA #0                          ; SET FOR BLACK BACKGROUND
                    STA HGR_BITS
                                                    ; --------------------------------
                                                    ; FILL SCREEN WITH (HGR.BITS)
                                                    ; --------------------------------
BKGND               LDA HGR_PAGE                    ; PUT BUFFER ADDRESS IN HGR.SHAPE
                    STA HGR_SHAPE+1
                    LDY #0
                    STY HGR_SHAPE
?1                  LDA HGR_BITS                    ; COLOR BYTE
                    STA (HGR_SHAPE),Y               ; CLEAR HIRES TO HGR.BITS
                    JSR COLOR_SHIFT                 ; CORRECT FOR COLOR SHIFT
                    INY                             ; (SLOWS CLEAR BY FACTOR OF 2)
                    BNE ?1
                    INC HGR_SHAPE+1
                    LDA HGR_SHAPE+1
                    AND #$1F                        ; DONE?  ($40 OR$60)
                    BNE ?1                          ; NO
                    RTS                             ; YES, RETURN
                                                    ; --------------------------------
                                                    ; SET THE HIRES CURSOR POSITION
                                                    ; 
                                                    ; (Y,X) = HORIZONTAL COORDINATE  (0-279)
                                                    ; (A)   = VERTICAL COORDINATE    (0-191)
                                                    ; --------------------------------
HPOSN               STA HGR_Y                       ; SAVE Y- AND X-POSITIONS
                    STX HGR_X                       ; 
                    STY HGR_X+1                     ; 
                    PHA                             ; Y-POS ALSO ON STACK
                    AND #$C0                        ; CALCULATE BASE ADDRESS FOR Y-POS
                    STA MON_GBASL                   ; FOR Y=ABCDEFGH
                    LSR                             ; GBASL=ABAB0000
                    LSR                             ; 
                    ORA MON_GBASL                   ; 
                    STA MON_GBASL                   ; 
                    PLA                             ; (A)      (GBASH)   (GBASL)
                    STA MON_GBASH                   ; ?-ABCDEFGH  ABCDEFGH  ABAB0000
                    ASL                             ; A-BCDEFGH0  ABCDEFGH  ABAB0000
                    ASL                             ; B-CDEFGH00  ABCDEFGH  ABAB0000
                    ASL                             ; C-DEFGH000  ABCDEFGH  ABAB0000
                    ROL MON_GBASH                   ; A-DEFGH000  BCDEFGHC  ABAB0000
                    ASL                             ; D-EFGH0000  BCDEFGHC  ABAB0000
                    ROL MON_GBASH                   ; B-EFGH0000  CDEFGHCD  ABAB0000
                    ASL                             ; E-FGH00000  CDEFGHCD  ABAB0000
                    ROR MON_GBASL                   ; 0-FGH00000  CDEFGHCD  EABAB000
                    LDA MON_GBASH                   ; 0-CDEFGHCD  CDEFGHCD  EABAB000
                    AND #$1F                        ; 0-000FGHCD  CDEFGHCD  EABAB000
                    ORA HGR_PAGE                    ; 0-PPPFGHCD  CDEFGHCD  EABAB000
                    STA MON_GBASH                   ; 0-PPPFGHCD  PPPFGHCD  EABAB000
                    TXA                             ; DIVIDE X-POS BY 7 FOR INDEX FROM BASE
                    CPY #0                          ; IS X-POS < 256?
                    BEQ ?2                          ; YES
                    LDY #35                         ; NO: 256/7 = 36 REM 4
                                                    ; CARRY=1, SO ADC #4 IS TOO LARGE;
                                                    ; HOWEVER, ADC #4 CLEARS CARRY
                                                    ; WHICH MAKES SBC #7 ONLY -6
                                                    ; BALANCING IT OUT.
                    ADC #4                          ; FOLLOWING INY MAKES Y=36
?1                  INY
?2                  SBC #7
                    BCS ?1
                    STY HGR_HORIZ                   ; HORIZONTAL INDEX
                    TAX                             ; USE REMAINDER-7 TO LOOK UP THE
                    LDA MSKTBL-$100+7,X             ; BIT MASK
                    STA MON_HMASK
                    TYA                             ; QUOTIENT GIVES BYTE INDEX
                    LSR                             ; ODD OR EVEN COLUMN?
                    LDA HGR_COLOR                   ; IF ON ODD BYTE (CARRY SET)
                    STA HGR_BITS                    ; THEN ROTATE BITS
                    BCS COLOR_SHIFT                 ; ODD COLUMN
                    RTS                             ; EVEN COLUMN
                                                    ; --------------------------------
                                                    ; PLOT A DOT
                                                    ; 
                                                    ; (Y,X) = HORIZONTAL POSITION
                                                    ; (A)   = VERTICAL POSITION
                                                    ; --------------------------------
HPLOT0              JSR HPOSN
                    LDA HGR_BITS                    ; CALCULATE BIT POSN IN GBAS,
                    EOR (MON_GBASL),Y               ; HGR.HORIZ, AND HMASK FROM
                    AND MON_HMASK                   ; Y-COOR IN A-REG,
                    EOR (MON_GBASL),Y               ; X-COOR IN X,Y REGS.
                    STA (MON_GBASL),Y               ; FOR ANY 1-BITS, SUBSTITUTE
                    RTS                             ; CORRESPONDING BIT OF HGR.BITS
                                                    ; --------------------------------
                                                    ; MOVE LEFT OR RIGHT ONE PIXEL
                                                    ; 
                                                    ; IF STATUS IS +, MOVE RIGHT; IF -, MOVE LEFT
                                                    ; IF ALREADY AT LEFT OR RIGHT EDGE, WRAP AROUND
                                                    ; 
                                                    ; REMEMBER BITS IN HI-RES BYTE ARE BACKWARDS ORDER:
                                                    ; BYTE N   BYTE N+1
                                                    ; S7654321   SEDCBA98
                                                    ; --------------------------------
MOVE_LEFT_OR_RIGHT
                    BPL MOVE_RIGHT                  ; + MOVE RIGHT, - MOVE LEFT
                    LDA MON_HMASK                   ; MOVE LEFT ONE PIXEL
                    LSR                             ; SHIFT MASK RIGHT, MOVES DOT LEFT
                    BCS LR_2                        ; ...DOT MOVED TO NEXT BYTE
                    EOR #$C0                        ; MOVE SIGN BIT BACK WHERE IT WAS
LR_1                STA MON_HMASK                   ; NEW MASK VALUE
                    RTS                             ; 
LR_2                DEY                             ; MOVED TO NEXT BYTE, SO DECR INDEX
                    BPL LR_3                        ; STILL NOT PAST EDGE
                    LDY #39                         ; OFF LEFT EDGE, SO WRAP AROUND SCREEN
LR_3                LDA #$C0                        ; NEW HMASK, RIGHTMOST BIT ON SCREEN
LR_4                STA MON_HMASK                   ; NEW MASK AND INDEX
                    STY HGR_HORIZ                   ; 
                    LDA HGR_BITS                    ; ALSO NEED TO ROTATE COLOR
                                                    ; --------------------------------
COLOR_SHIFT
                    ASL                             ; ROTATE LOW-ORDER 7 BITS
                    CMP #$C0                        ; OF HGR.BITS ONE BIT POSN.
                    BPL ?1
                    LDA HGR_BITS
                    EOR #$7F
                    STA HGR_BITS
?1                  RTS
                                                    ; --------------------------------
                                                    ; MOVE RIGHT ONE PIXEL
                                                    ; IF ALREADY AT RIGHT EDGE, WRAP AROUND
                                                    ; --------------------------------
MOVE_RIGHT
                    LDA MON_HMASK
                    ASL                             ; SHIFTING BYTE LEFT MOVES PIXEL RIGHT
                    EOR #$80                        ; 
                                                    ; ORIGINAL:  C0 A0 90 88 84 82 81
                                                    ; SHIFTED:   80 40 20 10 08 02 01
                                                    ; EOR #$80:  00 C0 A0 90 88 84 82
                    BMI LR_1                        ; FINISHED
                    LDA #$81                        ; NEW MASK VALUE
                    INY                             ; MOVE TO NEXT BYTE RIGHT
                    CPY #40                         ; UNLESS THAT IS TOO FAR
                    BCC LR_4                        ; NOT TOO FAR
                    LDY #0                          ; TOO FAR, SO WRAP AROUND
                    BCS LR_4                        ; ...ALWAYS
                                                    ; --------------------------------
                                                    ; --------------------------------
                                                    ; "XDRAW" ONE BIT
                                                    ; --------------------------------
LRUDX1              CLC                             ; C=0 MEANS NO 90 DEGREE ROTATION
LRUDX2              LDA HGR_DX+1                    ; C=1 MEANS ROTATE 90 DEGREES
                    AND #4                          ; IF BIT2=0 THEN DON'T PLOT
                    BEQ LRUD4                       ; YES, DO NOT PLOT
                    LDA #$7F                        ; NO, LOOK AT WHAT IS ALREADY THERE
                    AND MON_HMASK
                    AND (MON_GBASL),Y               ; SCREEN BIT = 1?
                    BNE LRUD3                       ; YES, GO CLEAR IT
                    INC HGR_COLLISIONS              ; NO, COUNT THE COLLISION
                    LDA #$7F                        ; AND TURN THE BIT ON
                    AND MON_HMASK                   ; 
                    BPL LRUD3                       ; ...ALWAYS
                                                    ; --------------------------------
                                                    ; "DRAW" ONE BIT
                                                    ; --------------------------------
LRUD1               CLC                             ; C=0 MEANS NO 90 DEGREE ROTATION
LRUD2               LDA HGR_DX+1                    ; C=1 MEANS ROTATE
                    AND #4                          ; IF BIT2=0 THEN DO NOT PLOT
                    BEQ LRUD4                       ; DO NOT PLOT
                    LDA (MON_GBASL),Y
                    EOR HGR_BITS                    ; 1'S WHERE ANY BITS NOT IN COLOR
                    AND MON_HMASK                   ; LOOK AT JUST THIS BIT POSITION
                    BNE LRUD3                       ; THE BIT WAS ZERO, SO PLOT IT
                    INC HGR_COLLISIONS              ; BIT IS ALREADY 1; COUNT COLLSN
                                                    ; --------------------------------
                                                    ; TOGGLE BIT ON SCREEN WITH (A)
                                                    ; --------------------------------
LRUD3               EOR (MON_GBASL),Y
                    STA (MON_GBASL),Y
                                                    ; --------------------------------
                                                    ; DETERMINE WHERE NEXT POINT WILL BE, AND MOVE THERE
                                                    ; C=0 IF NO 90 DEGREE ROTATION
                                                    ; C=1 ROTATES 90 DEGREES
                                                    ; --------------------------------
LRUD4               LDA HGR_DX+1                    ; CALCULATE THE DIRECTION TO MOVE
                    ADC HGR_QUADRANT
                    AND #3                          ; WRAP AROUND THE CIRCLE
CON_03              = *-1                           ; (( A CONSTANT ))
                                                    ; 
                                                    ; 00 -- UP
                                                    ; 01 -- DOWN
                                                    ; 10 -- RIGHT
                                                    ; 11 -- LEFT
                                                    ; 
                    CMP #2                          ; C=0 IF 0 OR 1, C=1 IF 2 OR 3
                    ROR                             ; PUT C INTO SIGN, ODD/EVEN INTO C
                    BCS MOVE_LEFT_OR_RIGHT
                                                    ; --------------------------------
MOVE_UP_OR_DOWN
                    BMI MOVE_DOWN                   ; SIGN FOR UP/DOWN SELECT_
                                                    ; --------------------------------
                                                    ; MOVE UP ONE PIXEL
                                                    ; IF ALREADY AT TOP, GO TO BOTTOM
                                                    ; 
                                                    ; REMEMBER:  Y-COORD   GBASH     GBASL
                                                    ; ABCDEFGH  PPPFGHCD  EABAB000
                                                    ; --------------------------------
                    CLC                             ; MOVE UP
                    LDA MON_GBASH                   ; CALC. BASE ADDRESS OF PREV. LINE
                    BIT CON_1C                      ; LOOK AT BITS 000FGH00 IN GBASH
                    BNE ?5                          ; SIMPLE, JUST FGH=FGH-1
                                                    ; GBASH=PPP000CD, GBASL=EABAB000
                    ASL MON_GBASL                   ; WHAT IS "E"?
                    BCS ?3                          ; E=1, THEN EFGH=EFGH-1
                    BIT CON_03                      ; LOOK AT 000000CD IN GBASH
                    BEQ ?1                          ; Y-POS IS AB000000 FORM
                    ADC #$1F                        ; CD <> 0, SO CDEFGH=CDEFGH-1
                    SEC                             ; 
                    BCS ?4                          ; ...ALWAYS
?1                  ADC #$23                        ; ENOUGH TO MAKE GBASH=PPP11111 LATER
                    PHA                             ; SAVE FOR LATER
                    LDA MON_GBASL                   ; GBASL IS NOW ABAB0000 (AB=00,01,10)
                    ADC #$B0                        ; 0000+1011=1011 AND CARRY CLEAR
                                                    ; OR 0101+1011=0000 AND CARRY SET
                                                    ; OR 1010+1011=0101 AND CARRY SET
                    BCS ?2                          ; NO WRAP-AROUND NEEDED
                    ADC #$F0                        ; CHANGE 1011 TO 1010 (WRAP-AROUND)
?2                  STA MON_GBASL                   ; FORM IS NOW STILL ABAB0000
                    PLA                             ; PARTIALLY MODIFIED GBASH
                    BCS ?4                          ; ...ALWAYS
?3                  ADC #$1F                        ; 
?4                  ROR MON_GBASL                   ; SHIFT IN E, TO GET EABAB000 FORM
?5                  ADC #$FC                        ; FINISH GBASH MODS
UD_1                STA MON_GBASH                   ; 
                    RTS
                                                    ; --------------------------------
                    CLC                             ; <<<NEVER USED>>>
                                                    ; --------------------------------
                                                    ; MOVE DOWN ONE PIXEL
                                                    ; IF ALREADY AT BOTTOM, GO TO TOP
                                                    ; 
                                                    ; REMEMBER:  Y-COORD   GBASH     GBASL
                                                    ; ABCDEFGH  PPPFGHCD  EABAB000
                                                    ; --------------------------------
MOVE_DOWN
                    LDA MON_GBASH                   ; TRY IT FIRST, BY FGH=FGH+1
                    ADC #4                          ; GBASH = PPPFGHCD
CON_04              = *-1                           ; (( CONSTANT ))
                    BIT CON_1C                      ; IS FGH FIELD NOW ZERO?
                    BNE UD_1                        ; NO, SO WE ARE FINISHED
                                                    ; YES, RIPPLE THE CARRY AS HIGH
                                                    ; AS NECESSARY
                    ASL MON_GBASL                   ; LOOK AT "E" BIT
                    BCC ?2                          ; NOW ZERO; MAKE IT 1 AND LEAVE
                    ADC #$E0                        ; CARRY = 1, SO ADDS $E1
                    CLC                             ; IS "CD" NOT ZERO?
                    BIT CON_04                      ; TESTS BIT 2 FOR CARRY OUT OF "CD"
                    BEQ ?3                          ; NO CARRY, FINISHED
                                                    ; INCREMENT "AB" THEN
                                                    ; 0000 --> 0101
                                                    ; 0101 --> 1010
                                                    ; 1010 --> WRAP AROUND TO LINE 0
                    LDA MON_GBASL                   ; 0000  0101  1010
                    ADC #$50                        ; 0101  1010  1111
                    EOR #$F0                        ; 1010  0101  0000
                    BEQ ?1                          ; 
                    EOR #$F0                        ; 0101  1010
?1                  STA MON_GBASL                   ; NEW ABAB0000
                    LDA HGR_PAGE                    ; WRAP AROUND TO LINE ZERO OF GROUP
                    BCC ?3                          ; ...ALWAYS
?2                  ADC #$E0
?3                  ROR MON_GBASL
                    BCC UD_1                        ; ...ALWAYS
                                                    ; --------------------------------
                                                    ; HLINRL IS NEVER CALLED BY APPLESOFT
                                                    ; 
                                                    ; ENTER WITH:  (A,X) = DX FROM CURRENT POINT
                                                    ; (Y)   = DY FROM CURRENT POINT
                                                    ; --------------------------------
HLINRL              PHA                             ; SAVE (A)
                    LDA #0                          ; CLEAR CURRENT POINT SO HGLIN WILL
                    STA HGR_X                       ; ACT RELATIVELY
                    STA HGR_X+1                     ; 
                    STA HGR_Y                       ; 
                    PLA                             ; RESTORE (A)
                                                    ; --------------------------------
                                                    ; DRAW LINE FROM LAST PLOTTED POINT TO (A,X),(Y)
                                                    ; 
                                                    ; ENTER WITH:  (A,X) = X OF TARGET POINT
                                                    ; (Y)   = Y OF TARGET POINT
                                                    ; --------------------------------
HGLIN               PHA                             ; COMPUTE DX = X- X0
                    SEC
                    SBC HGR_X
                    PHA
                    TXA
                    SBC HGR_X+1
                    STA HGR_QUADRANT                ; SAVE DX SIGN (+ = RIGHT, - = LEFT)
                    BCS ?1                          ; NOW FIND ABS (DX)
                    PLA                             ; FORMS 2'S COMPLEMENT
                    EOR #$FF
                    ADC #1
                    PHA
                    LDA #0
                    SBC HGR_QUADRANT
?1                  STA HGR_DX+1
                    STA HGR_E+1                     ; INIT HGR.E TO ABS(X-X0)
                    PLA
                    STA HGR_DX
                    STA HGR_E
                    PLA
                    STA HGR_X                       ; TARGET X POINT
                    STX HGR_X+1                     ; 
                    TYA                             ; TARGET Y POINT
                    CLC                             ; COMPUTE DY = Y-HGR.Y
                    SBC HGR_Y                       ; AND SAVE -ABS(Y-HGR.Y)-1 IN HGR.DY
                    BCC ?2                          ; (SO + MEANS UP, - MEANS DOWN)
                    EOR #$FF                        ; 2'S COMPLEMENT OF DY
                    ADC #$FE                        ; 
?2                  STA HGR_DY                      ; 
                    STY HGR_Y                       ; TARGET Y POINT
                    ROR HGR_QUADRANT                ; SHIFT Y-DIRECTION INTO QUADRANT
                    SEC                             ; COUNT = DX -(-DY) = # OF DOTS NEEDED
                    SBC HGR_DX                      ; 
                    TAX                             ; COUNTL IS IN X-REG
                    LDA #$FF
                    SBC HGR_DX+1
                    STA HGR_COUNT
                    LDY HGR_HORIZ                   ; HORIZONTAL INDEX
                    BCS MOVEX2                      ; ...ALWAYS
                                                    ; --------------------------------
                                                    ; MOVE LEFT OR RIGHT ONE PIXEL
                                                    ; (A) BIT 6 HAS DIRECTION
                                                    ; --------------------------------
MOVEX               ASL                             ; PUT BIT 6 INTO SIGN POSITION
                    JSR MOVE_LEFT_OR_RIGHT
                    SEC
                                                    ; --------------------------------
                                                    ; DRAW LINE NOW
                                                    ; --------------------------------
MOVEX2              LDA HGR_E                       ; CARRY IS SET
                    ADC HGR_DY                      ; E = E-DELTY
                    STA HGR_E                       ; NOTE: DY IS (-DELTA Y)-1
                    LDA HGR_E+1                     ; CARRY CLR IF HGR.E GOES NEGATIVE
                    SBC #0
?1                  STA HGR_E+1
                    LDA (MON_GBASL),Y
                    EOR HGR_BITS                    ; PLOT A DOT
                    AND MON_HMASK
                    EOR (MON_GBASL),Y
                    STA (MON_GBASL),Y
                    INX                             ; FINISHED ALL THE DOTS?
                    BNE ?2                          ; NO
                    INC HGR_COUNT                   ; TEST REST OF COUNT
                    BEQ RTS_22                      ; YES, FINISHED.
?2                  LDA HGR_QUADRANT                ; TEST DIRECTION
                    BCS MOVEX                       ; NEXT MOVE IS IN THE X DIRECTION
                    JSR MOVE_UP_OR_DOWN             ; IF CLR, NEG, MOVE
                    CLC                             ; E = E+DX
                    LDA HGR_E
                    ADC HGR_DX
                    STA HGR_E
                    LDA HGR_E+1
                    ADC HGR_DX+1
                    BVC ?1                          ; ...ALWAYS
                                                    ; --------------------------------


MSKTBL              ASM_DATA(%10000001)
                    ASM_DATA(%10000010)
                    ASM_DATA(%10000100)
                    ASM_DATA(%10001000)
                    ASM_DATA(%10010000)
                    ASM_DATA(%10100000)
                    ASM_DATA(%11000000)
                                                    ; --------------------------------
CON_1C              ASM_DATA(%00011100)                ; MASK FOR "FGH" BITS
                                                    ; --------------------------------

                                                    ; --------------------------------
                                                    ; TABLE OF COS(90*X/16 DEGREES)*$100 - 1
                                                    ; WITH ONE BYTE PRECISION, X=0 TO 16:
                                                    ; --------------------------------
COSINE_TABLE        ASM_DATA($FF,$FE,$FA,$F4,$EC,$E1,$D4,$C5)
                    ASM_DATA($B4,$A1,$8D,$78,$61,$49,$31,$18)
                    ASM_DATA($FF)
                                                    ; --------------------------------
                                                    ; HFIND -- CALCULATES CURRENT POSITION OF HI-RES CURSOR
                                                    ; (NOT CALLED BY ANY APPLESOFT ROUTINE)
                                                    ; 
                                                    ; CALCULATE Y-COORD FROM GBASL,H
                                                    ; AND X-COORD FROM HORIZ AND HMASK
                                                    ; --------------------------------
HFIND               LDA MON_GBASL                   ; GBASL = EABAB000
                    ASL                             ; E INTO CARRY
                    LDA MON_GBASH                   ; GBASH = PPPFGHCD
                    AND #3                          ; 000000CD
                    ROL                             ; 00000CDE
                    ORA MON_GBASL                   ; EABABCDE
                    ASL                             ; ABABCDE0
                    ASL                             ; BABCDE00
                    ASL                             ; ABCDE000
                    STA HGR_Y                       ; ALL BUT FGH
                    LDA MON_GBASH                   ; PPPFGHCD
                    LSR                             ; 0PPPFGHC
                    LSR                             ; 00PPPFGH
                    AND #7                          ; 00000FGH
                    ORA HGR_Y                       ; ABCDEFGH
                    STA HGR_Y                       ; THAT TAKES CARE OF Y-COORDINATE!
                    LDA HGR_HORIZ                   ; X = 7*HORIZ + BIT POS. IN HMASK
                    ASL                             ; MULTIPLY BY 7
                    ADC HGR_HORIZ                   ; 3* SO FAR
                    ASL                             ; 6*
                    TAX                             ; SINCE 7* MIGHT NOT FIT IN 1 BYTE,
                                                    ; WAIT TILL LATER FOR LAST ADD
                    DEX                             ; 
                    LDA MON_HMASK                   ; NOW FIND BIT POSITION IN HMASK
                    AND #$7F                        ; ONLY LOOK AT LOW SEVEN
?1                  INX                             ; COUNT A SHIFT
                    LSR                             ; 
                    BNE ?1                          ; STILL IN THERE
                    STA HGR_X+1                     ; ZERO TO HI-BYTE
                    TXA                             ; 6*HORIZ+LOG2(HMASK)
                    CLC                             ; ADD HORIZ ONE MORE TIME
                    ADC HGR_HORIZ                   ; 7*HORIZ+LOG2(HMASK)
                    BCC ?2                          ; UPPER BYTE = 0
                    INC HGR_X+1                     ; UPPER BYTE = 1
?2                  STA HGR_X                       ; STORE LOWER BYTE
RTS_22              RTS
                                                    ; --------------------------------
                                                    ; DRAW A SHAPE
                                                    ; 
                                                    ; (Y,X) = SHAPE STARTING ADDRESS
                                                    ; (A)   = ROTATION (0-3F)
                                                    ; --------------------------------
                                                    ; APPLESOFT DOES NOT CALL DRAW0
                                                    ; --------------------------------
DRAW0               STX HGR_SHAPE                   ; SAVE SHAPE ADDRESS
                    STY HGR_SHAPE+1
                                                    ; --------------------------------
                                                    ; APPLESOFT ENTERS HERE
                                                    ; --------------------------------
DRAW1               TAX                             ; SAVE ROTATION (0-$3F)
                    LSR                             ; DIVIDE ROTATION BY 16 TO GET
                    LSR                             ; QUADRANT (0=UP, 1=RT, 2=DWN, 3=LFT)
                    LSR
                    LSR
                    STA HGR_QUADRANT
                    TXA                             ; USE LOW 4 BITS OF ROTATION TO INDEX
                    AND #$0F                        ; THE TRIG TABLE
                    TAX
                    LDY COSINE_TABLE,X              ; SAVE COSINE IN HGR.DX
                    STY HGR_DX                      ; 
                    EOR #$F                         ; AND SINE IN DY
                    TAX
                    LDY COSINE_TABLE+1,X
                    INY
                    STY HGR_DY
                    LDY HGR_HORIZ                   ; INDEX FROM GBASL,H TO BYTE WE'RE IN
                    LDX #0
                    STX HGR_COLLISIONS              ; CLEAR COLLISION COUNTER
                    LDA (HGR_SHAPE,X)               ; GET FIRST BYTE OF SHAPE DEFN
?1                  STA HGR_DX+1                    ; KEEP SHAPE BYTE IN HGR.DX+1
                    LDX #$80                        ; INITIAL VALUES FOR FRACTIONAL VECTORS
                    STX HGR_E                       ; ?5 IN COSINE COMPONENT
                    STX HGR_E+1                     ; ?5 IN SINE COMPONENT
                    LDX HGR_SCALE                   ; SCALE FACTOR
?2                  LDA HGR_E                       ; ADD COSINE VALUE TO X-VALUE
                    SEC                             ; IF >= 1, THEN DRAW
                    ADC HGR_DX                      ; 
                    STA HGR_E                       ; ONLY SAVE FRACTIONAL PART
                    BCC ?3                          ; NO INTEGRAL PART
                    JSR LRUD1                       ; TIME TO PLOT COSINE COMPONENT
                    CLC                             ; 
?3                  LDA HGR_E+1                     ; ADD SINE VALUE TO Y-VALUE
                    ADC HGR_DY                      ; IF >= 1, THEN DRAW
                    STA HGR_E+1                     ; ONLY SAVE FRACTIONAL PART
                    BCC ?4                          ; NO INTEGRAL PART
                    JSR LRUD2                       ; TIME TO PLOT SINE COMPONENT
?4                  DEX                             ; LOOP ON SCALE FACTOR.
                    BNE ?2                          ; STILL ON SAME SHAPE ITEM
                    LDA HGR_DX+1                    ; GET NEXT SHAPE ITEM
                    LSR                             ; NEXT 3 BIT VECTOR
                    LSR                             ; 
                    LSR                             ; 
                    BNE ?1                          ; MORE IN THIS SHAPE BYTE
                    INC HGR_SHAPE                   ; GO TO NEXT SHAPE BYTE
                    BNE ?5
                    INC HGR_SHAPE+1
?5                  LDA (HGR_SHAPE,X)               ; NEXT BYTE OF SHAPE DEFINITION
                    BNE ?1                          ; PROCESS IF NOT ZERO
                    RTS                             ; FINISHED
                                                    ; --------------------------------
                                                    ; XDRAW A SHAPE (SAME AS DRAW, EXCEPT TOGGLES SCREEN)
                                                    ; 
                                                    ; (Y,X) = SHAPE STARTING ADDRESS
                                                    ; (A)   = ROTATION (0-3F)
                                                    ; --------------------------------
                                                    ; APPLESOFT DOES NOT CALL XDRAW0
                                                    ; --------------------------------
XDRAW0              STX HGR_SHAPE                   ; SAVE SHAPE ADDRESS
                    STY HGR_SHAPE+1
                                                    ; --------------------------------
                                                    ; APPLESOFT ENTERS HERE
                                                    ; --------------------------------
XDRAW1              TAX                             ; SAVE ROTATION (0-$3F)
                    LSR                             ; DIVIDE ROTATION BY 16 TO GET
                    LSR                             ; QUADRANT (0=UP, 1=RT, 2=DWN, 3=LFT)
                    LSR
                    LSR
                    STA HGR_QUADRANT
                    TXA                             ; USE LOW 4 BITS OF ROTATION TO INDEX
                    AND #$0F                        ; THE TRIG TABLE
                    TAX
                    LDY COSINE_TABLE,X              ; SAVE COSINE IN HGR.DX
                    STY HGR_DX                      ; 
                    EOR #$F                         ; AND SINE IN DY
                    TAX
                    LDY COSINE_TABLE+1,X
                    INY
                    STY HGR_DY
                    LDY HGR_HORIZ                   ; INDEX FROM GBASL,H TO BYTE WE'RE IN
                    LDX #0
                    STX HGR_COLLISIONS              ; CLEAR COLLISION COUNTER
                    LDA (HGR_SHAPE,X)               ; GET FIRST BYTE OF SHAPE DEFN
?1                  STA HGR_DX+1                    ; KEEP SHAPE BYTE IN HGR.DX+1
                    LDX #$80                        ; INITIAL VALUES FOR FRACTIONAL VECTORS
                    STX HGR_E                       ; ?5 IN COSINE COMPONENT
                    STX HGR_E+1                     ; ?5 IN SINE COMPONENT
                    LDX HGR_SCALE                   ; SCALE FACTOR
?2                  LDA HGR_E                       ; ADD COSINE VALUE TO X-VALUE
                    SEC                             ; IF >= 1, THEN DRAW
                    ADC HGR_DX                      ; 
                    STA HGR_E                       ; ONLY SAVE FRACTIONAL PART
                    BCC ?3                          ; NO INTEGRAL PART
                    JSR LRUDX1                      ; TIME TO PLOT COSINE COMPONENT
                    CLC                             ; 
?3                  LDA HGR_E+1                     ; ADD SINE VALUE TO Y-VALUE
                    ADC HGR_DY                      ; IF >= 1, THEN DRAW
                    STA HGR_E+1                     ; ONLY SAVE FRACTIONAL PART
                    BCC ?4                          ; NO INTEGRAL PART
                    JSR LRUDX2                      ; TIME TO PLOT SINE COMPONENT
?4                  DEX                             ; LOOP ON SCALE FACTOR.
                    BNE ?2                          ; STILL ON SAME SHAPE ITEM
                    LDA HGR_DX+1                    ; GET NEXT SHAPE ITEM
                    LSR                             ; NEXT 3 BIT VECTOR
                    LSR                             ; 
                    LSR                             ; 
                    BNE ?1                          ; MORE IN THIS SHAPE BYTE
                    INC HGR_SHAPE                   ; GO TO NEXT SHAPE BYTE
                    BNE ?5
                    INC HGR_SHAPE+1
?5                  LDA (HGR_SHAPE,X)               ; NEXT BYTE OF SHAPE DEFINITION
                    BNE ?1                          ; PROCESS IF NOT ZERO
                    RTS                             ; FINISHED
                                                    ; --------------------------------
                                                    ; GET HI-RES PLOTTING COORDINATES (0-279,0-191) FROM
                                                    ; TXTPTR.  LEAVE REGISTERS SET UP FOR HPOSN:
                                                    ; (Y,X)=X-COORD
                                                    ; (A)  =Y-COORD
                                                    ; --------------------------------
HFNS                JSR FRMNUM                      ; EVALUATE EXPRESSION, MUST BE NUMERIC
                    JSR GETADR                      ; CONVERT TO 2-BYTE INTEGER IN LINNUM
                    LDY LINNUM+1                    ; GET HORIZ COOR IN X,Y
                    LDX LINNUM                      ; 
                    CPY #>280                       ; MAKE SURE IT IS < 280
                    BCC ?1                          ; IN RANGE
                    BNE GGERR                       ; 
                    CPX #<280                       ; 
                    BCS GGERR                       ; 
?1                  TXA                             ; SAVE HORIZ COOR ON STACK
                    PHA                             ; 
                    TYA                             ; 
                    PHA                             ; 
                    LDA #HICHAR(`,')                        ; REQUIRE A COMMA
                    JSR SYNCHR                      ; 
                    JSR GETBYT                      ; EVAL EXP TO SINGLE BYTE IN X-REG
                    CPX #192                        ; CHECK FOR RANGE
                    BCS GGERR                       ; TOO BIG
                    STX FAC                         ; SAVE Y-COORD
                    PLA                             ; RETRIEVE HORIZONTAL COORDINATE
                    TAY                             ; 
                    PLA                             ; 
                    TAX                             ; 
                    LDA FAC                         ; AND VERTICAL COORDINATE
                    RTS                             ; 
                                                    ; --------------------------------
GGERR               JMP GOERR                       ; ILLEGAL QUANTITY ERROR
                                                    ; --------------------------------
                                                    ; "HCOLOR=" STATEMENT
                                                    ; --------------------------------
HCOLOR              JSR GETBYT                      ; EVAL EXP TO SINGLE BYTE IN X
                    CPX #8                          ; VALUE MUST BE 0-7
                    BCS GGERR                       ; TOO BIG
                    LDA COLORTBL,X                  ; GET COLOR PATTERN
                    STA HGR_COLOR
RTS_23              RTS
                                                    ; --------------------------------


COLORTBL            ASM_DATA(%00000000)
                    ASM_DATA(%00101010)
                    ASM_DATA(%01010101)
                    ASM_DATA(%01111111)
                    ASM_DATA(%00000000 | %10000000)
                    ASM_DATA(%00101010 | %10000000)
                    ASM_DATA(%01010101 | %10000000)
                    ASM_DATA(%01111111 | %10000000)

                                                    ; --------------------------------
                                                    ; "HPLOT" STATEMENT
                                                    ; 
                                                    ; HPLOT X,Y
                                                    ; HPLOT TO X,Y
                                                    ; HPLOT X1,Y1 TO X2,Y2
                                                    ; --------------------------------
HPLOT               CMP #TOKEN_TO                   ; "PLOT TO" FORM?
                    BEQ ?2                          ; YES, START FROM CURRENT LOCATION
                    JSR HFNS                        ; NO, GET STARTING POINT OF LINE
                    JSR HPLOT0                      ; PLOT THE POINT, AND SET UP FOR
                                                    ; DRAWING A LINE FROM THAT POINT
?1                  JSR CHRGOT                      ; CHARACTER AT END OF EXPRESSION
                    CMP #TOKEN_TO                   ; IS A LINE SPECIFIED?
                    BNE RTS_23                      ; NO, EXIT
?2                  JSR SYNCHR                      ; YES. ADV. TXTPTR (WHY NOT CHRGET)
                    JSR HFNS                        ; GET COORDINATES OF LINE END
                    STY DSCTMP                      ; SET UP FOR LINE
                    TAY                             ; 
                    TXA                             ; 
                    LDX DSCTMP                      ; 
                    JSR HGLIN                       ; PLOT LINE
                    JMP ?1                          ; LOOP TILL NO MORE "TO" PHRASES
                                                    ; --------------------------------
                                                    ; "ROT=" STATEMENT
                                                    ; --------------------------------
ROT                 JSR GETBYT                      ; EVAL EXP TO A BYTE IN X-REG
                    STX HGR_ROTATION
                    RTS
                                                    ; --------------------------------
                                                    ; "SCALE=" STATEMENT
                                                    ; --------------------------------
SCALE               JSR GETBYT                      ; EVAL EXP TO A BYTE IN X-REG
                    STX HGR_SCALE
                    RTS
                                                    ; --------------------------------
                                                    ; SET UP FOR DRAW AND XDRAW
                                                    ; --------------------------------
DRWPNT              JSR GETBYT                      ; GET SHAPE NUMBER IN X-REG
                    LDA HGR_SHAPE_PNTR              ; SEARCH FOR THAT SHAPE
                    STA HGR_SHAPE                   ; SET UP PNTR TO BEGINNING OF TABLE
                    LDA HGR_SHAPE_PNTR+1
                    STA HGR_SHAPE+1
                    TXA
                    LDX #0
                    CMP (HGR_SHAPE,X)               ; COMPARE TO # OF SHAPES IN TABLE
                    BEQ ?1                          ; LAST SHAPE IN TABLE
                    BCS GGERR                       ; SHAPE # TOO LARGE
?1                  ASL                             ; DOUBLE SHAPE# TO MAKE AN INDEX
                    BCC ?2                          ; ADD 256 IF SHAPE # > 127
                    INC HGR_SHAPE+1
                    CLC
?2                  TAY                             ; USE INDEX TO LOOK UP OFFSET FOR SHAPE
                    LDA (HGR_SHAPE),Y               ; IN OFFSET TABLE
                    ADC HGR_SHAPE
                    TAX
                    INY
                    LDA (HGR_SHAPE),Y
                    ADC HGR_SHAPE_PNTR+1
                    STA HGR_SHAPE+1                 ; SAVE ADDRESS OF SHAPE
                    STX HGR_SHAPE
                    JSR CHRGOT                      ; IS THERE ANY "AT" PHRASE?
                    CMP #TOKENDB                    ; 
                    BNE ?3                          ; NO, DRAW RIGHT WHERE WE ARE
                    JSR SYNCHR                      ; SCAN OVER "AT"
                    JSR HFNS                        ; GET X- AND Y-COORDS TO START DRAWING AT
                    JSR HPOSN                       ; SET UP CURSOR THERE
?3                  LDA HGR_ROTATION                ; ROTATION VALUE
                    RTS
                                                    ; --------------------------------
                                                    ; "DRAW" STATEMENT
                                                    ; --------------------------------
DRAW                JSR DRWPNT
                    JMP DRAW1
                                                    ; --------------------------------
                                                    ; "XDRAW" STATEMENT
                                                    ; --------------------------------
XDRAW               JSR DRWPNT
                    JMP XDRAW1
                                                    ; --------------------------------
                                                    ; "SHLOAD" STATEMENT
                                                    ; 
                                                    ; READS A SHAPE TABLE FROM CASSETTE TAPE
                                                    ; TO A POSITION JUST BELOW HIMEM.
                                                    ; HIMEM IS THEN MOVED TO JUST BELOW THE TABLE
                                                    ; --------------------------------
SHLOAD              LDA #>LINNUM                    ; SET UP TO READ TWO BYTES
                    STA MON_A1H                     ; INTO LINNUM,LINNUM+1
                    STA MON_A2H                     ; 
                    LDY #LINNUM                     ; 
                    STY MON_A1L                     ; 
                    INY                             ; LINNUM+1
                    STY MON_A2L                     ; 
                    JSR MON_READ                    ; READ TAPE
                    CLC                             ; SETUP TO READ (LINNUM) BYTES
                    LDA MEMSIZ                      ; ENDING AT HIMEM-1
                    TAX                             ; 
                    DEX                             ; FORMING HIMEM-1
                    STX MON_A2L                     ; 
                    SBC LINNUM                      ; FORMING HIMEM-(LINNUM)
                    PHA                             ; 
                    LDA MEMSIZ+1                    ; 
                    TAY                             ; 
                    INX                             ; SEE IF HIMEM LOW-BYTE WAS ZERO
                    BNE ?1                          ; NO
                    DEY                             ; YES, HAVE TO DECREMENT HIGH BYTE
?1                  STY MON_A2H                     ; 
                    SBC LINNUM+1                    ; 
                    CMP STREND+1                    ; RUNNING INTO VARIABLES?
                    BCC ?2                          ; YES, OUT OF MEMORY
                    BNE ?3                          ; NO, STILL ROOM
?2                  JMP MEMERR                      ; MEM FULL ERR
?3                  STA MEMSIZ+1                    ; 
                    STA FRETOP+1                    ; CLEAR STRING SPACE
                    STA MON_A1H                     ; (BUT NAMES ARE STILL IN VARTBL!)
                    STA HGR_SHAPE_PNTR+1
                    PLA
                    STA HGR_SHAPE_PNTR
                    STA MEMSIZ
                    STA FRETOP
                    STA MON_A1L
                    JSR MON_RD2BIT                  ; READ TO TAPE TRANSITIONS
                    LDA #3                          ; SHORT DELAY FOR INTERMEDIATE HEADER
                    JMP MON_READ2                   ; READ SHAPES
                                                    ; --------------------------------
                                                    ; CALLED FROM STORE AND RECALL
                                                    ; --------------------------------
TAPEPNT
                    CLC
                    LDA LOWTR
                    ADC LINNUM
                    STA MON_A2L
                    LDA LOWTR+1
                    ADC LINNUM+1
                    STA MON_A2H
                    LDY #4
                    LDA (LOWTR),Y
                    JSR GETARY2
                    LDA HIGHDS
                    STA MON_A1L
                    LDA HIGHDS+1
                    STA MON_A1H
                    RTS
                                                    ; --------------------------------
                                                    ; CALLED FROM STORE AND RECALL
                                                    ; --------------------------------
GETARYPT
                    LDA #$40
                    STA SUBFLG
                    JSR PTRGET
                    LDA #0
                    STA SUBFLG
                    JMP VARTIO
                                                    ; --------------------------------
                                                    ; "HTAB" STATEMENT
                                                    ; 
                                                    ; NOTE THAT IF WNDLEFT IS NOT 0, HTAB CAN PRINT
                                                    ; OUTSIDE THE SCREEN (EG., IN THE PROGRAM)
                                                    ; --------------------------------
HTAB                JSR GETBYT
                    DEX
                    TXA
?1                  CMP #40
                    BCC ?2
                    SBC #40
                    PHA
                    JSR CRDO
                    PLA
                    JMP ?1
?2                  STA MON_CH
                    RTS
                                                    ; --------------------------------
                    .HASCII "KRW"                   ; UNKNOWN
