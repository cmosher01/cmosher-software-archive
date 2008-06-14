#include "stdafx.h"
#include "Timer.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

CTimer::CTimer()
{
	LARGE_INTEGER li;
	if (!::QueryPerformanceFrequency(&li))
		throw "QueryPerformanceFrequency failed";

	m_dCPuS.QuadPart = (__int64)(li.QuadPart/1000000.0+.0000001);

	m_dLastCheckpoint = GetCount();
}

CTimer::~CTimer()
{
}

int CTimer::Checkpoint()
{
	LARGE_INTEGER d = GetCount();
	__int64 dC = d.QuadPart-m_dLastCheckpoint.QuadPart;
	m_dLastCheckpoint = d;
	return (int)(((double)dC)/m_dCPuS.QuadPart+.000001);
}

LARGE_INTEGER CTimer::GetCount()
{
	LARGE_INTEGER li;
	if (!::QueryPerformanceCounter(&li))
		throw "QueryPerformanceCounter failed";
	return li;
}

int CTimer::Peek()
{
	LARGE_INTEGER d = GetCount();
	__int64 dC = d.QuadPart-m_dLastCheckpoint.QuadPart;
	return (int)(((double)dC)/m_dCPuS.QuadPart+.000001);
}
