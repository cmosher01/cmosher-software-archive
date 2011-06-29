#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "LavaRnd/random.h"

int main(int argc, char *argv[])
{
	long int c = 45000017L;
	if (argc >= 3 && !strcmp(argv[1],"-n"))
	{
		c = atoi(argv[2]);
	}

	printf("# generator LavaRnd\n");
	printf("type: d\n");
	printf("count: %ld\n",c);
	printf("numbit: 32\n");

	while (c--)
	{
		u_int32_t i32;
		i32 = random32();
		printf("%10u\n",i32);
	}
	return 0;
}
