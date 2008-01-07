package disk;

/*
 * Created on Sep 12, 2007
 */
/**
 * Emulates the arm stepper motor in the Disk ][.
 * This implementation differs from the actual Disk ][ in
 * that it rounds half- and quarter-tracks down to the
 * next whole track. Also, this emulator moves the arm
 * instantaneously, whereas the Disk ][ arm would actually
 * take some time to reach its new position (this would
 * cause a difference if the state of the magnets changed
 * during this interval). 
 *
 * @author Chris Mosher
 */
class StepperMotor
{
	private static final int QTRACKS = Drive.TRACKS_PER_DISK << 2;

	private int quarterTrack = 0x11 << 2; // start in the middle of the disk... why not?

	private int pos; // 0 - 7

	private int mags;

	private static int[] mapMagPos = {-1,0,2,1,4,-1,3,2,6,7,-1,0,5,6,4,-1};
	/*
	mags ps magval
	3210
	---- -- ------
	0001 0  1
	0011 1  3
	0010 2  2
	0110 3  6
	0100 4  4
	1100 5  C
	1000 6  8
	1001 7  9

	(strange, but defined)
	1011 0  B
	0111 2  7
	1110 4  E
	1101 6  D

	(undefined, i.e., no movement)
	0000 ?  0
	0101 ?  5
	1010 ?  A
	1111 ?  F
	*/

	void setMagnet(final int magnet, final boolean on)
	{
		if (magnet < 0 || 4 <= magnet)
		{
			throw new IllegalStateException();
		}

		final int mask = 1 << magnet;
		if (on)
		{
			this.mags |= mask;
		}
		else
		{
			this.mags &= ~mask;
		}
		final int newPos = StepperMotor.mapMagPos[this.mags];
		int d = 0;
		if (newPos >= 0)
		{
			d = calcDeltaPos(this.pos,newPos);
			this.pos = newPos;

			this.quarterTrack += d;
			constrainTrack();
		}

//		System.out.print(" ARM: magnet "+magnet+" "+(on?"on ":"off"));
//		System.out.print(" ["+
//			((mags&1)>0?"*":".")+
//			((mags&2)>0?"*":".")+
//			((mags&4)>0?"*":".")+
//			((mags&8)>0?"*":".")+
//			"]");
//		if (d != 0)
//		{
//			System.out.print(" track "+HexUtil.byt((byte)(this.quarterTrack >> 2)));
//			int fract = this.quarterTrack & 3;
//			if (fract != 0)
//			{
//				System.out.print(fract == 1 ? " +.25" : fract == 2 ? " +.5" : " +.75");
//			}
//		}
//		System.out.println();
	}

	private void constrainTrack()
	{
		if (this.quarterTrack < 0)
		{
			this.quarterTrack = 0;
		}
		else if (QTRACKS <= this.quarterTrack)
		{
			this.quarterTrack = QTRACKS-1;
		}
	}

	private static int calcDeltaPos(final int cur, final int next)
	{
		int d = next-cur; // -7 to +7

		if (d==4 || d==-4)
		{
			d = 0;
		}
		else if (d>4)
		{
			d -= 8;
		}
		else if (d<-4)
		{
			d += 8;
		}

		return d;
	}

	public int getTrack()
	{
		return this.quarterTrack >> 2;
	}
}
