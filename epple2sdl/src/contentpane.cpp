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
#include "contentpane.h"

#include "analogtv.h"
#include "computercontrolpanel.h"
#include "monitorcontrolpanel.h"
#include "screen.h"
#include "screenimage.h"
#include "emulator.h"

ContentPane::ContentPane(ScreenImage& screenImage, AnalogTV& display, Emulator& emu)
{
//	setFocusPolicy(Qt::NoFocus);
	Screen* pscreen = new Screen(screenImage);

	MonitorControlPanel* monitorControls = new MonitorControlPanel(display);

	ComputerControlPanel* computerControls = new ComputerControlPanel(emu);

/*
	QVBoxLayout* layout = new QVBoxLayout();
	layout->addWidget(pscreen);
	layout->addWidget(monitorControls);
	layout->addWidget(computerControls);

	setLayout(layout);

	pscreen->setFocus();
*/
}


ContentPane::~ContentPane()
{
}
