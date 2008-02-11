#include "memory.h"
#include <iostream>

int main ()
{
	Memory m(0xC000);

	std::cout << std::hex << m.size() << std::endl;

	m.write(0x2000,3);
	std::cout << (int)m.read(0x2000) << std::endl;

	m.clear();
	std::cout << (int)m.read(0x2000) << std::endl;

	return 0;
}
