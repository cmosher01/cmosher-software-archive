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

class TimingGenerator
{
public:
	TimingGenerator();
	static const int CRYSTAL_HZ;
	static const int CRYSTAL_CYCLES_PER_CPU_CYCLE;
	static const int EXTRA_CRYSTAL_CYCLES_PER_CPU_LONG_CYCLE;

	static const int HORIZ_CYCLES;
	static const int AVG_CPU_HZ;
	static const int CPU_HZ;
};

#endif
