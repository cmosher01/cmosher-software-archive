//this is a test change
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <io.h>

void main(int argc, char* argv[]);
int filter(int* pc);

void main(int argc, char* argv[])
{
	char *sCarriage = "\r\n";
/*
	if (argv[1])
	{
		if (!strcmp(argv[1],"c"))
			sCarriage = "\r";
		else if (!strcmp(argv[1],"lc"))
			sCarriage = "\n\r";
		else if (!strcmp(argv[1],"l"))
			sCarriage = "\n";
	}
*/
/*
	for (int i(0); i<argc; i++)
	{
		printf("arg%2d: \"%s\"\n",i,argv[i]);
	}
	printf("press <Enter> to quit.");
	getchar();
	return;
*/

	if (argc<=1)
		return;

	if (argv[1] && !argv[1][0])
		return;

	int ignore_next_r(0);
	int ignore_next_n(0);

	FILE* fi = fopen(argv[1],"r");
	if (!fi)
	{
		fprintf(stderr,"Can't open file %s.\n",argv[1]);
		return;
	}
	FILE* fo = fopen("tempout.dat","w");
	if (!fo)
	{
		fprintf(stderr,"Can't open file %s.\n",argv[1]);
		return;
	}
	_setmode(_fileno(fi),_O_BINARY);
	_setmode(_fileno(fo),_O_BINARY);

	int c;
	while ((c = getc(fi))!=EOF)
	{
		switch (c)
		{
		case '\r':
			if (ignore_next_r)
				ignore_next_r = 0;
			else
			{
				putc(sCarriage[0],fo);
				if (sCarriage[1])
					putc(sCarriage[1],fo);
				ignore_next_n = 1;
			}
			break;
		case '\n':
			if (ignore_next_n)
				ignore_next_n = 0;
			else
			{
				putc(sCarriage[0],fo);
				if (sCarriage[1])
					putc(sCarriage[1],fo);
				ignore_next_r = 1;
			}
			break;
		default:
			if (filter(&c))
				putc(c,fo);
			ignore_next_n = 0;
			ignore_next_r = 0;
		}
	}
	fclose(fo);
	fclose(fi);
	char newname[_MAX_PATH];
	strcpy(newname,argv[1]);
	strcat(newname,".org");
	rename(argv[1],newname);
	rename("tempout.dat",argv[1]);

	FILE* fx = fopen("tempout.dat","r");
	if (fx)
	{
		fclose(fx);
		printf("Error, original file couldn't be renamed."
			" New file is tempout.dat.\n");
		getchar();
	}
}

int filter(int* pc)
{
	unsigned char c = *pc;
	c &= 0x7f;
	if (c=='\t') return c;
	if (c<0x20||0x7e<c) return 0;
	*pc = c;
	return 1;
}
