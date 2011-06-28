#ifndef _dirsave_
#define _dirsave

class CDirSave
{
	CString m_strInitDir;
	BOOL m_bFirst;

	static UINT APIENTRY OFNHookProc(
		HWND hdlg,	// handle to child dialog window
		UINT uiMsg,	// message identifier
		WPARAM wParam,	// message parameter
		LPARAM lParam 	// message parameter
		);

public:
	CDirSave(const CString& strInitDir = "");
	virtual ~CDirSave() {}
	BOOL GetSaveDirName(CString& strPath);
};

#endif
