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
#include "computercontrolpanel.h"

#include "emulator.h"
#include "powerlight.h"
#include <QtGui/QHBoxLayout>
#include <QtGui/QVBoxLayout>
#include <QtGui/QButtonGroup>
#include <QtGui/QRadioButton>
#include <QtGui/QLabel>

ComputerControlPanel::ComputerControlPanel(Emulator& emu, QWidget *parent):
	QWidget(parent),
	emu(emu),
	powerState(false)
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



	this->light = new PowerLight();
	this->light->turnOn(false);


	layout->addLayout(powerLayout);
	layout->addWidget(light);
	setLayout(layout);
}

ComputerControlPanel::~ComputerControlPanel()
{
}

void ComputerControlPanel::powerOn()
{
	if (this->powerState)
	{
		return;
	}
	this->light->turnOn(true);
	this->emu.powerOnComputer();
	this->powerState = true;
}

void ComputerControlPanel::powerOff()
{
	if (!this->powerState)
	{
		return;
	}
	this->light->turnOn(false);
	this->emu.powerOffComputer();
	this->powerState = false;
}
