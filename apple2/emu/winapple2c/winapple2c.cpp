#include "stdafx.h"
#include "apple2c.h"

int main(int argc, char* argv[]);
bool do_command();
void setup();
void print_help();
void print_intro();
void test();

class main_data
{
public:
	main_data() {}
	virtual ~main_data() {}

	apple_ii_c a1;
};

main_data g;

int main(int argc, char* argv[])
{
	setup();
	while (do_command()) {}
	return 0;
}

bool do_command()
{
	bool b_continue(true);

	::printf("a2c>");
	char s[1024];
	char* ok = ::gets(s);
	if (!ok)
		return false;

	string cmd(s);
	try
	{
		if (cmd=="")
		{
		}
		else if (cmd=="help")
		{
			print_help();
		}
		else if (cmd=="exit")
		{
			b_continue = false;
		}
		else if (cmd=="run")
		{
			g.a1.run();
		}
		else if (cmd=="test")
		{
			test();
		}
		else
		{
			printf("Command \"%s\" is invalid.\nType help for a list of valid commands.\n",cmd.c_str());
		}
	}
	catch (char* e)
	{
		printf(e);
	}

	return b_continue;
}

void setup()
{
	HANDLE h = ::GetStdHandle(STD_OUTPUT_HANDLE);
	WORD attr(BACKGROUND_RED|BACKGROUND_GREEN|BACKGROUND_BLUE|BACKGROUND_INTENSITY);
	::SetConsoleTextAttribute(h,attr);
	COORD home;
	home.X = home.Y = 0;
	DWORD cc(0);
	::FillConsoleOutputAttribute(h,attr,65536,home,&cc);

	print_intro();
}

void print_intro()
{
	printf("The Apple //c Emulator for Windows 98\n");
	printf("Type help for a list of valid commands.\n");
}

void print_help()
{
	printf("exit\texit the program\n");
	printf("run\tstart the Apple //c emulator\n");
}

void test()
{
}
