/**
    A basic Java class stub for a Win32 Console Application.
*/
package com.surveysampling.mosher.testex;


public class TestEx
{
    public static class e1 extends java.lang.Exception
    {
        public e1()
        {
            super("e1");
        }
    }

    public static class e2 extends java.lang.Exception
    {
        public e2()
        {
            super("e2");
        }
    }

	public TestEx()
	{
	}

    public void test()
    {
        try
        {
            throw new e1();
        }
        catch (e1 e)
        {
            System.out.println(e.getMessage());
        }
    }

    static public void main(String args[])
    {

        try
        {
            TestEx x = new TestEx();
            x.test();
        }
        catch (Throwable e)
        {
            System.out.println("main exception handler");
        }
/*        File f = new File("test.txt");
        int size = (int)f.length();

        FileInputStream in = null;
        try
        {
            in = new FileInputStream(f);

            byte[] data = new byte[size];
            int bytes_read = 0;
            while(bytes_read < size)
                bytes_read += in.read(data, bytes_read, size-bytes_read);
        }
        finally
        {
            if (in != null)
                in.close();
        }
        */
/*        String x = "3";
        System.out.println(Integer.valueOf(x));

        String ja[] = new String[7];
        ja[0] = "test1";
        ja[6] = "test6";
        //ja[7] = "bad";
        System.out.println(ja.getClass());
        System.out.println(ja[0] );
        System.out.println(ja[6]);
*/
/*        if (.3+.3+.3 == .9)
            System.out.println("yes");
        else
            System.out.println("no");*/
    }
}
