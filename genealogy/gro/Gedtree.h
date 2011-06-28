#ifndef GEDTREE
#define GEDTREE 1

#include <iosfwd>
#include <string>

#include "appinfo.h"
#include "IndiClip.h"
#include "WebServer.h"

#define SetDispText(s) item.pszText = (LPTSTR)(LPCTSTR)(s);


class CGedtreeApp : public wxApp
{

public:
	CGedtreeApp();
	virtual ~CGedtreeApp();
	BOOL ConfirmDelete(const wxString& strItem);
//	void GetReg(const wxString& strKey,const wxString& strSub,wxString& strVal,const wxString& strDefault);
//	void GetReg(const wxString& strKey,const wxString& strSub,int& nVal,const int& nDefault);
//	void PutReg(const wxString& strKey,const wxString& strSub,const wxString& strVal);
//	void PutReg(const wxString& strKey,const wxString& strSub,const int& nVal);
//	void SaveShellFileTypes();
//	void RestoreShellFileTypes();
//	void PrepareForRegisterShellFileTypes();
//	void MoveKey(wxDocTemplate* pt, enum wxDocTemplate::DocStringIndex idocstr, HKEY hKeySrc, HKEY hKeyDst);
//	void RemoveKey(wxDocTemplate* pt, enum wxDocTemplate::DocStringIndex idocstr, HKEY hKeySrc);
//	void CopyKey(HKEY hKeySrc, HKEY hKeyDest, LPCTSTR name);
//	void DeleteKey(HKEY hKey, LPCTSTR name);
	void ResetAllDocuments(UINT flagsChanged = -1);
	void ResetFont();
	BOOL SaveAsUnicode();
	wxDocument* GetOpenDocument(LPCTSTR lpszFileName);
	BOOL LowMem();
	void Uninstall();
	void RemoveApp();
	void UninstallRegistry();
	void CreateStartMenuShortcut(BOOL bShowMessage = TRUE);
	void DeleteStartMenuShortcut(BOOL bShowMessage = TRUE);
	wxString GetStartMenuShortcutFileName();
	void AddTemplates();
	wxDC* GetDC();
	void SavePathsToReg();
	void OpenDocsFromReg();
	void OpenDocsFromAppDir();
	bool GetPathFromReg(int i, wxString& strPath);
	bool DoPrintDlg(bool bForceReset = false);
	void Copy(CGedtreeDoc* pDocFrom, CGedtreeDoc* pDocTo);
	void CloseClip();
	//TODO void GetDocs(std::wostream& os);
	//TODO void htmlFooter(std::wostream& os);
	CGedtreeDoc* FindDoc(const std::wstring& sid);
	// TODO void writecss(std::wostringstream& os, std::wstring& mime, std::wstring& lastmod, const std::wstring& ifmod);
	void webFile(const std::wstring& path, thinsock& sock, std::wstring& mime);

	CAppInfo m_info;
	HKEY m_hkey;
	wxFont m_font;
	wxFont m_fontBigBold;
	wxFont m_fontSmall;
	double m_fontRescale;
	wxDC* m_pDC;
	wxSize m_sizePage;
	wxString m_strAppDir;
	PRINTDLG m_printdlg;
	bool m_bShowPageBreaks;
	CGedtreeDoc* m_pClip;
	WebServer* m_pWebServer;
	ULARGE_INTEGER m_ulBuildTime;
	std::wstring m_strBuildTime;
	double m_nMaxIndiWidth;
	double m_nIndiBorderX;
	double m_nIndiBorderY;

	class groch : public WebServer::ContentHandler
	{
	public:
		groch() {}
		virtual wstring getHTML(const wstring& path, wstring& mime, thinsock& sock, wstring& lastmod, const wstring& ifmod);
//		virtual wstring getContype(const wstring& path, wstring& mime, thinsock& sock, wstring& lastmod, const wstring& ifmod);
	};
	groch mgroch;

// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CGedtreeApp)
	public:
	virtual BOOL InitInstance();
	virtual int ExitInstance();
	//}}AFX_VIRTUAL

// Implementation

	//{{AFX_MSG(CGedtreeApp)
	afx_msg void OnAppAbout();
	afx_msg void OnFileNew();
	afx_msg void OnFont();
	afx_msg void OnViewSubmitter();
	afx_msg void OnOptionsUnicode();
	afx_msg void OnOptionsPageBreaks();
	afx_msg void OnUpdateOptionsPageBreaks(wxUpdateUIEvent& pCmdUI);
	afx_msg void OnToolsHoliday();
	afx_msg void OnBirthCalc();
	afx_msg void OnUpdateFileSetup(wxUpdateUIEvent& pCmdUI);
	afx_msg void OnFileSetup();
	afx_msg void OnFileServe();
	afx_msg void OnFilePageSetup();
	afx_msg void OnUpdateFileRegister(wxUpdateUIEvent& pCmdUI);
	afx_msg void OnFileRegister();
	//}}AFX_MSG
	DECLARE_EVENT_TABLE()

	void GetRegKey();
	void InitFonts(LOGFONT* plf);
	void FontBigBold(LOGFONT* plf);
	void FontSmall(LOGFONT* plf);
	void WriteFontToRegistry(const LOGFONT* plf);
	void ReadFontFromRegistry();
	void WritePageSetupToRegistry();
	void ReadPageSetupFromRegistry();
	BOOL FirstInstance();
	void BuildAppPaths();
	void InitPageSetup();
	void ResetPageSize();
	void CreateClipboard();
};

DECLARE_APP(CGedtreeApp);

#endif
