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

#include <QtGui/QPainter>
#include <GL/glu.h>
#include <QtGui/QWidget>

#include "apple2.h"

Screen::Screen(const ScreenImage& image, QWidget *parent):
	QGLWidget(parent),
	image(image)
{
	setFocusPolicy(Qt::NoFocus);
	resize(ScreenImage::WIDTH,ScreenImage::HEIGHT);
	setMaximumSize(ScreenImage::WIDTH,ScreenImage::HEIGHT);
	setMinimumSize(ScreenImage::WIDTH,ScreenImage::HEIGHT);
	connect(&this->image,SIGNAL(changed()),this,SLOT(updateGL()));
	show();
}

void Screen::initializeGL()
{
	glViewport(0, 0, ScreenImage::WIDTH+2, ScreenImage::HEIGHT+2);
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity( );
	gluOrtho2D(0, ScreenImage::WIDTH+2, 0, ScreenImage::HEIGHT+2);
	glPixelZoom(1,-1);
	glClearColor (0.0, 0.0, 0.0, 0.0);
	glShadeModel(GL_FLAT);
	glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
	glRasterPos2f(0,ScreenImage::HEIGHT);
}
