/*
    epple2
    Copyright (C) 2008 by Chris Mosher <chris@mosher.mine.nu>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY, without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
#include "keyboard.h"
#include "hypermode.h"
#include "keyboardbuffermode.h"

Keyboard::Keyboard(KeypressQueue& q, HyperMode& fhyper, KeyboardBufferMode& buffered):
	keys(q),
	fhyper(fhyper),
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
