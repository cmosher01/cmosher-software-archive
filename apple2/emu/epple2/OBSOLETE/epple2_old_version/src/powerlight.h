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
#ifndef POWERLIGHT_H
#define POWERLIGHT_H

#include <QtGui/QLabel>

class PowerLight : public QLabel
{
	Q_OBJECT

private:

public:
	PowerLight(QWidget * parent = 0);
	~PowerLight();

	void turnOn(bool powerOn);
};

#endif
