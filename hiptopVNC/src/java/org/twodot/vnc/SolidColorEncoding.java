package org.twodot.vnc;

import java.io.IOException;

import danger.ui.Bitmap;
import danger.ui.Pen;
import danger.ui.Rect;

public class SolidColorEncoding extends Encoding {

    private long stat_bytes = 0;
	private long stat_rects = 0;

	SolidColorEncoding(rfbProto r, Bitmap b) {
		super(r,b);
	}

	public long getStats(byte statType) {
		if (statType == Encoding.STAT_RECTS)
			return stat_rects;
		else if (statType == Encoding.STAT_BYTES)
			return stat_bytes;
		return 0;
	}

	public int  getEncodingID() { return rfbProto.EncodingSolidColor; }

	public String getName() { return "SolidColor"; }

	public void doEncoding() throws IOException {
		doEncoding(rfb.updateRectX, rfb.updateRectY,
				   rfb.updateRectW, rfb.updateRectH);
    }

	public void doEncoding(int x, int y, int w, int h) throws IOException {
		// This encoding reads in the color and fills
        // the updateRect region with that color.

        // get the color
		byte color = rfb.is.readByte();

        // get a pen (draw directly into the frameBuffer)
        Pen p = fb.createPen();

        // set the pen's color and draw the rect.
        p.setColor(Encoding.getColorFrom8BitRGB(color));
        p.fillRect(Rect.newXYWH(x,y,w,h));

		//Accumulate statistics
		stat_rects++;
		stat_bytes++; //only 1 byte in (the color)

	}

}
