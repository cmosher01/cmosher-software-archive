package com.surveysampling.mosher.fixlf;

import java.io.InputStreamReader;
import java.io.BufferedReader;

public class Fixlf
{
    public static void main(String[] rArg) throws Throwable
    {
        InputStreamReader isr = null;
        BufferedReader br = null;
        try
        {
            isr = new InputStreamReader(System.in);
            br = new BufferedReader(isr);

            String s = br.readLine();
            while (s != null)
            {
                System.out.println(s);
                s = br.readLine();
            }
        }
        finally
        {
            br.close();
        }
    }
}
