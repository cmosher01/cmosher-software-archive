#pragma once
#include "aplmem.h"
#include "ncr65c02.h"

class ii_c_video_t
{
	static unsigned int charmap[128][8];
	// first index is ascii value (0-127)
	//second index (0-7) is the scan line of character cell (0 is top, 7 is bottom)

	HANDLE th;
	HWND hw;
	HDC hdc;
	HPEN h_white_pen;
	POINT pos;

	void scan();
	void home();
	void draw_byte(byte c);

public:
	ii_c_video_t() : th(0), hw(0) {}
	void setup(ii_c_memory_t& xmem, ncr65c02_t& xcpu);
	DWORD raster();

	ii_c_memory_t* mem;
	ncr65c02_t* cpu;
};
