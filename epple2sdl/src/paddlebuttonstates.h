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
#ifndef PADDLEBUTTONSTATES_H
#define PADDLEBUTTONSTATES_H

#include <vector>

class PaddleButtonStates
{
	static const int PADDLE_COUNT;

	std::vector<bool> button;

public:
	PaddleButtonStates();
	~PaddleButtonStates();
	void setButton(const int btn, const bool pressed);
	bool isDown(const int paddle);
};

#endif
