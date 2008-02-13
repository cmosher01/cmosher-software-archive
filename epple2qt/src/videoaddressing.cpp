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
#include "videoaddressing.h"
#include "timinggenerator.h"

VideoAddressing::VideoAddressing()
{
}

static const int MEGA(1000000);


static int calculateVisibleCharactersPerRow()
{
	/*
		*                                1000+1 seconds   2 fields   1 frame         1000000 microseconds         63   50
		* total horizontal line period = -------------- * -------- * ------------- * --------------------   =   ( -- + -- ) microseconds per line
		*                                60*1000 fields   1 frame    3*5*5*7 lines   1 second                          90
		*
		*                                                                                    10   81
		* horizontal blanking period = (1.5+4.7+.6+2.5+1.6) = 10.9 microseconds per line = ( -- + -- ) microseconds per line
		*                                                                                         90
		*
		* visible line period = total horizontal line period minus horizontal blanking period =
		* 
		* 52   59
		* -- + -- microseconds per line
		*      90
		*
		*
		* To avoid the over-scan area, the Apple ][ uses only the middle 75% of the visible line, or 4739/120 microseconds
		* 
		* Apple ][ uses half the clock rate, or 315/44 MHz, to oscillate the video signal.
		* 
		* The result is 315/44 MHz * 4739/120 microseconds/line, rounded down, = 282 full pixel spots across the screen.
		* The Apple ][ displays 7 bits per byte hi-res or lo-res, (or 7 pixel-wide characters for text mode), so that
		* gives 282/7, which rounds down to 40 bytes per line.
		*/
	return ((int)((double)(VideoAddressing::NTSC_COLOR_FIELD_EVERY+1)/(VideoAddressing::NTSC_FIELDS_PER_SECOND*VideoAddressing::NTSC_COLOR_FIELD_EVERY)*2/(VideoAddressing::NTSC_LINES_PER_FRAME)*MEGA - (1.5+4.7+.6+2.5+1.6)) *
		.75 *
		(TimingGenerator::CRYSTAL_HZ/2)) / MEGA / VideoAddressing::VISIBLE_BITS_PER_BYTE;
}

static int calculateVisibleRows()
{
	/*
		* NTSC total lines per frame (525) minus unusable lines (19 plus 20) = 486 usable lines
		* To avoid the over-scan area, use the middle 80% of the vertical lines, giving 388 (rounded down) clearly visible lines
		* Apple ][ uses only half the vertical resolution because it doesn't interlace, giving 194.
		* Text characters are 8 pixels tall, so 194/8 rounded down gives 24 text lines.
		* Multiply by 8 to give 192 lines total.
		*/
	return ((int)(VideoAddressing::NTSC_LINES_PER_FRAME-(20+19))*.8) / 2 / VideoAddressing::VISIBLE_LINES_PER_CHARACTER * VideoAddressing::VISIBLE_LINES_PER_CHARACTER;
}

static int calc(const int t)
{
	int c = t % VideoAddressing::VISIBLE_BYTES_PER_FIELD;
	if (t >= VideoAddressing::SCANNABLE_BYTES)
	{
		c -= VideoAddressing::RESET_BYTES;
	}

	int n = c / VideoAddressing::BYTES_PER_ROW;
	const int s = (n >> 6);
	n -= s << 6;
	const int q = (n >> 3);
	n -= q << 3;
	const int base = (n<<10) + (q<<7) + VideoAddressing::VISIBLE_BYTES_PER_ROW*s;

	const int half_page = base & 0xFF80;

	int a = base+(c%VideoAddressing::BYTES_PER_ROW)-VideoAddressing::BLANKED_BYTES_PER_ROW;
	if (a < half_page)
	{
		a += 0x80;
	}
	return a;
}

void VideoAddressing::buildLUT(const unsigned short base, const unsigned short len, unsigned short lut[])
{
	for (int t = 0; t < BYTES_PER_FIELD; ++t)
	{
		int off = (calc(t) % len);

		const int col = t % BYTES_PER_ROW;
		const int row = t / BYTES_PER_ROW;

		if (col < BLANKED_BYTES_PER_ROW)
		{
			// HBL
			if (base < 0x1000)
			{
				off += 0x1000;
			}
			if (col == 0)
			{
				++off;
			}
		}

		if (row >= VISIBLE_ROWS_PER_FIELD)
		{
			// VBL
			const int base2 = off & 0xFF80;
			off &= 0x7F;
			off -= 8;
			off &= 0x7F;
			off += base2;
		}

		lut[t] = base + off;
	}
}

const int VideoAddressing::NTSC_LINES_PER_FRAME(3*5*5*7);
const int VideoAddressing::NTSC_LINES_PER_FIELD(NTSC_LINES_PER_FRAME/2);
const int VideoAddressing::NTSC_FIELDS_PER_SECOND(60);
const int VideoAddressing::NTSC_COLOR_FIELD_EVERY(1000);

const int VideoAddressing::APPLE_BYTES((NTSC_COLOR_FIELD_EVERY+1)*TimingGenerator::CPU_HZ);
const int VideoAddressing::LINES(NTSC_FIELDS_PER_SECOND*NTSC_COLOR_FIELD_EVERY*NTSC_LINES_PER_FIELD);
const int VideoAddressing::BYTES_PER_ROW(APPLE_BYTES/LINES);

const int VideoAddressing::BYTES_PER_FIELD(BYTES_PER_ROW*NTSC_LINES_PER_FIELD);

const int VideoAddressing::VISIBLE_BITS_PER_BYTE(7);
const int VideoAddressing::VISIBLE_LINES_PER_CHARACTER(8);

const int VideoAddressing::VISIBLE_BYTES_PER_ROW(calculateVisibleCharactersPerRow());
const int VideoAddressing::VISIBLE_ROWS_PER_FIELD(calculateVisibleRows());

const int VideoAddressing::BLANKED_BYTES_PER_ROW(BYTES_PER_ROW-VISIBLE_BYTES_PER_ROW);
const int VideoAddressing::VISIBLE_BYTES_PER_FIELD(BYTES_PER_ROW*VISIBLE_ROWS_PER_FIELD);
const int VideoAddressing::SCANNABLE_ROWS(0x100);
const int VideoAddressing::SCANNABLE_BYTES(SCANNABLE_ROWS*BYTES_PER_ROW);
const int VideoAddressing::RESET_ROWS(NTSC_LINES_PER_FIELD-SCANNABLE_ROWS);
const int VideoAddressing::RESET_BYTES(RESET_ROWS*BYTES_PER_ROW);
