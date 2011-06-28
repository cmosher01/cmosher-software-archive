/*
 *	Oscillator.java
 *
 *	This file is part of jsresources.org
 */

/*
 * Copyright (c) 1999 - 2001 by Matthias Pfisterer
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
 |<---            this code is formatted to fit into 80 columns             --->|
 */

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;

public class Oscillator extends AudioInputStream {
	private byte[] m_abData;
	private int m_nBufferPosition;
	private long m_lRemainingFrames;

	public Oscillator(float fSignalFrequency, float fAmplitude, AudioFormat audioFormat, long lLength) {
		super(new ByteArrayInputStream(new byte[0]), new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, audioFormat.getSampleRate(), 16, 2, 4, audioFormat.getFrameRate(), audioFormat.isBigEndian()), lLength);
		m_lRemainingFrames = lLength;
		fAmplitude = (float) (fAmplitude * Math.pow(2, getFormat().getSampleSizeInBits() - 1));
		// length of one period in frames
		int nPeriodLengthInFrames = Math.round(getFormat().getFrameRate() / fSignalFrequency);
		int nBufferLength = nPeriodLengthInFrames * getFormat().getFrameSize();
		m_abData = new byte[nBufferLength];
		for (int nFrame = 0; nFrame < nPeriodLengthInFrames; nFrame++) {
			// relative position inside the period of the waveform. 0.0 = beginning, 1.0 = end
			final float fPeriodPosition = (float) nFrame / (float) nPeriodLengthInFrames;
			final float fValue = (float) Math.sin(fPeriodPosition * 2.0 * Math.PI);
			final int nValue = Math.round(fValue * fAmplitude);
			final int nBaseAddr = (nFrame) * getFormat().getFrameSize();
			// this is for 16 bit stereo, little endian
			m_abData[nBaseAddr + 0] = m_abData[nBaseAddr + 2] = (byte) (nValue & 0xFF);
			m_abData[nBaseAddr + 1] = m_abData[nBaseAddr + 3] = (byte) ((nValue >>> 8) & 0xFF);
		}
	}

	/**
	 * Returns the number of bytes that can be read without blocking. Since
	 * there is no blocking possible here, we simply try to return the number of
	 * bytes available at all. In case the length of the stream is indefinite,
	 * we return the highest number that can be represented in an integer. If
	 * the length if finite, this length is returned, clipped by the maximum
	 * that can be represented.
	 */
	public int available() {
		int nAvailable = 0;
		if (m_lRemainingFrames == AudioSystem.NOT_SPECIFIED) {
			nAvailable = Integer.MAX_VALUE;
		} else {
			final long lBytesAvailable = m_lRemainingFrames * getFormat().getFrameSize();
			nAvailable = (int) Math.min(lBytesAvailable, (long) Integer.MAX_VALUE);
		}
		return nAvailable;
	}

	/*
	 * this method should throw an IOException if the frame size is not 1. Since
	 * we currently always use 16 bit samples, the frame size is always greater
	 * than 1. So we always throw an exception.
	 */
	public int read() throws IOException {
		throw new IOException("cannot use this method currently");
	}

	public int read(byte[] abData, int nOffset, int nLength) throws IOException {
		if (nLength % getFormat().getFrameSize() != 0) {
			throw new IOException("length must be an integer multiple of frame size");
		}
		int nConstrainedLength = Math.min(available(), nLength);
		int nRemainingLength = nConstrainedLength;
		while (nRemainingLength > 0) {
			int nNumBytesToCopyNow = m_abData.length - m_nBufferPosition;
			nNumBytesToCopyNow = Math.min(nNumBytesToCopyNow, nRemainingLength);
			System.arraycopy(m_abData, m_nBufferPosition, abData, nOffset, nNumBytesToCopyNow);
			nRemainingLength -= nNumBytesToCopyNow;
			nOffset += nNumBytesToCopyNow;
			m_nBufferPosition = (m_nBufferPosition + nNumBytesToCopyNow) % m_abData.length;
		}
		int nFramesRead = nConstrainedLength / getFormat().getFrameSize();
		if (m_lRemainingFrames != AudioSystem.NOT_SPECIFIED) {
			m_lRemainingFrames -= nFramesRead;
		}
		int nReturn = nConstrainedLength;
		if (m_lRemainingFrames == 0) {
			nReturn = -1;
		}
		return nReturn;
	}
}
