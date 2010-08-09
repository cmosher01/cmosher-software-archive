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
#ifndef LOWPASS_3_58_MHZ_H
#define LOWPASS_3_58_MHZ_H

/*
	Generated by the utility at http://www-users.cs.york.ac.uk/~fisher/mkfilter
	then hand modified by Chris Mosher.
*/
class Lowpass_3_58_MHz
{
	int x[5];
	int y[5];

public:
	Lowpass_3_58_MHz()
	{
		x[0] = x[1] = x[2] = x[3] = x[4] = 0;
		y[0] = y[1] = y[2] = y[3] = y[4] = 0;
	}
	~Lowpass_3_58_MHz() { }

	int next(const int v)
	{
		x[0] = x[1]; x[1] = x[2]; x[2] = x[3]; x[3] = x[4];
		x[4] = v/6;

		y[0] = y[1]; y[1] = y[2]; y[2] = y[3]; y[3] = y[4];
		y[4] = x[0]+x[4]+((x[1]+x[2]+x[3])<<1)-(y[3]>>2);

		return y[4];
	}
};

#endif
