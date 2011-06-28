// HeadNotes.h : header file
//

/////////////////////////////////////////////////////////////////////////////
// CHeadNotes dialog

class CHeadNotes : public wxDialog
{
// Construction
public:
	CHeadNotes(wxWindow* pParent = NULL);   // standard constructor

// Dialog Data
	//{{AFX_DATA(CHeadNotes)
	enum { IDD = IDD_NOTES };
	wxButton*	m_buttonOK;
	wxButton*	m_buttonCancel;
	wxButton*	m_buttonDelete;
	wxTextCtrl*	m_editText;
	wxString	m_strText;
	//}}AFX_DATA


// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CHeadNotes)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:
	void PositionControls(int cx, int cy);

	// Generated message map functions
	//{{AFX_MSG(CHeadNotes)
	afx_msg void OnDelete();
	afx_msg void OnSize(UINT nType, int cx, int cy);
	virtual BOOL OnInitDialog();
	//}}AFX_MSG
	DECLARE_EVENT_TABLE()
};
