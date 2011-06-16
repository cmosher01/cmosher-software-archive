/***************************************************************************
 *   Copyright (C) 2008 by Chris Mosher,,,   *
 *   chris@mosher.mine.nu   *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/
#include "card.h"

Card::Card():
	rom(0x0100),
	seventhRom(0x0800)
{
}


Card::~Card()
{
}


// override
void Card::reset()
{
}



// override
unsigned char Card::io(const unsigned short /*address*/, const unsigned char data, const bool /*writing*/)
{
	return data;
}



unsigned char Card::readRom(const unsigned short address)
{
	this->activeSeventhRom = true;
	return this->rom.read(address);
}

void Card::readSeventhRom(const unsigned short address, unsigned char* const pb)
{
	if (address == 0x7FF)
	{
		this->activeSeventhRom = false;
	}
	else if (this->activeSeventhRom)
	{
		*pb = this->seventhRom.read(address);
	}
}

void Card::loadRom(const unsigned short base, std::istream& in)
{
	this->rom.load(base,in);
}

void Card::loadSeventhRom(const unsigned short base, std::istream& in)
{
	this->seventhRom.load(base,in);
}



bool Card::inhibitMotherboardRom()
{
	return false;
}



void Card::ioBankRom(const unsigned short /*addr*/, unsigned char* const /*pb*/, const bool /*write*/)
{
}



void Card::loadBankRom(const unsigned short /*base*/, std::istream& /*in*/)
{
//	throw InvalidMemoryLoad("This card has no $D000 ROM");
}



//JPanel getPanel(const GUI gui)
//{
//	return new DefaultCardPanel(getTypeName());
//}



//String getTypeName()
//{
//	return this.getClass().getSimpleName();
//}



//DropTargetListener getDropListener()
//{
//	return null;
//}

bool Card::isDirty()
{
	return false;
}
