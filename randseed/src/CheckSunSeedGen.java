import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;

/*
 * Created on April 25, 2005
 */

/**
 * TODO
 *
 * @author Chris Mosher
 */
public class CheckSunSeedGen
{
	public static void main(String[] args) throws NoSuchAlgorithmException
	{
        System.setProperty("java.security.egd","");
        Security.setProperty("securerandom.source","");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        long r = random.nextLong();
        System.out.println(Long.toHexString(r));
	}
}
