import java.io.IOException;
import java.io.OutputStream;
import java.util.StringTokenizer;

/*
 * Created on 2007-10-17
 */
/**
 * Static utility methods.
 * @author christopher_mosher
 */
public final class Util {
  private Util() {
    throw new IllegalStateException();
  }

  /**
   * Writes a word to a stream.
   * @param word 16-byte word to write
   * @param out stream to write word to
   * @throws IOException if there is an error writing
   */
  public static void wordout(int word, OutputStream out) throws IOException {
    out.write(word);
    out.write(word >> 8);
  }

  /**
   * Writes a bytes a given number of times to the given stream.
   * @param n number of times to write byt
   * @param byt byte to write
   * @param out stream to write word to
   * @throws IOException if there is an error writing
   */
  public static void nout(int n, int byt, OutputStream out) throws IOException {
    for (int i = 0; i < n; ++i) {
      out.write(byt);
    }
  }

  /**
   * Writes an array of bytes to the given stream.
   * @param r the array of bytes to write
   * @param out stream to write word to
   * @throws IOException if there is an error writing
   */
  public static void arrayout(final int[] r, final OutputStream out) throws IOException {
    for (int i = 0; i < r.length; ++i) {
      out.write(r[i]);
    }
  }

  /**
   * Gets the next token from the given {@link StringTokenizer}. If there are
   * not more tokens, return an empty string.
   * 
   * @param tok
   *          the tokenizer
   * @return next token, or emtpy string if no more tokens
   */
  public static String nextTok(final StringTokenizer tok) {
    if (!tok.hasMoreTokens()) {
      return "";
    }
    return tok.nextToken();
  }
}
