//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.FileDescriptor;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
//import java.io.PrintWriter;
//import java.nio.charset.Charset;

public class Stdio
{
    public static final StdioBuffered buffered = new StdioBuffered();
    public static final StdioDirect direct = new StdioDirect();

    private Stdio()
    {
    }

    public static void main(String[] rArg) throws Throwable
    {
        Stdio.buffered.redirectOutputs();
        Runtime.getRuntime().gc();

        System.out.println("This is a test");
        System.out.print(42);
        System.out.println();
        System.out.flush();

        Stdio.direct.out.print("Enter a line of text: ");
        Stdio.direct.out.flush();
//        String s = Stdio.stdio.in.readLine();
//        Stdio.stdio.out.print("You entered: ");
//        Stdio.stdio.out.println(s);
//        Stdio.stdio.out.flush();
    }
}
