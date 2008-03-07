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
#ifndef VIDEOSTATICGENERATOR_H
#define VIDEOSTATICGENERATOR_H

class AnalogTV;
#include "timable.h"
#include "applentsc.h"

class VideoStaticGenerator : public Timable
{
private:
	AnalogTV& display;
	signed char sig[AppleNTSC::SIGNAL_LEN];
	signed char* isig;
	signed char* isiglim;
	unsigned int hpos;

public:
	VideoStaticGenerator(AnalogTV& display);
	~VideoStaticGenerator();

	virtual void tick();
	void powerOn();
};

#endif
