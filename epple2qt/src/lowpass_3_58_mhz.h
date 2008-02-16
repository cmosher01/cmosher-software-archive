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
#ifndef LOWPASS_3_58_MHZ_H
#define LOWPASS_3_58_MHZ_H

class Lowpass_3_58_MHz
{
	int x[7];
	int y[7];

public:
	Lowpass_3_58_MHz();
	~Lowpass_3_58_MHz();

	/*
	Generated by:   http://www-users.cs.york.ac.uk/~fisher/mkfilter
	
	Summary
	You specified the following parameters:
	
	      filtertype 	= 	Bessel
	      passtype 	= 	Lowpass
	      ripple 	= 	
	      order 	= 	2
	      samplerate 	= 	14318181.818181818181818181818182
	      corner1 	= 	3579545.4545454545454545454545455
	      corner2 	= 	
	      adzero 	= 	3579545.4545454545454545454545455
	      logmin 	= 	
	*/

	int next(const int v)
	{
		x[0] = x[1]; x[1] = x[2]; x[2] = x[3]; x[3] = x[4];
		x[4] = v/6;

		y[0] = y[1]; y[1] = y[2]; y[2] = y[3]; y[3] = y[4];
		y[4] = x[0]+x[4]+((x[1]+x[2]+x[3])<<1)-y[2]/12-(y[3]>>2);

		return y[4];
	}
};

#endif
