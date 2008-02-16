/***************************************************************************
 *   Copyright (C) 2008 by Chris Mosher,,,   *
 *   chris@mosher.mine.nu   *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/
#include "emulator.h"
#include "timinggenerator.h"

Emulator::Emulator():
	display(screenImage),
	apple2(keypresses,paddleButtonStates,display/*,hyper,buffered*/),
	videoStatic(display)
{
}


Emulator::~Emulator()
{
}


void Emulator::powerOnComputer()
{
	if (this->timer)
	{
		this->timer->shutdown();
		this->timer = 0;
	}
	this->apple2.powerOn();

	this->timer = new TimingGenerator(this->apple2,this->throttle);
	this->timer->run();
}

void Emulator::powerOffComputer()
{
	if (this->timer)
	{
		this->timer->shutdown();
		this->timer = 0;
	}
	// TODO ask if unsaved changes
	this->apple2.powerOff();
	this->timer = new TimingGenerator(this->videoStatic,this->throttle);
	this->timer->run();
}

void Emulator::close()
{
	if (this->timer)
	{
		this->timer->shutdown();
		this->timer = 0;
	}
}


//void Emulator::config(final Config cfg) throws IOException, InvalidMemoryLoad, InvalidDiskImage
//{
//	cfg.parseConfig(this->apple2.rom,this->apple2.slts,this->hyper,getStdInEOF());
//}
