/*
 * Created on Nov 18, 2005
 */
package nu.mine.mosher.grodb.persist;

import nu.mine.mosher.grodb.persist.key.FacsimileKey;
import nu.mine.mosher.grodb.persist.key.SourceID;
import com.sleepycat.persist.model.DeleteAction;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

@Entity
public class Facsimile
{
	@PrimaryKey
	private final FacsimileKey id;

	@SecondaryKey(relate=Relationship.MANY_TO_ONE,relatedEntity=Source.class,onRelatedEntityDelete=DeleteAction.CASCADE)
	private final SourceID fkSource;

	private final String mimetype;
	private final byte[] rawdata;

	private Facsimile()
	{
		this.id = null;
		this.fkSource = null;
		this.mimetype = null;
		this.rawdata = null;
	}

	/**
	 * @param id 
	 * @param mimetype
	 * @param rawdata
	 */
	public Facsimile(final FacsimileKey id, final String mimetype, final byte[] rawdata)
	{
		this.id = id;
		this.fkSource = id.getSource();
		this.mimetype = mimetype;
		this.rawdata = new byte[rawdata.length];
		System.arraycopy(rawdata,0,this.rawdata,0,this.rawdata.length);
	}

	/**
	 * @return ID (primary key)
	 */
	public FacsimileKey getID()
	{
		return this.id;
	}

	/**
	 * @return Returns the mime type.
	 */
	public String getMimeType()
	{
		return this.mimetype;
	}

	/**
	 * @return number of bytes in raw data
	 */
	public int getRawDataSize()
	{
		return this.rawdata.length;
	}

	/**
	 * @param fill array of bytes to copy raw data into
	 */
	public void getRawData(final byte[] fill)
	{
		System.arraycopy(this.rawdata,0,fill,0,this.rawdata.length);
	}
}
