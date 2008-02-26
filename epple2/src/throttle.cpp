/***************************************************************************
 *   Copyright (C) 2008 by Chris Mosher,,,   *
 *   chris@mosher.mine.nu   *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/
#include "throttle.h"
#include "timinggenerator.h"
#include "util.h"


Throttle::Throttle():
	ticksPrev(clock()),
	times(0),
	suspend(false)
{
}

Throttle::~Throttle()
{
}


void Throttle::tick()
{
	suspendIfNecessary();
	throttleIfNecessary();
}
#include <iostream>
void Throttle::throttleIfNecessary()
{
	/*
	 * Check every 100 milliseconds to see how far
	 * ahead we are, and sleep by the difference.
	 */
	++this->times;
	if (this->times >= CHECK_EVERY_CYCLE)
	{
		const clock_t ticksActual = clock()-this->ticksPrev;
		const long ticksDelta = EXPECTED_TICKS-ticksActual;
//		this->speedRatio = (float)ticksDelta/ticksActual;
//		std::cout << speedRatio << std::endl;
		sleep(ticksDelta);

		this->ticksPrev = clock();
		this->times = 0;
	}
}

void Throttle::sleep(const long ticksDelta)
{
	if (ticksDelta > 0)
	{
#ifdef _MSC_VER
		clock_t ticksEnd = clock()+ticksDelta;
		while (clock() < ticksEnd)
		{
			// just spin here
		}
#else
		struct timespec sleepTime;
		sleepTime.tv_sec = ticksDelta/CLOCKS_PER_SEC;
		sleepTime.tv_nsec = ticksDelta*NANOS_PER_CLOCK;
		nanosleep(&this->sleepTime,0);
#endif
	}
}

void Throttle::suspendIfNecessary()
{
/*	while (this->suspend.get())
	{
		try
		{
			this->suspend.wait();
		}
		catch (InterruptedException e)
		{
			Thread.currentThread().interrupt();
		}
	}
*/}

void Throttle::toggleSuspend()
{
	this->suspend = !this->suspend;
//	this->suspend.notifyAll();
}

float Throttle::getSpeedRatio()
{
	return this->speedRatio;
}
