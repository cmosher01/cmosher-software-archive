#ifndef _indiclip_
#define _indiclip_

class CGedtreeDoc;

class CIndiClip
{
public:
	CIndiClip() {}
	virtual ~CIndiClip() {}
	void Copy(CGedtreeDoc* pDocFrom);
	void Paste(CGedtreeDoc* pDocTo);

private:
	R_int m_riIndi;
	CGedtreeDoc* m_pDoc;
};

#endif
