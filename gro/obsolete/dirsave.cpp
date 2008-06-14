#include "stdafx.h"
#include "dirsave.h"
//#include <dlgs.h>
#include <shlobj.h>

 /*
static CString* pstrInitDir;

UINT APIENTRY CDirSave::OFNHookProc(
	HWND hdlg,	// handle to child dialog window
	UINT uiMsg,	// message identifier
	WPARAM wParam,	// message parameter
	LPARAM lParam 	// message parameter
	)
{
	static BOOL bFirst;

	CWnd* pWnd = CWnd::FromHandle(::GetParent(hdlg));

	switch (uiMsg)
	{
	case WM_INITDIALOG:
		bFirst = TRUE;
		pWnd->CenterWindow();
		pWnd->SendMessage(CDM_HIDECONTROL,stc2,0);
		pWnd->SendMessage(CDM_HIDECONTROL,cmb1,0);
		pWnd->SendMessage(CDM_SETCONTROLTEXT,stc3,(LPARAM)_T("Folder:"));
	break;
	case WM_NOTIFY:
		LPNMHDR pd = (LPNMHDR)lParam;
		switch (pd->code)
		{
		case CDN_SELCHANGE:
			if (bFirst)
			{
				bFirst = FALSE;
				pWnd->SendMessage(CDM_SETCONTROLTEXT,edt1,(LPARAM)(LPCTSTR)*pstrInitDir);
				break;
			}
			//else fall through
		case CDN_FOLDERCHANGE:
			TCHAR dir[MAX_PATH];
			LRESULT nRet = pWnd->SendMessage(CDM_GETFOLDERPATH,sizeof(dir),(LPARAM)dir);
			if (nRet<0) dir[0] = '\0';
			if (*dir)
			{
				HWND hList;
				hList = pWnd->GetDlgItem(lst2)->GetSafeHwnd();
				hList = ::GetWindow(hList,GW_CHILD);
				int i = ::SendMessage(hList,LVM_GETNEXTITEM,(WPARAM)(int)-1,MAKELPARAM((UINT)LVNI_SELECTED,0));
				if (i==-1)
				{
				}
				else
				{
					TCHAR sel[MAX_PATH];
					LV_ITEM lvi;
					lvi.iItem = i;
					lvi.iSubItem = 0;
					lvi.mask = LVIF_TEXT;
					lvi.pszText = sel;
					lvi.cchTextMax = sizeof(sel);
					LRESULT n = ::SendMessage(hList,LVM_GETITEMTEXT,i,(LPARAM)&lvi);
					if (n>0)
					{
						if (dir[_tcslen(dir)-1]!=_T('\\'))
							_tcscat(dir,_T("\\"));
						_tcscat(dir,sel);
					}
				}
			}
			pWnd->SendMessage(CDM_SETCONTROLTEXT,edt1,(LPARAM)dir);
		};
	};

	return FALSE;
}
*/

CDirSave::CDirSave(const CString& strInitDir):
	m_strInitDir(strInitDir)
{
}

BOOL CDirSave::GetSaveDirName(CString& strPath)
{
	strPath.Empty();

	TCHAR  szDir[MAX_PATH];
	BOOL  fRet;
	TCHAR  szPath[MAX_PATH];
	LPITEMIDLIST pidl;
	LPITEMIDLIST pidlRoot;
	LPMALLOC lpMalloc;

	BROWSEINFO bi =
	{
		AfxGetMainWnd()->GetSafeHwnd(),
		NULL,
		szPath,
		_T("Select folder to create \"GRO\" folder in:"),
		0,//BIF_RETURNONLYFSDIRS,
		NULL, 0L, 0
	};

	if (0 != SHGetSpecialFolderLocation(HWND_DESKTOP, CSIDL_DESKTOP/*CSIDL_DRIVES*/, &pidlRoot))
		return FALSE;
	if (NULL == pidlRoot)
		return FALSE;

	// see Q179378 for help on initially selecting "program files" folder.
	bi.pidlRoot = pidlRoot;
	pidl = SHBrowseForFolder(&bi);

	if (NULL != pidl)
		fRet = SHGetPathFromIDList(pidl, szDir);
	else
		fRet = FALSE;

	// Get the shell's allocator to free PIDLs
	if (!SHGetMalloc(&lpMalloc) && (NULL != lpMalloc))
	{
		if (NULL != pidlRoot)
			lpMalloc->Free(pidlRoot);
		if (NULL != pidl)
			lpMalloc->Free(pidl);
		lpMalloc->Release();
	}

	if (fRet)
	{
		strPath = szDir;

		if (strPath.Right(1)!=_T("\\"))
			strPath += _T("\\");

		strPath += "GRO\\";
	}

	return fRet;
}

/*
BOOL CDirSave::GetSaveDirName(CString& strPath)
{
	strPath.Empty();

	TCHAR path[MAX_PATH] = _T("");

	OPENFILENAME ofn;
	ofn.lStructSize = sizeof(ofn);
	ofn.hwndOwner = AfxGetMainWnd()->GetSafeHwnd();
	ofn.hInstance = NULL;
	ofn.lpstrFilter = _T("show no files!\0show no files!\0\0");
	ofn.lpstrCustomFilter = NULL;
	ofn.nMaxCustFilter = 0;
	ofn.nFilterIndex = 0;
	ofn.lpstrFile = path;
	ofn.nMaxFile = MAX_PATH;
	ofn.lpstrFileTitle = NULL;
	ofn.nMaxFileTitle = 0;
	ofn.lpstrInitialDir = _T("C:\\");
	ofn.lpstrTitle = _T("Select Installation Folder");
	ofn.Flags =
		OFN_ENABLEHOOK|
		OFN_EXPLORER|
		OFN_HIDEREADONLY|
		OFN_NOTESTFILECREATE|
		OFN_NOVALIDATE|
		0;
	ofn.nFileOffset = 0;
	ofn.nFileExtension = 0;
	ofn.lpstrDefExt = NULL;
	ofn.lCustData = 0;
	ofn.lpfnHook = OFNHookProc;
	ofn.lpTemplateName = NULL;

	pstrInitDir = &m_strInitDir;

	BOOL bOK = ::GetSaveFileName(&ofn);

	if (bOK)
		strPath = path;

	return bOK;
}
*/
