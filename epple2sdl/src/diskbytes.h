#ifndef DISKBYTES_H
#define DISKBYTES_H

#include <string>
#include <vector>

class DiskBytes
{
private:
	enum { TRACKS_PER_DISK = 0x23 };
	enum { BYTES_PER_TRACK = 0x1A00 };

//	HyperMode& fhyper;

	std::vector<unsigned char> bytes[TRACKS_PER_DISK];

	std::string fileName;
	std::string filePath;
	bool writable;
	bool loaded;
	unsigned int byt; // represents rotational position of disk
	unsigned int waitTimes;
	bool modified;

	void nextByte();

public:
	DiskBytes();
	~DiskBytes();

	void load(const std::string& filePath);
	std::string getFileName()
	{
		return this->fileName;
	}

	bool isLoaded()
	{
		return this->loaded;
}

	void save();
	void unload();
	unsigned char get(const int track);
	void put(const unsigned char track, const unsigned char value);
	bool isWriteProtected()
	{
		return !this->writable;
	}

	bool isModified()
	{
		return this->modified;
	}
};

#endif
