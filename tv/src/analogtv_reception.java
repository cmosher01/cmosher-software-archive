/*
 * Created on Jan 18, 2008
 */
public class analogtv_reception
{
  public analogtv_input input;
  public double ofs;
  public double level = 1;
  public double multipath;
  public double freqerr;

  public double[] ghostfir = new double [ANALOGTV.GHOSTFIR_LEN];
  public double[] ghostfir2 = new double [ANALOGTV.GHOSTFIR_LEN];

  public double hfloss;
  public double hfloss2;
}
