#include "stdafx.h"
#include "pauser.h"

CPauser::CPauser() : tweak(0)
{
	LARGE_INTEGER i;
	::QueryPerformanceFrequency(&i);
	freq = i.QuadPart;
}

CPauser::~CPauser()
{
}

void CPauser::Pause(int usec)
{
	// cycles to wait
	int cycleD = (int)(((double)freq)*usec/1e6+.000001);

	LARGE_INTEGER t0, t1;

	// get current time
	::QueryPerformanceCounter(&t0);
	// calculate time to end at
	__int64 tEnd = t0.QuadPart+cycleD-tweak;

	// loop, querying time, until hit calculated end time
	::QueryPerformanceCounter(&t1);
	while (t1.QuadPart < tEnd)
	{
		::QueryPerformanceCounter(&t1);
	}
}

void CPauser::Tweak(int cycles)
{
	tweak = cycles;
}
