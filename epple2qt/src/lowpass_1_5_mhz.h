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
#ifndef LOWPASS_1_5_MHZ_H
#define LOWPASS_1_5_MHZ_H

class Lowpass_1_5_MHz
{
public:
	Lowpass_1_5_MHz();
	~Lowpass_1_5_MHz();

	int x[3];
	int y[5];

	int next(const int v)
	{
		x[0] = x[1]; x[1] = x[2];
		x[2] = v >> 3;

		y[0] = y[1]; y[1] = y[2]; y[2] = y[3]; y[3] = y[4];
		y[4] = x[0]+x[2]-y[0]/22+y[1]/3-y[2]+y[3]+(y[3]>>1);

		return y[4];
	}

};

#endif
