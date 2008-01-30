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
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import video.AnalogTV;
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
	}

	private void powerOn()
	{
		if (display.isOn())
		{
			return;
		}
		display.powerOn(true);
	}

	private void powerOff()
	{
		if (!display.isOn())
		{
			return;
		}
		display.powerOn(false); // TODO call from own thread
		// TODO why does this not always blank the screen???
	}

	public void setUpListeners(VideoDisplayDevice display)
	{
		this.display = display;
	}
}
