/**
 * @(#)DebugTracer.java	April 3, 1998
 *
 * I MAKE NO WARRANTIES ABOUT THE SUITABILITY OF THIS SOFTWARE, EITHER
 * EXPRESS OR IMPLIED AND SHALL NOT BE LIABLE FOR ANY DAMAGES THIS
 * SOFTWARE MAY BRING TO YOUR SYSTEM. USE IT AT YOUR OWN RISK.
 *
 * Author : Steve Yeong-Ching Hsueh
 */

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * DebugTracer is used for tracking the calling functions in the stack.
 * It could be used to print out function names and line numbers for debugging.
 */
public class DebugTracer extends Throwable {
 PrintWriter pw;
 PipedOutputStream ppo = new PipedOutputStream();
 PipedInputStream  ppi;

 /**
  * constructor
  */
 public DebugTracer() {
    try {
        ppi = new PipedInputStream(ppo);
    }
    catch (IOException e) { }
    pw  = new PrintWriter(ppo, true);
 }

 /**
  * return the calling source
  */
 public String getSource() {

    Vector vt = getVectorStack(3);
    if(vt == null) return "source unknown";
    if(vt.size() < 2) return "source unknown";
    return (String)vt.elementAt(2);
 }

 /**
  * return the calling source
  */
 public String getSource(int depth) {

    Vector vt = getVectorStack(depth+2); // need to add this method's level
    StringBuffer source = new StringBuffer();
    if(vt.size() < 2) return "";
    for(int i=2; i<vt.size(); i++) {
        source.append((String)vt.elementAt(i)).append("\n");
    }
    return source.toString();
 }

 /**
  * print current stack
  */
 public void printStack(int depth) {

    Vector vt = getVectorStack(depth+1);
    for(int i=1;  i<vt.size(); i++) {
     System.out.println((String)vt.elementAt(i));
    }

 }

 /**
  * return stack stored in a vector
  */
 public Vector getVectorStack(int depth) {
    byte   buf[] = new byte[2048];
    String stack = "";
    String temp  = "";
    short  count = 0;
    fillInStackTrace();
    printStackTrace(pw);

    try {
        int avail;
        while((avail = ppi.available()) > 0) {
            ppi.read(buf, 0, avail);
            stack += new String(buf, 0, avail);
        }
    }
    catch (IOException e) { }
    int l = 0;
    // System.out.println(stack);

    // not a safe way, may not work with all jdks
    StringTokenizer st = new StringTokenizer(stack, "\n");
    Vector vt = new Vector();
    while (st.hasMoreTokens()) {

        temp = st.nextToken();
        if(++count < 2) continue;              // don't count the first 2 level
        l = temp.indexOf("at ");               // search for "at "
        if(l >= 0) temp = temp.substring(l+3); // skip  over "at "
        vt.addElement(temp);
        if(count >= depth+1 ) break;           // check depth; first 2 level doesn't count
    }
    return vt;
 }



}