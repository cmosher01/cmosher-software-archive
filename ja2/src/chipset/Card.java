/*
 * Created on Nov 28, 2007
 */
package chipset;

import gui.GUI;
import java.awt.dnd.DropTargetListener;
import java.io.InputStream;
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
	public byte io(@SuppressWarnings("unused") final int address, final byte data, @SuppressWarnings("unused") final boolean writing)
	{
		return data;
	}



	public byte readRom(final int address)
	{
		this.activeSeventhRom = true;
		return this.rom.read(address);
	}

	public void readSeventhRom(final int address, final byte[] rb)
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



	public void ioBankRom(@SuppressWarnings("unused") final int addr, @SuppressWarnings("unused") final byte[] rb, @SuppressWarnings("unused") final boolean write)
	{
	}



	public void loadBankRom(@SuppressWarnings("unused") final int base, @SuppressWarnings("unused") final InputStream file) throws InvalidMemoryLoad
	{
		throw new InvalidMemoryLoad("This card has no $D000 ROM");
	}



	public JPanel getPanel(@SuppressWarnings("unused") final GUI gui)
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
