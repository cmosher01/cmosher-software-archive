/*
16 possible combinations of states of stepper motor magnets
and the corresponding position of the axle (0-7 rotation, ? means undefined)

3210 magnets (0 rotation == magnet 0)
--------
(normal)
0001 0
0011 1
0010 2
0110 3
0100 4
1100 5
1000 6
1001 7

(strange, but defined)
1011 0
0111 2
1110 4
1101 6

(undefined, i.e., no movement)
0000 ?
0101 ?
1010 ?
1111 ?


transition from state to state yields arm movement
ascending moves arm inward, toward higher tracks
descending moves arm outward, toward lower tracks

from state / to state / yields eighths of track movement (+/-)
---------------------------------
0 0  0
0 1  +1
0 2  +2
0 3  +3
0 4  ? (i.e., 0)
0 5  -3
0 6  -2
0 7  -1

1 1  0
1 2  +1
1 3  +2
1 4  +3
1 5  ? (i.e., 0)
1 6  -3
1 7  -2
1 0  -1

2 2  0
2 3  +1
2 4  +2
2 5  +3
2 6  ? (i.e., 0)
2 7  -3
2 0  -2
2 1  -1

3 3  0
3 4  +1
3 5  +2
3 6  +3
3 7  ? (i.e., 0)
3 0  -3
3 1  -2
3 2  -1

4 4  0
4 5  +1
4 6  +2
4 7  +3
4 0  ? (i.e., 0)
4 1  -3
4 2  -2
4 3  -1


5 5  0
5 6  +1
5 7  +2
5 0  +3
5 1  ? (i.e., 0)
5 2  -3
5 3  -2
5 4  -1


6 6  0
6 7  +1
6 0  +2
6 1  +3
6 2  ? (i.e., 0)
6 3  -3
6 4  -2
6 5  -1


7 7  0
7 0  +1
7 1  +2
7 2  +3
7 3  ? (i.e., 0)
7 4  -3
7 5  -2
7 6  -1
*/
class ii_c_floppy_drive
{
	bool mag[4];

	int track_in_eighths;
	// this is the number of eighth-tracks the arm is at. When figuring out the track number,
	// we always round down to the next lower whole track number (we don't do half or 1/4 tracks).
	// e.g., treat track 1.5 as track 1.

	static int calc_delta_eighth_track(int prev, int cur)
	{
		int d = cur-prev; // -7 to +7
		if (d==4 || d==-4)
			d = 0;
		else if (d>4)
			d -= 8;
		else if (d<-4)
			d += 8;
		return d;
	}

	int toggle_magnet(int magnet_number, bool b_on);

public:
	
};
