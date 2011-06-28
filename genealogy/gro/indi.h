#ifndef header_indi
#define header_indi

#include "name.h"
#include "event.h"
#include "attr.h"
#include "datevalue.h"
#include "gedrecord.h"
#include "myrect.h"
#include <list>
#include <utility>

class CChildFrame;
class CFamily;
class CMyDC;

class CIndividual : public CGedRecord
{
public:
	CName m_name;
	int m_nSex; //0=unknown,1=male,2=female
	wxArrayCEvt m_revt;
	int m_iBirth;
	int m_iDeath;
	wxArrayCAttr m_rattr;
	wxString m_strTreeDisplay;
	wxString m_strBirthDisplay;
	wxString m_strDeathDisplay;
//	wxString m_strEllipsisDisplay;
//	wxString m_strBirthXML;
//	wxString m_strDeathXML;
	MyRect m_rectFrame;
	MyRect m_rectText;
	int m_nScale;
	MyRect m_rectScaledFrame;
	MyRect m_rectScaledFrameDraw;
	MyRect m_rectScaledText;
	BOOL m_bSelected;
	BOOL m_bHidden;
	BOOL m_bMark;
	int m_iFather;
	int m_iMother;
	R_int m_riSpouse;
	R_int m_riChild;
	R_int m_riSpouseToFamily;
	int m_iChildToFamily;
	CChildFrame* m_pFrame;
	CChildFrame* m_pPedigree;
	int m_nGrid;
	int m_nLevel;
	CIndividual* m_pMale;
	int m_maxmale;

	CIndividual(CGedtreeDoc* pDoc = NULL, HTREEITEM hTreeItem = NULL);
	~CIndividual();
	CIndividual(const CIndividual& o);
	CIndividual& operator=(const CIndividual& o);
	wxString Name();
	wxString Ident();
	void Calc();
	void CalcFull();
	void PlaceAt(const wxPoint& point);
	void ResetWidth();
	void GridRect(wxRect& rect);
	BOOL MoveToIsInBounds(wxPoint& pt);
	void MoveTo(const wxPoint& point);
	int SetScale(int nScale);
	double GetScale();
	void CalcScale();
	void OnDraw(CMyDC& dc);
	wxRect GetFrameRect();
	wxRect GetUnscaledFrameRect();
	BOOL HitTest(const wxPoint& point);
	BOOL HitTest(const wxRect& rect);
	BOOL Select(BOOL bSelect = TRUE);
	void Shift(const wxSize& sizShift);
	void ShiftUnscaled(const wxSize& sizShift);
	BOOL AddSpouse(int iSpouse, BOOL bCheckFirst = FALSE);
	BOOL AddChild(int iChild, BOOL bCheckFirst = FALSE);
	BOOL AddSpouseToFamily(int iSpouseToFamily, BOOL bCheckFirst = FALSE);
	void Clean(int nDepth);
	void CalcFamilies();
	void OpenView();
	void OpenPedigree();
	void AddEvent(HTREEITEM htiEvent, const wxString& strTypeTok);
	void AddAttr(HTREEITEM htiAttr, const wxString& strTypeTok, const wxString& strValue);
	BOOL GetFromTree();
	wxPoint GetXY(const wxString& str);
	wxString GetXY(const wxPoint& pt);
	void ScaleForPrint(wxRect& rect);
	void Disconnect();
	void SetSex(int nSex);
	wxString Sex();
	void DebugRelations();
	wxString GetWebPage(wxFile& tpl, const wxString& sDocID = "");
	wxString GetRTFPage(wxFile& tpl);
	wxString GetIndiLink(int iIndi, const wxString& sDocID = "");
	wxString GetIndiRTF(int iIndi);
	wxString GetIndiPageRef(int iIndi);
	wxString GetSourLink(int iSour, const wxString& strCita, const wxString& sDocID = "");
	wxString Cell(const wxString& str);
	BOOL Private();
	virtual wxString GetLinkText() { return Name(); }
	void GetSortedEvents(wxArrayCEvt& revt);
	void GetSortedEvents(CFamily& fami, wxArrayCEvt& revt);
	void GetSortedAttrs(wxArrayCAttr& rattr);
	wxString Census(const wxArrayCDate& rdateCensusDay);
	wxString AllPlaces();
	wxString GetAnomalies();
	int GetSimpleBirth();
	void GetSortedSpouseFamilies(R_int& riSpouseToFamily);
	void setLevel(int i = 0);
	void setSeqWithSpouses(double& seq, std::pair<double,double> lev_bounds[], bool left, std::list<CIndividual*>& rptoclean = std::list<CIndividual*>());
	bool setMaxMaleIf(int n) { bool is = n>m_maxmale; if (is) m_maxmale = n; return is; }
	void markWithSpouses();
	void setRootWithSpouses(CIndividual* proot);
	CIndividual* GetLeftSpouse();
	CIndividual* GetRightSpouse();
//	void writeSVG(std::wostringstream& os, int pt);
//	void writeApplet(std::wostream& os);
	bool wasMarriedBy(int ymd);
	int MostRecentSpouse(int ymd);
	void showAsOf(CDate* pasof);
	void WriteDescent(wxFile& f, int indiNum, std::list<CIndividual*>& todo);
};
WX_DECLARE_OBJARRAY(CIndividual,wxArrayCIndividual);

#endif
