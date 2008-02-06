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

public class ComputerControlPanel extends JPanel
{
	private final Emulator emu;
	private final PowerLight powerLight = new PowerLight();
	private boolean powerState;

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
		if (this.powerState)
		{
			return;
		}
		this.powerState = true;
		this.powerLight.turnOn(true);
		this.powerLight.repaint();
		try
		{
			this.emu.powerOnComputer();
		}
		catch (IOException e)
		{
			// TODO handle I/O exception from reading text roms
			e.printStackTrace();
		}
	}

	private void powerOff()
	{
		if (!this.powerState)
		{
			return;
		}
		this.powerState = false;
		this.powerLight.turnOn(false);
		this.powerLight.repaint();
		this.emu.powerOffComputer();
	}
}
