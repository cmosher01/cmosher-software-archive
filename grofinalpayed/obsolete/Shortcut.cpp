#include "stdafx.h"
#include "shortcut.h"
#include <shlobj.h>
#include <shellapi.h>

void CreateShortcut(LPCTSTR pszShortcutFile, LPCTSTR pszLink)
{
/*	HRESULT hres;
	IPersistFile *ppf = NULL;
	IShellLink *psl = NULL;

	try
	{
		// Create an IShellLink object and get a pointer to the IShellLink 
		// interface (returned from CoCreateInstance).
		hres = CoCreateInstance (CLSID_ShellLink, NULL, CLSCTX_INPROC_SERVER, IID_IShellLink, (void **)&psl);
		if (!SUCCEEDED (hres))
			throw _T("Can't find Shell Link COM class.");

		// Query IShellLink for the IPersistFile interface for 
		// saving the shortcut in persistent storage.
		hres = psl->QueryInterface (IID_IPersistFile, (void **)&ppf);
		if (!SUCCEEDED (hres))
			throw _T("Can't find Persist File interface.");

		// Set the path to the shortcut target.
		hres = psl->SetPath (pszShortcutFile);
		if (!SUCCEEDED (hres))
			throw _T("Can't set path for Persist File.");

		// Ensure that the string is ANSI. 
		WCHAR wsz[MAX_PATH];  
		MultiByteToWideChar(CP_ACP, 0, pszLink, -1, wsz, MAX_PATH);

		// Save the shortcut via the IPersistFile::Save member function.
		hres = ppf->Save (wsz, TRUE);
		if (!SUCCEEDED (hres))
			throw _T ("Can't write into the Persist File.");

	}
	catch (...)
	{
		if (ppf) ppf->Release();
		if (psl) psl->Release();
		throw;
	}
	if (ppf) ppf->Release();
	if (psl) psl->Release();
*/
} 
