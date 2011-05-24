#include <config.h>

#include "assert_that.h"

#include <stdlib.h>
#include <stdio.h>


struct ctx_assertion {
	int c_failed_assertion;
	int c_assertion;
};



ctx_assertion* ctx_assertion_factory() {
	return (ctx_assertion*)malloc(sizeof(ctx_assertion));
}

void ctx_assertion_free(ctx_assertion* ctx) {
	free(ctx);
}

void assert_that_function(ctx_assertion* ctx, char* name, int is_true, char* file_name, int line_number) {
	++ctx->c_assertion;
	if (!is_true) {
		fprintf(stderr,"%s:%d: assertion failed: %s\n",file_name,line_number,name);
		++ctx->c_failed_assertion;
	}
}

int count_assertions(ctx_assertion* ctx) {
	return ctx->c_assertion;
}

int count_failed_assertions(ctx_assertion* ctx) {
	return ctx->c_failed_assertion;
}
