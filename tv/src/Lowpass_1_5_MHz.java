/*
 * Created on Jan 21, 2008
 */
public class Lowpass_1_5_MHz
{
	/* Digital filter designed by mkfilter/mkshape/gencode   A.J. Fisher
	   Command line: /www/usr/fisher/helpers/mkfilter -Bu -Lp -o 3 -a 1.0476190476e-01 0.0000000000e+00 -Z 2.5000000000e-01 -l
	
	#define NZEROS 5
	#define NPOLES 5
	#define GAIN   9.820186136e+01
	
	static float xv[NZEROS+1], yv[NPOLES+1];
	
	static void filterloop()
	  { for (;;)
	      { xv[0] = xv[1]; xv[1] = xv[2]; xv[2] = xv[3]; xv[3] = xv[4]; xv[4] = xv[5]; 
	        xv[5] = next input value / GAIN;
	        yv[0] = yv[1]; yv[1] = yv[2]; yv[2] = yv[3]; yv[3] = yv[4]; yv[4] = yv[5]; 
	        yv[5] =   (xv[0] + xv[5]) + 3 * (xv[1] + xv[4]) + 4 * (xv[2] + xv[3])
	                     + ( -0.0000000000 * yv[0]) + ( -0.0000000000 * yv[1])
	                     + (  0.2608994250 * yv[2]) + ( -1.1262209053 * yv[3])
	                     + (  1.7023917784 * yv[4]);
	        next output value = yv[5];
	      }
	  }
	 */
	private static final int NZEROS = 5;
	private static final int NPOLES = 5;
	private static final double GAIN = 9.820186136e+01;
	private final double[] xv = new double[NZEROS + 1];
	private final double[] yv = new double[NPOLES + 1];

	public double transition(final double next_input_value)
	{
		xv[0] = xv[1];
		xv[1] = xv[2];
		xv[2] = xv[3];
		xv[3] = xv[4];
		xv[4] = xv[5];
		xv[5] = next_input_value / GAIN;

		yv[0] = yv[1];
		yv[1] = yv[2];
		yv[2] = yv[3];
		yv[3] = yv[4];
		yv[4] = yv[5];
		yv[5] = (xv[0] + xv[5]) + 3 * (xv[1] + xv[4]) + 4 * (xv[2] + xv[3]) + (-0.0000000000 * yv[0]) + (-0.0000000000 * yv[1]) + (0.2608994250 * yv[2]) + (-1.1262209053 * yv[3]) + (1.7023917784 * yv[4]);

		return yv[5];
	}
}
