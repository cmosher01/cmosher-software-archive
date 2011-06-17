import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/*
 * Created on Oct 9, 2004
 */


/**
 * TODO
 * 
 * @author Chris
 */
public class VolumeEntity
{
    protected List rSector = new ArrayList(); //VolumeSector

    /**
     * @return
     */
    public String toString()
    {
        return "";
    }

    /**
     * @param rPos
     */
    public void getPos(Collection rPos)
    {
        for (Iterator i = this.rSector.iterator(); i.hasNext();)
        {
            VolumeSector sect = (VolumeSector)i.next();
            rPos.add(sect.getPos());
        }
    }
}
