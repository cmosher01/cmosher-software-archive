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
//			   - added byteArrayToBitmap function

package org.twodot.vnc;

import java.io.IOException;

import danger.ui.Bitmap;
import danger.ui.Color;
import danger.ui.ColorSpace;

public abstract class Encoding {

	public static final byte STAT_RECTS = 0;
	public static final byte STAT_BYTES = 1;

	rfbProto rfb;
	Bitmap fb;

	Encoding() {}

	Encoding(rfbProto rfbP, Bitmap fbB) {
		rfb = rfbP;
		fb = fbB;
	}

	public abstract int  getEncodingID();
	public abstract String getName();

	public abstract void doEncoding() throws IOException;
	public abstract void doEncoding(int x, int y, int w, int h) throws IOException;

	public abstract long getStats(byte statType);

	public static Color getColorFrom8BitRGB(byte bt) {
						int r = (bt & 7)<<5;
						int g = ((bt >> 3) & 7)<<5;
						int b = ((bt >> 6) & 3)<<6;

						return new Color(r,g,b);
	}

	public static Bitmap byteArrayToBitmap(byte[] b, int w, int h) {

		byte[] tmpBytes = new byte[w*h*2];
		
		//from BGR233 to BGR565
		for (int x=0; x<w*h; x++) {
			tmpBytes[2*x+1] = (byte)((0xC0 & b[x]) | ((b[x] >> 3) & 7)); 
			tmpBytes[2*x] = (byte)((b[x] & 7) << 2);
		}
		
		return new Bitmap(tmpBytes, w, h, ColorSpace.RGB16_BGR, 0);
	}
}