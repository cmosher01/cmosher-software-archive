import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Utility class for identifying loitering objects. Objects are tracked by 
 * calling ObjectTracker.add() when instantiated, and calling 
 * ObjectTracker.remove() when finalized. Only classes that implement 
 * ObjectTracker.Tracked can be tracked. As instances are created and 
 * destroyed, they are reported to the stdout. Summaries by class can also be 
 * reported on demand. To enable this functionality, add -DObjectTracker 
 * when running your program. This will track all classes that implement
 * ObjectTracker.Tracked and call add/remove as indicated in the
 * previous paragraph.
 * For a finer degree of control, specify a list of filters
 * when setting the <code>ObjectTracker</code> property. For instance,
 * -DObjectTracker=+MySpecialClass,-ClassFoo will only report o
 * on instances of classes whose name contains MySpecialClass
 * but not ClassFoo. Hence MySpecialClassBar will be tracked, while 
 * MySpecialClassFoo will not be. See <A HREF="ObjectTracker.html#start()"> 
 * start()</A> for more details.
 * Limitations
 * Since you must add instrumentation to all the classes you want to track,
 * this is not nearly as useful as a Memory Profiler/Debugger like
 * JProbe Profiler. Also, since it cannot tell you which references
 * are causing the object to loiter, it doesn't help you remove the loiterers.
 * If you want to solve the problem, you really need to use a Memory 
 * Profiler/Debugger like JProbe Profiler. The only thing ObjectTracker can 
 * help with is testing whether an instance of a known class goes away.
 * Implementation Notes
 * The current implementation assumes that every object has a unique
 * hashcode. A false assumption in general, but does work in JavaSoft's Win32 
 * VM for JDK1.1. This implementation will definitely not work in JavaSoft's
 * implementation of the Java 2 VM, including the HotSpot VM.
 */
public class ObjectTracker
{

    // Property ObjectTracker turns this on when set
    private final static boolean ENABLED = System.getProperty("ObjectTracker") != null;
    // Classes are hashed by name into this table.
    private static Hashtable classReg;
    private static Vector patterns;

    /** Record info about an object.  Class and ordinal number are stored. */
    private static class ObjectEntry
    {
        int ordinal; // distinguishes between mult. instances
        String clazz; // classname
        String name; // name (may be null)

        public ObjectEntry(int ordinal, String clazz, String name)
        {
            this.ordinal = ordinal;
            this.clazz = clazz;
            this.name = name;
        }
        public String toString()
        {
            return clazz + ":#" + ordinal + " (" + name + ")";
        }
    } // ObjectEntry
    /** Records info about a class. Within each class, a table of objects is 
     * maintained, along with next ordinal to use to stamp next object 
     * of this class. */
    private static class ClassEntry
    {
        String clazz; // class name
        Hashtable objects; // list of ObjectEntry
        int ordinal; // last instance of this class created
        public ClassEntry(String clazz)
        {
            this.clazz = clazz;
            objects = new Hashtable();
            ordinal = 1;
        }
        public String toString()
        {
            return clazz;
        }
        /** Get the name of the object by invoking getName().
         * Uses reflection to find the method. */
        private String getName(Object o)
        {
            String name = null;
            try
            {
                Class cl = o.getClass();
                Method m = cl.getMethod("getName", null);
                name = (m.invoke(o, null)).toString();
            }
            catch (Exception e)
            {
            }
            return name;
        }
        public void addObject(Object obj)
        {
            // Store this object in the object table
            Integer id = new Integer(System.identityHashCode(obj));
            ObjectEntry entry = new ObjectEntry(ordinal, clazz, getName(obj));
            objects.put(id, entry);
            ordinal++;
            System.out.println("    added: " + entry);
        }
        public void removeObject(Object obj)
        {
            // Removes this object from the object table
            Integer id = new Integer(System.identityHashCode(obj));
            ObjectEntry entry = (ObjectEntry) objects.get(id);
            objects.remove(id);
            System.out.println("    removed: " + entry);
        }
        /** Dump out a list of all object in this table */
        public void listObjects()
        {
            if (objects.size() == 0)
            {
                // skip empty tables
                return;
            }
            System.out.println("For class: " + clazz);
            Enumeration objs = objects.elements();
            while (objs.hasMoreElements())
            {
                ObjectEntry entry = (ObjectEntry) objs.nextElement();
                System.out.println("    " + entry);
            }
        }
    } // ClassEntry
    /** No constructor */
    private ObjectTracker()
    {
    }
    /**  Determine is this class name should be tracked. 
     * @return true if this class should be tracked. @see start */
    private static boolean isIncluded(String clazz)
    {
        int i = 0, size = patterns.size();
        if (size == 0)
        {
            // always match if list is empty
            return true;
        }
        boolean flag = false;
        for (; i < size; i++)
        {
            String pat = (String) patterns.elementAt(i);
            String op = pat.substring(0, 1); // + or -
            String name = pat.substring(1);
            if (name.equals("all"))
            {
                if (op.equals("+"))
                    flag = true; // match all, unless told otherwise
                else if (op.equals("-"))
                    flag = false; // match nothing, unless told otherwise
            }
            else if (clazz.indexOf(name) != -1)
            {
                // match if any of the filter names is a substring of
                // the class name
                if (op.equals("+"))
                    return true;
                else if (op.equals("-"))
                    return false;
            }
        }
        return flag;
    }
    /** Must be called before any objects can be tracked. Turns on object tracking
     * if property <code>ObjectTracker</code> is set.  In addition, the list of
     * patterns assigned to this property is stored for future pattern matching
     * by <code>isIncluded()</code>. This list of patterns must be supplied as a
     * comma-separated list, each preceded by <code>+</code> or <code>-</code>,
     * which indicates whether or not the pattern should cause matching classes to
     * be tracked or not. If property <code>ObjectTracker</code> has no values, 
     * it is equivalent to <code>+all</code>. */
    public static void start()
    {
        if (ENABLED)
        {
            classReg = new Hashtable();
            patterns = new Vector();

            String targets = System.getProperty("ObjectTracker");
            StringTokenizer parser = new StringTokenizer(targets, ",");
            while (parser.hasMoreTokens())
            {
                String token = parser.nextToken();
                patterns.addElement(token);
            }
        }
    }
    /** Add object to the tracked list. Will only be added if object's class has
     * not been filtered out. @param obj object to be added to tracking list */
    public static void add(Tracked obj)
    {
        if (ENABLED)
        {
            String clazz = obj.getClass().getName();
            if (isIncluded(clazz))
            {
                ClassEntry entry = (ClassEntry) classReg.get(clazz);
                if (entry == null)
                {
                    // first one for this class
                    entry = new ClassEntry(clazz);
                    classReg.put(clazz, entry);
                }
                entry.addObject(obj);
            }
        }
    }
    /** Removes object from tracked list.  This method should be called 
     * from the finalizer. @param obj object to be removed from tracking list  */
    public static void remove(Tracked obj)
    {
        if (ENABLED)
        {
            String clazz = obj.getClass().getName();
            if (isIncluded(clazz))
            {
                ClassEntry entry = (ClassEntry) classReg.get(clazz);
                entry.removeObject(obj);
            }
        }
    }
    /** Print tracked objects, summarized by class.  Also prints a
     * summary of free/total memory.  */
    public static void dump()
    {
        if (ENABLED)
        {
            Enumeration e = classReg.elements();
            while (e.hasMoreElements())
            {
                ClassEntry entry = (ClassEntry) e.nextElement();
                entry.listObjects();
            }
            System.out.println("==================================");
            System.out.println("Total Memory: " + Runtime.getRuntime().totalMemory());
            System.out.println("Free  Memory: " + Runtime.getRuntime().freeMemory());
            System.out.println("==================================");
            System.out.println("");
        }
    }
    /**  All classes that want to use this service must implement this
     * interface.  This forces this class to implement Object's finalize
     * method, which should call <code>ObjectTracker.remove()</code>. */
    public interface Tracked
    {
        /**  All classes that use ObjectTracker must implement a finalizer. */
        void finalize();
    }
}
