//import java.io.BufferedWriter;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;

public class StdioDirect
{
    public final InputStreamReader in;
    public final PrintWriter out;
    public final PrintWriter err;

    public StdioDirect()
    {
        in = new InputStreamReader(new FileInputStream(FileDescriptor.in));
        out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(FileDescriptor.out)));
        err = new PrintWriter(new OutputStreamWriter(new FileOutputStream(FileDescriptor.err)));
    }

    public StdioDirect(Charset cs)
    {
        in = new InputStreamReader(new FileInputStream(FileDescriptor.in),cs);
        out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(FileDescriptor.out),cs));
        err = new PrintWriter(new OutputStreamWriter(new FileOutputStream(FileDescriptor.err),cs));
    }

    public void redirectOutputs()
    {
        System.setOut(new PrintStreamAdapter(out));
        System.setErr(new PrintStreamAdapter(err));
    }
}
