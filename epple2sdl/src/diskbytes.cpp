#include "diskbytes.h"

#include <vector>
#include <algorithm>
#include <istream>
#include <ostream>
#include <fstream>

DiskBytes::DiskBytes(/*TODO HyperMode& fhyper*/)
{
	unload();
}

DiskBytes::~DiskBytes()
{
}

void DiskBytes::load(const std::string& filePath)
{
	if (isLoaded())
	{
		unload();
	}

	std::ifstream in(filePath.c_str(),std::ios::binary);
	for (int t(0); t < TRACKS_PER_DISK; ++t)
	{
		this->bytes[t].resize(BYTES_PER_TRACK);
		in.read((char*)&this->bytes[t][0],BYTES_PER_TRACK);
	}
	// TODO check file length on all bytes >= 0x96
	// TODO check I/O errors
	in.close();

	this->filePath = filePath;

	std::ofstream outf(filePath.c_str(),std::ios::binary|std::ios::app);
	this->writable = outf.is_open();
	outf.close();

	this->loaded = true;
	this->modified = false;
}

void DiskBytes::save()
{
	if (isWriteProtected() || !isLoaded())
	{
		return;
	}
	std::ofstream out(filePath.c_str(),std::ios::binary);
	for (int t(0); t < TRACKS_PER_DISK; ++t)
		out.write((char*)&this->bytes[t][0],BYTES_PER_TRACK);
	out.flush();
	out.close();

	this->modified = false;
}

void DiskBytes::unload()
{
	this->byt = 0;
	this->writable = true;
	this->loaded = false;
	this->filePath = "";
	this->modified = false;
}

unsigned char DiskBytes::get(const int track)
{
//	if (track < 0 || Drive.TRACKS_PER_DISK <= track)
//	{
//		throw new IllegalStateException();
//	}
	if (!isLoaded())
	{
// TODO		waitIfTooFast();
//		if (!isLoaded())
//		{
			return 0xFF;
//		}
	}
	const unsigned char ret = this->bytes[track][this->byt];
	nextByte();
	return ret;
}
/*
void waitIfTooFast()
{
	if (this->fhyper.isHyper())
	{
		return;
	}

	++this->waitTimes;
	if (this->waitTimes >= 0x100)
	{
		synchronized (this->loaded)
		{
			try
			{
				this->loaded.wait(50);
			}
			catch (InterruptedException e)
			{
				Thread.currentThread().interrupt();
			}
		}
		this->waitTimes = 0;
	}
}
*/
void DiskBytes::put(const unsigned char track, const unsigned char value)
{
	if (TRACKS_PER_DISK <= track)
	{
		throw 0;
	}
	if (isWriteProtected() || !isLoaded())
	{
		return;
	}
	this->bytes[track][this->byt] = value;
	this->modified = true;
	nextByte();
}

void inline DiskBytes::nextByte()
{
	// emulates circular disk track
	++this->byt;
	if (this->byt >= BYTES_PER_TRACK)
	{
		this->byt = 0;
	}
}

