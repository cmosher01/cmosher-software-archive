#ifndef header_fami
#define header_fami

#include "gedrecord.h"
#include "event.h"
class CIndividual;
class CMyDC;
#include "myrect.h"
#include <utility>

WX_DECLARE_OBJARRAY(wxPoint,wxArraywxPoint);

class CFamily : public CGedRecord
{
public:
	wxArrayCEvt m_revt;
	int m_iHusband;
	int m_iWife;
	R_int m_riChild;
	wxPoint m_pt1, m_pt2, m_ptP;
	wxPoint m_ptC1, m_ptC2;
	wxArraywxPoint m_rpointChild;
	int m_nScale;
	wxPoint m_ptsc1, m_ptsc2, m_ptscP;
	wxPoint m_ptscC1, m_ptscC2;
	wxArraywxPoint m_rpointscChild;
	MyRect m_rectBounds;
	MyRect m_rectScaledBounds;
	MyRect m_rectScaledBoundsDraw;
	BOOL m_bHidden;

	CFamily(CGedtreeDoc* pDoc = NULL, HTREEITEM hTreeItem = NULL);
	CFamily::CFamily(const CFamily& family);
	CFamily& operator=(const CFamily& o);
	void Calc();
	void SetScale(int nScale);
	double GetScale();
	void CalcScale();
	wxPoint CalcParPt(wxPoint pt1, wxPoint pt2);
	void OnDraw(CMyDC& dc);
	void ScaleForPrint(wxRect& rect);
	void ScaleForPrint(wxPoint& pt);
	BOOL AddChild(int iChild, BOOL bCheckFirst = FALSE);
	void CalcDraw();
	void CalcBounds();
	void CheckBounds(const wxPoint& pt);
	void GetFromTree();
	void AddEvent(HTREEITEM htiEvent, const wxString& strTypeTok);
	void Disconnect(int iIndividual);
	void Connect(CIndividual* pIndi0, CIndividual* pIndi1, int nRelation);
	void ConnectParent(CIndividual* pIndi);
	void ConnectChild(CIndividual* pIndi);
	void DebugRelations();
	void GetSortedChildren(R_int& riChild);
	int GetSimpleMarriage();
	int GetSimpleMarriageEnd();
//	void writeSVG(std::wostringstream& os);
//	void writeApplet(std::wostream& os);
	void showAsOf(CDate* pasof);
};
WX_DECLARE_OBJARRAY(CFamily,wxArrayCFamily);

#endif
