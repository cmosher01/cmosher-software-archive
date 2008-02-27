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
#ifndef THROTTLE_H
#define THROTTLE_H

#include <ctime>

class Throttle
{
private:
	enum { CHECK_EVERY_CYCLE = 51024 };
	enum { EXPECTED_TICKS = CLOCKS_PER_SEC/20 };
	enum { NANOS_PER_CLOCK = 1000000000/CLOCKS_PER_SEC };

	clock_t ticksPrev;
	unsigned int times;
	bool suspend;
	float speedRatio;

public:
	Throttle();
	~Throttle();

	void tick();
	void throttleIfNecessary();
	void sleep(const long msDelta);
	void suspendIfNecessary();
	void toggleSuspend();
	float getSpeedRatio();
};

#endif
