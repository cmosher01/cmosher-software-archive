// keygen.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include <iostream>
using namespace std;

int main(int argc, char* argv[])
{
	if (argc<2)
		return 1;

	char* n1 = argv[1];

	char* p = const_cast<char*>(n1);
	int c(0);
	while (*p)
	{
		c++;
		p++;
	}

	char* my_key = new char[2*c+1];

	int m(0);
	while (*n1)
	{
		char x = *n1;
		char c1 = static_cast<char>('A'-1+
			((x&0x80)>>4)+
			((x&0x20)>>3)+
			((x&0x04)>>1)+
			((x&0x02)>>1));

		char c2 = static_cast<char>('F'-1+
			((x&0x40)>>3)+
			((x&0x10)>>2)+
			((x&0x08)>>2)+
			((x&0x01)>>0));

		my_key[m++] = c1;
		my_key[m++] = c2;

		n1++;
	}
	my_key[m++] = '\0';

	cout << my_key << endl;

	delete [] my_key;

	return 0;
}

