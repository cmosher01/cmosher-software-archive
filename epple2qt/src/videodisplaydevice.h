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
#ifndef VIDEODISPLAYDEVICE_H
#define VIDEODISPLAYDEVICE_H

#include "displaytype.h"

class VideoDisplayDevice
{
protected:
	VideoDisplayDevice();
	virtual ~VideoDisplayDevice();

public:
	virtual void powerOn(bool b) = 0;
	virtual bool isOn() = 0;

	virtual void setType(DisplayType type) = 0;

	virtual void putSignal(signed char ire) = 0;
	virtual void putAsDisconnectedVideoIn() = 0;
	virtual void restartSignal() = 0;
};

#endif
