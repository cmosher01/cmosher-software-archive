#include "memory.h"
#include <vector>
#include <algorithm>
#include <istream>

static const int CLEAR_VALUE(0);

Memory::Memory(const size_t n):
	bytes(n)
{
}

size_t Memory::size() const
{
	return this->bytes.size();
}

unsigned char Memory::read(const unsigned short address) const
{
	return this->bytes[address];
}

void Memory::write(const unsigned short address, const unsigned char data)
{
	this->bytes[address] = data;
}

void Memory::clear()
{
	std::fill(this->bytes.begin(),this->bytes.end(),CLEAR_VALUE);
}

void Memory::powerOn()
{
//	const RAMInitializer initRam(*this);
//	initRam.init();
}

void Memory::powerOff()
{
	clear();
}

void Memory::load(std::istream& in)
{
	in.read((char*)this->bytes.front(),this->bytes.size());
}
