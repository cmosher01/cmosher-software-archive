package edu.fsu.stat.geo.diehard.tests;

import java.util.Arrays;

import nu.mine.mosher.math.Stats;
import nu.mine.mosher.random.RandomNumberGenerator;

public class Birthday extends RandomnessTest
{
    private static final int no_obs = 500;
    private static final int no_bday = 1024;
    private static final int no_bits = 24;
    private static final int mask = (1<<no_bits)-1;
    private static final double lambda = Math.pow(no_bday,3)/(4*Math.pow(2,no_bits));

    double chi_fit;
    int dgf;

    public Birthday(RandomNumberGenerator rng)
    {
        super(rng);
    }

    public double test()
    {
        int[] obs = new int[no_obs];
        double[] p = new double[32-no_bits+1];
        long[] bdspace = new long[no_bday];

        for (int rt = 32 - no_bits; rt >= 0; --rt)
        {
            int sum = 0;

            for (int k = 0; k < no_obs; ++k)
            {
                for (int i = 0; i < no_bday; ++i)
                {
                    bdspace[i] = (next() >> rt) & mask;
                }
                Arrays.sort(bdspace);

                for (int i = no_bday - 1; i >= 1; --i)
                {
                    bdspace[i] -= bdspace[i - 1];
                }
                Arrays.sort(bdspace);

                int no_dup = 0;
                for (int i = 1; i < no_bday; ++i)
                {
                    if (bdspace[i] == bdspace[i - 1])
                        ++no_dup;
                }

                sum += no_dup;
                obs[k] = no_dup;
            }

            p[rt] = P_fit(lambda, obs, no_obs);
            StringBuffer s = new StringBuffer(256);
            s.append(33-no_bits-rt);
            s.append(" to ");
            s.append(32-rt);
            s.append(" ");
            s.append((double)sum/no_obs);
            s.append(" ");
            s.append(chi_fit);
            s.append(" ");
            s.append(p[rt]);
            mLog.info(s.toString());
        }

        double pvalue = Stats.KStest(p,32-no_bits+1);
        
        StringBuffer s = new StringBuffer(256);
        s.append("degree of freedoms is: ");
        s.append(dgf);
        mLog.info(s.toString());

        s = new StringBuffer(256);
        s.append("p-value for KStest on those ");
        s.append(32-no_bits+1);
        s.append(" p-values: ");
        s.append(pvalue);
        mLog.info(s.toString());

        return pvalue;
    }

    protected double P_fit(double lambda, int[] obs, int no_obs)
    {
        int dim = no_obs / 5 + 1;
        int i = -1;
        int j, k = 0;
        double rest = no_obs;

        int[] f = new int[dim];
        Arrays.fill(f,0);
        double[] Ef = new double[dim];
        Arrays.fill(Ef,0);

        Arrays.sort(obs);

        for (j = 0; j < dim; ++j)
        {
            while (Ef[j] < 4.99999999D)
            {
                ++i;
                Ef[j] += no_obs * Stats.Poisson(lambda, i);
            }

            while (k < no_obs && obs[k] <= i)
            {
                ++f[j];
                ++k;
            }

            rest -= Ef[j];
            if (rest < 4.99999999D)
            {
                Ef[j] += rest;
                f[j] += no_obs - k;
                break;
            }
        }

        chi_fit = 0;
        for (i = 0; i <= j; ++i)
        {
            chi_fit += (f[i] - Ef[i]) * (f[i] - Ef[i]) / Ef[i];
        }

        dgf = j;

        return 1 - Stats.Chisq(dgf, chi_fit);
    }
}
//#include "header.h"
//
//static real chi_fit;
//static int    dgf;
//
//int ucmpr(const void *u1, const void *u2);
//
///*compute the statistic of goodness of fit to a Poisson distribution*/
//real P_fit(real lambda, counter *obs, int no_obs)
//{
//  counter *f, dim=no_obs/5;
//  register int i=-1;
//  register counter j, k=0;
//  real rest=no_obs, *Ef;
//
//  f=(counter*)calloc(dim, sizeof(counter));
//  Ef=(real*)calloc(dim, sizeof(real));
//
//  qsort(obs, no_obs, sizeof(counter), ucmpr);
//
//  for(j=0; j<dim; ++j){
//    while( Ef[j]<5 ){
//      ++i;
//      Ef[j]+=no_obs*Poisson(lambda, i);
//    }
//
//    while( obs[k]<=i ){
//      ++f[j];
//      ++k;
//    }
//
//    rest-=Ef[j];
//    if( rest<5 ){
//      Ef[j]+=rest;
//      f[j]+=no_obs-k;
//      break;
//    }
//  }
//
//  chi_fit=0;
//  for(i=0; i<=j; ++i){   
//   chi_fit+=(f[i]-Ef[i])*(f[i]-Ef[i])/Ef[i];
//  }
//
//  dgf=j;
///*
//  cfree(f, dim, sizeof(counter));
//  cfree(Ef, dim, sizeof(real));
//*/
//
//  return 1-Chisq(dgf,chi_fit);
//}
//
//void bday(char *filename)
//{
//  const counter no_obs=500, no_bday=1024, no_bits=24;
//  register const counter mask=pow(2,no_bits)-1;
//  const real lambda=pow(no_bday,3)/(4*pow(2,no_bits));
//
//  register int rt;
//  register int i, k, sum;
//  uniform *bdspace;
//  counter no_dup, *obs;
//  real pvalue, *p;
//
//  puts("\n\t|-------------------------------------------------------------|");
//  puts("\t|           This is the BIRTHDAY SPACINGS TEST                |");
//  puts("\t|Choose m birthdays in a \"year\" of n days.  List the spacings |");
//  puts("\t|between the birthdays.  Let j be the number of values that   |");
//  puts("\t|occur more than once in that list, then j is asymptotically  |");
//  puts("\t|Poisson distributed with mean m^3/(4n).  Experience shows n  |");
//  puts("\t|must be quite large, say n>=2^18, for comparing the results  |");
//  puts("\t|to the Poisson distribution with that mean.  This test uses  |");
//  puts("\t|n=2^24 and m=2^10, so that the underlying distribution for j |");
//  puts("\t|is taken to be Poisson with lambda=2^30/(2^26)=16. A sample  |");
//  puts("\t|of 200 j''s is taken, and a chi-square goodness of fit test  |");
//  puts("\t|provides a p value.  The first test uses bits 1-24 (counting |");
//  puts("\t|from the left) from integers in the specified file.  Then the|");
//  puts("\t|file is closed and reopened, then bits 2-25 of the same inte-|");
//  puts("\t|gers are used to provide birthdays, and so on to bits 9-32.  |");
//  puts("\t|Each set of bits provides a p-value, and the nine p-values   |");
//  puts("\t|provide a sample for a KSTEST.                               |");
//  puts("\t|------------------------------------------------------------ |\n");
// 
//  printf("\t\tRESULTS OF BIRTHDAY SPACINGS TEST FOR %s\n", filename);
//  printf("\t(no_bdays=%d, no_days/yr=2^%d,",no_bday, no_bits);
//  printf(" lambda=%.2f, sample size=%d)\n\n", lambda, no_obs);
//  printf("\tBits used\tmean\t\tchisqr\t\tp-value\n");
//
//  obs=(counter*)malloc(no_obs*sizeof(counter));
//  p=(real*)malloc((32-no_bits+1)*sizeof(real));
//  bdspace=(uniform*)malloc(no_bday*sizeof(uniform));
// 
//
//#define GETDAY       ( (uni(filename) >> rt) & mask )
// 
//  for(rt=32-no_bits; rt>=0; --rt){
//    sum=0;
//
//    for(k=0; k<no_obs; ++k){
//      for(i=0; i<no_bday; ++i){ 
//        bdspace[i]=GETDAY;
//      }
//
//      qsort(bdspace, no_bday, sizeof(uniform), ucmpr);
// 
//      for(i=no_bday-1;  i>=1; --i){
//        bdspace[i]-=bdspace[i-1];
//      }
//      qsort(bdspace, no_bday, sizeof(uniform), ucmpr);
//
//      no_dup=0;
//      for(i=1; i<no_bday; ++i){
//        if(bdspace[i]!=bdspace[i-1]) continue;
//        else ++no_dup;
//      }
//      sum+=no_dup;
//      obs[k]=no_dup;
//    } 
//    uni("close");
//
//    p[rt]=P_fit(lambda, obs, no_obs);
//
//    printf("\t %d to %d", 33-no_bits-rt,32-rt);
//    printf("\t%.2f\t\t%.4f\t\t%f\n", (real)sum/no_obs, chi_fit, p[rt]);   
//
//  }
//
//  /*clean up*/
//  free(bdspace);
//  free(obs);
//    
//  pvalue=KStest(p,32-no_bits+1);
//
//  printf("\n\t\t\tdegree of freedoms is: %d\n", dgf);
//  puts("\t---------------------------------------------------------------");
//  printf("\t\tp-value for KStest on those %d p-values: %f",32-no_bits+1,pvalue);
//  puts("\n"); 
//
//  free(p);
// 
//  return;
//}
//
///*main()
//{
//  char ch;
//
//  bday("binc");
//
//  return;
//}*/
//
//
