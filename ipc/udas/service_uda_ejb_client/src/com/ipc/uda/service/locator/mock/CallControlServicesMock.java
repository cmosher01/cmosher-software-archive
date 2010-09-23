/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.locator.mock;

import javax.mail.Address;

import com.ipc.va.cti.ConnectionID;
import com.ipc.va.cti.CtiException;
import com.ipc.va.cti.DeviceID;
import com.ipc.va.cti.EventCause;
import com.ipc.va.cti.UserData;
import com.ipc.va.cti.callControl.AudioDeviceChannel;
import com.ipc.va.cti.callControl.AudioDeviceType;
import com.ipc.va.cti.callControl.ConferenceActionType;
import com.ipc.va.cti.callControl.TelephonyTone;
import com.ipc.va.cti.callControl.services.CallControlServices;
import com.ipc.va.cti.callControl.services.extensions.AcceptCallExtensions;
import com.ipc.va.cti.callControl.services.extensions.AnswerCallExtensions;
import com.ipc.va.cti.callControl.services.extensions.CancelTelephonyTonesExtensions;
import com.ipc.va.cti.callControl.services.extensions.ClearCallExtensions;
import com.ipc.va.cti.callControl.services.extensions.ClearConnectionExtensions;
import com.ipc.va.cti.callControl.services.extensions.ConferenceCallExtensions;
import com.ipc.va.cti.callControl.services.extensions.GenerateDigitsExtensions;
import com.ipc.va.cti.callControl.services.extensions.GenerateTelephonyTonesExtensions;
import com.ipc.va.cti.callControl.services.extensions.HoldCallExtensions;
import com.ipc.va.cti.callControl.services.extensions.MakeCallExtensions;
import com.ipc.va.cti.callControl.services.extensions.RetrieveCallExtensions;
import com.ipc.va.cti.callControl.services.extensions.TransferCallExtensions;
import com.ipc.va.cti.callControl.services.results.AcceptCallResult;
import com.ipc.va.cti.callControl.services.results.AnswerCallResult;
import com.ipc.va.cti.callControl.services.results.CancelTelephonyTonesResult;
import com.ipc.va.cti.callControl.services.results.ClearCallResult;
import com.ipc.va.cti.callControl.services.results.ClearConnectionResult;
import com.ipc.va.cti.callControl.services.results.ConferenceCallResult;
import com.ipc.va.cti.callControl.services.results.ForceLineClearResult;
import com.ipc.va.cti.callControl.services.results.GenerateDigitsResult;
import com.ipc.va.cti.callControl.services.results.GenerateHookFlashResult;
import com.ipc.va.cti.callControl.services.results.GenerateTelephonyTonesResult;
import com.ipc.va.cti.callControl.services.results.HoldCallResult;
import com.ipc.va.cti.callControl.services.results.MakeCallResult;
import com.ipc.va.cti.callControl.services.results.ReleaseResourceResult;
import com.ipc.va.cti.callControl.services.results.RetrieveCallResult;
import com.ipc.va.cti.callControl.services.results.SeizeResourceResult;
import com.ipc.va.cti.callControl.services.results.SendUserInformationResult;
import com.ipc.va.cti.callControl.services.results.SetCallMutingResult;
import com.ipc.va.cti.callControl.services.results.SetCallPrivacyResult;
import com.ipc.va.cti.callControl.services.results.SetCallRecordingResult;
import com.ipc.va.cti.callControl.services.results.SetCallSignalingResult;
import com.ipc.va.cti.callControl.services.results.SetCallTalkbackResult;
import com.ipc.va.cti.callControl.services.results.TransferCallResult;

/**
 * @author mordarsd
 *
 */
public class CallControlServicesMock implements CallControlServices
{

	@Override
	public AcceptCallResult acceptCall(ConnectionID callToBeAccepted,
			UserData userData, AcceptCallExtensions extensions)
			throws Exception, CtiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AnswerCallResult answerCall(ConnectionID callToBeAnswered,
			UserData userData, AnswerCallExtensions extensions)
			throws Exception, CtiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CancelTelephonyTonesResult cancelTelephonyTones(
			CancelTelephonyTonesExtensions extensions)
			throws Exception, CtiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ClearCallResult clearCall(ConnectionID callToBeCleared,
			UserData userData, EventCause reason, ClearCallExtensions extensions)
			throws Exception, CtiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ClearConnectionResult clearConnection(
			ConnectionID connectionToBeCleared, UserData userData,
			EventCause reason, ClearConnectionExtensions extensions)
			throws Exception, CtiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConferenceCallResult conferenceCall(ConnectionID originalCall,
			ConferenceCallExtensions extensions) throws Exception, CtiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ForceLineClearResult forceLineClear(DeviceID lineToBeCleared)
			throws Exception, CtiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GenerateDigitsResult generateDigits(ConnectionID connectionID,
			String charactersToSend, int toneDuration,
			GenerateDigitsExtensions extensions) throws Exception, CtiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GenerateTelephonyTonesResult generateTelephonyTones(
			ConnectionID connectionID, TelephonyTone toneToSend,
			int toneDuration, GenerateTelephonyTonesExtensions extensions)
			throws Exception, CtiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HoldCallResult holdCall(ConnectionID callToBeHeld,
			HoldCallExtensions extensions) throws Exception, CtiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MakeCallResult makeCall(DeviceID callingDevice,
			DeviceID calledDevice, UserData userData, EventCause reason,
			MakeCallExtensions extensions) throws Exception, CtiException {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public ReleaseResourceResult releaseResource(String userAOR, String resourceAOR,
            int appearance, AudioDeviceType audioDeviceType, AudioDeviceChannel audioDeviceChannel)
            throws Exception, CtiException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SeizeResourceResult seizeResource(String userAOR, String resourceAOR, int appearance,
            boolean isCallPrivate, AudioDeviceType audioDeviceType,
            AudioDeviceChannel audioDeviceChannel) throws Exception, CtiException
    {
        // TODO Auto-generated method stub
        return null;
    }

	@Override
	public SendUserInformationResult sendUserInformation(
			ConnectionID connection, UserData userData) throws Exception,
			CtiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SetCallMutingResult setCallMuting(ConnectionID connectionID,
			boolean isCallMuted) throws Exception, CtiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SetCallPrivacyResult setCallPrivacy(ConnectionID connectionID,
			boolean isCallPrivate) throws Exception, CtiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SetCallRecordingResult setCallRecording(ConnectionID connectionID,
			boolean isCallRecorded) throws Exception, CtiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SetCallSignalingResult setCallSignaling(ConnectionID connectionID,
			boolean isCallSignaled) throws Exception, CtiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SetCallTalkbackResult setCallTalkback(ConnectionID connectionID,
			boolean isCallTalkbackEnabled) throws Exception, CtiException {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public TransferCallResult transferCall(ConnectionID connectionID,
            TransferCallExtensions extension) throws Exception, CtiException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public GenerateHookFlashResult generateHookFlash(ConnectionID connectionID) throws Exception,
            CtiException
    {
        // TODO Auto-generated method stub
        return null;
    }
}
