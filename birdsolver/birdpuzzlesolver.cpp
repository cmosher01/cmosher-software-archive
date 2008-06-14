#include <iostream.h>
#include <stdio.h>

enum { P, B, S, G };
enum { H, T };
enum { U, R, D, L };

static char f_colors[] = "PBSG";
static char f_parts[]="HT";

static char color(int i) { return f_colors[i]; }
static char part(int i) { return f_parts[i]; }

struct birdpiece
{
	int color;
	int part;
};

struct card
{
	birdpiece pieces[4];
};

struct position
{
	card c;
	int orient;
};

static position layout[9];

static card f_cards[] = 
{
	P,H,S,H,B,T,G,T,
	B,T,P,T,G,H,S,T,
	B,H,S,T,P,T,G,H,
	P,T,S,T,B,T,G,H,
	P,H,G,T,S,H,G,T,
	P,H,S,T,G,T,B,T,
	P,H,B,H,P,T,S,T,
	S,T,G,H,B,T,B,H,
	G,T,B,H,S,H,P,H
};

bool test();
bool testc(int c1, int d1, int c2, int d2);

#define LOOP(qq) \
	for (ic[qq]=0; ic[qq]<9; ic[qq]++) \
{ \
	{ \
		int overlap=0; \
		for (int k=0; k<qq; k++) \
		{ \
			if (ic[k]==ic[qq]) overlap=1; \
		} \
		if (overlap) continue; \
	} \
	for (io[qq]=0; io[qq]<4; io[qq]++) \
	{  \
	layout[qq].c = f_cards[ic[qq]]; \
	layout[qq].orient=io[qq];


int main()
{
	int ic[9]; // card index
	int io[9]; // orientation

	LOOP(0)
		cout << '*' << flush;
	LOOP(1)
		if (!testc(0,R,1,L)) continue;
	LOOP(2)
		if (!testc(1,R,2,L) ) continue;
	LOOP(3)
		if (!testc(0,D,3,U)) continue;
	LOOP(4)
		if (!testc(1,D,4,U)) continue;
		if (!testc(3,R,4,L)) continue;
	LOOP(5)
		if (!testc(4,R,5,L)) continue;
		if (!testc(2,D,5,U)) continue;
	LOOP(6)
		if (!testc(3,D,6,U)) continue;
	LOOP(7)
		if (!testc(6,R,7,L)) continue;
		if (!testc(4,D,7,U)) continue;
	LOOP(8)
			if (test())
			{
				for (int il=0; il<9; il++)
				{
					layout[il].c;
					layout[il].orient;
					cout << endl;
					cout
						<< color(layout[il].c.pieces[0].color) << " " << part(layout[il].c.pieces[0].part) << " "
						<< color(layout[il].c.pieces[1].color) << " " << part(layout[il].c.pieces[1].part) << " "
						<< color(layout[il].c.pieces[2].color) << " " << part(layout[il].c.pieces[2].part) << " "
						<< color(layout[il].c.pieces[3].color) << " " << part(layout[il].c.pieces[3].part) << " "
						<< layout[il].orient << "; ";
				}
				cout << endl;
			}



}}
}}
}}
}}
}}
}}
}}
}}
}}

/*
	for (ic[0]=0; ic[0]<9; ic[0]++)
	{
		for (k=0; k<0; k++
	}



	for (int ic=0; ic<9; ic++)
	{
		for (int ip = 0; ip<4; ip++)
		{
			for (int il=0; il<9; il++)
			{
				layout[il].c = f_cards[ic];
				layout[il].orient = ip;
			}
			if (test())
			{
				for (int il=0; il<9; il++)
				{
					layout[il].c;
					layout[il].orient;
					cout
						<< layout[il].c.pieces[0].color << " " << layout[il].c.pieces[0].part << " "
						<< layout[il].c.pieces[1].color << " " << layout[il].c.pieces[0].part << " "
						<< layout[il].c.pieces[2].color << " " << layout[il].c.pieces[0].part << " "
						<< layout[il].c.pieces[3].color << " " << layout[il].c.pieces[0].part << " "
						<< layout[il].orient << "; ";
				}
				cout << endl;
			}
			else
			{
			}
		}
	}
	*/
//	getchar();
	return 0;
}

bool test()
{
	return
		testc(0,R,1,L) &&
		testc(1,R,2,L) &&
		testc(3,R,4,L) &&
		testc(4,R,5,L) &&
		testc(6,R,7,L) &&
		testc(7,R,8,L) &&
		testc(0,D,3,U) &&
		testc(3,D,6,U) &&
		testc(1,D,4,U) &&
		testc(4,D,7,U) &&
		testc(2,D,5,U) &&
		testc(5,D,8,U);
}

bool testc(int c1, int d1, int c2, int d2)
{
	position& p1 = layout[c1];
	position& p2 = layout[c2];
	birdpiece& b1 = p1.c.pieces[(d1+p1.orient)%4];
	birdpiece& b2 = p2.c.pieces[(d2+p2.orient)%4];
	return
		b1.color==b2.color &&
		b1.part != b2.part;
}
