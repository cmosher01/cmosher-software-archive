package nu.mine.mosher.math;

import java.util.Arrays;

public class Stats
{
    public static double phi(double x)
    {
        return Math.exp(-x * x / 2D) / Math.sqrt(2D * Math.PI);
    }

    /*c.d.f of Standard Normal*/
    public static double Phi(double x)
    {
        x = x / Math.sqrt(2D);
        x = 1D + erf(x);

        return x / 2D;
    }

    /*p.d.f of Chi-square*/
    public static double chisq(int df, double x)
    {
        return (Math.pow(x / 2D, (df - 2D) / 2D) * Math.exp(-x / 2D) / (2D * G(df / 2D)));
    }

    /*c.d.f of Chi-square*/
    public static double Chisq(int df, double x)
    {
        double r;
        if (df==1)
            r = 2D * Phi(Math.sqrt(x)) - 1D;
        else if (df==2)
            r = 1D - Math.exp(-x / 2D);
        else
            r = (Chisq(df - 2, x) - 2D * chisq(df, x));
        return r;
//        switch (df)
//        {
//            case 1 :
//                return 2 * Phi(Math.sqrt(x)) - 1;
//            case 2 :
//                return 1 - Math.exp(-x / 2);
//            default :
//                break;
//        }
//    
//        return (Chisq(df - 2, x) - 2 * chisq(df, x));
    }

    /*p.d.f of Poisson distribution*/
    public static double Poisson(double lambda, int k)
    {
        if (k == 0)
            return Math.exp(-lambda);
    
        return Math.exp(-lambda) * Math.pow(lambda, k) / G(k + 1D);
    }

    public static double G(double z)
    {
        int tmp = (int)(2*z);
        if (tmp != 2*z || z == 0)
            throw new IllegalArgumentException();

        double r;
        if (tmp==1)
            r = Math.sqrt(Math.PI);
        else if (tmp==2)
            r = 1D;
        else
            r = (z - 1D) * G(z - 1D);
        return r;
//        switch (tmp)
//        {
//            case 1 :
//                return Math.sqrt(Math.PI);
//            case 2 :
//                return 1;
//            default :
//                break;
//        }
//        return (z - 1) * G(z - 1);
    }
    
    public static double erf(double x)
    {
        int s = (x > 1e-10D) ? 1 : (x < -1e-10D) ? -1 : 0;

        x = .707106781187D*Math.abs(x);
        if (x - 3 > -1e-10D || s == 0)
            return s;

        if (x - 1 < 1e-10D)
        {
            double w = x * x;
            x =
                ((((((((.000124818987 * w - .001075204047) * w + .005198775019) * w - .019198292004) * w + .059054035642) * w
                    - .151968751364)
                    * w
                    + .319152932694)
                    * w
                    - .531923007300)
                    * w
                    + .797884560593)
                    * 2
                    * x;
        }
        else
        {
            x -= 2;
            x =
                (((((((((((((-.000045255659 * x + .000152529290) * x - .000019538132) * x - .000676904986) * x + .001390604284) * x
                    - .000794620820)
                    * x
                    - .002034254874)
                    * x
                    + .006549791214)
                    * x
                    - .010557625006)
                    * x
                    + .011630447319)
                    * x
                    - .009279453341)
                    * x
                    + .005353579108)
                    * x
                    - .002141268741)
                    * x
                    + .000535310849)
                    * x
                    + .999936657524;
        }
        return s*x;
    }

    /*c.d.f of Anderson-Darling statistic (a quick algorithm)*/
    public static double AD(double z)
    {
        if (z < .01)
            return 0;

        if (z <= 2)
        {
            return 2 * Math.exp(-1.2337 / z) * (1 + z / 8 - .04958 * z * z / (1.325 + z)) / Math.sqrt(z);
        }

        if (z <= 4)
            return 1 - .6621361 * Math.exp(-1.091638 * z) - .95095 * Math.exp(-2.005138 * z);

        if (4 < z)
            return 1 - .4938691 * Math.exp(-1.050321 * z) - .5946335 * Math.exp(-1.527198 * z);

        return -1; /*error indicator*/
    }

    public static double KStest(double[] x, int dim)
    {
        double pvalue, tmp, z = -dim * dim, epsilon = Math.pow(10, -20);

        Arrays.sort(x);

        for (int i = 0; i < dim; ++i)
        {
            tmp = x[i] * (1 - x[dim - 1 - i]);
            tmp = Math.max(epsilon, tmp);
            z -= (2 * i + 1) * Math.log(tmp);
        }

        z /= dim;
        pvalue = 1 - AD(z);

        return pvalue;
    }
}
