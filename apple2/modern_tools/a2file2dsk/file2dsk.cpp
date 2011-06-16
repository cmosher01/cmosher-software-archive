#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <io.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <windows.h>

typedef unsigned char byte;

void main(int argc, char* argv[]);
void run(int argc, char* argv[]);
long GetDate();
void writedsk(const void* buf, int c);
void convertPDto33();

#pragma pack(1)
struct DirEnt
{
	byte nStorageType_NameLen;
	char name[15];
	byte fileType;
	short blockKey;
	short cUsed;
	byte eof[3];
	long dateCreation;
	byte version;
	byte versionMin;
	byte access;
	union
	{
		struct
		{
			byte entryLen;
			byte cEntry;
		};
		short auxType;
	};
	union
	{
		struct
		{
			short cFile;
			short blockBitMap;
		};
		long dateLastMod;
	};
	union
	{
		short cBlock;
		short blockDirKey;
	};
};

static const int cBLOCK(0x200);
static const int cVOLBLOCKS(0x118);

static byte disk[cBLOCK*cVOLBLOCKS];
static byte* pDisk = disk;

void main(int argc, char* argv[])
{
	try
	{
		run(argc,argv);
	}
	catch (char* s)
	{
		fprintf(stderr,"%s.\n",s);
	}

	fprintf(stderr,"Press return to exit.\n");
	getchar();
}

void run(int argc, char* argv[])
{
	if (sizeof DirEnt !=0x27)
	{
		throw "Vol dir header is wrong size";
	}

	if (argc<=1 || (argv[1] && !argv[1][0]))
	{
		throw "Specify file to create ProDOS disk for";
	}

	struct _stat fst;
	int bad = _stat(argv[1],&fst);
	if (bad)
	{
		throw "Can't get input file info";
	}
	int cByte = fst.st_size;
	int cBlock = (cByte+cBLOCK-1)/cBLOCK;
	int storageType;
	if (cBlock<=0x1)
		storageType = 1;
	else if (cBlock<=0x100)
		storageType = 2;
//to do: implement tree files:
//	else if (cBlock<=0x10000)
//		storageType = 3;
	else
		throw "File too large. Must be <= 128K (131,072) bytes";

	FILE* fi = NULL;
	FILE* fo = NULL;

	try
	{
		FILE* fi = fopen(argv[1],"r");
		if (!fi)
			throw "Can't open input file";

		// copy infile path
		char name[_MAX_PATH];
		strcpy(name,argv[1]);
		*strchr(name,'.') = '\0';

		// get (truncated to 15 and uppercase) file name
		char tname[_MAX_PATH];
		strcpy(tname,name);
		tname[15] = '\0';
		_strupr(tname);

		// outfile (.dsk)
		char newfile[_MAX_PATH];
		strcpy(newfile,name);
		strcat(newfile,".dsk");
		FILE* fo = fopen(newfile,"w");
		if (!fo)
			throw "Can't open output file";

		// create and mod date for files in image
		long date = GetDate();

		_setmode(_fileno(fi),_O_BINARY);
		_setmode(_fileno(fo),_O_BINARY);

		// put empty blocks 0 and 1
		byte buf[cBLOCK];
		for (int i(0); i<cBLOCK; i++)
			buf[i] = 0;
		writedsk(buf,cBLOCK);
		writedsk(buf,cBLOCK);

		if (pDisk-disk!=cBLOCK*2)
			throw "Bad disk size (blocks 0-1)";

		// put vol dir (block 2)
		writedsk(buf,4); // back and fdw ptrs both zero

		DirEnt vdh;
		memset(&vdh,0,sizeof(vdh));
		vdh.nStorageType_NameLen =
			0xF0 | strlen(tname);
		strcpy(vdh.name,tname);
		vdh.dateCreation = date;
		vdh.access = 0xE3;
		vdh.entryLen = 0x27;
		vdh.cEntry = 13;
		vdh.cFile = 1;
		vdh.blockBitMap = 6;
		vdh.cBlock = cVOLBLOCKS;
		writedsk(&vdh,sizeof(vdh));

		DirEnt fde;
		memset(&fde,0,sizeof(fde));

		fde.nStorageType_NameLen =
			(storageType<<4) | strlen(tname);
		strcpy(fde.name,tname);
		fde.fileType = 6; // bin for now
		fde.blockKey = 7;
		fde.cUsed = cBlock+(storageType==2); // fix for tree files
		memcpy(&fde.eof,&cByte,3);
		fde.dateCreation = date;
		fde.access = 0xE3;
		fde.auxType = 0x800; // for bin or sys, load address (for txt, reclen)
		fde.dateLastMod = date;
		fde.blockDirKey = 2;
		writedsk(&fde,sizeof(fde));

		writedsk(buf,cBLOCK-(4+sizeof(vdh)+sizeof(fde))); // write rest of block 2

		if (pDisk-disk!=cBLOCK*3)
			throw "Bad disk size (blocks 0-2)";

		writedsk(buf,cBLOCK); // block 3
		writedsk(buf,cBLOCK); // block 4
		writedsk(buf,cBLOCK); // block 5



		// vol map of free space (block 6)
		// all zeros (entire disk used)
		writedsk(buf,cBLOCK);
	/* this is an attempt to use only blocks actually
	used by the file (and the system). It's still
	wrong; sometimes it writes an extra byte of zeros
	at the beginning.
		int cUsedVol = 7+fde.cUsed;
		int cFullBytesInMap = cUsedVol/8;
		if (cFullBytesInMap)
			writedsk(buf,cFullBytesInMap);

		int cBitsInMap = cUsedVol-cFullBytesInMap*8;
		BYTE nByte(0);
		for (i = 0; i<8-cBitsInMap; i++)
		{
			nByte <<= 1;
			nByte |= 1;
		}
		if (cBitsInMap)
			writedsk(&nByte,1);

		nByte = 0xFF;
		for (i = 0; i<35-(cFullBytesInMap+(cBitsInMap?2:1)); i++)
			writedsk(&nByte,1);

		writedsk(buf,cBLOCK-35); // rest of map (zeros)
	*/

		if (pDisk-disk!=cBLOCK*7)
			throw "Bad disk size (blocks 0-6)";

		if (storageType==2)
		{
			// write index block (block 7)

			// write LSBs
			short int b = 8;
			for (i = 0; i<cBlock; i++)
			{
				if (b==0x88) // skip track 11
					b += 8;
				writedsk(&b,1);
				b++;
			}
			writedsk(buf,cBLOCK/2-cBlock);

			// write MSBs
			b = 8;
			for (i = 0; i<cBlock; i++)
			{
				if (b==0x88) // skip track 11
					b += 8;
				writedsk(((char*)&b)+1,1);
				b++;
			}
			writedsk(buf,cBLOCK/2-cBlock);
		}

		if (pDisk-disk!=cBLOCK*8)
			throw "Bad disk size (blocks 0-7)";

		int ch, c(0);
		BOOL bSkip11(FALSE);
		while ((ch = getc(fi))!=EOF)
		{
			c++;
			if (!bSkip11 && pDisk-disk==0x11000)
			{
				// skip track 11
				for (int i(0); i<8; i++)
					writedsk(buf,cBLOCK);
				bSkip11 = TRUE;
			}
			writedsk(&ch,1);
		}
		if (c!=cByte)
			throw "Bad file size";

		int cRest = cBlock*cBLOCK-cByte;
		writedsk(buf,cRest); // write rest of last block

		if (pDisk-disk!=(fde.cUsed+7+(!!bSkip11)*8)*cBLOCK)
			throw "Error: disk image wrong size";

		// write rest of disk
		int cUsedVol = 7+fde.cUsed;
		for (i = 0; i<cVOLBLOCKS-(cUsedVol+(!!bSkip11)*8); i++)
			writedsk(buf,cBLOCK);

		if (pDisk-disk!=sizeof(disk))
			throw "Error: disk image wrong size";

		typedef byte sector[0x100];
		sector* sec = (sector*)disk;
		for (int tracknum(0); tracknum<0x23; tracknum++)
		{
			fwrite(sec,sizeof(sector),1,fo);
			sec += 0xE;
			for (int sectornum(1); sectornum<0xF; sectornum++)
			{
				fwrite(sec,sizeof(sector),1,fo);
				sec--;
			}
			sec += 0xF;
			fwrite(sec,sizeof(sector),1,fo);
			sec++;
		}
	}
	catch (...)
	{
		if (fo) fclose(fo);
		if (fi) fclose(fi);
		throw;
	}

	if (fo) fclose(fo);
	if (fi) fclose(fi);
}

long GetDate()
{
	SYSTEMTIME t;
	::GetLocalTime(&t);
	long n(0);
	n |= t.wYear+20;
	n <<= 4; n |= t.wMonth;
	n <<= 5; n |= t.wDay;
	n <<= 8; n |= t.wHour;
	n <<= 8; n |= t.wMinute;
	char x[4];
	x[3] = ((char*)&n)[1];
	x[2] = ((char*)&n)[0];
	x[1] = ((char*)&n)[3];
	x[0] = ((char*)&n)[2];
	n = *((long*)x);
	return n;
}

void writedsk(const void* buf, int c)
{
	memcpy(pDisk,buf,c);
	pDisk += c;
}
