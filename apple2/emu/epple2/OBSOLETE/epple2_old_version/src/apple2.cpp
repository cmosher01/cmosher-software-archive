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
#include "apple2.h"
#include "slots.h"
#include "videomode.h"
#include "keyboard.h"
#include "addressbus.h"
#include "memory.h"
#include "picturegenerator.h"
#include "textcharacters.h"
#include "video.h"
#include "cpu.h"
#include "paddles.h"
#include "paddlebuttonstates.h"
#include "speakerclicker.h"
#include "analogtv.h"
#include "powerupreset.h"
#include "diskcontroller.h"
#include "languagecard.h"

#include <iostream>
#include <fstream>

Apple2::Apple2(KeypressQueue& keypresses, PaddleButtonStates& paddleButtonStates, AnalogTV& tv, HyperMode& fhyper, KeyboardBufferMode& buffered):
	kbd(keypresses,fhyper,buffered),
	ram(AddressBus::MOTHERBOARD_RAM_SIZ),
	rom(AddressBus::MOTHERBOARD_ROM_SIZ),
	addressBus(ram,rom,kbd,videoMode,paddles,paddleButtonStates,speaker,slts),
	picgen(tv,videoMode,this->revision),
	video(videoMode,addressBus,picgen,textRows),
	cpu(addressBus),
	powerUpReset(*this),
	revision(1)
{
}

Apple2::~Apple2()
{
}


void Apple2::tick()
{
	this->cpu.tick();
	this->video.tick();
	this->paddles.tick();
	this->speaker.tick();

	if (this->revision > 0)
		this->powerUpReset.tick();
}

void Apple2::powerOn()
{
	this->ram.powerOn();
	this->cpu.powerOn();
	this->videoMode.powerOn();
	this->video.powerOn();
	this->picgen.powerOn();
	this->powerUpReset.powerOn();
	// TODO clear up all other things for Apple ][ power-on
}

void Apple2::powerOff()
{
	this->ram.powerOff();
}

void Apple2::reset()
{
	this->cpu.reset();
	this->slts.reset();
}
