package solution;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TickNonceStrategy implements NonceStrategy
{
	private static Format format = new SimpleDateFormat("yyyyMMddHHmmss");
	public String generateNonce()
	{
		return format.format(new Date());
	}
}
