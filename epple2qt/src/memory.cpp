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
#include "memory.h"
#include <vector>
#include <algorithm>
#include <istream>
#include "RAMInitializer.h"

const int Memory::CLEAR_VALUE(0);

Memory::Memory(const size_t n):
        bytes(n)
{
}

size_t Memory::size() const
{
        return this->bytes.size();
}

unsigned char Memory::read(const unsigned short address) const
{
        return this->bytes[address];
}

void Memory::write(const unsigned short address, const unsigned char data)
{
        this->bytes[address] = data;
}

void Memory::clear()
{
        std::fill(this->bytes.begin(),this->bytes.end(),CLEAR_VALUE);
}

void Memory::powerOn()
{
      RAMInitializer initRam(*this);
      initRam.init();
}

void Memory::powerOff()
{
        clear();
}

void Memory::load(std::istream& in)
{
        in.read((char*)this->bytes.front(),this->bytes.size());
}
