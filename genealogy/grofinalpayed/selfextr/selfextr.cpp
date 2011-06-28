#include "resource.h"
#include <windows.h>
#include <tchar.h>
#include <shlobj.h>

static void ExtractEnclosure();
static void ExtractEnclosure(WORD id, const char* filename);

#include "bzlib.h"

class CRes
{
	HGLOBAL m_glob;
	DWORD m_c;

public:
	class Error { };

	CRes(const char *resType, WORD resID)
	{
		HRSRC rs = ::FindResource(::GetModuleHandle(0),MAKEINTRESOURCE(resID),resType);
		if (!rs)
			throw Error();

		m_c = ::SizeofResource(::GetModuleHandle(0),rs);
		if (!m_c)
			throw Error();

		m_glob = ::LoadResource(::GetModuleHandle(0),rs);
		if (!m_glob)
			throw Error();
	}

	~CRes()
	{
//		::UnlockResource(m_glob);
		::FreeResource(m_glob);
	}

	BYTE *GetPtr() const
	{
		BYTE *p = (BYTE*)::LockResource(m_glob);
		if (!p)
			throw Error();
		return p;
	}

	int GetSize() const
	{
		return m_c;
	}

/*
	void Save(const char* filename) const
	{
		BYTE *p = GetPtr();
		int c = GetSize();

		CStdioFile f(filename,CFile::modeCreate|CFile::modeWrite|CFile::typeBinary);
		f.Write(p,c);
	}
*/
};

int __stdcall WinMain(HINSTANCE h, HINSTANCE h2, LPSTR psz, int n)
{
	ExtractEnclosure();

	STARTUPINFO si;
	::ZeroMemory(&si, sizeof(si));
	si.cb = sizeof(si);
	PROCESS_INFORMATION pi;
	::CreateProcess(NULL, "gro.exe", NULL, NULL, TRUE, 0, NULL, NULL, &si, &pi);

	return 0;
}

char upc(char c)
{
	if ('a' <= c && c <= 'z')
		c += ('A'-'a');
	return c;
}

void ExtractEnclosure()
{
	LPMALLOC pMalloc;
	::SHGetMalloc(&pMalloc);

	LPITEMIDLIST pidl;
	::SHGetSpecialFolderLocation(0,CSIDL_INTERNET_CACHE,&pidl);

	char pathITemp[MAX_PATH];
	::SHGetPathFromIDList(pidl,pathITemp);

	pMalloc->Free(pidl);
	pMalloc->Release();

	char pathExe[MAX_PATH];
	::GetModuleFileName(0,pathExe,MAX_PATH);

	bool bInITemp(true);
	if (!pathITemp[0])
		bInITemp = false;
	else
	{
		int i(0);
		while (pathITemp[i] && pathExe[i] && bInITemp)
		{
			if (upc(pathITemp[i]) != upc(pathExe[i]))
				bInITemp = false;
			i++;
		}
	}
	if (bInITemp)
	{
		// user chose "run from current location" in browser
		if (::GetFileAttributes("C:\\Program Files\\GRO\\")!=FILE_ATTRIBUTE_DIRECTORY)
		{
			::CreateDirectory("C:\\Program Files\\GRO\\",0);
		}
		::SetCurrentDirectory("C:\\Program Files\\GRO\\");
	}

	ExtractEnclosure(IDR_ENCL,"gro.exe");
	ExtractEnclosure(IDR_ENCL2,"groaplt.jar");
#ifdef COMPAT98
	ExtractEnclosure(IDR_ENCL3,"unicows.dll");
	ExtractEnclosure(IDR_ENCL4,"mfc42lu.dll");
	ExtractEnclosure(IDR_ENCL5,"msluirt.dll");
	ExtractEnclosure(IDR_ENCL6,"mslup60.dll");
	ExtractEnclosure(IDR_ENCL7,"mslurt.dll");
#endif
}

void ExtractEnclosure(WORD id, const char* filename)
{
	CRes bin("BIN",id);

	BYTE *src = bin.GetPtr();
	unsigned int csrc = bin.GetSize();

	unsigned int cdest = 1024*1024; // 1 MB (must be big enough for uncompressed file)
	char *dest = new char[cdest];

	int e = BZ2_bzBuffToBuffDecompress(dest,&cdest,(char*)src,csrc,false,0);

	FILE *f = fopen(filename,"wb");
	fwrite(dest,cdest,1,f);
	fclose(f);

	delete [] dest;
}
