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
#include "addressbus.h"
#include "memory.h"
#include "Keyboard.h"
#include "videomode.h"

AddressBus::AddressBus(Memory& ram, Memory& rom, Keyboard& kbd, VideoMode& vid):
	ram(ram), rom(rom), kbd(kbd), vid(vid)
{
}


AddressBus::~AddressBus()
{
}






const int AddressBus::MOTHERBOARD_RAM_BAS = 0x00000;
const int AddressBus::MOTHERBOARD_RAM_LIM = 0x0C000;
const int AddressBus::MOTHERBOARD_RAM_SIZ = MOTHERBOARD_RAM_LIM-MOTHERBOARD_RAM_BAS;

const int AddressBus::MOTHERBOARD_ROM_BAS = 0x0D000;
const int AddressBus::MOTHERBOARD_ROM_LIM = 0x10000;
const int AddressBus::MOTHERBOARD_ROM_SIZ = MOTHERBOARD_ROM_LIM-MOTHERBOARD_ROM_BAS;



unsigned char AddressBus::read(const unsigned short address)
{
	if ((address >> 14 == 3)) // >= $C000
	{
		if ((address >> 12) == 0xC)
		{
			// 11007sssxxxxxxxx
			const bool seventh = address & 0x0800;
			const int slot = (address >> 8) & 7;
			if (seventh)
			{
				//this->data = this->slots.readSeventhRom(address & 0x07FF);
			}
			else if (slot == 0)
			{
				this->data = readSwitch(address & 0x00FF);
			}
			else
			{
				//this->data = this->slots.readRom(slot,address & 0x00FF);
			}
		}
		else
		{
			//this->data = this->slots.ioBankRom(address - 0xD000,this->data,false);
			//if (!this->slots.inhibitMotherboardRom())
			{
				this->data = this->rom.read(address - 0xD000);
			}
		}
	}
	else // < $C000
	{
		this->data = this->ram.read(address);
	}

	return this->data;
}

void AddressBus::write(const unsigned short address, const unsigned char d)
{
	this->data = d;

	if ((address >> 14 == 3)) // >= $C000
	{
		if ((address >> 12) == 0xC)
		{
			// 11007sssxxxxxxxx
			const bool seventh = address & 0x0800;
			const int slot = (address >> 8) & 7;
			if (!seventh && slot == 0)
			{
				writeSwitch(address & 0x00FF);
			}
		}
		else
		{
			//this->slots.ioBankRom(address - 0xD000,this->data,true);
		}
	}
	else // < $C000
	{
		this->ram.write(address,this->data);
	}
}








unsigned char AddressBus::readSwitch(unsigned short address)
{
	if (address < 0x80)
	{
		const int islot = (address >> 4) & 0xF;
		const int iswch = (address & 0xF);
		if (islot == 0x0)
		{
			this->data = this->kbd.get();
		}
		else if (islot == 0x1)
		{
			this->kbd.clear();
		}
		else if (islot == 0x2)
		{
			// ignore cassette output
		}
		else if (islot == 0x3)
		{
			//this->speaker.click();
		}
		else if (islot == 0x4)
		{
			// ignore utility strobe
		}
		else if (islot == 0x5)
		{
			if (iswch < 0x8)
			{
				this->data = this->vid.io(address,this->data);
			}
			else
			{
				// ignore annunciator outputs
			}
		}
		else if (islot == 0x6)
		{
			int sw2 = iswch & 0x7;
			if (sw2 == 0)
			{
				// ignore cassette input
			}
			else if (sw2 < 4)
			{
				//setD7(this->paddleButtons.isDown(sw2));
			}
			else
			{
				sw2 &= 3;
				//setD7(!this->paddles.isTimedOut(sw2));
			}
		}
		else if (islot == 0x7)
		{
			//this->paddles.startTimers();
			setD7(true);
		}
	}
	else
	{
		// slot I/O switches
		address &= 0x7F;
		const int islot = (address >> 4) & 0xF;
		const int iswch = (address & 0xF);
		//this->data = this->slots.io(islot,iswch,this->data,false);
	}

	return this->data;
}

void AddressBus::setD7(const bool set)
{
	if (set)
	{
		this->data |= 0x80;
	}
	else
	{
		this->data &= 0x7F;
	}
}

void AddressBus::writeSwitch(unsigned short address)
{
	if (address < 0x80)
	{
		const int islot = (address >> 4) & 0xF;
		const int iswch = (address & 0xF);

		if (islot == 0x1)
		{
			this->kbd.clear();
		}
		else if (islot == 0x5)
		{
			if (iswch < 0x8)
				this->vid.io(address,this->data);
		}
		// ignore all other switch writes
	}
	else
	{
		// slot I/O switches
		address &= 0x7F;
		const int islot = (address >> 4) & 0xF;
		const int iswch = (address & 0xF);
		//this->slots.io(islot,iswch,this->data,true);
	}
}