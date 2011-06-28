// bindifcmd.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include <iostream>
#include "dif.h"
using namespace std;

int main(int argc, char* argv[])
{
	if (argc != 4)
	{
		cout << "Usage: bindifcmd file1 file2 dif-file" << endl;
	}

	gendif(argv[1],argv[2],argv[3],2,64);

	return 0;
}
