#ifdef HAVE_CONFIG_H
#include <config.h>
#endif

#include "ctest.h"

#include <stdlib.h>
#include <stdio.h>



/*
  CTEST suite context. It just contains a
  count of tests run, and a count of failed ones.
*/
struct ctest_ctx
  {
    int c_test;
    int c_test_failures;
  };



/*
  Allocation and deallocation of the suite context
*/
ctest_ctx *ctest_ctx_alloc()
{
  return malloc(sizeof(ctest_ctx));
}

void ctest_ctx_free(ctest_ctx *ctx)
{
  free(ctx);
}



/*
  The main test function (best called by the CTEST macro).
  If is_true is false, print and error message containing
  file_name, line_number. and name.
  Update counts in the given suite context ctx.
*/
void ctest_fn(ctest_ctx *ctx, char *name, int is_true, char *file_name, int line_number)
{
  ++ctx->c_test;
  if (!is_true)
    {
      fprintf(stderr,"%s:%d: test failed: %s\n",file_name,line_number,name);
      ++ctx->c_test_failures;
    }
}



/*
  Simple accessors for the counts
*/
int ctest_count(ctest_ctx *ctx)
{
  return ctx->c_test;
}

int ctest_count_failures(ctest_ctx *ctx)
{
  return ctx->c_test_failures;
}
