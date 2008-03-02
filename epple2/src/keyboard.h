//
// C++ Interface: Keyboard
//
// Description: 
//
//
// Author: Chris Mosher,,, <chris@mosher.mine.nu>, (C) 2008
//
// Copyright: See COPYING file that comes with this distribution
//
//
#ifndef KEYBOARD_H
#define KEYBOARD_H

#include <queue>

typedef std::queue<unsigned char> KeypressQueue;

class HyperMode;
class KeyboardBufferMode;

class Keyboard
{
private:
	KeypressQueue& keys;
	HyperMode& hyper;
	KeyboardBufferMode& buffered;

	unsigned char latch;

public:
	Keyboard(KeypressQueue& q, HyperMode& hyper, KeyboardBufferMode& buffered);
	void clear();
	unsigned char get();
};

#endif
