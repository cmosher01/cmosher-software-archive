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
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.io.RandomAccessFile;



public class Delta
{
    public static final int S = Checksum.S;
    public static final int buff_size = 64 * S;



    public Delta()
    {
    }

    public static void computeDelta(File sourceFile, File targetFile, GDiffWriter output) throws IOException
    {
        int targetLength = (int)targetFile.length();
        int targetidx = 0;

        Checksum checksum = new Checksum();
        checksum.generateChecksums(sourceFile);

        PushbackInputStream target = new PushbackInputStream(new BufferedInputStream(new FileInputStream(targetFile)),buff_size);
        RandomAccessFile source = new RandomAccessFile(sourceFile,"r");

        boolean done = false;
        byte buf[] = new byte[S];
        long hashf = 0;
        byte b[] = new byte[1];
        byte sourcebyte[] = new byte[S];

        if (targetLength - targetidx <= S)
        {
            //gls031504a start
            source.close();
            target.close();
            output.close();
            throw new IOException("Unable to compute delta, input file is too short");
            //gls031504a end
        }

        // initialize first complete checksum.
        target.read(buf,0,S);
        targetidx += S;

        hashf = Checksum.queryChecksum(buf,S);

        // The check for alternative hashf is only because I wanted to verify
        // that the
        // update method really is correct. I will remove it shortly.
        long alternativehashf = hashf;

        /* This flag indicates that we've run out of source bytes */
        boolean sourceOutofBytes = false;

        while (!done)
        {

            int index = checksum.findChecksumIndex(hashf);
            if (index != -1)
            {

                boolean match = true;
                int offset = index * S;
                int length = S - 1;
                source.seek(offset);

                //				possible match, need to check byte for byte
                if (sourceOutofBytes == false && source.read(sourcebyte,0,S) != -1)
                {
                    for (int ix = 0; ix < S /*CAM ??? && match*/; ix++)
                    {
                        if (sourcebyte[ix] != buf[ix])
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
                    //System.out.println("before targetidx : " + targetidx );
                    // The length of the match is determined by comparing bytes.
                    //long start = System.currentTimeMillis();

                    boolean ok = true;
                    byte[] sourceBuff = new byte[buff_size];
                    byte[] targetBuff = new byte[buff_size];
                    int source_idx = 0;
                    int target_idx = 0;
                    //int tCount = 0;

                    do
                    {
                        source_idx = source.read(sourceBuff,0,buff_size);
                        //System.out.print("Source: "+ source_idx);
                        if (source_idx == -1)
                        {
                            /*
                             * Ran our of source bytes during match, so flag
                             * this
                             */
                            sourceOutofBytes = true;
                            //System.out.println("Source out ... target has: "
                            // + target.available());
                            break;
                        }

                        /*
                         * Don't read more target bytes then source bytes ...
                         * this is *VERY* important
                         */
                        target_idx = target.read(targetBuff,0,source_idx);
                        //System.out.println(" Target: "+target_idx);
                        if (target_idx == -1)
                        {
                            /*
                             * Ran out of target bytes during this match, so
                             * we're done
                             */
                            //System.err.println("Ran outta bytes
                            // Sourceidx="+source_idx +" targetidx:"+target_idx
                            // );
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
                        while (i < read_idx && ok);
                        b[0] = targetBuff[i - 1]; //gls100603a (fix from Dan
                                                  // Morrione)
                    }
                    while (ok && targetLength - targetidx > 0);

                    // this is a insert instruction
                    //System.out.println("output.addCopy("+offset+","+length+")");
                    output.addCopy(offset,length);

                    if (targetLength - targetidx <= S - 1)
                    {
                        // eof reached, special case for last bytes
                        buf[0] = b[0]; // don't loose this byte
                        int remaining = targetLength - targetidx;
                        /*int readStatus =*/ target.read(buf,1,remaining);
                        targetidx += remaining;
                        for (int ix = 0; ix < (remaining + 1); ix++)
                            output.addData(buf[ix]);
                        done = true;
                    }
                    else
                    {
                        buf[0] = b[0];
                        target.read(buf,1,S - 1);
                        targetidx += S - 1;
                        alternativehashf = hashf = Checksum.queryChecksum(buf,S);
                    }
                    continue; //continue loop
                }
            }

            if (targetLength - targetidx > 0)
            {
                // update the adler fingerprint with a single byte

                target.read(b,0,1);
                targetidx += 1;

                // insert instruction with the old byte we no longer use...
                output.addData(buf[0]);

                alternativehashf = Checksum.incrementChecksum(alternativehashf,buf[0],b[0]);

                for (int j = 0; j < 15; j++)
                    buf[j] = buf[j + 1];
                buf[15] = b[0];
                hashf = Checksum.queryChecksum(buf,S);
            }
            else
            {
                for (int ix = 0; ix < S; ix++)
                    output.addData(buf[ix]);
                done = true;
            }

        }
        source.close(); //gls100603a (Fix from Torgeir Veimo)
        target.close(); //gls110603a
        output.close();
    }

    // sample program to compute the difference between two input files.
    public static void main(String argv[]) throws IOException
    {
        if (argv.length != 3)
        {
            System.err.println("usage Delta [-d] source target [output]");
            System.err.println("either -d or an output filename must be specified.");
            System.err.println("aborting..");
            return;
        }
        File sourceFile = new File(argv[0]);
        File targetFile = new File(argv[1]);
        GDiffWriter output = new GDiffWriter(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(new File(argv[2])))));

        if (sourceFile.length() > Integer.MAX_VALUE || targetFile.length() > Integer.MAX_VALUE)
        {
            System.err.println("source or target is too large, max length is " + Integer.MAX_VALUE);
            System.err.println("aborting..");
            return;
        }

        Delta.computeDelta(sourceFile,targetFile,output);

        output.flush();
        output.close();
    }
}
