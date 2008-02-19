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
#include "steppermotor.h"
#include "Util.h"

StepperMotor::StepperMotor():
	quarterTrack(QTRACKS >> 1), // start in the middle of the disk... just for fun
	pos(0),
	mags(0)
{
}

StepperMotor::~StepperMotor()
{
}

signed char StepperMotor::mapMagPos[] = {-1,0,2,1,4,-1,3,2,6,7,-1,0,5,6,4,-1};


void StepperMotor::setMagnet(const unsigned char magnet, const bool on)
{
//	if (magnet < 0 || 4 <= magnet)
//	{
//		throw new IllegalStateException();
//	}

	const unsigned char mask = 1 << magnet;
	if (on)
	{
		this->mags |= mask;
	}
	else
	{
		this->mags &= ~mask;
	}
	const signed char newPos = mapMagPos[this->mags];
	signed char d = 0;
	if (newPos >= 0)
	{
		d = calcDeltaPos(this->pos,newPos);
		this->pos = newPos;

		this->quarterTrack += d;
		Util::constrain((unsigned char)0,this->quarterTrack,(unsigned char)QTRACKS);
	}
}
