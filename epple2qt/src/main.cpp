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

#include "guiemulator.h"
#include "videoaddressing.h" //TODO
#include "timinggenerator.h" //TODO

int main( int argc, char ** argv )
{
	std::cout << "main" << std::endl;


#define DUMP(x) std::cout << #x << ":    " << x << std::endl;

DUMP(TimingGenerator::CRYSTAL_HZ)
DUMP(TimingGenerator::CRYSTAL_CYCLES_PER_CPU_CYCLE)
DUMP(TimingGenerator::EXTRA_CRYSTAL_CYCLES_PER_CPU_LONG_CYCLE)
DUMP(TimingGenerator::HORIZ_CYCLES)
DUMP(TimingGenerator::AVG_CPU_HZ)
DUMP(TimingGenerator::CPU_HZ)

DUMP(VideoAddressing::NTSC_LINES_PER_FRAME)
DUMP(VideoAddressing::NTSC_LINES_PER_FIELD)
DUMP(VideoAddressing::NTSC_FIELDS_PER_SECOND)
DUMP(VideoAddressing::NTSC_COLOR_FIELD_EVERY)

DUMP(VideoAddressing::APPLE_BYTES)
DUMP(VideoAddressing::LINES)
DUMP(VideoAddressing::BYTES_PER_ROW)

DUMP(VideoAddressing::BYTES_PER_FIELD)

DUMP(VideoAddressing::VISIBLE_BITS_PER_BYTE)
DUMP(VideoAddressing::VISIBLE_LINES_PER_CHARACTER)

DUMP(VideoAddressing::VISIBLE_BYTES_PER_ROW)
DUMP(VideoAddressing::VISIBLE_ROWS_PER_FIELD)

DUMP(VideoAddressing::BLANKED_BYTES_PER_ROW)
DUMP(VideoAddressing::VISIBLE_BYTES_PER_FIELD)
DUMP(VideoAddressing::SCANNABLE_ROWS)
DUMP(VideoAddressing::SCANNABLE_BYTES)
DUMP(VideoAddressing::RESET_ROWS)
DUMP(VideoAddressing::RESET_BYTES)

	QApplication app(argc, argv);
	Emulator* emu = new GUIEmulator();
	emu->init();
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
