// monhex.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include <stdio.h>
#include <io.h>
#include <fcntl.h>
/*
input file has line[s] in this format, for example:
0800- 4C 03 08 A9 90 C5 7B F0

(output from Apple //c Monitor command 800.807)

*/

int main(int argc, char* argv[])
{
	unsigned char memory[0x10000];
	int addr, min_addr(0xFFFF), max_addr(0x0000);
	int b[8];

	int n = scanf("%4X- %2X %2X %2X %2X %2X %2X %2X %2X",
		&addr, b, b+1, b+2, b+3, b+4, b+5, b+6, b+7);
	while (n!=EOF)
	{
		n--;
		if (addr<min_addr)
			min_addr = addr;
		if (addr+n-1>max_addr)
			max_addr = addr+n-1;
		for (int i(0); i<n; i++)
			memory[addr+i] = b[i];
		n = scanf("%4X- %2X %2X %2X %2X %2X %2X %2X %2X",
			&addr, b, b+1, b+2, b+3, b+4, b+5, b+6, b+7);
	}

	fprintf(stderr,"range: %4.4X.%4.4X\n", min_addr, max_addr);

	_setmode(_fileno(stdout),_O_BINARY);
	for (int i(min_addr); i<=max_addr; i++)
		putchar((int)memory[i]);

	return 0;
}
