//package com.surveysampling.mosher.uuid;
//
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//import java.security.SecureRandom;
//
///** Generates cluster-safe UUID values based on current time and IP address.
//
//A UUID is a string-based primary key consisting of 32-digits (spaces inserted only 
//for clarity), encoded in hexadecimal as in figure X.1.  The string is composed 
//as follows:
//
//1.	Unique down to the millisecond. Digits 1-8 are the hex encoded lower 32 
//bits of the System.currentTimeMillis() call. 
//
//2.	Unique Across a Cluster. Digits 9-16 are the hex encoded representation 
//of the 32-bit integer of the underlying IP Address (an IP is divided into 
//4 separate bytes, appended together they form 32 bits).
//
//3.	Unique down to the objects within a Server. Digits 17-24 are the hex 
//representation of the call to System.identityHashCode(this), which is 
//guaranteed to return distinct integers for distinct objects within a machine 
//(the algorithm returns the memory address of an object). This assures that 
//multiple instances of a GUID generator on the same machine will not 
//create duplicate keys, even if in different JVMs.
//
//4.	Unique within an object within a millisecond. Finally, digits 25-32 
//represent a random 32 bit integer generated on every method call using the 
//Cryptographically strong java.security.SecureRandom class.  Thus multiple 
//calls to the same method within the same millisecond are guaranteed to be 
//unique. 
//
//Altogether, a UUID created using this algorithm is guaranteed to be unique 
//across all machines in a cluster, across all instances of UUID generators 
//within one machine, down to the millisecond and even down to the individual 
//method call within each millisecond. 	
//
//	java -cp \java;\java\dist\cordiem.jar com.cordiem.MySQL.UUID
//*/
//public class UUID {
//
//	/** the infamous self-referral. */
//    private static UUID self = null;
//
//	/** secure random to provide non-repeating seed. */
//	private static SecureRandom seeder;
//  
//	/** cached value for mid part of string. */
//	private static String midValue;
//
//	/** Returns the instance value.
//	 * @throws NtmException		a generic exception.
//	 * @return a UUID object representing the instance value
//	 */
//    public static synchronized UUID getInstance() /* throws NtmException */ {
//        if (self == null) {
//            self = new UUID();
//			self.initialize();
//        }
//        return( self );
//    }
//
//	/** This method is intended for use when only one UUID is needed. If you're
//	 * generated many UUID for an application, then call the getInstance()
//	 * method directly. And then call the getUUID() method each time a new UUID
//	 * is needed.
//	 */
//	public static String createUUID() /* throws NtmException */ {
//		UUID pt = UUID.getInstance();
//		return(pt.getUUID());
//	}
//
//	/** A no-nothing no-parameter constructor. */
//	private UUID() { 
//	}
//
//	/** Generates a hex code from the IP address and starts the randomizer.
//	 * @throws NtmException an exception thrown by the Cordiem Java classes.
//	 */
//	private void initialize() /* throws NtmException */ {
//		InetAddress inet = null;
//		
//		// get the internet address
//		try {
//	        inet = InetAddress.getLocalHost();
//		}
//		catch (UnknownHostException e) {
//			throw new /*NtmException*/Exception("Unable to determine local IP address.");
//		}
//
//        byte [] bytes = inet.getAddress();
//
//        midValue = ToHex.bytesToHex(bytes) + ToHex.intToHex(System.identityHashCode(this));
//
//        // load up the randomizer first
//        seeder = new SecureRandom();
//        int node = UUID.seeder.nextInt();
//    }
//
//	/** Gets a UUID value.
//	@return a UUID value.
//	*/
//    public String getUUID() 
//    {
////		System.out.println( "UUID; getUUID: Starting." );
//
//		return (
//			ToHex.intToHex((int) (System.currentTimeMillis() & 0xFFFFFFFF)) 
//			+ UUID.midValue 
//			+ ToHex.intToHex(seeder.nextInt())
//		);
//    }
//
//	/**
//	 * runs timing tests on the class's methods.
//	 * 
//	 * @param  args NOT USED; The command-line arguments.
//	 * @throws NtmException a generic Cordiem exception.
//	 */
//	public static void main(String args[]) throws NtmException
//	{
//		UUID pt = UUID.getInstance();
//		long timeStart = System.currentTimeMillis();
//		pt.initialize();
//		long timeEnd = System.currentTimeMillis();
//		long timeElapsed = timeEnd - timeStart;
//		System.out.println("UUID: initializatin took " + timeElapsed + " msecs.");
//
//		int n = 100000;
//
//		System.out.println("UUID: Each call to getUUID takes about 0.02 msecs on my laptop.");
//		System.out.println("");
//		System.out.println("UUID: performing timing test for " + n + " executions of getUUID.");
//
//		timeStart = System.currentTimeMillis();
//		for (int i=0; i < n; i++) {
//			pt.getUUID();
//		}		
//		timeEnd = System.currentTimeMillis();
//		timeElapsed = timeEnd - timeStart;
//		System.out.println("    " + n + " executions took " + timeElapsed + " msecs.");
//		System.out.println("    Each call took " + ( timeElapsed / n) + " msecs.");
//	}
//}
