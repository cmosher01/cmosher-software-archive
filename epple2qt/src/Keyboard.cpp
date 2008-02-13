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

unsigned char Keyboard::get()
{
	if ((this->latch & 0x80) == 0)
	{
		if (!this->keys.empty())
		{
			this->latch = this->keys.front();
			this->keys.pop();
		}
	}
	return this->latch;
}
