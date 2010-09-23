package com.ipc.solid.openclosed.abstractserver.test6.alarm;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests the {@link AlarmChecker} class functionality.
 */
public class AlarmCheckerTest
{
	@Test
	public void testNull()
	{
		final AlarmSpy alarm = new AlarmSpy();
		/*AlarmChecker checker =*/ new AlarmChecker(new AlwaysAlarming(),alarm);
		// don't run checker... we shouldn't have any alarms:
		assertEquals("",alarm.toString());
	}

	@Test
	public void testNoAlarms()
	{
		final AlarmSpy alarm = new AlarmSpy();
		AlarmChecker checker = new AlarmChecker(new NeverAlarming(),alarm);
		checker.run();
		assertEquals("O",alarm.toString());
		checker.run();
		assertEquals("OO",alarm.toString());
		checker.run();
		assertEquals("OOO",alarm.toString());
	}

	@Test
	public void testOneAlarm()
	{
		final AlarmSpy alarm = new AlarmSpy();
		final SettableAlarmable alarmable = new SettableAlarmable();

		AlarmChecker checker = new AlarmChecker(alarmable,alarm);
		assertEquals(0,alarmable.getCount());

		checker.run();
		assertEquals("O",alarm.toString());
		assertEquals(1,alarmable.getCount());

		alarmable.alarming = true;
		checker.run();
		assertEquals("OU",alarm.toString());
		assertEquals(2,alarmable.getCount());

		alarmable.alarming = false;
		checker.run();
		assertEquals("OUA",alarm.toString());
		assertEquals(3,alarmable.getCount());

		checker.run();
		assertEquals("OUAO",alarm.toString());
		assertEquals(4,alarmable.getCount());
	}

	@Test
	public void testTwoAlarms()
	{
		final AlarmSpy alarm = new AlarmSpy();
		final SettableAlarmable alarmable = new SettableAlarmable();

		AlarmChecker checker = new AlarmChecker(alarmable,alarm);
		assertEquals(0,alarmable.getCount());

		checker.run();
		assertEquals("O",alarm.toString());
		assertEquals(1,alarmable.getCount());

		alarmable.alarming = true;
		checker.run();
		assertEquals("OU",alarm.toString());
		assertEquals(2,alarmable.getCount());

		checker.run();
		assertEquals("OUS",alarm.toString());
		assertEquals(3,alarmable.getCount());

		alarmable.alarming = false;
		checker.run();
		assertEquals("OUSA",alarm.toString());
		assertEquals(4,alarmable.getCount());

		checker.run();
		assertEquals("OUSAO",alarm.toString());
		assertEquals(5,alarmable.getCount());
	}

	@Test
	public void testThreeAlarms()
	{
		final AlarmSpy alarm = new AlarmSpy();
		final SettableAlarmable alarmable = new SettableAlarmable();

		AlarmChecker checker = new AlarmChecker(alarmable,alarm);
		assertEquals(0,alarmable.getCount());

		checker.run();
		assertEquals("O",alarm.toString());
		assertEquals(1,alarmable.getCount());

		alarmable.alarming = true;
		checker.run();
		assertEquals("OU",alarm.toString());
		assertEquals(2,alarmable.getCount());

		checker.run();
		assertEquals("OUS",alarm.toString());
		assertEquals(3,alarmable.getCount());

		checker.run();
		assertEquals("OUSS",alarm.toString());
		assertEquals(4,alarmable.getCount());

		alarmable.alarming = false;
		checker.run();
		assertEquals("OUSSA",alarm.toString());
		assertEquals(5,alarmable.getCount());

		checker.run();
		assertEquals("OUSSAO",alarm.toString());
		assertEquals(6,alarmable.getCount());
	}

	@Test
	public void testIntermittentAlarms()
	{
		final AlarmSpy alarm = new AlarmSpy();
		final SettableAlarmable alarmable = new SettableAlarmable();

		AlarmChecker checker = new AlarmChecker(alarmable,alarm);
		assertEquals(0,alarmable.getCount());

		checker.run();
		assertEquals("O",alarm.toString());
		assertEquals(1,alarmable.getCount());

		alarmable.alarming = true;
		checker.run();
		assertEquals("OU",alarm.toString());
		assertEquals(2,alarmable.getCount());

		checker.run();
		assertEquals("OUS",alarm.toString());
		assertEquals(3,alarmable.getCount());

		alarmable.alarming = false;
		checker.run();
		assertEquals("OUSA",alarm.toString());
		assertEquals(4,alarmable.getCount());

		checker.run();
		assertEquals("OUSAO",alarm.toString());
		assertEquals(5,alarmable.getCount());

		alarmable.alarming = true;
		checker.run();
		assertEquals("OUSAOU",alarm.toString());
		assertEquals(6,alarmable.getCount());

		checker.run();
		assertEquals("OUSAOUS",alarm.toString());
		assertEquals(7,alarmable.getCount());

		checker.run();
		assertEquals("OUSAOUSS",alarm.toString());
		assertEquals(8,alarmable.getCount());

		alarmable.alarming = false;
		checker.run();
		assertEquals("OUSAOUSSA",alarm.toString());
		assertEquals(9,alarmable.getCount());

		checker.run();
		assertEquals("OUSAOUSSAO",alarm.toString());
		assertEquals(10,alarmable.getCount());
	}

	@Test(expected=RuntimeException.class)
	public void testExceptionAlarmable()
	{
		final AlarmSpy alarm = new AlarmSpy();
		final ExceptionAlarmable alarmable = new ExceptionAlarmable();

		AlarmChecker checker = new AlarmChecker(alarmable,alarm);
		checker.run();
	}
}
