// EditEvent.h : header file
//

/////////////////////////////////////////////////////////////////////////////
// CEditEvent dialog

#include "event.h"
#include "attr.h"

class CEditEvent : public wxDialog
{
// Construction
public:
	CEditEvent(BOOL bFamily = FALSE, BOOL bAttr = FALSE, wxWindow* pParent = NULL);   // standard constructor

// Dialog Data
	CEvt m_evt;
	CAttr m_attr;
	BOOL m_bFamily;
	BOOL m_bAttr;
	wxComboBox*	m_comboType;
	//{{AFX_DATA(CEditEvent)
	enum { IDD = IDD_EVENT };
	wxTextCtrl*	m_editPlace;
	wxButton*	m_buttonOK;
	wxButton*	m_buttonCancel;
	wxButton*	m_buttonDelete;
	wxTextCtrl*	m_editNote;
	wxStaticText	m_staticSource;
	wxStaticText	m_staticDate;
	wxTextCtrl*	m_editType;
	wxString	m_strPlace;
	wxString	m_strType;
	int		m_nType;
	wxString	m_strNote;
	//}}AFX_DATA


// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CEditEvent)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:
	void DataToValue();
	void ValueToData();
	void SetStaticTitles();
	void Enable();
	void FillEvents();
	void PositionControls(int cx, int cy);

	// Generated message map functions
	//{{AFX_MSG(CEditEvent)
	afx_msg void OnChangedate();
	afx_msg void OnSelchangeType();
	afx_msg void OnChangesource();
	afx_msg void OnDelete();
	afx_msg void OnSize(UINT nType, int cx, int cy);
	virtual BOOL OnInitDialog();
	//}}AFX_MSG
	DECLARE_EVENT_TABLE()
};
