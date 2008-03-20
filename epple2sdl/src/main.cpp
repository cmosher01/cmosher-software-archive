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


#include "guiemulator.h"
#include "config.h"

#include <SDL/SDL.h>

#include <string>
#include <iostream>
#include <ostream>
#include <memory>

int main(int argc, char* argv[])
{
	if (SDL_Init(SDL_INIT_TIMER | SDL_INIT_AUDIO | SDL_INIT_VIDEO) != 0)
	{
		fprintf(stderr,"Unable to initialize SDL: %s\n",SDL_GetError());
		return 1;
	}

	SDL_EnableUNICODE(1);
	SDL_ShowCursor(0);
	SDL_EnableKeyRepeat(0,0);

	std::auto_ptr<Emulator> emu(new GUIEmulator());

	if (argc > 2)
	{
		std::cerr << "usage: epple2 [config-file]" << std::endl;
		return 1;
	}
	std::string config_file("epple2.conf");
	if (argc > 1)
	{
		config_file = argv[1];
	}
	Config cfg(config_file);
	emu->config(cfg);

	emu->init();

	return emu->run();
}
