/**
 * @(#)FileUtil.java	April 3, 1998
 *
 * I MAKE NO WARRANTIES ABOUT THE SUITABILITY OF THIS SOFTWARE, EITHER
 * EXPRESS OR IMPLIED AND SHALL NOT BE LIABLE FOR ANY DAMAGES THIS
 * SOFTWARE MAY BRING TO YOUR SYSTEM. USE IT AT YOUR OWN RISK.
 *
 * Author : Steve Yeong-Ching Hsueh
 */

import java.io.FileInputStream;
import java.io.IOException;

/**
 * common file utilities
 */
public class FileUtil {

    public FileUtil() { }

    /**
     * read file and return its content in a String
     */
    public static String readFile(String filename) {
        StringBuffer content = new StringBuffer();
        try {
            FileInputStream fs = new FileInputStream(filename);
            byte buf[] = null;
            int  len   = 0;
            while ((len = fs.available()) > 0) {
                buf = new byte[len];
                fs.read(buf);
                content.append(new String(buf));
            }
            fs.close();
        }
        catch(IOException e) { }

        return content.toString();
    }



}