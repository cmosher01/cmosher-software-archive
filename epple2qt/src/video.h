#ifndef VIDEO_H_
#define VIDEO_H_

#include <vector>

class VideoMode;
class AddressBus;
class PictureGenerator;
class TextCharacters;

class Video
{
private:
	static const int FLASH_HALF_PERIOD;

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
