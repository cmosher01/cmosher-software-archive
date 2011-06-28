/*
 * Created on April 26, 2005
 */
package com.surveysampling.emailpanel.counts.api.request;

/**
 * <code>EpanCountRequest</code> calls this interface
 * to indicate when each count query is finished running.
 * Users pass in an object that implements this interface
 * to the <code>run</code> method of <code>EpanCountRequest</code>.
 * 
 * @author Chris Mosher
 */
public interface EpanCountDoneListener
{
    /**
     * <code>EpanCountRequest</code> calls this method when
     * it first starts (passing <code>null</code>), and
     * after each count query is finished running (passing
     * the <code>EpanCount</code> that just finished).
     * @param epanCount the <code>EpanCount</code> that just finished,
     * or <code>null</code>
     */
    void done(EpanCount epanCount);

    /**
     * <code>EpanCountRequest</code> calls this method after
     * calling <code>done</code> on the last count query
     * for the request.
     * @param epanCountRequest the <code>EpanCountRequest</code>
     * that is completely finished running
     */
    void allDone(EpanCountRequest epanCountRequest);
}
