#ifndef VIDEO_H_
#define VIDEO_H_

#include <vector>
#include "timinggenerator.h"

class VideoMode;
class AddressBus;
class PictureGenerator;
class TextCharacters;

class Video
{
private:
	enum { TEXT_BASE_1 = 0x0400 };
	enum { TEXT_BASE_2 = 0x0800 };
	enum { TEXT_LEN = 0x0400 };
	
	enum { HRES_BASE_1 = 0x2000 };
	enum { HRES_BASE_2 = 0x4000 };
	enum { HRES_LEN = 0x2000 };
	
	
	enum { FLASH_HALF_PERIOD = TimingGenerator::AVG_CPU_HZ/4 }; // 2 Hz period = 4 Hz half-period

	std::vector<unsigned short> lutTEXT[2]; // [0] is page 1, [1] is page 2
	std::vector<unsigned short> lutHRES[2]; // [0] is page 1, [1] is page 2

	VideoMode& mode;
	AddressBus& addressBus;
	PictureGenerator& picgen;

	TextCharacters& textRows;

	unsigned int t;

	bool flash;
	int cflash;

	void updateFlash();
	unsigned char getDataByte();
	unsigned char getRowToPlot(unsigned char d);
	bool inverseChar(const unsigned char d);

public:
	Video(VideoMode& mode, AddressBus& addressBus, PictureGenerator& picgen, TextCharacters& textRows);
	~Video();
	void powerOn();
	void tick();
};

#endif /*VIDEO_H_*/
