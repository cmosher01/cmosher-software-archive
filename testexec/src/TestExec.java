import java.io.IOException;

public class TestExec
{
    public static Process p;
    public static Flag done = new Flag();
//    public static LineDumper procout;
//    public static LineDumper procerr;

    public static void main(String[] args) throws Throwable
    {
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            public void run()
            {
                System.out.println("stopping subprocess...");
                p.destroy();
                try
                {
                    done.waitUntilTrue();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });
        System.out.println("parent process");
        execDumpOutput();
        waitFor();
        done.waitToSetTrue();
    }

    protected static void execDumpOutput() throws InterruptedException, IOException
    {
        System.out.println("starting subprocess...");

        p = Runtime.getRuntime().exec("java -cp . Small");
//        procout = new LineDumper(new InputStreamReader(p.getInputStream()));
//        procerr = new LineDumper(new InputStreamReader(p.getErrorStream()));
    }

    public static void waitFor()
    {
        try
        {
            p.waitFor();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        System.out.println("subprocess done");

//        procout.waitFor();
//        procerr.waitFor();

        int exitValue = p.exitValue();
        System.out.println("exit value: "+exitValue);
    }
}
