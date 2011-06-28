#ifndef header_relatives
#define header_relatives

class CRelatives
{
public:
	BOOL bAnc;
	BOOL bAncSib;
	BOOL bAncSibSps;

	BOOL bFather;
	BOOL bFatherSib;
	BOOL bFatherSibSps;

	BOOL bFatherAnc;
	BOOL bFatherAncSib;
	BOOL bFatherAncSibSps;

	BOOL bMother;
	BOOL bMotherSib;
	BOOL bMotherSibSps;

	BOOL bMotherAnc;
	BOOL bMotherAncSib;
	BOOL bMotherAncSibSps;

	BOOL bSelf;

	BOOL bSps;
	BOOL bSib;
	BOOL bSibSps;

	BOOL bChild;
	BOOL bChildSps;

	BOOL bDesc;
	BOOL bDescSps;

	BOOL bDo;

	CRelatives() :

		bAnc(FALSE),
		bAncSib(FALSE),
		bAncSibSps(FALSE),

		bFather(FALSE),
		bFatherSib(FALSE),
		bFatherSibSps(FALSE),

		bFatherAnc(FALSE),
		bFatherAncSib(FALSE),
		bFatherAncSibSps(FALSE),

		bMother(FALSE),
		bMotherSib(FALSE),
		bMotherSibSps(FALSE),

		bMotherAnc(FALSE),
		bMotherAncSib(FALSE),
		bMotherAncSibSps(FALSE),

		bSelf(FALSE),

		bSps(FALSE),
		bSib(FALSE),
		bSibSps(FALSE),

		bChild(FALSE),
		bChildSps(FALSE),

		bDesc(FALSE),
		bDescSps(FALSE),

		bDo(FALSE)
		{ }

	void GetNextTraverse(
		CRelatives* pRelsFather,
		CRelatives* pRelsMother,
		CRelatives* pRelsChildren) const;
};

#endif
