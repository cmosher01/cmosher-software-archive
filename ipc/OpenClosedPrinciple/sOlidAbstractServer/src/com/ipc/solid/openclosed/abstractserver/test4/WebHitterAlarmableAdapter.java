package com.ipc.solid.openclosed.abstractserver.test4;

import com.ipc.solid.openclosed.abstractserver.test4.alarm.Alarmable;
import com.ipc.solid.openclosed.abstractserver.test4.webhitter.WebHitter;

public class WebHitterAlarmableAdapter implements Alarmable
{
	private final WebHitter hitter;
	public WebHitterAlarmableAdapter(WebHitter hitter)
	{
		this.hitter = hitter;
	}
	@Override
	public boolean isAlarming()
	{
		return hitter.cantConnect();
	}
}
