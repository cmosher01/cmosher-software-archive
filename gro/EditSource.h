// EditSource.h : header file
//

#include "sour.h"
//{{AFX_INCLUDES()
//#include "webbrowser.h"
//}}AFX_INCLUDES

/////////////////////////////////////////////////////////////////////////////
// CEditSource dialog

class CEditSource : public wxDialog
{
// Construction
public:
	CEditSource(wxWindow* pParent = NULL);   // standard constructor

// Dialog Data
	CSource m_sour;
	wxString m_strDefaultDir;
	//{{AFX_DATA(CEditSource)
	enum { IDD = IDD_SOURCE };
//	CTabCtrl	m_tab;
	wxTextCtrl*	m_editPubl;
	wxTextCtrl*	m_editTitle;
	wxTextCtrl*	m_editAuthor;
	wxTextCtrl*	m_editText;
	wxButton*	m_buttonOK;
	wxButton*	m_buttonCancel;
	wxButton*	m_buttonDelete;
	wxStaticText	m_staticRepo;
	wxString	m_strAuthor;
	wxString	m_strPubl;
	wxString	m_strText;
	wxString	m_strTitle;
	wxHtmlWindow* m_ie;
	//}}AFX_DATA


// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CEditSource)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:
	void DataToValue();
	void ValueToData();
	void SetStaticTitles();
	void Enable();
	void PositionControls(int cx, int cy);
	void OnRefresh();
	void SetDefaultDir();
	void RestoreDefaultDir();
	void SetDir(const wxString& s);

	// Generated message map functions
	//{{AFX_MSG(CEditSource)
	afx_msg void OnChangerepo();
	afx_msg void OnDelete();
	virtual BOOL OnInitDialog();
	afx_msg void OnSize(UINT nType, int cx, int cy);
	afx_msg void OnNewWindow2Ie(LPDISPATCH FAR* ppDisp, BOOL FAR* Cancel);
	afx_msg void OnSelchangeTab(NMHDR* pNMHDR, LRESULT* pResult);
	afx_msg void OnDestroy();
//	DECLARE_EVENTSINK_MAP()
	//}}AFX_MSG
	DECLARE_EVENT_TABLE()
};
