public class GetClass
{
    public static void main(java.lang.String[] args)
    {
        someStaticMethod();
    }

    public static void someStaticMethod()
    {
        System.out.println("I'm in " + new CurrentClassGetter().getClassName() + " class");
    }

    public static class CurrentClassGetter extends SecurityManager
    {
        public String getClassName()
        {
            return getClassContext()[1].getName();
        }
    }
}
