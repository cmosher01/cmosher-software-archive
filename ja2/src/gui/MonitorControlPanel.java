/*
 * Created on Jan 28, 2008
 */
package gui;

import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import video.DisplayType;
import video.VideoDisplayDevice;

public class MonitorControlPanel extends JPanel
{
	private VideoDisplayDevice display;

	public MonitorControlPanel()
	{
		setLayout(new FlowLayout());
		setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		setOpaque(false);
		addNotify();
		setFocusable(false);



	    final ButtonGroup power = new ButtonGroup();

	    final JRadioButton powerOn = new JRadioButton("ON");
	    powerOn.setFocusable(false);
	    powerOn.setOpaque(false);
	    power.add(powerOn);
	    powerOn.addActionListener(new ActionListener()
	    {
			public void actionPerformed(ActionEvent e)
			{
				powerOn();
			}
	    });
	    add(powerOn);



	    final JRadioButton powerOff = new JRadioButton("OFF");
	    powerOff.setSelected(true);
	    powerOff.setFocusable(false);
	    powerOff.setOpaque(false);
	    power.add(powerOff);
	    powerOff.addActionListener(new ActionListener()
	    {
			public void actionPerformed(ActionEvent e)
			{
				powerOff();
			}
	    });
	    add(powerOff);

	    final ButtonGroup displayType = new ButtonGroup();
	    initDisplayButton(displayType,"Color",DisplayType.MONITOR_COLOR,true);
	    initDisplayButton(displayType,"White",DisplayType.MONITOR_WHITE,false);
	    initDisplayButton(displayType,"Green",DisplayType.MONITOR_GREEN,false);
	    initDisplayButton(displayType,"Orange",DisplayType.MONITOR_ORANGE,false);
	    initDisplayButton(displayType,"Old Color",DisplayType.TV_OLD_COLOR,false);
	    initDisplayButton(displayType,"Old B&W",DisplayType.TV_OLD_BW,false);
	    initDisplayButton(displayType,"New Color",DisplayType.TV_NEW_COLOR,false);
	    initDisplayButton(displayType,"New B&W",DisplayType.TV_NEW_BW,false);
	}

	private void initDisplayButton(final ButtonGroup displayType, String name, final DisplayType type, final boolean selected)
	{
		final JRadioButton displayTypeButton = new JRadioButton(name);
		displayTypeButton.setSelected(selected);
	    displayTypeButton.setFocusable(false);
	    displayTypeButton.setOpaque(false);
	    displayType.add(displayTypeButton);
	    displayTypeButton.addActionListener(new ActionListener()
	    {
			public void actionPerformed(ActionEvent e)
			{
				display.setType(type);
			}
	    });
	    add(displayTypeButton);
	}

	private void powerOn()
	{
		if (display.isOn())
		{
			return;
		}
		Thread th = new Thread(new Runnable()
		{
			public void run()
			{
				display.powerOn(true);
			}
		});
		th.setName("User-powerOn");
		th.setDaemon(true);
		th.start();
	}

	private void powerOff()
	{
		if (!display.isOn())
		{
			return;
		}
		Thread th = new Thread(new Runnable()
		{
			public void run()
			{
				display.powerOn(false);
			}
		});
		th.setName("User-powerOff");
		th.setDaemon(true);
		th.start();
		// TODO why does this not always blank the screen???
	}

	public void setUpListeners(VideoDisplayDevice display)
	{
		this.display = display;
	    display.setType(DisplayType.MONITOR_COLOR);
	}
}
