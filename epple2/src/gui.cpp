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
#include "gui.h"

#include "analogtv.h"
#include "monitorcontrolpanel.h"
#include "screenimage.h"
#include "apple2.h"
#include "emulator.h"
#include "contentpane.h"

GUI::GUI(ScreenImage& screenImage, Apple2& apple2, Emulator& emu, AnalogTV& display, KeypressQueue& keys)
{
	ContentPane* pcontent = new ContentPane(screenImage,apple2,display,emu,keys,this);
	setCentralWidget(pcontent);
}


GUI::~GUI()
{
}
#include <iostream>
void GUI::closeEvent(QCloseEvent* event)
{
	std::cout << "GUI::closeEvent" << std::endl;
}
