package com.ipc.solid.openclosed.abstractserver.test6.alarm;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PeriodicAlarmChecker
{
	private static final int THREAD_POOL_SIZE = 1;

	private final AlarmChecker checker;
	private final long periodInSeconds;

	private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(THREAD_POOL_SIZE);

	public PeriodicAlarmChecker(final AlarmChecker checker, final long periodInSeconds)
	{
		this.checker = checker;
		this.periodInSeconds = periodInSeconds;
	}

	public void start()
	{
		this.executor.scheduleAtFixedRate(this.checker,0,this.periodInSeconds,TimeUnit.SECONDS);
	}
}
