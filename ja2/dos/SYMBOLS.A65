                .FEATURE LABELS_WITHOUT_COLONS, LEADING_DOT_IN_IDENTIFIERS

                .IFDEF FRANKLIN
VERSION         = 330
                .ENDIF

                .IFNDEF VERSION
                .ERROR "Must define label VERSION as version to build (e.g., -D VERSION=332)."
                .END
                .ENDIF




                                ; --------------------------------
                                ; ZERO-PAGE ADDRESSES
                                ; --------------------------------

CH              = $24           ; OFFSET FROM LEFT EDGE OF WINDOW
                                ; TO NEXT CHR POS'N ON SCRN LINE.
                                ; (RANGE:  $00-$23.)
PT2BTBUF        = $26           ; PTR ($26,$27) TO TARGET BUF USED
                                ; WHEN READING IN DOS DURING BOOT.
HOLDNIBL        = $26           ; TEMP REG USED FOR HOLDING TWO-
                                ; ENCODED NIBBLES WHEN WRITING
                                ; A SECTOR DURING RWTS'S FORMAT OR
                                ; WRITE OPERATIONS.
PROSCRTH        = $26           ; TEMP REG USED FOR:
                                ; - DECODING ODD/EVEN ADDR FIELD
                                ; BYTES.
                                ; - CNTR AND INDEX TO RWTS BUFFERS
                                ; (1 & 2) WHEN POSTNIBBLING.
                                ; - CNTR FOR # OF CHANCES TO FIND
                                ; THE CORRECT ADDR PROLOGUE.
STPSDONE        = $26           ; - CNTR FOR # OF HALFTRKS MOVED.
                                ; - CURRENT DISTANCE (EXPRESSED IN
                                ; HALFTRACKS) FROM THE START OR
                                ; DESTINATION HALFTRACK POS'NS.
FRMTSLOT        = $27           ; SLOT * 16 USED TO INDEX DRIVE
                                ; BASE ADDRESSES.
HOLDPRES        = $27           ; HOLDS CURRENT HALFTRACK POSITION
                                ; WHEN SEEKING.
CKSUMCAL        = $27           ; RUNNING CHKSUM CALCULATION USED
                                ; WHEN READING ODD-EVEN ENCODED
                                ; ADDRESS FIELD BYTES.
BASL            = $28           ; PTR ($28,$29) TO THE LEFT END OF
                                ; THE SCREEN LINE.
DESTRK          = $2A           ; DESTINATION HALFTRACK POS'N WHEN
                                ; SEEKING.
SLT16ZPG        = $2B           ; SLOT*16 USED TO INDEX DRIVE
                                ; FUNCTIONS.
CKSUMDSK        = $2C           ; DECODED CHECKUSM VAL READ FROM
                                ; ADDR FIELD (RWTS).
SECDSK          = $2D           ; PHYS SEC # FOUND IN ADDR FIELD.
TRKDSK          = $2E           ; TRK # FOUND IN ADDR FIELD.
VOLDSK          = $2F           ; VOL # FOUND IN ADDR FIELD.
PROMPT          = $33           ; PROMPT CHAR USED TO SIGNAL INPUT
                                ; IS REQUIRED.  ALSO USED AS A FLG
                                ; IN THE CMD PARSING & PROCESSING
                                ; ROUTINES.
DRVZPG          = $35           ; DENOTES DRIVE SELECTED:
                                ; NEG = DRIVE 1, POS = DRIVE 2.
CSW             = $36           ; MAIN OUTPUT HOOK (ALSO KNOWN AS
                                ; THE CHARACTER OUTPUT SWITCH).
                                ; PTR ($36,$37) TO DEVICE/ROUTINE
                                ; WHICH HANDLES OUTPUT CHARACTERS.
KSW             = $38           ; MAIN INPUT HOOK (ALSO KNOWN AS
                                ; THE KEYBOARD SWITCH).
                                ; PTR ($38,$39) TO DEVICE/ROUTINE
                                ; THAT HANDLES INPUT CHARACTERS.
PTR2DCT         = $3C           ; PTR ($3C,$3D) TO RWTS'S DEVICE
                                ; CHARACTERISTIC TABLE.
BOOTSEC         = $3D           ; PHYS SEC # WANTED WHEN BOOTING.
PTR2RDSC        = $3E           ; PTR ($3E,$3F) TO BOOT0'S READ-
                                ; SECTOR SUBROUT'N (BTRDSEC, $CS5C
                                ; WHERE S=SLOT #, NORMALLY=$C65C.)
PTR2BUF         = $3E           ; PTR ($3E,$EF) TO RWTS'S CURRENT
                                ; DATA BUFFER.
HOLDAA          = $3E           ; - HOLDS  A CONSTANT ($AA) FOR
                                ; PROLOGUE AND EPILOGUE BYTES
                                ; USED WHEN FORMATTING.
                                ; - ALSO USED AS A TEMP REG WHEN
                                ; CREATING ODD-EVEN ENCODED BYTS
                                ; ASSOC WITH THE ADDR FIELD.


                .IF VERSION < 330
FRMTVOL         = $2F           ; VOL # TO WRITE WHEN FORMATTING.
FRMTKCTR        = $41           ; TRK# TO WRITE/READ WHEN FORMAT-
                                ; TING/VERIFYING.
FRMTSEC         = $4B           ; HOLDS # OF SEC TO BE FORMATTED
ENC44MASK       = $4A           ; HOLDS 10101010 MASK FOR 4 & 4 ENCODING
                .ELSE
FRMTVOL         = $41           ; VOL # TO WRITE WHEN FORMATTING.
FRMTKCTR        = $44           ; TRK# TO WRITE/READ WHEN FORMAT-
                                ; TING/VERIFYING. ALSO ACTS AS A
                                ; CNTR OF THE # OF TRKS FORMATTED/
                                ; VERIFIED.
FRMTSEC         = $3F           ; HOLDS # OF SEC TO BE FORMATTED &
                                ; ALSO ACTS AS CNTR FOR # OF SECS
                                ; VERIFIED WHEN CHECKING TRK JUST
                                ; FORMATTED.
                .ENDIF

A3L             = $40           ; MULTIPURPOSE REG & PTR ($40,$41)
A3H             = $41
A4L             = $42           ; MULTIPURPOSE REG & PTR ($42,$43)
A4H             = $43
A5L             = $44           ; MULTIPURPOSE REG & PTR ($44,$45)
A5H             = $45

SYNCNTR         = $45           ; # OF SYNC BYTES TO WRT BTWN SECS
                                ; & DELAY CNTR TO LET SOME SYNCS
                                ; PASS BY READ HEAD WHEN VERIFYING
                                ; A TRK JUST FORMATTED.
MTRTIME         = $46           ; MOTOR-ON-TIME COUNT ($46,$47).
                                ; CNTR USED TO DETERMINE IF THE
                                ; DRIVE MOTOR HAS BEEN ON LONG
                                ; ENOUGH TO DO RELIABLE READING.
                                ; THE MOTOR IS CONSIDERED TO BE UP
                                ; TO SPEED AFTER APPROX. 1 SECOND
                                ; (AT WHICH TIME, MTRTIME = 0000).
PTR2IOB         = $48           ; PTR ($48,49) TO RWTS'S INPUT/
                                ; OUTPUT BLOCK (IOB).
STATUS          = $48           ; STATUS REGISTER SAVE AREA.
LOMEM           = $4A           ; PTR ($4A,$4B) TO START OF INTEGER
                                ; BASIC'S VARIABLE TABLE.
HIMEM           = $4C           ; PTR ($4C,$4D) TO END OF INTEGER
                                ; BASIC PRGM.
TXTTAB          = $67           ; PTR ($67,$68) TO START OF THE
                                ; APPLESOFT PRGM TEXT. DEFAULTS TO
                                ; $801 ON A COLDSTART (ONCE EXECU-
                                ; TION FLOWS INTO APPLESOFT).
                                ; PTR'S CONTENTS CAN BE CHANGED TO
                                ; ACCOMMODATE THE LOADING OF AN
                                ; APPLESOFT PRGM IN A NON-STANDARD
                                ; POSITION.
VARTAB          = $69           ; PTR ($69,$6A) TO START OF SIMPLE
                                ; VARIABLE TABLE.
FRETOP          = $6F           ; PTR ($6F,$70) TO START OF FREE
                                ; SPACE (LOCATED BTWN THE END OF
                                ; THE VARIABLE TBL & THE START OF
                                ; THE STRING VALUES).
MEMSIZ          = $73           ; PTR ($73,$74) TO HIGHEST MEMORY
                                ; LOCATION (PLUS 1) THAT IS AVAIL-
                                ; ABLE TO AN APPLESOFT PRGM.  (NOT
                                ; TO BE CONFUSED WITH THE END OF
                                ; THE APPLESOFT PRGM). CAN ALSO BE
                                ; EXPRESSED AS A PTR TO THE FIRST
                                ; BYTE OF THE DOS BUFFER REGION.
CURLIN          = $75           ; CURRENT APPLESOFT PROGRAM LINE
                                ; NUMBER ( $75,$76).  USED BY DOS
                                ; TO DETERMINE IF BASIC IS RUNNING.
                                ; (CURLIN+1 CONTAINS AN $FF WHEN
                                ; COMPUTER IS IN THE IMMED MODE.)
PRGEND          = $AF           ; PTR ($AF,$B0) TO END OF APPLESOFT
                                ; PRGM (PLUS 1) IF FP CMD WAS USED
                                ; OR TO END OF PRGM (PLUS 2) IF A
                                ; NEW CMD WAS USED.
INTPGMST        = $CA           ; PTR ($CA,$CB) TO START OF THE
                                ; INTEGER PRGM.
INTVRLND        = $CC           ; INTEGER BASIC'S CURRENT END-OF-
                                ; VARIABLE PTR ($CC,$CD).
PROTFLG         = $D6           ; PRGM PROTECTION FLAG.
                                ; IF PROTECTION FLAG IS ON (NEG),
                                ; ALL APPLESOFT CMDS CAUSE A RUN &
                                ; DOS'S SAVE CMD CAUSE A PHONY
                                ; PROGRAM-TOO-LARGE ERROR MSG TO BE
                                ; GENERATED.
ERRFLG          = $D8           ; ON-ERROR FLAG.  EQUALS $80 IF
                                ; ON-ERR IS ACTIVE.
RUNMODE         = $D9           ; INTEGER BASIC'S MODE FLAG.
                                ; CONTAINS A NEG VALUE IF INTEGER
                                ; IS IN THE DEFERRED MODE.


                                ; --------------------------------
                                ; PAGE TWO
                                ; --------------------------------
BUF200          = $200          ; THE INPUT BUFFER ($200-$2FF).
                                ; (ALSO KNOWN AS KEYBOARD BUFFER.)

                                ; --------------------------------
                                ; PAGE THREE
                                ; --------------------------------
PG3DOSVT        = $3D0          ; START OF PAGE-3 DOS VECTOR TBL.
RESETVEC        = $3F2          ; RESET VECTOR.  CONTAINS ADDR OF
                                ; ROUT'N WHERE EXECUT'N DIVERTS TO
                                ; WHEN THE RESET KEY IS PRESSED.
VLDBYTE         = $3F4          ; RESET VALIDATION BYTE.
                                ; IF $3F3 EOR #$A5 IS NOT EQUAL TO
                                ; $3F4, THEN DISK REBOOTS AND A
                                ; COLDSTART IS DONE.  OTHERWISE,
                                ; EXECUTION GOES TO THE ADDR IN
                                ; $3F2/$3F3.


                                ; --------------------------------
                                ; TEXTSCREEN HOLES
                                ; --------------------------------
TRK4DRV1        = $478          ; BASE ADR USED TO REFERENCE THE
                                ; LOCATION THAT CONTAINS THE LAST
                                ; HALFTRACK ON WHICH DRIVE 1 WAS
                                ; ALIGNED.
PRESTRK         = $478          ; - CURRENT HALFTRACK NUMBER.
                                ; - SAVE REG FOR WHOLE TRK# WANTED
                                ; WHEN HAVE TO GO RECALIBRATE.
TRK4DRV2        = $4F8          ; BASE ADR USED TO REFERENCE THE
                                ; LOCATION THAT CONTAINS THE LAST
                                ; HALFTRACK ON WHICH DRIVE 2 WAS
                                ; ALIGNED.
RSEEKCNT        = $4F8          ; CNTR FOR # OF RE-SEEKS ALLOWED
                                ; BETWEEN RECALIBRATIONS (4 --> 1,
                                ; DO RECALIBRATION WHEN CNTR = 0.)

READCNTR        = $578          ; CNTR FOR # OF ATTEMPTS ALLOWED TO
                                ; GET A GOOD ADDR OR DATA READ.
SLOTPG5         = $5F8          ; SLOT*16 USED WHEN DOS WAS BOOTED

SLOTPG6         = $678          ; SLOT*16 USED WHEN FORMATTING &
                                ; WRITING.
RECLBCNT        = $6F8          ; CNTR FOR # OF RECALIBRATIONS
                                ; ALLOWED BEFORE RWTS GIVES UP.



                                ; --------------------------------
                                ; RAM-BASED APPLESOFT ADDRESSES
                                ; A(RAM)
                                ; --------------------------------
CLRFPRAM        = $E65          ; ENTRY POINT TO CLEAR VARIABLES
                                ; FROM A RAM-BASED APPLESOFT PRGM.
RUNFPRAM        = $FD4          ; ENTRY POINT TO EXECUTE RAM-BASED
                                ; APPLESOFT PRGM.


                                ; --------------------------------
                                ; DOS BUFFER START
                                ; --------------------------------

DOSTOP          = $BF00         ; Mosher: first byte of highest page to
                                ; relocate DOS to





                                ;
COL80OFF        = $C00C
ALTCAHR0FF      = $C00E

RRAMWXXXD2      = $C080
RROMWRAMD2      = $C081
RROMWXXXD2      = $C082
RRAMWRAMD2      = $C083
RRAMWXXXD1      = $C088
RROMWRAMD1      = $C089
RROMWXXXD1      = $C08A
RRAMWRAMD1      = $C08B

                                ; --------------------------------
                                ; DRIVE FUNCTION BASE ADDRS
                                ; --------------------------------
                                ; NOTE:  THE ACTUAL ADDRESSES
                                ; USED ARE DEPENDENT UPON WHICH
                                ; SLOT HOUSES THE DSK CONTROLLER
                                ; CARD. (SEE COMMENTS IN ANY ONE
                                ; OF THE FORMATTED DISASSEMBLIES
                                ; OF RWTS FOR MORE DETAILS.)

                                ; TURN STEPPER MOTOR MAGNETS OFF OR ON.
MAG0FF          = $C080
MAG0ON          = $C081
MAG1FF          = $C082
MAG1ON          = $C083
MAG2FF          = $C084
MAG2ON          = $C085
MAG3FF          = $C086
MAG3ON          = $C087

MTROFF          = $C088         ; TURN DRIVE MOTOR OFF.
MTRON           = $C089         ; TURN DRIVE MOTOR ON.
SELDRV1         = $C08A         ; SELECT DRIVE 1.
SELDRV2         = $C08B         ; SELECT DRIVE 2.
Q6L             = $C08C         ; SHIFT BYTE IN OR OUT OF LATCH.
Q6H             = $C08D         ; LOAD LATCH FROM DATA BUS.
Q7L             = $C08E         ; PREPARE TO READ.
Q7H             = $C08F         ; PREPARE TO WRITE.

BTRDSEC         = $C65C         ; BOOT0'S READ SECTOR SUBROUTINE.
                                ; (ACTUALLY=$CS5C, WHERE S=SLOT#.)


                                ; --------------------------------
                                ; APPLESOFT ADDRESSES
                                ; --------------------------------
RESTART         = $D43C         ; APLSFT BASIC'S WRMSTART ROUTINE.
SETLINKS        = $D4F2         ; ROUT'N THAT CLEARS ALL VAR'BLES,
                                ; RESETS THE STK PTR & RECALCS THE
                                ; LINKS IN @ APPLESOFT PRGM LINE.
                                ; (THIS ROUTINE IS WHAT ENABLES
                                ; APLSFT PRGMS TO BE RELOCATABLE.)
SETZPTRS        = $D665         ; APPLESOFT ROUTINE THAT SIMULATES
                                ; CLEAR & RESTORE STATEMENTS.
NEWSTT          = $D7D2         ; ROUTINE TO COLLECT & EXECUTE THE
                                ; APPLESOFT PRGM STATEMENT.
BSCERHLR        = $D865         ; BASIC'S ERROR-HANDLING ROUTINE.
BASICCLD        = $E000         ; BASIC'S COLDSTART ROUTINE.
BASICWRM        = $E003         ; BASIC'S WARMSTART ROUTINE.
BASICCHN        = $E836         ; BASIC'S CHAIN ENTRY POINT.
BASICERR        = $E3E3         ; BASIC'S ERROR HANDLER???

                                ; --------------------------------
                                ; MONITOR ROM ROUTINES
                                ; --------------------------------
INSDS2          = $F88E         ; Instruction translation routine
OLDBRK          = $FA59         ; DEFAULT BREAK INTERRUPT HANDLER.
INIT            = $FB2F         ; SCREEN INITIALIZATION ROUTINE.
                                ; (SET FULL SCREEN TEXT.)
HOME            = $FC58         ; BLANK OUT CURRENT SCROLL WINDOW
                                ; & PUT THE CURSOR IN THE TOP LEFT
                                ; CORNER OF THE WINDOW.  (DOES NOT
                                ; RESET THE WINDOW SIZE.)
RDKEY           = $FD0C         ; REQUEST INPUT VIA INPUT HOOK.
KEYIN           = $FD1B         ; WAIT FOR INPUT FROM KEYBOARD.
PRBYTE          = $FDDA         ; PRINT CONTENTS OF (A) AS HX VAL.
COUT            = $FDED         ; ROUTE OUTPUT VIA THE OUTPUT HK.
COUT1           = $FDF0         ; MONITOR ROM (TRUE) OUTPUT HNDLR.
SETKBD          = $FE89         ; SELECT KEYBOARD AS INPUT DEVICE.
INPORT          = $FE8B         ; ENTRY POINT TO SELECT INPUT
                                ; DEVICE OTHER THAN KEYBOARD.
SETVID          = $FE93         ; SELECT SCREEN AS OUTPUT DEVICE.
OUTPORT         = $FE95         ; ENTRY POINT TO SELECT OUTPUT
                                ; DEVICE OTHER THAN SCREEN.
MONRTS          = $FF58         ; AN RTS INSTRUC LOC'D IN MONITOR
                                ; ROM. MANY RELOCATABLE PGMS "JSR"
                                ; TO HERE IN ORDER TO LOAD UP THE
                                ; STACK WITH THE ADDR OF THE "JSR"
                                ; SO THEY CAN FIND OUT THE PRGM'S
                                ; PRESENT LOCATION.
MON             = $FF65         ; NORM ENTRY ROUTE TO MONITOR ROM.
                                ; ("CALL -151" GOES HERE.)






                                ; ASCII VALUES (WITH HIGH-BIT SET)
BEL             = $07|%10000000 ; bell
CR              = $0D|%10000000 ; carriage-return





TRKPERDSK       = $23           ; Number of tracks per disk
CATTRK          = TRKPERDSK/2   ; Catalog track is in middle of disk
