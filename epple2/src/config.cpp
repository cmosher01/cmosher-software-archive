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
#include "config.h"

#include <istream>
#include <fstream>
#include <sstream>

Config::Config(const std::string& file_path):
	file_path(file_path)
{
}


Config::~Config()
{
}

static void strip_comment(std::string& str)
{
	const size_t comment = str.find('#');
	if (comment < std::string::npos)
	{
		str.erase(comment);
	}
}

static void trim(std::string& str)
{
	{
		const size_t p = str.find_first_not_of(" \t");
		if (p < std::string::npos)
		{
			str.erase(0,p);
		}
	}
	{
		const size_t p = str.find_last_not_of(" \t");
		if (p < std::string::npos-1)
		{
			str.erase(p+1);
		}
	}
}

void Config::parse(Memory& memory, Slots& slts /*HyperMode hyper, StandardIn.EOFHandler eofHandler*/)
{
	std::ifstream in(this->file_path.c_str());
	if (!in.is_open())
	{
		throw "Cannot open config file.";
	}

	std::string line;
	getline(in,line);
	while (!in.eof())
	{
		strip_comment(line);
		trim(line);
		if (!line.empty())
		{
			parseLine(line,memory,slts/*,hyper,eofHandler*/);
		}
		getline(in,line);
	}
	in.close();

//	verifyUniqueCards(slts);
}

/*
void verifyUniqueCards(const Slots& cards)
{
	int nStdOut = 0;
	int nStdIn = 0;
	for (Card card : cards)
	{
		if (card instanceof StandardOut)
		{
			++nStdOut;
		}
		else if (card instanceof StandardIn)
		{
			++nStdIn;
		}
	}
	if (nStdOut > 1)
	{
		throw new IllegalArgumentException("Error in config file: only one stdout card is supported.");
	}
	if (nStdIn > 1)
	{
		throw new IllegalArgumentException("Error in config file: only one stdin card is supported.");
	}
}
*/
#include <iostream>
void Config::parseLine(const std::string& line, Memory& memory, Slots& slts /*HyperMode hyper, StandardIn.EOFHandler eofHandler*/)
{
//	std::cout << "/" << line << "/" << std::endl;

//	final StringTokenizer tok = new StringTokenizer(line);
	std::istringstream tok(line);

//	final String cmd = tok.nextToken();
	std::string cmd;
	tok >> cmd;
	if (cmd == "slot")
	{
//		if (!tok.hasMoreTokens()) throw new IllegalArgumentException("Error in config file: "+line);
//		final String sSlot = tok.nextToken();
//		final int slot = Integer.decode(sSlot);
//		if (!tok.hasMoreTokens()) throw new IllegalArgumentException("Error in config file: "+line);
//		final String sCardType = tok.nextToken();
		int slot;
		std::string sCardType;
		tok >> slot >> sCardType;

		insertCard(sCardType,slot,slts);//,hyper,eofHandler);
	}
	else if (cmd == "import")
	{
//		if (!tok.hasMoreTokens()) throw new IllegalArgumentException("Error in config file: "+line);
//		final String sm = tok.nextToken();
		std::string sm;
		tok >> sm;

		int slot(-1);
		if (sm == "slot")
		{
//			if (!tok.hasMoreTokens()) throw new IllegalArgumentException("Error in config file: "+line);
//			final String sSlot = tok.nextToken();
//			slot = Integer.decode(sSlot);
			tok >> slot;
		}
		else if (sm != "motherboard")
		{
			throw 0;// TODO new IllegalArgumentException("Error in config file: "+line);
		}
//		if (!tok.hasMoreTokens()) throw new IllegalArgumentException("Error in config file: "+line);

//		final String romtype = tok.nextToken();
		std::string romtype;
		tok >> romtype;

//		if (!tok.hasMoreTokens()) throw new IllegalArgumentException("Error in config file: "+line);
//		final String sBase = tok.nextToken();
//		final int base = Integer.decode(sBase);
		int base(0);
		tok >> base;

//		if (!tok.hasMoreTokens()) throw new IllegalArgumentException("Error in config file: "+line);
//		final String file = tok.nextToken("\0").trim(); // rest of line
//		final InputStream rom = new BufferedInputStream(new FileInputStream(new File(file)));
		std::string file;
		getline(tok,file);
		std::ifstream rom(file);

		if (slot < 0) // motherboard
		{
			if (romtype != "rom")
			{
				throw 0;// TODO new IllegalArgumentException("Error in config file: "+line);
			}
			memory.load(base,rom);
		}
		else
		{
			if (8 <= slot)
			{
				throw 0;// TODO new IllegalArgumentException("Error in config file: invalid slot number: "+slot);
			}
			final Card card = slts.get(slot);
			if (romtype.equalsIgnoreCase("rom"))
			card.loadRom(base,rom);
			else if (romtype.equalsIgnoreCase("rom7"))
			card.loadSeventhRom(base,rom);
			else if (romtype.equalsIgnoreCase("rombank"))
			card.loadBankRom(base,rom);
			else
				throw new IllegalArgumentException("Error in config file: invalid rom (must be rom, rom7, or rombank): "+romtype);
		}
		rom.close();
	}
	else if (cmd == "load")
	{
		if (!tok.hasMoreTokens()) throw new IllegalArgumentException("Error in config file: "+line);
		if (!tok.nextToken().equalsIgnoreCase("slot")) throw new IllegalArgumentException("Error in config file: "+line);
		final String sSlot = tok.nextToken();
		final int slot = Integer.decode(sSlot);

		if (!tok.hasMoreTokens()) throw new IllegalArgumentException("Error in config file: "+line);
		if (!tok.nextToken().equalsIgnoreCase("drive")) throw new IllegalArgumentException("Error in config file: "+line);
		final String sDrive = tok.nextToken();
		final int drive = Integer.decode(sDrive);

		if (!tok.hasMoreTokens()) throw new IllegalArgumentException("Error in config file: "+line);
		final String nib = tok.nextToken("\0").trim(); // rest of line
		final File fnib = new File(nib);

		loadDisk(slts,slot,drive,fnib);
	}
	else
	{
		throw 1;//TODO new IllegalArgumentException("Error in config file: "+line);
	}
}

void Config::loadDisk(Slots& slts, int slot, int drive, const std::string& fnib)
{
/*
	if (drive < 1 || 2 < drive)
	{
		throw new IllegalArgumentException("Error in config file: invalid drive number "+drive);
	}
	final Card card = slts.get(slot);
	if (!(card instanceof DiskController))
	{
		throw new IllegalArgumentException("Card in slot "+slot+" is not a disk controller card.");
	}
	final DiskController controller = (DiskController)card;
	controller.loadDisk(drive-1,fnib);
*/
}

void Config::insertCard(const std::string& cardType, int slot, Slots& slts/*, HyperMode hyper, StandardIn.EOFHandler eofHandler*/)
{
/*
	if (slot < 0 || Slots.SLOTS <= slot)
	{
		throw new IllegalArgumentException("Error in config file: invalid slot number: "+slot);
	}
	final Card card;
	if (cardType.equalsIgnoreCase("language"))
	{
		card = new LanguageCard();
	}
	else if (cardType.equalsIgnoreCase("firmware"))
	{
	card = new FirmwareCard();
	}
	else if (cardType.equalsIgnoreCase("disk"))
	{
	card = new DiskController(hyper);
	}
	else if (cardType.equalsIgnoreCase("clock"))
	{
	card = new ClockCard();
	}
	else if (cardType.equalsIgnoreCase("stdout"))
	{
		card = new StandardOut();
	}
	else if (cardType.equalsIgnoreCase("stdin"))
	{
	final KeypressQueue stdinkeys = new KeypressQueue();
	new StandardInProducer(stdinkeys);
	card = new StandardIn(eofHandler,stdinkeys);
	}
	else
	{
		throw new IllegalArgumentException("Error in config file: unknown card type: "+cardType);
	}

	slts.set(slot,card);
*/
}
