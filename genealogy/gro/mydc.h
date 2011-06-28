#pragma once
#include "myrect.h"

class CMyDC
{
	wxDC* m_pDC;
	MyRect m_draw;

public:
	CMyDC(wxDC* pDC, const MyRect& draw) : m_pDC(pDC), m_draw(draw) {}
	virtual ~CMyDC() {}

	void DrawLine(const wxPoint& p1, const wxPoint& p2, bool bHidden = false);
	bool FixXY(wxPoint& p, const wxPoint& q);
	bool FixPoint(wxPoint& p, const wxPoint& q);
	void ClearBackground();
	void FrameRectGray(const MyRect& rect);
	void FrameRect(const MyRect& rect, bool bHidden = false);
	void DrawRectSel(const MyRect& rect, bool bSelected);
	void FrameRect3D(const MyRect& rect, bool bHidden = false);
	void DrawText(const wxString& s, const MyRect& rect);
	void DrawTextSel(const wxString& s, const MyRect& rect, bool bSelected, bool bHidden = false);
	void Drag(const MyRect& rect);
	void MakeDraw(MyRect& r);
	void MakeDraw(wxPoint& p);
	bool Print() { return !!m_pDC->IsPrinting(); }
	wxDC* GetDC() { return m_pDC; }
	bool Visible(const MyRect& rect);
};
