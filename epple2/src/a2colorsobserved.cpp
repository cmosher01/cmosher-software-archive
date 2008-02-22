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
#include "a2colorsobserved.h"

#include <QtGui/QColor>

A2ColorsObserved::A2ColorsObserved():
	COLOR(0x10)
{
//	const unsigned int clr[] = { 0x1, 0xB, 0x3, 0x2, 0x7, 0x6, 0x4, 0xE, 0xC, 0x8, 0xD, 0x9, 0x5, 0xA, 0xF, 0x0 };
	const unsigned int map[] = { 0xF, 0x0, 0x3, 0x2, 0x6, 0xC, 0x5, 0x4, 0x9, 0xB, 0xD, 0x1, 0x8, 0xA, 0x7, 0xE };
	const unsigned int hue[] = { 342, 342, 277, 233, 233, 213, 160, 160,  75,  33,  52,  24,   0,   0,   0,   0 };
	const unsigned int sat[] = { 100,  50,  75, 100,  50, 100, 100, 100, 100, 100, 100, 100,   0,   0,   0,   0 };
	const unsigned int val[] = {  67, 100, 100,  75, 100, 100,  33, 100,  75,  50, 100, 100,  50,  50, 100,   0 };

	for (unsigned int i(0); i < COLOR.size(); ++i)
	{
		const QColor c = QColor::fromHsv(hue[map[i]],(int)(sat[map[i]]*(255.0/100.0)+.5),(int)(val[map[i]]*(255.0/100.0)+.5));
		COLOR[i] = (c.red()) | (c.green() << 8) | (c.blue() << 16);
	}
}

A2ColorsObserved::~A2ColorsObserved()
{
}
