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
#ifndef ADDRESSBUS_H
#define ADDRESSBUS_H

class Memory;
class Keyboard;
class VideoMode;
class Slots;

class AddressBus
{
private:
	Memory& ram;
	Memory& rom;
	Keyboard& kbd;
	VideoMode& vid;
	//PaddlesInterface paddles;
	//SpeakerClicker speaker;
	Slots& slts;
	//PaddleBtnInterface paddleButtons;

	unsigned char data; // this emulates the (floating) data bus

public:
	AddressBus(Memory& ram, Memory& rom, Keyboard& kbd, VideoMode& vid, Slots& slts);
	~AddressBus();

	unsigned char read(const unsigned short address);
	void write(const unsigned short address, const unsigned char d);
	unsigned char readSwitch(unsigned short address);
	void setD7(const bool set);
	void writeSwitch(unsigned short address);

	static const int MOTHERBOARD_RAM_BAS;
	static const int MOTHERBOARD_RAM_LIM;
	static const int MOTHERBOARD_RAM_SIZ;

	static const int MOTHERBOARD_ROM_BAS;
	static const int MOTHERBOARD_ROM_LIM;
	static const int MOTHERBOARD_ROM_SIZ;
};

#endif
