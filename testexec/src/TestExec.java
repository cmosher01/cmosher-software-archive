import java.io.IOException;
import java.io.InputStreamReader;

public class TestExec
{
    public static void main(String[] args) throws Throwable
    {
        System.out.println("parent process");
        execDumpOutput();
    }

    protected void execDumpOutput() throws InterruptedException, IOException
    {
        System.out.println("starting subprocess...");
        Process p = Runtime.getRuntime().exec("lsloop");

        LineDumper procout = new LineDumper(new InputStreamReader(p.getInputStream()));
        LineDumper procerr = new LineDumper(new InputStreamReader(p.getErrorStream()));

        p.waitFor();
        System.out.println("subprocess done");

        procout.waitFor();
        procerr.waitFor();

        int exitValue = p.exitValue();
        System.out.println("exit value: "+exitValue);
    }
}
