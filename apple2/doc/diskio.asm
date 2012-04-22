000000001
000000100
000010000
001000000



 3 2 1 0 magnets (0 rotation == magnet 0)
--------
(normal)
00000001 0 01
00000101 1 05
00000100 2 04
00010100 3 14
00010000 4 10
01010000 5 50
01000000 6 40
01000001 7 41

(strange, but defined)
01000101 0 45
00010101 2 15
01010100 4 54
01010001 6 51

(undefined, i.e., no movement)
00000000 ? 00
00010001 ? 11
01000100 ? 44
01010101 ? 55



3210 magnets (0 rotation == magnet 0)
--------
(normal)
0001 0  1
0011 1  3
0010 2  2
0110 3  6
0100 4  4
1100 5  C
1000 6  8
1001 7  9

(strange, but defined)
1011 0  B
0111 2  7
1110 4  E
1101 6  D

(undefined, i.e., no movement)
0000 ?  0
0101 ?  5
1010 ?  A
1111 ?  F



motor off
LDA $C0E8

motor on
LDA $C0E9

drive 1
LDA $C0EA

drive 2
LDA $C0EB

read
     LDA $C0EE ; Q7L
READ LDA $C0EC ; Q6L
     BPL READ

sense write protect
LDA $C0ED ; Q6H
LDA $C0EE ; Q7L
BMI WRITEPROTECT

write
LDA $C0EF ; write-mode Q7H
LDA DATA
STA $C0ED ; load latch Q6H
ORA $C0EC ; write      Q6L



