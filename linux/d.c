#include<time.h>
#include<stdio.h>

int main(void)
{
  clock_t ticks1, ticks2;

  ticks1=clock();
  ticks2=ticks1;
  while((ticks2/CLOCKS_PER_SEC-ticks1/CLOCKS_PER_SEC)<1)
    ticks2=clock();

  printf("Took %ld ticks to wait one second.\n",ticks2-ticks1);
  printf("This value should be the same as CLOCKS_PER_SEC which is %ld.\n",CLOCKS_PER_SEC);
  return 0;
}
