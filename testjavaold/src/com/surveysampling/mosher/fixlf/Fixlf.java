package com.surveysampling.mosher.fixlf;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

public class Fixlf
{
    public static void main(String[] rArg)
    {
        try
        {
            Fixlf m = new Fixlf();
        }
        catch (Throwable e)
        {
            System.err.println("Severe error: "+e.getMessage());
        }
    }

    Fixlf() throws IOException
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
            try
            {
                if (br != null)
                    br.close();
            }
            catch (Throwable t)
            {
            }
            try
            {
                if (isr != null)
                    isr.close();
            }
            catch (Throwable t)
            {
            }
        }
    }
};
