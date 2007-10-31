         .FEATURE LABELS_WITHOUT_COLONS

         .EXPORT MON
         .EXPORT BELL
         .EXPORT CROUT
         .EXPORT COUT
         .EXPORT PRBYTE
         .EXPORT PRYX2
         .EXPORT RESET
         .EXPORT RESTORE
         .EXPORT SAV1
         .EXPORT SAVE
         .IF VERSION >= 2
         .EXPORT SETNORM
         .EXPORT SETVID
         .EXPORT SETKBD
         .EXPORT COUT1
         .ENDIF

         ;KEYIN
         .IMPORT GETLNZ
         .IMPORT KEYIN
         ;DISPLAY2
         .IMPORT NXTA1
         .IMPORT NXTA4
         .IF VERSION >= 2
         .IMPORT LFB78
         .ELSE
         .IMPORT VIDOUT
         .ENDIF
         ;DISPLAY1
         .IMPORT INIT
         ;CASETTE
         .IMPORT HEADR
         .IMPORT WRBIT
         .IMPORT RDBYTE
         .IMPORT RD2BIT
         .IMPORT RDBIT
         ;DEBUG
         .IMPORT STEP
         .IMPORT REGDSP
         ;DISASM
         .IMPORT PCADJ
         .IMPORT INSTDSP
         .IMPORT PRNTYX

         .INCLUDE "symbols.s65"
         .INCLUDE "hascmap.s65"

;-----------------------------------------------------------------------
;
; MONITOR COMMANDS
;
; Handles monitor commands, such as L (list) and G (go).
;
;-----------------------------------------------------------------------

IOADR    =     $C000

BASIC    =     $E000
BASIC2   =     $E003

                          ; ASCII
CTRL_B   =     $02 | %10000000
CTRL_C   =     $03 | %10000000
CTRL_E   =     $05 | %10000000
CTRL_K   =     $0B | %10000000
ASCCR    =     $0D | %10000000
CTRL_P   =     $10 | %10000000
CTRL_Y   =     $19 | %10000000



CROUT    LDA   #$8D
         BNE   COUT
PRA1     LDY   A1H        ;PRINT CR,A1 IN HEX
         LDX   A1L
PRYX2    JSR   CROUT
         JSR   PRNTYX
         LDY   #$00
         LDA   #'-'       ;PRINT '-'
         JMP   COUT






XAM8     LDA   A1L
         ORA   #%00000111 ;SET TO FINISH AT
         STA   A2L        ;  MOD 8=7
         LDA   A1H
         STA   A2H
MODSCHK  LDA   A1L
         AND   #%00000111
         BNE   DATAOUT
XAM      JSR   PRA1
DATAOUT  LDA   #' '
         JSR   COUT       ;OUTPUT BLANK
         LDA   (A1L),Y
         JSR   PRBYTE     ;OUTPUT BYTE IN HEX
         JSR   NXTA1
         BCC   MODSCHK    ;CHECK IF TIME TO,
RTS4C    RTS              ;  PRINT ADDR

XAMPM    LSR   A          ;DETERMINE IF MON
         BCC   XAM        ;  MODE IS XAM
         LSR   A          ;  ADD, OR SUB
         LSR   A
         LDA   A2L
         BCC   ADD
         EOR   #$FF       ;SUB: FORM 2'S COMPLEMENT
ADD      ADC   A1L
         PHA
         LDA   #'='
         JSR   COUT       ;PRINT '=', THEN RESULT
         PLA





PRBYTE   PHA              ;PRINT BYTE AS 2 HEX
         LSR   A          ;  DIGITS, DESTROYS A-REG
         LSR   A
         LSR   A
         LSR   A
         JSR   PRHEXZ
         PLA
PRHEX    AND   #%00001111 ;PRINT HEX DIG IN A-REG
PRHEXZ   ORA   #'0'       ;  LSB'S
         CMP   #'9'+1
         BCC   COUT
         ADC   #$06



COUT     JMP   (CSWL)     ;VECTOR TO USER OUTPUT ROUTINE
COUT1    CMP   #' '
         BCC   COUTZ      ;DON'T OUTPUT CTRL'S INVERSE
         AND   INVFLG     ;MASK WITH INVERSE FLAG
COUTZ    STY   YSAV1      ;SAV Y-REG
         PHA              ;SAV A-REG
         .IF VERSION >= 2
         JSR   LFB78
         .ELSE
         JSR   VIDOUT     ;OUTPUT A-REG AS ASCII
         .ENDIF
         PLA              ;RESTORE A-REG
         LDY   YSAV1      ;  AND Y-REG
         RTS              ;  THEN RETURN



BL1      DEC   YSAV
         BEQ   XAM8

BLANK    DEX              ;BLANK TO MON
         BNE   SETMDZ     ;AFTER BLANK

         CMP   #$BA       ;DATA STORE MODE?
         BNE   XAMPM      ;  NO, XAM, ADD, OR SUB
STOR     STA   MODE       ;KEEP IN STORE MODE
         LDA   A2L
         STA   (A3L),Y    ;STORE AS LOW BYTE AS (A3)
         INC   A3L
         BNE   RTS5       ;INCR A3, RETURN
         INC   A3H
RTS5     RTS

SETMODE  LDY   YSAV       ;SAVE CONVERTED ':', '+',
         LDA   IN-1,Y     ;  '-', '.' AS MODE.
SETMDZ   STA   MODE
         RTS

LT       LDX   #$01
LT2      LDA   A2L,X      ;COPY A2 (2 BYTES) TO
         STA   A4L,X      ;  A4 AND A5
         STA   A5L,X
         DEX
         BPL   LT2
         RTS

MOVE     LDA   (A1L),Y    ;MOVE (A1 TO A2) TO
         STA   (A4L),Y    ;  (A4)
         JSR   NXTA4
         BCC   MOVE
         RTS

VFY      LDA   (A1L),Y    ;VERIFY (A1 TO A2) WITH
         CMP   (A4L),Y    ;  (A4)
         BEQ   VFYOK
         JSR   PRA1
         LDA   (A1L),Y
         JSR   PRBYTE
         LDA   #' '
         JSR   COUT
         LDA   #'('
         JSR   COUT
         LDA   (A4L),Y
         JSR   PRBYTE
         LDA   #')'
         JSR   COUT
VFYOK    JSR   NXTA4
         BCC   VFY
         RTS

LIST1    JSR   A1PC       ;MOVE A1 (2 BYTES) TO
         LDA   #TEXTHEIGHT-4 ;  PC IF SPEC'D AND
LIST2    PHA              ;  DISEMBLE 20 INSTRS
         JSR   INSTDSP
         JSR   PCADJ      ;ADJUST PC EACH INSTR
         STA   PCL
         STY   PCH
         PLA
         SEC
         SBC   #1         ;NEXT OF 20 INSTRS
         BNE   LIST2
         RTS

A1PC     TXA              ;IF USER SPEC'D ADR
         BEQ   A1PCRTS    ;  COPY FROM A1 TO PC
A1PCLP   LDA   A1L,X
         STA   PCL,X
         DEX
         BPL   A1PCLP
A1PCRTS  RTS

SETINV   LDY   #$3F       ;SET FOR INVERSE VID
         BNE   SETIFLG    ; VIA COUT1
SETNORM  LDY   #$FF       ;SET FOR NORMAL VID
SETIFLG  STY   INVFLG
         RTS

SETKBD   LDA   #$00       ;SIMULATE PORT #0 INPUT (IN#0)
INPORT   STA   A2L        ;  SPECIFIED (KEYIN ROUTINE)
INPRT    LDX   #KSWL
         LDY   #<KEYIN
         BNE   IOPRT
SETVID   LDA   #$00       ;SIMULATE PORT #0 OUTPUT (PR#0)
OUTPORT  STA   A2L        ;  SPECIFIED (COUT1 ROUTINE)
OUTPRT   LDX   #CSWL
         LDY   #<COUT1
IOPRT    LDA   A2L        ;SET RAM IN/OUT VECTORS
         AND   #%00001111
         BEQ   IOPRT1
         ORA   #>IOADR
         LDY   #$00
         BEQ   IOPRT2
IOPRT1   LDA   #>COUT1
IOPRT2   STY   LOC0,X
         STA   LOC1,X
         RTS

         NOP
         NOP

XBASIC   JMP   BASIC      ;TO BASIC WITH SCRATCH

BASCONT  JMP   BASIC2     ;CONTINUE BASIC

GO       JSR   A1PC       ;ADR TO PC IF SPEC'D
         JSR   RESTORE    ;RESTORE META REGS
         JMP   (PCL)      ;GO TO USER SUBR

REGZ     JMP   REGDSP     ;TO REG DISPLAY

TRACE
         .IF VERSION < 2
         DEC   YSAV
STEPZ    JSR   A1PC       ;ADR TO PC IF SPEC'D
         JMP   STEP       ;TAKE ONE STEP
         .ELSE
         RTS
         NOP
         RTS
STEPZ    NOP
         NOP
         NOP
         NOP
         NOP
         .ENDIF

USR      JMP   USRADR     ;TO USR SUBR AT USRADR



WRITE    LDA   #$40
         JSR   HEADR      ;WRITE 10-SEC HEADER
         LDY   #$27
WR1      LDX   #$00
         EOR   (A1L,X)
         PHA
         LDA   (A1L,X)
         JSR   WRBYTE
         JSR   NXTA1
         LDY   #$1D
         PLA
         BCC   WR1
         LDY   #$22
         JSR   WRBYTE
         BEQ   BELL
WRBYTE   LDX   #$10
WRBYT2   ASL   A
         JSR   WRBIT
         BNE   WRBYT2
         RTS



CRMON    JSR   BL1        ;HANDLE A CR AS BLANK
         PLA              ;  THEN POP STACK
         PLA              ;  AND RTN TO MON
         BNE   MONZ






READ     JSR   RD2BIT     ;FIND TAPEIN EDGE
         LDA   #$16
         JSR   HEADR      ;DELAY 3.5 SECONDS
         STA   CHKSUM     ;INIT CHKSUM=$FF
         JSR   RD2BIT     ;FIND TAPEIN EDGE
RD2      LDY   #$24       ;LOOK FOR SYNC BIT
         JSR   RDBIT      ;  (SHORT 0)
         BCS   RD2        ;  LOOP UNTIL FOUND
         JSR   RDBIT      ;SKIP SECOND SYNC H-CYCLE
         LDY   #$3B       ;INDEX FOR 0/1 TEST
RD3      JSR   RDBYTE     ;READ A BYTE
         STA   (A1L,X)    ;STORE AT (A1)
         EOR   CHKSUM
         STA   CHKSUM     ;UPDATE RUNNING CHKSUM
         JSR   NXTA1      ;INC A1, COMPARE TO A2
         LDY   #$35       ;COMPENSATE 0/1 INDEX
         BCC   RD3        ;LOOP UNTIL DONE
         JSR   RDBYTE     ;READ CHKSUM BYTE
         CMP   CHKSUM
         BEQ   BELL       ;GOOD, SOUND BELL AND RETURN
PRERR    LDA   #'E'
         JSR   COUT       ;PRINT "ERR", THEN BELL
         LDA   #'R'
         JSR   COUT
         JSR   COUT

BELL     LDA   #$87       ;OUTPUT BELL AND RETURN
         JMP   COUT








RESTORE  LDA   STATUS     ;RESTORE 6502 REG CONTENTS
         PHA              ;  USED BY DEBUG SOFTWARE
         LDA   ACC
RESTR1   LDX   XREG
         LDY   YREG
         PLP
         RTS
SAVE     STA   ACC        ;SAVE 6502 REG CONTENTS
SAV1     STX   XREG
         STY   YREG
         PHP
         PLA
         STA   STATUS
         TSX
         STX   SPNT
         CLD
         RTS



RESET    JSR   SETNORM    ;NORMAL
         JSR   INIT       ;
         JSR   SETVID     ;PR#0
         JSR   SETKBD     ;IN#0
MON      CLD              ;MUST SET HEX MODE!
         JSR   BELL
MONZ     LDA   #$AA       ;'*' PROMPT FOR MON
         STA   PROMPT
         JSR   GETLNZ     ;READ A LINE
         JSR   ZMODE      ;CLEAR MON MODE, SCAN IDX
NXTITM   JSR   GETNUM     ;GET ITEM, NON-HEX
         STY   YSAV       ;  CHAR IN A-REG
         LDY   #$17       ;  X-REG=0 IF NO HEX INPUT
CHRSRCH  DEY
         BMI   MON        ;NOT FOUND, GO TO MON
         CMP   CHRTBL,Y   ;FIND CMND CHAR IN TEL
         BNE   CHRSRCH
         JSR   TOSUB      ;FOUND, CALL CORRESPONDING
         LDY   YSAV       ;  SUBROUTINE
         JMP   NXTITM



DIG      LDX   #$03
         ASL   A
         ASL   A          ;GOT HEX DIG,
         ASL   A          ;  SHIFT INTO A2
         ASL   A
NXTBIT   ASL   A
         ROL   A2L
         ROL   A2H
         DEX              ;LEAVE X=$FF IF DIG
         BPL   NXTBIT
NXTBAS   LDA   MODE
         BNE   NXTBS2     ;IF MODE IS ZERO
         LDA   A2H,X      ; THEN COPY A2 TO
         STA   A1H,X      ; A1 AND A3
         STA   A3H,X
NXTBS2   INX
         BEQ   NXTBAS
         BNE   NXTCHR

GETNUM   LDX   #$00       ;CLEAR A2
         STX   A2L
         STX   A2H
NXTCHR   LDA   IN,Y       ;GET CHAR
         INY
         EOR   #$B0
         CMP   #$0A
         BCC   DIG        ;IF HEX DIG, THEN
         ADC   #$88
         CMP   #$FA
         BCS   DIG
         RTS


TOSUB    LDA   #>GO       ;PUSH HIGH-ORDER
         PHA              ;  SUBR ADR ON STK
         LDA   SUBTBL,Y   ;PUSH LOW-ORDER
         PHA              ;  SUBR ADR ON STK
         LDA   MODE
ZMODE    LDY   #$00       ;CLR MODE, OLD MODE
         STY   MODE       ;  TO A-REG
         RTS              ; GO TO SUBR VIA RTS




         .DEFINE F(CHR) <(CHR^$B0+$89)

CHRTBL   .BYTE F(CTRL_C)
         .BYTE F(CTRL_Y)
         .BYTE F(CTRL_E)
         .IF VERSION < 2
         .BYTE F('T')
         .ELSE
         .BYTE F(CTRL_Y)
         .ENDIF
         .BYTE F('V')
         .BYTE F(CTRL_K)
         .IF VERSION < 2
         .BYTE F('S')
         .ELSE
         .BYTE F(CTRL_Y)
         .ENDIF
         .BYTE F(CTRL_P)
         .BYTE F(CTRL_B)
         .BYTE F('-')
         .BYTE F('+')
         .BYTE F('M')
         .BYTE F('<')
         .BYTE F('N')
         .BYTE F('I')
         .BYTE F('L')
         .BYTE F('W')
         .BYTE F('G')
         .BYTE F('R')
         .BYTE F(':')
         .BYTE F('.')
         .BYTE F(ASCCR)
         .BYTE F(' ')

SUBTBL   .BYTE <BASCONT-1
         .BYTE <USR-1
         .BYTE <REGZ-1
         .BYTE <TRACE-1
         .BYTE <VFY-1
         .BYTE <INPRT-1
         .BYTE <STEPZ-1
         .BYTE <OUTPRT-1
         .BYTE <XBASIC-1
         .BYTE <SETMODE-1
         .BYTE <SETMODE-1
         .BYTE <MOVE-1
         .BYTE <LT-1
         .BYTE <SETNORM-1
         .BYTE <SETINV-1
         .BYTE <LIST1-1
         .BYTE <WRITE-1
         .BYTE <GO-1
         .BYTE <READ-1
         .BYTE <SETMODE-1
         .BYTE <SETMODE-1
         .BYTE <CRMON-1
         .BYTE <BLANK-1
