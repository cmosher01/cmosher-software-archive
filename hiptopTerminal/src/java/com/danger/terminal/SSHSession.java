/* -*- Mode: java; indent-tabs-mode: t; c-basic-offset: 4; tab-width: 4 -*- */

// Copyright 2001-2003, Danger, Inc.  All Rights Reserved.
// This file is subject to the Danger, Inc. Sample Code License,
// which is provided in the file SAMPLE_CODE_LICENSE.
// Copies are also available from http://developer.danger.com/

package com.danger.terminal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import danger.app.Application;
import danger.app.Event;
import danger.app.Listener;
import danger.app.Timer;
import danger.crypto.BigInteger;
import danger.crypto.Cipher;
import danger.crypto.HMAC;
import danger.crypto.MD5Factory;
import danger.crypto.SHA;
import danger.crypto.SHAFactory;
import danger.crypto.SecureRandom;
import danger.ui.DialogWindow;
import danger.ui.StaticText;
import danger.ui.TextField;
import danger.util.ByteArray;
import danger.util.DEBUG;
import danger.util.format.StringFormat;

public class SSHSession extends Listener implements Runnable, Session, TerminalResources, danger.SystemStrings,
							TerminalEvents
{

	public SSHSession(SSHHostInfo info, InputStream in, OutputStream out, int width, int height,
					  String terminalType)
		throws IOException
	{
		fRawInput = in;
		fRawOutput = out;
		mColumns = width;
		mRows = height;
		mTerminalType = terminalType;
		mOutputBufferSize = info.getWindowSize();
		mOutputBuffer = new byte[mOutputBufferSize];
		mOutputBufferTail = 0;
		mLogin = null;
		mPassword = null;
		mLoggedIn = false;
		mInfo = info;
	}

	public void
	refreshShow ()
	{
		synchronized (this) {
			if (mLoginWindow != null) {
				// make sure the login dialog floats back up to the top, if it's open
				mLoginWindow.show();
			} else if (mHostKeyWindow != null) {
				mHostKeyWindow.show();
			}
		}
	}

	public void
	setHostname (String hostname)
	{
		mHostname = hostname;
	}

	public void
	jumpStart ()
	{
		(new Thread(this, "SSH:" + mHostname)).start();
	}

	public InputStream
	getInputStream ()
	{
		return new SSHInputStream();
	}

	public OutputStream
	getOutputStream ()
	{
		return new SSHOutputStream();
	}

	public void
	resize (int cols, int rows)
	{
		mColumns = cols;
		mRows = rows;
		if (mLoggedIn) {
			try {
				resizeTerminal();
			} catch (IOException x) {
				// pass
			}
		}
	}

	private void
	write(byte[] stuff)
		throws IOException
	{
		synchronized (this) {
			fRawOutput.write(stuff);
		}
	}

	private void
	write(byte[] stuff, int offset, int length)
		throws IOException
	{
		synchronized (this) {
			fRawOutput.write(stuff, offset, length);
		}
	}

	private void
	write(String s)
		throws IOException
	{
		write(s.getBytes());
	}

	private void
	checkWriteWindow ()
		throws IOException
	{
		synchronized (mServerWindowBuffer) {
			if (mServerWindowSize == 0) {
				return;
			}
			if (mServerWindowSize < mServerWindowBufferSize) {
				writeChannelData(mServerWindowBuffer, mServerWindowSize);
				System.arraycopy(mServerWindowBuffer, mServerWindowSize, mServerWindowBuffer, 0,
								 mServerWindowBufferSize - mServerWindowSize);
				mServerWindowSize = 0;
				mServerWindowBuffer.notify();
				return;
			}
			writeChannelData(mServerWindowBuffer, mServerWindowBufferSize);
			mServerWindowBufferSize = 0;
			mServerWindowSize -= mServerWindowBufferSize;
			mServerWindowBuffer.notify();
		}
	}

	private void
	writeWindow (byte[] in, int offset, int length)
		throws IOException
	{
		synchronized (mServerWindowBuffer) {
			while (mServerWindowBuffer.length - mServerWindowBufferSize < length) {
				if (mDisconnected) {
					return;
				}
				try {
					mServerWindowBuffer.wait();
				} catch (InterruptedException e) {
					// pass
				}
			}
			System.arraycopy(in, offset, mServerWindowBuffer, mServerWindowBufferSize, length);
			mServerWindowBufferSize += length;
			checkWriteWindow();
		}
	}

	private void add (byte[] in)
	{
		synchronized (mOutputBuffer) {
			while (in.length + mOutputBufferTail > mOutputBufferSize) {
				try {
					mOutputBuffer.wait();
				} catch (InterruptedException e) {
					// pass
				}
			}
			System.arraycopy(in, 0, mOutputBuffer, mOutputBufferTail, in.length);
			mOutputBufferTail += in.length;
			mOutputBuffer.notify();
		}
	}

	private void add (String s)
	{
		char[] c = s.toCharArray();
		byte[] b = new byte[c.length];
		for (int i = 0, len = c.length; i < len; i++) {
			b[i] = (byte)((int)c[i] & 0xff);
		}
		add(b);
	}

	private void addLine (String s)
	{
		add(s);
		add("\r\n".getBytes());
	}

	private void disconnect ()
	{
		synchronized (this) {
			mDisconnected = true;
			this.notifyAll();	// wake up people waiting on login info
			if (mLoginWindow != null) {
				mLoginWindow.hide();
				mLoginWindow = null;
			}
			if (mHostKeyWindow != null) {
				mHostKeyWindow.hide();
				mHostKeyWindow = null;
			}
			synchronized (mOutputBuffer) {
				// EOF
				mOutputBuffer.notify();
			}
			if (mServerWindowBuffer != null) {
				synchronized (mServerWindowBuffer) {
					// if any thread is waiting for the server window to open
					mServerWindowBuffer.notify();
				}
			}
		}
	}

	private void waitForLoginInfo ()
	{
		synchronized (this) {
			while ((mLogin == null) && ! mDisconnected) {
				try {
					this.wait();
				} catch (InterruptedException x) { }
			}
		}
	}

	public boolean receiveEvent (Event e) {
		switch (e.type) {
		case EVENT_AUTHENTICATE:
			synchronized (this) {
				mLoginWindow = null;
				DialogWindow dialog = (DialogWindow) e.argument;
				mLogin = ((TextField) dialog.getDescendantWithID(ID_LOGIN_USERNAME)).toString();
				mPassword = ((TextField) dialog.getDescendantWithID(ID_LOGIN_PASSWORD)).toString();
				mInfo.setUsername(mLogin);
				this.notifyAll();
			}
			return true;
		case EVENT_DONT_AUTHENTICATE:
			addLine(Terminal.instance().getString(STRING_DISCONNECTED_NO_AUTH));
			synchronized (this) {
				mLoginWindow = null;
				try {
					fRawInput.close();
					fRawOutput.close();
				} catch (IOException x) { }
				disconnect();
			}
			return true;
		case EVENT_SAVE_HOST_KEY:
			saveHostKey();
			// fall through
		case EVENT_DISMISS_HOST_KEY:
			synchronized (this) {
				mHostKeyWindow = null;
			}
			if (! mLoggedIn && ! mDisconnected) {
				try {
					sendLogin();
				} catch (IOException x) { }
			}
			return true;
		case EVENT_REJECT_HOST_KEY:
			addLine(Terminal.instance().getString(STRING_HOST_KEY_REJECTED));
			synchronized (this) {
				mHostKeyWindow.hide();
				mHostKeyWindow = null;
				try {
					fRawInput.close();
					fRawOutput.close();
				} catch (IOException x) { }
				disconnect();
			}
			return true;
		case Event.EVENT_TIMER:
			synchronized (this) {
				if (mWindowUsed > kWindowThreshold) {
					try {
						debugTraffic("window increase: " + mWindowUsed);
						sendAddWindow(mWindowUsed);
					} catch (IOException x) {
						// pass
					}
					mWindowUsed = 0;
				}
			}
			break;
		}

		return super.receiveEvent(e);
	}

	/** If the user asks us to remember their password, the settings screen
	 * may call this function to retrieve the password they used.
	 */
	/* package */ String
	getPassword ()
	{
		if (mLoggedIn && (mLogin != null) && (mPassword != null)) {
			return mPassword;
		} else {
			return null;
		}
	}


	// ----------  guts  ----------

	private String readLine ()
		throws IOException
	{
		StringBuffer line = new StringBuffer();
		int ch = fRawInput.read();

		while (ch != 10) {
			if (ch < 0) {
				// EOF
				throw new IOException("End of stream");
			}
			line.append((char)ch);
			ch = fRawInput.read();
		}
		if ((line.length() > 0) && (line.charAt(line.length()-1) == '\r')) {
			line.deleteCharAt(line.length()-1);
		}
		return line.toString();
	}

	private void encodeInt(int n, byte[] buffer, int offset)
	{
		buffer[offset] = (byte)((n >> 24) & 0xff);
		buffer[offset+1] = (byte)((n >> 16) & 0xff);
		buffer[offset+2] = (byte)((n >> 8) & 0xff);
		buffer[offset+3] = (byte)(n & 0xff);
	}

	private int decodeInt(byte[] buffer, int offset)
	{
		return (((int) buffer[offset] & 0xff) << 24) |
			(((int) buffer[offset+1] & 0xff) << 16) |
			(((int) buffer[offset+2] & 0xff) << 8) |
			((int) buffer[offset+3] & 0xff);
	}

	private void sendMessage(SSHMessage msg)
		throws IOException
	{
		// pad up at least 4 bytes, to nearest block-size (usually 8)
		int len = msg.getBufferLength();
		int padding = kBlockSize - ((len + 1) % kBlockSize) + 4;

		byte[] buffer = new byte[len + padding + 5];
		encodeInt(len + padding + 1, buffer, 0);
		buffer[4] = (byte)padding;
		msg.copyBuffer(buffer, 5);
		SecureRandom.getBytes(buffer, len+5, padding);

		// (encipher + write) needs to be atomic, and the sequence number
		synchronized (this) {
			byte[] hmacBuffer = null;
			if (mMacKeyOut != null) {
				HMAC hmac;
				if (mUsingMD5HMAC) {
					hmac = new HMAC(new MD5Factory());
				} else {
					hmac = new HMAC(new SHAFactory());
				}
				hmac.init(mMacKeyOut);
				byte[] blob = new byte[4];
				encodeInt(mOutSequence, blob, 0);
				hmac.update(blob);
				hmac.update(buffer);
				hmacBuffer = hmac.digest();
			}

			if (mCipherOut != null) {
				// encrypt
				mCipherOut.encrypt(buffer, 0, buffer, 0, buffer.length);
			}

			write(buffer);
			if (hmacBuffer != null) {
				write(hmacBuffer, 0, mMacSize);
			}

			mOutSequence++;
		}
	}

	// at some point, our InputStream.read() function started actually
	// following the spec. (the nerve!)  so, this implements "danger"
	// read semantics.
	private static void
	dread (InputStream is, byte[] b, int start, int len)
		throws IOException
	{
		while (len > 0) {
			int n = is.read(b, start, len);
			if (n < 0) {
				throw new IOException("End of Stream");
			}
			start += n;
			len -= n;
		}
	}

	private static void
	dread (InputStream is, byte[] b)
		throws IOException
	{
		dread(is, b, 0, b.length);
	}

	private SSHMessage readMessage()
		throws IOException
	{
		byte[] header = new byte[kBlockSize];

		// read one block first and decrypt it --
		// that'll give us the packet's actual size
		dread(fRawInput, header);
		if (mCipherIn != null) {
			// decrypt
			mCipherIn.decrypt(header, 0, header, 0, header.length);
		}

		int packetSize = decodeInt(header, 0);
		int prefetched = kBlockSize-4;
		byte[] packet = new byte[packetSize];
		System.arraycopy(header, 4, packet, 0, prefetched);

		if (packetSize % kBlockSize != 4) {
			throw new IOException(StringFormat.fromRsrc(STRING_POORLY_BLOCKED, String.valueOf(packetSize)));
		}
		if (packetSize > kMaxPacketSize) {
			throw new IOException(Terminal.instance().getString(STRING_IMPROBABLE_PACKET));
		}
		dread(fRawInput, packet, prefetched, packetSize-prefetched);
		if (mCipherIn != null) {
			// decrypt
			mCipherIn.decrypt(packet, prefetched, packet, prefetched, packetSize-prefetched);
		}

		if (mMacKeyIn != null) {
			// read and verify mac
			byte[] their_hmac = new byte[mMacSize];
			dread(fRawInput, their_hmac, 0, mMacSize);
			HMAC hmac;
			if (mUsingMD5HMAC) {
				hmac = new HMAC(new MD5Factory());
			} else {
				hmac = new HMAC(new SHAFactory());
			}
			hmac.init(mMacKeyIn);
			byte[] blob = new byte[4];
			encodeInt(mInSequence, blob, 0);
			hmac.update(blob);
			encodeInt(packetSize, blob, 0);
			hmac.update(blob);
			hmac.update(packet);
			byte[] my_hmac = hmac.digest();
			if (! ByteArray.startsWith(my_hmac, 0, my_hmac.length, their_hmac)) {
				throw new IOException(Terminal.instance().getString(STRING_CORRUPTED_HMAC));
			}
		}

		int padding = (int)packet[0];

		//DEBUG.p("got packet of size " + packetSize + " (padding " + padding + ") type " + packet[1]);

		mInSequence++;
		return new SSHMessage(packet, 1, packetSize - padding - 1);
	}

	private static boolean StringListContains (String[] in, String s)
	{
		for (int i = 0; i < in.length; i++) {
			if (in[i].equalsIgnoreCase(s)) {
				return true;
			}
		}
		return false;
	}

	private static String StringListToString (String[] in)
	{
		StringBuffer out = new StringBuffer();

		for (int i = 0; i < in.length; i++) {
			if (i > 0) {
				out.append(", ");
			}
			out.append("'");
			out.append(in[i]);
			out.append("'");
		}
		return out.toString();
	}

	private byte[] generateKey (byte id)
	{
		SSHMessage msg = new SSHMessage();
		msg.add(mK);
		msg.add(mH);
		msg.add(id);
		msg.add(mH);
		SHA digest = new SHA();
		digest.update(msg.getBuffer());
		return digest.digest();
	}

	public void popupLoginDialog ()
	{
		Application	app = Terminal.instance();

		synchronized (this) {
			mLoginWindow = app.getDialog(ID_LOGIN_DIALOG, this);

			StaticText text = (StaticText) mLoginWindow.getDescendantWithID(ID_LOGIN_DIALOG_HEADING);
			if (mFailedLogins > 0) {
				text.setText(StringFormat.fromRsrc(STRING_RETRY_LOGIN, mHostname));
			} else {
				text.setText(StringFormat.withFormat(text.toString(), mHostname));
			}
			mLoginWindow.invalidate();

			mLoginWindow.setCancelButtonEvent(new Event(this, EVENT_DONT_AUTHENTICATE, 0, 0, mLoginWindow));

			String login = mInfo.getUsername();
			String password = null;
			if (login != null) {
				password = mInfo.getSavedPassword();
			}
			if (login != null) {
				((TextField) mLoginWindow.getDescendantWithID(ID_LOGIN_USERNAME)).setText(login);
			}
			if (password != null) {
				((TextField) mLoginWindow.getDescendantWithID(ID_LOGIN_PASSWORD)).setText(password);
			}

			mLoginWindow.show();
			
			/* show resets the focussed field... */
			if (login != null && login.length() > 0) {
				if (password != null && password.length() > 0) {
					mLoginWindow.setFocusedChild(mLoginWindow.getDescendantWithID(ID_LOGIN_OK));
				} else {
					mLoginWindow.setFocusedChild(mLoginWindow.getDescendantWithID(ID_LOGIN_PASSWORD));
				}
			}
		}
	}

	private void saveHostKey ()
	{
		if (mUsingDSSKey) {
			mInfo.setDSSKey(mHostKeyBlob);
		} else {
			mInfo.setRSAKey(mHostKeyBlob);
		}
	}

	private byte[] loadHostKey ()
	{
		if (mUsingDSSKey) {
			return mInfo.getDSSKey();
		} else {
			return mInfo.getRSAKey();
		}
	}

	public boolean
	getUsingDSSKey ()
	{
		return mUsingDSSKey;
	}

	public String decodeChannelReason (int reasonCode)
	{
		switch (reasonCode) {
		case 1:
			return Terminal.instance().getString(STRING_CHAN_PROHIBITED);
		case 2:
			return Terminal.instance().getString(STRING_CHAN_CONNECT_FAILED);
		case 3:
			return Terminal.instance().getString(STRING_CHAN_UNKNOWN_TYPE);
		case 4:
			return Terminal.instance().getString(STRING_CHAN_RESOURCE_SHORTAGE);
		}

		return "???";
	}


	// ----------  specific protocol pieces-parts  ----------

	private void verifyServerBanner ()
		throws IOException
	{
		String serverBanner = readLine();
		try {
			if (! serverBanner.substring(0, 4).equals("SSH-")) {
				addLine(Terminal.instance().getString(STRING_BAD_BANNER));
				throw new IOException();
			}
		} catch (IndexOutOfBoundsException e) {
			addLine(Terminal.instance().getString(STRING_BAD_BANNER));
			throw new IOException();
		}

		mServerBanner = serverBanner;

		// discard any comment
		int x = serverBanner.indexOf(' ');
		if (x >= 0) {
			serverBanner = serverBanner.substring(0, x);
		}

		// figure out protocol version
		String protocolVersion = "0.0";
		x = serverBanner.indexOf('-');
		if (x >= 0) {
			int y = serverBanner.indexOf('-', x+1);
			if (y >= 0) {
				protocolVersion = serverBanner.substring(x+1, y);
			}
		}
		if (! protocolVersion.equals("1.99") && ! protocolVersion.substring(0, 2).equals("2.")) {
			addLine(Terminal.instance().getString(STRING_INCOMPATIBLE_SSH));
			throw new IOException();
		}
	}

	// send a really wimpy kex-init packet that says we're a bare-bones ssh client
	private void sendKexInit ()
		throws IOException
	{
		DEBUG.p("send kex init");
		byte[] randBytes = new byte[16];
		SSHMessage msg = new SSHMessage();
		msg.add(kMsg_KexInit);
		SecureRandom.getBytes(randBytes);
		msg.add(randBytes);
		msg.add("diffie-hellman-group1-sha1");
		if (mInfo.getPreferDSS()) {
			msg.add("ssh-dss,ssh-rsa");
		} else {
			msg.add("ssh-rsa,ssh-dss");
		}
		msg.add("blowfish-cbc");
		msg.add("blowfish-cbc");
		msg.add("hmac-sha1-96,hmac-md5");
		msg.add("hmac-sha1-96,hmac-md5");
		msg.add("none");
		msg.add("none");
		msg.add("");
		msg.add("");
		msg.add(false);
		msg.add(0);
		// save a copy for later
		mClientKexInit = msg.getBuffer();
		sendMessage(msg);
	}

	private void parseKexInit (SSHMessage msg)
		throws IOException
	{
		byte[] cookie = msg.getBytes(16);
		String[] kexAlgoList = msg.getStringList();
		String[] keyAlgoList = msg.getStringList();
		String[] clientEncryptAlgoList = msg.getStringList();
		String[] serverEncryptAlgoList = msg.getStringList();
		String[] clientMacAlgoList = msg.getStringList();
		String[] serverMacAlgoList = msg.getStringList();
		String[] clientZAlgoList = msg.getStringList();
		String[] serverZAlgoList = msg.getStringList();
		String[] clientLangList = msg.getStringList();
		String[] serverLangList = msg.getStringList();
		boolean kexFollows = msg.getBoolean();
		int unused = msg.getInt();

		if ((cookie == null) || (kexAlgoList == null) || (keyAlgoList == null) ||
			(clientEncryptAlgoList == null) || (serverEncryptAlgoList == null) ||
			(clientMacAlgoList == null) || (serverMacAlgoList == null) ||
			(clientZAlgoList == null) || (serverZAlgoList == null) ||
			(clientLangList == null) || (serverLangList == null)) {
			throw new IOException("Incomplete kex init");
		}

		System.err.println("kex algos: " + StringListToString(kexAlgoList));
		System.err.println("key algos: " + StringListToString(keyAlgoList));
		System.err.println("encrypt algos: " + StringListToString(clientEncryptAlgoList) + "/" +
						   StringListToString(serverEncryptAlgoList));
		System.err.println("mac algos: " + StringListToString(clientMacAlgoList) + "/" +
						   StringListToString(serverMacAlgoList));
		System.err.println("compress algos: " + StringListToString(clientZAlgoList) + "/" +
						   StringListToString(serverZAlgoList));
		System.err.println("langs: " + StringListToString(clientLangList) + "/" +
						   StringListToString(serverLangList));
		System.err.println("kex follows? " + (kexFollows ? "yes" : "no"));

		// we are *really picky* here.  theoretically our choices are the minimum
		// that a compliant ssh2 server can offer, though.
		// have to add hmac-md5 because spies.com doesn't have hmac-sha1-96 (even tho it's required!)
		if (! StringListContains(kexAlgoList, "diffie-hellman-group1-sha1")) {
			throw new IOException("Incompatible key exchange");
		}
		if (! StringListContains(keyAlgoList, "ssh-rsa") &&
			! StringListContains(keyAlgoList, "ssh-dss")) {
			throw new IOException("Incompatible key format");
		}
		if (! StringListContains(clientEncryptAlgoList, "blowfish-cbc") ||
			! StringListContains(serverEncryptAlgoList, "blowfish-cbc")) {
			throw new IOException("Incompatible encryption");
		}
		if (! StringListContains(clientMacAlgoList, "hmac-sha1-96") &&
			! StringListContains(clientMacAlgoList, "hmac-md5")) {
			throw new IOException("Incompatible HMAC");
		}
		if (! StringListContains(serverMacAlgoList, "hmac-sha1-96") &&
			! StringListContains(serverMacAlgoList, "hmac-md5")) {
			throw new IOException("Incompatible HMAC");
		}
		if (! StringListContains(clientZAlgoList, "none") ||
			! StringListContains(serverZAlgoList, "none")) {
			throw new IOException("Incompatible compression");
		}

		mMacSize = 12;
		mMacNaturalSize = 20;
		if (mInfo.getPreferDSS()) {
			mUsingDSSKey = StringListContains(keyAlgoList, "ssh-dss");
		} else {
			mUsingDSSKey = ! StringListContains(keyAlgoList, "ssh-rsa");
		}
		if (! StringListContains(clientMacAlgoList, "hmac-sha1-96")) {
			mUsingMD5HMAC = true;
			mMacSize = 16;
			mMacNaturalSize = 16;
		}

		// we have to SAVE this giant blob :/
		mServerKexInit = msg.getBuffer();
	}

	private void generateDH ()
		throws IOException
	{
		addLine(Terminal.instance().getString(STRING_COMPUTING_E));

		// first, generate an "x" (1 < x < q), where q = (p-1)/2.
		// p is a 1024-bit number, where the first 64 bits are 1.
		// therefore, q can be approximated at 2^1023.  we drop the subset of
		// potential x where the first 63 bits are 1, because some of those
		// will be larger than q (but this is a tiny subset of potential x).
		byte[] xBytes = new byte[128];
		do {
			SecureRandom.getBytes(xBytes);
			xBytes[0] = (byte)(xBytes[0] & 0x7f);
		} while (((xBytes[0] == 0x7f) && (xBytes[1] == 0xff) && (xBytes[2] == 0xff) &&
				  (xBytes[3] == 0xff) && (xBytes[4] == 0xff) && (xBytes[5] == 0xff) &&
				  (xBytes[6] == 0xff) && (xBytes[7] == 0xff)) ||
				 (xBytes[0] == 0));
		mX = new BigInteger(xBytes);

		// now compute e = g^x mod P, where g=2
		mE = kG.modPow(mX, mP);

		DEBUG.p("Got e (whew)");
		SSHMessage msg = new SSHMessage();
		msg.add(kMsg_KexDHInit);
		msg.add(mE);
		sendMessage(msg);
	}

	private boolean verifyRSASignature (byte[] host_key, byte[] signature_bytes, byte[] data)
		throws IOException
	{
		addLine(Terminal.instance().getString(STRING_VERIFYING_RSA));

		SSHMessage keyMsg = new SSHMessage(host_key);
		String keyType = keyMsg.getString();
		// pull out (e, n) from the host key
		BigInteger keyE = keyMsg.getBigInteger();
		BigInteger keyN = keyMsg.getBigInteger();

		if ((keyType == null) || (keyE == null) || (keyN == null)) {
			addLine(Terminal.instance().getString(STRING_CORRUPTED_HOST_KEY));
			throw new IOException();
		}
		if (! keyType.equals("ssh-rsa")) {
			throw new IOException("Expected ssh-rsa key, got " + keyType);
		}

		// pull out the signature
		SSHMessage sigMsg = new SSHMessage(signature_bytes);
		String sigName = sigMsg.getString();
		byte[] signature = sigMsg.getByteString();

		if ((sigName == null) || (signature == null)) {
			addLine(Terminal.instance().getString(STRING_CORRUPTED_SIG));
			throw new IOException();
		}

		// this is kinda hacky, but should suffice: sometimes the high bit of the signature is set,
		// which is okay, but we need it to be treated as an unsigned number.
		if ((signature[0] & 0x80) == 0x80) {
			byte[] newSig = new byte[signature.length+1];
			newSig[0] = 0;
			System.arraycopy(signature, 0, newSig, 1, signature.length);
			signature = newSig;
		}
		//DEBUG.p("sig '" + sigName + "' (" + signature.length + "): " + SSHMessage.makeBufferPrintable(signature));

		// now verify the signature by SHA'ing H (yes, again) and then
		// encrypting using the server's public host key.  because the
		// encryption results in a 1024-bit number, we actually need to chop
		// it to only the last 20 bytes, because the rest is gibberish.
		SHA newHash = new SHA();
		newHash.update(data);
		byte[] blob = newHash.digest();
		// verify that the signature is the same byte sequence
		// (this will usually be fast because the host key's e is small)
		byte[] verifyBlob = new BigInteger(signature).modPow(keyE, keyN).toByteArray();
		//DEBUG.p("blob: " + SSHMessage.makeBufferPrintable(blob));
		//DEBUG.p("verify: " + SSHMessage.makeBufferPrintable(verifyBlob, verifyBlob.length-20, 20));
		return ByteArray.startsWith(verifyBlob, verifyBlob.length-20, 20, blob);
	}

	private boolean verifyDSSSignature (byte[] host_key, byte[] signature_bytes, byte[] data)
		throws IOException
	{
		addLine(Terminal.instance().getString(STRING_VERIFYING_DSS));

		SSHMessage keyMsg = new SSHMessage(host_key);
		String keyType = keyMsg.getString();
		// pull out (p, q, g, y) from the host key
		BigInteger keyP = keyMsg.getBigInteger();
		BigInteger keyQ = keyMsg.getBigInteger();
		BigInteger keyG = keyMsg.getBigInteger();
		BigInteger keyY = keyMsg.getBigInteger();

		if ((keyType == null) || (keyP == null) || (keyQ == null) || (keyG == null) || (keyY == null)) {
			addLine(Terminal.instance().getString(STRING_CORRUPTED_HOST_KEY));
			throw new IOException();
		}
		if (! keyType.equals("ssh-dss")) {
			throw new IOException("Expected ssh-dss key, got " + keyType);
		}

		String sigName;
		byte[] signature;
		if (signature_bytes.length == 40) {
			// bug on spies.com: signature name is missing
			sigName = "ssh-dss";
			signature = signature_bytes;
		} else {
			// pull out the signature
			SSHMessage sigMsg = new SSHMessage(signature_bytes);
			sigName = sigMsg.getString();
			signature = sigMsg.getByteString();
		}

		if ((sigName == null) || (signature == null) || (signature.length != 40)) {
			addLine(Terminal.instance().getString(STRING_CORRUPTED_SIG));
			throw new IOException();
		}

		// more hacks to pull out (r, s) which are NOT encoded in BigInteger style...
		// instead, they're just rammed in, 20 bytes each.
		byte[] bR = new byte[21];
		bR[0] = 0;
		System.arraycopy(signature, 0, bR, 1, 20);
		byte[] bS = new byte[21];
		bS[0] = 0;
		System.arraycopy(signature, 20, bS, 1, 20);
		BigInteger sigR = new BigInteger(bR);
		BigInteger sigS = new BigInteger(bS);

		SHA newHash = new SHA();
		newHash.update(data);
		byte[] blob = newHash.digest();
		// make this into a number
		byte[] bSHA = new byte[21];
		bSHA[0] = 0;
		System.arraycopy(blob, 0, bSHA, 1, 20);
		BigInteger sigM = new BigInteger(bSHA);

		// DSS is a lot more involved than RSA :(
		BigInteger W = sigS.modInverse(keyQ);
		BigInteger U1 = sigM.multiply(W).mod(keyQ);
		BigInteger U2 = sigR.multiply(W).mod(keyQ);

		BigInteger V1 = keyG.modPow(U1, keyP);
		BigInteger V2 = keyY.modPow(U2, keyP);
		BigInteger V = V1.multiply(V2).mod(keyP).mod(keyQ);

		byte[] verifyBlob = V.toByteArray();
		return ByteArray.startsWith(bR, bR.length-verifyBlob.length, verifyBlob.length, verifyBlob);
	}

	private void parseKexReply (SSHMessage msg)
		throws IOException
	{
		byte[] host_key = msg.getByteString();
		mF = msg.getBigInteger();
		byte[] signature_bytes = msg.getByteString();

		if ((host_key == null) || (mF == null) || (signature_bytes == null)) {
			addLine(Terminal.instance().getString(STRING_INCOMPLETE_KEX));
			throw new IOException();
		}

		// now compute K = f^x mod P (server's f = g^y mod P  ->  K = (g^y)^x = g^(xy) mod P)
		addLine(Terminal.instance().getString(STRING_COMPUTING_K));
		mK = mF.modPow(mX, mP);
		DEBUG.p("Got K (whew)");

		// build up the hash H of (V_C || V_S || I_C || I_S || K_S || e || f || K)
		SSHMessage hashMsg = new SSHMessage();
		hashMsg.add(kBanner);
		hashMsg.add(mServerBanner);
		hashMsg.addByteString(mClientKexInit);
		hashMsg.addByteString(mServerKexInit);
		hashMsg.addByteString(host_key);
		hashMsg.add(mE);
		hashMsg.add(mF);
		hashMsg.add(mK);
		SHA hash = new SHA();
		hash.update(hashMsg.getBuffer());
		mH = hash.digest();
		//DEBUG.p("H: " + SSHMessage.makeBufferPrintable(mH));

		if (mUsingDSSKey) {
			if (! verifyDSSSignature(host_key, signature_bytes, mH)) {
				throw new IOException("Signature is bad medicine!  (bug Robey)");
			}
		} else {
			if (! verifyRSASignature(host_key, signature_bytes, mH)) {
				throw new IOException("Signature is bad medicine!  (bug Robey)");
			}
		}

		addLine(Terminal.instance().getString(STRING_KEX_COMPLETE));
		mHostKeyBlob = host_key;

		// tell the server to switch to these tasty new keys
		SSHMessage outMsg = new SSHMessage();
		outMsg.add(kMsg_NewKeys);
		sendMessage(outMsg);
	}

	private void parseNewKeys (SSHMessage msg)
		throws IOException
	{
		byte[] buffer;
		byte[] ivOut = new byte[8];
		byte[] ivIn = new byte[8];
		byte[] keyOut = new byte[16];
		byte[] keyIn = new byte[16];
		mMacKeyOut = new byte[mMacNaturalSize];
		mMacKeyIn = new byte[mMacNaturalSize];

		// generate the A - F keys
		buffer = generateKey((byte)'A');
		System.arraycopy(buffer, 0, ivOut, 0, 8);
		buffer = generateKey((byte)'B');
		System.arraycopy(buffer, 0, ivIn, 0, 8);
		buffer = generateKey((byte)'C');
		System.arraycopy(buffer, 0, keyOut, 0, 16);
		buffer = generateKey((byte)'D');
		System.arraycopy(buffer, 0, keyIn, 0, 16);
		buffer = generateKey((byte)'E');
		System.arraycopy(buffer, 0, mMacKeyOut, 0, mMacNaturalSize);
		buffer = generateKey((byte)'F');
		System.arraycopy(buffer, 0, mMacKeyIn, 0, mMacNaturalSize);

		if (false) {
			DEBUG.p("A ivOut : " + SSHMessage.makeBufferPrintable(ivOut));
			DEBUG.p("B ivIn  : " + SSHMessage.makeBufferPrintable(ivIn));
			DEBUG.p("C keyOut: " + SSHMessage.makeBufferPrintable(keyOut));
			DEBUG.p("D keyIn : " + SSHMessage.makeBufferPrintable(keyIn));
			DEBUG.p("E macOut: " + SSHMessage.makeBufferPrintable(mMacKeyOut));
			DEBUG.p("F macIn : " + SSHMessage.makeBufferPrintable(mMacKeyIn));
		}

		mCipherOut = Cipher.getInstance("blowfish");
		mCipherIn = Cipher.getInstance("blowfish");
		if ((mCipherOut == null) || (mCipherIn == null)) {
			throw new IOException("Can't load blowfish (?!)");
		}
		mCipherOut.init(keyOut, ivOut);
		mCipherIn.init(keyIn, ivIn);
		addLine(Terminal.instance().getString(STRING_BLOWFISH_ON));

		// all that CRAP we've got floating in memory?
		// explicitly unlink it 'cause we don't need it anymore.
		mServerBanner = null;
		mClientKexInit = mServerKexInit = null;
		mP = mX = mE = mF = mK = null;
		mH = null;

		// verify server host key, then auth
		if (! mLoggedIn) {
			waitForLoginInfo();
			if (! mDisconnected) {
				byte[] oldkey = loadHostKey();
				if (oldkey == null) {
					// don't have a saved host key for this host
					synchronized (this) {
						mHostKeyWindow = (HostKeyDialog) Terminal.instance().getDialog(ID_HOST_KEY_DIALOG, this);
						mHostKeyWindow.setTitleFormatText(mHostname);
						mHostKeyWindow.setHostKey(mUsingDSSKey ? "DSS" : "RSA", mHostKeyBlob);
						mHostKeyWindow.show();
					}
				} else if (! ByteArray.equals(oldkey, mHostKeyBlob)) {
					// this is bad: host key has changed
					synchronized (this) {
						mHostKeyWindow = (HostKeyDialog) Terminal.instance().getDialog(ID_CHANGED_HOST_KEY_DIALOG,
																					   this);
						// cancel is default
						mHostKeyWindow.setDefaultFrameButton(DialogWindow.TOP_POSITION1);
						mHostKeyWindow.setTitleFormatText(mHostname);
						mHostKeyWindow.setHostKey(mUsingDSSKey ? "DSS" : "RSA", mHostKeyBlob);
						mHostKeyWindow.show();
					}
				} else {
					// saved host key: thumbs up!
					DEBUG.p("host key looks good");
					sendLogin();
				}
			}
		}
	}

	private void sendLogin ()
		throws IOException
	{
		SSHMessage msg = new SSHMessage();
		msg.add(kMsg_ServiceRequest);
		msg.add("ssh-userauth");
		sendMessage(msg);

		// don't bother waiting for the service_accept
		msg = new SSHMessage();
		msg.add(kMsg_UserAuthRequest);
		msg.add(mLogin);
		msg.add("ssh-connection");
		msg.add("password");
		msg.add(false);
		msg.add(mPassword);
		sendMessage(msg);
	}

	// someday, i may want to abstract ssh "channels" into a separate class
	private void openSession ()
		throws IOException
	{
		SSHMessage msg = new SSHMessage();
		msg.add(kMsg_ChannelOpenRequest);
		msg.add("session");
		msg.add(kChannelID);
		msg.add(mInfo.getWindowSize());
		msg.add(kLargestPacketSize);
		sendMessage(msg);
	}

	private void getPTY ()
		throws IOException
	{
		SSHMessage msg = new SSHMessage();
		msg.add(kMsg_ChannelRequest);
		msg.add(mServerChannel);
		msg.add("pty-req");
		msg.add(false);
		msg.add(mTerminalType);
		msg.add(mColumns);
		msg.add(mRows);
		// pixels wide & high?
		msg.add(0);
		msg.add(0);
		msg.add("");
		sendMessage(msg);
	}

	private void resizeTerminal ()
		throws IOException
	{
		SSHMessage msg = new SSHMessage();
		msg.add(kMsg_ChannelRequest);
		msg.add(mServerChannel);
		msg.add("window-change");
		msg.add(false);
		msg.add(mColumns);
		msg.add(mRows);
		// pixels wide & high?
		msg.add(0);
		msg.add(0);
		sendMessage(msg);
	}

	private void invokeShell ()
		throws IOException
	{
		SSHMessage msg = new SSHMessage();
		msg.add(kMsg_ChannelRequest);
		msg.add(mServerChannel);
		msg.add("shell");
		msg.add(true);
		sendMessage(msg);
	}

	private void setEnv (String name, String value)
		throws IOException
	{
		SSHMessage msg = new SSHMessage();
		msg.add(kMsg_ChannelRequest);
		msg.add(mServerChannel);
		msg.add("env");
		msg.add(false);
		msg.add(name);
		msg.add(value);
		sendMessage(msg);
	}

	private void writeChannelData (byte[] in, int length)
		throws IOException
	{
		SSHMessage msg = new SSHMessage();
		msg.add(kMsg_ChannelData);
		msg.add(mServerChannel);
		msg.add(length);
		msg.add(in, 0, length);
		sendMessage(msg);
	}

	private void sendAddWindow (int length)
		throws IOException
	{
		SSHMessage msg = new SSHMessage();
		msg.add(kMsg_ChannelWindowAdjust);
		msg.add(mServerChannel);
		msg.add(length);
		sendMessage(msg);
	}

	private void doNegotiation ()
		throws IOException
	{
		DEBUG.p("banner");
		write(kBanner + "\r\n");
		DEBUG.p("negotiate");

		verifyServerBanner();
		addLine(Terminal.instance().getString(STRING_NEGOTIATING_ENCRYPTION));
		sendKexInit();

		SSHMessage msg;
		byte msgtype;
		do {
			msg = readMessage();
			msgtype = msg.getByte();
		} while (msgtype == kMsg_Ignore);
		if (msgtype != kMsg_KexInit) {
			throw new IOException("Expected kex-init, got " + msgtype);
		}
		// keep the user occupied while we do calculations
		popupLoginDialog();
		parseKexInit(msg);
		generateDH();

		do {
			msg = readMessage();
			msgtype = msg.getByte();
		} while (msgtype == kMsg_Ignore);
		if (msgtype != kMsg_KexDHReply) {
			throw new IOException("Expected kex-reply, got " + msgtype);
		}
		parseKexReply(msg);

		while (true) {
			msg = readMessage();
			msgtype = msg.getByte();
			switch (msgtype) {
			case kMsg_Disconnect:
				int code = msg.getInt();
				String desc = msg.getString();
				throw new IOException("Disconnected (" + code + "): " + desc);
			case kMsg_Ignore:
				String ignore = msg.getString();
				break;
			case kMsg_UserAuthFailure:
				// theoretically it could be a partial login, but we don't support that
				mFailedLogins++;
				mLogin = null;
				mPassword = null;
				if (mFailedLogins >= 3) {
					// give up
					addLine(Terminal.instance().getString(STRING_AUTH_FAILED));
					throw new IOException("Authentication failed.");
				}
				popupLoginDialog();
				waitForLoginInfo();
				if (! mDisconnected) {
					try {
						sendLogin();
					} catch (IOException x) { }
				}
				break;
			case kMsg_UserAuthSuccess:
				addLine(Terminal.instance().getString(STRING_AUTHENTICATED));
				mLoggedIn = true;
				mInfo.setSavedPassword(mPassword);
				mInfo.save();
				openSession();
				break;
			case kMsg_UserAuthBanner:
				String banner = msg.getString();
				if (mFirstBanner) {
					addLine("");
				}
				addLine(banner);
				mFirstBanner = false;
				break;
			case kMsg_ServiceAccept:
				String service = msg.getString();
				DEBUG.p("service " + service + " okay'd");
				break;
			case kMsg_NewKeys:
				parseNewKeys(msg);
				break;
			case kMsg_ChannelOpenSuccess:
				msg.getInt();	// chan #
				mServerChannel = msg.getInt();
				synchronized (this) {
					mServerWindowSize = msg.getInt();
					mServerMaxPacketSize = msg.getInt();
					mServerWindowBuffer = new byte[mServerMaxPacketSize];
					mServerWindowBufferSize = 0;
					mWindowTimer = new Timer(kWindowTimer, true, this);
					mWindowTimer.start();
				}
				DEBUG.p("server channel open: window size = " + mServerWindowSize +
						", max packet = " + mServerMaxPacketSize);
				getPTY();
				//setEnv("DASSH", "1");
				mInfo.setStatus(HostInfo.STATUS_OPEN);
				invokeShell();
				break;
			case kMsg_ChannelOpenFailure:
				msg.getInt();	// chan #
				int reasonCode = msg.getInt();
				String reason = msg.getString();
				String lang = msg.getString();
				throw new IOException("Channel open failed: " + decodeChannelReason(reasonCode) + " : " +
									  reason);
			case kMsg_ChannelWindowAdjust:
				msg.getInt();	// chan #
				int bytes = msg.getInt();
				synchronized (this) {
					mServerWindowSize += bytes;
				}
				checkWriteWindow();
				break;
			case kMsg_ChannelData:
				msg.getInt();	// chan #
				byte[] data = msg.getByteString();
				add(data);
				synchronized (this) {
					mWindowUsed += data.length;
					debugTraffic("in: " + data.length + " bytes; window now " + mWindowUsed + "/" + mInfo.getWindowSize());
				}
				break;
			case kMsg_ChannelEOF:
				msg.getInt();	// chan #
				DEBUG.p("channel EOF");
				break;
			case kMsg_ChannelClose:
				msg.getInt();	// chan #
				DEBUG.p("channel close");
				throw new IOException("Connection closed.");
			case kMsg_ChannelRequest:
				msg.getInt();	// chan #
				String name = msg.getString();
				if (name.equals("exit-status")) {
					int status = msg.getInt();
					DEBUG.p("exit status: " + status);
				} else {
					DEBUG.p("chan request (ignored): " + msg);
				}
			case kMsg_ChannelSuccess:
				msg.getInt();	// chan #
				DEBUG.p("channel request OK");
				break;
			case kMsg_ChannelFailure:
				msg.getInt();	// chan #
				DEBUG.p("channel request denied!");
				throw new IOException("Channel request denied");
			default:
				DEBUG.p("??? unhandled packet " + (int)msgtype);
				break;
			}
		}
	}

	public void run ()
	{
		// decode P (might take non-trivial time, sadly)
		mP = new BigInteger(kPString, 16);

		try {
			doNegotiation();
		} catch (IOException e) {
			DEBUG.p(e.toString());
			synchronized (this) {
				String msg = e.getMessage();
				if (msg != null) {
					add("\r\n\r\n+++ " + msg + "\r\n");
				}
				disconnect();
			}
		} catch (Throwable t) {
			DEBUG.p("freak-ass exception! " + t.toString());
			t.printStackTrace();
		}
	}


	static public void
	debugTraffic (String out)
	{
		if (mDebugging) {
			System.err.println("%%% " + out);
		}
	}

	private static final boolean mDebugging = false;

	private SSHHostInfo mInfo;
	
	private InputStream fRawInput;
	private OutputStream fRawOutput;

	// Current configuration
	private String mTerminalType;
	private String mHostname;
	private int mColumns;
	private int mRows;

	/* incoming data buffer (from SSH stream, read by SSHInputStream below).
	 * wait/notify on this to indicate new data or EOF.
	 * tail -> first empty (ready) byte
	 */
	private byte[] mOutputBuffer;
	private int mOutputBufferSize;
	private int mOutputBufferTail;

	// returned from the login dialog
	private String mLogin;
	private String mPassword;
	private boolean mLoggedIn;

	private boolean mDisconnected = false;
	private boolean mFirstBanner = true;
	private int mFailedLogins = 0;
	private int mServerChannel;
	private int mServerWindowSize = 0;
	private int mServerMaxPacketSize = 0;
	// wait/notify on this to indicate change in buffer status:
	private byte[] mServerWindowBuffer = null;
	private int mServerWindowBufferSize = 0;

	// state needed for computing keys
	private String mServerBanner = null;
	private byte[] mClientKexInit = null;
	private byte[] mServerKexInit = null;
	private BigInteger mP;
	private BigInteger mX;
	private BigInteger mE, mF, mK;
	private byte[] mH;

	private DialogWindow mLoginWindow;
	private HostKeyDialog mHostKeyWindow;
	private byte[] mMacKeyIn, mMacKeyOut;
	private Cipher mCipherIn, mCipherOut;
	private int mInSequence = 0;
	private int mOutSequence = 0;
	private Timer mWindowTimer = null;
	private int mWindowUsed = 0;			// how much of our window has the server used
	private boolean mUsingDSSKey = false;
	private boolean mUsingMD5HMAC = false;
	private int mMacSize, mMacNaturalSize;
	private byte[] mHostKeyBlob;

	// constants that you shouldn't change
	private static final String kBanner = "SSH-2.0-TerminalMonkey_1.1";
	private static final int kBlockSize = 8;
	private static final BigInteger kG = new BigInteger(2);
	private static final int kCmd_HostKeyOkay = 142;
	private static final int kCmd_HostKeyBad = 143;
	private static final int kCmd_HostKeySave = 144;

	// constants that you may want to change
	private static final int kChannelID = 1;			// meaningless number
	private static final int kLargestPacketSize = 2048;	// largest packet we wish to receive at once
	private static final int kMaxPacketSize = 35000;	// largest packet we'll ever allow at lower levels
	private static final int kWindowTimer = 500;		// msec between checking the window
	private static final int kWindowThreshold = 512;	// threshold above which we extend the window

	// ssh codes
	private static final byte kMsg_Disconnect = 1;
	private static final byte kMsg_Ignore = 2;
	private static final byte kMsg_ServiceRequest = 5;
	private static final byte kMsg_ServiceAccept = 6;
    private static final byte kMsg_KexInit = 20;
	private static final byte kMsg_NewKeys = 21;
	private static final byte kMsg_KexDHInit = 30;
	private static final byte kMsg_KexDHReply = 31;
	private static final byte kMsg_UserAuthRequest = 50;
	private static final byte kMsg_UserAuthFailure = 51;
	private static final byte kMsg_UserAuthSuccess = 52;
	private static final byte kMsg_UserAuthBanner = 53;
	private static final byte kMsg_ChannelOpenRequest = 90;
	private static final byte kMsg_ChannelOpenSuccess = 91;
	private static final byte kMsg_ChannelOpenFailure = 92;
	private static final byte kMsg_ChannelWindowAdjust = 93;
	private static final byte kMsg_ChannelData = 94;
	private static final byte kMsg_ChannelEOF = 96;
	private static final byte kMsg_ChannelClose = 97;
	private static final byte kMsg_ChannelRequest = 98;
	private static final byte kMsg_ChannelSuccess = 99;
	private static final byte kMsg_ChannelFailure = 100;

	// P
	private static String kPString =
		"FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD129024E08" +
		"8A67CC74020BBEA63B139B22514A08798E3404DDEF9519B3CD3A431B" +
		"302B0A6DF25F14374FE1356D6D51C245E485B576625E7EC6F44C42E9" +
		"A637ED6B0BFF5CB6F406B7EDEE386BFB5A899FA5AE9F24117C4B1FE6" +
		"49286651ECE65381FFFFFFFFFFFFFFFF";


	class SSHInputStream extends InputStream
	{
		public int read()
			throws IOException
		{
			byte[] b = new byte[1];
			read(b);
			return b[0];
		}

		public int
		read (byte[] buffer)
			throws IOException
		{
			synchronized (mOutputBuffer) {
				while (mOutputBufferTail == 0) {
					if (mDisconnected) {
						/* do this after the above output buffer check,
						 * so the console will read anything buffered first.
						 */
						throw new IOException("Disconnected");
					}
					try {
						mOutputBuffer.wait();
					} catch (InterruptedException e) {
						// pass
					}
				}

				int max = buffer.length;
				if (max > mOutputBufferTail) {
					max = mOutputBufferTail;
				}
				System.arraycopy(mOutputBuffer, 0, buffer, 0, max);
				if (max < mOutputBufferTail) {
					System.arraycopy(mOutputBuffer, max, mOutputBuffer, 0, mOutputBufferTail - max);
				}
				mOutputBufferTail -= max;
				mOutputBuffer.notify();
				return max;
			}
		}
	}

	class SSHOutputStream extends OutputStream
	{
		public void write(int value)
			throws IOException
		{
			byte[] ba = new byte[1];
			ba[0] = (byte)value;
			writeWindow(ba, 0, 1);
		}

		public void write(byte[] stuff, int offset, int length)
			throws IOException
		{
			writeWindow(stuff, offset, length);
		}

		public void write(byte[] stuff)
			throws IOException
		{
			writeWindow(stuff, 0, stuff.length);
		}
	}

}




/*

format for authorized_keys

$ echo "AAAAB3NzaC1yc2EAAAABIwAAAIEAxMmRrAuH/UmKdbW+/0RAF2KSVv9a31L+kxdmA0Cb7uis3YQ8Bv3FmewaXa2vgUgnR1FZ3qAEFuSDmAPZD8cyytzscJFz2a5OMwJMLOVwrahmvsxJIqybESxYyZuDYXIC4tU4LoUeFT3RLfIFk9qEhcqF5TMh0+X713il4Dk9Ff8=" | mimencode -u | od -t x1
0000000 00 00 00 07 73 73 68 2d 72 73 61 00 00 00 01 23
0000020 00 00 00 81 00 c4 c9 91 ac 0b 87 fd 49 8a 75 b5
0000040 be ff 44 40 17 62 92 56 ff 5a df 52 fe 93 17 66
0000060 03 40 9b ee e8 ac dd 84 3c 06 fd c5 99 ec 1a 5d
0000100 ad af 81 48 27 47 51 59 de a0 04 16 e4 83 98 03
0000120 d9 0f c7 32 ca dc ec 70 91 73 d9 ae 4e 33 02 4c
0000140 2c e5 70 ad a8 66 be cc 49 22 ac 9b 11 2c 58 c9
0000160 9b 83 61 72 02 e2 d5 38 2e 85 1e 15 3d d1 2d f2
0000200 05 93 da 84 85 ca 85 e5 33 21 d3 e5 fb d7 78 a5
0000220 e0 39 3d 15 ff
0000225

*    (7) ssh-rsa (1) 0x23 = e (129) [129 bytes] = n
*    4 + 7 + 4 + 1 + 4 + 129 = total 149 bytes
*/
