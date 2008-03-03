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
#include "gui.h"

#include "analogtv.h"
#include "monitorcontrolpanel.h"
#include "screenimage.h"
#include "apple2.h"
#include "emulator.h"
#include "contentpane.h"
#include "hypermode.h"
#include "keyboardbuffermode.h"
#include <QtGui/QKeyEvent>
#include <QtGui/QApplication>
#include <QtGui/QClipboard>
#include <QtCore/QString>

GUI::GUI(ScreenImage& screenImage, Apple2& apple2, Emulator& emu, AnalogTV& display, KeypressQueue& keys, HyperMode& fhyper, KeyboardBufferMode& buffered):
	apple2(apple2),
	keys(keys),
	fhyper(fhyper),
	buffered(buffered)
{
	ContentPane* pcontent = new ContentPane(screenImage,display,emu,this);
	setCentralWidget(pcontent);
	grabKeyboard();
}


GUI::~GUI()
{
}
#include <iostream>
void GUI::closeEvent(QCloseEvent* event)
{
	std::cout << "GUI::closeEvent" << std::endl;
}

void inline GUI::pt(const int key)
{
	this->keys.push(key);
}

void GUI::keyPressEvent(QKeyEvent *event)
{
	const unsigned int key(event->key());
	const QString text(event->text());
	const unsigned int chr(text.length()>0 ? text.at(0).unicode() : 0);

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
	else if ('a' <= chr && chr <= 'z')
	{
		pt(chr-32);
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
	else if (key == Qt::Key_F11)
	{
		this->fhyper.toggleHyper();
	}
	else if (key == Qt::Key_F12)
	{
		this->buffered.toggleBuffered();
	}
	// TODO other special keys
	else
	{
		QWidget::keyPressEvent(event);
	}
}
