/*
 * Created on May 26, 2005
 */
package com.surveysampling.util;

/**
 * Objects that implement this interface want
 * to watch the progress of some process, and be
 * informed by the process of the progress of its
 * task.
 * 
 * @author Chris Mosher
 */
public interface ProgressWatcher
{
    /**
     * Called by the process being watched to notify
     * the watcher of the total number of steps that
     * are required for this process, or to notify him
     * of the fact that the total number of steps is unknown (-1).
     * 
     * @param totalSteps or -1 if total steps is unknown
     */
    void setTotalSteps(int totalSteps);

    /**
     * Called by the process being watched to indicate
     * that some progress has been made. Typically, this
     * method will be called with <code>progress</code>
     * of zero the first time, followed by 1, 2, etc.
     * 
     * @param progress 0, 1, ..., n-1 to indicate the
     * step that is about to be processed, where n is
     * the total number of steps
     * @param message for step about to be processed,
     * never <code>null</code>, but may be an empty string.
     */
    void setProgress(int progress, String message);
}
