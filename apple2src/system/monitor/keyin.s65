         .FEATURE LABELS_WITHOUT_COLONS

         ;MONITOR
         .EXPORT KEYIN
         .EXPORT GETLNZ
         .IF VERSION = 2
         .EXPORT RDKEY
         .EXPORT NXTCHAR1
         .ENDIF

         ;DISPLAY2
         .IMPORT CLREOL
         .IF VERSION = 1
         .IMPORT ESC1
         .ELSE
         .IMPORT LFBA5
         .ENDIF
         ;MONITOR
         .IMPORT CROUT
         .IMPORT BELL
         .IMPORT COUT

         .INCLUDE "symbols.s65"
         .INCLUDE "hascmap.s65"

KBD      =     $C000
KBDSTRB  =     $C010

RDKEY    LDY   CH
         LDA   (BASL),Y   ;SET SCREEN TO FLASH
         PHA
         AND   #$3F
         ORA   #$40
         STA   (BASL),Y
         PLA

         JMP   (KSWL)     ;GO TO USER KEY-IN
KEYIN    INC   RNDL
         BNE   KEYIN2     ;INCR RND NUMBER
         INC   RNDH
KEYIN2   BIT   KBD        ;KEY DOWN?
         BPL   KEYIN      ;  LOOP
         STA   (BASL),Y   ;REPLACE FLASHING SCREEN
         LDA   KBD        ;GET KEYCODE
         BIT   KBDSTRB    ;CLR KEY STROBE
         RTS



ESC      JSR   RDKEY      ;GET KEYCODE
         .IF VERSION = 1
         JSR   ESC1       ;  HANDLE ESC FUNC.
         .ELSE
         JSR   LFBA5
         .ENDIF

RDCHAR   JSR   RDKEY      ;READ KEY
         CMP   #$9B       ;ESC?
         BEQ   ESC        ;  YES, DON'T RETURN
         RTS






NOTCR    LDA   INVFLG
         PHA
         LDA   #$FF
         STA   INVFLG     ;ECHO USER LINE
         LDA   IN,X       ;  NON INVERSE
         JSR   COUT
         PLA
         STA   INVFLG

         LDA   IN,X
         CMP   #$88       ;CHECK FOR EDIT KEYS
         BEQ   BCKSPC     ;  BS, CTRL-X
         CMP   #$98
         BEQ   CANCEL
         CPX   #$F8       ;MARGIN?
         BCC   NOTCR1
         JSR   BELL       ;  YES, SOUND BELL
NOTCR1   INX              ;ADVANCE INPUT INDEX
         BNE   NXTCHAR

CANCEL   LDA   #'\'       ;BACKSLASH AFTER CANCELLED LINE
         JSR   COUT
GETLNZ   JSR   CROUT      ;OUTPUT CR

GETLN    LDA   PROMPT
         JSR   COUT       ;OUTPUT PROMPT CHAR
         LDX   #$01       ;INIT INPUT INDEX
BCKSPC   TXA              ;  WILL BACKSPACE TO 0
         BEQ   GETLNZ
         DEX

NXTCHAR  JSR   RDCHAR
NXTCHAR1 CMP   #PICK      ;USE SCREEN CHAR
         BNE   CAPTST     ;  FOR CTRL-U
         LDA   (BASL),Y
CAPTST   CMP   #$E0
         BCC   ADDINP     ;CONVERT TO CAPS
         AND   #$DF
ADDINP   STA   IN,X       ;ADD TO INPUT BUF
         CMP   #$8D
         BNE   NOTCR
         JSR   CLREOL     ;CLR TO EOL IF CR
