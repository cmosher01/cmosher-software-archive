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
#include "Keyboard.h"
#include "paddlebuttonstates.h"
#include "apple2.h"
#include "videostaticgenerator.h"
#include "screenimage.h"
#include "analogtv.h"
class TimingGenerator;
//class EOFHandler;

class Emulator
{
private:
	Throttle throttle;
//	HyperMode hyper;
//	KeyboardBufferMode buffered;
	KeypressQueue keypresses;
	PaddleButtonStates paddleButtonStates;
	AnalogTV display;
	Apple2 apple2;
	VideoStaticGenerator videoStatic;
	ScreenImage screenImage;

	TimingGenerator* timer;

public:
	Emulator();
	virtual ~Emulator();

	virtual void init() = 0;
//	virtual EOFHandler getStdInEOF() = 0;

	void powerOnComputer();
	void powerOffComputer();
	void close();
};

#endif
