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
#include "timinggenerator.h"
#include "videoaddressing.h"

VideoAddressing::VideoAddressing()
{
}
static int calc(const unsigned int t)
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

void VideoAddressing::buildLUT(const unsigned short base, const unsigned short len, std::vector<unsigned short>& lut)
{
	lut.resize(BYTES_PER_FIELD);
	for (unsigned int t = 0; t < BYTES_PER_FIELD; ++t)
	{
		unsigned int off = (calc(t) % len);

		const unsigned int col = t % BYTES_PER_ROW;
		const unsigned int row = t / BYTES_PER_ROW;

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
