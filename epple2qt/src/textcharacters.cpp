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
#include "textcharacters.h"

#include <fstream>
#include <algorithm>

static unsigned char translateRow(unsigned char b)
{
	// translateRow(abcdefgh) == 0hgfedcb
	unsigned char r(0);
	for (int i(0); i < 7; ++i)
	{
		r <<= 1;
		r |= b & 1;
		b >>= 1;
	}
	return r;
}

TextCharacters::TextCharacters():
	rows(0x200)
{
	std::ifstream is_rom("GI2513.ROM",std::ifstream::binary);
	is_rom.read((char*)&this->rows.front(),this->rows.size());
	is_rom.close();
	std::transform(this->rows.begin(),this->rows.end(),this->rows.begin(),translateRow);
}

TextCharacters::~TextCharacters()
{
}

unsigned char TextCharacters::get(unsigned char iRow)
{
// TODO
//	if (iRow < 0 || SIZE <= iRow)
//	{
//		return 0;
//	}

	return this->rows[iRow];
}
