/**
 * @(#)ServerInterface.java April 3, 1998 I MAKE NO WARRANTIES ABOUT THE
 * SUITABILITY OF THIS SOFTWARE, EITHER EXPRESS OR IMPLIED AND SHALL NOT BE
 * LIABLE FOR ANY DAMAGES THIS SOFTWARE MAY BRING TO YOUR SYSTEM. USE IT AT YOUR
 * OWN RISK. Author : Steve Yeong-Ching Hsueh
 */

import java.util.Properties;



interface ServerInterface
{
    public void showServerResponse(String msg);

    public void showClientRequest(String msg);

    public void updateHTTPCounter();

    public void logAccess(int level, String src, String msg);

    public void logError(String src, String msg);

    public boolean re_initialization();

    public Properties getHTTPConfiguration();

    public ProxyCachePool getProxyCachePool();
}