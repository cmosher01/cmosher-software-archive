#include "video.h"
#include "videoaddressing.h"

const unsigned short int TEXT_BASE_1(0x0400);
const unsigned short int TEXT_BASE_2(0x0800);
const unsigned short int TEXT_LEN(0x0400);

const unsigned short int HRES_BASE_1(0x2000);
const unsigned short int HRES_BASE_2(0x4000);
const unsigned short int HRES_LEN(0x2000);

Video::Video()
{
	VideoAddressing::buildLUT(TEXT_BASE_1,TEXT_LEN,lutTEXT[0]);
	VideoAddressing::buildLUT(TEXT_BASE_2,TEXT_LEN,lutTEXT[1]);
	VideoAddressing::buildLUT(HRES_BASE_1,HRES_LEN,lutHRES[0]);
	VideoAddressing::buildLUT(HRES_BASE_2,HRES_LEN,lutHRES[1]);
}

Video::~Video()
{
}
