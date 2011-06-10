         .FEATURE LABELS_WITHOUT_COLONS

         .EXPORT M6502VEC

         .IF VERSION < 2
         .IMPORT RESET
         .ELSE
         .IMPORT RESET2
         .ENDIF
         .IMPORT IRQ

         .INCLUDE "symbols.s65"

M6502VEC .ADDR NMI        ;NMI VECTOR

         .IF VERSION < 2
         .ADDR RESET      ;RESET VECTOR
         .ELSE
         .ADDR RESET2
         .ENDIF

         .ADDR IRQ        ;IRQ VECTOR
