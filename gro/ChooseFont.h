#if 0 

#pragma once
// ChooseFont.h : header file
//

/////////////////////////////////////////////////////////////////////////////
// CChooseFont dialog

class CChooseFont : public wxFontDialog
{

public:
	CChooseFont(LPLOGFONT lplfInitial = NULL,
		DWORD dwFlags = CF_EFFECTS | CF_SCREENFONTS,
		wxDC* pdcPrinter = NULL,
		wxWindow* pParentWnd = NULL);
#ifndef _AFX_NO_RICHEDIT_SUPPORT
	CChooseFont(const CHARFORMAT& charformat,
		DWORD dwFlags = CF_SCREENFONTS,
		wxDC* pdcPrinter = NULL,
		wxWindow* pParentWnd = NULL);
#endif

public:
	bool m_bScale;

protected:
	virtual BOOL OnInitDialog();
	virtual void OnOK();
	DECLARE_EVENT_TABLE()
};

#endif
#define CChooseFont wxFontDialog
