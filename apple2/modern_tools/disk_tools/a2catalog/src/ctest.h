#ifndef UUID_b743105c6bd5471c871fa85d8ec31a1c
#define UUID_b743105c6bd5471c871fa85d8ec31a1c

/*
  CTEST -- A simple unit test framework for C
  Copyright (C) 2011, by Christopher A. Mosher

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/



/*

CTEST is a very simple unit test framework for C.
Its main function is to check for pass/fail of tests,
print error messages for failures, and keep count.

To use the CTEST framework, perform to following
actions:
  1. Call ctest_ctx_alloc to allocate a suite context.
  2. Call the CTEST macro for each unit test to perform.
  3. Check the count of failed tests in the suite by
     calling ctest_count_failures.
  4. Call ctest_ctx_free to free the suite context.

Here is a simple example:

#include "ctest.h"
int main(int argc, char **argv)
{
  int f;

  ctest_ctx *ctx = ctest_ctx_alloc();

  CTEST(ctx,"9 divided by 3 is 3",9/3==3);
  CTEST(ctx,"10 divided by 3 is 3",10/3==3);
  f = ctest_count_failures(ctx);

  ctest_ctx_free(ctx);

  if (f)
    {
      return 1;
    }

  return 0;
}

*/


/*
  Opaque structure containing the CTEST context for
  one suite of unit tests.
*/
struct ctest_ctx;
typedef struct ctest_ctx ctest_ctx;

/*
  Functions to allocate and deallocate a CTEST context.
*/
ctest_ctx *ctest_ctx_alloc();
void ctest_ctx_free(ctest_ctx *ctx);

/*
  Perform one unit test. Call the CTEST macro for each unit
  test to perform. Use the CTEST macro, not the ctest_fn
  function directly, because the CTEST macro gets the current
  file name and line number of the test (which it uses in the
  error message in case the test fails).
  ctx is the CTEST context, which must have been allocated by
    a call to ctest_ctx_alloc.
  name is any string to identify the test with (this is used
    only to print the error message).
  assertion is the boolean expression to test. If the expression
    evaluates to non-zero (true), then the test is considered
    correct; if the expression evaluates to zero (false), then
    the test is considered failed.
*/
#define CTEST(ctx,name,assertion) ctest_fn(ctx,name,assertion,__FILE__,__LINE__)

void ctest_fn(ctest_ctx *ctx, char *name, int is_true, char *file_name, int line_number);

/*
  Accessor functions to get the (current) count of tests
  run, and the count of failures.
*/
int ctest_count(ctest_ctx *ctx);
int ctest_count_failures(ctest_ctx *ctx);

#endif
