#pragma once

class CPauser
{
	__int64 freq;
	int tweak;

public:
	CPauser();
	virtual ~CPauser();

	void Pause(int usec);
	void Tweak(int cycles);
};
