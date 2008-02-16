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
#include "picturegenerator.h"
#include "videoaddressing.h"
#include "videodisplaydevice.h"
#include "videomode.h"
#include "applentsc.h"
#include "timinggenerator.h"

const int PictureGenerator::VISIBLE_X_OFFSET(VideoAddressing::BYTES_PER_ROW-VideoAddressing::VISIBLE_BYTES_PER_ROW);

PictureGenerator::PictureGenerator(VideoDisplayDevice& display, VideoMode& mode):
	display(display), mode(mode)
{
}


PictureGenerator::~PictureGenerator()
{
}


void PictureGenerator::powerOn()
{
	this->hpos = 0;
	this->line = 0;
	this->display.restartSignal();
}

void PictureGenerator::shiftLoRes()
{
	/*
	* For byte ABCDEFGH in register, perform
	* the following 4-bit end-around shifts:
	* 
	* +---<----+   +---<----+
	* |        |   |        |
	* +->ABCD->+   +->EFGH->+
	* 
	* Therefore:
	* 
	* ABCDEFGH --> DABCHEFG
	*/

	unsigned char rot_bits = this->latchGraphics & 0x11;
	// 000D000H
	rot_bits <<= 3;
	// D000H000

	this->latchGraphics &= 0xEE;
	// ABC0EFG0
	this->latchGraphics >>= 1;
	// 0ABC0EFG
	this->latchGraphics |= rot_bits;
	// DABCHEFG
}

void PictureGenerator::shiftHiRes()
{
	/*
	* For byte ABCDEFGH in register, perform
	* the following shift:
	* 
	* +---<----+
	* |        |
	* +->ABCD->+--->EFGH->
	* 
	* Therefore:
	* 
	* ABCDEFGH --> DABCDEFG
	*/

	unsigned char rot_bits = this->latchGraphics & 0x10;
	// 000D0000
	rot_bits <<= 3;
	// D0000000

	this->latchGraphics >>= 1;
	// 0ABCDEFG
	this->latchGraphics |= rot_bits;
	// DABCDEFG
}

void PictureGenerator::shiftText()
{
	this->latchText >>= 1;
}

bool PictureGenerator::getTextBit()
{
	return this->latchText & 1;
}

bool PictureGenerator::getHiResBit()
{
	return this->latchGraphics & 1;
}

bool PictureGenerator::getLoResBit(const bool odd, const bool vc)
{
	const int nibble = (this->latchGraphics >> (vc ? 4 : 0)) & 0x0F;
	return (nibble >> (odd ? 2 : 0)) & 1;
}

void PictureGenerator::loadGraphics(const unsigned char value)
{
	this->latchGraphics = value;
	this->d7 = this->latchGraphics & 0x80;
}

void PictureGenerator::loadText(const int value)
{
	this->latchText = value;
}


void PictureGenerator::tick(const int t, const unsigned char rowToPlot)
{
	if (this->mode.isDisplayingText(t))
		loadText(rowToPlot);
	else
		loadGraphics(rowToPlot);

	if (t==0)
	{
		this->line = 0;
	}

	int cycles = TimingGenerator::CRYSTAL_CYCLES_PER_CPU_CYCLE;
	if (this->hpos == TimingGenerator::HORIZ_CYCLES-1)
	{
		cycles += TimingGenerator::EXTRA_CRYSTAL_CYCLES_PER_CPU_LONG_CYCLE;
	}

	//		 hi-res half-pixel shift:
	const bool shift = this->mode.isHiRes() && !this->mode.isDisplayingText(t) && this->d7 && this->line < VideoAddressing::VISIBLE_ROWS_PER_FIELD && !(this->hpos < VISIBLE_X_OFFSET);
	const bool showLastHiRes = shift && this->lasthires;

	int xtra(0);
	if (shift)
	{
		--cycles;
		++xtra;
	}
	const int firstBlankedCycle(TimingGenerator::CRYSTAL_CYCLES_PER_CPU_CYCLE-xtra);

	for (int cycle(0); cycle < cycles-1; ++cycle)
	{
		const bool bit = shiftLatch(t,cycle);
		writeVideoSignal(shift,showLastHiRes,firstBlankedCycle,cycle,bit);
	}
	// optimization: pull the last iteration of the loop out, so we don't getHiResBit every time
	{
		this->lasthires = getHiResBit(); // save it for the next plotted byte, just in case we need it
		const int cycle = cycles-1;
		const bool bit = shiftLatch(t,cycle);
		writeVideoSignal(shift,showLastHiRes,firstBlankedCycle,cycle,bit);
	}

	++this->hpos;
	if (this->hpos >= TimingGenerator::HORIZ_CYCLES)
	{
		this->hpos = 0;
		++this->line;
	}
}

bool PictureGenerator::shiftLatch(const int t, const int cycle)
{
	bool bit;
	if (this->mode.isDisplayingText(t))
	{
		bit = getTextBit();
		if (cycle & 1) // @ 7MHz
		{
			shiftText();
		}
	}
	else if (this->mode.isHiRes())
	{
		bit = getHiResBit();
		if (cycle & 1) // @ 7MHz
		{
			shiftHiRes();
		}
	}
	else // LO-RES
	{
		const int y = t / VideoAddressing::BYTES_PER_ROW;
		bit = getLoResBit((t & 1) == (this->line & 1), y & 4);
		shiftLoRes();
	}
	return bit;
}

static const signed char lutCB[] = { AppleNTSC::BLANK_LEVEL,-AppleNTSC::CB_LEVEL,AppleNTSC::BLANK_LEVEL,+AppleNTSC::CB_LEVEL };

void PictureGenerator::writeVideoSignal(const bool shift, const bool showLastHiRes, const int firstBlankedCycle, const int cycle, const bool bit)
{
	if (shift && !cycle)
	{
		this->display.putSignal(showLastHiRes ? AppleNTSC::WHITE_LEVEL : AppleNTSC::BLANK_LEVEL);
	}

	const int hcycle(this->hpos*TimingGenerator::CRYSTAL_CYCLES_PER_CPU_CYCLE+cycle);
	signed char sig;
	if (this->line < VideoAddressing::VISIBLE_ROWS_PER_FIELD)
	{
		if (this->hpos < VISIBLE_X_OFFSET)
		{
			sig = hbl(hcycle);
		}
		else
		{
			if (bit && cycle < firstBlankedCycle)
			{
				sig = AppleNTSC::WHITE_LEVEL;
			}
			else
			{
				sig = AppleNTSC::BLANK_LEVEL;
			}
		}
	}
	else
	{
		sig = vbl(hcycle);
	}
	this->display.putSignal(sig);
}

signed char PictureGenerator::vbl(const int hcycle)
{
	signed char sig;
	if (224 <= this->line && this->line < 240) // VSYNC // TODO symbolize constants
	{
		sig = AppleNTSC::SYNC_LEVEL;
	}
	else
	{
		if (AppleNTSC::SYNC_START <= hcycle && hcycle < AppleNTSC::BP_START)
		{
			sig = AppleNTSC::SYNC_LEVEL;
		}
		else
		{
			sig = AppleNTSC::BLANK_LEVEL;
		}
	}
	return sig;
}

signed char PictureGenerator::hbl(const int hcycle)
{
	signed char cb;
	if (AppleNTSC::CB_START <= hcycle && hcycle < AppleNTSC::CB_END)
	{
		if (this->mode.isText()) // TODO && rev > 0
		{
			cb = AppleNTSC::BLANK_LEVEL;
		}
		else
		{
			cb = lutCB[(hcycle-AppleNTSC::CB_START)%4];
		}
	}
	else if (AppleNTSC::SYNC_START <= hcycle && hcycle < AppleNTSC::BP_START)
	{
		cb = AppleNTSC::SYNC_LEVEL;
	}
	else
	{
		cb = AppleNTSC::BLANK_LEVEL;
	}
	return cb;
}
