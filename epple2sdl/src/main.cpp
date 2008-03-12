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
#include <string>
#include <SDL/SDL.h>
#include <clocale>
#include <iostream>

int main( int argc, char ** argv )
{
	const char* act = ::setlocale(LC_ALL,"english_us");
	std::cout << "locale: " << act << std::endl;

	if (SDL_Init(SDL_INIT_TIMER | SDL_INIT_AUDIO | SDL_INIT_VIDEO) != 0)
	{
			printf("Unable to initialize SDL: %s\n",SDL_GetError());
			return 1;
	}
	SDL_EnableUNICODE(1);
	SDL_ShowCursor(0);
	SDL_EnableKeyRepeat(0,0);

	Emulator* emu = new GUIEmulator();
//	printf("Created Emulator\n");

	std::string config_file("./epple2.conf");
//	std::string config_file("/home/chris/epple2sdl/epple2.conf");
	Config cfg(config_file);//this.args.getConfig());
//	printf("Opened config file\n");
	emu->config(cfg);
//	printf("Processed config file\n");

	emu->init();

	const int ret = emu->run();

	delete emu;

	return ret;
}
