package com.surveysampling.mosher.ordinalenum;

class OrdinalEnum implements Comparable
{
    private final String name;
    private static int ordinalNext = 0;
    private final int ordinal = ordinalNext++;

    public String toString()
    {
        return name;
    }

    private OrdinalEnum(String s)
    {
        name = s;
    }

    public int compareTo(Object o)
    {
        return ordinal-((OrdinalEnum)o).ordinal;
    }

    public static final OrdinalEnum FIRST = new OrdinalEnum("first");
    public static final OrdinalEnum SECOND = new OrdinalEnum("second");



    public static void main(String rArg[])
    {
        System.out.println(OrdinalEnum.FIRST);
    }
}
