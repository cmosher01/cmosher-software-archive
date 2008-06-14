#include "stdafx.h"

static void getBuildTime(SYSTEMTIME* st)
{
	//__DATE__
	//Mmm dd yyyy
	//01234567890
	string sdat(__DATE__);

	//__TIME__
	//hh:mm:ss
	//01234567
	string stim(__TIME__);

	string mos("JanFebMarAprMayJunJulAugSepOctNovDec");

	st->wYear = atoi(sdat.substr(7,4).c_str());
	st->wMonth = mos.find(sdat.substr(0,3))/3+1;
	st->wDay = atoi(sdat.substr(4,2).c_str());
	st->wDayOfWeek = 0; // not set
	st->wHour = atoi(stim.substr(0,2).c_str());
	st->wMinute = atoi(stim.substr(3,2).c_str());
	st->wSecond = atoi(stim.substr(6,2).c_str());
	st->wMilliseconds = 0;
}

static string toHTTPTime(SYSTEMTIME* st)
{
	BOOL bOK(FALSE);
	string mos("JanFebMarAprMayJunJulAugSepOctNovDec");
	string wks("SunMonTueWedThuFriSat");

	FILETIME tLocal;
	bOK = ::SystemTimeToFileTime(st,&tLocal);
	if (!bOK)
	{
		DWORD e = ::GetLastError();
		cout << "error: " << e << endl;
		return "";
	}



	FILETIME tUTC;
	bOK = ::LocalFileTimeToFileTime(&tLocal,&tUTC);
	if (!bOK)
	{
		DWORD e = ::GetLastError();
		cout << "error: " << e << endl;
		return "";
	}



	SYSTEMTIME stUTC;
	bOK = ::FileTimeToSystemTime(&tUTC,&stUTC);
	if (!bOK)
	{
		DWORD e = ::GetLastError();
		cout << "error: " << e << endl;
		return "";
	}

	string sdtMo = mos.substr((stUTC.wMonth-1)*3,3);
	string sdtWk = wks.substr(stUTC.wDayOfWeek*3,3);

	//Thu, 13 Mar 2003 04:35:00 GMT
	char ssdt[30];
	::sprintf(ssdt,"%s, %02d %s %04d %02d:%02d:%02d GMT",
		sdtWk.c_str(),
		stUTC.wDay,
		sdtMo.c_str(),
		stUTC.wYear,
		stUTC.wHour,
		stUTC.wMinute,
		stUTC.wSecond);

	return string(ssdt);
}

//example http header times:
//Thu, 13 Mar 2003 04:35:00 GMT; length=1546
//Tuesday, 09-Dec-97 03:17:50 GMT (...)
//will always be GMT
void fromHTTPTime(const string& s, SYSTEMTIME* st)
{
	st->wDayOfWeek = 0; // not set
	st->wMilliseconds = 0; // not set

	string::size_type i, j;

	// find the first number, and that will
	// be the day
	i = s.find_first_of("0123456789");
	j = s.find_first_not_of("0123456789",i);
	st->wDay = atoi(s.substr(i,j-i).c_str());
	i = j;

	// month
	string mos("JanFebMarAprMayJunJulAugSepOctNovDec");
	i = s.find_first_of("ABCDEFGHIJKLMNOPQRSTUVWXYZ",i);
	st->wMonth = mos.find(s.substr(i,3))/3+1;
	i += 3;

	// year
	i = s.find_first_of("0123456789",i);
	j = s.find_first_not_of("0123456789",i);
	st->wYear = atoi(s.substr(i,j-i).c_str());
	if (st->wYear < 100)
	{
		if (st->wYear <= 60)
			st->wYear += 2000;
		else
			st->wYear += 1900;
	}
	i = j;

	//hour
	i = s.find_first_of("0123456789",i);
	j = s.find_first_not_of("0123456789",i);
	st->wHour = atoi(s.substr(i,j-i).c_str());
	i = j;

	//minute
	i = s.find_first_of("0123456789",i);
	j = s.find_first_not_of("0123456789",i);
	st->wMinute = atoi(s.substr(i,j-i).c_str());
	i = j;

	//second
	i = s.find_first_of("0123456789",i);
	j = s.find_first_not_of("0123456789",i);
	st->wSecond = atoi(s.substr(i,j-i).c_str());
	i = j;

	// ignore rest of input string
}

int main(int argc, char* argv[])
{
	SYSTEMTIME st;

	fromHTTPTime("Thu, 13 Mar 2003 04:35:00 GMT; length=1546",&st);
	cout << toHTTPTime(&st) << endl;
	fromHTTPTime("Thu, 3 Mar 03 04:35:00 GMT; length=1546",&st);
	cout << toHTTPTime(&st) << endl;
	fromHTTPTime("Thu, 03 Mar 2003 04:35:00 GMT; length=1546",&st);
	cout << toHTTPTime(&st) << endl;
	fromHTTPTime("13 Mar 97 04:35:00 GMT; length=1546",&st);
	cout << toHTTPTime(&st) << endl;
	fromHTTPTime("3 Mar 2003 04:35:00 GMT; length=1546",&st);
	cout << toHTTPTime(&st) << endl;
	fromHTTPTime("03 Mar 2003 04:35:00 GMT; length=1546",&st);
	cout << toHTTPTime(&st) << endl;
	fromHTTPTime("Tuesday, 09-Dec-97 03:17:50 GMT (...)",&st);
	cout << toHTTPTime(&st) << endl;

	string x;
	cin >> x;

	return 0;
}
