/*
 * Created on Jan 26, 2008
 */
package gui;

import emu.Emulator;
import gui.buttons.PowerLight;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import video.AnalogTV;
import video.VideoStaticGenerator;
import chipset.TimingGenerator;
import chipset.cpu.CPU6502;

public class ComputerControlPanel extends JPanel
{
	private final Emulator emu;
	private final PowerLight powerLight = new PowerLight();

	public ComputerControlPanel(final Emulator emu)
	{
		this.emu = emu;

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


	    add(this.powerLight);
	}

	private void powerOn()
	{
		powerLight.turnOn(true);
		powerLight.repaint();
		Thread th = new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					emu.powerOnComputer();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		});
		th.setName("User-powerOn");
		th.setDaemon(true);
		th.start();
	}

	private void powerOff()
	{
		powerLight.turnOn(false);
		powerLight.repaint();
		Thread th = new Thread(new Runnable()
		{
			public void run()
			{
				emu.powerOffComputer();
			}
		});
		th.setName("User-powerOff");
		th.setDaemon(true);
		th.start();
	}
}
