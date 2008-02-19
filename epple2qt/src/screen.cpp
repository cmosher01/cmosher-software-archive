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
#include <GL/glu.h>
#include <QApplication>
#include <QString>

#include "screenimage.h"
#include "apple2.h"

Screen::Screen(const ScreenImage& image, Apple2& apple2, QWidget *parent):
	QGLWidget(parent),
	image(image),
	apple2(apple2)
{
//	resize(this->image.size());
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
void inline Screen::pt(const int key)
{
//	std::cout << "sending keypress: " << std::hex << key << std::endl;
	this->keys->push(key);
}
void Screen::keyPressEvent(QKeyEvent *event)
{
	const unsigned int key(event->key());
	const unsigned int chr(event->text().at(0).unicode());

	if (key == Qt::Key_Enter || key == Qt::Key_Return)
	{
		pt('\r');
	}
	else if (key == Qt::Key_Left)
	{
		pt(8);
	}
	else if (key == Qt::Key_Right)
	{
		pt(21);
	}
	else if (key == Qt::Key_Up)
	{
		pt(11);
	}
	else if (key == Qt::Key_Down)
	{
		pt(10);
	}
	// TODO ^@ --> NULL
//		else if (chr == '@' && (mod & InputEvent.SHIFT_DOWN_MASK) != 0 && (mod & InputEvent.CTRL_DOWN_MASK) != 0 )
//		{
//			this.keys.put(0);
//		}
	else if (1 <= chr && chr < 0x80)
	{
		pt(chr);
	}
	else if (key == Qt::Key_Insert)
	{
		QString s = QApplication::clipboard()->text();
		for (int i(0); i < s.length(); ++i)
		{
			unsigned int c = s.at(i).unicode();
			if (c == 0x0A)
			{
				c = 0x0D;
			}
			pt(c);
		}
	}
	else if (key == Qt::Key_Pause)
	{
		this->apple2.reset();
	}
	else
	{
//		std::cout << "ignoring keypress: " << std::hex << key << std::endl;
		QWidget::keyPressEvent(event);
	}
}

// void Screen::paintEvent(QPaintEvent*)
// {
// 	QPainter painter(this);
// 	this->image.drawOnto(painter);
// }

void Screen::plot()
{
//	repaint();
	update();
}

void Screen::initializeGL()
{
	glClearColor (0.0, 0.0, 0.0, 0.0);
	glShadeModel(GL_FLAT);
	glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
}

void Screen::paintGL(void)
{
	glRasterPos2f (-.85, .85);
	glPixelZoom (1,-1);
	glDrawPixels(ScreenImage::WIDTH, ScreenImage::HEIGHT, GL_RGBA, GL_UNSIGNED_BYTE, this->image.image());
	glFlush ();
}
