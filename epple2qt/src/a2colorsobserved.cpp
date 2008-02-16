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

A2ColorsObserved::A2ColorsObserved()
{
}

const unsigned int A2ColorsObserved::clr[] = { 0x1, 0xB, 0x3, 0x2, 0x7, 0x6, 0x4, 0xE, 0xC, 0x8, 0xD, 0x9, 0x5, 0xA, 0xF, 0x0 };
const unsigned int A2ColorsObserved::map[] = { 0xF, 0x0, 0x3, 0x2, 0x6, 0xC, 0x5, 0x4, 0x9, 0xB, 0xD, 0x1, 0x8, 0xA, 0x7, 0xE };
const unsigned int A2ColorsObserved::hue[] = { 342, 342, 277, 233, 233, 213, 160, 160,  75,  33,  52,  24,   0,   0,   0,   0 };
const unsigned int A2ColorsObserved::sat[] = { 100,  50,  75, 100,  50, 100, 100, 100, 100, 100, 100, 100,   0,   0,   0,   0 };
const unsigned int A2ColorsObserved::val[] = {  67, 100, 100,  75, 100, 100,  33, 100,  75,  50, 100, 100,  50,  50, 100,   0 };

const unsigned int A2ColorsObserved::COLOR[] =
{
	QColor::fromHsv(hue[map[0x0]],(int)(sat[map[0x0]]*(255.0/100.0)+.5),(int)(val[map[0x0]]/(255.0/100.0)+.5)).rgb(),
	QColor::fromHsv(hue[map[0x1]],(int)(sat[map[0x1]]*(255.0/100.0)+.5),(int)(val[map[0x1]]/(255.0/100.0)+.5)).rgb(),
	QColor::fromHsv(hue[map[0x2]],(int)(sat[map[0x2]]*(255.0/100.0)+.5),(int)(val[map[0x2]]/(255.0/100.0)+.5)).rgb(),
	QColor::fromHsv(hue[map[0x3]],(int)(sat[map[0x3]]*(255.0/100.0)+.5),(int)(val[map[0x3]]/(255.0/100.0)+.5)).rgb(),
	QColor::fromHsv(hue[map[0x4]],(int)(sat[map[0x4]]*(255.0/100.0)+.5),(int)(val[map[0x4]]/(255.0/100.0)+.5)).rgb(),
	QColor::fromHsv(hue[map[0x5]],(int)(sat[map[0x5]]*(255.0/100.0)+.5),(int)(val[map[0x5]]/(255.0/100.0)+.5)).rgb(),
	QColor::fromHsv(hue[map[0x6]],(int)(sat[map[0x6]]*(255.0/100.0)+.5),(int)(val[map[0x6]]/(255.0/100.0)+.5)).rgb(),
	QColor::fromHsv(hue[map[0x7]],(int)(sat[map[0x7]]*(255.0/100.0)+.5),(int)(val[map[0x7]]/(255.0/100.0)+.5)).rgb(),
	QColor::fromHsv(hue[map[0x8]],(int)(sat[map[0x8]]*(255.0/100.0)+.5),(int)(val[map[0x8]]/(255.0/100.0)+.5)).rgb(),
	QColor::fromHsv(hue[map[0x9]],(int)(sat[map[0x9]]*(255.0/100.0)+.5),(int)(val[map[0x9]]/(255.0/100.0)+.5)).rgb(),
	QColor::fromHsv(hue[map[0xA]],(int)(sat[map[0xA]]*(255.0/100.0)+.5),(int)(val[map[0xA]]/(255.0/100.0)+.5)).rgb(),
	QColor::fromHsv(hue[map[0xB]],(int)(sat[map[0xB]]*(255.0/100.0)+.5),(int)(val[map[0xB]]/(255.0/100.0)+.5)).rgb(),
	QColor::fromHsv(hue[map[0xC]],(int)(sat[map[0xC]]*(255.0/100.0)+.5),(int)(val[map[0xC]]/(255.0/100.0)+.5)).rgb(),
	QColor::fromHsv(hue[map[0xD]],(int)(sat[map[0xD]]*(255.0/100.0)+.5),(int)(val[map[0xD]]/(255.0/100.0)+.5)).rgb(),
	QColor::fromHsv(hue[map[0xE]],(int)(sat[map[0xE]]*(255.0/100.0)+.5),(int)(val[map[0xE]]/(255.0/100.0)+.5)).rgb(),
	QColor::fromHsv(hue[map[0xF]],(int)(sat[map[0xF]]*(255.0/100.0)+.5),(int)(val[map[0xF]]/(255.0/100.0)+.5)).rgb(),
};
