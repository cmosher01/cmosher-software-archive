#ifdef HAVE_CONFIG_H
#include <config.h>
#endif

#include "ctest.h"

#include <assert.h>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>



/*
  CTEST suite context. It just contains a
  count of tests run, and a count of failed ones.
*/
struct ctest_ctx
  {
    long magic;
    long c_pass;
    long c_fail;
  };


/* fill unused memory with this: */
#define BAD_MEM 0xCC

/* Magic bytes: "CTst" */
#define MAGIC 0x74735443

static void ctest_ctx_check(const ctest_ctx *const ctx)
{
  assert(ctx);
  assert(ctx->magic==MAGIC);
  assert(ctx->c_pass >= 0);
  assert(ctx->c_fail >= 0);
}

/*
  Allocation and deallocation of the suite context
*/
ctest_ctx *ctest_ctx_alloc(void)
{
  ctest_ctx *const ctx = (ctest_ctx*)malloc(sizeof(*ctx));
  memset(ctx,BAD_MEM,sizeof(ctest_ctx));

  ctx->magic = MAGIC;

  ctx->c_pass = 0;
  ctx->c_fail = 0;

  ctest_ctx_check(ctx);

  return ctx;
}

void ctest_ctx_free(ctest_ctx *const ctx)
{
  ctest_ctx_check(ctx);
  if (!ctest_count_test(ctx))
    {
      fprintf(stderr,"Warning: no CTEST unit tests were run.\n");
    }
  memset(ctx,BAD_MEM,sizeof(ctest_ctx));
  free(ctx);
}


/*
  The main test function (best called by the CTEST macro).
  If is_true is false, print an error message containing
  file_name, line_number. and name.
  Update counts in the given suite context ctx.
*/
void ctest(ctest_ctx *const ctx, const char *const name, const int is_true, const char *const file_name, const unsigned long line_number)
{
  ctest_ctx_check(ctx);
  if (is_true)
    {
      ++ctx->c_pass;
    }
  else
    {
      ++ctx->c_fail;
      fprintf(stderr,"%s:%lu: test failed: %s\n",file_name,line_number,name);
    }
}



/*
  Simple accessors for the counts
*/
long ctest_count_pass(const ctest_ctx *const ctx)
{
  ctest_ctx_check(ctx);
  return ctx->c_pass;
}

long ctest_count_fail(const ctest_ctx *const ctx)
{
  ctest_ctx_check(ctx);
  return ctx->c_fail;
}

long ctest_count_test(const ctest_ctx *const ctx)
{
  ctest_ctx_check(ctx);
  return ctx->c_pass+ctx->c_fail;
}
