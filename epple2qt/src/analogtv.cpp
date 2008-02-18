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
#include "analogtv.h"
#include "screenimage.h"
#include "applentsc.h"
#include "a2colorindex.h"
#include "lowpass_3_58_mhz.h"
#include "lowpass_1_5_mhz.h"

#include <cmath>
#include <ctime>
#include <cstdlib>

AnalogTV::AnalogTV(ScreenImage& image):
	VideoDisplayDevice(),
	image(image),
	on(false),
	signal(AppleNTSC::SIGNAL_LEN),
	isig(0),
	noise(false),
	rrr(1)
{
	srand(time(0));

	hirescolor.push_back(HIRES_GREEN);
	hirescolor.push_back(HIRES_ORANGE);
	hirescolor.push_back(HIRES_VIOLET);
	hirescolor.push_back(HIRES_BLUE);

	loreslightcolor.push_back(LIGHT_BROWN);
	loreslightcolor.push_back(LIGHT_MAGENTA);
	loreslightcolor.push_back(LIGHT_BLUE);
	loreslightcolor.push_back(LIGHT_BLUE_GREEN);

	loresdarkcolor.push_back(DARK_BLUE_GREEN);
	loresdarkcolor.push_back(DARK_BROWN);
	loresdarkcolor.push_back(DARK_MAGENTA);
	loresdarkcolor.push_back(DARK_BLUE);
}


AnalogTV::~AnalogTV()
{
}


bool AnalogTV::isOn()
{
	return this->on;
}

void AnalogTV::powerOn(bool b)
{
	this->on = b;
	this->image.notifyObservers();
}

void AnalogTV::putAsDisconnectedVideoIn()
{
	this->noise = true;
//	this->rrr *= 16807;
//	this->rrr %= 0x7FFFFFFF;
//	++this->rrr;
//	const signed char v = this->rrr>>17;
	putSignal((rand()>>7&0x7F)-27);
	this->noise = false;
}

void AnalogTV::putSignal(const signed char ire)
{
//		if (this->isig >= AppleNTSC::SIGNAL_LEN)
//		{
//			throw new IllegalStateException("At end of screen; must re-synch before writing any more signal");
//		}
	this->signal[this->isig++] = ire;
	if (this->isig == AppleNTSC::SIGNAL_LEN)
	{
		if (isOn())
		{
			this->drawCurrent();
		}
		else
		{
			this->drawBlank();
		}
		this->isig = 0;
	}
}

void AnalogTV::restartSignal()
{
	this->isig = 0;
	this->image.notifyObservers();
}

void AnalogTV::setType(DisplayType type)
{
	this->type = type;
}











// void dump()
// {
// 	const int[] yiq = new int[AppleNTSC::H];
// 	for (int row = 0; row < 192; ++row)
// 	{
// 		const CB cb = get_cb(row);
// 		const IQ iq_factor = get_iq_factor(cb);
// 		ntsc_to_yiq(row*AppleNTSC::H+350,AppleNTSC::H-2-350,iq_factor,yiq);
// 		for (int col = 350; col < AppleNTSC::H-2; ++col)
// 		{
// 			const int sig = this->signal[row*AppleNTSC::H+col];
// 			System.out.printf(" %+04d",sig);
// 			const int yiqv = yiq[col-350];
// 			int y = (yiqv&0xFF)-IQINTOFF;
// 			int i = ((yiqv>>8)&0xFF)-IQINTOFF;
// 			int q = ((yiqv>>16)&0xFF)-IQINTOFF;
// 			System.out.printf("(%+04d,%+04d,%+04d)",y,i,q);
// 
// 			const int rgb = yiq2rgb(yiqv);
// 			const int r = (rgb >> 16) & 0xff;
// 			const int g = (rgb >> 8) & 0xff;
// 			const int b = (rgb ) & 0xff;
// 			System.out.printf("[%06X:%03d,%03d,%03d]",rgb,r,g,b);
// 		}
// 		System.out.println();
// 	}
// 
// }








class IQ
{
public:
	double iq[4];
	IQ()
	{
		for (int i = 0; i < 4; ++i)
			iq[i] = 0;
	}
	IQ(double aiq[])
	{
		for (int i = 0; i < 4; ++i)
			iq[i] = aiq[i];
	}
	double get(int i) const { return this->iq[i]; }
};

const IQ& AnalogTV::BLACK_AND_WHITE = IQ();

class CB
{
public:
	std::vector<int> cb;

	CB(const int acb[]):
		cb(AppleNTSC::CB_END-AppleNTSC::CB_START-AnalogTV::CB_EXTRA)
	{
		for (std::vector<int>::size_type i(0); i < this->cb.size(); ++i)
		{
			this->cb[i] = acb[i];
		}
	}
	int get(const int i) const { return this->cb[i]; }
	int length() const { return this->cb.size(); }
	void getPhase(double phase[]) const
	{
		for (int i = 0; i < length(); ++i)
		{
			phase[i & 3] += this->cb[i];
		}
		double tot = 0;
		for (int i = 0; i < 4; ++i)
		{
			tot += phase[i] * phase[i];
		}
		const double tsrt = sqrt(tot);
		for (int i = 0; i < 4; i++)
		{
			phase[i] /= tsrt;
		}
	}
	bool isColor() const
	{
		int tot = 0;
		for (int i = 0; i < length(); ++i)
		{
			const int icb = this->cb[i];
			if (icb < 0)
				tot += -icb;
			else
				tot += icb;
		}
		return 220 < tot && tot < 260;
	}
};







void AnalogTV::drawCurrent()
{
	switch (this->type)
	{
		case MONITOR_COLOR: drawMonitorColor(); break;
		case MONITOR_WHITE: drawMonitorWhite(); break;
		case MONITOR_GREEN: drawMonitorGreen(); break;
		case MONITOR_ORANGE: drawMonitorOrange(); break;
		case TV_OLD_COLOR: drawTVOld(); break;
		case TV_OLD_BW: drawTVOld(); break;
		case TV_NEW_COLOR: drawTVNew(); break;
		case TV_NEW_BW: drawTVNew(); break;
	}
}



const int AnalogTV::D_IP(AppleNTSC::H-2-350);

void AnalogTV::drawMonitorColor()
{
	unsigned int *rgb = new unsigned int[AppleNTSC::H];
	int ip = 0;
	for (int row = 0; row < 192; ++row)
	{
		const CB cb = get_cb(row);
		const bool removeColor = (this->type == TV_NEW_BW || !cb.isColor());
		ntsc_to_rgb_monitor(row*AppleNTSC::H+350,D_IP,rgb);
		for (int col = 350; col < AppleNTSC::H-2; ++col)
		{
			int rgbv = rgb[col-350];
			if (removeColor && rgbv != 0)
			{
				rgbv = 0xFFFFFF;
			}
			this->image.setElem(ip,rgbv);
			this->image.setElem(ip+D_IP,rgbv); // display same pixel on next row
			++ip;
		}
		ip += D_IP;
	}
	this->image.notifyObservers();
	delete [] rgb;
}

void AnalogTV::drawMonitorWhite()
{
	drawMonitorMonochrome(colors.c()[WHITE]);
}

void AnalogTV::drawMonitorGreen()
{
	drawMonitorMonochrome(colors.c()[HIRES_GREEN]);
}

void AnalogTV::drawMonitorOrange()
{
	drawMonitorMonochrome(colors.c()[HIRES_ORANGE]);
}

void AnalogTV::drawMonitorMonochrome(const unsigned int color)
{
	int ip = 0;
	for (int row = 0; row < 192; ++row)
	{
		for (int col = 350; col < AppleNTSC::H-2; ++col)
		{
			const int is = row*AppleNTSC::H+col;
			const unsigned int rgb = this->signal[is] > 50 ? color : colors.c()[BLACK];
			this->image.setElem(ip,rgb);
			this->image.setElem(ip+D_IP,rgb);
			++ip;
		}
		ip += D_IP;
	}
	this->image.notifyObservers();
}

void AnalogTV::drawTVOld()
{
	int *yiq = new int[AppleNTSC::H];
	int ip = 0;
	for (int row = 0; row < 192; ++row)
	{
		IQ iq_factor;
		if (this->type == TV_OLD_COLOR)
		{
			const CB cb = get_cb(row);
			iq_factor = get_iq_factor(cb);
		}
		else
		{
			iq_factor = BLACK_AND_WHITE;
		}
		ntsc_to_yiq(row*AppleNTSC::H+350,AppleNTSC::H-2-350,iq_factor,yiq);
		for (int col = 350; col < AppleNTSC::H-2; ++col)
		{
			const int rgb = yiq2rgb(yiq[col-350]);
			this->image.setElem(ip,rgb);
			this->image.setElem(ip+D_IP,rgb);
			++ip;
		}
		ip += D_IP;
	}
	this->image.notifyObservers();
	delete [] yiq;
}

void AnalogTV::drawTVNew()
{
	unsigned int *rgb = new unsigned int[AppleNTSC::H];
	int ip = 0;
	for (int row = 0; row < 192; ++row)
	{
		const CB cb = get_cb(row);
		const bool removeColor = (this->type == TV_NEW_BW || !cb.isColor());
		ntsc_to_rgb_newtv(row*AppleNTSC::H+350,AppleNTSC::H-2-350,rgb);
		for (int col = 350; col < AppleNTSC::H-2; ++col)
		{
			int rgbv = rgb[col-350];
			if (removeColor)
			{
				rgbv = color2bw(rgbv);
			}
			this->image.setElem(ip,rgbv);
			this->image.setElem(ip+D_IP,rgbv);
			++ip;
		}
		ip += D_IP;
	}
	this->image.notifyObservers();
	delete [] rgb;
}

void AnalogTV::drawBlank()
{
	this->image.blank();
	this->image.notifyObservers();
}





#include <iostream>//TODO
void AnalogTV::ntsc_to_rgb_monitor(const int isignal, const int siglen, unsigned int rgb[])
{
	int s0, s1, se;
	s0 = s1 = isignal;
	se = isignal+siglen;
// TODO
//	for (int sss = s1; sss < se; ++sss)
//	{
//		std::cout << (signed int)this->signal[sss] << " ";
//	}
//		std::cout << std::endl;
	while (s1 < se)
	{
		// no signal (black)
		while (this->signal[s0] < 50 && s0<se) { rgb[s0-isignal] = 0; ++s0; }

		// signal (white, grey, or color)
		s1 = s0;
		while (this->signal[s1] > 50 && s1<se) { ++s1; }
		const int slen = s1-s0;
		unsigned int c = 0;
		if (slen >= 4)
		{
			c = colors.c()[WHITE];
		}
		else if (slen == 1)
		{
			if (this->signal[s0-2] > 50 && this->signal[s0+2] > 50)
				c = colors.c()[WHITE];
			else
				c = loresdarkcolor[s0 % 4];
		}
		else if (slen == 2)
		{
			c = hirescolor[s0 % 4];
		}
		else if (slen == 3)
		{
			c = loreslightcolor[s0 % 4];
		}
		else
		{
			++s1;
		}

		for (int i = s0; i < s1; ++i)
			rgb[i-isignal] = c;
		s0 = s1;
	}
}

void AnalogTV::ntsc_to_rgb_newtv(const int isignal, const int siglen, unsigned int rgb[])
{
	int sp, s0, s1, se;
	sp = s0 = s1 = isignal;
	se = isignal+siglen;
	unsigned int c = 0;
	while (s1 < se)
	{
		// no signal; black...
		sp = s0;
		while (this->signal[s0] < 50 && s0<se) { rgb[s0-isignal] = 0; ++s0; }
		// unless it's too short, then color it (but not white)
		if (s0-sp < 4 && c != colors.c()[WHITE])
		{
			for (int i = sp; i < s0; ++i)
				rgb[i-isignal] = c;
		}

		// signal (white, grey, or color)
		s1 = s0;
		while (this->signal[s1] > 50 && s1<se) { ++s1; }
		const int slen = s1-s0;
		if (slen >= 4)
		{
			c = 0xFFFFFF;
		}
		else if (slen == 1)
		{
			if (this->signal[s0-2] > 50 && this->signal[s0+2] > 50)
				c = 0x808080;
			else
				c = loresdarkcolor[s0 % 4];
		}
		else if (slen == 2)
		{
			c = hirescolor[s0 % 4];
		}
		else if (slen == 3)
		{
			c = loreslightcolor[s0 % 4];
		}
		else
		{
			++s1;
		}

		for (int i = s0; i < s1; ++i)
			rgb[i-isignal] = c;
		s0 = s1;
	}
}






int *AnalogTV::rcb = new int[AppleNTSC::CB_END-AppleNTSC::CB_START-CB_EXTRA];
CB AnalogTV::get_cb(int lineno)
{
	const int isp = lineno * AppleNTSC::H;
	for (int i = AppleNTSC::CB_START + CB_EXTRA/2; i < AppleNTSC::CB_END - CB_EXTRA/2; ++i)
	{
		this->rcb[i-(AppleNTSC::CB_START + CB_EXTRA/2)] = this->signal[isp + i];
	}
	return CB(this->rcb);
}


// TODO static const Map<CB,IQ> cacheCB = new HashMap<CB,IQ>(2,1);

const double AnalogTV::IQ_OFFSET_DEGREES = 33;
const double AnalogTV::IQ_OFFSET_RADIANS = AnalogTV::IQ_OFFSET_DEGREES * 3.1415927 / 180;
const double AnalogTV::TINT_I = -cos(AnalogTV::IQ_OFFSET_RADIANS);
const double AnalogTV::TINT_Q = +sin(AnalogTV::IQ_OFFSET_RADIANS);

const double AnalogTV::COLOR_THRESH(1.4);

IQ AnalogTV::get_iq_factor(const CB& cb)
{
//	if (cacheCB.containsKey(cb))
//	{
//		return cacheCB.get(cb);
//	}
	double cb_phase[4];
	cb.getPhase(cb_phase);
	const double cb_i = cb_phase[2]-cb_phase[0];
	const double cb_q = cb_phase[3]-cb_phase[1];
	if (cb_i*cb_i + cb_q*cb_q < COLOR_THRESH)
	{
		return BLACK_AND_WHITE;
	}

	double iq_factor[4];

	iq_factor[0] = cb_i * TINT_I + cb_q * TINT_Q;
	iq_factor[2] = -iq_factor[0];
	iq_factor[1] = cb_q * TINT_I - cb_i * TINT_Q;
	iq_factor[3] = -iq_factor[1];

//	const IQ iq = new IQ(iq_factor);
//	if (!this->noise)
//	{
//		cacheCB.put(cb,iq);
//	}
	return IQ(iq_factor);
}

const int AnalogTV::IQINTOFF(130);

void AnalogTV::ntsc_to_yiq(const int isignal, const int siglen, const IQ& iq_factor, int yiq[])
{
	Lowpass_3_58_MHz filterY;
	Lowpass_1_5_MHz filterI;
	Lowpass_1_5_MHz filterQ;
	for (int off = 0; off < siglen; ++off)
	{
		const int sig = this->signal[isignal + off];
		const int y = filterY.next(sig); // + 40; // to show blacker-than-black levels
		int i;
		int q;
		if (y < -2)
		{
			i = 0;
			q = 0;
		}
		else
		{
			i = filterI.next((int)(sig * iq_factor.get(off & 3)));
			q = filterQ.next((int)(sig * iq_factor.get((off + 3) & 3)));
		}

		yiq[off] = (((q+IQINTOFF)&0xff) << 16) | (((i+IQINTOFF)&0xff) << 8) | ((y+IQINTOFF)&0xff);
	}
}

int AnalogTV::yiq2rgb(const int yiq)
{
	double r = ((yiq&0xFF)-IQINTOFF) + 0.956 * (((yiq>>8)&0xFF)-IQINTOFF) + 0.621 * (((yiq>>16)&0xFF)-IQINTOFF);
	double g = ((yiq&0xFF)-IQINTOFF) - 0.272 * (((yiq>>8)&0xFF)-IQINTOFF) - 0.647 * (((yiq>>16)&0xFF)-IQINTOFF);
	double b = ((yiq&0xFF)-IQINTOFF) - 1.105 * (((yiq>>8)&0xFF)-IQINTOFF) + 1.702 * (((yiq>>16)&0xFF)-IQINTOFF);

	const int rgb =
		(calc_color(r) << 16)| 
		(calc_color(g) <<  8)| 
		(calc_color(b) <<  0);

	return rgb;
}

int AnalogTV::color2bw(const int rgb)
{
	const int y = rgb2y(rgb);
	return y<<16 | y<<8 | y;
}

int AnalogTV::rgb2y(const int rgb) // y in range 0-255
{
	return (int)((0.299*((rgb>>16)&0xFF) + 0.587*((rgb>>8)&0xFF) + 0.114*(rgb&0xFF))/1.04);
}

int AnalogTV::calc_color(const double color)
{
	int x = (int)(color * 0x100 / AppleNTSC::LEVEL_RANGE + .5);
	x = clamp(0,x,0x100);
	return x & 0xFF;
}

int AnalogTV::clamp(int min, int x, int lim)
{
	if (x < min)
		return min;
	if (lim <= x)
		return lim-1;
	return x;
}
