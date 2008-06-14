// bindif.cpp : Defines the class behaviors for the application.
//

#include "stdafx.h"
#include "dif.h"

#include <stdio.h>

enum state_t {start,copy,skip,insert,end};

static long calceof(FILE *f);
static void statechange(FILE *fdif, state_t newstate, long c, FILE *f = 0);
static bool difFindMatch(FILE *f1, FILE *f2, long *fmark, int cs, int cm);
static bool difMatch(FILE *f1, FILE *f2, int cm);

void gendif(const char *file_1, const char *file_2, const char *file_dif, int cMinMatch, int cMaxSearch)
{
	FILE *f1 = fopen(file_1,"rb");
	FILE *f2 = fopen(file_2,"rb");
	FILE* fdif = fopen(file_dif,"wb");

	char b1[32768];
	char b2[32768];
	setvbuf(f1,b1,_IOFBF,32767);
	setvbuf(f2,b2,_IOFBF,32767);

	statechange(fdif,start,0);
	int c1 = fgetc(f1);
	int c2 = fgetc(f2);
	while (c1 != EOF || c2 != EOF)
	{
		if (c1==c2)
			statechange(fdif,copy,1);
		else
		{
			ungetc(c1,f1);
			ungetc(c2,f2);

			long fmark;
			if (difFindMatch(f1,f2,&fmark,cMaxSearch,cMinMatch))
				statechange(fdif,insert,fmark-ftell(f2),f2);
			else if (difFindMatch(f2,f1,&fmark,cMaxSearch,cMinMatch))
				statechange(fdif,skip,fmark-ftell(f1),f1);
			else
			{
				statechange(fdif,skip,1,f1);
				statechange(fdif,insert,1,f2);
			}
		}
		c1 = fgetc(f1);
		c2 = fgetc(f2);
	}
	statechange(fdif,end,0);

	fclose(f1);
	fclose(f2);
	fclose(fdif);
}

void statechange(FILE *fdif, state_t newstate, long c, FILE *f)
{
	static state_t state;
	static int ccopy(0);
	static int cskip(0);
	static int cinsert(0);
	static long posinsert(-1);
	static FILE *fileinsert;

	if (newstate==start)
	{
		state = start;
		return;
	}

	int i;
	char s[256];
	if (newstate!=state)
	{
		switch (state) //old state
		{
			case copy:
				sprintf(s,"c%d",ccopy);
				fputs(s,fdif);
				ccopy = 0;
			break;
			case skip:
			case insert:
			{
				if (newstate==copy||newstate==end)
				{
					if (cskip)
					{
						sprintf(s,"s%d",cskip);
						fputs(s,fdif);
						cskip = 0;
					}
					if (cinsert)
					{
						char s[256];
						sprintf(s,"i%d{",cinsert);
						fputs(s,fdif);

						long orig = ftell(fileinsert);

						fseek(fileinsert,posinsert,SEEK_SET);
						for (int i(0); i<cinsert; i++)
							fputc(fgetc(fileinsert),fdif);

						fseek(fileinsert,orig,SEEK_SET);

						fputc('}',fdif);
						cinsert = 0;
						posinsert = -1;
					}
				}
			}
		}
		state = newstate;
	}

	switch (state)
	{
		case copy:
			ccopy += c;
		break;
		case skip:
			cskip += c;
			for (i = 0; i<c; i++)
				(void)fgetc(f);
		break;
		case insert:
			if (posinsert<0)
			{
				posinsert = ftell(f);
				fileinsert = f;
			}
			cinsert += c;
			for (i = 0; i<c; i++)
				(void)fgetc(f);
		break;
	}
}

bool difFindMatch(FILE *f1, FILE *f2, long *fmark, int cs, int cm)
{
	long orig = ftell(f2);

	bool endoffile(false);
	while (cs-- && !endoffile && !difMatch(f1,f2,cm))
		endoffile = fgetc(f2)==EOF;

	bool found(!endoffile && cs>0);

	*fmark = ftell(f2);
	fseek(f2,orig,SEEK_SET);

	return found;
}

bool difMatch(FILE *f1, FILE *f2, int cm)
{
	long orig1 = ftell(f1);
	long orig2 = ftell(f2);

	bool same(true);
	for (int i(0); i<cm && same; i++)
	{
		int c1 = fgetc(f1);
		int c2 = fgetc(f2);
		if ((c2==EOF && c1!=EOF) || (c1!=c2))
			same = false;
	}

	fseek(f1,orig1,SEEK_SET);
	fseek(f2,orig2,SEEK_SET);

	return same;
}

// returns the fseek of f when it it's at EOF
long calceof(FILE *f)
{
	long orig = ftell(f);

	fseek(f,0,SEEK_END);
	int poseof = ftell(f);

	fseek(f,orig,SEEK_SET);

	return poseof;
}

void applydif(const char *file_1, const char *file_dif, const char *file_2)
{
	FILE *f1 = fopen(file_1,"rb");
	FILE *f2 = fopen(file_dif,"rb");
	FILE *f3 = fopen(file_2,"wb");

	int cdif, i, c;
	while ((cdif = fgetc(f2)) != EOF)
	{
		fscanf(f2,"%d",&c);
		switch (cdif)
		{
			case 'c':
				for (i = 0; i<c; i++)
					fputc(fgetc(f1),f3);
			break;
			case 's':
				for (i = 0; i<c; i++)
					(void)fgetc(f1);
			break;
			case 'i':
				fgetc(f2); // {
				for (i = 0; i<c; i++)
					fputc(fgetc(f2),f3);
				fgetc(f2); // }
			break;
		}
	}

	fclose(f1);
	fclose(f2);
	fclose(f3);
}
