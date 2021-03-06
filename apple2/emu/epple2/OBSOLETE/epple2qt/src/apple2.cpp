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
#include "apple2.h"
#include "slots.h"
#include "videomode.h"
#include "keyboard.h"
#include "addressbus.h"
#include "memory.h"
#include "picturegenerator.h"
#include "textcharacters.h"
#include "video.h"
#include "cpu.h"
#include "paddles.h"
#include "paddlebuttonstates.h"
#include "speakerclicker.h"
#include "analogtv.h"
#include "powerupreset.h"
#include "diskcontroller.h"

#include <iostream>
#include <fstream>

Apple2::Apple2(KeypressQueue& keypresses, PaddleButtonStates& paddleButtonStates, AnalogTV& tv):
	kbd(keypresses),
	ram(AddressBus::MOTHERBOARD_RAM_SIZ),
	rom(AddressBus::MOTHERBOARD_ROM_SIZ),
	addressBus(ram,rom,kbd,videoMode,paddles,paddleButtonStates,speaker,slts),
	picgen(tv,videoMode),
	video(videoMode,addressBus,picgen,textRows),
	cpu(addressBus),
	powerUpReset(*this)
{
	// TODO remove loading of ROM (only for testing)
	{
//		std::ifstream rom_in("..\\apple2src\\firmware\\rom\\apple2p_f800.rom",std::ios::binary);
		std::ifstream rom_in("../apple2src/firmware/rom/apple2p_f800.rom",std::ios::binary);
		rom.load(0x2800,rom_in);
		if (!rom_in.good())
		{
			std::cout << "ERROR reading ROM file apple2p_f800.rom" << std::endl;
		}
		rom_in.close();
	}
	{
//		std::ifstream rom_in("..\\apple2src\\firmware\\rom\\apple2p_d000.rom",std::ios::binary);
		std::ifstream rom_in("../apple2src/firmware/rom/apple2p_d000.rom",std::ios::binary);
		rom.load(0x0000,rom_in);
		if (!rom_in.good())
		{
			std::cout << "ERROR reading ROM file apple2p_d000.rom" << std::endl;
		}
		rom_in.close();
	}
	{
		DiskController* disk = new DiskController();
		this->slts.set(6,disk);
//		std::ifstream rom_in("..\\apple2src\\firmware\\rom\\disk2_16sect_c600_patched_nodelay.rom",std::ios::binary);
		std::ifstream rom_in("../apple2src/firmware/rom/disk2_16sect_c600_patched_nodelay.rom",std::ios::binary);
		if (!rom_in.good())
		{
			std::cout << "ERROR reading ROM file disk2_16sect_c600_patched_nodelay.rom" << std::endl;
		}
		disk->loadRom(0,rom_in);
//		disk->loadDisk(0,"c:\\temp\\dos33.nib");
		disk->loadDisk(0,"../splitvid.nib");
		rom_in.close();
	}
}


Apple2::~Apple2()
{
}


void Apple2::tick()
{
	this->cpu.tick();
	this->video.tick();
	this->paddles.tick();
	this->speaker.tick();
	this->powerUpReset.tick();
}

void Apple2::powerOn()
{
	this->ram.powerOn();
	this->cpu.powerOn();
	this->videoMode.powerOn();
	this->video.powerOn();
	this->picgen.powerOn();
	this->powerUpReset.powerOn();
	// TODO clear up all other things for Apple ][ power-on
}

void Apple2::powerOff()
{
	this->ram.powerOff();
}

void Apple2::reset()
{
	this->cpu.reset();
	this->slts.reset();
}
