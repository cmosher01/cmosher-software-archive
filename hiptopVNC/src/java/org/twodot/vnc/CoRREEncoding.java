// Copyright (C) 2003 Jake Bordens.  All Rights Reserved.
// Copyright (C) 2002 Ultr@VNC Team Members. All Rights Reserved.
// Copyright (C) 2001,2002 HorizonLive.com, Inc.  All Rights Reserved.
// Copyright (C) 2002 Constantin Kaplinsky.  All Rights Reserved.
// Copyright (C) 1999 AT&T Laboratories Cambridge.  All Rights Reserved.

//  This is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; either version 2 of the License, or
//  (at your option) any later version.
//
//  This software is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this software; if not, write to the Free Software
//  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,
//  USA.

//
//  This file is part of
//         Hiptop VNC Client - a VNC client for the danger hiptop.
//

//version 0.10 - origial release
//version 0.11 - added stats code

package org.twodot.vnc;

import java.io.IOException;

import danger.ui.Bitmap;
import danger.ui.Pen;

public class CoRREEncoding extends Encoding {

	private long stat_bytes = 0;
	private long stat_rects = 0;

	CoRREEncoding(rfbProto r, Bitmap b) {
		super(r,b);
	}

	public long getStats(byte statType) {
		if (statType == Encoding.STAT_RECTS)
			return stat_rects;
		else if (statType == Encoding.STAT_BYTES)
			return stat_bytes;
		return 0;
	}
	
	public int  getEncodingID() { return rfbProto.EncodingCoRRE; }

	public String  getName() { return "CoRRE"; }

	public void doEncoding() throws IOException {
		doEncoding(rfb.updateRectX, rfb.updateRectY, rfb.updateRectW, rfb.updateRectH);
	}

	public void doEncoding(int x, int y, int w, int h) throws IOException {

		int nSubrects = rfb.is.readInt();
		int bg = rfb.is.read();
		int pixel, sx, sy, sw, sh;

		//draw a rectangle of color bg
		Pen p = fb.createPen();
		p.setColor(getColorFrom8BitRGB((byte)bg));
		p.fillRect(x, y, x+w, y+h);

		for (int j = 0; j < nSubrects; j++) {
			pixel = rfb.is.read();
			sx = rfb.is.read() + rfb.updateRectX;
			sy = rfb.is.read() + rfb.updateRectY;
			sw = rfb.is.read();
			sh = rfb.is.read();
			p.setColor(getColorFrom8BitRGB((byte)pixel));
			p.fillRect(sx, sy, sx+sw, sy+sh);
		}
		
		//accumulate stats
		stat_bytes += 5 + (nSubrects * 5);
		stat_rects++;
	}

}