import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.SortedMap;

import com.surveysampling.hash.EnglishWord;
import com.surveysampling.hash.SimpleHashAssocArray;
import com.surveysampling.hash.algorithm.*;
import com.surveysampling.queue.SimpleQueue;
import com.surveysampling.queue.SimpleStack;

public class Play
{
    private static final int POSITIVE_BITS = ~(1<<31);
    public static void main(String[] args) throws UnsupportedEncodingException, IOException
    {
//        final Map<String,Charset> map = Charset.availableCharsets();
//        for (final String name : map.keySet())
//        {
//            System.out.println(name);
//        }


        byte[] rb = new byte[] { 65, 32, -125, 33 };
        String cvt = new String(rb,"windows-1252");
        for (int i = 0; i < cvt.length(); ++i)
        {
            final char c = cvt.charAt(i);
            System.out.println(Integer.toHexString(c&0xffff));
        }
        System.out.println(cvt);
        byte[] bytes = cvt.getBytes("windows-1252");
        for (byte b : bytes)
        {
            System.out.println(Integer.toString(b));
        }
        System.out.println();
        System.out.println("default:");
        System.out.println(Charset.defaultCharset().name());
        System.out.println(Charset.defaultCharset().displayName());
        System.out.println(Charset.defaultCharset().getClass().getName());








//        final SimpleHashAssocArray<EnglishWord,Integer> map = new SimpleHashAssocArray<EnglishWord,Integer>(12289);
//        final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(new File("c:/dictionary/dict.txt"))));
//        int words = 0;
//        final long start = System.currentTimeMillis();
//        for (String word = in.readLine(); word != null; word = in.readLine())
//        {
//            ++words;
//
//            final EnglishWord eng = new EnglishWord(word);
//            map.put(eng,words);
//        }
//        final long end = System.currentTimeMillis();
//        in.close();
//
//        System.out.println(end-start+" ms");
//        System.out.println("word count: "+words);
//        final Collection<Integer> rSize = new ArrayList<Integer>(12289);
//        map.getBucketSizes(rSize);
//        System.out.println("count of buckets by size: (size:count)");
//        final SimpleHashAssocArray<Integer,Integer> sizes = new SimpleHashAssocArray<Integer,Integer>(64);
//        for (final Integer siz : rSize)
//        {
//            if (!sizes.containsKey(siz))
//            {
//                sizes.put(siz,0);
//            }
//            sizes.put(siz,sizes.get(siz)+1);
//        }
//
//        final List<SimpleHashAssocArray<Integer,Integer>.KeyValuePair> rSizes = new ArrayList<SimpleHashAssocArray<Integer,Integer>.KeyValuePair>();
//        sizes.getEntries(rSizes);
//        for (final SimpleHashAssocArray<Integer,Integer>.KeyValuePair pair : rSizes)
//        {
//            System.out.print(pair.getKey());
//            System.out.print("\t");
//            System.out.print(pair.getValue());
//            System.out.println();
//        }












//        System.out.println(Integer.toHexString(-1>>>1));

//        System.out.println(Integer.toHexString(POSITIVE_BITS));
//        final SimpleQueue<String> q = new SimpleQueue<String>();
//        q.offer("one");
//        q.offer("two");
//        q.offer("three");
//        q.offer("four");
//
//        System.out.println(q);
//        for (String s = q.poll(); s != null; s = q.poll())
//        {
//            System.out.println(s);
//        }
//
//
//        final SimpleStack<String> stack = new SimpleStack<String>();
//        stack.push("one");
//        stack.push("two");
//        stack.push("three");
//        stack.push("four");
//
//        System.out.println(stack);
//        for (String s = stack.pop(); s != null; s = stack.pop())
//        {
//            System.out.println(s);
//        }




//        final int[][] rcNib = new int[16][8];
//
//        final HashingAlgorithm hash = new RSHash();
//
//        final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(new File("c:/dictionary/dict.txt"))));
//        int words = 0;
//        final long start = System.currentTimeMillis();
//        for (String word = in.readLine(); word != null; word = in.readLine())
//        {
//            ++words;
//            int h = hash.hash(word.getBytes("US-ASCII"));
////            int h = word.hashCode();
////            h += ~(h << 9);
////            h ^=  (h >>> 14);
////            h +=  (h << 4);
////            h ^=  (h >>> 10);
//
//            for (int nib = 0; nib < 8; ++nib)
//            {
//                ++rcNib[h&0xF][nib];
//                h >>= 4;
//            }
//        }
//        final long end = System.currentTimeMillis();
//        in.close();
//
//        System.out.println(end-start+" ms");
//
//        final int avg = words/16;
//        System.out.println(avg);
//
//        System.out.print("  ");
//        for (int val = 0; val < 0x10; ++val)
//        {
//            System.out.printf("%8X",val);
//        }
//        System.out.println();
//
//        for (int nib = 0; nib < 8; ++nib)
//        {
//            System.out.printf("%1d ",nib);
//            for (int val = 0; val < 0x10; ++val)
//            {
//                System.out.printf("  %+6d",rcNib[val][nib]-avg);
//            }
//            System.out.println();
//        }
        
        
        
        
//        int h = 0x12345678;
//        h += ~(h << 9);
//        h ^=  (h >>> 14);
//        h +=  (h << 4);
//        h ^=  (h >>> 10);
//        System.out.printf("%08X\n",h); // 41F1BF7B
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
//        final Set<HashingAlgorithm> hashes = new HashSet<HashingAlgorithm>();
//        HashManager.addAllHashingAlgorithms(hashes);
//
//        final int size = 32;
//
//        final BloomFilter bloom = new BloomFilter(size,hashes);
//        bloom.add("Test");
//
//        System.out.println(bloom.contains("Test"));
//        System.out.println(bloom.contains("TestTwo"));
//        System.out.println(bloom.contains("TestA"));
//        System.out.println(bloom.contains("tesT"));
//        System.out.println(bloom.contains("test"));
//        System.out.println(bloom.contains("test "));

//        final int bb = bb(); // 10111011
//        final int fb = fb(); // 11111011
//        final boolean ok = ((bb | 0x40) == fb);

//        final boolean ok2 = ((bb() | 0x40) == fb());

//        System.out.println(ok);
//        System.out.println(bb | 0x40);
//        System.out.println(fb);
//        System.out.println(anint());

//        byte a01 = (byte)0x00000001;
//        byte a02 = (byte)0x00000002;
//        byte a04 = (byte)0x00000004;
//        byte a08 = (byte)0x00000008;
//        byte a10 = (byte)0x00000010;
//        byte a20 = (byte)0x00000020;
//        byte a40 = (byte)0x00000040;
//        byte a80 = (byte)0x00000080;

//        byte t81 = (byte)0x00000081;
//        byte t18 = (byte)0x00000018;
//
//        System.out.println((a80 & t81) == a80);
//        System.out.println((a80 & t81) != 0);
//
//        System.out.println((a08 & t18) == a08);
//        System.out.println((a08 & t18) != 0);

//        final byte m = a80;
//        for (int i = 0; i < 256; ++i)
//        {
//            final byte t = (byte)i;
//            final boolean t1 = ((t & m) == m);
//            final boolean t2 = ((t & m) != 0);
//
//            if (t1 != t2)
//            {
//                System.out.println("Error at "+i);
//            }
//        }

//        final boolean a1 = (1 & 1) != 0;
//        System.out.println(a1);
//        final long HighBits = (~0L) << 28;
//
//        System.out.println(HighBits);
//        System.out.println(0xF0000000);
//        System.out.println(Long.toHexString(~0));
//        System.out.println(Long.toHexString(~0L));
//        System.out.println(Long.toHexString((long)~0 & (int)~0));
//
//        final int m = 5;
//        for (int i = -40; i <= 40; ++i)
//        {
//            final int r = ((i % m) + m) % m;
//            final int r4 = i % m;
//            final int r3 = (int)((((long)i)&0x00000000ffffffffl) % (long)m);
//            final int r2 = positiveMod(i,m);
//            final int r5 = (int)((i + ~0) % m);
//            System.out.printf("%3d %3d %3d %3d %3d %3d\n",i,r,r2,r3,r4,r5);
//        }

//        int h = 0x52222222;
//        int g = h & 0xf0000000;
//        int i = h ^ g ^ (g >> 28);
//        System.out.println(Integer.toHexString(h)+" "+Integer.toHexString(g)+" "+Integer.toHexString(i));
//        System.out.println(Integer.toHexString((1 << 28) - 1));
    }

    private static byte bb() { return (byte)0xBB; }
    private static byte fb() { return (byte)0xFB; }
//    private static int anint() { return 77777; }

    private static int positiveMod(final int x, final int mod)
    {
        final int r = x % mod;
        if (r < 0)
        {
            return r + mod;
        }
        return r;
    }
}
