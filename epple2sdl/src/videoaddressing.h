/*
    epple2
    Copyright (C) 2008 by Chris Mosher <chris@mosher.mine.nu>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY, without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
#ifndef VIDEOADDRESSING_H
#define VIDEOADDRESSING_H

#include <vector>

class VideoAddressing
{
public:
	VideoAddressing();
	static void buildLUT(const unsigned short base, const unsigned short len, std::vector<unsigned short>& lut);

	enum { NTSC_LINES_PER_FRAME = 525 };
	enum { NTSC_LINES_PER_FIELD = 262 };
	enum { NTSC_FIELDS_PER_SECOND = 60 };
	enum { NTSC_COLOR_FIELD_EVERY = 1000 };

	enum { APPLE_BYTES = 1023750728 };
	enum { LINES = 15720000 };
	enum { BYTES_PER_ROW = 65 };

	enum { BYTES_PER_FIELD = 17030 };

	enum { VISIBLE_BITS_PER_BYTE = 7 };
	enum { VISIBLE_LINES_PER_CHARACTER = 8 };

	enum { VISIBLE_BYTES_PER_ROW = 40 };
	enum { VISIBLE_ROWS_PER_FIELD = 192 };

	enum { BLANKED_BYTES_PER_ROW = 25 };
	enum { VISIBLE_BYTES_PER_FIELD = 12480 };
	enum { SCANNABLE_ROWS = 256 };
	enum { SCANNABLE_BYTES = 16640 };
	enum { RESET_ROWS = 6 };
	enum { RESET_BYTES = 390 };
};

#endif
