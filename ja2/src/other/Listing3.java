/*
 * Created on Aug 3, 2007
 */
package other;

public class Listing3
{
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
/*
100 FOR Y = 0 TO 191
110 GOSUB 1000
120 FOR X = 0 TO 39
130 POKE BASE + X,Y
140 NEXT X,Y
150 FOR Y = 128 TO 191
160 GOSUB 1000
170 FOR X = 40 TO 47
180 POKE BASE + X,Y + 64
190 NEXT X,Y
200 END
*/
		for (int y = 0; y < 192; ++y)
		{
			for (int x = -25; x < 40; ++x)
			{
				int a = calcRowOffset(y,x);
				System.out.print(Integer.toHexString(a));
				System.out.print("    ");
			}
			System.out.println();
		}
	}

/*
1000 L = Y:S = 0:Q = 0
1010 S = INT (L / 64)
1020 L = L - S * 64
1030 Q = INT (L / 8)
1040 L = L - Q * 8
1050 BASE = 8192 + 1024 * L + 128 * Q + 40 * S
1060 RETURN
*/
	private static int calcRowOffset(final int row, final int col)
	{
		int n = row;
		final int s = n/0x40;
		n -= s*0x40;
		final int q = n/8;
		n -= q*8;
		final int base = 0x400*n+0x80*q+40*s;

		final int hp = (base >> 7) << 7;

		int a = base+col;
		if (a < hp)
		{
			a += 0x80;
		}
		return 0x2000+a;
	}
}
