import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/*
 * Created on Oct 12, 2004
 */


/**
 * TODO
 * 
 * @author Chris
 */
public class VolumeFileRecovered
{
    private VolumeTSMap ts;
    private VolumeFileData data;

    /**
     * @param pos
     * @param disk
     * @throws InvalidPosException
     */
    public void readFromMedia(DiskPos pos, Disk disk) throws InvalidPosException
    {
        List rPosFile = new ArrayList();
        ts = new VolumeTSMap();
        ts.readFromMedia(pos,disk);
        ts.getTS(rPosFile);
        data = new VolumeFileData();
        data.readFromMedia(rPosFile,disk);
    }

    /**
     * @param s
     */
    public void dump(StringBuffer s)
    {
        s.append("File: [recovered] [");
        ts.dump(s);
        s.append("]\n");
    }

    /**
     * @param rPos
     */
    public void getUsed(Collection rPos)
    {
        ts.getPos(rPos);
        data.getPos(rPos);
    }

    /**
     * @return
     */
    public VolumeTSMap getTSMap()
    {
        return ts;
    }
}
