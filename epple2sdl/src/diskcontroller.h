#include "card.h"
#include "drive.h"
#include <string>

class DiskController : public Card
{
private:
	DiskBytes diskBytes1;
	StepperMotor arm1;
	Drive drive1;

	DiskBytes diskBytes2;
	StepperMotor arm2;
	Drive drive2;

	Drive* currentDrive;

	bool write;
	bool motorOn;


	//DiskDriveControllerPanel panel;


	// TODO for a rev. 0 motherboard, the disk controller will auto reset the CPU

	void set(unsigned char data)
	{
		if (!this->motorOn)
		{
			return;
		}
		this->currentDrive->set(data);
	}

	unsigned char get() const
	{
		if (!this->motorOn)
		{
			return 0xFF;
		}
		return this->currentDrive->get();
	}

/*	void update()
	{
		if (this->panel == null)
		{
			return;
		}
		this->panel.updateDrives();
	}
*/
	Drive& getDrive(const unsigned char drive)
	{
		return (drive == 0) ? this->drive1 : this->drive2;
	}

	Drive& getOtherDrive()
	{
		return (this->currentDrive == &this->drive1) ? this->drive2 : this->drive1;
	}

/*	public JPanel getPanel(const GUI gui)
	{
		if (this->panel == null)
		{
			this->panel = new DiskDriveControllerPanel(this,gui);
		}
		return this->panel;
	}

	public DropTargetListener getDropListener()
	{
		return this->panel.getDropListener();
	}
*/


public:
	DiskController();
	~DiskController();

	virtual unsigned char io(const unsigned short address, const unsigned char data, const bool writing);

	void reset()
	{
		this->motorOn = false;
		this->currentDrive = &this->drive1;
//		update();
	}

	void loadDisk(unsigned char drive, const std::string& fnib)
	{
		this->getDrive(drive).loadDisk(fnib);
	}

	bool isMotorOn()
	{
		return this->motorOn;
	}

	const DiskBytes& getDiskBytes(unsigned char disk)
	{
		return this->getDrive(disk).getDiskBytes();
	}

	unsigned char getTrack()
	{
		return this->currentDrive->getTrack();
	}

	bool isWriting()
	{
		return this->write;
	}

	bool isModified()
	{
		return this->currentDrive->isModified();
	}

	bool isModifiedOther()
	{
		return getOtherDrive().isModified();
	}

	bool isWriteProtected()
	{
		return this->currentDrive->isWriteProtected();
	}

	bool isDirty()
	{
		return isModified() || isModifiedOther();
	}

	unsigned char getCurrentDriveNumber()
	{
		return this->currentDrive == &this->drive1 ? 0 : 1;
	}

	unsigned char getOtherDriveNumber()
	{
		return 1-getCurrentDriveNumber();
	}
};
