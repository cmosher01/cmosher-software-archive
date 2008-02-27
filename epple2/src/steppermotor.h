#ifndef STEPPERMOTOR_H
#define STEPPERMOTOR_H

class StepperMotor
{
private:
	enum { TRACKS_PER_DISK = 0x23 };
	enum { QTRACKS = TRACKS_PER_DISK << 2 };

	signed short quarterTrack;
	signed char pos; // 0 - 7
	unsigned char mags;

	static signed char mapMagPos[];

	static signed char calcDeltaPos(const unsigned char cur, const signed char next)
	{
		signed char d = next-cur; // -7 to +7

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

public:
	StepperMotor();
	~StepperMotor();

	void setMagnet(const unsigned char magnet, const bool on);
	unsigned char getTrack()
	{
		return this->quarterTrack >> 2;
	}
};

#endif
