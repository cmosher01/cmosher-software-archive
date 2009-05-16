#include <cstdio>
#include <cstdlib>
#include <signal.h>
#include <execinfo.h>

void bt_sighandler(int sig, struct sigcontext ctx)
{
  if (sig == SIGSEGV)
    printf("Got signal %d; faulty address is %p; stack trace:\n", sig, ctx.cr2);
  else
    printf("Got signal %d; stack trace:\n", sig);

  void *trace[64];
  size_t trace_size = backtrace(trace,16);
  char** messages = backtrace_symbols(trace, trace_size);

  for (size_t i = 2; i<trace_size; ++i)
    printf("    %s\n", messages[i]);

  exit(0);
}


int func_a(int a, char b)
{
  char *p = (char *)0xdeadbeef;

  a = a + b;
  *p = 10;	/* CRASH here!! */

  return 2*a;
}


int func_b()
{
  int res, a = 5;

  res = 5 + func_a(a, 't');

  return res;
}


int main()
{
  /* Install our signal handler */
  struct sigaction sa;

  sa.sa_handler = (void (*) (int))bt_sighandler;
  sigemptyset(&sa.sa_mask);
  sa.sa_flags = SA_RESTART;

  sigaction(SIGSEGV, &sa, NULL);
  sigaction(SIGUSR1, &sa, NULL);
  /* ... add any other signal here */

  /* Do something */
  printf("%d\n", func_b());
}
