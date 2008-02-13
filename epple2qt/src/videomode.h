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
#ifndef VIDEOMODE_H
#define VIDEOMODE_H

class VideoMode
{
private:
	static const int MIXED_TEXT_LINES;
	static const int ROWS_PER_TEXT_LINE;
	static const int MIXED_TEXT_CYCLE;

	bool swText;
	bool swMixed;
	int swPage2;
	bool swHiRes;

public:
	VideoMode();
	unsigned char io(const unsigned short addr, const unsigned char b);
	bool isText();
	bool isHiRes();
	bool isMixed();
	int getPage();
	bool isDisplayingText(const int atTickInField);
	void powerOn();
};

#endif
