
public class Freq
{
	public static final int freq[] =
	{
		20,
		25,
		31,
		38,
		47,
		58,
		70,
		85,
		102,
		122,
		144,
		171,
		200,
		234,
		271,
		314,
		361,
		414,
		472,
		537,
		609,
		688,
		775,
		871,
		976,
		1090,
		1215,
		1351,
		1499,
		1660,
		1835,
		2024,
		2229,
		2450,
		2689,
		2946,
		3224,
		3522,
		3842,
		4186,
		4555,
		4951,
		5374,
		5827,
		6311,
		6828,
		7380,
		7969,
		8597,
		9265,
		9976,
		10732,
		11536,
		12390,
		13297,
		14259,
		15279,
		16360,
		17505,
		18717,
		20000,
	};

	public static final int iref = freq.length/2;

	public static int next(final int i)
	{
		int j = i;
		++j;
		if (j >= Freq.freq.length)
		{
			j = Freq.freq.length-1;
		}
		return j;
	}

	public static int prev(final int i)
	{
		int j = i;
		--j;
		if (j < 0)
		{
			j = 0;
		}
		return j;
	}
}
