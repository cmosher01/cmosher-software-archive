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
#ifndef RAMINITIALIZER_H
#define RAMINITIALIZER_H

class Memory;

class RAMInitializer
{
private:
	class done {};

	Memory& ram;
	unsigned short nextinit;

	void putBytesUntilFull(int bit, int pat);
	void ramPattern1(const int bit) throw (done);
	void ramPattern2(const int bit) throw (done);
	void putn(const int c, bool on, const int bit) throw (done);

public:
	RAMInitializer(Memory& mem);
	void init();
};

#endif
