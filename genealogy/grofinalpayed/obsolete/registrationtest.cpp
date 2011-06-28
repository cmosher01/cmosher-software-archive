
inline bool RegistrationTest(LPCTSTR name, LPCTSTR key) //??? inline
{
	bool good(false);

	char* p = const_cast<char*>(name);
	int c(0);
	while (*p)
	{
		c++;
		p++;
	}

	char* my_key = new char[2*(c+1)];

	int m(0);
	while (*name)
	{
		char x = *name;
		char c1 = static_cast<char>('A'-1+
			((x&0x80)>>4)+
			((x&0x20)>>3)+
			((x&0x04)>>1)+
			((x&0x02)>>1));

		char c2 = static_cast<char>('F'-1+
			((x&0x40)>>3)+
			((x&0x10)>>2)+
			((x&0x08)>>2)+
			((x&0x01)>>0));

		my_key[m++] = c1;
		my_key[m++] = c2;

		name++;
	}
	my_key[m++] = '\0';

	char* my_test = my_key;
	while (*my_test)
	{
		if (*key && *key!=*my_test)
			good = false;
		if (*key)
			key++;
		my_test++;
	}

	delete [] my_key;

	return good;
}
