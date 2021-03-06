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
#include "guiemulator.h"

#include "gui.h"

GUIEmulator::GUIEmulator():
	Emulator()
{
}


GUIEmulator::~GUIEmulator()
{
}




void GUIEmulator::init()
{
	GUI* pgui = new GUI(this->screenImage,this->apple2,*this,this->display,this->keypresses,this->fhyper,this->buffered);

	powerOffComputer();

	this->display.setType(AnalogTV::MONITOR_COLOR);
	this->display.powerOn(false);

	pgui->show();
}

/*
	@Override
	protected StandardIn.EOFHandler getStdInEOF()
	{
		return new StandardIn.EOFHandler()
		{
			public void handleEOF()
			{
				// For a GUI, we don't do anything special when STDIN hits EOF
			}
		};
	}
*/
