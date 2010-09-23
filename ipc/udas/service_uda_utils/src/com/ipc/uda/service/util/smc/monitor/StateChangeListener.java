package com.ipc.uda.service.util.smc.monitor;

public interface StateChangeListener
{
	void stateChanged(String statePrev, String event, String stateNext);
	void mapChanged(String map, boolean entered);
	void undefinedChange(String statePrev, String event, String stateNext);
}
