#ifndef UUID_b743105c6bd5471c871fa85d8ec31a1c
#define UUID_b743105c6bd5471c871fa85d8ec31a1c

struct ctx_assertion;
typedef struct ctx_assertion ctx_assertion;

ctx_assertion* ctx_assertion_factory();
void ctx_assertion_free(ctx_assertion* ctx);

#define ASSERT_THAT(ctx,name,assertion) assert_that_function(ctx,name,assertion,__FILE__,__LINE__);
void assert_that_function(ctx_assertion* ctx, char* name, int is_true, char* file_name, int line_number);

int count_assertions(ctx_assertion* ctx);
int count_failed_assertions(ctx_assertion* ctx);

#endif
