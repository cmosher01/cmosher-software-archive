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
#ifndef ANALOGTV_H
#define ANALOGTV_H

#include "analogtv.h"
#include "applentsc.h"
#include "a2colorsobserved.h"

#include <vector>


class ScreenImage;
class IQ;
class CB;

class AnalogTV
{
public:
	enum DisplayType
	{
		MONITOR_COLOR,
		MONITOR_WHITE,
		MONITOR_GREEN,
		MONITOR_ORANGE,
		TV_OLD_COLOR,
		TV_OLD_BW,
		TV_NEW_COLOR,
		TV_NEW_BW,
	};

private:
	ScreenImage& image;

	bool on;
//	std::vector<signed char> signal;
//	std::vector<signed char>::iterator isig;
//	std::vector<signed char>::iterator siglim;
	bool noise;
	DisplayType type;
	unsigned long /* TODO long*/ int rrr;

	static int* rcb;

	A2ColorsObserved colors;
	std::vector<unsigned int> hirescolor;
	std::vector<unsigned int> loreslightcolor;
	std::vector<unsigned int> loresdarkcolor;

	static const int IQINTOFF;
	static const double IQ_OFFSET_DEGREES;
	static const double IQ_OFFSET_RADIANS;
	static const double TINT_I;
	static const double TINT_Q;
	static const double COLOR_THRESH;
	static const IQ& BLACK_AND_WHITE;
	enum { D_IP = AppleNTSC::H-2-350 };

	void drawMonitorColor();
	void drawMonitorWhite();
	void drawMonitorGreen();
	void drawMonitorOrange();
	void drawMonitorMonochrome(const unsigned int color);
	void drawTVOld();
	void drawTVNew();
	void drawBlank();
	void ntsc_to_rgb_monitor(const int isignal, const int siglen, unsigned int rgb[]);
	void ntsc_to_rgb_newtv(const int isignal, const int siglen, unsigned int rgb[]);
	CB get_cb(int lineno);
	IQ get_iq_factor(const CB& cb);
	void ntsc_to_yiq(const int isignal, const int siglen, const IQ& iq_factor, int yiq[]);
	static int yiq2rgb(const int yiq);
	static int color2bw(const int rgb);
	static int rgb2y(const int rgb); // ;y in range 0-255
	static int calc_color(const double color);
	static int clamp(int min, int x, int lim);

public:
	void drawCurrent();
	signed char* signal;

	enum { CB_EXTRA = 32 };

	AnalogTV(ScreenImage& image);
	~AnalogTV();
	
	bool isOn() const
	{
		return this->on;
	}

	void powerOn(bool b);
	void putAsDisconnectedVideoIn();
	void restartSignal();
	void setType(DisplayType type);
/*
	void putSignal(const signed char ire)
	{
		if (this->isig == this->siglim)
		{
			if (isOn())
			{
				this->drawCurrent();
			}
			else
			{
				this->drawBlank();
			}
			this->isig = signal.begin();
		}
		*(this->isig) = ire;
		++this->isig;
	}
*/
};

#endif
