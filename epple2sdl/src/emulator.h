/***************************************************************************
 *   Copyright (C) 2008 by Chris Mosher,,,   *
 *   chris@mosher.mine.nu   *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/
#ifndef EMULATOR_H
#define EMULATOR_H

#include "throttle.h"
#include "keyboard.h"
#include "paddlebuttonstates.h"
#include "apple2.h"
#include "videostaticgenerator.h"
#include "screenimage.h"
#include "analogtv.h"
#include "keyboardbuffermode.h"
#include "hypermode.h"

class Timable;
//class EOFHandler;
class Config;
class SDL_KeyboardEvent;

class Emulator
{
protected:
	PaddleButtonStates paddleButtonStates;
	KeypressQueue keypresses;

	HyperMode fhyper;
	KeyboardBufferMode buffered;
	Throttle throttle;
	ScreenImage screenImage;
	AnalogTV display;
	VideoStaticGenerator videoStatic;
	Apple2 apple2;

	Timable* timable;

	bool quit;

	void dispatchKeypress(const SDL_KeyboardEvent& keyEvent);

public:
	Emulator();
	virtual ~Emulator();

	void config(Config& cfg);

	virtual void init();
//	virtual EOFHandler getStdInEOF() = 0;

	void powerOnComputer();
	void powerOffComputer();
	void toggleComputerPower();
	void cycleDisplayType();

	virtual int run();
};

#endif
