#include "stdafx.h"
#include "parser.h"



static const int BUF = 40*(16+1)*2+2;

int main(int argc, char* argv[])
{
	HANDLE m_hFile = ::CreateFile("COM1",GENERIC_READ,0,NULL,OPEN_EXISTING,0,NULL);
	if (m_hFile==INVALID_HANDLE_VALUE)
	{
		cerr << "cannot open COM1" << endl;
		return 1;
	}

	if (!::SetCommMask(m_hFile,EV_RLSD))
	{
		cerr << "Error setting COM events" << endl;
		return 1;
	}

	BOOL bEverGotAPulse(FALSE);

	if (!::SetPriorityClass(::GetCurrentProcess(),REALTIME_PRIORITY_CLASS))
	{
		cerr << "Error setting priority class" << endl;
		return 1;
	}

	if (!::SetThreadPriority(::GetCurrentThread(),THREAD_PRIORITY_TIME_CRITICAL))
	{
		cerr << "Error setting priority" << endl;
		return 1;
	}

	::EscapeCommFunction(m_hFile,SETRTS);

	DWORD dwModemStatus;
	if (!::GetCommModemStatus(m_hFile,&dwModemStatus))
	{
		cerr << "Error getting modem status" << endl;
		return 1;
	}

	// clear status
	while ((dwModemStatus&MS_RLSD_ON)!=MS_RLSD_ON)
	{
		::GetCommModemStatus(m_hFile,&dwModemStatus);
	}

	__int64 usec[BUF];
	int i = 0;
	LARGE_INTEGER t, lastpulse;
	lastpulse.QuadPart = 0;
	bool bWasPulse = FALSE;
	cout << "timing" << endl;
	while (i < BUF || !bEverGotAPulse)
	{
		::GetCommModemStatus(m_hFile,&dwModemStatus);
		bool bIsPulse = (dwModemStatus&MS_RLSD_ON)!=MS_RLSD_ON;
		if (bIsPulse!=bWasPulse)
		{
			::QueryPerformanceCounter(&t);

			if (!bEverGotAPulse)
				bEverGotAPulse = TRUE;

			usec[i++] = (bWasPulse?t.QuadPart:-t.QuadPart);
			bWasPulse = bIsPulse;
		}
	}
	::EscapeCommFunction(m_hFile,CLRRTS);

	LARGE_INTEGER f;
	::QueryPerformanceFrequency(&f);
	CParser parser;
	parser.SetParams(2000,1000);
	__int64 prev = 0;
	for (int j(0); j<i; ++j)
	{
		double c = (double)usec[j];
		if (prev)
		{
			if (c >= 0)
			{
				int x = (int)((c+prev)*1e6/f.QuadPart);
				parser.Add(true,x);
			}
			else
			{
				int x = (int)((-c-prev)*1e6/f.QuadPart);
				parser.Add(false,x);
			}
		}
		prev = c;
	}
	parser.Parse();

	return 0;
}
