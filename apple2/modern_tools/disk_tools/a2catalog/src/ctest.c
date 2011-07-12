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
    int c_pass;
    int c_fail;
  };



/*
  Allocation and deallocation of the suite context
*/
ctest_ctx *ctest_ctx_alloc(void)
{
  return (ctest_ctx*)malloc(sizeof(ctest_ctx));
}

void ctest_ctx_free(ctest_ctx *const ctx)
{
  free(ctx);
}



/*
  The main test function (best called by the CTEST macro).
  If is_true is false, print and error message containing
  file_name, line_number. and name.
  Update counts in the given suite context ctx.
*/
void ctest_fn(ctest_ctx *const ctx, const char *const name, const int is_true, const char *const file_name, const int line_number)
{
  if (is_true)
    {
      ++ctx->c_pass;
    }
  else
    {
      ++ctx->c_fail;
      fprintf(stderr,"%s:%d: test failed: %s\n",file_name,line_number,name);
    }
}



/*
  Simple accessors for the counts
*/
int ctest_count_pass(const ctest_ctx *const ctx)
{
  return ctx->c_pass;
}

int ctest_count_fail(const ctest_ctx *const ctx)
{
  return ctx->c_fail;
}
