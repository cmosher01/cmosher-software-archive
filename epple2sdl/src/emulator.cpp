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
#include "config.h"
#include "timinggenerator.h"

#include <SDL/SDL.h>

Emulator::Emulator():
	throttle(fhyper),
	display(screenImage),
	videoStatic(display),
	apple2(keypresses,paddleButtonStates,display,fhyper,buffered),
	timable(0),
	quit(false)
{
}


Emulator::~Emulator()
{
}

void Emulator::toggleComputerPower()
{
	if (this->timable==&this->videoStatic)
		powerOnComputer();
	else
		powerOffComputer();
}

void Emulator::powerOnComputer()
{
	this->apple2.powerOn();

	this->timable = &this->apple2;
}

void Emulator::powerOffComputer()
{
	// TODO ask if unsaved changes
	this->apple2.powerOff();
	this->videoStatic.powerOn();

	this->timable = &this->videoStatic;
}

void Emulator::config(Config& cfg)
{
	cfg.parse(this->apple2.rom,this->apple2.slts/*,this->fhyper,getStdInEOF()*/,this->apple2.revision);
}

void Emulator::init()
{
}

#define CHECK_EVERY_CYCLE 51024
#define EXPECTED_MS 50

int Emulator::run()
{
	int skip = CHECK_EVERY_CYCLE;
	Uint32 prev_ms = SDL_GetTicks();
	while (!this->quit)
	{
		if (this->timable)
		{
			this->timable->tick();
		}
		--skip;
		if (!skip)
		{
			skip = CHECK_EVERY_CYCLE;
			SDL_Event event;
			while (SDL_PollEvent(&event))
			{
				switch (event.type)
				{
				case SDL_QUIT:
					this->quit = true;
				break;
				case SDL_KEYDOWN:
					dispatchKeypress(event.key);
				break;
				}
			}
			if (!this->fhyper.isHyper())
			{
				int actual_ms = SDL_GetTicks()-prev_ms;
				int dlta_ms = EXPECTED_MS-actual_ms;
				if (2 <= dlta_ms && dlta_ms <= EXPECTED_MS) // sanity check
				{
					SDL_Delay(dlta_ms);
				}
	
				prev_ms = SDL_GetTicks();
			}
		}
	}
	SDL_Quit();
	return 0;
}

void Emulator::dispatchKeypress(const SDL_KeyboardEvent& keyEvent)
{
	unsigned char key = (unsigned char)(keyEvent.keysym.unicode & 0x7F);
	SDLKey sym = keyEvent.keysym.sym;
	SDLMod mod = keyEvent.keysym.mod;
	unsigned char scancode = keyEvent.keysym.scancode;

//	printf("key: %d    sym: %d    mod: %04X    scn: %d\n",key,sym,mod,scancode);

	if (sym == SDLK_LEFT)
	{
		key = 8;
	}
	else if (sym == SDLK_RIGHT)
	{
		key = 21;
	}
	else if (sym == SDLK_UP)
	{
		key = 11;
	}
	else if (sym == SDLK_DOWN)
	{
		key = 10;
	}
	else if (sym == SDLK_PAUSE)
	{
//		printf("    RESET\n");
		this->apple2.reset();
	}
	else if (sym == SDLK_F11)
	{
		this->fhyper.toggleHyper();
//		printf("    hyper mode is now: %s\n",this->fhyper.isHyper()?"on":"off");
	}
	else if (sym == SDLK_F12)
	{
		this->buffered.toggleBuffered();
//		printf("    keyboard buffering is now: %s\n",this->buffered.isBuffered()?"on":"off");
	}
	else if (sym == SDLK_F1)
	{
		toggleComputerPower();
	}
	else if (sym == SDLK_F2)
	{
		this->display.cycleType();
	}
	else if (sym == SDLK_END)
	{
		this->quit = true;
		return;
	}

	if ('a' <= key && key <= 'z')
	{
		key -= 32;
	}

	if (((mod&KMOD_LSHIFT) || (mod&KMOD_RSHIFT)) && ((mod&KMOD_LCTRL) || (mod&KMOD_RCTRL)) && sym == '2')
	{
		// Ctrl-Shift-2 == Ctrl-@ == NUL == ASCII: 0
		key = 0;
	}
	else if (key == 0)
	{
		return;
	}


//	printf("    sending to apple as ascii------------------------------>%02X (%02X)\n",key,key|0x80);
	this->keypresses.push(key);
}
