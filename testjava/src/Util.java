import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Logger;

public class Util
{
    private Util()
    {
        throw new UnsupportedOperationException();
    }

    public static void unused(Object o)
    {
        Object x = o;
        o = x;
    }

    public static void unused(boolean primitiveBool)
    {
        boolean x = primitiveBool;
        primitiveBool = x;
    }

    public static void unused(byte primitiveNum)
    {
        byte x = primitiveNum;
        primitiveNum = x;
    }

    public static void unused(short primitiveNum)
    {
        short x = primitiveNum;
        primitiveNum = x;
    }

    public static void unused(int primitiveNum)
    {
        int x = primitiveNum;
        primitiveNum = x;
    }

    public static void unused(long primitiveNum)
    {
        long x = primitiveNum;
        primitiveNum = x;
    }

    public static void unused(char primitiveNum)
    {
        char x = primitiveNum;
        primitiveNum = x;
    }

    public static void unused(float primitiveNum)
    {
        float x = primitiveNum;
        primitiveNum = x;
    }

    public static void unused(double primitiveNum)
    {
        double x = primitiveNum;
        primitiveNum = x;
    }

    public static void ignoreMainArguments(String[] rArguments)
    {
        if (rArguments.length > 0)
        {
            Logger.global.warning("Arguments ignored.");
        }
    }

    public static ArrayList list(Iterator i)
    {
        ArrayList r = new ArrayList();
        while (i.hasNext())
        {
            r.add(i.next());
        }
        return r;
    }
//  public static ArrayList<T> list(Iterable<T> i)
//  {
//      ArrayList<T> r = new ArrayList<T>();
//      for (T x : i)
//      {
//          r.add(x);
//      }
//      return r;
//  }

    public static HashSet set(Iterator i)
    {
        HashSet s = new HashSet();
        while (i.hasNext())
        {
            s.add(i.next());
        }
        return s;
    }
}
