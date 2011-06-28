/*
 * Copyright (c) 2001 Torgeir Veimo Copyright (c) 2002 Nicolas PERIDONT Bug
 * Fixes: Daniel Morrione dan@morrione.net Permission is hereby granted, free of
 * charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to
 * the following conditions: The above copyright notice and this permission
 * notice shall be included in all copies or substantial portions of the
 * Software. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE. Change Log: iiimmddyyn nnnnn Description ----------
 * ----- ------------------------------------------------------- gls100603a
 * Fixes from Torgeir Veimo and Dan Morrione gls110603a Stream not being closed
 * thus preventing a file from being subsequently deleted. gls031504a Error
 * being written to stderr rather than throwing exception
 */

package nu.mine.mosher;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.io.RandomAccessFile;



public class Delta
{
    public static final int S = Checksum.S;
    public static final int buff_size = 64 * S;



    public static void computeDelta(File sourceFile, File targetFile, GDiffWriter output) throws IOException
    {
        int targetLength = (int)targetFile.length();
        int targetidx = 0;

        Checksum checksum = new Checksum();
        checksum.generateChecksums(sourceFile);

        PushbackInputStream target = new PushbackInputStream(new BufferedInputStream(new FileInputStream(targetFile)),buff_size);
        RandomAccessFile source = new RandomAccessFile(sourceFile,"r");

        byte rSrc[] = new byte[S];
        byte rTrg[] = new byte[S];
        byte b[] = new byte[1];

        if (targetLength - targetidx <= S)
        {
            source.close();
            target.close();
            output.close();
            throw new IOException("Unable to compute delta, input file is too short");
            // TODO do something nicer here (maybe generate a diff with just one insert instruction)
        }

        // initialize first complete checksum.
        target.read(rTrg,0,S);
        targetidx += S;

        long hashf = Checksum.queryChecksum(rTrg,S);

        // TODO The check for alternative hashf is only because I wanted to verify
        // that the update method really is correct. I will remove it shortly.
        long alternativehashf = hashf;

        // This flag indicates that we've run out of source bytes
        boolean sourceOutofBytes = false;

        boolean done = false;
        while (!done)
        {

            int index = checksum.findChecksumIndex(hashf);
            if (index != -1)
            {

                boolean match = true;
                int offset = index * S;
                int length = S - 1;
                source.seek(offset);

                // possible match, need to check byte for byte
                if (!sourceOutofBytes && source.read(rSrc,0,S) != -1)
                {
                    for (int ix = 0; ix < S /*CAM ??? && match*/; ix++)
                    {
                        if (rSrc[ix] != rTrg[ix])
                        {
                            match = false;
                        }
                    }
                }
                else
                {
                    sourceOutofBytes = true;
                }

                if (match && !sourceOutofBytes)
                {
                    // The length of the match is determined by comparing bytes.
                    boolean ok;
                    byte[] sourceBuff = new byte[buff_size];
                    byte[] targetBuff = new byte[buff_size];
                    int source_idx = 0;
                    int target_idx = 0;
                    do
                    {
                        source_idx = source.read(sourceBuff,0,buff_size);
                        if (source_idx < 0)
                        {
                            // Ran out of source bytes during match, so flag this
                            sourceOutofBytes = true;
                            break;
                        }

                        /*
                         * Don't read more target bytes than source bytes ...
                         * this is *VERY* important
                         */
                        target_idx = target.read(targetBuff,0,source_idx);
                        if (target_idx < 0)
                        {
                            // Ran out of target bytes during this match, so we're done
                            break;
                        }

                        int read_idx = Math.min(source_idx,target_idx);
                        int i = 0;
                        do
                        {
                            targetidx++;
                            ++length;
                            ok = sourceBuff[i] == targetBuff[i];
                            i++;
                            if (!ok)
                            {
                                b[0] = targetBuff[i - 1];

                                if (target_idx != -1)
                                {
                                    target.unread(targetBuff,i,target_idx - i);
                                }
                            }
                        }
                        while (ok && i < read_idx);
                        b[0] = targetBuff[i - 1];
                    }
                    while (ok && targetidx < targetLength);

                    // this is a copy instruction
                    output.addCopy(offset,length);

                    if (targetLength - targetidx < S)
                    {
                        // eof reached, special case for last bytes
                        rTrg[0] = b[0]; // don't loose this byte
                        int remaining = targetLength - targetidx;
                        target.read(rTrg,1,remaining);
                        targetidx += remaining;
                        for (int ix = 0; ix <= remaining; ix++)
                        {
                            output.addData(rTrg[ix]);
                        }
                        done = true;
                    }
                    else
                    {
                        rTrg[0] = b[0];
                        target.read(rTrg,1,S - 1);
                        targetidx += S - 1;
                        alternativehashf = hashf = Checksum.queryChecksum(rTrg,S);
                    }
                    continue; //continue loop
                }
            }

            if (targetidx > targetLength)
            {
                // update the adler fingerprint with a single byte
                target.read(b,0,1);
                targetidx += 1;

                // insert instruction with the old byte we no longer use...
                output.addData(rTrg[0]);

                alternativehashf = Checksum.incrementChecksum(alternativehashf,rTrg[0],b[0]);

                for (int i = 0; i < S-1; ++i)
                {
                    rTrg[i] = rTrg[i + 1];
                }
                rTrg[S-1] = b[0];
                hashf = Checksum.queryChecksum(rTrg,S);
            }
            else
            {
                for (int i = 0; i < S; ++i)
                {
                    output.addData(rTrg[i]);
                }
                done = true;
            }

        }
        source.close();
        target.close();
        output.close();
    }

    public static ByteArrayOutputStream delta(File sourceFile, File targetFile) throws IOException
    {
        ByteArrayOutputStream gdiff = new ByteArrayOutputStream();
        GDiffWriter output = new GDiffWriter(new DataOutputStream(new BufferedOutputStream(gdiff)));

        if (sourceFile.length() > Integer.MAX_VALUE || targetFile.length() > Integer.MAX_VALUE)
        {
            throw new IOException("source or target is too large, max length is " + Integer.MAX_VALUE);
        }

        Delta.computeDelta(sourceFile,targetFile,output);
        output.flush();

        return gdiff;
    }
}
