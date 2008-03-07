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
#include "monitorcontrolpanel.h"
#include <QtGui/QHBoxLayout>
#include <QtGui/QVBoxLayout>
#include <QtGui/QButtonGroup>
#include <QtGui/QRadioButton>
#include <QtGui/QLabel>

MonitorControlPanel::MonitorControlPanel(AnalogTV& display, QWidget *parent):
	QWidget(parent),
	display(display),
	powerState(false),
	displayTypeState(AnalogTV::MONITOR_COLOR)
{
	setFocusPolicy(Qt::NoFocus);
	QHBoxLayout* layout = new QHBoxLayout();

	QVBoxLayout* powerLayout = new QVBoxLayout();
	QButtonGroup* power = new QButtonGroup(this);

	QRadioButton* powerOn = new QRadioButton("ON");
	powerOn->setFocusPolicy(Qt::NoFocus);
	power->addButton(powerOn);
	connect(powerOn,SIGNAL(toggled(bool)),this,SLOT(powerOn()));
	powerLayout->addWidget(powerOn);



	QRadioButton* powerOff = new QRadioButton("OFF");
	powerOff->setFocusPolicy(Qt::NoFocus);
	power->addButton(powerOff);
	connect(powerOff,SIGNAL(toggled(bool)),this,SLOT(powerOff()));
	powerLayout->addWidget(powerOff);

	powerOff->setChecked(true);



	QHBoxLayout* displayTypeLayoutMon = new QHBoxLayout();
	QHBoxLayout* displayTypeLayoutTV = new QHBoxLayout();

	QLabel* labelMon = new QLabel("Monitor:");
	displayTypeLayoutMon->addWidget(labelMon);
	QLabel* labelTV = new QLabel("TV:");
	displayTypeLayoutTV->addWidget(labelTV);

	displayType = new QButtonGroup(this);
	initDisplayButton(displayTypeLayoutMon,"Color",AnalogTV::MONITOR_COLOR,true);
	initDisplayButton(displayTypeLayoutMon,"White",AnalogTV::MONITOR_WHITE,false);
	initDisplayButton(displayTypeLayoutMon,"Green",AnalogTV::MONITOR_GREEN,false);
	initDisplayButton(displayTypeLayoutMon,"Orange",AnalogTV::MONITOR_ORANGE,false);
	initDisplayButton(displayTypeLayoutTV,"Old Color",AnalogTV::TV_OLD_COLOR,false);
	initDisplayButton(displayTypeLayoutTV,"Old B&&W",AnalogTV::TV_OLD_BW,false);
	initDisplayButton(displayTypeLayoutTV,"New Color",AnalogTV::TV_NEW_COLOR,false);
	initDisplayButton(displayTypeLayoutTV,"New B&&W",AnalogTV::TV_NEW_BW,false);

	QVBoxLayout* displayTypeLayout = new QVBoxLayout();
	displayTypeLayout->addLayout(displayTypeLayoutMon);
	displayTypeLayout->addLayout(displayTypeLayoutTV);

	layout->addLayout(powerLayout);
	layout->addLayout(displayTypeLayout);
	setLayout(layout);
}


MonitorControlPanel::~MonitorControlPanel()
{
}

void MonitorControlPanel::initDisplayButton(QHBoxLayout* displayTypeLayout, const char* name, const AnalogTV::DisplayType type, const bool selected)
{
	QRadioButton* displayTypeButton = new QRadioButton(name);
	displayTypeButton->setFocusPolicy(Qt::NoFocus);
	this->displayType->addButton(displayTypeButton,type);
	connect(displayTypeButton,SIGNAL(toggled(bool)),this,SLOT(displayTypeChange()));
	displayTypeLayout->addWidget(displayTypeButton);

	displayTypeButton->setChecked(selected);
}

void MonitorControlPanel::displayTypeChange()
{
	AnalogTV::DisplayType type = (AnalogTV::DisplayType)this->displayType->checkedId();
	if (type == displayTypeState)
	{
		return;
	}
	this->display.setType(type);
	displayTypeState = type;
}

void MonitorControlPanel::powerOn()
{
	if (this->powerState)
	{
		return;
	}
	this->display.powerOn(true);
	this->powerState = true;
}

void MonitorControlPanel::powerOff()
{
	if (!this->powerState)
	{
		return;
	}
	this->display.powerOn(false);
	this->powerState = false;
}
