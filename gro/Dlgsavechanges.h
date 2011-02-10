// DlgSaveChanges.h : header file
//

/////////////////////////////////////////////////////////////////////////////
// CDlgSaveChanges dialog

class CDlgSaveChanges : public wxDialog
{
// Construction
public:
	CDlgSaveChanges(wxString& strFileName, wxWindow* pParent = NULL);   // standard constructor

// Dialog Data
	//{{AFX_DATA(CDlgSaveChanges)
	enum { IDD = IDD_SAVECHANGES };
	wxString	m_strPrompt;
	//}}AFX_DATA


// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CDlgSaveChanges)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:

	// Generated message map functions
	//{{AFX_MSG(CDlgSaveChanges)
	afx_msg void OnYes();
	afx_msg void OnNo();
	//}}AFX_MSG
	DECLARE_EVENT_TABLE()

	wxString m_strFileName;
};
