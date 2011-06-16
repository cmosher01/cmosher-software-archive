/*
    epple2
    Copyright (C) 2008 by Chris Mosher <chris@mosher.mine.nu>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY, without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
#include "powerlight.h"

#include <QtCore/QString>
#include <QtGui/QPalette>


PowerLight::PowerLight(QWidget* parent):
	QLabel(QString("POWER"),parent)
{
	setFocusPolicy(Qt::NoFocus);

	setAlignment(Qt::AlignCenter);
	setAutoFillBackground(true);

	QPalette palette;
	palette.setColor(QPalette::Active,QPalette::Window,QColor(255,240,120));
	palette.setColor(QPalette::Inactive,QPalette::Window,QColor(255,240,120));
	palette.setColor(QPalette::Disabled,QPalette::Window,QColor(128,128,128));
	palette.setColor(QPalette::Active,QPalette::WindowText,QColor(0,0,0));
	palette.setColor(QPalette::Inactive,QPalette::WindowText,QColor(0,0,0));
	palette.setColor(QPalette::Disabled,QPalette::WindowText,QColor(0,0,0));
	palette.setColor(QPalette::Active,QPalette::Text,QColor(0,0,0));
	palette.setColor(QPalette::Inactive,QPalette::Text,QColor(0,0,0));
	palette.setColor(QPalette::Disabled,QPalette::Text,QColor(0,0,0));
	palette.setColor(QPalette::Disabled,QPalette::Light,QColor(128,128,128));
	palette.setColor(QPalette::Disabled,QPalette::Midlight,QColor(0,0,0));
	palette.setColor(QPalette::Disabled,QPalette::Dark,QColor(0,0,0));
	palette.setColor(QPalette::Disabled,QPalette::Mid,QColor(0,0,0));
	palette.setColor(QPalette::Disabled,QPalette::Shadow,QColor(0,0,0));
	setPalette(palette);

	setMinimumSize(50,50);
	setMaximumSize(50,50);

	QFont font("Arial",7,QFont::Normal);
	setFont(font);
}

PowerLight::~PowerLight()
{
}

void PowerLight::turnOn(bool powerOn)
{
	setEnabled(powerOn);
	update();
}
