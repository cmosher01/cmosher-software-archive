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
#ifndef SCREEN_H
#define SCREEN_H

#include "keyboard.h"

#include <QWidget>
#include <QtOpenGL/QGLWidget>
class QPaintEvent;

class ScreenImage;
class Apple2;

class Screen : public QGLWidget
{
	Q_OBJECT

	const ScreenImage& image;
	Apple2& apple2;
	KeypressQueue* keys;
	void pt(int key);

protected:
//	void paintEvent(QPaintEvent*);
	void initializeGL();
//	void resizeGL(int w, int h);
	void paintGL();
 
public:
	Screen(const ScreenImage& image, Apple2& apple2, QWidget *parent = 0);
	~Screen();

	void setKeypressQueue(KeypressQueue& q);
	void keyPressEvent(QKeyEvent *event);

public slots:
	void plot();
};

#endif
