/**
 * @(#)byteArrayOperator.java	April 3, 1998
 *
 * I MAKE NO WARRANTIES ABOUT THE SUITABILITY OF THIS SOFTWARE, EITHER
 * EXPRESS OR IMPLIED AND SHALL NOT BE LIABLE FOR ANY DAMAGES THIS
 * SOFTWARE MAY BRING TO YOUR SYSTEM. USE IT AT YOUR OWN RISK.
 *
 * Author : Steve Yeong-Ching Hsueh
 */



/**
 * This class contains common functions for byte array operation
 */
public class byteArrayOperator {

    byteArrayOperator() { }

    /**
     * copy length of source to end of target. target must end with 0
     */
    public static void copy(byte[] target, int last_byte, byte[] source, int length) {
        for(int i=0; i<length; i++) {
            target[last_byte+i] = source[i];
        }
    }

    /**
     * check if this byte array is printable
     * true if printable is 3 times or greater the number of none-printable
     */
    public static boolean isPrintable(byte[] in) {
        if(in == null) return false;
        return isPrintable(in, in.length);
    }

    /**
     * check if this byte array is printable
     * true if printable is 3 times or greater the number of none-printable
     */
    public static boolean isPrintable(byte[] in, int length) {
        if(in == null || length <= 0) return false;

        int pcount  = 0;  // printable count
        int npcount = 0;  // none printable count
        int dif     = 0;

        if(length < 10) {
            for(int i=0; i<length; i++)
                if(in[i] > 31 && in[i] < 127) pcount++;
                else npcount++;
        }
        else {
            dif = length/10;
            for(int i=0; i<length; i += dif) {
                if(in[i] > 31 && in[i] < 127) pcount++;
                else npcount++;
            }
        }

        if(npcount == 0 || (pcount/npcount) >= 3) return true;

        return false;
    }

} //