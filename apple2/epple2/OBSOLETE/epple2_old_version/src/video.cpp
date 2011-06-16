#include "video.h"
#include "timinggenerator.h"
#include "videoaddressing.h"
#include "videomode.h"
#include "addressbus.h"
#include "picturegenerator.h"
#include "textcharacters.h"


Video::Video(VideoMode& mode, AddressBus& addressBus, PictureGenerator& picgen, TextCharacters& textRows):
	mode(mode), addressBus(addressBus), picgen(picgen), textRows(textRows)
{
	VideoAddressing::buildLUT(TEXT_BASE_1,TEXT_LEN,lutTEXT[0]);
	VideoAddressing::buildLUT(TEXT_BASE_2,TEXT_LEN,lutTEXT[1]);
	VideoAddressing::buildLUT(HRES_BASE_1,HRES_LEN,lutHRES[0]);
	VideoAddressing::buildLUT(HRES_BASE_2,HRES_LEN,lutHRES[1]);
}

Video::~Video()
{
}

void Video::powerOn()
{
	this->t = 0;
	this->flash = false;
	this->cflash = 0;
}

void Video::tick()
{
	const unsigned char data = getDataByte();
	const unsigned char rowToPlot = getRowToPlot(data);

	this->picgen.tick(this->t,rowToPlot);

	updateFlash();

	++this->t;

	if (this->t >= VideoAddressing::BYTES_PER_FIELD)
	{
		this->t = 0;
	}
}

void Video::updateFlash()
{
	++this->cflash;
	if (this->cflash >= FLASH_HALF_PERIOD)
	{
		this->flash = !this->flash;
		this->cflash = 0;
	}
}

unsigned char Video::getDataByte()
{
	// TODO should fix the mixed-mode scanning during VBL (see U.A.][, p. 5-13)

	std::vector<unsigned short>& lut = 
	(this->mode.isDisplayingText(this->t) || !this->mode.isHiRes())
	?
		this->lutTEXT[this->mode.getPage()]
	:
		this->lutHRES[this->mode.getPage()];

	return this->addressBus.read(lut[this->t]);
}

unsigned char Video::getRowToPlot(unsigned char d)
{
	if (this->mode.isDisplayingText(this->t))
	{
		const bool inverse = inverseChar(d);
		const int y = this->t / VideoAddressing::BYTES_PER_ROW;
		d = this->textRows.get(((d & 0x3F) << 3) | (y & 0x07)); // TODO d << 3 ?
		if (inverse)
		{
			d = ~d & 0x7F;
		}
	}
	return d;
}

bool Video::inverseChar(const unsigned char d)
{
	bool inverse;

	const int cs = (d >> 6) & 3;
	if (cs == 0)
	{
		inverse = true;
	}
	else if (cs == 1)
	{
		inverse = this->flash;
	}
	else
	{
		inverse = false;
	}

	return inverse;
}
