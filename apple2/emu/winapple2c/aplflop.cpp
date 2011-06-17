#include "stdafx.h"
#include "aplflop.h"



int ii_c_floppy_drive::toggle_magnet(int magnet_number, bool b_on)
{
	mag[magnet_number] = b_on;
}
