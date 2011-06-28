// ConfirmDelete.h : header file
//

/////////////////////////////////////////////////////////////////////////////
// CConfirmDelete dialog

class CConfirmDelete : public wxDialog
{
// Construction
public:
	CConfirmDelete(const wxString& strItem, wxWindow* pParent = NULL);   // standard constructor

// Dialog Data
	//{{AFX_DATA(CConfirmDelete)
	enum { IDD = IDD_CONFIRMDELETE };
	wxString	m_strMessage;
	//}}AFX_DATA


// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CConfirmDelete)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:

	// Generated message map functions
	//{{AFX_MSG(CConfirmDelete)
	afx_msg void OnDelete();
	//}}AFX_MSG
	DECLARE_EVENT_TABLE()
};
