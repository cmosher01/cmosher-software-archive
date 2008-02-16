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


#include "playqmake.h"

#include "memory.h"
#include "Keyboard.h"
#include "videomode.h"
#include "slots.h"
#include "addressbus.h"
#include "cpu.h"
#include "textcharacters.h"

#include <iostream>
#include <fstream>

#include <QApplication>

int main( int argc, char ** argv )
{
	std::cout << "main" << std::endl;

	QApplication app(argc, argv);
	playqmake widget;
	widget.show();
	return app.exec();

/*
	TextCharacters txt;
	unsigned char xxx = txt.get(0x41);

	Memory ram(0xC000);
	Memory rom(0x3000);
	KeypressQueue kq;
	Keyboard kbd(kq);
	VideoMode vid;
	Slots slts;
	AddressBus bus(ram,rom,kbd,vid,slts);
	CPU cpu(bus);

	std::ifstream rom_in("/home/chris/apple2src/firmware/rom/apple2_f800.rom");
	rom.load(0x2800,rom_in);

	ram.powerOn();
	vid.powerOn();
	cpu.powerOn();
	cpu.reset();
	for (int i = 0; i < 1000; ++i)
	{
		cpu.tick();
	}

	return 0;
*/
}
