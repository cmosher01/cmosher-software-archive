package org.twodot.vnc;

import java.io.IOException;
import java.util.zip.DataFormatException;

import danger.ui.Bitmap;
import danger.ui.Pen;
import danger.ui.Rect;
import danger.util.Decompressor;

public class SolMonoZipEncoding extends Encoding {

    private long stat_bytes = 0;
    private long stat_rects = 0;

    byte[] zlibIn, zlibOut;
    int zlibInSize, zlibOutSize;

    SolMonoZipEncoding(rfbProto r, Bitmap b) {
            super(r,b);
    }

    public long getStats(byte statType) {
        if (statType == Encoding.STAT_RECTS)
            return stat_rects;
        else if (statType == Encoding.STAT_BYTES)
            return stat_bytes;
        return 0;
    }

    public int  getEncodingID() { return rfbProto.EncodingSolMonoZip; }

    public String  getName() { return "Zlib"; }

    public void doEncoding() throws IOException {
        doEncoding(rfb.updateRectX, rfb.updateRectY, rfb.updateRectW, rfb.updateRectH);
    }

    public void doEncoding(int x, int y, int w, int h) throws IOException {
        //this incoding uses the x,y,w,h for different reasons
        int numRects = x;
	    int numUncompressedBytes = y + (w*65535);

        Decompressor zlibDecomp;

        zlibDecomp = new Decompressor();

        try {
            int numCompressedBytes = rfb.is.readInt();

            if (zlibIn == null || zlibInSize < numCompressedBytes) {
                zlibIn = new byte[numCompressedBytes];
                zlibInSize = numCompressedBytes;
            }

            // not quite sure why +500, but thats what I've seen in other
            // VNC implementations
            if (zlibOut == null || zlibOutSize < numUncompressedBytes + 500) {
                zlibOut = new byte[numUncompressedBytes+500];
                zlibOutSize = numUncompressedBytes+ 500;
            }

            // read in the zlib buffer
            rfb.is.readFully(zlibIn, 0, numCompressedBytes);
            zlibDecomp.setInput(zlibIn, 0, numCompressedBytes);
            //int dBytes = zlibDecomp.inflate(zlibOut,0,numUncompressedBytes);

            for (int i = 0; i < numRects; i++) {
                // process each rect
                short sx = inflateShort(zlibDecomp);
                short sy = inflateShort(zlibDecomp);
                short sw = inflateShort(zlibDecomp);
                short sh = inflateShort(zlibDecomp);
                int sRectEncoding = inflateInt(zlibDecomp);

                Rect subRect = Rect.newXYWH(sx,sy,sw,sh);

                if (sRectEncoding==rfbProto.EncodingSolidColor) {

                    // This sub encoding is UNTESTED
                    byte pixels[] = new byte[1];
                    zlibDecomp.inflate(pixels, 0, 1);

                    //draw the solid rect
                    Pen p = fb.createPen();
                    p.setColor(Encoding.getColorFrom8BitRGB(pixels[0]));
                    p.fillRect(subRect);

                } else if (sRectEncoding == rfbProto.EncodingXORMonoColor_Zlib) {

                    throw new Exception("Unsupported SolMonoZip Encoding: XORMonoColor");

                    // This subencoding is UNTESTED
//                    int maskLength = ((sw*sh)+7)/8;
//                    byte mask[] = new byte[maskLength];
//                    byte colors[] = new byte[2];
//
//                    zlibDecomp.inflate(mask, 0, maskLength);
//                    zlibDecomp.inflate(colors, 0, 2);
//
//                    //decode the mask
//                    //debug code
//                    Pen p = fb.createPen();
//                    p.setColor(Color.GREEN);
//                    p.drawRect(subRect);

                } else if (sRectEncoding == rfbProto.EncodingXOR_Zlib) {

                    throw new Exception("Unsupported SolMonoZip Encoding: XOR_Zlib");

                    //This subEncoding is both UNFINISHED and UNTESTED
//                    int maskLength = ((sw*sh)+7)/8;
//                    byte mask[] = new byte[maskLength];
//                    byte color[] = new byte[1];
//                    zlibDecomp.inflate(mask, 0, maskLength);
//
//                    int cb = 0;
//
//                    for (int cy = sy; cy < sy + sh; cy++)
//                        for (int cx = sx; cx < sx + sw; cx++) {
//
//                            if (getBit(mask, cb)) {
//                                zlibDecomp.inflate(color, 0, 1);
//
//                            }
//                            cb++;
//                        }
//
//                    //debug code
//                    Pen p = fb.createPen();
//                    p.setColor(Color.BLUE);
//                    p.drawRect(subRect);

                }  else if (sRectEncoding == rfbProto.EncodingRaw) {

                    int numPixels = sw * sh;
                    byte[] pixels = new byte[numPixels];

                    zlibDecomp.inflate(pixels, 0, numPixels);

                    Bitmap tmpBmp = byteArrayToBitmap(pixels,  sw,  sh);
                    tmpBmp.copyBitsTo(fb, Rect.newXYWH(sx,sy,sw,sh), Rect.newXYWH(0,0,sw,sh));
                }
            }

            //accumulate stats
            stat_rects ++;
            stat_bytes += 4 + numCompressedBytes;

        } catch (Exception e) {
            System.out.println("DFE SolMonoZipEncoding: " + e.getMessage());
        }

    }

    private short inflateShort(Decompressor decomp) throws DataFormatException {
        byte b[] = new byte[2];

        decomp.inflate(b,0,2);
        int output = b[0] & 0xFF;

        return (short)((b[1] & 0xFF) + (output << 8));
    }

    private int inflateInt(Decompressor decomp) throws DataFormatException {
        byte b[] = new byte[4];

        decomp.inflate(b,0,4);

        int b2 = b[0] & 0xFF;
        int b1 = b[1] & 0xFF;
        int b0 = b[2] & 0xFF;
        return (b[3] & 0xFF) + (b2 << 8) + (b1 << 16) + (b0 << 24);
    }
//
//    private boolean getBit(byte[] bytes, int bit) {
//        int byte_ndx,bit_ndx;
//		byte_ndx=bit/8;
//		bit_ndx=bit%8;
//
//        return ((bytes[byte_ndx] << bit_ndx) & 0x80) != 0;
//    }
}
