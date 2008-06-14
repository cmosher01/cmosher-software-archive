#pragma once

class CComPort
{
	CString m_strPortName;
	HANDLE m_hFile;

public:
	CComPort(int nPort);
	virtual ~CComPort();

	void Open();
	BOOL IsPulse();
	BOOL WaitForChange();
	void Pulse(BOOL bOn);
	void Pulse(BOOL bOn, int milliseconds);
};
