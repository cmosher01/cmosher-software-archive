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
	quit(false),
	repeat(false),
	keysDown(0)
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
	this->screenImage.drawPower(true);

	this->timable = &this->apple2;
}

void Emulator::powerOffComputer()
{
	// TODO ask if unsaved changes
	this->apple2.powerOff();
	this->screenImage.drawPower(false);
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
#define CHECK_CYCLES_K 51024000
#define EXPECTED_MS 50

// U.A.2 p. 7-13: REPT key repeats at 10Hz
#define CYCLES_PER_REPT 102048

enum { EXPECTED_TICKS = CLOCKS_PER_SEC/20 };
enum { NANOS_PER_CLOCK = 1000000000/CLOCKS_PER_SEC };


int Emulator::run()
{
	int skip = CHECK_EVERY_CYCLE;
	Uint32 prev_ms = SDL_GetTicks();
	clock_t ticksPrev = clock();
	while (!this->quit)
	{
		if (this->timable)
		{
			this->timable->tick();
			if (this->repeat)
			{
				--this->rept;
				if (this->rept <= 0)
				{
					this->rept = CYCLES_PER_REPT;
					if (this->keysDown > 0)
					{
						this->keypresses.push(this->lastKeyDown);
					}
				}
			}
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
				case SDL_KEYUP:
					dispatchKeyUp(event.key);
				break;
				}
			}
			if (!this->fhyper.isHyper())
			{
				const int delta_ms = EXPECTED_MS-(SDL_GetTicks()-prev_ms);
				if (0 < delta_ms && delta_ms <= EXPECTED_MS)
				{
					SDL_Delay(delta_ms);
				}
	
			}
			this->screenImage.displayHz(CHECK_CYCLES_K/(SDL_GetTicks()-prev_ms));
			prev_ms = SDL_GetTicks();
		}
	}
	SDL_Quit();
	return 0;
}

void Emulator::dispatchKeyUp(const SDL_KeyboardEvent& keyEvent)
{
	unsigned char key = (unsigned char)(keyEvent.keysym.unicode & 0x7F);
	SDLKey sym = keyEvent.keysym.sym;
	SDLMod mod = keyEvent.keysym.mod;
	unsigned char scancode = keyEvent.keysym.scancode;
//	printf("key UP: %d    sym: %d    mod: %04X    scn: %d\n",key,sym,mod,scancode);

	if (sym < 0x7F || sym == SDLK_LEFT || sym == SDLK_RIGHT)
	{
		--this->keysDown;
	}
	else if (sym == SDLK_F10)
	{
		this->repeat = false;
		this->rept = 0;
	}
}

void Emulator::dispatchKeypress(const SDL_KeyboardEvent& keyEvent)
{
	unsigned char key = (unsigned char)(keyEvent.keysym.unicode & 0x7F);
	SDLKey sym = keyEvent.keysym.sym;
	SDLMod mod = keyEvent.keysym.mod;
	unsigned char scancode = keyEvent.keysym.scancode;

//	printf("key DN: %d    sym: %d    mod: %04X    scn: %d\n",key,sym,mod,scancode);

	if (sym < 0x7F || sym == SDLK_LEFT || sym == SDLK_RIGHT)
	{
		++this->keysDown;
	}

	if (sym == SDLK_LEFT)
	{
		key = 8;
	}
	else if (sym == SDLK_RIGHT)
	{
		key = 21;
	}
	else if (sym == SDLK_PAUSE)
	{
//		printf("    RESET\n");
		this->apple2.reset();
		return;
	}
	else if (sym == SDLK_INSERT)
	{
		std::string s = this->clip.getText();
		for (int i = 0; i < s.length(); ++i)
		{
			key = s[i];
			if (key == '\n')
				key = '\r';
			if ('a' <= key && key <= 'z')
			{
				key -= 32;
			}
			this->keypresses.push(key);
		}
		return;
	}
	else if (sym == SDLK_F10)
	{
		this->repeat = true;
		this->rept = CYCLES_PER_REPT;
		return;
	}
	else if (sym == SDLK_F11)
	{
		this->fhyper.toggleHyper();
		this->screenImage.toggleHyperLabel();
		return;
	}
	else if (sym == SDLK_F12)
	{
		this->buffered.toggleBuffered();
		this->screenImage.toggleKdbBufferLabel();
		return;
	}
	else if (sym == SDLK_F1)
	{
		toggleComputerPower();
		return;
	}
	else if (sym == SDLK_F2)
	{
		this->display.cycleType();
		this->screenImage.cycleDisplayLabel();
		return;
	}
	else if (sym == SDLK_F3)
	{
		this->screenImage.toggleFullScreen();
		this->screenImage.drawPower(this->timable==&this->apple2);
		return;
	}
	else if (sym == SDLK_F4)
	{
		this->display.toggleBleedDown();
		this->screenImage.toggleFillLinesLabel();
		return;
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
	else if (((mod&KMOD_LCTRL) || (mod&KMOD_RCTRL)) && SDLK_KP0 <= sym && sym <= SDLK_KP9 && !(mod&KMOD_RSHIFT) && !(mod&KMOD_LSHIFT))
	{
		key = sym-SDLK_KP0+'0';
	}
	else if (((mod&KMOD_LCTRL) || (mod&KMOD_RCTRL)) && (('2' <= sym && sym <= '8') || sym == '/' || sym == ' ') && !(mod&KMOD_RSHIFT) && !(mod&KMOD_LSHIFT))
	{
		key = sym;
	}
	else if (sym == ']')
	{
		if ((mod&KMOD_LSHIFT) || (mod&KMOD_RSHIFT))
		{
			// ignore '}' (shift ']')
			return;
		}
		if ((mod&KMOD_LCTRL) || (mod&KMOD_RCTRL))
		{
			// Ctrl-] == ASCII: $1D
			key = 29;
		}
	}
	else if (key == 0 || sym == SDLK_TAB || sym == '`' || sym == '[' || sym == '\\' || sym == SDLK_DELETE)
	{
		return;
	}


//	printf("    sending to apple as ascii------------------------------>%02X (%02X)\n",key,key|0x80);
	this->keypresses.push(key);
	this->lastKeyDown = key;
}
