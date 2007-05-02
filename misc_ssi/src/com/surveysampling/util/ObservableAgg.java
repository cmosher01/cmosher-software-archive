package com.surveysampling.util;

import java.util.Observable;

/**
 * Use this class instead of java.util.Observable
 * if you want to aggregate an Observable object
 * instead of extend it.
 * 
 * If you aggregate an Observable object, then you
 * wouldn't be able to invoke its setChanged method
 * becasue it's potected. This class exposes that
 * functionality.
 * 
 * In addition, this class combines the setChanged
 * method and the notifyObservers method into just
 * one method (called "updateObservers"), to make
 * it easier to use.
 */
public class ObservableAgg extends Observable
{
	/**
	 * Invokes the update method on and of this
	 * object's Observers. Same as calling
	 * updateObservers(null).
	 */
	public void updateObservers()
	{
		super.setChanged();
		super.notifyObservers();
	}

	/**
	 * Invokes the update method on and of this
	 * object's Observers.
	 */
	public void updateObservers(Object obj)
	{
		super.setChanged();
		super.notifyObservers(obj);
	}
}
