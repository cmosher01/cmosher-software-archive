#ifndef _appinfo_
#define _appinfo_

class CAppInfo
{
public:
	class CError
	{
	public:
		int nLastError;
		CError() { nLastError = ::GetLastError(); }
	};

	wxString m_strAppPath;
	wxString m_strAppDrive;
	wxString m_strAppDir;
	wxString m_strAppFName;
	wxString m_strAppExt;
	UINT m_nAppDriveType;
	BOOL m_bPermanent;

	// All info is file info (not product info)
	int m_nVersion; // example: 0x00010002 means version 1.2
	wxString m_strVersion; // example: "1.2"
	wxString m_strInternalName;
	wxString m_strDescription;
	wxString m_strCopyright;
	wxString m_strCompany;
	wxString m_strComments;
	wxString m_strName;

	CAppInfo();
	virtual ~CAppInfo();

	void Read();

protected:
	char* m_pVerBuf;
	wxString GetString(const wxString& strStringField);
};

#endif
