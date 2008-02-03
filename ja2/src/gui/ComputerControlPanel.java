/*
 * Created on Jan 26, 2008
 */
package gui;

import gui.buttons.PowerLight;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import video.AnalogTV;
import video.VideoStaticGenerator;
import chipset.TimingGenerator;
import chipset.cpu.CPU6502;

public class ComputerControlPanel extends JPanel
{
	private final PowerLight powerLight = new PowerLight();
	private TimingGenerator clock;
	private CPU6502 cpu;
	private VideoStaticGenerator vidStatic;
	private AnalogTV tv;

	public ComputerControlPanel()
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



	    add(this.powerLight);
	}

	private void powerOn()
	{
		if (clock.isRunning())
		{
			return;
		}
		powerLight.turnOn(true);
		powerLight.repaint();
		Thread th = new Thread(new Runnable()
		{
			public void run()
			{
				vidStatic.shutdown();
				tv.restartSignal();
				clock.run();
			}
		});
		th.setName("User-powerOn");
		th.setDaemon(true);
		th.start();
	}

	private void powerOff()
	{
		if (!clock.isRunning())
		{
			return;
		}
		powerLight.turnOn(false);
		powerLight.repaint();
		Thread th = new Thread(new Runnable()
		{
			public void run()
			{
				clock.shutdown();
				vidStatic.run();
			}
		});
		th.setName("User-powerOff");
		th.setDaemon(true);
		th.start();
	}

	public void setUpListeners(TimingGenerator clock, CPU6502 cpu, VideoStaticGenerator vidStatic, AnalogTV tv)
	{
		this.clock = clock;
		this.cpu = cpu;
		this.vidStatic = vidStatic;
		this.tv = tv;

		this.vidStatic.run();
	}
}
