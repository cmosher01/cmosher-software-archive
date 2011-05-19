/*
 * Created on 2007-10-18
 */
/**
 * Nibble-encodes/decodes a byte using Apple ][ "4 and 4" encoding.
 * 
 * @author christopher_mosher
 */
public class Nibblizer4and4 {
  private Nibblizer4and4() {
    throw new IllegalStateException();
  }

  /**
   * <pre>
   * input byte: hgfedcba
   * output word: 1g1e1c1a1h1f1d1b
   * </pre>
   * 
   * @param n
   *          byte to be encoded
   * @return n, 4 and 4 encoded
   */
  public static int encode(final int n) {
    if (!(0 <= n && n < 0x100)) {
      throw new IllegalArgumentException("invalid byte: " + n);
    }
    // hgfedcba00000000
    // 000000000hgfedcb
    // | 1010101010101010
    // ------------------
    // 1g1e1c1a1h1f1d1b
    return (n << 8) | (n >> 1) | 0xAAAA;
  }

  /**
   * <pre>
   * input word: 1g1e1c1a1h1f1d1b
   * output byte: hgfedcba
   * </pre>
   * 
   * @param n
   *          4 and 4 encoded byte to be decoded
   * @return n, decoded
   */
  public static int decode(final int n) {
    // g1e1c1a1h1f1d1b1
    // & 000000001g1e1c1a
    // ------------------
    // 00000000hgfedcba
    return ((n << 1) | 1) & (n >> 8);
  }
}
