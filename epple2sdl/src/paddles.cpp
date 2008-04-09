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
#include "paddles.h"
#include <SDL/SDL.h>

Paddles::Paddles():
	rTick(PADDLE_COUNT)
{
}


Paddles::~Paddles()
{
}


void Paddles::tick()
{
	for (int paddle = 0; paddle < PADDLE_COUNT; ++paddle)
	{
		if (this->rTick[paddle] > 0)
			--this->rTick[paddle];
	}
}

void Paddles::startTimers()
{
	try
	{
		tryStartPaddleTimers();
	}
	catch (...)
	{
		// TODO
	}
}

void Paddles::tryStartPaddleTimers()
{
	int x, y;
	SDL_GetMouseState(&x,&y);

	double pMin = 0;
	double pMax = 500;
	x = (int)((x-pMin)/(pMax-pMin)*PADDLE_CYCLES+.5);
	y = (int)((y-pMin)/(pMax-pMin)*PADDLE_CYCLES+.5);

	if (isTimedOut(0))
		this->rTick[0] = x;
	if (isTimedOut(1))
		this->rTick[1] = y;

	if (isTimedOut(2))
		this->rTick[2] = REALTIME_1MS_CYCLES;
	if (isTimedOut(3))
		this->rTick[3] = REALTIME_100US_CYCLES;
}

bool Paddles::isTimedOut(const int paddle)
{
	if (paddle < 0 || PADDLE_COUNT <= paddle)
	{
		return false;
	}
	return this->rTick[paddle] <= 0;
}
