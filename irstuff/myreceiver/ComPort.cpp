#include "stdafx.h"
#include "ComPort.h"
#include "Timer.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

CComPort::CComPort(int nPort):
	m_hFile(INVALID_HANDLE_VALUE)
{
	m_strPortName.Format("COM%d",nPort);
}

CComPort::~CComPort()
{
	if (m_hFile!=INVALID_HANDLE_VALUE)
		::CloseHandle(m_hFile);
}

void CComPort::Open()
{
	m_hFile = ::CreateFile(m_strPortName,GENERIC_READ,0,NULL,OPEN_EXISTING,0,NULL);
	if (m_hFile==INVALID_HANDLE_VALUE)
	{
		CString str;
		str.Format("Could not open the communications (serial) port: %s",(LPCTSTR)m_strPortName);
		throw str;
	}

	if (!::SetCommMask(m_hFile,EV_RLSD))
		throw "Error setting COM events.";
}

BOOL CComPort::IsPulse()
{
	DWORD dwModemStatus;
	if (!::GetCommModemStatus(m_hFile,&dwModemStatus))
		throw "Could not get the serial port status";

	return (dwModemStatus&MS_RLSD_ON)!=MS_RLSD_ON;
}

void CComPort::Pulse(BOOL bOn)
{
	if (!::EscapeCommFunction(m_hFile,bOn?SETRTS:CLRRTS))
		throw "Error setting or clearing RTS.";
}

void CComPort::Pulse(BOOL bOn, int uS)
{
	CTimer timer;
	Pulse(bOn);
	int x;
	while ((x = timer.Peek())<uS)
	{
	}
//	CString s;
//	s.Format("%d/%d %.6f%%\n",x,uS,((double)(x*100))/uS);
//	::OutputDebugString(s);
}

BOOL CComPort::WaitForChange()
{
	COMSTAT comstat;
	DWORD err;
	if (!::ClearCommError(m_hFile,&err,&comstat))
		throw "Error ClearCommError.";

	DWORD events;
	if (!::WaitCommEvent(m_hFile,&events,0))
		throw "Error waiting for COM event.";

	if (!::GetCommMask(m_hFile,&events))
		throw "Error getting COM event.";

	return (events&EV_RLSD)!=EV_RLSD;
}

/*
#define COMM_MSRSHADOW 35
#define MSR_RLSD 0x80

{
	lpMSRShadow = (((LPBYTE)SetCommEventMask(1, 0)) + 35);
	bRLSD = (*lpMSRShadow) & MSR_RLSD;

	if (bRLSD)
}
*/