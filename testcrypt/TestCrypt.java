import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TestCrypt
{
    public static void main(String[] rArg) throws Throwable
    {
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG",sun.security.provider.SecureRandom);
        random.setSeed(random.generateSeed(128));
    }

    public static void showProviders()
    {
        Provider[] providers = Security.getProviders();
        for (int i = 0; i < providers.length; i++)
        {
            // Get services provided by each provider
            Set ent = providers[i].entrySet();
            for (Iterator e = ent.iterator(); e.hasNext();)
            {
                Map.Entry entry = (Map.Entry)e.next();
                String key = (String)entry.getKey();
                String val = (String)entry.getValue();
                System.out.print(key);
                System.out.print(", ");
                System.out.println(val);
            }
        }
    }
    public static String[] getCryptoImpls(String serviceType)
    {
        Set result = new HashSet();

        // All all providers
        Provider[] providers = Security.getProviders();
        for (int i = 0; i < providers.length; i++)
        {
            // Get services provided by each provider
            Set keys = providers[i].keySet();
            for (Iterator it = keys.iterator(); it.hasNext();)
            {
                String key = (String)it.next();
                key = key.split(" ")[0];

                if (key.startsWith(serviceType + "."))
                {
                    result.add(key.substring(serviceType.length() + 1));
                }
                else if (key.startsWith("Alg.Alias." + serviceType + "."))
                {
                    // This is an alias
                    result.add(key.substring(serviceType.length() + 11));
                }
            }
        }
        return (String[])result.toArray(new String[result.size()]);
    }
}
