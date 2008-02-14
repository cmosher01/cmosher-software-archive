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
#ifndef ADDRESSINGMODE_H
#define ADDRESSINGMODE_H


class AddressingMode
{
	enum Mode
	{
		SINGLE,
		INTERNAL_IMMEDIATE,
		INTERNAL_ZERO_PAGE,
		INTERNAL_ABSOLUTE,
		INTERNAL_INDIRECT_X,
		INTERNAL_ABSOLUTE_XY,
		INTERNAL_ZERO_PAGE_XY,
		INTERNAL_INDIRECT_Y,
		STORE_ZERO_PAGE,
		STORE_ABSOLUTE,
		STORE_INDIRECT_X,
		STORE_ABSOLUTE_XY,
		STORE_ZERO_PAGE_XY,
		STORE_INDIRECT_Y,
		RMW_ZERO_PAGE,
		RMW_ABSOLUTE,
		RMW_ZERO_PAGE_X,
		RMW_ABSOLUTE_X,
		MISC_PUSH,
		MISC_PULL,
		MISC_JSR,
		MISC_BREAK,
		MISC_RTI,
		JMP_ABSOLUTE,
		JMP_INDIRECT,
		RTS,
		BRANCH,
		IRQ,
		RESET,
		NMI,
	};

	AddressingMode() {}

public:

	static Mode mode[0x100];
};

#endif
