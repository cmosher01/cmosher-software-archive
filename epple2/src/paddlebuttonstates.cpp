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
#include "paddlebuttonstates.h"

const int PaddleButtonStates::PADDLE_COUNT(4);

PaddleButtonStates::PaddleButtonStates():
	button(PADDLE_COUNT)
{
}

PaddleButtonStates::~PaddleButtonStates()
{
}

void PaddleButtonStates::setButton(const int btn, const bool pressed)
{
	this->button[btn] = pressed;
}

bool PaddleButtonStates::isDown(const int paddle)
{
	if (paddle < 0 || PADDLE_COUNT <= paddle)
	{
		return false;
	}
	return this->button[paddle];
}
