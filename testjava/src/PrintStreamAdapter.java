import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

public class PrintStreamAdapter extends PrintStream
{
    private final PrintWriter writer;

    public PrintStreamAdapter(PrintWriter writer)
    {
        super(new ByteArrayOutputStream());
        this.writer = writer;
    }

    public boolean checkError()
    {
        return writer.checkError();
    }

    public void close()
    {
        writer.close();
    }

    public void flush()
    {
        writer.flush();
    }

    public void print(boolean b)
    {
        writer.print(b);
    }

    public void print(char c)
    {
        writer.print(c);
    }

    public void print(char[] s)
    {
        writer.print(s);
    }

    public void print(double d)
    {
        writer.print(d);
    }

    public void print(float f)
    {
        writer.print(f);
    }

    public void print(int i)
    {
        writer.print(i);
    }

    public void print(long l)
    {
        writer.print(l);
    }

    public void print(Object obj)
    {
        writer.print(obj);
    }

    public void print(String s)
    {
        writer.print(s);
    }

    public void println()
    {
        writer.println();
    }

    public void println(boolean x)
    {
        writer.println(x);
    }

    public void println(char x)
    {
        writer.println(x);
    }

    public void println(char[] x)
    {
        writer.println(x);
    }

    public void println(double x)
    {
        writer.println(x);
    }

    public void println(float x)
    {
        writer.println(x);
    }

    public void println(int x)
    {
        writer.println(x);
    }

    public void println(long x)
    {
        writer.println(x);
    }

    public void println(Object x)
    {
        writer.println(x);
    }

    public void println(String x)
    {
        writer.println(x);
    }

    public void write(int b)
    {
        writer.write(b);
    }

    public void write(byte[] b) throws IOException
    {
        throw new UnsupportedOperationException();
    }

    public void write(byte[] buf, int off, int len)
    {
        throw new UnsupportedOperationException();
    }
}
