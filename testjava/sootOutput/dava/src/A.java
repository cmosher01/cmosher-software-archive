import java.io.*;

public abstract class A
{

    public A()
    {
        super();

        System.out.println("A constructor");
        return;
    }

    public void c()
    {
        System.out.println("A c");
        this.s();
        return;
    }

    public abstract void s();
}
