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
#ifndef SCREENIMAGE_H
#define SCREENIMAGE_H

#include "applentsc.h"
#include "videoaddressing.h"

class SDL_Surface;

class ScreenImage
{
public:
	ScreenImage();
	~ScreenImage();

	void notifyObservers();
	void setElem(const unsigned int i, const unsigned int val);
	void blank();
	enum
	{
		HEIGHT = VideoAddressing::VISIBLE_ROWS_PER_FIELD*2,
		WIDTH = AppleNTSC::H-AppleNTSC::PIC_START-2
	};

private:
	SDL_Surface* screen;
};

#endif
