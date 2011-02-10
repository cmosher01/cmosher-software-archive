#ifndef STDAFX
#define STDAFX 1


#define wxUSE_GUI 1
#include <wx/wx.h>
#include <wx/wxchar.h>
#include <wx/docview.h>
#include <wx/stdpaths.h>
#include <wx/treebase.h>
#include <wx/treectrl.h>
#include <wx/listctrl.h>
#include <wx/file.h>
#include <wx/scrolwin.h>
#include <wx/html/htmlwin.h>

WX_DEFINE_ARRAY_INT(int,R_int);
WX_DECLARE_STRING_HASH_MAP(wxString,CMapSS);
WX_DECLARE_STRING_HASH_MAP(int,CMapSI);



typedef const wxChar* LPCTSTR;

#define afx_msg
#define AfxMessageBox(x) wxMessageBox(x)

class CDataExchange
{
public:
    bool m_bSaveAndValidate;
    void Fail();
};

#define DDX_Control(pDX,ID,m)
#define DDX_Radio(pDX,ID,m)
#define DDX_CBIndex(pDX,ID,m)
#define DDX_Text(pDX,ID,m)
#define DDV_MinMaxInt(pDX,ID,mn,mx)

#define HTREEITEM wxTreeItemId

#define DECLARE_DYNCREATE(x)
#define IMPLEMENT_DYNCREATE(x,y)





#include "resource.h"


#endif
