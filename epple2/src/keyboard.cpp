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
#include "keyboard.h"
#include "hypermode.h"
#include "keyboardbuffermode.h"

Keyboard::Keyboard(KeypressQueue& q, HyperMode& hyper, KeyboardBufferMode& buffered):
	keys(q),
	hyper(hyper),
	buffered(buffered)
{
}

void Keyboard::clear()
{
	this->latch &= 0x7F;
}

unsigned char Keyboard::get()
{
	// TODO wait if too fast (can we do this in standard C++???)
	if (!this->buffered.isBuffered() || !(this->latch & 0x80))
	{
		if (!this->keys.empty())
		{
			this->latch = this->keys.front() | 0x80;
			this->keys.pop();
		}
	}
	return this->latch;
}
