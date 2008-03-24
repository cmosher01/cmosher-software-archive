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
#include "speakerclicker.h"
#include <SDL/SDL.h>
#include <iostream>
#include <ostream>
#include <deque>

std::deque<char> locbuf;
volatile bool done;
SDL_cond* bufchg;
SDL_mutex* buflck;

void fillbuf(void *userdata, Uint8 *stream, int len)
{
	int tot(0);
	while (len && !done)
	{
		// wait for locbuf to have some data
		// (it gets filled up by tick() method)
		SDL_LockMutex(buflck);
		while (locbuf.empty() && !done)
		{
			SDL_CondWait(bufchg,buflck);
		}
		if (done)
		{
			SDL_UnlockMutex(buflck);
			return;
		}
		while (locbuf.size() > 0 && len > 0)
		{
			int v = locbuf.front();
			*stream++ = v;

			if (v < 0) v = -v;
			tot += v;

			--len;
			locbuf.pop_front();
		}
		if (tot <= 0)
		{
			if (locbuf.size() >= 1024)
				locbuf.clear();
		}
		else
		{
			if (locbuf.size() >= 65536);
				locbuf.clear();
		}
		SDL_UnlockMutex(buflck);
	}
}

#define TICKS_PER_SAMPLE 47
#define AMP 126

SpeakerClicker::SpeakerClicker():
	clicked(false),
	t(TICKS_PER_SAMPLE)
{
	done = false;

	SDL_AudioSpec audio;
	audio.freq = 22050; // samples per second
	audio.format = AUDIO_S8; // 8 bits (1 byte) per sample
	audio.channels = 1; // mono
	audio.silence = 0;
	audio.samples = 4096;
	audio.callback = fillbuf;
	audio.userdata = 0;
	if (SDL_OpenAudio(&audio,0) != 0)
	{
		done = true;
		std::cerr << "Unable to initialize audio: " << SDL_GetError() << std::endl;
	}
	else
	{
		bufchg = SDL_CreateCond();
		buflck = SDL_CreateMutex();

//		std::cout << "initialized audio: " << std::endl;
//		std::cout << "freq: " << audio.freq << std::endl;
//		std::cout << "format: " << (audio.format==AUDIO_S8?"8-bit":"other") << std::endl;
//		std::cout << "channels: " << (int)audio.channels << std::endl;
//		std::cout << "silence: " << (int)audio.silence << std::endl;
//		std::cout << "samples: " << audio.samples << std::endl;
//		std::cout << "size: " << audio.size << std::endl;
		SDL_PauseAudio(0);
	}
}


SpeakerClicker::~SpeakerClicker()
{
	if (done)
	{
		return;
	}
	done = true;
	SDL_LockMutex(buflck);
	SDL_CondSignal(bufchg);
	SDL_UnlockMutex(buflck);
	// TODO delete cond and mutex (in ~SpeakerClicker or in fillbuf?)
	SDL_CloseAudio();
}

/*
void SpeakerClicker::tick()
{
	--this->t;
	if (this->t <= 0)
	{
		this->t = TICKS_PER_SAMPLE;
		if (this->clicked)
		{
			this->positive = !this->positive;
			this->clicked = false;
		}
		locbuf.push_back(this->positive ? AMP : -AMP);
		locbuf.push_back(this->positive ? AMP : -AMP);
	}
}
*/

void SpeakerClicker::tick()
{
	if (done)
	{
		return;
	}

	--this->t;
	if (this->t <= 0)
	{
		this->t = TICKS_PER_SAMPLE;
		int amp;
		if (this->clicked)
		{
			// TODO synchronize access to locbuf (test with LudeRunner.nib)
			amp = this->positive ? AMP : -AMP;
			this->positive = !this->positive;
			this->clicked = false;
		}
		else
		{
			amp = 0;
		}
		SDL_LockMutex(buflck);
		locbuf.push_back(amp);
		SDL_CondSignal(bufchg);
		SDL_UnlockMutex(buflck);
	}
}

void SpeakerClicker::click()
{
	this->clicked = true;
}
