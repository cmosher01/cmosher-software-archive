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
#include "powerupreset.h"
#include "apple2.h"
#include "timinggenerator.h"

PowerUpReset::PowerUpReset(Apple2& apple):
	apple(apple)
{
}


PowerUpReset::~PowerUpReset()
{
}


void PowerUpReset::tick()
{
	if (this->pendingTicks > 0)
	{
		--this->pendingTicks;
		if (this->pendingTicks == 0)
		{
			this->apple.reset();
		}
	}
}

void PowerUpReset::powerOn()
{

	// TODO if rev > 0
	this->pendingTicks = (int)(TimingGenerator::AVG_CPU_HZ*.3); // U.A.II, p. 7-15
}
