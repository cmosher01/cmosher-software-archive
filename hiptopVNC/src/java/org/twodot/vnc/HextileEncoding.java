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

public class HextileEncoding extends Encoding {

	private long stat_bytes = 0;
	private long stat_rects = 0;

	HextileEncoding(rfbProto r, Bitmap b) {
		super(r,b);
	}

	public long getStats(byte statType) {
		if (statType == Encoding.STAT_RECTS)
			return stat_rects;
		else if (statType == Encoding.STAT_BYTES)
			return stat_bytes;
		return 0;
	}

	public int  getEncodingID() { return rfbProto.EncodingHextile; }

	public String  getName() { return "Hextile"; }

	public void doEncoding() throws IOException {
		doEncoding(rfb.updateRectX, rfb.updateRectY, rfb.updateRectW, rfb.updateRectH);
	}

	public void doEncoding(int x, int y, int w, int h) throws IOException {

		RawEncoding RawRect = new RawEncoding(rfb, fb);

		int bg = 0, fg = 0, sx, sy, sw, sh;
		Pen p = fb.createPen();

		for (int ty = y; ty < y + h; ty += 16) {
			for (int tx = x; tx < x + w; tx += 16) {
				int tw = 16, th = 16;

				if (x + w - tx < 16)
					tw = x + w - tx;

				if (y + h - ty < 16)
					th = y + h - ty;

				int subencoding = rfb.is.read();
				stat_bytes++;
				
				if ((subencoding & rfbProto.HextileRaw) != 0) {
					RawRect.doEncoding(tx, ty, tw, th);
					continue;
				}

				if ((subencoding & rfbProto.HextileBackgroundSpecified) != 0) {
					bg = rfb.is.read();
					stat_bytes++;
				}

				p.setColor(getColorFrom8BitRGB((byte)bg));
				p.fillRect(tx, ty, tx+tw, ty+th);

				if ((subencoding & rfbProto.HextileForegroundSpecified) != 0) {
					fg = rfb.is.read();
					stat_bytes++;
				}

				if ((subencoding & rfbProto.HextileAnySubrects) == 0)
					continue;

				int nSubrects = rfb.is.read();
				stat_bytes++;
				
				if ((subencoding & rfbProto.HextileSubrectsColoured) != 0) {
					for (int j = 0; j < nSubrects; j++) {
						fg = rfb.is.read();
						int b1 = rfb.is.read();
						int b2 = rfb.is.read();
						
						stat_bytes += 3;
						
						sx = (b1 >> 4) + tx;
						sy = (b1 & 0xf) + ty;
						sw = (b2 >> 4) + 1;
						sh = (b2 & 0xf) + 1;

						p.setColor(getColorFrom8BitRGB((byte)fg));
						p.fillRect(sx, sy, sx+sw, sy+sh);
					}
				} else {
					p.setColor(getColorFrom8BitRGB((byte)fg));
					for (int j = 0; j < nSubrects; j++) {
						int b1 = rfb.is.read();
						int b2 = rfb.is.read();
						
						stat_bytes += 2;
						
						sx = (b1 >> 4) + tx;
						sy = (b1 & 0xf) + ty;
						sw = (b2 >> 4) + 1;
						sh = (b2 & 0xf) + 1;

						p.fillRect(sx, sy, sx+sw, sy+sh);
					}
				}
			}
		}
		
		//Hextile uses an ecapsulated raw encoding (subrect)
		//get the stats from that encoding, and add it to ours
		stat_bytes += RawRect.getStats(Encoding.STAT_BYTES);
		stat_rects++;
	}
}