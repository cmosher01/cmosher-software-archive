#pragma once

#include <algorithm>

class MyRect
{
public:
	int left, right, top, bottom;
	MyRect() : left(0), right(0), top(0), bottom(0) {}
	MyRect(const wxRect& r) { SetRect(r.GetLeft(),r.GetTop(),r.GetRight(),r.GetBottom()); }

	virtual ~MyRect() {}

	operator const wxRect() const { return wxRect(wxPoint(left,top),wxPoint(right,bottom)); }

	int Width() const { return abs(right-left); }
	int Height() const { return abs(bottom-top); }
	wxPoint TopLeft() const { return wxPoint(left,top); }
	wxPoint BottomRight() const { return wxPoint(right,bottom); }
	bool PtInRect(const wxPoint& p) const { return left <= p.x && p.x <= right && top <= p.y && p.y <= bottom; }
	bool RectInRect(const MyRect& r) const { return top <= r.bottom && r.top <= bottom && left <= r.right && r.left <= right; }
	wxPoint CenterPoint() const { return wxPoint((right+left)/2,(bottom+top)/2); }
	wxSize Size() const { return wxSize(Width(),Height()); }
	bool IsEmpty() const { return !Width() && !Height(); }

	void SetRect(int xleft, int ytop, int xright, int ybottom) { left = xleft; right = xright; top = ytop; bottom = ybottom; }
	void SetRectEmpty() { SetRect(0,0,0,0); }
	void InflateRect( int x, int y ) { left -= x; right += x; top -= y; bottom += y; }
	void InflateRect( const wxSize& size ) { left -= size.GetWidth(); right += size.GetWidth(); top -= size.GetHeight(); bottom += size.GetHeight(); }
	void InflateRect( int l, int t, int r, int b ) { left -= l; right += r; top -= t; bottom += b; }
	void operator |= (const MyRect& r)
	{
		if (r.IsEmpty())
			return;

		if (IsEmpty())
		{
			left = r.left;
			right = r.right;
			top = r.top;
			bottom = r.bottom;
		}
		else
		{
			left = std::min(left,r.left);
			right = std::max(right,r.right);
			top = std::min(top,r.top);
			bottom = std::max(bottom,r.bottom);
		}
	}
	void operator +=(const wxSize& s) { left += s.GetWidth(); right += s.GetWidth(); top += s.GetHeight(); bottom += s.GetHeight(); }
};
