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
#ifndef TIMINGGENERATOR_H
#define TIMINGGENERATOR_H

class TimingGenerator
{
public:
	enum { CRYSTAL_HZ = 14318182 };
	enum { CRYSTAL_CYCLES_PER_CPU_CYCLE = 14 };
	enum { EXTRA_CRYSTAL_CYCLES_PER_CPU_LONG_CYCLE = 2 };
	enum { HORIZ_CYCLES = 65 };
	enum { AVG_CPU_HZ = 1020484 };
	enum { CPU_HZ = 1022728 };
};

#endif
