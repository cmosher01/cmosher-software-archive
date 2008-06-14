#include "stdafx.h"
#include "gedtree.h"
#include "gedtreedoc.h"
//#include "myado.h"

#pragma warning(push)
#pragma warning(disable:4146)
#import "c:\program files\common files\system\ado\msado15.dll" rename("EOF","ADODB_EOF")
#pragma warning(pop)
using namespace ADODB;

#include "getdataidl.h"
#include "getdata.h"

STDMETHODIMP CGetData::GetIndi(BSTR doc, int iIndi, _Recordset **ppRS)
{
	AFX_MANAGE_STATE(AfxGetStaticModuleState());
	try
	{
		ADODB::_RecordsetPtr rs(__uuidof(ADODB::Recordset));
		rs->CursorLocation = ADODB::adUseClient;
		rs->PutStayInSync(true);

		ADODB::_ConnectionPtr shape(__uuidof(ADODB::Connection));
		shape->Open("Provider=MSDataShape;Data Provider=NONE;","","",NULL);

		CString s(
		"SHAPE APPEND "
			"NEW adInteger AS iIndi, "
			"NEW adInteger AS bPrivate, "
			"NEW adVarChar(255) AS name, "
			"NEW adInteger AS iFather, "
			"NEW adVarChar(255) AS nameFather, "
			"NEW adInteger AS iMother, "
			"NEW adVarChar(255) AS nameMother, "
			"(( "
				"SHAPE APPEND "
				"NEW adInteger AS iIndi, "
				"NEW adVarChar(64) AS name, "
				"NEW adVarChar(64) AS date, "
				"NEW adVarChar(255) AS place, "
				"NEW adBSTR AS notes, "
				"NEW adBSTR AS citationtext, "
				"NEW adVarChar(255) AS source, "
				"NEW adInteger AS iSource) "
				"RELATE iIndi TO iIndi "
			") "
			"AS events, "
			"(( "
				"SHAPE APPEND "
				"NEW adInteger AS iIndi, "
				"NEW adVarChar(64) AS name, "
				"NEW adVarChar(255) AS value, "
				"NEW adVarChar(64) AS date, "
				"NEW adVarChar(255) AS place, "
				"NEW adBSTR AS notes, "
				"NEW adBSTR AS citationtext, "
				"NEW adVarChar(255) AS source, "
				"NEW adInteger AS iSource) "
				"RELATE iIndi TO iIndi"
			") "
			"AS attributes, "
			"(( "
				"SHAPE APPEND "
				"NEW adInteger AS iIndi, "
				"NEW adInteger AS iPartner, "
				"NEW adVarChar(64) AS name, "
				"(( "
					"SHAPE APPEND "
					"NEW adInteger AS iPartner, "
					"NEW adVarChar(64) AS name, "
					"NEW adVarChar(64) AS date, "
					"NEW adVarChar(255) AS place, "
					"NEW adBSTR AS notes, "
					"NEW adBSTR AS citationtext, "
					"NEW adVarChar(255) AS source, "
					"NEW adInteger AS iSource) "
					"RELATE iPartner TO iPartner "
				") "
				"AS events, "
				"(( "
					"SHAPE APPEND "
					"NEW adInteger AS iPartner, "
					"NEW adInteger AS iChild, "
					"NEW adVarChar(64) AS name, "
					"NEW adVarChar(64) AS birthdate, "
					"NEW adVarChar(255) AS birthplace, "
					"NEW adVarChar(64) AS deathdate, "
					"NEW adVarChar(255) AS deathplace, "
					"NEW adBoolean AS bMore) "
					"RELATE iPartner TO iPartner "
				") "
				"AS children) "
				"RELATE iIndi TO iIndi"
			") "
			"AS partners");
		rs->Open(_variant_t(s),_variant_t((IDispatch*)shape,true),
			ADODB::adOpenStatic,ADODB::adLockOptimistic,ADODB::adCmdText);

		ADODB::_RecordsetPtr rsEvents(__uuidof(ADODB::Recordset));
		ADODB::_RecordsetPtr rsAttrs(__uuidof(ADODB::Recordset));
		ADODB::_RecordsetPtr rsParts(__uuidof(ADODB::Recordset));
		ADODB::_RecordsetPtr rsPEvents(__uuidof(ADODB::Recordset));
		ADODB::_RecordsetPtr rsPChildren(__uuidof(ADODB::Recordset));



		CString strDocID(doc);

		CGedtreeDoc* pDoc = 0;
		POSITION pos = theApp.GetFirstDocTemplatePosition();
		CDocTemplate* pt = theApp.GetNextDocTemplate(pos);
		pos = pt->GetFirstDocPosition();
		while (pos)
		{
			CGedtreeDoc* pDocTest = (CGedtreeDoc*)pt->GetNextDoc(pos);
			if (strDocID.CompareNoCase(pDocTest->GetTitle())==0)
			{
				pDoc = pDocTest;
				break;
			}
		}
		if (!pDoc)
		{
			*ppRS = 0;
			return E_INVALIDARG;
		}



		if (0 <= iIndi && iIndi < pDoc->m_rIndividual.GetSize())
		{
			CIndividual* pIndi = pDoc->Individual(iIndi);

			rs->AddNew();

			rs->Fields->Item["iIndi"]->Value = _variant_t((long)iIndi);
			rs->Fields->Item["bPrivate"]->Value = _variant_t((long)pIndi->Private());
			rs->Fields->Item["name"]->Value = _bstr_t(pIndi->m_name.Name());
			long i = pIndi->m_iFather;
			rs->Fields->Item["iFather"]->Value = _variant_t(i);
			if (i >= 0)
			{
				CIndividual* pIndiR = pDoc->Individual(i);
				rs->Fields->Item["nameFather"]->Value = _bstr_t(pIndiR->m_name.Name());
			}
			i = pIndi->m_iMother;
			rs->Fields->Item["iMother"]->Value = _variant_t(i);
			if (i >= 0)
			{
				CIndividual* pIndiR = pDoc->Individual(i);
				rs->Fields->Item["nameMother"]->Value = _bstr_t(pIndiR->m_name.Name());
			}

			CArray<CEvt,CEvt&> revt;
			pIndi->GetSortedEvents(revt);

			rsEvents = rs->GetFields()->GetItem("events")->Value;
			for (int iEvt(0); iEvt<revt.GetSize(); iEvt++)
			{
				CEvt& evt = revt[iEvt];
				rsEvents->AddNew();
				rsEvents->Fields->Item["iIndi"]->Value = _variant_t((long)iIndi);
				rsEvents->Fields->Item["name"]->Value = _bstr_t(evt.m_strType);
				rsEvents->Fields->Item["date"]->Value = _bstr_t(evt.m_dvDate.Display(DATE_SHORTDATE));
				rsEvents->Fields->Item["place"]->Value = _bstr_t(evt.m_strPlace);
				rsEvents->Fields->Item["notes"]->Value = _bstr_t(evt.m_strNote);
				long iSour = evt.m_cita.m_iSource;
				rsEvents->Fields->Item["iSource"]->Value = _variant_t(iSour);
				if (iSour>=0)
				{
					rsEvents->Fields->Item["citationtext"]->Value = _bstr_t(evt.m_cita.m_strText);
					rsEvents->Fields->Item["source"]->Value = _bstr_t(evt.m_cita.Display());
				}
			}

			CArray<CAttr,CAttr&> rattr;
			pIndi->GetSortedAttrs(rattr);

			rsAttrs = rs->GetFields()->GetItem("attributes")->Value;
			for (int iAttr(0); iAttr<rattr.GetSize(); iAttr++)
			{
				CAttr& attr = rattr[iAttr];
				CEvt& evt = attr.m_evt;
				rsAttrs->AddNew();
				rsAttrs->Fields->Item["iIndi"]->Value = _variant_t((long)iIndi);
				rsAttrs->Fields->Item["name"]->Value = _bstr_t(attr.m_strType);
				rsAttrs->Fields->Item["value"]->Value = _bstr_t(attr.m_strValue);
				rsAttrs->Fields->Item["date"]->Value = _bstr_t(evt.m_dvDate.Display(DATE_SHORTDATE));
				rsAttrs->Fields->Item["place"]->Value = _bstr_t(evt.m_strPlace);
				rsAttrs->Fields->Item["notes"]->Value = _bstr_t(evt.m_strNote);
				long iSour = evt.m_cita.m_iSource;
				rsAttrs->Fields->Item["iSource"]->Value = _variant_t(iSour);
				if (iSour>=0)
				{
					rsAttrs->Fields->Item["citationtext"]->Value = _bstr_t(evt.m_cita.m_strText);
					rsAttrs->Fields->Item["source"]->Value = _bstr_t(evt.m_cita.Display());
				}
			}

			rsParts = rs->GetFields()->GetItem("partners")->Value;
			CArray<int,int> riSpouseToFamily;
			pIndi->GetSortedSpouseFamilies(riSpouseToFamily);
			for (int iPart(0); iPart<riSpouseToFamily.GetSize(); iPart++)
			{
				int iFami = riSpouseToFamily[iPart];
				CFamily& fami = pDoc->m_rFamily[iFami];

				int iPartner;
				if (fami.m_iHusband==iIndi)
					iPartner = fami.m_iWife;
				else
				{
//							ASSERT(fami.m_iWife==iIndi);
					iPartner = fami.m_iHusband;
				}

				rsParts->AddNew();
				rsParts->Fields->Item["iIndi"]->Value = _variant_t((long)iIndi);
				rsParts->Fields->Item["iPartner"]->Value = _variant_t((long)iPartner);
				if (iPartner>=0)
				{
					CIndividual* pIndiR = pDoc->Individual(iPartner);
					rsParts->Fields->Item["name"]->Value = _bstr_t(pIndiR->m_name.Name());
				}

				CArray<CEvt,CEvt&> revt;
				pIndi->GetSortedEvents(fami,revt);

				rsPEvents = rsParts->GetFields()->GetItem("events")->Value;
				for (int iEvt(0); iEvt<revt.GetSize(); iEvt++)
				{
					CEvt& evt = revt[iEvt];
					rsPEvents->AddNew();
					rsPEvents->Fields->Item["iPartner"]->Value = _variant_t((long)iPartner);
					rsPEvents->Fields->Item["name"]->Value = _bstr_t(evt.m_strType);
					rsPEvents->Fields->Item["date"]->Value = _bstr_t(evt.m_dvDate.Display(DATE_SHORTDATE));
					rsPEvents->Fields->Item["place"]->Value = _bstr_t(evt.m_strPlace);
					rsPEvents->Fields->Item["notes"]->Value = _bstr_t(evt.m_strNote);
					long iSour = evt.m_cita.m_iSource;
					rsPEvents->Fields->Item["iSource"]->Value = _variant_t(iSour);
					if (iSour>=0)
					{
						rsPEvents->Fields->Item["citationtext"]->Value = _bstr_t(evt.m_cita.m_strText);
						rsPEvents->Fields->Item["source"]->Value = _bstr_t(evt.m_cita.Display());
					}
				}

				rsPChildren = rsParts->GetFields()->GetItem("children")->Value;
				CArray<int,int> riChild;
				fami.GetSortedChildren(riChild);
				for (int iChildi(0); iChildi<riChild.GetSize(); iChildi++)
				{
					int iChild = riChild[iChildi];
					CIndividual* pIndiR = pDoc->Individual(iChild);
					rsPChildren->AddNew();

					rsPChildren->Fields->Item["iPartner"]->Value = _variant_t((long)iPartner);
					rsPChildren->Fields->Item["iChild"]->Value = _variant_t((long)iChild);
					rsPChildren->Fields->Item["name"]->Value = _bstr_t(pIndiR->m_name.Name());

					CString birthdate, birthplace;
					if (pIndiR->m_iBirth>=0)
					{
						birthdate = pIndiR->m_revt[pIndiR->m_iBirth].m_dvDate.Display(DATE_SHORTDATE);
						birthplace = pIndiR->m_revt[pIndiR->m_iBirth].m_strPlace;
					}
					rsPChildren->Fields->Item["birthdate"]->Value = _bstr_t(birthdate);
					rsPChildren->Fields->Item["birthplace"]->Value = _bstr_t(birthplace);

					CString deathdate, deathplace;
					if (pIndiR->m_iDeath>=0)
					{
						deathdate = pIndiR->m_revt[pIndiR->m_iDeath].m_dvDate.Display(DATE_SHORTDATE);
						deathplace = pIndiR->m_revt[pIndiR->m_iDeath].m_strPlace;
					}
					rsPChildren->Fields->Item["deathdate"]->Value = _bstr_t(deathdate);
					rsPChildren->Fields->Item["deathplace"]->Value = _bstr_t(deathplace);

					bool bMore = !!pIndiR->m_riChild.GetSize();
					rsPChildren->Fields->Item["bMore"]->Value = bMore;
				}
			}
		}

		if (rsEvents->State==ADODB::adStateOpen && !rsEvents->BOF)
		{
			rsEvents->Update();
		}
		if (rsAttrs->State==ADODB::adStateOpen && !rsAttrs->BOF)
		{
			rsAttrs->Update();
		}
		if (rsParts->State==ADODB::adStateOpen && !rsParts->BOF)
		{
			rsParts->Update();
		}
		if (rsPEvents->State==ADODB::adStateOpen && !rsPEvents->BOF)
		{
			rsPEvents->Update();
		}
		if (rsPChildren->State==ADODB::adStateOpen && !rsPChildren->BOF)
		{
			rsPChildren->Update();
		}
		if (rs->State==ADODB::adStateOpen && !rs->BOF)
		{
			rs->Update();
			rs->MoveFirst();
		}

		*ppRS = reinterpret_cast<ADODB::_Recordset*>(rs.Detach());
	}
	catch (_com_error& e)
	{
		_bstr_t s = e.Description();
		return e.Error();
	}

	return S_OK;
}

STDMETHODIMP CGetData::GetSource(BSTR doc, int iSource, _Recordset **ppRS)
{
	AFX_MANAGE_STATE(AfxGetStaticModuleState())
	try
	{
		ADODB::_RecordsetPtr rs(__uuidof(ADODB::Recordset));
		rs->CursorLocation = ADODB::adUseClient;
		rs->PutStayInSync(true);

		ADODB::_ConnectionPtr shape(__uuidof(ADODB::Connection));
		shape->Open("Provider=MSDataShape;Data Provider=NONE;","","",NULL);

		CString s(
		"SHAPE APPEND "
			"NEW adInteger AS iSource, "
			"NEW adVarChar(255) AS title, "
			"NEW adVarChar(255) AS author, "
			"NEW adVarChar(255) AS publ, "
			"NEW adBSTR AS text, "
			"NEW adVarChar(255) AS repository, "
			"NEW adVarChar(255) AS address");
		rs->Open(_variant_t(s),_variant_t((IDispatch*)shape,true),
			ADODB::adOpenStatic,ADODB::adLockOptimistic,ADODB::adCmdText);



		CString strDocID(doc);

		CGedtreeDoc* pDoc = 0;
		POSITION pos = theApp.GetFirstDocTemplatePosition();
		CDocTemplate* pt = theApp.GetNextDocTemplate(pos);
		pos = pt->GetFirstDocPosition();
		while (pos)
		{
			CGedtreeDoc* pDocTest = (CGedtreeDoc*)pt->GetNextDoc(pos);
			if (strDocID.CompareNoCase(pDocTest->GetTitle())==0)
			{
				pDoc = pDocTest;
				break;
			}
		}
		if (!pDoc)
		{
			*ppRS = 0;
			return E_INVALIDARG;
		}



		if (0 <= iSource && iSource < pDoc->m_rIndividual.GetSize())
		{
			CSource& source = pDoc->m_rSource[iSource];

			rs->AddNew();

			rs->Fields->Item["iSource"]->Value = _variant_t((long)iSource);
			rs->Fields->Item["title"]->Value = _bstr_t(source.m_strTitle);

			CString s(source.m_strAuthor);
			if (s.IsEmpty())
				s = "[unknown author]";
			rs->Fields->Item["author"]->Value = _bstr_t(s);

			rs->Fields->Item["publ"]->Value = _bstr_t(source.m_strPublish);
//			rs->Fields->Item["text"]->Value = _bstr_t(source.TextBlock(source.m_strText));
			rs->Fields->Item["text"]->Value = _bstr_t(source.m_strText);
			if (source.m_iRepository>=0)
			{
				rs->Fields->Item["repository"]->Value = _bstr_t(pDoc->m_rRepository[source.m_iRepository].m_strName);
				rs->Fields->Item["address"]->Value = _bstr_t(pDoc->m_rRepository[source.m_iRepository].m_strAddr);
			}
		}

		if (rs->State==ADODB::adStateOpen && !rs->BOF)
		{
			rs->Update();
			rs->MoveFirst();
		}

		*ppRS = reinterpret_cast<ADODB::_Recordset*>(rs.Detach());
	}
	catch (_com_error& e)
	{
		_bstr_t s = e.Description();
		return e.Error();
	}

	return S_OK;
}

static const int siz(10);
STDMETHODIMP CGetData::GetIndex(BSTR doc, int iLevel, int iBase, _Recordset **ppRS)
{
	AFX_MANAGE_STATE(AfxGetStaticModuleState())
	try
	{
		ADODB::_RecordsetPtr rs(__uuidof(ADODB::Recordset));
		rs->CursorLocation = ADODB::adUseClient;
		rs->PutStayInSync(true);

		ADODB::_ConnectionPtr shape(__uuidof(ADODB::Connection));
		shape->Open("Provider=MSDataShape;Data Provider=NONE;","","",NULL);

		CString s(
		"SHAPE APPEND "
			"NEW adInteger AS iBase, "
			"NEW adVarChar(255) AS name1, "
			"NEW adVarChar(255) AS name2, "
			"NEW adInteger AS birth, "
			"NEW adInteger AS death, "
			"NEW adInteger AS iUp, "
			"NEW adInteger AS iPrev, "
			"NEW adInteger AS iNext");

		rs->Open(_variant_t(s),_variant_t((IDispatch*)shape,true),
			ADODB::adOpenStatic,ADODB::adLockOptimistic,ADODB::adCmdText);

		CString strDocID(doc);

		CGedtreeDoc* pDoc = 0;
		POSITION pos = theApp.GetFirstDocTemplatePosition();
		CDocTemplate* pt = theApp.GetNextDocTemplate(pos);
		pos = pt->GetFirstDocPosition();
		while (pos)
		{
			CGedtreeDoc* pDocTest = (CGedtreeDoc*)pt->GetNextDoc(pos);
			if (strDocID.CompareNoCase(pDocTest->GetTitle())==0)
			{
				pDoc = pDocTest;
				break;
			}
		}
		if (!pDoc)
		{
			*ppRS = 0;
			return E_INVALIDARG;
		}


		iLevel = pDoc->mcLevel-iLevel-1; // 0, 1, 2, 3 becomes 3, 2, 1, 0

		int i;

		long iUp(-1), iPrev(-1), iNext(-1);
		// calculate these once and just store them in each row (for now)
		iUp = iBase;
		for (i = 0; i < iLevel+2; i++)
			iUp /= siz;
		for (i = 0; i < iLevel+2; i++)
			iUp *= siz;

		long iDiff(1);
		for (i = 0; i < iLevel+1; i++)
			iDiff *= siz;
		iPrev = iBase-iDiff;
		iNext = iBase+iDiff;
		if (iPrev<0)
			iPrev = -1;
		if (iNext>=pDoc->mrSrtIndi.GetSize())
			iNext = -1;

		if (iLevel)
		{
			// index page
			int iSkip(1);
			for (i = 0; i < iLevel; i++)
				iSkip *= siz;

			int iEnd(iSkip+iBase-1);
			int iPrevEnd(-1);
			for (i = 0; i < siz; i++)
			{
				if (iBase>=pDoc->mrSrtIndi.GetSize())
					iBase = pDoc->mrSrtIndi.GetSize()-1;

				if (iBase==iPrevEnd) break;

				if (iEnd>=pDoc->mrSrtIndi.GetSize())
					iEnd = pDoc->mrSrtIndi.GetSize()-1;

				CIndividual* pIndi1 = pDoc->Individual(pDoc->mrSrtIndi[iBase]);
				CString s1(pIndi1->m_name.Surname());
				s1 += ", "; s1 += pIndi1->m_name.GivenName();

				CIndividual* pIndi2 = pDoc->Individual(pDoc->mrSrtIndi[iEnd]);
				CString s2(pIndi2->m_name.Surname());
				s2 += ", "; s2 += pIndi2->m_name.GivenName();

				// insert "iBase through iEnd" into recordset
				rs->AddNew();
				rs->Fields->Item["iBase"]->Value = (long)iBase;
				rs->Fields->Item["name1"]->Value = _bstr_t(s1);
				rs->Fields->Item["name2"]->Value = _bstr_t(s2);
				rs->Fields->Item["iUp"]->Value = iUp;
				rs->Fields->Item["iPrev"]->Value = iPrev;
				rs->Fields->Item["iNext"]->Value = iNext;

				iPrevEnd = iEnd;

				iBase += iSkip;
				iEnd += iSkip;
			}
		}
		else
		{
			// leaf index page
			int iPrevBase(-1);
			for (i = 0; i < siz; i++)
			{
				if (iBase>=pDoc->mrSrtIndi.GetSize())
					iBase = pDoc->mrSrtIndi.GetSize()-1;

				if (iBase==iPrevBase) break;

				CIndividual* pIndi1 = pDoc->Individual(pDoc->mrSrtIndi[iBase]);
				CString s1(pIndi1->m_name.Surname());
				s1 += ", "; s1 += pIndi1->m_name.GivenName();

				int iIndi = pIndi1->m_i;

				long nBirth(0), nDeath(0);
				if (pIndi1->m_iBirth>=0)
					nBirth = pIndi1->m_revt[pIndi1->m_iBirth].m_dvDate.GetSimpleYear();
				if (pIndi1->m_iDeath>=0)
					nDeath = pIndi1->m_revt[pIndi1->m_iDeath].m_dvDate.GetSimpleYear();

				// insert indi into recordset
				rs->AddNew();
				rs->Fields->Item["iBase"]->Value = (long)iIndi;
				rs->Fields->Item["name1"]->Value = _bstr_t(s1);
				rs->Fields->Item["birth"]->Value = nBirth;
				rs->Fields->Item["death"]->Value = nDeath;
				rs->Fields->Item["iUp"]->Value = iUp;
				rs->Fields->Item["iPrev"]->Value = iPrev;
				rs->Fields->Item["iNext"]->Value = iNext;

				iPrevBase = iBase;

				iBase++;
			}
		}

		if (rs->State==ADODB::adStateOpen && !rs->BOF)
		{
			rs->Update();
			rs->MoveFirst();
		}

		*ppRS = reinterpret_cast<ADODB::_Recordset*>(rs.Detach());
	}
	catch (_com_error& e)
	{
		_bstr_t s = e.Description();
		return e.Error();
	}

	return S_OK;
}

STDMETHODIMP CGetData::GetDocs(_Recordset **ppRS)
{
	AFX_MANAGE_STATE(AfxGetStaticModuleState())
	try
	{
		ADODB::_RecordsetPtr rs(__uuidof(ADODB::Recordset));
		rs->StayInSync = true;

		ADODB::_ConnectionPtr shape(__uuidof(ADODB::Connection));
		shape->Open("Provider=MSDataShape;Data Provider=NONE;","","",NULL);

		CString s(
			"SHAPE APPEND "
			"NEW adVarChar(255) AS title, "
			"NEW adVarChar(255) AS path, "
			"NEW adLongVarChar AS notes");
		rs->Open(_variant_t(s),_variant_t((IDispatch*)shape,true),
			ADODB::adOpenStatic,ADODB::adLockOptimistic,ADODB::adCmdText);

		CGedtreeDoc* pDoc = 0;
		POSITION pos = theApp.GetFirstDocTemplatePosition();
		CDocTemplate* pt = theApp.GetNextDocTemplate(pos);
		pos = pt->GetFirstDocPosition();
		while (pos)
		{
			pDoc = (CGedtreeDoc*)pt->GetNextDoc(pos);

			rs->AddNew();

			rs->Fields->Item["title"]->Value = _bstr_t(pDoc->GetTitle().Left(255));
			rs->Fields->Item["path"]->Value = _bstr_t(pDoc->GetPathName().Left(255));
			rs->Fields->Item["notes"]->Value = _bstr_t(pDoc->m_head.m_strNote);
		}

		if (rs->State==ADODB::adStateOpen && !rs->BOF)
		{
			rs->Update();
			rs->MoveFirst();
		}

		*ppRS = reinterpret_cast<ADODB::_Recordset*>(rs.Detach());
	}
	catch (_com_error& e)
	{
		_bstr_t s = e.Description();
		return e.Error();
	}

	return S_OK;
}

STDMETHODIMP CGetData::GetPassword(BSTR *bstrPwd)
{
	AFX_MANAGE_STATE(AfxGetStaticModuleState())
	try
	{
		if (!bstrPwd)
			_com_issue_error(E_POINTER);

		_bstr_t pwd = theApp.m_strPassword;

		*bstrPwd = pwd.copy();
	}
	catch (_com_error& e)
	{
		_bstr_t s = e.Description();
		return e.Error();
	}

	return S_OK;
}
