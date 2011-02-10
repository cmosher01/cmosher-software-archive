// EditName.h : header file
//

#include "name.h"

/////////////////////////////////////////////////////////////////////////////
// CEditName dialog

class CEditName : public wxDialog
{
// Construction
public:
	CEditName(wxWindow* pParent = NULL);   // standard constructor

// Dialog Data
	CName m_name;
	//{{AFX_DATA(CEditName)
	enum { IDD = IDD_NAME };
	wxTextCtrl*	m_editName;
	wxStaticText	m_staticSource;
	wxString	m_strName;
	//}}AFX_DATA


// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CEditName)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:
	void DataToValue();
	void ValueToData();
	void SetStaticTitles();

	// Generated message map functions
	//{{AFX_MSG(CEditName)
	afx_msg void OnChangesource();
	//}}AFX_MSG
	DECLARE_EVENT_TABLE()
};
