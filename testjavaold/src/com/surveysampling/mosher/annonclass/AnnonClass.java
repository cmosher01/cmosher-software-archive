package com.surveysampling.mosher.annonclass;

class AnnonClass
{
    static private class x {}

    public AnnonClass()
    {
    }

    static public void main(String rArg[])
    {
        String s =
        (
            new x()
            {
                public String foo()
                {
                    return "foo";
                }
            }
        )
        .foo();

        System.out.println(s);
    }
}
