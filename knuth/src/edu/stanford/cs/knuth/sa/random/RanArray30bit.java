package edu.stanford.cs.knuth.sa.random;

import java.io.IOException;

/**
 * Generates a series of random integers.
 * The algorithm used was developed by Donald E. Knuth.
 * See "A Better Random Number Generator" at http://www-cs-faculty.stanford.edu/~knuth/news02.html#rng
 * 
 * @author Chris Mosher
 */
public class RanArray30bit
{
    private static final int KK = 100;
    private static final int LL = 37;
    private static final int MM = 1<<30;

    private int[] ranx = new int[KK];

    private int[] ranbuf;
    private int nextat = KK;

    public RanArray30bit()
    {
        this((int)getDefaultSeed());
    }

    /**
     * @param seed
     */
    public RanArray30bit(int seed)
    {
        int[] x = new int[KK+KK-1];

//  register long ss=(seed+2)&(MM-2);
//  for (j=0;j<KK;j++) {
//    x[j]=ss;                      /* bootstrap the buffer */
//    ss<<=1; if (ss>=MM) ss-=MM-2; /* cyclic shift 29 bits */
//  }
        int ss = (seed+2)&(MM-2);
        for (int j = 0; j < KK; ++j)
        {
            x[j] = ss;
            ss <<= 1;
            if (ss >= MM)
                ss -= MM-2;
        }
        ++x[1];


//  for (ss=seed&(MM-1),t=TT-1; t; ) {       
        ss = seed&(MM-1);//sseed;
        int t = 69;
        while (t > 0)
        {
            for (int j = KK-1; j > 0; --j)
            {
                x[j+j] = x[j];
                x[j+j-1] = 0;
            }
            for (int j = KK+KK-2; j >= KK; --j)
            {
                x[j-(KK-LL)] = modDiff(x[j-(KK-LL)],x[j]);
                x[j-KK] = modDiff(x[j-KK],x[j]);
            }
    
            if (ss%2 == 1)
            {
                for (int j = KK-1; j >= 0; --j)
                    x[j+1] = x[j];
                x[0] = x[KK];
                x[LL] = modDiff(x[LL],x[KK]);
            }
            if (ss != 0)
                ss >>= 1;
            else
                --t;
        }

//  for (j=0;j<LL;j++) ran_x[j+KK-LL]=x[j];
//  for (;j<KK;j++) ran_x[j-LL]=x[j];
//  for (j=0;j<10;j++) ran_array(x,KK+KK-1); /* warm things up */
        for (int j = 0; j < LL; ++j)
            ranx[j+KK-LL] = x[j];
        for (int j = LL; j < KK; ++j)
            ranx[j-LL] = x[j];
        for (int j = 0; j < 10; ++j)
            generate(KK+KK-1);
    }

    public int next()
    {
        if (nextat >= KK)
        {
            ranbuf = generate(1009);
            nextat = 0;
        }
        return ranbuf[nextat++];
    }

    protected int[] generate(int n)
    {
        if (n < KK)
            throw new IllegalArgumentException("n must be >= "+KK);

        int[] aa = new int[n];

//  for (j=0;j<KK;j++) aa[j]=ran_x[j];
                    //j = (0,KK]
//  for (;j<n;j++) aa[j]=mod_diff(aa[j-KK],aa[j-LL]);
                    //j = (KK,n]
//  for (i=0;i<LL;i++,j++) ran_x[i]=mod_diff(aa[j-KK],aa[j-LL]);
                    //j = (n,n+LL]
//  for (;i<KK;i++,j++) ran_x[i]=mod_diff(aa[j-KK],ran_x[i-LL]);
                    //j = (n+LL,n+KK], i = (LL,KK]

//aa   [(  0,  99)] <-- ran_x[(   0,  99)]
//aa   [(100,1008)] <-- aa   [(   0, 908)] - aa   [( 63, 971)]
//ran_x[(  0,  36)] <-- aa   [( 909, 945)] - aa   [(972,1009)];
//ran_x[( 37,  99)] <-- aa   [( 946,1008)] - ran_x[(  0,  62)];

        //(0,KK]
        for (int j = 0; j < KK; ++j)
            aa[j] = ranx[j];

        //(KK,n]
        for (int j = KK; j < n; ++j)
            aa[j] = modDiff(aa[j-KK],aa[j-LL]);

        //(0,LL]
        for (int j = 0; j < LL; ++j)
            ranx[j] = modDiff(aa[n+j-KK],aa[n+j-LL]);

        //(LL,KK]
        for (int j = LL; j < KK; ++j)
            ranx[j] = modDiff(aa[n+j-KK],ranx[j-LL]);

        return aa;
    }

    protected static int modDiff(int x, int y)
    {
        long t = x - y;
        if (t < 0)
            t += MM;
        return (int)t;
    }

    /**
     * Generates some random numbers using this class.
     * @param rArg
     * @throws IOException
     */
    public static void main(String[] rArg)
    {
        int[] a = null;

        RanArray30bit rg = new RanArray30bit(310952);
        for (int i = 0; i < 2010; ++i)
            a = rg.generate(1009);
        System.out.print(a[0]);
        System.out.println(" (should be 995235265)");
        System.out.println("-------------------------------------------------------");

        RanArray30bit rg2 = new RanArray30bit(310952);
        for (int i = 0; i < 1010; ++i)
            a = rg2.generate(2009);
        System.out.print(a[0]);
        System.out.println(" (should be 995235265)");
        System.out.println("-------------------------------------------------------");
//
//        KnuthRandomGenerator rg3 = new KnuthRandomGenerator();
//        for (int i = 0; i < 2000; ++i)
//            System.out.println(rg3.next());
//        System.out.println("-------------------------------------------------------");
//
//        for (int seed = 1; seed < 500; ++seed)
//        {
//            KnuthRandomGenerator rgx = new KnuthRandomGenerator(seed);
//            for (int i = 0; i < 4; ++i)
//                System.out.println(rgx.next());
//        }

//        KnuthRandomGenerator rgx = new KnuthRandomGenerator();
//        DataOutputStream ofile = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("knuth.32")));
//        for (int i = 0; i < 0x2bc000; ++i)
//        {
//            ofile.writeInt(rgx.next());
//            if (i%(250000) == 0)
//                System.out.println("at "+i);
//        }
//        ofile.close();
    }
}
///*    This program by D E Knuth is in the public domain and freely copyable
// *    AS LONG AS YOU MAKE ABSOLUTELY NO CHANGES!
// *    It is explained in Seminumerical Algorithms, 3rd edition, Section 3.6
// *    (or in the errata to the 2nd edition --- see
// *        http://www-cs-faculty.stanford.edu/~knuth/taocp.html
// *    in the changes to Volume 2 on pages 171 and following).              */
//
///*    N.B. The MODIFICATIONS introduced in the 9th printing (2002) are
//      included here; there's no backwards compatibility with the original. */
//
///*    This version also adopts Brendan McKay's suggestion to
//      accommodate naive users who forget to call ran_start(seed).          */
//
///*    If you find any bugs, please report them immediately to
// *                 taocp@cs.stanford.edu
// *    (and you will be rewarded if the bug is genuine). Thanks!            */
//
///************ see the book for explanations and caveats! *******************/
///************ in particular, you need two's complement arithmetic **********/
//
//#define KK 100                     /* the long lag */
//#define LL  37                     /* the short lag */
//#define MM (1L<<30)                 /* the modulus */
//#define mod_diff(x,y) (((x)-(y))&(MM-1)) /* subtraction mod MM */
//
//long ran_x[KK];                    /* the generator state */
//
//#ifdef __STDC__
//void ran_array(long aa[],int n)
//#else
//void ran_array(aa,n)    /* put n new random numbers in aa */
//  long *aa;   /* destination */
//  int n;      /* array length (must be at least KK) */
//#endif
//{
//  register int i,j;
//  for (j=0;j<KK;j++) aa[j]=ran_x[j];
//  for (;j<n;j++) aa[j]=mod_diff(aa[j-KK],aa[j-LL]);
//  for (i=0;i<LL;i++,j++) ran_x[i]=mod_diff(aa[j-KK],aa[j-LL]);
//  for (;i<KK;i++,j++) ran_x[i]=mod_diff(aa[j-KK],ran_x[i-LL]);
//}
//
///* the following routines are from exercise 3.6--15 */
///* after calling ran_start, get new randoms by, e.g., "x=ran_arr_next()" */
//
//#define QUALITY 1009 /* recommended quality level for high-res use */
//long ran_arr_buf[QUALITY];
//long ran_arr_dummy=-1, ran_arr_started=-1;
//long *ran_arr_ptr=&ran_arr_dummy; /* the next random number, or -1 */
//
//#define TT  70   /* guaranteed separation between streams */
//#define is_odd(x)  ((x)&1)          /* units bit of x */
//
//#ifdef __STDC__
//void ran_start(long seed)
//#else
//void ran_start(seed)    /* do this before using ran_array */
//  long seed;            /* selector for different streams */
//#endif
//{
//  register int t,j;
//  long x[KK+KK-1];              /* the preparation buffer */
//  register long ss=(seed+2)&(MM-2);
//  for (j=0;j<KK;j++) {
//    x[j]=ss;                      /* bootstrap the buffer */
//    ss<<=1; if (ss>=MM) ss-=MM-2; /* cyclic shift 29 bits */
//  }
//  x[1]++;              /* make x[1] (and only x[1]) odd */
//  for (ss=seed&(MM-1),t=TT-1; t; ) {       
//    for (j=KK-1;j>0;j--) x[j+j]=x[j], x[j+j-1]=0; /* "square" */
//    for (j=KK+KK-2;j>=KK;j--)
//      x[j-(KK-LL)]=mod_diff(x[j-(KK-LL)],x[j]),
//      x[j-KK]=mod_diff(x[j-KK],x[j]);
//    if (is_odd(ss)) {              /* "multiply by z" */
//      for (j=KK;j>0;j--)  x[j]=x[j-1];
//      x[0]=x[KK];            /* shift the buffer cyclically */
//      x[LL]=mod_diff(x[LL],x[KK]);
//    }
//    if (ss) ss>>=1; else t--;
//  }
//  for (j=0;j<LL;j++) ran_x[j+KK-LL]=x[j];
//  for (;j<KK;j++) ran_x[j-LL]=x[j];
//  for (j=0;j<10;j++) ran_array(x,KK+KK-1); /* warm things up */
//  ran_arr_ptr=&ran_arr_started;
//}
//
//#define ran_arr_next() (*ran_arr_ptr>=0? *ran_arr_ptr++: ran_arr_cycle())
//long ran_arr_cycle()
//{
//  if (ran_arr_ptr==&ran_arr_dummy)
//    ran_start(314159L); /* the user forgot to initialize */
//  ran_array(ran_arr_buf,QUALITY);
//  ran_arr_buf[100]=-1;
//  ran_arr_ptr=ran_arr_buf+1;
//  return ran_arr_buf[0];
//}
//
//#include <stdio.h>
//int main()
//{
//  register int m; long a[2009]; 
//  ran_start(310952L);
//  for (m=0;m<=2009;m++) ran_array(a,1009);
//  printf("%ld\n", a[0]);             /* 995235265 */
//  ran_start(310952L);
//  for (m=0;m<=1009;m++) ran_array(a,2009);
//  printf("%ld\n", a[0]);             /* 995235265 */
//  return 0;
//}




//      SUBROUTINE RNARRY(AA,N)
//C       FORTRAN 77 version of "ran_array"
//C       from Seminumerical Algorithms by D E Knuth, 3rd edition (1997)
//C       including the MODIFICATIONS made in the 9th printing (2002)
//C       ********* see the book for explanations and caveats! *********
//      IMPLICIT INTEGER (A-Z)
//      DIMENSION AA(*)
//      PARAMETER (KK=100)
//      PARAMETER (LL=37)
//      PARAMETER (MM=2**30)
//      COMMON /RSTATE/ RANX(KK)
//      SAVE /RSTATE/
//      DO 1 J=1,KK
// 1       AA(J)=RANX(J)
//      DO 2 J=KK+1,N
//         AA(J)=AA(J-KK)-AA(J-LL)
//         IF (AA(J) .LT. 0) AA(J)=AA(J)+MM
// 2    CONTINUE
//      DO 3 J=1,LL
//         RANX(J)=AA(N+J-KK)-AA(N+J-LL)
//         IF (RANX(J) .LT. 0) RANX(J)=RANX(J)+MM
// 3    CONTINUE
//      DO 4 J=LL+1,KK
//         RANX(J)=AA(N+J-KK)-RANX(J-LL)
//         IF (RANX(J) .LT. 0) RANX(J)=RANX(J)+MM
// 4    CONTINUE
//      END
//
//
//      SUBROUTINE RNSTRT(SEED)
//      IMPLICIT INTEGER (A-Z)
//      PARAMETER (KK=100)
//      PARAMETER (LL=37)
//      PARAMETER (MM=2**30)
//      PARAMETER (TT=70)
//      PARAMETER (KKK=KK+KK-1)
//      DIMENSION X(KKK)
//      COMMON /RSTATE/ RANX(KK)
//      SAVE /RSTATE/
//      IF (SEED .LT. 0) THEN
//         SSEED=MM-1-MOD(-1-SEED,MM)
//      ELSE
//         SSEED=MOD(SEED,MM)
//      END IF
//      SS=SSEED-MOD(SSEED,2)+2
//      DO 1 J=1,KK
//         X(J)=SS
//         SS=SS+SS
//         IF (SS .GE. MM) SS=SS-MM+2
// 1    CONTINUE
//      X(2)=X(2)+1
//      SS=SSEED
//      T=TT-1
// 10   DO 12 J=KK,2,-1
//         X(J+J-1)=X(J)
// 12      X(J+J-2)=0
//      DO 14 J=KKK,KK+1,-1
//         X(J-(KK-LL))=X(J-(KK-LL))-X(J)
//         IF (X(J-(KK-LL)) .LT. 0) X(J-(KK-LL))=X(J-(KK-LL))+MM
//         X(J-KK)=X(J-KK)-X(J)
//         IF (X(J-KK) .LT. 0) X(J-KK)=X(J-KK)+MM
// 14   CONTINUE
//      IF (MOD(SS,2) .EQ. 1) THEN
//         DO 16 J=KK,1,-1
// 16      X(J+1)=X(J)
//         X(1)=X(KK+1)
//         X(LL+1)=X(LL+1)-X(KK+1)
//         IF (X(LL+1) .LT. 0) X(LL+1)=X(LL+1)+MM
//       END IF
//       IF (SS .NE. 0) THEN
//          SS=SS/2
//       ELSE
//          T=T-1
//       END IF
//       IF (T .GT. 0) GO TO 10
//       DO 20 J=1,LL
// 20       RANX(J+KK-LL)=X(J)
//       DO 21 J=LL+1,KK
// 21       RANX(J-LL)=X(J)
//       DO 22 J=1,10
// 22       CALL RNARRY(X,KKK)
//       END
//
//
//       PROGRAM MAIN
//C      a rudimentary test program:
//       IMPLICIT INTEGER (A-Z)
//       DIMENSION A(2009)
//       EXTERNAL RNSTRT, RNARRY
//       CALL RNSTRT(310952)
//       DO 1 I=1,2010
//          CALL RNARRY(A,1009)
// 1     CONTINUE 
//       PRINT '(I15)',A(1)
//C                   the number should be 995235265
//       CALL RNSTRT(310952)
//       DO 2 I=1,1010
//          CALL RNARRY(A,2009)
// 2     CONTINUE 
//       PRINT '(I15)',A(1)
//C                                 again, 995235265
//       END
//
