/* -*- Mode: java; indent-tabs-mode: t; c-basic-offset: 4; tab-width: 4 -*- */

// Copyright 2001-2003, Danger, Inc.  All Rights Reserved.
// This file is subject to the Danger, Inc. Sample Code License,
// which is provided in the file SAMPLE_CODE_LICENSE.
// Copies are also available from http://developer.danger.com/

package com.danger.terminal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TelnetSession implements Session {
	public TelnetSession (HostInfo info, InputStream in, OutputStream out, int width, int height,
		String terminalType) throws IOException
	{
		mCanDoWindowSize = false;
		fRawInput = in;
		fRawOutput = out;
		mColumns = width;
		mRows = height;
		mTerminalType = terminalType;
		mInfo = info;
		mInfo.setStatus(HostInfo.STATUS_OPEN);
		sendInitialOptions();
	}

	/* package */ void
	setConsole (TerminalConsole console)
	{
		mConsole = console;
	}

	public void
	refreshShow()
	{
		// pass
	}

	public InputStream
	getInputStream()
	{
		return new TelnetInputStream();
	}
	
	public OutputStream
	getOutputStream()
	{
		return new TelnetOutputStream();
	}

	public void
	resize (int cols, int rows)
	{
		mColumns = cols;
		mRows = rows;
		if (mCanDoWindowSize) {
			byte[] geometry = new byte[4];
			geometry[0] = (byte)(mColumns >> 8);
			geometry[1] = (byte) mColumns;
			geometry[2] = (byte) (mRows >> 8);
			geometry[3] = (byte) mRows;
			try {
				sendOption(kSuboptionBegin, kWindowSize, geometry);
			} catch (IOException x) {
				// pass
			}
		}
	}

	private void
	write(int ch) throws IOException
	{
		synchronized (this) {
            if (' ' <= ch && ch <= '~')
            {
                fRawOutput.write(ch);
                System.err.print((char)ch);
            }
            else if (ch == '\r')
            {
                fRawOutput.write(ch);
                System.err.print("<CR>");
            }
            else if (ch == '\n')
            {
                System.err.println("<LF>");
                fRawOutput.flush();
            }
            else
            {
                fRawOutput.write(ch);
                System.err.print("<");
                System.err.print(Integer.toHexString(ch));
                System.err.print(">");
            }
		}
	}
	
	private void
	write(byte[] stuff) throws IOException
	{
//		synchronized (this) {
//			fRawOutput.write(stuff);
//		}
        for (int i = 0; i < stuff.length; ++i)
        {
            byte b = stuff[i];
            write(b);
        }
	}

	public void
	write(byte[] stuff, int offset, int length) throws IOException
	{
//		synchronized (this) {
//			fRawOutput.write(stuff, offset, length);
//		}
        for (int i = offset; i < length; ++i)
        {
            byte b = stuff[i];
            write(b);
        }
	}

    private int readOneChar() throws IOException
    {
        int ch = fRawInput.read();
        if (' ' <= ch && ch <= '~')
        {
            System.out.print((char)ch);
        }
        else if (ch == '\r')
        {
            System.out.print("<CR>");
        }
        else if (ch == '\n')
        {
            System.out.println("<LF>");
        }
        else
        {
            System.out.print("<");
            System.out.print(Integer.toHexString(ch));
            System.out.print(">");
        }
        return ch;
    }

	private int
	read() throws IOException
	{
		int ch = readOneChar();
		while (ch == kInterpretAsCommand)
        {
            synchronized (this)
            {
                processCommand();
            }
            ch = readOneChar();
		}
		return ch;
	}

    // CAM:
    private int
    readNoWait() throws IOException
    {
        if (fRawInput.available() == 0)
        {
            return -1;
        }
        int ch;
        for (;;) {
            ch = readOneChar();
            synchronized (this) {
                if (ch != kInterpretAsCommand)
                    break;

                processCommand();
            }
            boolean avail = fRawInput.available() > 0;
            if (!avail)
            {
                ch = -1;
                break;
            }
        }

        if (mWaitingForOptions) {
            // assume dumb remote host, turn on line mode
            mWaitingForOptions = false;
            mConsole.setLineMode(true);
        }

        return ch;
    }

	private int
	read (byte[] b, int off, int len)
		throws IOException
	{
		if (len == 0) {
			return 0;
		}

		int count = 0;

		/* do a blocking call on the first byte */
		int ch = read();
		synchronized (this) {
			if (ch == -1) {
				throw new IOException("End of stream");
			}
			b[off++] = (byte)ch;
			count++;

			/* after the first byte has been read, check available() and only
			 * read in what's buffered.  this is to mimic standard read(byte[])
			 * sematics.
			 */
			while ((count < len) && (fRawInput.available() > 0)) {
				ch = readNoWait(); // CAM
				if (ch == -1) {
					return count;
				}
				b[off++] = (byte)ch;
				count++;System.err.println("count: "+count);
			}
		}

		return count;
	}

	private void
	sendInitialOptions() throws IOException
	{
		synchronized (this) {
			sendOption(kWill, kTerminalType);
			sendOption(kWill, kWindowSize);
			// request to get out of line-mode
			mWaitingForOptions = true;
			sendOption(kWill, kSuppressGoAhead);
		}
	}
	
	private void
	processCommand() throws IOException
	{
		int command = readOneChar();
		int option;
		switch (command) {
			case kNOP:
				break;

			case kWill:
			case kWont:
			case kDo:
			case kDont:
				option = readOneChar();
				if (processOption(command, option, null)) {
					if (command == kWill)
						sendOption(kDo, option);
				} else {
					if (command == kWill)
						sendOption(kDont, option);
					else if (command == kDo)
						sendOption(kWont, option);
				}
				
				break;

			case kSuboptionBegin:
				do {
					option = readOneChar();
					if (option == kSuboptionEnd)
						break;
						
					byte[] optionData = new byte[64];
					int offs = 0;
					for (;;) {
						int ch = readOneChar();
						if (ch == kInterpretAsCommand)
							break;
						
						optionData[offs++] = (byte) ch;
					}

					processOption(kSuboptionBegin, option, optionData);
				} while (true);
				break;
				
			default:
				System.out.println("Unknown command " + command);
		}
	}
	
	private boolean
	processOption(int command, int option, byte data[])
		throws IOException
	{
		System.out.println("RECV: " + command2String(command) + " " + option2String(option));
		switch (option) {
			case kEcho:
				if (command == kWill) {
					// turn off local echo
					mConsole.setLocalEcho(false);
				} else if (command == kWont) {
					// turn on local echo
					mConsole.setLocalEcho(true);
				}
				break;

			case kSuppressGoAhead:
				mWaitingForOptions = false;
				if (command == kDo) {
					sendOption(kDo, option);
				} else if (command == kWill) {
					mConsole.setLineMode(false);
				} else {
					mConsole.setLineMode(true);
				}
				break;

			case kLogout:
				if (command == kDo)
					sendOption(kWill, option);

				break;
				
			case kTerminalType:
				if (command == kSuboptionBegin && data[0] == kSend) {
					byte[] termString = mTerminalType.getBytes();
					byte[] optionval = new byte[termString.length + 1];
					System.arraycopy(termString, 0, optionval, 1, termString.length);
					optionval[0] = kIs;
					sendOption(kSuboptionBegin, kTerminalType, optionval);
				}

				break;
				
			case kWindowSize:
				if (command == kDo) {
					mCanDoWindowSize = true;
					byte[] geometry = new byte[4];
					geometry[0] = (byte)(mColumns >> 8);
					geometry[1] = (byte) mColumns;
					geometry[2] = (byte) (mRows >> 8);
					geometry[3] = (byte) mRows;
					sendOption(kSuboptionBegin, kWindowSize, geometry);
				}
				
				break;
				
			case kEnvironment:
				if (command == kSuboptionBegin && data[0] == kSend) {
					byte[] env = new byte[1];
					env[0] = kIs;
					sendOption(kSuboptionBegin, kEnvironment, env);
				} else if (command == kDo)
					sendOption(kWill, kEnvironment);
				
				break;
				
			case kExtendedOptions:
				// Falls through for now.
				System.out.println("oops");

			default:	// Unrecognized option
				return false;
		}
		
		
		return true;
	}
	
	private void
	sendOption(int command, int option) throws IOException
	{
		sendOption(command, option, null);
	}
	
	private void
	sendOption(int command, int option, byte data[]) throws IOException
	{
		System.out.println("SEND: " + command2String(command) + " " + option2String(option));
		synchronized (this) {
			fRawOutput.write(kInterpretAsCommand);
			switch (command) {
				case kSuboptionBegin:
					fRawOutput.write(kSuboptionBegin);
					fRawOutput.write(option);
					fRawOutput.write(data);
					fRawOutput.write(kInterpretAsCommand);
					fRawOutput.write(kSuboptionEnd);
					break;

				default:
					fRawOutput.write(command);
					fRawOutput.write(option);
			}
		}
	}
	
	private String
	command2String(int command)
	{
		switch (command) {
			case 240:
				return "SE";
			case 241:
				return "NOP";
			case 242:
				return "MARK";
			case 243:
				return "BRK";
			case 244:
				return "IP";
			case 245:
				return "AO";
			case 246:
				return "AYT";
			case 247:
				return "EC";
			case 248:
				return "EL";
			case 249:
				return "GA";
			case 250:
				return "SB";
			case 251:
				return "WILL";
			case 252:
				return "WONT";
			case 253:
				return "DO";
			case 254:
				return "DONT";
			case 255:
				return "IAC";
			default:
				return "" + command;
		}
	}

	private String
	option2String(int option)
	{
		switch (option) {
			case 0: return "BIN";
			case 1: return "Echo"; 
			case 2: return "reconnect";   
			case 3: return "Suppress Go Ahead";
			case 4: return "Approx Message Size";
			case 5: return "Status";
			case 6: return "Timing Mark"; 
			case 7: return "Remote Controlled Trans and Echo";
			case 8: return "Output Line Width";   
			case 9: return "Output Page Size";
			case 10: return "Output Carriage-Return Disposition"; 
			case 11: return "Output Horizontal Tab Stops"; 
			case 12: return "Output Horizontal Tab Disposition"; 
			case 13: return "Output Formfeed Disposition"; 
			case 14: return "Output Vertical Tabstops"; 
			case 15: return "Output Vertical Tab Disposition"; 
			case 16: return "Output Linefeed Disposition"; 
			case 17: return "Extended ASCII"; 
			case 18: return "Logout"; 
			case 19: return "Byte Macro"; 
			case 20: return "Data Entry Terminal";
			case 21: return "SUPDUP"; 
			case 22: return "SUPDUP Output"; 
			case 23: return "Send Location"; 
			case 24: return "Terminal Type"; 
			case 25: return "End of Record"; 
			case 26: return "TACACS User Identification"; 
			case 27: return "Output Marking"; 
			case 28: return "Terminal Location Number"; 
			case 29: return "Telnet 3270 Regime";
			case 30: return "X.3 PAD"; 
			case 31: return "NAWS";
			case 32: return "Terminal Speed"; 
			case 33: return "Remote Flow Control"; 
			case 34: return "Linemode";
			case 35: return "X Display Location"; 
			case 36: return "Environment"; 
			case 37: return "Authentication";
			case 38: return "Encryption"; 
			case 39: return "New Environment"; 
			case 255: return "EXT";
			default:
				return "" + option;
		}
	}


	private InputStream fRawInput;
	private OutputStream fRawOutput;
	
	// Current configuration
	private String mTerminalType;
	private int mColumns;
	private int mRows;
	private boolean mCanDoWindowSize;
	private boolean mWaitingForOptions;

	private HostInfo mInfo;
	private TerminalConsole mConsole;

	// Telnet commands
	private static final int kSuboptionEnd = 240;
	private static final int kNOP = 241;
	private static final int kDataMark = 242;
	private static final int kBreak = 243;
	private static final int kInterrupt = 244;
	private static final int kAbort = 245;
	private static final int kAreYouThere = 246;
	private static final int kEraseChar = 247;
	private static final int kEraseLine = 248;
	private static final int kGoAhead = 249;
	private static final int kSuboptionBegin = 250;
	private static final int kWill = 251;
	private static final int kWont = 252;
	private static final int kDo = 253;
	private static final int kDont = 254;
	private static final int kInterpretAsCommand = 255;

	// Telnet options
	private static final int kEcho = 1;
	private static final int kSuppressGoAhead = 3;
	private static final int kLogout = 18;
	private static final int kTerminalType = 24;
	private static final int kWindowSize = 31;
	private static final int kLineMode = 34;
	private static final int kEnvironment = 36;
	private static final int kAuthentication = 37;
	private static final int kExtendedOptions = 255;

	// Suboption specific parameters
	private static final int kIs = 0;
	private static final int kSend = 1;

	class TelnetInputStream extends InputStream
	{
		public int read() throws IOException {
			return TelnetSession.this.read();
		}

		public int read (byte[] b, int off, int len) throws IOException {
			return TelnetSession.this.read(b, off, len);
		}
	}

	class TelnetOutputStream extends OutputStream
	{
		public void write(byte[] stuff) throws IOException {
			TelnetSession.this.write(stuff);
		}
		
		public void write(int value) throws IOException {
			TelnetSession.this.write(value);
		}
		
		public void write(byte[] stuff, int offset, int length) throws IOException {
			TelnetSession.this.write(stuff, offset, length);
		}
	}
}

