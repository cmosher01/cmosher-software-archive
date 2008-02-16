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
#ifndef APPLENTSC_H
#define APPLENTSC_H

class AppleNTSC
{
private:
	AppleNTSC();

public:
	static const int V;
	static const int H;

	static const int SIGNAL_LEN;

	static const int FP_START;
	static const int SYNC_START;
	static const int BP_START;
	static const int CB_START;
	static const int CB_END;
	static const int SPIKE;
	static const int PIC_START;

	static const signed char WHITE_LEVEL;
	static const signed char BLANK_LEVEL;
	static const signed char SYNC_LEVEL;
	static const unsigned char LEVEL_RANGE;
	static const signed char CB_LEVEL;
};

#endif
