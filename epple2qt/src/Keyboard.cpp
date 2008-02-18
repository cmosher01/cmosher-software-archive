//
// C++ Implementation: Keyboard
//
// Description: 
//
//
// Author: Chris Mosher,,, <chris@mosher.mine.nu>, (C) 2008
//
// Copyright: See COPYING file that comes with this distribution
//
//
#include "Keyboard.h"
//#include "KeypressQueue.h"

Keyboard::Keyboard(KeypressQueue& q):
	keys(q)
{
}

void Keyboard::clear()
{
	this->latch &= 0x7F;
}
#include <iostream>//TODO remove
unsigned char Keyboard::get()
{
	if (!(this->latch & 0x80)) // TODO hyper and kdb buffer
	{
		if (!this->keys.empty())
		{
			this->latch = this->keys.front() | 0x80;
std::cout << "got key off queue: " << std::hex << (int)this->latch << std::endl;
			this->keys.pop();
		}
	}
	return this->latch;
}
