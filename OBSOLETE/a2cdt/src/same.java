import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/*
 * Created on 2007-10-22
 */
/**
 * Checks to see if two binary files are the same or not.
 * @author christopher_mosher
 */
public class same {
  /**
   * Exists with status 1 if the files are different, 0 if they are the same.
   * @param args command line args: file1 file2
   * @throws IOException if there is an error reading the files
   */
  public static void main(String[] args) throws IOException {
    if (args.length != 2) {
      throw new IllegalArgumentException("usage: java same file1 file2");
    }
    final BufferedInputStream in1 = new BufferedInputStream(new FileInputStream(new File(args[0])));
    final BufferedInputStream in2 = new BufferedInputStream(new FileInputStream(new File(args[1])));
    if (!sameContents(in1, in2)) {
      System.exit(1);
    }
  }

  /**
   * Checks if the two binary streams have the exact same contents.
   * @param in1 stream 1
   * @param in2 stream 2
   * @return <code>true</code> if in1 and in2 have the same contents
   * @throws IOException if there is an error reading the streams
   */
  public static boolean sameContents(final BufferedInputStream in1, final BufferedInputStream in2) throws IOException {
    int b1 = in1.read();
    int b2 = in2.read();
    while (b1 >= 0 && b2 >= 0) {
      if (b1 != b2) {
        return false;
      }
      b1 = in1.read();
      b2 = in2.read();
    }
    return b1 == b2;
  }
}
