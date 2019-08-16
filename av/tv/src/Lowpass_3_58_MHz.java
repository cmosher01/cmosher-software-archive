/*
 * Created on Jan 21, 2008
 */
public class Lowpass_3_58_MHz
{
	/* Digital filter designed by mkfilter/mkshape/gencode   A.J. Fisher
	   Command line: /www/usr/fisher/helpers/mkfilter -Bu -Lp -o 4 -a 2.5000000000e-01 0.0000000000e+00 -Z 2.5000000000e-01 -l

	#define NZEROS 6
	#define NPOLES 6
	#define GAIN   2.128093084e+01

	static float xv[NZEROS+1], yv[NPOLES+1];

	static void filterloop()
	  { for (;;)
	      { xv[0] = xv[1]; xv[1] = xv[2]; xv[2] = xv[3]; xv[3] = xv[4]; xv[4] = xv[5]; xv[5] = xv[6]; 
	        xv[6] = next input value / GAIN;
	        yv[0] = yv[1]; yv[1] = yv[2]; yv[2] = yv[3]; yv[3] = yv[4]; yv[4] = yv[5]; yv[5] = yv[6]; 
	        yv[6] =   (xv[0] + xv[6]) + 4 * (xv[1] + xv[5]) + 7 * (xv[2] + xv[4])
	                     + 8 * xv[3]
	                     + ( -0.0000000000 * yv[0]) + ( -0.0000000000 * yv[1])
	                     + ( -0.0176648009 * yv[2]) + ( -0.0000000000 * yv[3])
	                     + ( -0.4860288221 * yv[4]) + ( -0.0000000000 * yv[5]);
	        next output value = yv[6];
	      }
	  }
	*/

	private static final int NZEROS = 6;
	private static final int NPOLES = 6;
	private static final double GAIN = 2.128093084e+01;
	private final double[] xv = new double[NZEROS + 1];
	private final double[] yv = new double[NPOLES + 1];

	public double transition(final double next_input_value)
	{
		xv[0] = xv[1];
		xv[1] = xv[2];
		xv[2] = xv[3];
		xv[3] = xv[4];
		xv[4] = xv[5];
		xv[5] = xv[6];
		xv[6] = next_input_value / GAIN;

		yv[0] = yv[1];
		yv[1] = yv[2];
		yv[2] = yv[3];
		yv[3] = yv[4];
		yv[4] = yv[5];
		yv[5] = yv[6];
        yv[6] = (xv[0] + xv[6]) + 4 * (xv[1] + xv[5]) + 7 * (xv[2] + xv[4]) + 8 * xv[3] + (-0.0000000000 * yv[0]) + (-0.0000000000 * yv[1]) + (-0.0176648009 * yv[2]) + (-0.0000000000 * yv[3]) + (-0.4860288221 * yv[4]) + (-0.0000000000 * yv[5]);

        return yv[6];
	}
}
