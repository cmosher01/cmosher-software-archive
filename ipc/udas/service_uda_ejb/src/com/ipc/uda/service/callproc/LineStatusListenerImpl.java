/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.callproc;


import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.context.UserContextManager;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.types.ButtonAppearance;
import com.ipc.uda.types.SystemLineStatus;
import com.ipc.va.cti.lineStatus.events.LineStatusEvent;
import com.ipc.va.cti.lineStatus.events.LineStatusListener;
import com.ipc.va.cti.logicalDevice.AgentID;
import com.ipc.va.cti.monitoring.MonitorCrossRefID;
import com.ipc.va.dialog.Dialog;
import com.ipc.va.dialog.DialogInfo;

/**
 * @author mordarsd
 * @author mosherc
 * @author sharmar
 */
public class LineStatusListenerImpl implements LineStatusListener
{

    private static final LineStatusListenerImpl instance = new LineStatusListenerImpl();
    
    private LineStatusListenerImpl()
    {
        // this is a singleton
    }

    public static LineStatusListenerImpl getInstance()
    {
        return LineStatusListenerImpl.instance;
    }
    

    /**
     * Handles an update of a telephone line's status.
     * CTI will send this event to us when the status of a line has changed.
     * This method will decode the message to determine the new status
     * (IDLE, BUSY, INCOMING, or HOLD), and determine which UDAC the event
     * is for. Then it gets the corresponding button from that user's button
     * sheet, and sends the line-status event to that button.
     */
    @Override
    public void lineStatusUpdate(LineStatusEvent event)
    {
        if (event == null)
        {
            Log.logger().info("Received system line status event with no event information.");
            return;
        }

        // Get dialog, dialog-info, agent-ID, and x-ref ID from the event.
        final Dialog dialog = event.getDialog();
        final DialogInfo dialogInfo = event.getDialogInfo();
        final AgentID agentID = event.getAgentID();
        final MonitorCrossRefID xRef = event.getMonitorCrossRefID();

        LineStatusListenerImpl.handleEvent(dialog,dialogInfo,agentID,xRef);
    }

    private static void handleEvent(final Dialog dialog, final DialogInfo dialogInfo, 
                                    final AgentID agentID, final MonitorCrossRefID xrefID)
    {
        /*
         * Build our ButtonAppearance object from the AOR and
         * appearance found in the dialog and dialog-info.
         */
        final Optional<ButtonAppearance> optAppearance = getAppearance(dialog,dialogInfo);
        if (!optAppearance.exists())
        {
            return;
        }

    	// get the line-status enum corresponding to the status found in the dialog
        final Optional<SystemLineStatus> status = SystemLineStatus.fromDialog(dialog);

        // get this user's context
        if (agentID == null)
        {
            Log.logger().info("Received system line status event with no AgentID.");
            return;
        }

        final Optional<UserContext> optCtx = UserContextManager.getInstance().getUserContext(xrefID);
        if (!optCtx.exists())
        {
            Log.logger().info("Received system line status event for unknown user: " + agentID.getAgentID());
            return;
        }

        // get the button from the user's button sheet (based on the ButtonAppearance)
        final Set<ButtonPressCall> calls = optCtx.get().getCallContext().getButtonSheet().getCalls(optAppearance.get());

        final String cliName = getCliName(dialog);
        final String cliNumber = getCliNumber(dialog);
        
        // send the corresponding line-status event to the button(s)
        for (final ButtonPressCall call : calls)
        {
        	sendLineStatusToCall(status,call,cliName,cliNumber);
        }
        
        // get the speaker from the user's speaker sheet 
        final Set<SpeakerCall> speakerCalls = optCtx.get().getCallContext().getSpeakerSheet().getCalls(optAppearance.get());
        
     // send the corresponding line-status event to the speaker(s)
        for(final SpeakerCall call : speakerCalls)
        {
            sendLineStatusToSpeaker(status,call,cliName,cliNumber);
        }  
    }

    private static void sendLineStatusToSpeaker(final Optional<SystemLineStatus> status, final SpeakerCall call,final String cliName, final String cliNumber)
    {
        if (!status.exists())
        {
            return;
        }

        switch (status.get())
        {
            case IDLE:
                call.lineIdle();
            break;
            case BUSY:
                call.lineBusy();
            break;
            case INCOMING:
                call.lineRing();
                call.lineCli(cliName,cliNumber);
            break;
            case HOLD:
                call.lineHold();
            break;
        }
    }

    private static String getCliName(final Dialog dialog)
    {
        try
        {
            return dialog.getRemote().getIdentity().getDisplayName();
        }
        catch(final Throwable e)
        {
            return "";
        }
    }

    private static String getCliNumber(final Dialog dialog)
    {
        try
        {
            return dialog.getRemote().getIdentity().getValue();
        }
        catch(final Throwable e)
        {
            return "";
        }
    }

    private static void sendLineStatusToCall(final Optional<SystemLineStatus> status, final Call call, final String cliName, final String cliNumber)
    {
        if (!status.exists())
        {
            return;
        }

        switch (status.get())
    	{
    	    case IDLE:
    	        call.lineIdle();
    	    break;
            case BUSY:
                call.lineBusy();
            break;
            case INCOMING:
                call.lineRing();
                call.lineCli(cliName,cliNumber);
            break;
            case HOLD:
                call.lineHold();
            break;
    	}
    }

    private static Optional<ButtonAppearance> getAppearance(final Dialog dialog, final DialogInfo dialogInfo)
    {
    	try
        {
            final String aor = parseCtiAor(dialogInfo.getEntity());
            final int appearance = dialog.getAppearance().intValue();
            return new Optional<ButtonAppearance>(new ButtonAppearance(aor,appearance));
        }
        catch (final Throwable e)
        {
            Log.logger().info("Received system line status event with invalid AOR/appearance",e);
            return new Nothing<ButtonAppearance>();
        }
    }

    /**
     * @param entity
     * @return
     */
    static String parseCtiAor(final String ctiAor)
    {
        
        Pattern pat = Pattern.compile("(?:.*:)?(.*?)(?:@.*?)?");
        Matcher matcher = pat.matcher(ctiAor);
        if (!matcher.matches())
        {
            return "";
        }
        return matcher.group(1);
    }
}
