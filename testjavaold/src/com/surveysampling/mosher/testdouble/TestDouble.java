package com.surveysampling.mosher.testdouble;

public class TestDouble
{
    public static void main(String[] rArg)
    {
//        System.getProperties().list(System.out);

        for (int i = 0; i<10; i++)
        {
            System.out.print(i);
            System.out.print(": ");
            System.out.print((int)(0.1+2.3*i));
            System.out.print(", ");
            System.out.println((int)(0.1+2.3*i+1e-12));
        }

/*        Runtime r = Runtime.getRuntime(); 
        long free = r.freeMemory();
        long tot = r.totalMemory();

        System.out.print("total memory: ");
        System.out.println(tot);
        System.out.print("free memory: ");
        System.out.println(free);
*/
    }
}
