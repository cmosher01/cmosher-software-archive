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
#ifndef APPLE2_H
#define APPLE2_H

#include "timable.h"
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
class Emulator;

class Apple2 : public Timable
{
	Slots slts;
	VideoMode videoMode;
	Keyboard kbd;
	Paddles paddles;
	SpeakerClicker speaker;
	Memory rom;
	Memory ram;
	AddressBus addressBus;
	PictureGenerator picgen;
	TextCharacters textRows;
	Video video;
	CPU cpu;
	PowerUpReset powerUpReset;
	int revision;

public:
	Apple2(KeypressQueue& keypresses, PaddleButtonStates& paddleButtonStates, AnalogTV& tv, HyperMode& fhyper, KeyboardBufferMode& buffered);
	~Apple2();

	void powerOn();
	void powerOff();
	void reset();

	virtual void tick();

	friend class Emulator;
};

#endif
