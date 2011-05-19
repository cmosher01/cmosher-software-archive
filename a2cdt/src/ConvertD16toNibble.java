import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.StringTokenizer;

// TODO fix the count of FF bytes between all addr/data areas
/*
 * Created on 2007-10-18
 */
/**
 * Converts a 16-sector logical disk image (Apple ][ DOS 3.3) to a nibble image.
 * 
 * @author christopher_mosher
 */
public class ConvertD16toNibble {
  private static boolean runProgram = true;
  private static int volume = 0xFE;
  private static final int TPD = 0x23; // tracks per disk
  private static final int SPT = 16; // sectors per track
  private static final int BPS = 0x0100; // bytes per sector
  private static final int[] sector16map = new int[] { 0x0, 0x7, 0xE, 0x6, 0xD, 0x5, 0xC, 0x4, 0xB, 0x3, 0xA, 0x2, 0x9, 0x1, 0x8, 0xF };

  /**
   * @param args
   *          command line args (see help)
   * @throws IOException
   *           if error occurs reading or writing standard streams
   */
  public static void main(String[] args) throws IOException {
    try {
      run(args);
    } catch (final IllegalArgumentException badarg) {
      System.err.println("Invalid argument. Use --help for help.");
      throw badarg;
    }
  }

  private static void run(final String... args) throws IOException {
    for (final String arg : args) {
      if (arg.startsWith("--")) {
        parseArg(arg.substring(2));
      } else {
        throw new IllegalArgumentException(arg);
      }
    }
    if (!runProgram) {
      return;
    }
    final int[][][] d16 = read16disk();
    write16nib(d16);
  }

  private static int[][][] read16disk() throws IOException {
    final int[][][] d16 = new int[TPD][SPT][BPS];
    final BufferedInputStream in = new BufferedInputStream(new FileInputStream(FileDescriptor.in));
    for (int t = 0; t < TPD; ++t) {
      for (int s = 0; s < SPT; ++s) {
        for (int b = 0; b < BPS; ++b) {
          d16[t][s][b] = in.read();
          if (d16[t][s][b] == -1) {
            throw new IOException("input file had less than 0x23000 bytes.");
          }
        }
      }
    }
    int eof = in.read();
    if (eof != -1) {
      throw new IOException("input file had more than 0x23000 bytes.");
    }
    in.close();
    return d16;
  }

  private static void write16nib(final int[][][] d16) throws IOException {
    final BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(FileDescriptor.out));
    for (int t = 0; t < TPD; ++t) {
      Util.nout(0x30, 0xFF, out);
      for (int s = 0; s < SPT; ++s) {
        sect16out(d16[t][sector16map[s]], t, s, out);
      }
      Util.nout(0x110, 0xFF, out);
    }
    out.flush();
    out.close();
  }

  private static void sect16out(final int[] data, final int track, final int sector, final OutputStream out) throws IOException {
    addr16out(track, sector, out);
    Util.nout(0x6, 0xFF, out);
    data16out(data, out);
    Util.nout(0x1B, 0xFF, out);
  }

  private static void addr16out(final int track, final int sector, final OutputStream out) throws IOException {
    out.write(0xD5);
    out.write(0xAA);
    out.write(0x96);
    Util.wordout(Nibblizer4and4.encode(volume), out);
    Util.wordout(Nibblizer4and4.encode(track), out);
    Util.wordout(Nibblizer4and4.encode(sector), out);
    Util.wordout(Nibblizer4and4.encode(volume ^ track ^ sector), out);
    out.write(0xDE);
    out.write(0xAA);
    out.write(0xEB);
  }

  private static void data16out(final int[] data, final OutputStream out) throws IOException {
    out.write(0xD5);
    out.write(0xAA);
    out.write(0xAD);
    final int[] nib = Nibblizer6and2.encode(data);
    Util.arrayout(nib, out);
    out.write(0xDE);
    out.write(0xAA);
    out.write(0xEB);
  }

  private static void parseArg(final String arg) {
    final StringTokenizer tok = new StringTokenizer(arg, "=");
    final String opt = Util.nextTok(tok);
    final String val = Util.nextTok(tok);
    if (opt.equals("volume")) {
      volume = Integer.decode(val).intValue();
    } else if (opt.equals("help")) {
      help();
    } else {
      throw new IllegalArgumentException(arg);
    }
  }

  private static void help() {
    System.err.println("usage:");
    System.err.println("    java ConvertD16toNibble [OPTIONS] <in.d16 >out.nib");
    System.err.println("OPTIONS:");
    System.err.println("    --help       show this help");
    System.err.println("    --volume=N   use N for disk volume (default: 0xFE)");
    runProgram = false;
  }
}
