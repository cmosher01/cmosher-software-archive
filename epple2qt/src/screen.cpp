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
#include "screen.h"

#include <QtGui>
#include <QPainter>
#include "screenimage.h"

#include "renderthread.h"

Screen::Screen(const ScreenImage& image, QWidget *parent):
	QWidget(parent),
	image(image)
{
	resize(this->image.size());
	connect(&this->image,SIGNAL(changed()),this,SLOT(plot()));
}


Screen::~Screen()
{
}

void Screen::setKeypressQueue(KeypressQueue& q)
{
	this->keys = &q;
}
#include <iostream> //TODO remove
void Screen::keyPressEvent(QKeyEvent *event)
{
	const int key(event->key());
	const int chr(event->text().at(0).unicode());
	std::cout << "keypress: " << std::hex << key << std::endl;

	if (key == Qt::Key_Enter || key == Qt::Key_Return)
	{
		this->keys->push('\r');
	}
	else if (key == Qt::Key_Left)
	{
		this->keys->push(8);
	}
	else if (key == Qt::Key_Right)
	{
		this->keys->push(21);
	}
	else if (key == Qt::Key_Up)
	{
		this->keys->push(11);
	}
	else if (key == Qt::Key_Down)
	{
		this->keys->push(10);
	}
	// TODO ^@ --> NULL
//		else if (chr == '@' && (mod & InputEvent.SHIFT_DOWN_MASK) != 0 && (mod & InputEvent.CTRL_DOWN_MASK) != 0 )
//		{
//			this.keys.put(0);
//		}
	else if (0 <= chr && chr < 0x80)
	{
		this->keys->push(chr);
	}
	else
	{
		QWidget::keyPressEvent(event);
	}
}

void Screen::paintEvent(QPaintEvent*)
{
	QPainter painter(this);
	this->image.drawOnto(painter);
}

void Screen::plot()
{
//	repaint();
	update();
}
