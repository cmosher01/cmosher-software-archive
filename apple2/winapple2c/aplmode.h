#pragma once
#include "opcode.h"

class mode
{
public:
	enum t
	{
		immediate, // #0
		absolute, // $0000
		zero_page, // $00
		accumulator,
		implied,
		zero_page_x_indirect, // ($00,X)
		zero_page_indirect_y, // ($00),Y
		zero_page_x, // $00,X
		zero_page_y, // $00,Y
		absolute_x, // $0000,X
		absolute_y, // $0000,Y
		relative,
		absolute_indirect, // ($0000)
		absolute_x_indirect,
		zero_page_indirect // ($00)
	};
	t m_mode;

	mode(opcode_t op);
	bool operator==(t x) { return m_mode==x; }
};
