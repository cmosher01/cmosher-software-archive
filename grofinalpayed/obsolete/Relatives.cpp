#include "stdafx.h"
#include "Relatives.h"

void CRelatives::GetNextTraverse(
	CRelatives* pRelsFather,
	CRelatives* pRelsMother,
	CRelatives* pRelsChildren)
	const
{
	if (bSib)
	{
		pRelsFather->bChild = TRUE;
		pRelsFather->bDo = TRUE;
		pRelsMother->bChild = TRUE;
		pRelsMother->bDo = TRUE;
	}
	if (bSibSps)
	{
		pRelsFather->bChildSps = TRUE;
		pRelsFather->bDo = TRUE;
		pRelsMother->bChildSps = TRUE;
		pRelsMother->bDo = TRUE;
	}

	if (bAnc)
	{
		pRelsFather->bSelf = TRUE;
		pRelsFather->bAnc = TRUE;
		pRelsFather->bDo = TRUE;
		pRelsMother->bSelf = TRUE;
		pRelsMother->bAnc = TRUE;
		pRelsMother->bDo = TRUE;
	}
	if (bAncSib)
	{
		pRelsFather->bSib = TRUE;
		pRelsFather->bAncSib = TRUE;
		pRelsFather->bDo = TRUE;
		pRelsMother->bSib = TRUE;
		pRelsMother->bAncSib = TRUE;
		pRelsMother->bDo = TRUE;
	}
	if (bAncSibSps)
	{
		pRelsFather->bSibSps = TRUE;
		pRelsFather->bAncSibSps = TRUE;
		pRelsFather->bDo = TRUE;
		pRelsMother->bSibSps = TRUE;
		pRelsMother->bAncSibSps = TRUE;
		pRelsMother->bDo = TRUE;
	}

	if (bFatherAnc)
	{
		pRelsFather->bSelf = TRUE;
		pRelsFather->bAnc = TRUE;
		pRelsFather->bDo = TRUE;
	}
	if (bFatherAncSib)
	{
		pRelsFather->bSib = TRUE;
		pRelsFather->bAncSib = TRUE;
		pRelsFather->bDo = TRUE;
	}
	if (bFatherAncSibSps)
	{
		pRelsFather->bSibSps = TRUE;
		pRelsFather->bAncSibSps = TRUE;
		pRelsFather->bDo = TRUE;
	}

	if (bFather)
	{
		pRelsFather->bSelf = TRUE;
		pRelsFather->bDo = TRUE;
	}
	if (bFatherSib)
	{
		pRelsFather->bSib = TRUE;
		pRelsFather->bDo = TRUE;
	}
	if (bFatherSibSps)
	{
		pRelsFather->bSibSps = TRUE;
		pRelsFather->bDo = TRUE;
	}

	if (bMotherAnc)
	{
		pRelsMother->bSelf = TRUE;
		pRelsMother->bAnc = TRUE;
		pRelsMother->bDo = TRUE;
	}
	if (bMotherAncSib)
	{
		pRelsMother->bSib = TRUE;
		pRelsMother->bAncSib = TRUE;
		pRelsMother->bDo = TRUE;
	}
	if (bMotherAncSibSps)
	{
		pRelsMother->bSibSps = TRUE;
		pRelsMother->bAncSibSps = TRUE;
		pRelsMother->bDo = TRUE;
	}

	if (bMother)
	{
		pRelsMother->bSelf = TRUE;
		pRelsMother->bDo = TRUE;
	}
	if (bMotherSib)
	{
		pRelsMother->bSib = TRUE;
		pRelsMother->bDo = TRUE;
	}
	if (bMotherSibSps)
	{
		pRelsMother->bSibSps = TRUE;
		pRelsMother->bDo = TRUE;
	}

	if (bChild)
	{
		pRelsChildren->bSelf = TRUE;
		pRelsChildren->bDo = TRUE;
	}

	if (bChildSps)
	{
		pRelsChildren->bSps = TRUE;
		pRelsChildren->bDo = TRUE;
	}

	if (bDesc)
	{
		pRelsChildren->bSelf = TRUE;
		pRelsChildren->bDesc = TRUE;
		pRelsChildren->bDo = TRUE;
	}

	if (bDescSps)
	{
		pRelsChildren->bSps = TRUE;
		pRelsChildren->bDescSps = TRUE;
		pRelsChildren->bDo = TRUE;
	}

	/*{
		// for debugging
#define out(b) if (b) {s.Format(#b ","); OutputDebugString(s);}

		CString s;

		out(bAnc);
		out(bAncSib);
		out(bAncSibSps);

		out(bFather);
		out(bFatherSib);
		out(bFatherSibSps);

		out(bFatherAnc);
		out(bFatherAncSib);
		out(bFatherAncSibSps);

		out(bMother);
		out(bMotherSib);
		out(bMotherSibSps);

		out(bMotherAnc);
		out(bMotherAncSib);
		out(bMotherAncSibSps);

		out(bSelf);

		out(bSps);
		out(bSib);
		out(bSibSps);

		out(bChild);
		out(bChildSps);

		out(bDesc);
		out(bDescSps);

		OutputDebugString("-> ");

		if (*bDoFather) {s.Format("Father,"); OutputDebugString(s);}
		if (*bDoMother) {s.Format("Mother,"); OutputDebugString(s);}
		if (*bDoChildren) {s.Format("Children,"); OutputDebugString(s);}

		OutputDebugString("\n");
	}*/
}
