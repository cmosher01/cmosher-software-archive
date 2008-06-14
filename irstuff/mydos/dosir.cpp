void main()
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

	cout << "press return when ready" << endl;
	getchar();

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
	::GetCommModemStatus(m_hFile,&dwModemStatus);

	// clear status
	while ((dwModemStatus&MS_RLSD_ON)!=MS_RLSD_ON)
		::Sleep(1);

	int usec[10000];
	int i = 0;
	LARGE_INTEGER t;
	bool bWasPulse = FALSE;
	while (i < 256 || !bEverGotAPulse)
	{
		::GetCommModemStatus(m_hFile,&dwModemStatus);
		bool bIsPulse = (dwModemStatus&MS_RLSD_ON)!=MS_RLSD_ON;
		if (bIsPulse!=bWasPulse)
		{
			::QueryPerformanceCounter(&t);
			if (!bEverGotAPulse)
				bEverGotAPulse = TRUE;
			else
				usec[i++] = bWasPulse?uS:-uS;
			bWasPulse = bIsPulse;
		}
	}
	::EscapeCommFunction(m_hFile,CLRRTS);

	for (int j(0); j<i; ++j)
	{
		int c = usec[j];
		if (c >= 0)
		{
			cout << "p " << c << endl;
		}
		else
		{
			cout << "s " << -c << endl;
		}
	}

	cout << "press return when ready" << endl;
	getchar();
	getchar();
}
