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

//TODO throttle


Throttle::Throttle():
	msPrev(0),
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

void Throttle::throttleIfNecessary()
{
	/*
	* Check every 100 milliseconds to see how far
	* ahead we are, and sleep by the difference.
	++this->times;
	if (this->times >= CHECK_EVERY_CYCLE)
	{
		final long msActual = System.currentTimeMillis()-this->msPrev;
		final long msDelta = EXPECTED_MS-msActual;
		this->speedRatio = (float)EXPECTED_MS/msActual;
		sleep(msDelta);

		this->msPrev = System.currentTimeMillis();
		this->times = 0;
	}
	*/
}

void Throttle::sleep(const long /*msDelta*/)
{
/*
	if (msDelta > 0)
	{
		try
		{
			Thread.sleep(msDelta);
		}
		catch (InterruptedException e)
		{
			Thread.currentThread().interrupt();
		}
	}
*/
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
