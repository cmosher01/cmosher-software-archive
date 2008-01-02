import gui.UI;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import keyboard.KeypressQueue;
import stdio.StandardIn;
import stdio.StandardInProducer;
import stdio.StandardOut;
import cards.FirmwareCard;
import cards.LanguageCard;
import chipset.Card;
import chipset.EmptySlot;
import chipset.InvalidMemoryLoad;
import chipset.Memory;
import disk.DiskBytes;
import disk.DiskInterface;
import disk.DiskState;
import disk.InvalidDiskImage;

/*
 * Created on Dec 1, 2007
 */
class Config
{
	private static final int SLOTS = 8;

	private String filename;

	public Config(final String filename)
	{
		this.filename = filename;
	}

	public void parseConfig(final Memory memory, final List<Card> cards, final DiskState diskState, final DiskBytes disk1, final DiskBytes disk2, final UI ui)
		throws IOException, InvalidMemoryLoad, InvalidDiskImage
	{
		cards.addAll(Collections.<Card>nCopies(SLOTS,new EmptySlot()));

		final BufferedReader cfg = new BufferedReader(new InputStreamReader(new FileInputStream(new File(this.filename))));

    	for (String line = cfg.readLine(); line != null; line = cfg.readLine())
    	{
    		int comment = line.indexOf('#');
    		if (comment >= 0)
    		{
    			line = line.substring(0,comment);
    		}
    		line = line.trim();

    		parseLine(line,memory,cards,diskState,disk1,disk2,ui);
    	}

    	cfg.close();

    	verifyUniqueCards(cards);
	}

	private void verifyUniqueCards(final List<Card> cards)
	{
		int nDisk = 0;
		int nStdOut = 0;
		int nStdIn = 0;
		for (Card card : cards)
		{
			if (card instanceof DiskInterface)
			{
				++nDisk;
			}
			else if (card instanceof StandardOut)
			{
				++nStdOut;
			}
			else if (card instanceof StandardIn)
			{
				++nStdIn;
			}
		}
		if (nDisk > 1)
		{
			throw new IllegalArgumentException("Error in config file: only one disk card is supported.");
		}
		if (nStdOut > 1)
		{
			throw new IllegalArgumentException("Error in config file: only one stdout card is supported.");
		}
		if (nStdIn > 1)
		{
			throw new IllegalArgumentException("Error in config file: only one stdin card is supported.");
		}
	}

	private void parseLine(final String line, final Memory memory, final List<Card> cards, final DiskState diskState, final DiskBytes disk1, final DiskBytes disk2, final UI ui)
		throws InvalidMemoryLoad, IOException, InvalidDiskImage
	{
		if (line.isEmpty())
		{
			return;
		}

		final StringTokenizer tok = new StringTokenizer(line);

		final String cmd = tok.nextToken();
		if (cmd.equalsIgnoreCase("slot"))
		{
			if (!tok.hasMoreTokens()) throw new IllegalArgumentException("Error in config file: "+line);
			final String sSlot = tok.nextToken();
			final int slot = Integer.decode(sSlot);
		
			if (!tok.hasMoreTokens()) throw new IllegalArgumentException("Error in config file: "+line);
			final String sCardType = tok.nextToken();

			insertCard(sCardType,slot,cards,diskState,ui);
		}
		else if (cmd.equalsIgnoreCase("import"))
		{
			if (!tok.hasMoreTokens()) throw new IllegalArgumentException("Error in config file: "+line);
			final String sm = tok.nextToken();
			int slot = -1;
			if (sm.equalsIgnoreCase("slot"))
			{
				if (!tok.hasMoreTokens()) throw new IllegalArgumentException("Error in config file: "+line);
				final String sSlot = tok.nextToken();
				slot = Integer.decode(sSlot);
			}
			else if (!sm.equalsIgnoreCase("motherboard"))
			{
				throw new IllegalArgumentException("Error in config file: "+line);
			}
			if (!tok.hasMoreTokens()) throw new IllegalArgumentException("Error in config file: "+line);

			final String romtype = tok.nextToken();

			if (!tok.hasMoreTokens()) throw new IllegalArgumentException("Error in config file: "+line);
			final String sBase = tok.nextToken();
			final int base = Integer.decode(sBase);

			if (!tok.hasMoreTokens()) throw new IllegalArgumentException("Error in config file: "+line);
			final String file = tok.nextToken("\0").trim(); // rest of line
			final InputStream rom = new BufferedInputStream(new FileInputStream(new File(file)));

			if (slot < 0) // motherboard
			{
				if (!romtype.equalsIgnoreCase("rom"))
				{
					throw new IllegalArgumentException("Error in config file: "+line);
				}
				memory.load(base,rom);
			}
			else
			{
				if (SLOTS <= slot)
				{
					throw new IllegalArgumentException("Error in config file: invalid slot number: "+slot);
				}
				final Card card = cards.get(slot);
				if (romtype.equalsIgnoreCase("rom"))
			    	card.loadRom(base,rom);
				else if (romtype.equalsIgnoreCase("rom7"))
			    	card.loadSeventhRom(base,rom);
				else if (romtype.equalsIgnoreCase("rombank"))
			    	card.loadBankRom(base,rom);
			}
			rom.close();
		}
		else if (cmd.equalsIgnoreCase("load"))
		{
			if (!tok.hasMoreTokens()) throw new IllegalArgumentException("Error in config file: "+line);
			if (!tok.nextToken().equalsIgnoreCase("slot")) throw new IllegalArgumentException("Error in config file: "+line);
			final String sSlot = tok.nextToken();
			final int slot = Integer.decode(sSlot);

			if (!tok.hasMoreTokens()) throw new IllegalArgumentException("Error in config file: "+line);
			if (!tok.nextToken().equalsIgnoreCase("drive")) throw new IllegalArgumentException("Error in config file: "+line);
			final String sDrive = tok.nextToken();
			final int drive = Integer.decode(sDrive);

			if (!tok.hasMoreTokens()) throw new IllegalArgumentException("Error in config file: "+line);
			final String nib = tok.nextToken("\0").trim(); // rest of line
			final File fnib = new File(nib);

			verifyDiskCard(cards,slot);
			if (drive == 1)
			{
				disk1.load(fnib);
			}
			else if (drive == 2)
			{
				disk2.load(fnib);
			}
			else
			{
				if (!tok.hasMoreTokens()) throw new IllegalArgumentException("Error in config file: invalid drive number "+drive);
			}
		}
		else
		{
			throw new IllegalArgumentException("Error in config file: "+line);
		}
	}

	private void verifyDiskCard(List<Card> cards, int slot)
	{
		if (slot < 0 || SLOTS <= slot)
		{
			throw new IllegalArgumentException("Error in config file: invalid slot number: "+slot);
		}
		final Card card = cards.get(slot);
		if (!(card instanceof DiskInterface))
		{
			throw new IllegalArgumentException("Error in config file: the card in slot "+slot+" is not a disk card");
		}
	}

	private void insertCard(final String cardType, final int slot, final List<Card> cards, final DiskState diskState, final UI ui)
	{
		if (slot < 0 || SLOTS <= slot)
		{
			throw new IllegalArgumentException("Error in config file: invalid slot number: "+slot);
		}
		final Card card;
		if (cardType.equalsIgnoreCase("language"))
		{
			card = new LanguageCard();
		}
		else if (cardType.equalsIgnoreCase("firmware"))
		{
	    	card = new FirmwareCard();
		}
		else if (cardType.equalsIgnoreCase("disk"))
		{
	    	card = new DiskInterface(diskState,ui);
		}
		else if (cardType.equalsIgnoreCase("stdout"))
		{
			card = new StandardOut();
		}
		else if (cardType.equalsIgnoreCase("stdin"))
		{
	    	final KeypressQueue stdinkeys = new KeypressQueue();
	    	final StandardInProducer stdinprod = new StandardInProducer(stdinkeys);
	    	card = new StandardIn(ui,stdinkeys);
		}
		else
		{
			throw new IllegalArgumentException("Error in config file: unknown card type: "+cardType);
		}

		cards.set(slot,card);
	}
}
