/*
 * Created on Nov 28, 2007
 */
package chipset;

import gui.GUI;
import java.awt.dnd.DropTargetListener;
import java.io.InputStream;
import javax.swing.JLabel;
import javax.swing.JPanel;

public abstract class Card
{
	protected final Memory rom = new Memory(0x100);
	private boolean activeSeventhRom;
	protected final Memory seventhRom = new Memory(0x800);



	// override
	public void reset()
	{
	}



	// override
	public byte io(int address, byte data, boolean writing)
	{
		return data;
	}



	public byte readRom(int address)
	{
		this.activeSeventhRom = true;
		return this.rom.read(address);
	}

	public void readSeventhRom(int address, byte[] rb)
	{
		if (address == 0x7FF)
		{
			this.activeSeventhRom = false;
		}
		else if (this.activeSeventhRom)
		{
			rb[0] = this.seventhRom.read(address);
		}
	}

	public void loadRom(final int base, final InputStream file) throws InvalidMemoryLoad
	{
		this.rom.load(base,file);
	}

	public void loadSeventhRom(final int base, final InputStream file) throws InvalidMemoryLoad
	{
		this.seventhRom.load(base,file);
	}



	public boolean inhibitMotherboardRom()
	{
		return false;
	}



	public void ioBankRom(int addr, byte[] rb, boolean write)
	{
	}



	public void loadBankRom(final int base, InputStream file) throws InvalidMemoryLoad
	{
		throw new InvalidMemoryLoad("This card has no $D000 ROM");
	}



	public JPanel getPanel(final GUI gui)
	{
		return new DefaultCardPanel(getTypeName());
	}



	public String getTypeName()
	{
		return this.getClass().getSimpleName();
	}



	public DropTargetListener getDropListener()
	{
		return null;
	}
}
