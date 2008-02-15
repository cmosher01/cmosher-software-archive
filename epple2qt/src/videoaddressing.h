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
#ifndef VIDEOADDRESSING_H
#define VIDEOADDRESSING_H

#include <vector>

class VideoAddressing
{
public:
	VideoAddressing();
	static void buildLUT(const unsigned short base, const unsigned short len, std::vector<unsigned short>& lut);

	static const int NTSC_LINES_PER_FRAME;
	static const int NTSC_LINES_PER_FIELD;
	static const int NTSC_FIELDS_PER_SECOND;
	static const int NTSC_COLOR_FIELD_EVERY;
	
	static const int APPLE_BYTES;
	static const int LINES;
	static const int BYTES_PER_ROW;
	
	static const int BYTES_PER_FIELD;
	
	static const int VISIBLE_BITS_PER_BYTE;
	static const int VISIBLE_LINES_PER_CHARACTER;
	
	static const int VISIBLE_BYTES_PER_ROW;
	static const int VISIBLE_ROWS_PER_FIELD;
	
	static const int BLANKED_BYTES_PER_ROW;
	static const int VISIBLE_BYTES_PER_FIELD;
	static const int SCANNABLE_ROWS;
	static const int SCANNABLE_BYTES;
	static const int RESET_ROWS;
	static const int RESET_BYTES;
};

#endif
