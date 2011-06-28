#ifdef HAVE_CONFIG_H
#include "config.h"
#endif

#include <stdio.h>
#include <errno.h>
#ifdef HAVE_LIMITS_H
#include <limits.h>
#endif

#include "getopt.h"

static int runProgram = 1;
static int count = INT_MAX;
static int skip = 0;

static const char* shortopts = "n:s:hv";
static const struct option longopts[] =
{
	{"count",1,0,'n'},
	{"skip",1,0,'s'},
	{"help",0,0,'h'},
	{"version" ,0,0,'v'}
};


static void version()
{
	printf("%s\n",PACKAGE_STRING);
}

static void help()
{
	printf("Reads bytes from standard input and writes them to\n");
	printf("standard output. Has options to skip leading bytes\n");
	printf("read in, or limit the count of bytes written out.\n");
	printf("\n");
	printf("Usage: sdd [OPTION...]\n");
	printf("\n");
	printf("Options:\n");
	printf("  -n, --count=N  write a maximum of N bytes\n");
	printf("  -s, --skip=S   skip the first S bytes from stdin\n");
	printf("  -h, --help     show this help\n");
	printf("  -v, --version  show program version information\n");
}

int main(int argc, char* argv[])
{
	int c;
	while ((c = getopt_long(argc,argv,shortopts,longopts,0)) >= 0)
	{
		switch (c)
		{
			case 'n':
				sscanf(optarg,"%d",&count);
			break;
			case 's':
				sscanf(optarg,"%d",&skip);
			break;
			case 'h':
				help();
				runProgram = 0;
			break;
			case 'v':
				version();
				runProgram = 0;
			break;
			default:
				runProgram = 0;
			break;
		}
	}
	if (!runProgram)
	{
		return 0;
	}

#ifdef HAVE_SETMODE
	setmode(stdin,O_BINARY);
	setmode(stdout,O_BINARY);
#endif

	int i = getchar();
	while (i != EOF && count > 0)
	{
		if (skip)
		{
			--skip;
		}
		else
		{
			putchar(i);
			--count;
		}
		i = getchar();
	}
	if (ferror(stdin))
	{
		perror(argv[0]);
		return errno;
	}
	return 0;
}
