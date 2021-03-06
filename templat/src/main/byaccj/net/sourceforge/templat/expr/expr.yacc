// This file needs to be compiled with BYACC/J using the following command:
// yacc -J -Jclass=ExprParser -Jextends=ExprParserHelper -Jsemantic=Object -Jpackage=net.sourceforge.templat.expr -Jthrows="ExprLexingException, ExprParsingException, TemplateParsingException" -Jnorun -Jnoconstruct -Jnodebug -l expr.yacc

%{
import net.sourceforge.templat.parser.context.ContextStack;
import net.sourceforge.templat.exception.TemplateParsingException;
import net.sourceforge.templat.expr.exception.ExprLexingException;
import net.sourceforge.templat.expr.exception.ExprParsingException;



/**
 * Parses an expression. This java source file is generated by
 * BYACC/J from the expr.yacc grammar source file.
 */
%}


%token EOF
%token DOT
%token COMMA
%token NUM
%token ID
%token WS



%%


expression
	: '!' S expression { $$ = !(Boolean)$3; }
	| '(' S expression S ')' { $$ = $3; }
	| literal
	| name { $$ = actions().applySelectors($1,actions().createList()); }
	| name selectors { $$ = actions().applySelectors($1,$2); }
	;

selectors
	: selectors selector { $$ = actions().addToList($2,$1); }
	| selector { $$ = actions().addToList($1,actions().createList()); }
	;

selector
	: DOT identifier args { $$ = actions().createMethodCall($2,$3); }
	| '[' S expression S ']' { $$ = actions().createArraySubscript($3); }
	;

args
	: '(' S arg_list S ')' { $$ = $3; }
	;

arg_list
	: arg_list S COMMA S expression { $$ = actions().addToList($5,$1); }
	| expression { $$ = actions().addToList($1,actions().createList()); }
	| /* empty */ { $$ = actions().createList(); }
	;

name
	: name DOT identifier { $$ = $1+"."+$3; }
	| identifier
	;

identifier
	: ID
	;

literal
	: NUM
	;

S
	: WS
	| /* empty */
	;

%%

public ExprParser(final String input, final ContextStack stackContext) {
	super(input, stackContext);
}

@Override
protected void setYylval(Object yylval) {
	this.yylval = yylval;
}

@Override
protected int getYychar() {
	return this.yychar;
}

@Override
protected Object getYyval() {
	return this.yyval;
}
