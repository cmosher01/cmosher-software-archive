/*
 * Created on Jan 11, 2008
 */
package cards;

import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;

public class ClockCard extends Card
{
	private byte latch;
	private int pos = 0xd;
	private String time;

	@Override
	public byte io(final int addr, @SuppressWarnings("unused") final byte data, @SuppressWarnings("unused") final boolean writing)
	{
		final int sw = addr & 0x0F;
		if (sw == 0)
		{
			if (this.latch >= 0)
			{
				if (this.pos == 0xd)
				{
					getTime();
				}
				final char c = this.time.charAt(this.pos);
				this.latch = (byte)(c | 0x80);
				--this.pos;
				if (this.pos < 0)
				{
					this.pos = 0xd;
				}
			}
		}
		else if (sw == 1)
		{
			this.latch &= 0x7F;
		}
		return this.latch;
	}

	private static final Calendar cal = Calendar.getInstance();
	private void getTime()
	{
		cal.setTime(new Date());
		final int month = cal.get(Calendar.MONTH)+1;
		final int weekday = cal.get(Calendar.DAY_OF_WEEK)-1;
		final int day = cal.get(Calendar.DAY_OF_MONTH);
		final int hour = cal.get(Calendar.HOUR_OF_DAY);
		final int minute = cal.get(Calendar.MINUTE);
		Formatter fmt = new Formatter();
		// mo,da,dt,hr,mn
		fmt.format("%02d,%02d,%02d,%02d,%02d",month,weekday,day,hour,minute);
		this.time = fmt.toString();
	}

	@Override
	public String getTypeName()
	{
		return "clock";
	}
}
