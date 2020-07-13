/*
    ByteAnalyzer
    Analyzes byte counts in a text file.

    Copyright © 2004, 2005, 2011, 2018, 2020, by Christopher Alan Mosher, Shelton, Connecticut, USA, cmosher01@gmail.com .

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

/*
 * Created on Jun 15, 2004
 */

package nu.mine.mosher.text;

import java.io.*;
import java.nio.charset.*;
import java.util.*;

/**
 * Checks each byte of a file, and maintains a count of occurrences of each byte
 * value in the file.
 *
 * @author Chris Mosher
 */
public class ByteAnalyzer {
    private static final int NORMAL = 0;
    private static final int CR = 1;
    private static final int LF = 2;

    private final Map<Integer, Integer> cBytes = new TreeMap<>();
    private int cCR;
    private int cLF;
    private int cCRLF;
    private int cLFCR;

    private int state;

    /**
     * Main program entry point. Creates a <code>ByteAnalyzer</code> object and
     * calls its <code>execute</code> method.
     *
     * @param args array of one argument, the file specification to analyze
     * @throws IOException
     */
    public static void main(final String... args) throws IOException {
        boolean help = false;
        boolean verbose = false;
        for (final String arg : args) {
            if (arg.startsWith("-")) {
                switch (arg) {
                    case "--help":
                    case "-h":
                        help = true;
                        break;
                    case "--verbose":
                    case "-v":
                        verbose = true;
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid option: " + arg);
                }
            }
        }

        if (help) {
            help();
        } else {
            for (final String arg : args) {
                if (!arg.startsWith("-")) {
                    final ByteAnalyzer prog = new ByteAnalyzer();
                    prog.execute(new File(arg).getCanonicalFile(), verbose);
                }
            }
        }

        System.out.flush();
        System.err.flush();
    }

    private static void help() {
        System.out.println("Usage:");
        System.out.println("    byte-analyzer [OPTIONS] input-file");
        System.out.println("Options:");
        System.out.println("    --help, -h     show help, and exit");
        System.out.println("    --verbose, -v  include normal characters (20-7E)");
    }

    /**
     * Counts occurrences of each byte value in the given file.
     *
     * @param f the <code>File</code> to analyze
     * @param verbose
     * @throws IOException
     */
    public void execute(final File f, final boolean verbose) throws IOException {
        final BufferedInputStream in = new BufferedInputStream(new FileInputStream(f.getCanonicalFile()));
        int x = in.read();
        while (x != -1) {
            switch (this.state) {
                case NORMAL: {
                    if (x == 0x0d) {
                        this.state = CR;
                    } else if (x == 0x0a) {
                        this.state = LF;
                    } else {
                        incByte(x);
                    }
                }
                break;
                case CR: {
                    if (x == 0x0a) {
                        ++this.cCRLF;
                        this.state = NORMAL;
                    } else if (x == 0x0d) {
                        ++this.cCR;
                    } else {
                        ++this.cCR;
                        incByte(x);
                        this.state = NORMAL;
                    }
                }
                break;
                case LF: {
                    if (x == 0x0d) {
                        ++this.cLFCR;
                        this.state = NORMAL;
                    } else if (x == 0x0a) {
                        ++this.cLF;
                    } else {
                        ++this.cLF;
                        incByte(x);
                        this.state = NORMAL;
                    }
                }
                break;
            }
            x = in.read();
        }

        in.close();

        if (this.state == CR) {
            ++this.cCR;
        } else if (this.state == LF) {
            ++this.cLF;
        }

        System.out.println(f);
        showMap(verbose);
    }

    private void incByte(final int x) {
        this.cBytes.merge(x, 1, Integer::sum);
    }

    /**
     * Prints a summary of the analyzed file.
     * @param verbose
     */
    public void showMap(final boolean verbose) {
        for (final Map.Entry<Integer, Integer> entry : this.cBytes.entrySet()) {
            final int cnt = entry.getValue();
            if (0 < cnt) {
                final int byt = entry.getKey();
                String s;
                final String c = new String(new byte[] {(byte)byt}, Charset.forName("windows-1252"));
                if (verbose || !verbose(byt)) {
                    if (printable(byt)) {
                        s = String.format("%02x %1s: %10d", byt, c, cnt);
                    } else {
                        s = String.format("%02x  : %10d", byt, cnt);
                    }
                    System.out.println(s);
                }
            }
        }

        if (this.cCR > 0) {
            System.out.println(String.format("%-4s: %10d", "CR", this.cCR));
        }
        if (this.cLF > 0) {
            System.out.println(String.format("%-4s: %10d", "LF", this.cLF));
        }
        if (this.cCRLF > 0) {
            System.out.println(String.format("%-4s: %10d", "CRLF", this.cCRLF));
        }
        if (this.cLFCR > 0) {
            System.out.println(String.format("%-4s: %10d", "LFCR", this.cLFCR));
        }
    }

    private static boolean verbose(final int byt) {
        return ' ' <= byt && byt <= '~';
    }

    private static boolean printable(final int byt) {
        return
            ' ' <= byt &&
            byt != 0x81 &&
            byt != 0x8d &&
            byt != 0x8f &&
            byt != 0x90 &&
            byt != 0x9d;
    }
}
