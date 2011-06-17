#pragma once
#include "aplmem.h"
#include "ncr65c02.h"
#include "aplvid.h"

class apple_ii_c
{
	ii_c_video_t vid;
	ii_c_memory_t mem;
	ncr65c02_t cpu;

public:
	apple_ii_c() {}
	void run();
};
