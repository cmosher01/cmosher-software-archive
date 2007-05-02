/*
 * Created on Nov 13, 2005
 */
package nu.mine.mosher.uuid;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Enumeration;
import java.util.Random;
import java.util.UUID;
import org.safehaus.uuid.EthernetAddress;
import org.safehaus.uuid.UUIDGenerator;



/**
 * TODO
 *
 * @author Chris Mosher
 */
public class UUIDFactory
{
    private final UUIDGenerator gen;
    private final EthernetAddress macAddr;

    /**
     * Initializes the UUID generator. This may be a
     * time-consuming process.
     */
    public UUIDFactory()
    {
    	this(false);
    }

    /**
     * Initializes the UUID generator. If <code>quick</code> is
     * <code>false</code>, then this may be a
     * time-consuming process (5 seconds).
     * @param quick 
     */
    public UUIDFactory(final boolean quick)
    {
    	this.gen = UUIDGenerator.getInstance();
    	final Random rng = createRandomNumberGenerator(quick);
        this.gen.setRandomNumberGenerator(rng);
        this.macAddr = getMACAddress(rng);
    }

    private static EthernetAddress getMACAddress(final Random rng)
	{
        try
		{
			final Enumeration<NetworkInterface> networkInterfaces = java.net.NetworkInterface.getNetworkInterfaces();
			while (networkInterfaces.hasMoreElements())
			{
				final NetworkInterface inet = networkInterfaces.nextElement();
				final byte[] mac = inet.getHardwareAddress();
				if (mac != null && mac.length == 6)
				{
					return new EthernetAddress(mac);
				}
			}
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
		}
        return generateFakeMACAddress(rng);
	}

    /**
     * Creates a UUID.
     * @return the UUID
     */
    public UUID createUUID()
    {
        final byte[] data = this.gen.generateTimeBasedUUID(this.macAddr).toByteArray();
        long msb = 0;
        long lsb = 0;
        assert data.length == 16;
        for (int i=0; i<8; i++)
        {
            msb = (msb << 8) | (data[i] & 0xff);
        }
        for (int i=8; i<16; i++)
        {
            lsb = (lsb << 8) | (data[i] & 0xff);
        }
        return new UUID(msb,lsb);
    }



    /**
     * Creates and initializes a <code>SecureRandom</code>
     * object if possible, otherwise a <code>Random</code>
     * object.
     * @return the random number generator
     */
    private static Random createRandomNumberGenerator(final boolean quick)
    {
        /*
         * Setting these properties to blank causes
         * Sun's SHA1 algorithm to seed the RNG
         * with a seed generated based on system load
         * (creating bogus threads and timing
         * cycles or some such thing). It will perform this
         * calculation upon the first request for a
         * random number (which we do in the
         * primeRNG method). This is a pure
         * Java solution to seed generation that would
         * otherwise depend on OS-specific calls. We
         * choose to use the pure Java solution here.
         * Note that this is the time-consuming operation
         * referred to in the constructor's java-docs.
         * An overriding class can instead supply his
         * own seed by overriding seedRNG to call
         * rng.setSeed(seed)
         */
    	if (!quick)
    	{
	        System.setProperty("java.security.egd","");
	        Security.setProperty("securerandom.source","");
    	}

        Random rand = null;
        try
        {
            rand = SecureRandom.getInstance("SHA1PRNG","SUN");
        }
        catch (final Throwable e)
        {
            e.printStackTrace();
            rand = new Random();
        }

        seedRNG(rand);

        primeRNG(rand);

        return rand;
    }

    /**
     * Provides a hook for overriding classes to
     * seed the random number generator, if desired
     * (by calling <code>rng.setSeed(seed)</code>).
     * This method by default just generates a
     * random number by calling <code>nextLong</code>.
     * @param rng
     */
    private static void seedRNG(final Random rng)
    {
        rng.nextLong();
    }

    /**
     * Generates 2000 random numbers (longs) from the given
     * generator. Use this method to "prime" the
     * generator.
     * See Donald Knuth about "priming the pump"
     * http://www-cs-faculty.stanford.edu/~knuth/news02.html#rng
     * @param rng the random number generator
     */
    private static void primeRNG(final Random rng)
    {
        for (int i = 0; i < 2000; ++i)
        {
            rng.nextLong();
        }
    }

    /**
     * Generates a fake MAC address. Since Java cannot
     * (without using JNI) get the system NIC's MAC address,
     * we use a fake one containing the system's local IP
     * address (4 bytes) and a random number (2 bytes). If the
     * IP address is not available, then we just return 6 random
     * bytes. In any case, the multi-cast flag (bit) will be set
     * in the generated address.
     * @param rng the random number generator used to
     * generate the two random bytes for the MAC address
     * @return the fake MAC address (with multi-cast bit set)
     */
    private static EthernetAddress generateFakeMACAddress(final Random rng)
    {
        final byte[] dummy = new byte[6];

        // get the 2 random bytes
        final byte[] rand2 = new byte[3];
        rng.nextBytes(rand2);

        /*
         * Set the multi-cast flag bit, to indicate that this is
         * not a real MAC address.
         */
        dummy[0] = (byte)(rand2[2] | 1);

        dummy[1] = rand2[1];

        // get the 4 byte IP address (if possible, else random)
        final byte[] ip = getIPAddrElseRandom(rng);
        dummy[2] = ip[0];
        dummy[3] = ip[1];
        dummy[4] = ip[2];
        dummy[5] = ip[3];

        
        // create the object with our IP address and random bytes
        return new EthernetAddress(dummy);
    }

    /**
     * Gets the local IP address, if available; otherwise
     * returns 4 random bytes using the given <code>Random</code>.
     * @param rng
     * @return array of 4 bytes, never <code>null</code>
     */
    private static byte[] getIPAddrElseRandom(final Random rng)
    {
        byte[] ip = null;
        try
        {
            ip = InetAddress.getLocalHost().getAddress();
            if (ip == null)
            {
                throw new NullPointerException();
            }
        }
        catch (final Throwable e)
        {
            e.printStackTrace();
            ip = new byte[4];
            rng.nextBytes(ip);
        }
        return ip;
    }
}
