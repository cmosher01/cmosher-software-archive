#ifndef DRIVE_H
#define DRIVE_H

#include <string>

#include "diskbytes.h"
#include "steppermotor.h"

class Drive
{
private:
	enum { TRACKS_PER_DISK = 0x23 };

	DiskBytes& disk;
	StepperMotor& arm;

public:
	Drive(DiskBytes& disk, StepperMotor& arm):
		disk(disk),
		arm(arm)
	{
	}

	~Drive() {}

	void loadDisk(const std::string& fnib)
	{
		this->disk.load(fnib);
	}

	bool isWriteProtected() const
	{
		return this->disk.isWriteProtected();
	}

	bool isModified() const
	{
		return this->disk.isModified();
	}



	void setMagnet(unsigned char q, bool on)
	{
		this->arm.setMagnet(q,on);
	}

	int getTrack() const
	{
		return this->arm.getTrack();
	}



	unsigned char get() const
	{
		return this->disk.get(this->arm.getTrack());
	}

	void set(unsigned char value)
	{
		this->disk.put(this->arm.getTrack(),value);
	}



	const DiskBytes& getDiskBytes()
	{
		return this->disk;
	}
};

#endif
