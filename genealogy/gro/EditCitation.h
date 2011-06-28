// EditCitation.h : header file
//

#include "cita.h"

/////////////////////////////////////////////////////////////////////////////
// CEditCitation dialog

class CEditCitation : public wxDialog
{
// Construction
public:
	CEditCitation(wxWindow* pParent = NULL);   // standard constructor

// Dialog Data
	CCitation m_cita;
	//{{AFX_DATA(CEditCitation)
	enum { IDD = IDD_CITATION };
	wxButton*	m_buttonOK;
	wxButton*	m_buttonCancel;
	wxButton*	m_buttonDelete;
	wxTextCtrl*	m_editText;
	wxStaticText*	m_staticSource;
	wxString	m_strPage;
	int		m_nQuality;
	wxString	m_strText;
	//}}AFX_DATA


// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CEditCitation)
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

	// Generated message map functions
	//{{AFX_MSG(CEditCitation)
	afx_msg void OnChangesource();
	afx_msg void OnDelete();
	afx_msg void OnSize(UINT nType, int cx, int cy);
	virtual BOOL OnInitDialog();
	//}}AFX_MSG
	DECLARE_EVENT_TABLE()
};
