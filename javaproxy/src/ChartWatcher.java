/**
 * @(#)ChartWatcher.java	April 3, 1998
 *
 * I MAKE NO WARRANTIES ABOUT THE SUITABILITY OF THIS SOFTWARE, EITHER
 * EXPRESS OR IMPLIED AND SHALL NOT BE LIABLE FOR ANY DAMAGES THIS
 * SOFTWARE MAY BRING TO YOUR SYSTEM. USE IT AT YOUR OWN RISK.
 *
 * Author : Steve Yeong-Ching Hsueh
 */

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * ChartWatcher class updates the graph every 10 minutes.
 */
public class ChartWatcher extends Thread
{
    String tmlabels[];
    Object parent;
    Calendar calendar = new GregorianCalendar();
    final static int onesec = 1000;         // 1 second
    final static int delay  = onesec * 30 ; // 0.5 minutes


    /**
     * constructor
     */
    public ChartWatcher(Object parent, String tmlabels[]) {
        this.tmlabels = tmlabels;
        this.parent = parent;
        this.start();
    }

    /**
     * update vector
     */
    public void updateTMVector() {
        int hr = calendar.get(Calendar.HOUR_OF_DAY);
        int mn = calendar.get(Calendar.MINUTE);
        String shr, smn;
        for(int i=tmlabels.length-1; i>=0; i--) { // i = 5~0
            if(mn<10) smn = "0"+mn;
            else smn = "" + mn;
            if(hr<10) shr = "0"+hr;
            else shr = "" + hr;

            tmlabels[i] = shr + ":" + smn;
            mn = mn - 10 ;
            if( mn < 0) {
                mn += 60;
                hr -= 1;
                if( hr < 0 ) hr += 24;
            }
        }
    }

    /**
     * run into an infinite loop till the program is stopped
     */
    public void run() {

        while( true ) {
            try {
                calendar.setTime(new Date());
                if(calendar.get(Calendar.MINUTE)%10 == 0) {
                    updateTMVector();     // update every 10 minutes
//                    ((ServerInterface) parent).updateChart1();
//                    ((ServerInterface) parent).updateChart2();
                    sleep(delay+onesec);  // sleep a while
                }
                sleep(delay);
            }
            catch( InterruptedException e ) { }
        }
    }


}