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
//version 0.13 - revised for XOR encodings

package org.twodot.vnc;

import java.io.IOException;

import danger.ui.Bitmap;
import danger.ui.Rect;
import danger.util.Decompressor;

public class ZlibEncoding extends Encoding {
	
	private long stat_bytes = 0;
	private long stat_rects = 0;
	Decompressor zlibDecomp;
	byte[] zlibIn, zlibOut;
	int zlibInSize = 0, zlibOutSize = 0;

	ZlibEncoding(rfbProto r, Bitmap b) {
		super(r,b);
        zlibDecomp = new Decompressor();
	}

	public long getStats(byte statType) {
		if (statType == Encoding.STAT_RECTS)
			return stat_rects;
		else if (statType == Encoding.STAT_BYTES)
			return stat_bytes;
		return 0;
	}

	public int  getEncodingID() { return rfbProto.EncodingZlib; }

	public String  getName() { return "Zlib"; }

	public void doEncoding() throws IOException {
		doEncoding(rfb.updateRectX, rfb.updateRectY, rfb.updateRectW, rfb.updateRectH);
	}

	public void doEncoding(int x, int y, int w, int h) throws IOException {
		try {
			int nBytes = rfb.is.readInt();
            //Rect subRect = Rect.newXYWH(x,y,w,h); //debug

			if (zlibIn == null || zlibInSize < nBytes) {
				zlibIn = new byte[nBytes];
				zlibInSize = nBytes;
			}

			if (zlibOut == null || zlibOutSize < w*h) {
				zlibOut = new byte[w*h];
				zlibOutSize = w*h;
			}

			rfb.is.readFully(zlibIn, 0, nBytes);

			zlibDecomp.setInput(zlibIn, 0, nBytes);
			/*int dBytes =*/ zlibDecomp.inflate(zlibOut,0,w*h);

            if (rfb.updateRectEncoding == rfbProto.EncodingZlib)  {

                //Normal ZLib Encoding
			    Bitmap tmpBmp = Encoding.byteArrayToBitmap(zlibOut, w, h);
			    tmpBmp.copyBitsTo(fb, Rect.newXYWH(x,y,w,h), Rect.newXYWH(0,0,w,h));

            } else if (rfb.updateRectEncoding == rfbProto.EncodingXOR_Zlib) {

                //XOR Zlib Encoding
                //DEBUG.p("XOR Zlib Start");

                //debug code
                //Pen p = fb.createPen();
                //p.setColor(Color.CYAN);
                //p.drawRect(subRect);

                throw new Exception("Unsupported ZLib Encoding: XOR_Zlib");

            } else if (rfb.updateRectEncoding == rfbProto.EncodingXORMultiColor_Zlib) {

                //XOR MultiColor Enocidng
//debug code    DEBUG.p("XOR MultiColor Start");

                //Pen p = fb.createPen();
                //p.setColor(Color.MAGENTA);
                //p.drawRect(subRect);

                throw new Exception("Unsupported ZLib Encoding: XORMultiColor");

            } else if (rfb.updateRectEncoding == rfbProto.EncodingXORMonoColor_Zlib) {

                //XOR MonoColor Encoding
                //DEBUG.p("XOR MonoColor Start");
//debug code
                //Pen p = fb.createPen();
                //p.setColor(Color.PINK);
                //p.drawRect(subRect);

                throw new Exception("Unsupported ZLib Encoding: XORMonoColor");

            } else {

                throw new Exception("Unknown Zlib Encoding!");

            }

			//accumulate stats
            stat_rects ++;
            stat_bytes += 4 + nBytes;
		} catch (Exception e) {
			System.out.println("DFE doZlibRect: " + e.getMessage());
		}
	}
}