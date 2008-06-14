#include "stdafx.h"
#include "pauser.h"

static const unsigned char dataByte = 0x5b;

int main(int argc, char* argv[])
{
	unsigned char dataBytes[1024];
	for (int j(0); j<1024; ++j)
	{
		dataBytes[j] = dataByte;
	}

	if (!::SetPriorityClass(::GetCurrentProcess(),REALTIME_PRIORITY_CLASS))
		throw "Error setting priority class.";

	if (!::SetThreadPriority(::GetCurrentThread(),THREAD_PRIORITY_TIME_CRITICAL))
		throw "Error setting priority.";

    HANDLE com2 = ::CreateFile("COM2", GENERIC_READ | GENERIC_WRITE, 0, NULL, OPEN_EXISTING, 0, NULL);
	if (com2 == INVALID_HANDLE_VALUE)
    {
        return 0;
    }

    // Set the size of input and output buffers
    ::SetupComm(com2, 1, 1);

    // clear the read and write buffers
    ::PurgeComm(com2, PURGE_TXABORT | PURGE_RXABORT | PURGE_TXCLEAR | PURGE_RXCLEAR);

    // Reinitializes all IRDA port settings
    DCB dcb;

    dcb.DCBlength = sizeof(DCB);

    ::GetCommState(com2, &dcb);

    dcb.BaudRate          = CBR_115200;
    dcb.fBinary           = TRUE;
    dcb.fOutxCtsFlow      = FALSE;
    dcb.fOutxDsrFlow      = FALSE;
    dcb.fDtrControl       = DTR_CONTROL_DISABLE;
    dcb.fDsrSensitivity   = FALSE;
    dcb.fTXContinueOnXoff = FALSE;
    dcb.fOutX             = FALSE;
    dcb.fInX              = FALSE;
    dcb.fErrorChar        = FALSE;  
    dcb.fNull             = FALSE;
    dcb.fRtsControl       = RTS_CONTROL_DISABLE;
    dcb.fAbortOnError     = FALSE;
    dcb.fParity           = FALSE;
	dcb.ByteSize          = 7;
	dcb.Parity            = NOPARITY; 
	dcb.StopBits          = ONESTOPBIT;

    ::SetCommState(com2, &dcb);

    // Set the timeouts for all read and write operations

    COMMTIMEOUTS timeouts;

    ::GetCommTimeouts(com2, &timeouts);

    timeouts.ReadIntervalTimeout         = MAXDWORD;
    timeouts.ReadTotalTimeoutMultiplier  = 0;
    timeouts.ReadTotalTimeoutConstant    = 0;
    timeouts.WriteTotalTimeoutMultiplier = 0;
    timeouts.WriteTotalTimeoutConstant   = 0;

    ::SetCommTimeouts(com2, &timeouts);

	DWORD dwEvent=EV_TXEMPTY;
	::SetCommMask(com2,dwEvent);



	CPauser pauser;
//	pauser.Tweak();

	int pulseBytes(7);

	DWORD dwBytesWritten;

	int c;

	c = 108;

	cout << "press Enter when ready." << endl;
	char s[1];
	cin.getline(s,1);

	::WriteFile(com2,dataBytes,c,&dwBytesWritten,NULL);

	int pause0(1000);
	int pause1(1700);
//	LARGE_INTEGER t0[20], t1[20];

	pauser.Pause(4220);
	for (int i(0); i<20; ++i)
	{
		::WriteFile(com2,dataBytes,pulseBytes,&dwBytesWritten,NULL);

		pauser.Pause(pause1);
		::WriteFile(com2,dataBytes,pulseBytes,&dwBytesWritten,NULL);
		pauser.Pause(pause1);
		::WriteFile(com2,dataBytes,pulseBytes,&dwBytesWritten,NULL);
//		::QueryPerformanceCounter(t0+i);
		pauser.Pause(pause0);
//		::QueryPerformanceCounter(t1+i);
		::WriteFile(com2,dataBytes,pulseBytes,&dwBytesWritten,NULL);
		pauser.Pause(pause0);
		::WriteFile(com2,dataBytes,pulseBytes,&dwBytesWritten,NULL);

		pauser.Pause(pause0);
		::WriteFile(com2,dataBytes,pulseBytes,&dwBytesWritten,NULL);
		pauser.Pause(pause1);
		::WriteFile(com2,dataBytes,pulseBytes,&dwBytesWritten,NULL);
		pauser.Pause(pause0);
		::WriteFile(com2,dataBytes,pulseBytes,&dwBytesWritten,NULL);
		pauser.Pause(pause1);
		::WriteFile(com2,dataBytes,pulseBytes,&dwBytesWritten,NULL);

		pauser.Pause(pause0);
		::WriteFile(com2,dataBytes,pulseBytes,&dwBytesWritten,NULL);
		pauser.Pause(pause1);
		::WriteFile(com2,dataBytes,pulseBytes,&dwBytesWritten,NULL);
		pauser.Pause(pause1);
		::WriteFile(com2,dataBytes,pulseBytes,&dwBytesWritten,NULL);
		pauser.Pause(pause1);
		::WriteFile(com2,dataBytes,pulseBytes,&dwBytesWritten,NULL);

		pauser.Pause(pause1);
		::WriteFile(com2,dataBytes,pulseBytes,&dwBytesWritten,NULL);
		pauser.Pause(pause0);
		::WriteFile(com2,dataBytes,pulseBytes,&dwBytesWritten,NULL);
		pauser.Pause(pause0);
		::WriteFile(com2,dataBytes,pulseBytes,&dwBytesWritten,NULL);
		pauser.Pause(pause0);
		::WriteFile(com2,dataBytes,pulseBytes,&dwBytesWritten,NULL);

		pauser.Pause(46420-16880-pause0-8*pause1);
	}

    ::PurgeComm(com2, PURGE_TXABORT | PURGE_RXABORT | PURGE_TXCLEAR | PURGE_RXCLEAR);

    ::CloseHandle(com2);
/*
	for (i = 0; i<20; ++i)
	{
		double a = (double)t0[i].QuadPart;
		double b = (double)t1[i].QuadPart;
		double d = b-a;
		cout << setprecision(24) << a << "-" << b << "=" << d << endl;
	}

	char s[1];
	cin.getline(s,1);
*/
	return 0;
}
