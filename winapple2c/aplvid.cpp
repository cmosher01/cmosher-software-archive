#include "stdafx.h"
#include "aplvid.h"

unsigned int ii_c_video_t::charmap[0x80][8] =
{
#define CHARMAP(c,r7,r6,r5,r4,r3,r2,r1,r0) \
	r0,r1,r2,r3,r4,r5,r6,r7,
#include "charmap.h"
#undef CHARMAP
};

static DWORD WINAPI vidthread(void* p)
{
	ii_c_video_t* t = reinterpret_cast<ii_c_video_t*>(p);
	return t->raster();
}

void ii_c_video_t::setup(ii_c_memory_t& xmem, ncr65c02_t& xcpu)
{
	if (th)
		return;

	mem = &xmem;
	cpu = &xcpu;

	h_white_pen = (HPEN)::GetStockObject(WHITE_PEN);

	DWORD id;
	th = ::CreateThread(0,0,vidthread,reinterpret_cast<void*>(this),0,&id);
	if (!th)
		throw "Error: cannot create thread to show video screen.";
}

static ii_c_video_t* get_vid(HWND hw)
{
	return reinterpret_cast<ii_c_video_t*>(::GetWindowLong(hw,GWL_USERDATA));
}

static LRESULT CALLBACK VidWndProc(HWND hwnd, UINT msg, WPARAM wParam, LPARAM lParam)
{
	LRESULT r(0);

	switch (msg)
	{
		case WM_CHAR:
		{
				ii_c_video_t* pvid = get_vid(hwnd);

				// turn on KBDSTRB/AKD
				byte *kbdstrb = pvid->mem->get_kdbstrb();
				*kbdstrb |= 0x80;

				// put ASCII value of key in KDB (with high bit set to indicate it is valid).
				byte *kbd = pvid->mem->get_kdb();
				*kbd = wParam & 0x80;
		}
		break;
		case WM_KEYDOWN:
		{
			// ??? could use this to generate ^S and ^Q ???
//			SHORT ks_control = ::GetKeyState(VK_SCROLL);
			if (wParam==VK_CANCEL) // "Ctrl" / "Pause--Break" on the PC was pressed: this means "reset" on the Apple //c
			{
				ii_c_video_t* pvid = get_vid(hwnd);
				pvid->cpu->reset();
			}

			unsigned char c = wParam;
			//VK_CLOSE_BRACKET
			if (('0' <= c && c < '9')||('A' <= c && c <= 'Z'))
			{
				bool b_control = !!(::GetKeyState(VK_CONTROL) & 0x8000);

				if (b_control)
				{
					ii_c_video_t* pvid = get_vid(hwnd);

					// turn on KBDSTRB/AKD
					byte *kbdstrb = pvid->mem->get_kdbstrb();
					*kbdstrb |= 0x80;

					// put ASCII value of key in KDB (with high bit set to indicate it is valid).
					byte *kbd = pvid->mem->get_kdb();
					*kbd = (c-0x30) & 0x80;
				}
			}
		}
		break;
		case WM_KEYUP:
		{
			ii_c_video_t* pvid = get_vid(hwnd);

			// turn off KBDSTRB/AKD
			byte *kbdstrb = pvid->mem->get_kdbstrb();
			*kbdstrb &= 0x7f;
		}
		break;
		case WM_DESTROY:
		{
			::PostQuitMessage(0);
		}
		break;
	}

	return ::DefWindowProc(hwnd,msg,wParam,lParam);
}

DWORD ii_c_video_t::raster()
{
	WNDCLASSEX wndclass;
	wndclass.cbSize = sizeof(wndclass);
	wndclass.style = CS_HREDRAW|CS_VREDRAW;
	wndclass.lpfnWndProc = VidWndProc;
	wndclass.cbClsExtra = 0;
	wndclass.cbWndExtra = 0;
	wndclass.hInstance = ::GetModuleHandle(0);
	wndclass.hIcon = ::LoadIcon(0,IDI_APPLICATION);
	wndclass.hCursor = ::LoadCursor(0,IDC_ARROW);
	wndclass.hbrBackground = (HBRUSH)::GetStockObject(WHITE_BRUSH);
	wndclass.lpszMenuName = 0;
	wndclass.lpszClassName = "WinApple";
	wndclass.hIconSm = ::LoadIcon(0,IDI_APPLICATION);
	BOOL bOK = ::RegisterClassEx(&wndclass);

	hw = ::CreateWindowEx(0,"WinApple","Apple //c",WS_OVERLAPPEDWINDOW|WS_VISIBLE,275,200,40*7+8,24*8+27,0,0,::GetModuleHandle(0),0);
	if (!hw)
		throw "Could not create the window to display the screen.";

	::SetWindowLong(hw,GWL_USERDATA,reinterpret_cast<LONG>(this));

	hdc = ::GetDC(hw);

	while (true)
	{
		MSG msg;

		while (!::PeekMessage(&msg,0,0,0,PM_NOREMOVE))
		{
			scan();
			::Sleep(10);
		}

		do
		{
			if (!::GetMessage(&msg,0,0,0))
				return msg.wParam; // this is the only exit of the raster() function

			::TranslateMessage(&msg);
			::DispatchMessage(&msg);
		}
		while (::PeekMessage(&msg,0,0,0,PM_NOREMOVE));
	}

	// this code is never executed
	return 0;
}

void ii_c_video_t::scan()
{
	byte*p = mem->get_text_base();
	home();
	for (int sec(0); sec < 3; ++sec)
	{
		for (int row(0); row < 8; ++row)
		{
			for (int col(0); col < 40; ++col)
			{
				byte a = *p++;
				a &= 0x7f; // clear high bit for now
				for (int scan_line(0); scan_line < 8; ++scan_line)
				{
					draw_byte(charmap[a][scan_line]);
					pos.y++;
				}
				pos.y -= 8; pos.x += 7; // next character cell
			}
			p += 0x58;
			pos.x = 0; // "return" to next beginning of
			pos.y += 8; // next character cell row
		}
		p -= 0x3d8;
	}
}

void ii_c_video_t::home()
{
	pos.x = pos.y = 0;
}

void ii_c_video_t::draw_byte(byte c)
{
	// translate pos into real coords of window ??? to do
	POINT p;
	p = pos;
//	p.x *= 8;
//	p.y *= 7;

	// clear
	HGDIOBJ old = ::SelectObject(hdc,h_white_pen);
	::MoveToEx(hdc,p.x,p.y,0);
	::LineTo(hdc,p.x+7,p.y);
	::SelectObject(hdc,old);

	// draw one byte (i.e., one pixel row, of 7 pixels wide)
	::MoveToEx(hdc,p.x,p.y,0);
	for (int pix(0); pix < 7; ++pix)
	{
		p.x++;
		if (c&1)
			::LineTo(hdc,p.x,p.y);
		else
			::MoveToEx(hdc,p.x,p.y,0);
		c >>= 1;
	}
}
