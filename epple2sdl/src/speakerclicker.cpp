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

SpeakerClicker::SpeakerClicker()
{
	SDL_AudioSpec audio;
	audio.freq = 44100; // samples per second
	audio.format = AUDIO_S16SYS; // 16 bits (2 bytes) per sample
	audio.channels = 1; // mono
	audio.silence = 0;
	audio.samples = 4096; // samples in buffer (approx. 100 ms)
	audio.size = 0;
	audio.callback = 0;
	audio.userdata = 0;
	if (SDL_OpenAudio(&audio,0) != 0)
	{
		std::cerr << "Unable to initialize audio: " << SDL_GetError() << std::endl;
	}
}


SpeakerClicker::~SpeakerClicker()
{
}


void SpeakerClicker::tick()
{
}

void SpeakerClicker::click()
{
}
