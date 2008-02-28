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
#ifndef MONITORCONTROLPANEL_H
#define MONITORCONTROLPANEL_H

#include "analogtv.h"
#include <QtGui/QWidget>
class QButtonGroup;
class QHBoxLayout;

class MonitorControlPanel : public QWidget
{
	Q_OBJECT

private:
	AnalogTV& display;
	bool powerState;
	AnalogTV::DisplayType displayTypeState;
	QButtonGroup* displayType;
	void initDisplayButton(QHBoxLayout* displayTypeLayout, const char* name, const AnalogTV::DisplayType type, const bool selected);

private slots:
	void powerOn();
	void powerOff();
	void displayTypeChange();

public:
	MonitorControlPanel(AnalogTV& display, QWidget *parent = 0);
	~MonitorControlPanel();
};

#endif
