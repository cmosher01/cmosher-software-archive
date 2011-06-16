                .INCLUDE "symbols.s65"
                .INCLUDE "zpabs.s65"


                .EXPORT DOSNMBF1
                .EXPORT DOSRELOC
                .EXPORT MASTERDOS

                .IMPORT DOSLIM
                .IMPORT RWTS
                .IMPORT ONTABLE
                .IMPORT PRENIBL
                .IMPORT FREE1
                .IMPORT WRITADR
                .IMPORT NMPG2RD
                .IMPORT CURDIRTK
                .IMPORT CMDTXTBL
                .IMPORT IBBUFP
                .IMPORT IBDCTP
                .IMPORT IBTYPE
                .IMPORT ADROFIOB
                .IMPORT FMXTNTRY
                .IMPORT ADRIOB
                .IMPORT IMGARAMV
                .IMPORT IMGFPV
                .IMPORT IMGINTV
                .IMPORT ADBSCERR
                .IMPORT RUNTRY
                .IMPORT CHAINTRY
                .IMPORT ADOSFNB1
                .IMPORT IMGCOLVT
                .IMPORT CURDIRNX
                .IMPORT TODOSCLD2
                .IMPORT ADOSTART
                .IMPORT DOSCOLD
                .IF VERSION < 320
                .IMPORT L1B28SR
                .ENDIF
                .IF VERSION < 330
                .IMPORT BOOT1
                .IMPORT BOOT2
                .IMPORT OUTHNDTB
                .ELSE
                .IMPORT CLOBCARD
                .IMPORT SECFLGS
                .IMPORT APPNDFLG
                .IF VERSION >= 331
                .IMPORT CMPATCH
                .IMPORT CKIFAPND
                .ENDIF
                .ENDIF

MASTERDOS       JMP   DOSCOLD

DOSRELOC
                                ; (A3) --> DOSTOP
                LDA   #>DOSTOP
                STAZ  A3H
                LDX   #<DOSTOP
                STXZ  A3L

                                ; check for existence of RAM,
                                ; starting at page $BF and working downwards
L1B0B           LDY   #0
                LDA   (A3L,X)   ; (note: X is always 0)
                STA   PROSCRTH
L1B11           TYA
                EOR   PROSCRTH
                STA   PROSCRTH
                TYA
                EOR   (A3L,X)   ; (note: X is always 0)
                STA   (A3L,X)   ; (note: X is always 0)
                CMP   PROSCRTH
                BNE   L1B24
                INY
                BNE   L1B11
                BEQ   L1B28

L1B24
                DECZ  A3H
                BNE   L1B0B     ; always branches

                .IF VERSION < 320
L1B28           JSR   L1B28SR
                .ELSE
                                ; upon entry, A3 --> first byte of highest RAM page (e.g., $BF00)
L1B28
                LDAZ  A3H
                AND   #$DF      ; $BF becomes $9F
                STAZ  A4H
                STXZ  A4L       ; (A4) --> $9F00
                LDA   (A4L,X)
                PHA             ; save (A4) in case we need to restore it below
                STA   PROSCRTH
L1B35           TYA
                EOR   PROSCRTH
                STA   PROSCRTH
                TYA
                EOR   (A3L,X)
                STA   (A4L,X)
                CMP   PROSCRTH
                BNE   L1B4C
                INY
                BNE   L1B35
                                ; comes here if 9F page maps to BF page
                LDY   A4H       ; $9F
                PLA             ; (A4)
                JMP   L1B51




L1B4C           PLA
                STA   (A4L,X)   ; restore (A4)
                LDY   A3H       ; $BF

                .ENDIF


                                ; upon entry Y contains highest destination page (e.g., $BF)
L1B51           INY
                STY   DSTPAGELIM ; page limit (e.g., $C0)
                SEC
                TYA
                SBC   PAGECNT
                STA   DSTPAGE   ; first page to reloc to (e.g., $9D)
                SEC
                SBC   SRCPAGE   ; first page to reloc from (e.g., $1D)
                BEQ   MASTERDOS ; if no reloc is necessary (already at dst addr), branch to boot routine



                STA   OFFSET    ; $80
                LDA   SRCPAGE   ; $1D
                STA   ADOSTART+1; fixup dos-start vector

                                ; fix this pointer inside DOS: it used to
                                ; point to us (the relocation), but since we
                                ; are doing the relocating now, change it
                                ; so it just goes right to the DOS cold-start routine
                LDA   #>DOSCOLD
                STA   TODOSCLD2+2
                LDA   #<DOSCOLD
                STA   TODOSCLD2+1


                                ; fix up DOS vectors (ranges defined
                                ; in FIXVECTBL).
                LDX   #0
                STXZ  A3L

FIXVEC          LDA   FIXVECTBL,X
                TAY
                LDA   FIXVECTBL+1,X
                STAZ  A3H
                JMP   L1B93

L1B86           CLC
                LDA   (A3L),Y
                ADC   OFFSET
                STA   (A3L),Y
                INY
                BNE   L1B93
                INCZ  A3H
L1B93           INY
                BNE   L1B98
                INCZ  A3H
L1B98
                LDAZ  A3H
                CMP   FIXVECTBL+3,X
                BCC   L1B86
                TYA
                CMP   FIXVECTBL+2,X
                BCC   L1B86

                TXA
                CLC
                ADC   #4
                TAX
                CPX   FIXVECTBLSIZ
                BCC   FIXVEC



                                ; Do the fixups.
                                ; Look through the DOS machine code instructions
                                ; and look for absolute references to addresses
                                ; within the code that is being relocated, and
                                ; add the offset (in pages) to the high-order byte.
                LDX   #0
FIXCOD          STX   CURDIRNX
                LDA   FIXCODTBL,X
                STAZ  A3L
                LDA   FIXCODTBL+1,X
                STAZ  A3H

.LOOP           LDX   #0
                LDA   (A3L,X)
                JSR   INSDS2
                LDYZ  VOLDSK
                CPY   #2
                BNE   .NOCHANGE
                LDA   (A3L),Y
                CMP   SRCPAGE
                BCC   .NOCHANGE
                CMP   SRCPAGELIM
                BCS   .NOCHANGE
                ADC   OFFSET
                STA   (A3L),Y
.NOCHANGE       SEC
                LDAZ  VOLDSK
                ADCZ  A3L
                STAZ  A3L
                LDA   #$00
                ADCZ  A3H
                STAZ  A3H
                LDX   CURDIRNX
                CMP   FIXCODTBL+3,X
                BCC   .LOOP
                LDAZ  A3L
                CMP   FIXCODTBL+2,X
                BCC   .LOOP

                TXA
                CLC
                ADC   #4
                TAX
                CPX   FIXCODTBLSIZ
                BCC   FIXCOD

                                ; Copy the image to the new location
RELOC           LDA   #$3F
                STAZ  A3H
                LDY   DSTPAGELIM
                DEY
                STYZ  A4H
                LDA   #0
                STAZ  A3L
                STAZ  A4L
                TAY
.LOOP2          LDA   (A3L),Y
                STA   (A4L),Y
                INY
                BNE   .LOOP2
                DEC   PAGECNT2
                BEQ   .DONE
                DECZ  A3H
                DECZ  A4H
                BNE   .LOOP2
.DONE
                                ; Done relocating, so now cold-start it
                JMP   IMGCOLVT



                                ; Table of vectors within DOS that we need to
                                ; fix up. Format:
                                ; START,LIMIT
FIXVECTBLSIZ    .BYTE    FIXVECTBLLIM-FIXVECTBL ; size of table in bytes
FIXVECTBL       .ADDR    ADOSFNB1
                .ADDR    CHAINTRY
                .ADDR    RUNTRY
                .ADDR    ADBSCERR
                .ADDR    IMGINTV+2
                .ADDR    IMGINTV+4
                .ADDR    IMGFPV
                .ADDR    IMGFPV+4
                .ADDR    IMGARAMV
                .ADDR    IMGARAMV+4
                .ADDR    IMGARAMV+6
                .ADDR    IMGARAMV+8
                .ADDR    ADRIOB
                .ADDR    FMXTNTRY
                .ADDR    ADROFIOB
                .ADDR    IBTYPE
                .ADDR    IBDCTP
                .ADDR    IBBUFP
FIXVECTBLLIM

                .RES    12

                                ; Table of code within DOS that we need to
                                ; fix up. Format:
                                ; START,LIMIT
FIXCODTBLSIZ    .BYTE    FIXCODTBLLIM-FIXCODTBL ; size of table in bytes
FIXCODTBL       .ADDR    DOSCOLD
                .ADDR    CMDTXTBL
                .ADDR    FMXTNTRY
                .ADDR    CURDIRTK
                .IF VERSION < 330
                .ADDR    BOOT2
                .ELSE
                .ADDR    APPNDFLG
                .ENDIF
                .ADDR    NMPG2RD
                .IF VERSION < 320
                .ADDR    BOOT1-2
                .ADDR    BOOT2-$102 ; NONSENSE ENTRIES?
                .ELSE
                .IF VERSION < 330
                .ADDR    BOOT1-2
                .ADDR    BOOT1-2 ; NONSENSE ENTRIES?
                .ELSE
                .ADDR    WRITADR
                .ADDR    FREE1
                .ENDIF
                .ENDIF
                .ADDR    PRENIBL
                .IF VERSION < 321
                .ADDR    ONTABLE-1
                .ELSEIF VERSION < 330
                .ADDR    ONTABLE+3
                .ELSE
                .ADDR    ONTABLE
                .ENDIF
                .IF VERSION >= 331
                .ADDR    CKIFAPND
                .ADDR    CMPATCH
                .ENDIF
                .ADDR    RWTS
                .IF VERSION >= 330
                .ADDR    SECFLGS
                .ADDR    CLOBCARD
                .ENDIF
                .ADDR    DOSLIM-1
FIXCODTBLLIM

                .IF VERSION < 330
                .RES 4
                .ENDIF

SRCPAGE         .BYTE    >ADOSFNB1
SRCPAGELIM      .BYTE    >DOSLIM

DSTPAGE         .RES    1
DSTPAGELIM      .RES    1

PAGECNT         .BYTE    >(DOSLIM-ADOSFNB1) ; number of pages to be moved
OFFSET          .RES    1                   ; number of pages to add, to reloc addresses
PAGECNT2        .BYTE    >(DOSLIM-ADOSFNB1) ; (redundant?) number of pages to be moved

                .IF VERSION < 320
                .BYTE    $13       ; UNUSED CODE???
                BMI   *-$46
                JSR   L1C7B
                LDA   #$80
                STA   $13
                PLA
                BNE   *-$2E
L1C7B           LDX   MEMSIZ
                .ENDIF
                .IF VERSION < 330
                LDA   MEMSIZ+1  ; UNUSED CODE???
                STX   FRETOP
                STA   FRETOP+1
                LDY   #$00
                STY   $8B
                LDA   $6D
                LDX   $6E
                STA   $9B
                STX   $9C
                LDA   #$55
                LDX   #$00
                STA   $5E
                STX   $5F
L1C97           CMP   $52
                BEQ   L1CA0
                JSR   *+$7F
                BEQ   L1C97
L1CA0           LDA   #$07
                STA   $8F
                LDA   VARTAB
                LDX   VARTAB+1
                STA   $5E
                STX   $5F
L1CAC           CPX   $6C
                BNE   L1CB4
                CMP   $6B
                BEQ   L1CB9
L1CB4           JSR   OUTHNDTB
                BEQ   L1CAC
L1CB9           STA   $94
                STX   $95
                LDA   #$03
                STA   $8F
L1CC1           LDA   $94
                LDX   $95
                CPX   $6E
                BNE   L1CD0
                CMP   $6D
                BNE   L1CD0
                JMP   *+$8C
L1CD0           STA   $5E
                STX   $00
DOSNMBF1        LDY   #$00
                LDA   ($5E),Y
                TAX
                INY
                LDA   ($5E),Y
                PHP
                INY
                LDA   ($5E),Y
                ADC   $94
                STA   $94
                INY
                LDA   ($5E),Y
                ADC   $95
                STA   $95
                PLP
                BPL   L1CC1
                TXA
                BMI   L1CC1
                .ADDR $1CA6,$1BA6,$1AA6,$1A80,$5E65,$5E85
                .BYTE $90,$02,$E6
                .ELSE
                                ; unused:
                .IF VERSION = 330
                .RES    $04
                .ENDIF
                .RES    $52
DOSNMBF1        .RES    $2D       ; this label represents the first DOS buffer
                .ENDIF
