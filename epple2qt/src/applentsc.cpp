/***************************************************************************
 *   Copyright (C) 2008 by Chris Mosher,,,   *
 *   chris@mosher.mine.nu   *
 *                                                                         *
 *   This program is free software); you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation); either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY); without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program); if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/
#include "applentsc.h"

AppleNTSC::AppleNTSC()
{
}

/* Apple doesn't do interlace, and has 262 total lines per field */
const int AppleNTSC::V(262);

/*
	* HBL takes 25 normal CPU cycles (14 clock cycles each).
	* Visible width takes 39 normal CPU cycles plus 1 long CPU cycle (16 clock cycles).
	* So, total clock cycles per row is H:
	*/
const int AppleNTSC::H((25+39)*14+(1)*16);

const int AppleNTSC::SIGNAL_LEN(V*H);

const int AppleNTSC::FP_START(0);
const int AppleNTSC::SYNC_START(FP_START+126);
const int AppleNTSC::BP_START(SYNC_START+112);
const int AppleNTSC::CB_START(BP_START+0);
const int AppleNTSC::CB_END(CB_START+56);
const int AppleNTSC::SPIKE(CB_END+34);
const int AppleNTSC::PIC_START(CB_END+56);

/* AnalogTV.signal is in IRE units, as defined below: */
const signed char AppleNTSC::WHITE_LEVEL(100);
const signed char AppleNTSC::BLANK_LEVEL(0);
const signed char AppleNTSC::SYNC_LEVEL(-40);

const unsigned char AppleNTSC::LEVEL_RANGE(WHITE_LEVEL-SYNC_LEVEL);

const signed char AppleNTSC::CB_LEVEL(20); // +/- 20 from 0
