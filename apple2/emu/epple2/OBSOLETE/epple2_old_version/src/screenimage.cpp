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
#include "screenimage.h"

ScreenImage::ScreenImage()
{
}

ScreenImage::~ScreenImage()
{
}

void ScreenImage::notifyObservers()
{
	emit changed();
}

void ScreenImage::setElem(const unsigned int i, const unsigned int val)
{
	unsigned int* pn = (unsigned int*)this->img;
	pn[i] = val;
}

void ScreenImage::blank()
{
	memset(this->img,0,WIDTH*HEIGHT*4);
}

// TODO dump PNG
//void ScreenImage::dump(const String type, const File file) throws IOException
//{
//	ImageIO.write(this->image,type,file);
//}
