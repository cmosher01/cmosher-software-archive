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
//version 0.11 - removed if statement and duplicate code
//             - added stats code
package org.twodot.vnc;

import java.io.IOException;

import danger.ui.Bitmap;
import danger.ui.Pen;
import danger.ui.Point;
import danger.ui.Rect;
import danger.util.Decompressor;

public class TightEncoding extends Encoding {

	private long stat_bytes = 0;
	private long stat_rects = 0;
	
	final static int tightZlibBufferSize = 512;
  	Decompressor[] tightInflaters;

	TightEncoding(rfbProto r, Bitmap b) {
		super(r,b);
		tightInflaters = new Decompressor[4];
	}
	
	public long getStats(byte statType) {
		if (statType == Encoding.STAT_RECTS)
			return stat_rects;
		else if (statType == Encoding.STAT_BYTES)
			return stat_bytes;
		return 0;
	}

	public int  getEncodingID() { return rfbProto.EncodingTight; }

	public String getName() { return "Tight"; }

	public void doEncoding() throws IOException {
		doEncoding(rfb.updateRectX, rfb.updateRectY,
				   rfb.updateRectW, rfb.updateRectH);
	}

	public void doEncoding(int x, int y, int w, int h) throws IOException {

		Pen fbp = fb.createPen();

 		int comp_ctl = rfb.is.readUnsignedByte();
 		stat_bytes ++;

 		// Flush zlib streams if we are told by the server to do so.
 		for (int stream_id = 0; stream_id < 4; stream_id++) {
 			if ((comp_ctl & 1) != 0 && tightInflaters[stream_id] != null) {
				tightInflaters[stream_id] = null;
		    }
      		comp_ctl >>= 1;
    	}

		// Check correctness of subencoding value.
		if (comp_ctl > rfbProto.TightMaxSubencoding) {
			throw new IOException("Incorrect tight subencoding: " + comp_ctl);
    	}

    	// Handle solid-color rectangles.
		if (comp_ctl == rfbProto.TightFill) {
			//System.out.println("Tight Fill Subenc");
			int idx = rfb.is.readUnsignedByte();
			stat_bytes++;
			
			fbp.setColor(getColorFrom8BitRGB((byte)idx));
			fbp.fillRect(x, y, x+w, y+h);
			//System.out.println("done with fill subenc.");
			return;
    	}

    	if (comp_ctl == rfbProto.TightJpeg)
			throw new IOException("Unsupported Tight Encoding: JPEG");


   		// Read filter id and parameters.
    	int numColors = 0, rowSize = w;
    	byte[] palette8 = new byte[2];
    	//int[] palette24 = new int[256];  //DELME?
    	//boolean useGradient = false;
    	if ((comp_ctl & rfbProto.TightExplicitFilter) != 0) {
      		int filter_id = rfb.is.readUnsignedByte();
      		stat_bytes++;
      		
      		if (filter_id == rfbProto.TightFilterPalette) {
				numColors = rfb.is.readUnsignedByte() + 1;
				stat_bytes++;
				
        		if (numColors != 2) {
	    			throw new IOException("Incorrect tight palette size: " + numColors);
	  			}
	  			
	  			rfb.is.readFully(palette8);
				stat_bytes+=2;
				
	  			rowSize = (w + 7) / 8;
      		} else if (filter_id == rfbProto.TightFilterGradient) {
      			//this should not happen.  Like JPEG,
      			//gradient is only used at higher than 8-bit
      			//color depth.
				throw new IOException("Gradient filter not implemented.");
      		} else if (filter_id != rfbProto.TightFilterCopy) {
				throw new IOException("Incorrect tight filter id: " + filter_id);
      		}
		}

		// Read, optionally uncompress and decode data.
		int dataSize = h * rowSize;

		//If the data is compressed, then decompress it
		//if isn't, pass it along as-is.
		byte[] buf;
		stat_bytes += dataSize;
		if (dataSize < rfbProto.TightMinToCompress) {
			buf = new byte[dataSize];
			rfb.is.readFully(buf);
		} else {
			// Data was compressed with zlib.
			int zlibDataLen = rfb.readCompactLen();
			byte[] zlibData = new byte[zlibDataLen];
			rfb.is.readFully(zlibData);

			//comp_ctl determines which inflater
			//is being used for this rect.
			int stream_id = comp_ctl & 0x03;
			if (tightInflaters[stream_id] == null) {
			  tightInflaters[stream_id] = new Decompressor();
			}
			
			Decompressor myInflater = tightInflaters[stream_id];
			myInflater.setInput(zlibData);
			buf = new byte[dataSize];
			try { myInflater.inflate(buf); }
			catch (Exception e) { e.printStackTrace(); }
		}
			
		Bitmap tmpBmp;		
		if (numColors == 2) {
			// Two colors.
			tmpBmp = new Bitmap(w,h);
			Pen p = tmpBmp.createPen();
			decodeMonoData(x, y, w, h, buf, palette8, p);
			tmpBmp.copyBitsTo(fb, Rect.newXYWH(x,y,w,h), Rect.newXYWH(0,0,w,h));
		} else {
			// Compressed truecolor data.
			//int inc = 0;

			//DONE: implement a more efficient way of doing this!
			//for (int dy = 0; dy < h; dy++) {
			//	for (int dx = 0; dx < w; dx++) {
			//		p.setColor(getColorFrom8BitRGB(buf[inc++]));
			//		p.drawPoint(new Point(dx,dy));
			//	}
			//}
			tmpBmp = Encoding.byteArrayToBitmap(buf,w,h);
			tmpBmp.copyBitsTo(fb, Rect.newXYWH(x,y,w,h), Rect.newXYWH(0,0,w,h));
		}
		
    	
    	
		//accumulate stats
		stat_rects ++;

	}

	private void decodeMonoData(int x, int y, int w, int h, byte[] src, byte[] palette, Pen p) {

	    int dx, dy, n;
	    //int i = y * rfb.framebufferWidth + x;
	    int rowBytes = (w + 7) / 8;
	    byte b;

		for (dy = 0; dy < h; dy++) {
	    	for (dx = 0; dx < w / 8; dx++) {
		   		b = src[dy*rowBytes+dx];
		   		for (n = 7; n >= 0; n--) {
		   			p.setColor(getColorFrom8BitRGB(palette[b >> n & 1]));
		   			p.drawPoint(new Point((8*dx)+(7-n),dy));
				}
	      	}

	    	for (n = 7; n >= 8 - w % 8; n--) {
			    p.setColor(getColorFrom8BitRGB(palette[src[dy*rowBytes+dx] >> n & 1]));
				p.drawPoint(new Point(((8*dx)+(7-n)),dy));
	       	}
	    }
  	}

}