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
#ifndef PICTUREGENERATOR_H
#define PICTUREGENERATOR_H

class VideoDisplayDevice;
class VideoMode;

class PictureGenerator
{
private:
	VideoDisplayDevice& display;
	VideoMode& mode;

	unsigned char latchGraphics;
	bool d7;
	unsigned char latchText;
	unsigned int hpos;
	unsigned int line;
	bool lasthires;

	void shiftLoRes();
	void shiftHiRes();
	void shiftText();
	bool getTextBit();
	bool getHiResBit();
	bool getLoResBit(const bool odd, const bool vc);
	void loadGraphics(const unsigned char value);
	void loadText(const int value);
	bool shiftLatch(const int t, const int cycle);
	void writeVideoSignal(const bool shift, const bool showLastHiRes, const int firstBlankedCycle, const int cycle, const bool bit);
	signed char vbl(const int hcycle);
	signed char hbl(const int hcycle);

	const unsigned int VISIBLE_X_OFFSET;

public:

	PictureGenerator(VideoDisplayDevice& display, VideoMode& mode);
	~PictureGenerator();

	void powerOn();
	void tick(const int t, const unsigned char c);
};

#endif
