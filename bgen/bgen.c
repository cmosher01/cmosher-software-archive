#ifdef HAVE_CONFIG_H
#include "config.h"
#endif
#include "getopt.h"
#include <stdio.h>
#ifdef HAVE_SETMODE
#include <fcntl.h>
#endif

static int runProgram = 1;
static int count = 1;
static int constant = 0;

static const char* shortopts = "c:n:hV";
static const struct option longopts[] =
{
	{"const",1,0,'c'},
	{"count",1,0,'n'},
	{"help",0,0,'h'},
	{"version" ,0,0,'V'}
};


static void version()
{
	printf("%s\n",PACKAGE_STRING);
	printf("\n");
	printf("%s\n","Copyright (C) 2008, by Chris Mosher.");
	printf("%s\n","License GPLv3+: GNU GPL version 3 or later <http://www.gnu.org/licenses/gpl.html>.");
	printf("%s\n","This is free software: you are free to change and redistribute it.");
	printf("%s\n","There is NO WARRANTY, to the extent permitted by law.");
}

static void help()
{
	printf("Writes a constant byte value, repeated N times,\n");
	printf("to standard output.\n");
	printf("\n");
	printf("Usage: bgen [OPTION...]\n");
	printf("\n");
	printf("Options:\n");
	printf("  -n, --count=N  write N bytes; default 1\n");
	printf("  -c, --const=C  write bytes of decimal value C (0-255); default 0\n");
	printf("  -h, --help     show this help\n");
	printf("  -V, --version  show program version information\n");
}

int main(int argc, char* argv[])
{
	int c;
	while ((c = getopt_long(argc,argv,shortopts,longopts,0)) >= 0)
	{
		switch (c)
		{
			case 'c':
				sscanf(optarg,"%d",&constant);
			break;
			case 'n':
				sscanf(optarg,"%d",&count);
			break;
			case 'h':
				help();
				runProgram = 0;
			break;
			case 'V':
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
	setmode(stdout,O_BINARY);
#endif

	while (count-- > 0)
	{
		putchar(constant);
	}

	return 0;
}
