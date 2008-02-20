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
#include "paddles.h"



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
/*
	const PointerInfo mouse = MouseInfo.getPointerInfo();
	const Rectangle rect = mouse.getDevice().getDefaultConfiguration().getBounds();
	const Point loc = mouse.getLocation();

	double p = loc.getX();
	double pMin = rect.getMinX();
	double pMax = rect.getMaxX()/2;
	const int x = (int)Math.round(Math.rint((p-pMin)/(pMax-pMin)*PADDLE_CYCLES));

	p = loc.getY();
	pMin = rect.getMinY();
	pMax = rect.getMaxY()/2;
	const int y = (int)Math.round(Math.rint((p-pMin)/(pMax-pMin)*PADDLE_CYCLES));
*/
int x(0); int y(0); // TODO paddles
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
