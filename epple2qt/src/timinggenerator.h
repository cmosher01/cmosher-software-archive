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
#ifndef TIMINGGENERATOR_H
#define TIMINGGENERATOR_H

class Timable;
class Throttle;

class TimingGenerator
{
private:
	bool shut;
	int thread; // TODO thread

	Timable& timable;
	Throttle& throttle;

public:
	TimingGenerator(Timable& timable, Throttle& throttle);

	static const unsigned int CRYSTAL_HZ;
	static const unsigned int CRYSTAL_CYCLES_PER_CPU_CYCLE;
	static const unsigned int EXTRA_CRYSTAL_CYCLES_PER_CPU_LONG_CYCLE;

	static const unsigned int HORIZ_CYCLES;
	static const unsigned int AVG_CPU_HZ;
	static const unsigned int CPU_HZ;

	void run();
	void threadProcedure();
	bool isShuttingDown();
	bool isRunning();
	void shutdown();
};

#endif
