package com.surveysampling.mosher.testcount;

import java.io.*;
//import com.surveysampling.util.StreamCounter;

class TestCount
{
    TestCount()
    {
    }

    public static void main(String[] rArg) throws IOException
    {
        byte[] buf = { 65,10,67,10,68,10,69,10 };
        ByteArrayInputStream bais = new ByteArrayInputStream(buf);
        int a1 = bais.read();
        System.out.println(a1);
        int a2 = bais.read();
        System.out.println(a2);
        int c = 0;//StreamCounter.countRecords((InputStream)bais);
        System.out.println(c);
        int a3 = bais.read();
        System.out.println(a3);
        int a4 = bais.read();
        System.out.println(a4);
    }
}
