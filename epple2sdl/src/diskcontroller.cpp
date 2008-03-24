#include "diskcontroller.h"

DiskController::DiskController(ScreenImage& gui, int slot):
	slot(slot),
	gui(gui),
	drive1(diskBytes1,arm1),
	drive2(diskBytes2,arm2),
	currentDrive(&this->drive1)
{
}

DiskController::~DiskController()
{
}

unsigned char DiskController::io(const unsigned short addr, const unsigned char d, const bool writing)
{
	unsigned char data(d);
	const unsigned char q = (addr & 0x000E) >> 1;
	const bool on = (addr & 0x0001);

	switch (q)
	{
		case 0:
		case 1:
		case 2:
		case 3:
			this->currentDrive->setMagnet(q,on);
//			update();
		break;
		case 4:
			this->motorOn = on;
//			update();
		break;
		case 5:
			this->currentDrive = (on ? &this->drive2 : &this->drive1);
			this->gui.clearCurrentDrive(slt,&getOtherDrive());
			this->gui.setCurrentDrive(slt,this->currentDrive);
		break;
		case 6:
			if (on && this->write && writing)
			{
				set(data);
//				update();
			}
			else if (!(on || this->write))
			{
				data = get();
			}
		break;
		case 7:
			this->write = on;
//			update();
			if (this->currentDrive->isWriteProtected())
			{
				data |= 0x80;
			}
			else
			{
				data &= 0x7F;
			}
		break;
	}
	return data;
}
