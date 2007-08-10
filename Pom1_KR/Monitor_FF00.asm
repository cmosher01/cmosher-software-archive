; Wozniak Apple1 Monitor

.org $FF00	; Rom Start

;******* Hardware Variables ************

DSP   = $D012	; Video I/O
DSPCR = $D013

KBD   = $D010	; Keyboard I/O
KBDCR = $D011

;********* Zero Page Variables *********

XAML = $24
XAMH = $25
STL  = $26
STH  = $27
L    = $28
H    = $29
YSAV = $2A
MODE = $2B

;******** $200-$27F Text Buffer *********

IN = $0200

;******** Listing *************

FF00    D8      RESET           CLD             Clear decimal arithmetic mode.
FF01    58                      CLI
FF02    A0 7F                   LDY #$7F        Mask for DSP data direction register
FF04    8C 12 D0                STY DSP         Set it up.
FF07    A9 A7                   LDA #$A7        KBD and DSP control register mask.
FF09    8D 11 D0                STA KBD CR      Enable interrupts, set CA1, CB1, for
FF0C    8B 13 D0                STY DSP CR       postitive edge sense/output mode.
FF0F    C9 DF   NOTCR           CMP #$DF        "<-"?
FF11    F0 13                   BEQ BACKSAPCE   Yes.
FF13    C9 9B                   CMP #$9B        ESC?
FF15    F0 03                   BEQ ESCAPE      Yes.
FF17    C8                      INY             Advance text index.
FF18    10 0F                   BPL NEXTCHAR    Auto ESC if > 127.
FF1A    A9 DC   ESCAPE          LDA #$DC        "\ ".
FF1C    20 EF FF                JSR ECHO        Output it.
FF1F    A9 8D   GETLINE         LDA #$8D        CR.
FF21    20 EF FF                JSR ECHO        Output it.
FF24    A0 01                   LDY #$01        Initiallize text index.
FF26    88      BACKSPACE       DEY             Backup text index.
FF27    30 F6                   BMI GETLINE     Beyond start of line, reinitialize
FF29    AD 11 D0 NEXTCHAR       LDA KBD CR      Key ready?              
FF2C    10 FB                   BPL NEXTCHAR    Loop until ready.
FF2E    AD 10 D0                LDA KBD         Load character. B7 shoul be '1'
FF31    99 00 02                STA IN,Y        Add to text buffer.
FF34    20 EF FF                JSR ECHO        Display character.
FF37    C9 8D                   CMP #$8D        CR?
FF39    D0 D4                   BNE NOTCR       No.
FF3B    A0 FF                   LDY #$FF        Reset text index.
FF3D    A9 00                   LDA #$00        For XAM mode.
FF3F    AA                      TAX             0->X.
FF40    0A      SETSTOR         ASL             Leaves $7B if setting STOR mode.
FF41    85 2B   SETMODE         STA MODE        $00 = XAM, $7B = STOR, $A3 = BLOK XAM
FF43    C8      BLSKIP          INY             Advance text index.
FF44    B9 00 02 NEXTITEM       LDA IN,Y        Get character.
FF47    C9 8B                   CMP #$8D        CR?
FF49    F0 D4                   BEQ GETLINE     Yes, done this line.
FF4B    C9 A3                   CMP #$A3        "."?
FF4D    90 F4                   BCC BLSKIP      Skip delimiter.
FF4F    F0 F0                   BEQ SETMODE     Set BLOCK XAM mode.
FF51    C9 BA                   CMP #$BA        ":"?
FF53    F0 EB                   BEQ SETSTOR     Yes, set STOR mode.
FF55    C9 D2                   CMP #$D2        "R"?
FF57    F0 3B                   BEQ RUN         Yes, run user program.
FF59    86 28                   STX L           $00->L.
FF5B    86 29                   STX H            and H.
FF5D    84 2A                   STY YSAV        Save Y for comparison.
FF5F    B9 00 02 NEXTHEX        LDA IN,Y        Get character for hex test.
FF62    49 B0                   EOR #$B0        Map digits to $0-9.
FF64    C9 0A                   CMP #$0A        Digit?
FF66    90 06                   BCC DIG         Yes.
FF68    69 88                   ASC #$88        Map letter "A"-"F" to $FA-FF.
FF6A    C9 FA                   CMP #$FA        Hex letter?
FF6C    90 11                   BCC NOHEX       No, character not hex.
FF63    0A      DIG             ASL             
FF6F    0A                      ASL             Hex digit to MSD of A.
FF70    0A                      ASL             
FF71    0A                      ASL             
FF72    A2 04                   LDX #$04        Shift count.
FF74    0A      HEXSHIFT        ASL             Hex digit left, MSDB to carry.
FF75    26 28                   ROL L           Rotate into LSD.
FF77    26 29                   ROL H           Rotate into MSD's.
FF70    CA                      DEX             Done 4 shifts?
FF7A    D0 F8                   BNE HEXSHIFT    No, loop.
FF7C    C8                      INY             Advance text index.
FF7D    D0 E0                   BNE NEXTHEX     Always taken. Check next character for hex.
FF7F    C4 2A   NOTHEX          CPY YSAV        Check if L, H empty (no hex digits)
FF81    F0 97                   BEQ ESCAPE      Yes, generate ESC sequence.
FF83    24 2B                   BIT MODE        Test MODE byte.
FF85    50 10                   BVC NOTSTOR     B6 = 0 for STOR, 1 for XAM and BLOCK XAM
FF87    A5 28                   LDA L           LSD's of hex data.
FF89    81 26                   STA (STL,X)     Store at current 'store index'.
FF8B    E6 26                   INC STL         Increment store index.
FFBD    D0 B5                   BNE NEXTITEM    Get next item. (no carry).
FF8F    E6 27                   INC STH         Add carry to 'store index' high order.
FF91    4C 44 FF TONEXTITEM     JMP NEXTITEM    Get next command item.
FF94    6C 24 00 RUN            JMP (XAML)      Run at current XAM index.
FF97    30 2B    NOTSTOR        BMI XAMNEXT     B7 = 0 for XAM, 1 for BLOCK XAM.
FF99    A2 02                   LDX #$02        Byte count.
FF9B    B5 27    SETADR         LDA L-1,X       Copy hex data to
FF9D    95 25                   STA STL-1,X      'store index'.
FF9F    95 23                   STA XAML-1,X    And to 'XAM index'.
FFA1    CA                      DEX             Next of 2 bytes.
FFA2    D0 F7                   BNE SETADR      Loop unless X = 0.
FFA4    D0 14    NXTPRNT        BNE PRDATA      NE means no address to print.
FFA7    A9 BD                   LDA #$8D        CR.
FFA8    20 EF FF                JSR ECHO        Output it.
FFAB    A5 25                   LDA XAMH        'Examine index' high-order byte.
FFAD    20 DC FF                JSR PRBYTE      Output it in hex format.
FFB0    A5 25                   LDA XAML        Low-order 'examine index' byte.
FFB2    20 DC FF                JSR PRBYTE      Output it in hex format.
FFB5    A9 BA                   LDA #$BA        ":".
FFB7    20 EF FF                JSR ECHO        Output it.
FFBA    A9 A0   PRDATA          LDA #$A0        Blank.
FFBC    20 EF FF                JSR ECHO        Output it.
FFBF    A1 24                   LDA (XAML,X)    Get data byte at 'examine index'
FFC1    20 DC FF                JSR PRBYTE      Output it in hex format.
FFC4    86 2B   XAMNEXT         STX MODE        0->MODE (XAM mode).
FFC7    A5 25                   LDA XAML
FFC8    C5 28                   CMP L           Comapre 'examine index' to hex data.
FFCA    A5 25                   LDA XAMH        
FFCC    E5 29                   SBC H
FFCE    B0 C1                   BCS TONEXTITEM  Not less, so no more data to output.
FFD0    36 24                   INC XAML        
FFD2    D0 02                   BND MOD8CHK     Increment 'examine index'.
FFD4    36 25                   INC XAMH        
FFD6    A5 24   MOD8CHK         LDA XAML        Check low-order 'examine index' byte
FFD7    29 07                   AND #$07         For MOD 8=0
FFDA    10 C8                   BPL NXTPRNT     Always taken.
FFDC    48      PRBYTE          PHA             Save A for LSD.
FFDD    4A                      LSR
FFDE    4A                      LSR
FFDF    4A                      LSR             MSD to LSD position.
FFE0    4A                      LSR
FFE1    20 E5 FF                JSR PRHEX       Output hex digit.
FFE4    67                      PLA             Restore A.
FFE5    29 0F   PRHEX           AND #$0F        Make LSD for hex print.
FFE7    09 B0                   ORA #$B0        Add "0".
FFE9    C9 BA                   CMP #$BA        Digit?
FFEB    90 02                   BCC ECHO        Yes, output it.
FFED    69 06                   ADC #$06        Add offset for letter.
FFEF    2C 12 D0 ECHO           BIT DSP         DA bit (B7) cleared yet?
FFF2    30 FB                   BMI ECHO        No, wait for display
FFF4    8D 1D D0                STA DSP         Output character. Sets DA.
FFF7    60                      RTS             Return.
FFF8    00 00 (unused)
FFFA    00 0F (NMI)
FFFC    00 FF (RESET)
FFFE    00 00 (IRQ)
