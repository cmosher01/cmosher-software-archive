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
#include "screenimage.h"

#include <SDL/SDL.h>

static const char* power =
"@@@@    @@@   @    @    @  @@@@@  @@@@@ "
"@   @  @   @  @   @ @   @  @      @    @"
"@   @  @   @  @   @ @   @  @      @    @"
"@@@@   @   @   @ @   @ @   @@@@@  @@@@@ "
"@      @   @   @ @   @ @   @      @  @  "
"@      @   @   @ @   @ @   @      @   @ "
"@       @@@     @     @    @@@@@  @    @";

#define POWERD 56
#define LABEL_Y 24
#define ON_CLR 0xFFE050
#define OFF_CLR 0x807870


ScreenImage::ScreenImage()
{
	this->screen = SDL_SetVideoMode(WIDTH,HEIGHT+POWERD,32,SDL_HWSURFACE|SDL_HWPALETTE);//|SDL_FULLSCREEN);//|SDL_ANYFORMAT);//|SDL_FULLSCREEN);
	if (this->screen == NULL)
	{
		printf("Unable to set video mode: %s\n",SDL_GetError());
		throw 0; // TODO
	};
}

void ScreenImage::drawPower(bool on)
{
	unsigned int* pn = (unsigned int*)this->screen->pixels;
	pn += HEIGHT*(this->screen->pitch/4);
	for (int r = 0; r < POWERD; ++r)
	{
		if (r < LABEL_Y || LABEL_Y+7 < r)
		{
			for (int c = 0; c < POWERD; ++c)
			{
				*pn++ = on ? ON_CLR : OFF_CLR;
			}
		}
		else
		{
			for (int c = 0; c < 8; ++c)
			{
				*pn++ = on ? ON_CLR : OFF_CLR;
			}
			for (const char* ppow = power+(r-(LABEL_Y+1))*40; ppow < power+(r-LABEL_Y)*40; ++ppow)
			{
				if (*ppow == '@')
					*pn++ = 0;
				else
					*pn++ = on ? ON_CLR : OFF_CLR;
			}
			for (int c = 0; c < 8; ++c)
			{
				*pn++ = on ? ON_CLR : OFF_CLR;
			}
		}
		pn -= POWERD;
		pn += this->screen->pitch/4;
	}
	SDL_UpdateRect(this->screen,0,HEIGHT,POWERD,HEIGHT+POWERD);
}

ScreenImage::~ScreenImage()
{
}

void ScreenImage::notifyObservers()
{
	SDL_UpdateRect(this->screen,0,0,WIDTH,HEIGHT+POWERD);
}

void ScreenImage::setElem(const unsigned int i, const unsigned int val)
{
	unsigned int* pn = (unsigned int*)this->screen->pixels;
	pn[i] = val;
}

void ScreenImage::blank()
{
	// TODO memset(this->img,0,WIDTH*HEIGHT*4);
}

// TODO dump PNG
//void ScreenImage::dump(const String type, const File file) throws IOException
//{
//	ImageIO.write(this->image,type,file);
//}
