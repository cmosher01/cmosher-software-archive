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
import video.VideoStaticGenerator;
import chipset.TimingGenerator;
import chipset.cpu.CPU6502;

public class ComputerControlPanel extends JPanel
{
	private final PowerLight powerLight = new PowerLight();
	private TimingGenerator clock;
	private CPU6502 cpu;

	public ComputerControlPanel()
	{
		setLayout(new FlowLayout());
		setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		setOpaque(true);
		addNotify();
		setFocusable(false);



	    final ButtonGroup power = new ButtonGroup();

	    final JRadioButton powerOn = new JRadioButton("ON");
	    powerOn.setFocusable(false);
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

		clock.run();
    	// if rev > 0,
//    	cpu.reset();
	}

	private void powerOff()
	{
		Thread th = new Thread(new Runnable()
		{
			public void run()
			{
				if (!clock.isRunning())
				{
					return;
				}
				powerLight.turnOn(false);
				powerLight.repaint();
				clock.shutdown();
			}
		});
		th.setName("User-powerOff");
		th.setDaemon(true);
		th.start();
	}

	public void setUpListeners(TimingGenerator clock, CPU6502 cpu)
	{
		this.clock = clock;
		this.cpu = cpu;
	}
}
