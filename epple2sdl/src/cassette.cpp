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



/*
cassette tape image file format
-------------------------------
Each byte represents one half cycle, in 10-microsoecond units.
For example, consider the following values in the file (decimal):

65 65 65 65 65 20 25 50 50 25 25 25 25 50 50

This represents the following half-cycles (in microseconds)
650 650 650 650 650 200 250 500 500 250 250 250 250 500 500
which has the following meaning:

|--------HEADER-----|--sync-|-1-bit-|-0-bit-|-0-bit-|-1-bit-|
|                   |       |       |       |       |       |
|650 650 650 650 650|200 250|500 500|250 250|250 250|500 500|
*/

#include "cassette.h"
//#include <SDL/SDL.h>
//#include <iostream>

static int rd[65536];
static int id(0);
static int idLim(0);

static bool pos(false);
static int td;
static bool reading(false);

Cassette::Cassette():
	t(0), prevCassette(0)
{
}


Cassette::~Cassette()
{
}

void Cassette::tick()
{
	++this->t;
	--td;
}


void Cassette::output()
{
	if (reading)
		return;

	const unsigned int half_cycles_us = getHalfCycleTime();

	rd[id++] = half_cycles_us;
	idLim = id;

	this->prevCassette = this->t;
}

unsigned char Cassette::getHalfCycleTime() // in 10-microsecond units
{
	const unsigned int delta_cycles(this->t-this->prevCassette);
	if (delta_cycles < 225)
		return 20;
	if (delta_cycles < 375)
		return 25;
	if (delta_cycles < 575)
		return 50;
	return 65;
}

bool Cassette::input()
{
	if (id < 0 || idLim <= id)
	{
		rewind();
		reading = true;
		td = 0;
	}

	if (td <= 0)
	{
		pos = !pos;
		td = rd[++id]*10;
	}

	return pos;
}

void Cassette::rewind()
{
	rewindFull();
	while (rd[id] == 65)
		++id;
	id -= 20;
	if (id < 0 )
		id = 0;
}

void Cassette::rewindFull()
{
	id = 0;
}
