#pragma once

class CTimer
{
	LARGE_INTEGER m_dCPuS;
	LARGE_INTEGER m_dLastCheckpoint;

	LARGE_INTEGER GetCount();

public:
	CTimer();
	virtual ~CTimer();
	int Checkpoint();
	int Peek();
};
