package nu.mosher.mine.a2diskedit;

public class CatEntry
{
	private String sName;
	public CatEntry(byte[] rb)
	{
		byte[] rname = new byte[0x1e];
		System.arraycopy(rb,3,rname,0,0x1e);
		sName = rname;
	}
}
