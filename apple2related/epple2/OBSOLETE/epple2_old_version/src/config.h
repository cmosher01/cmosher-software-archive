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
#ifndef CONFIG_H
#define CONFIG_H

#include <string>
class Memory;
class Slots;

class Config
{
private:
	const std::string& file_path;

	void parseLine(const std::string& line, Memory& memory, Slots& slts /*HyperMode fhyper, StandardIn.EOFHandler eofHandler*/, int& revision);
	void loadDisk(Slots& slts, int slot, int drive, const std::string& fnib);
	void insertCard(const std::string& cardType, int slot, Slots& slts/*, HyperMode fhyper, StandardIn.EOFHandler eofHandler*/);

public:
	Config(const std::string& file_path);
	~Config();

	void parse(Memory& memory, Slots& slts /*HyperMode fhyper, StandardIn.EOFHandler eofHandler*/, int& revision);
};

#endif