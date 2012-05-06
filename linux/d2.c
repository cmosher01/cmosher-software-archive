#include<time.h>
#include<stdio.h>

int main(int argc, char* argv[])
{
  const time_t secNow = time(NULL);

  const struct tm const * pTmNow = localtime(&secNow);

  const char const * sNow = asctime(pTmNow);

  printf("%s\n",sNow);

  return 0;
}
