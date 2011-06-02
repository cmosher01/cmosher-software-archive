define(`ASM_DATA',`.BYT')
define(`ASM_ADDR',`.WORD $1')
define(`ASM_RES',`.DSB $1,0')




define(`NL',`
');

changecom(`;',NL())

dnl assumes no single-letter macros
define(`SAFESUB',
	`ifelse(
		$#,0,``$0'',
		$#,2,`$0($@,eval(len(`$1')-$2))',
		$3,0,`',
		`substr(`$1',$2,1)`'$0(`$1',eval($2+1),eval($3-1))')')

define(`STR_REVERSE',
	`ifelse(
		len(`$1'),0,`',
		`$0(SAFESUB(`$1',1))`'SAFESUB(`$1',0,1)')')

define(`STR_FORCHAR',
	`ifelse(
		`$2',`',`',
		`pushdef(`$1',SAFESUB(`$2',0,1))$3`'popdef(`$1')`'$0($1,SAFESUB(`$2',1),`$3')')')

define(`STR_FORCHAR_LAST',
	`ifelse(
		len(`$2'),1,`pushdef(`$1',`$2')$4`'popdef(`$1')',
		`pushdef(`$1',SAFESUB(`$2',0,1))$3`'popdef(`$1')`'$0($1,SAFESUB(`$2',1),`$3',`$4')')')

define(`HICHAR',`"`$1'"|%10000000')
define(`LOCHAR',`"`$1'"&%01111111')

define(`HIASCII',`STR_FORCHAR(__,`$1',`ASM_DATA HICHAR(__) NL()')')
define(`LOASCII',`STR_FORCHAR(__,`$1',`ASM_DATA LOCHAR(__) NL()')')

define(`HLASCII',`STR_FORCHAR_LAST(__,`$1',`ASM_DATA HICHAR(__) NL()',`ASM_DATA LOCHAR(__) NL()')')
define(`LHASCII',`STR_FORCHAR_LAST(__,`$1',`ASM_DATA LOCHAR(__) NL()',`ASM_DATA HICHAR(__) NL()')')


.zero
P
D
Q .byt 0
X
Y
Z .byt 0
.bss
I1 .byt 0
I2 .byt 0
I3 .byt 0
.text
define(`GOOD',`BAD')
define(`EMPTY',`')
define(`DOUBLE',`SINGLE')
define(`SINGLE',`END')

S	LDA #HICHAR(`A')
	LDA #<Y
	LDA #>Y
	LDA Y
	STA Y
	LDA I1
	LDA I2
	LDA I3
	JMP COUT

	ASM_ADDR(COUT)
	ASM_RES(16)

	HIASCII(` ') ; GOOD
	HIASCII(`A') ; Some comment
	HIASCII(` A') ; With (unmacthed parens
	HIASCII(`A ') ; with `matched' quotes
	HIASCII(`AB') ; with `unmatched quotes
	HIASCII(`GOOD') ; with 'unmatched quotes
	HIASCII(`BGOOD')
	HIASCII(`EMTPY')
	HIASCII(`DOUBLE')

	HLASCII(`A')
	HLASCII(` A')
	HLASCII(`A ')
	HLASCII(`AB')
	HLASCII(`GOOD')
	HLASCII(`BGOOD')
	HLASCII(`EMTPY')
	HLASCII(`DOUBLE')


T	LDA #3
	JMP COUT
	LDA #4
	JMP XXX
XXX
	LDA #$A5
