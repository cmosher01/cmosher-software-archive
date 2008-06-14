// ipup.cpp : Defines the class behaviors for the application.
//

#include "stdafx.h"
#include "ipup.h"
#include "winsock2.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif



class LogFile
{
private:
	HANDLE f;
public:
	LogFile()
	{
		f = 0;
	}
	~LogFile()
	{
		Close();
	}
	void Open()
	{
		SYSTEMTIME tm;
		::GetLocalTime(&tm);
		CString sName;
		sName.Format("f:\\inetpub\\wwwroot\\webup\\%4.4d%2.2d%2.2d.log",tm.wYear,tm.wMonth,tm.wDay);
		f = ::CreateFile(sName,GENERIC_READ|GENERIC_WRITE,0,0,OPEN_ALWAYS,FILE_ATTRIBUTE_NORMAL,0);
		if (f==INVALID_HANDLE_VALUE)
			exit(::GetLastError());

		::SetFilePointer(f,0,0,FILE_END);
	}
	void Close()
	{
		if (f) ::CloseHandle(f);
		f = 0;
	}
	void Log(const char* s)
	{
		static char* dweek[] = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
		SYSTEMTIME tm;
		::GetLocalTime(&tm);
		CString st;
		st.Format("%4.4d/%2.2d/%2.2d %2.2d:%2.2d:%2.2d.%4.4d (%s): ",
			tm.wYear,
			tm.wMonth,
			tm.wDay,
			tm.wHour,
			tm.wMinute,
			tm.wSecond,
			tm.wMilliseconds,
			dweek[tm.wDayOfWeek]);
		DWORD w(0);
		::WriteFile(f,st,strlen(st),&w,0);
		::WriteFile(f,s,strlen(s),&w,0);
		::WriteFile(f,"\r\n",2,&w,0);
	}
};


/////////////////////////////////////////////////////////////////////////////
// CIpupApp

BEGIN_MESSAGE_MAP(CIpupApp, CWinApp)
	//{{AFX_MSG_MAP(CIpupApp)
		// NOTE - the ClassWizard will add and remove mapping macros here.
		//    DO NOT EDIT what you see in these blocks of generated code!
	//}}AFX_MSG
	ON_COMMAND(ID_HELP, CWinApp::OnHelp)
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CIpupApp construction

CIpupApp::CIpupApp()
{
	// TODO: add construction code here,
	// Place all significant initialization in InitInstance
}

/////////////////////////////////////////////////////////////////////////////
// The one and only CIpupApp object

CIpupApp theApp;

/////////////////////////////////////////////////////////////////////////////
// CIpupApp initialization

BOOL CIpupApp::InitInstance()
{
//	::Sleep(2000*60); // wait two minutes for startup

	while (true)
	{
		LogFile lf;
		lf.Open();

		// first see if all is OK.
		lf.Log("getting http://mosher.mine.nu/test/testconnection.html ...");
		CString sTestConnection = GetWebPage("http://mosher.mine.nu/test/testconnection.html");
		lf.Log("recieved: ");
		lf.Log(sTestConnection);
		if (sTestConnection=="Christopher Alan Mosher test web page")
		{
			lf.Log("web site is up.");
		}
		else
		{
			// get this machine's IP address
			// Keep looking every 10 seconds until we find it.
			// Count how many times we go through the loop without
			// being connected, and if it is more than 6 times (about
			// one minute), try to connect us.
			int times_off(0);
			bool bHave(false);
			CString sLocalIP;
			while (!bHave)
			{
				CString sPageLocalIP;
				try
				{
					lf.Log("getting IP address from http://checkip.dyndns.org/ ...");
					sPageLocalIP = GetWebPage("http://checkip.dyndns.org/");
				}
				catch (...)
				{
					lf.Log("error");
					sPageLocalIP.Empty();
				}
				int n1 = sPageLocalIP.Find("Current IP Address: ");
				if (n1>=0)
				{
					int n2 = sPageLocalIP.Find("<br>",n1);
					if (n2 >= 0)
					{
						sLocalIP = sPageLocalIP.Mid(n1+20,n2-n1-20);
						bHave = true;
					}
				}
				if (bHave)
				{
					lf.Log("IP address recieved ok");
				}
				else
				{
					lf.Log("couldn't get IP address");
					lf.Log("waiting 10 seconds...");
					lf.Close();
					::Sleep(10000);
					lf.Open();
					++times_off;
					if (times_off==6) // one minute has gone by, and still can't access internet
					{
						lf.Log("checking Linksys box:");
						// connect to the linksys box and check the PPPoE status
						lf.Log("getting http://192.168.1.1/Status.htm ...");
						CString s = GetWebPage("http://192.168.1.1/Status.htm",CString(),"","pherfs");
						int n1 = s.Find("Status: ");
						if (n1==-1)
						{
							// note, could be "page cannot be found"; need to check for this and do what????
							lf.Log("!!!!!wrong format of Linksys box Status page!!!!!");
							return FALSE;
						}
						int n2 = s.Find("</b>",n1+1);
						s = s.Mid(n1+8,n2-n1-8);
						// s is the PPPoE status
						lf.Log("status of PPPoE connection: ");
						lf.Log(s);
						if (s=="Connected")
						{
							// already connected to internet, so just try to
							// reset the linksys box
							lf.Log("resetting Linksys box...");
							CString s = GetWebPage("http://192.168.1.1/Gozila.cgi?exIP3=0",CString(),"","pherfs");
							lf.Log("waiting 30 seconds...");
							lf.Close();
							::Sleep(30000);
							lf.Open();
							// also try ipconfig /renew

							STARTUPINFO si;
							memset(&si,0,sizeof(si));
							si.cb = sizeof(si);
							si.dwFlags = STARTF_USESHOWWINDOW;
							si.wShowWindow = SW_HIDE;
							PROCESS_INFORMATION pi;
							lf.Log("executing ipconfig /renew...");
							if (!::CreateProcess(0,"ipconfig /renew",0,0,FALSE,0,0,0,&si,&pi))
							{
								lf.Log("!!!!!error doing ipconfig /renew!!!!!");
								return FALSE;
							}
							lf.Log("waiting 10 seconds...");
							lf.Close();
							::Sleep(10000);
							lf.Open();
						}
						else if (s=="Disconnected")
						{
							lf.Log("telling Linksys box to connect to PPPoE...");
							// disconnected, so click the "Connect" button, and then wait
							CString s = GetWebPage("http://192.168.1.1/Gozila.cgi?pppoeAct=1",CString(),"","pherfs");
						}
						else if (s=="Connecting")
						{
							lf.Log("Linksys box is trying to connect right now");
							// in the process of connecting,
							// so just keep waiting
						}
						else
						{
							lf.Log("!!!!!wrong format of Linksys box Status page (pppoe)!!!!!");
							return FALSE;
						}
						times_off = 0;
					}
				}
			}

			lf.Log("analyzing local IP address:");
			lf.Log(sLocalIP);
			long ipLocal = inet_addr(sLocalIP);
			if (ipLocal==INADDR_NONE)
			{
				lf.Log("!!!!!wrong format of local IP address!!!!!");
				return FALSE;
			}

			lf.Log("getting currently registered IP address at https://www.dyndns.org/services/dyndns/mosher.mine.nu ...");
			// get IP currently registered at dyndns and make sure it's the same as this machine's IP address
			CString sPageDyndns = GetWebPage("https://www.dyndns.org/services/dyndns/mosher.mine.nu","username=mosher&password=pherfs&__login=1","mosher","pherfs");
			int n1 = sPageDyndns.Find("IP in Database/DNS");
			if (n1==-1)
			{
				lf.Log("!!!!!wrong format of page at https://members.dyndns.org/nic/dyndns/!!!!!");
				return FALSE;
			}
//			for (int i(0); i<3; ++i)
				n1 = sPageDyndns.Find("<td>",n1+1);
			int n2 = sPageDyndns.Find("</td>",n1+1);
			CString sDyndnsIP = sPageDyndns.Mid(n1+6,n2-n1-7);

			lf.Log("analyzing currently registered IP address:");
			lf.Log(sDyndnsIP);

			long ipDyndns = inet_addr(sDyndnsIP);
			if (ipDyndns==INADDR_NONE)
			{
				lf.Log("!!!!!wrong format of Dyndns IP address!!!!!");
				return FALSE;
			}

			if (ipDyndns==ipLocal)
				lf.Log("local IP address already matches registered IP address; no action will be taken");
			else
			{
				lf.Log("local IP address is different than registered IP address");
				lf.Log("registering new local IP address now:");
				CString sResp = GetWebPage("http://members.dyndns.org/nic/update","system=dyndns&hostname=mosher.mine.nu","mosher","pherfs");
				lf.Log("recieved response:");
				lf.Log(sResp);
			}
		}

		lf.Log("waiting for 10 minutes...");
		lf.Close();
		::Sleep(1000*60*10); // wait 10 minutes and check again
	}

	return FALSE;
}

CString CIpupApp::GetWebPage(const CString& strURL, const CString& strFormData, const CString& strUser, const CString& strPass)
{
	CString sPage;
	CHttpConnection* pHTTPServer = 0;
	CHttpFile* pFile = 0;

	try
	{
		CInternetSession csiSession;

		// parse URL to get server/object/port
		DWORD dwServiceType;
		CString sServerName;
		CString sObject;
		INTERNET_PORT nPort;
		if (!::AfxParseURL(strURL,dwServiceType,sServerName,sObject,nPort))
			::AfxThrowInternetException(1);

		pHTTPServer = csiSession.GetHttpConnection(sServerName,nPort,strUser,strPass);

		int method(strFormData.IsEmpty() ? CHttpConnection::HTTP_VERB_GET : CHttpConnection::HTTP_VERB_POST);
		DWORD flags = INTERNET_FLAG_RELOAD|INTERNET_FLAG_NO_CACHE_WRITE;
		if (strURL.Left(5)=="https")
			flags |= INTERNET_FLAG_SECURE;
		pFile = pHTTPServer->OpenRequest(method,sObject,0,1,0,0,flags);

		if (method==CHttpConnection::HTTP_VERB_POST)
			pFile->SendRequest(CString("Content-Type: application/x-www-form-urlencoded"),(LPVOID)(LPCTSTR)strFormData, strFormData.GetLength()); 
		else
			pFile->SendRequest();

		CString sLine;
		while (pFile->ReadString(sLine))
			sPage += sLine;

		pFile->Close();
		delete pFile;

		pHTTPServer->Close();
		delete pHTTPServer;
	}
	catch (...)
	{
		if (pFile) pFile->Close();
		delete pFile;

		if (pHTTPServer) pHTTPServer->Close();
		delete pHTTPServer;

		throw;
	}

	return sPage;
}
