import java.io.IOException;
import java.io.InputStreamReader;

public class TestExec
{
    private static Process p;

    public static void main(String[] args) throws Throwable
    {
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            public void run()
            {
                System.out.println("parent shutdown handler");
                p.destroy();
            }
        });
        System.out.println("parent process");
        execDumpOutput();
    }

    protected static void execDumpOutput() throws InterruptedException, IOException
    {
        System.out.println("starting subprocess...");
        p = Runtime.getRuntime().exec("java -cp . Small");

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
