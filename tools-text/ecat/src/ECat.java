import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class ECat
{
    public static void main(String[] args) throws Throwable
    {
        String linesep = System.getProperty("line.separator");
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(FileDescriptor.in)));
        OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(FileDescriptor.out));
        for (String s = in.readLine(); s != null; s = in.readLine())
        {
            out.write(s);
            out.write(linesep);
            out.flush();
        }
    }
}
