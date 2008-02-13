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
#include "videomode.h"
#include "videoaddressing.h"

VideoMode::VideoMode()
{
}


const int VideoMode::MIXED_TEXT_LINES(4);
const int VideoMode::ROWS_PER_TEXT_LINE(8);
const int VideoMode::MIXED_TEXT_CYCLE((VideoAddressing::VISIBLE_ROWS_PER_FIELD-(VideoMode::MIXED_TEXT_LINES*VideoMode::ROWS_PER_TEXT_LINE))*VideoAddressing::BYTES_PER_ROW);



unsigned char VideoMode::io(const unsigned short addr, const unsigned char b)
{
	const int sw = (addr & 0xE) >> 1;
	const bool on = addr & 0x1;
	switch (sw)
	{
		case 0:
			this->swText = on; break;
		case 1:
			this->swMixed = on; break;
		case 2:
			this->swPage2 = on ? 1 : 0; break;
		case 3:
			this->swHiRes = on; break;
	}
	return b;
}



bool VideoMode::isText()
{
	return this->swText;
}

bool VideoMode::isHiRes()
{
	return this->swHiRes;
}

bool VideoMode::isMixed()
{
	return this->swMixed;
}

int VideoMode::getPage()
{
	return this->swPage2;
}

bool VideoMode::isDisplayingText(const int atTickInField)
{
	return this->swText || (this->swMixed && atTickInField >= MIXED_TEXT_CYCLE);
}



void VideoMode::powerOn()
{
	this->swText = false;
	this->swMixed = false;
	this->swPage2 = 0;
	this->swHiRes = false;
}
