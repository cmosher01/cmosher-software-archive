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
import video.VideoDisplayDevice;
import display.DisplayType;
import emu.Emulator;

public class MonitorControlPanel extends JPanel
{
	private final VideoDisplayDevice display;
	private boolean powerState;
	private DisplayType displayTypeState = DisplayType.MONITOR_COLOR;

	public MonitorControlPanel(final VideoDisplayDevice display)
	{
		this.display = display;

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

	    powerOff.setSelected(true);

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
	    displayTypeButton.setFocusable(false);
	    displayTypeButton.setOpaque(false);
	    displayType.add(displayTypeButton);
	    displayTypeButton.addActionListener(new ActionListener()
	    {
			public void actionPerformed(ActionEvent e)
			{
				if (type == displayTypeState)
				{
					return;
				}
				display.setType(type);
				displayTypeState = type;
			}
	    });
	    add(displayTypeButton);

	    displayTypeButton.setSelected(selected);
	}

	private void powerOn()
	{
		if (this.powerState)
		{
			return;
		}
		this.display.powerOn(true);
		this.powerState = true;
	}

	private void powerOff()
	{
		if (!this.powerState)
		{
			return;
		}
		this.display.powerOn(false);
		this.powerState = false;
	}
}
