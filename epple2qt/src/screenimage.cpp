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
#include "applentsc.h"
#include "videoaddressing.h"

#include <QColor>
#include <QPoint>
#include <QPainter>

static const int WIDTH(AppleNTSC::H-AppleNTSC::PIC_START-2);

ScreenImage::ScreenImage():
	image(WIDTH,VideoAddressing::VISIBLE_ROWS_PER_FIELD*2,QImage::Format_RGB32)
{
}

ScreenImage::~ScreenImage()
{
}

void ScreenImage::notifyObservers()
{
	// TODO observer/observable
//	setChanged();
//	super.notifyObservers();
}

void ScreenImage::setElem(const unsigned int i, const QColor& val)
{
	const unsigned int x = i % WIDTH;
	const unsigned int y = i / WIDTH;
	this->image.setPixel(x,y,val.rgb());
}

void ScreenImage::blank()
{
	this->image.fill(0);
}

QSize ScreenImage::size()
{
	return this->image.size();
}

const QImage& ScreenImage::getImage()
{
	return this->image;
}

void ScreenImage::drawOnto(QPainter& painter)
{
	painter.drawImage(QPoint(),this->image);
}

// TODO dump PNG
//void ScreenImage::dump(const String type, const File file) throws IOException
//{
//	ImageIO.write(this->image,type,file);
//}
