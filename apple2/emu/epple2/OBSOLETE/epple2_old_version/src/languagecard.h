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
#ifndef LANGUAGECARD_H
#define LANGUAGECARD_H

#include "card.h"
#include "memory.h"
#include <vector>

class LanguageCard : public Card
{
private:
	bool inhibit;
	std::vector<Memory*> ramBank;
	Memory ramTop;
	unsigned char bank;
	bool readEnable;
	bool writeEnable;
	unsigned char writeCount;

public:
	LanguageCard();
	~LanguageCard();

	virtual void reset() { /* does nothing */ }
	virtual bool inhibitMotherboardRom() { return this->inhibit; }
	virtual unsigned char io(const unsigned short address, const unsigned char data, const bool writing);
	virtual void ioBankRom(const unsigned short addr, unsigned char* const pb, const bool write);
};

#endif
