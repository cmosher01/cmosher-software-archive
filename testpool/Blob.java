import java.lang.ref.Reference;
import java.lang.ref.SoftReference;

class Blob
{
    public Blob()
    {
        System.out.println("Constructing " + id);
    }

    public String toString()
    {
        return "Blob: " + id;
    }

    protected void finalize() throws Throwable
    {
        System.out.println("finalizing: " + this);
    }

    private byte[] data = new byte[5000000];
    private int id = blobs++;

    private static int blobs = 0;

    public static void main(String[] args)
    {
        Reference[] theBlobs = new SoftReference[10];

        for (int i = 0; i < theBlobs.length; i++)
            theBlobs[i] = new SoftReference(new Blob());

        for (int i = 0; i < theBlobs.length; i++)
            System.out.println(theBlobs[i].get());
    }
}
