import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class FilePrinter extends PrintWriter
{
    public FilePrinter(File file) throws FileNotFoundException
    {
        this(file,false);
    }

    public FilePrinter(File file, boolean append) throws FileNotFoundException
    {
        super(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,append))));
    }
}
