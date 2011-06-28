#include "stdafx.h"
#include "util.h"

wxString CUtil::str(const int n)
{
	char s[17];
	_itoa(n,s,10);
	return wxString(s);
}

BOOL CUtil::DirectoryExists(const wxString& strDir)
{
	DWORD dwAttrib = ::GetFileAttributes(strDir);

	if (dwAttrib==MAXDWORD)
		return FALSE;

	if (dwAttrib&FILE_ATTRIBUTE_DIRECTORY)
		return TRUE;

	return FALSE;
}

wxString CUtil::GetWindowsDirectory()
{
	wxString str;
	::GetWindowsDirectory(str.GetBuffer(MAX_PATH+1),MAX_PATH+1);
	str.ReleaseBuffer();

	if (!str.IsEmpty())
		if (str.Right(1)!="\\")
			str += "\\";

	return str;
}
