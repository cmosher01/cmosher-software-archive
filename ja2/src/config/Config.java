package config;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import keyboard.KeypressQueue;
import stdio.StandardIn;
import stdio.StandardInProducer;
import stdio.StandardOut;
import cards.disk.DiskController;
import cards.disk.InvalidDiskImage;
import cards.memory.FirmwareCard;
import cards.memory.LanguageCard;
import chipset.Card;
import chipset.InvalidMemoryLoad;
import chipset.Memory;
import chipset.Slots;

/*
 * Created on Dec 1, 2007
 */
public class Config
{
	public static final int SLOTS = 8;

	private String filename;

	public Config(final String filename)
	{
		this.filename = filename;
	}

	public void parseConfig(final Memory memory, final Slots slots, final StandardIn.EOFHandler eofHandler)
		throws IOException, InvalidMemoryLoad, InvalidDiskImage
	{

		final BufferedReader cfg = new BufferedReader(new InputStreamReader(new FileInputStream(new File(this.filename))));

    	for (String line = cfg.readLine(); line != null; line = cfg.readLine())
    	{
    		int comment = line.indexOf('#');
    		if (comment >= 0)
    		{
    			line = line.substring(0,comment);
    		}
    		line = line.trim();

    		parseLine(line,memory,slots,eofHandler);
    	}

    	cfg.close();

    	verifyUniqueCards(slots);
	}

	private void verifyUniqueCards(final Slots cards)
	{
		int nStdOut = 0;
		int nStdIn = 0;
		for (Card card : cards)
		{
			if (card instanceof StandardOut)
			{
				++nStdOut;
			}
			else if (card instanceof StandardIn)
			{
				++nStdIn;
			}
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

	private void parseLine(final String line, final Memory memory, final Slots slots, final StandardIn.EOFHandler eofHandler)
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

			insertCard(sCardType,slot,slots,eofHandler);
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
				final Card card = slots.get(slot);
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

			loadDisk(slots,slot,drive,fnib);
		}
		else
		{
			throw new IllegalArgumentException("Error in config file: "+line);
		}
	}

	private void loadDisk(final Slots slots, final int slot, final int drive, final File fnib) throws IOException, InvalidDiskImage
	{
		if (drive < 1 || 2 < drive)
		{
			throw new IllegalArgumentException("Error in config file: invalid drive number "+drive);
		}
		slots.loadDisk(slot,drive-1,fnib);
	}

	private void insertCard(final String cardType, final int slot, final Slots slots, final StandardIn.EOFHandler eofHandler)
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
	    	card = new DiskController();
		}
		else if (cardType.equalsIgnoreCase("stdout"))
		{
			card = new StandardOut();
		}
		else if (cardType.equalsIgnoreCase("stdin"))
		{
	    	final KeypressQueue stdinkeys = new KeypressQueue();
	    	new StandardInProducer(stdinkeys);
	    	card = new StandardIn(eofHandler,stdinkeys);
		}
		else
		{
			throw new IllegalArgumentException("Error in config file: unknown card type: "+cardType);
		}

		slots.set(slot,card);
	}
}
