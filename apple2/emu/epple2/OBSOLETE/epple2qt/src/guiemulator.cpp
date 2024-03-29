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
#include "analogtv.h"

GUIEmulator::GUIEmulator():
	Emulator(),
	screen(screenImage,apple2)
{
}


GUIEmulator::~GUIEmulator()
{
}




void GUIEmulator::init()
{
//	ComputerControlPanel compControls(this);
//	MonitorControlPanel monitorControls(this->display);

//	GUI gui(this,this->screen,compControls,monitorControls,this->apple2.slts);
	GUI gui(this->screen);

/* TODO
	this->screenImage.addObserver(new Observer()
	{
	@SuppressWarnings({ "unused", "synthetic-access" })
		public void update(final Observable observableThatChagned, final Object typeOfChange)
		{
		GUIEmulator.this->screen.plot();
		}
	});
*/

	initKeyListeners();


//	this->screen.setFocusTraversalKeysEnabled(false);
//	this->screen.requestFocus();

	this->display.setType(AnalogTV::MONITOR_COLOR);//TV_NEW_COLOR);//TV_OLD_COLOR);
//TODO	powerOffComputer();
//	this->display.powerOn(false);
	powerOnComputer();
	this->display.powerOn(true);
}

void GUIEmulator::initKeyListeners()
{
	this->screen.setKeypressQueue(this->keypresses);
/*
	this->screen.removeKeyListeners();

	this->screen.addKeyListener(new KeyboardProducer(this->keypresses));
	this->screen.addKeyListener(new ClipboardProducer(this->keypresses));
	this->screen.addKeyListener(new HyperKeyHandler(this->hyper,this->buffered));
	this->screen.addKeyListener(new FnKeyHandler(this->apple2,this->screenImage,this->apple2.ram,this->throttle));
	this->screen.addKeyListener(new PaddleButtons(this->paddleButtonStates));
*/
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
