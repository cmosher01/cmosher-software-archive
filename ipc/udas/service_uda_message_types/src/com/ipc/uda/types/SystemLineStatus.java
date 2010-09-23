package com.ipc.uda.types;

import java.util.List;

import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;
import com.ipc.va.dialog.Dialog;
import com.ipc.va.dialog.Participant;
import com.ipc.va.dialog.State;

/**
 * Represents the system status of a telephone line.
 * @author mosherc
 */
public enum SystemLineStatus
{
    IDLE,
    HOLD,
    INCOMING,
    BUSY;

    /**
     * Determines the line status from the given CTI {@link Dialog}.
     * @param dialog {@link Dialog} to parse to get the line status
     * @return the line status represented in <code>dialog</code>; or
     * {@link Nothing} if the status cannot be determined
     */
    public static Optional<SystemLineStatus> fromDialog(final Dialog dialog)
    {
        if (dialog == null)
        {
            return new Nothing<SystemLineStatus>();
        }

        final State dialogStateParam = dialog.getState();
        if (dialogStateParam == null)
        {
            return new Nothing<SystemLineStatus>();
        }

        final String dialogState = dialogStateParam.getValue();
        if (dialogState == null || dialogState.isEmpty())
        {
            return new Nothing<SystemLineStatus>();
        }



        if (dialogState.equalsIgnoreCase("terminated"))
        {
            return new Optional<SystemLineStatus>(IDLE);
        }

        if (dialogState.equalsIgnoreCase("trying"))
        {
            return new Optional<SystemLineStatus>(BUSY);
        }

        if (dialogState.equalsIgnoreCase("early") && isRecipient(dialog))
        {
            return new Optional<SystemLineStatus>(INCOMING);
        }

        if (dialogState.equalsIgnoreCase("confirmed"))
        {
            if (isHold(dialog))
            {
                return new Optional<SystemLineStatus>(HOLD);
            }
            return new Optional<SystemLineStatus>(BUSY);
        }

        return new Nothing<SystemLineStatus>();
    }

    private static boolean isRecipient(final Dialog dialog)
    {
        final String direction = dialog.getDirection();
        if (direction == null)
        {
            return false;
        }
        return direction.trim().toLowerCase().equals("recipient");
    }

    /**
     * Checks if the dialog has local rendering turned off.
     * @param dialog {@link Dialog} to parse to get the rendering status
     * @return <code>true</code> if the given dialog has rendering off
     */
    private static boolean isHold(final Dialog dialog)
    {
        final Participant local = dialog.getLocal();
        if (local == null)
        {
            return false;
        }
        final Participant.Target target = local.getTarget();
        if (target == null)
        {
            return false;
        }

        final List<Participant.Target.Param> params = target.getParam();
        if (params == null)
        {
            return false;
        }

        for (final Participant.Target.Param param : params)
        {
            if (param != null)
            {
                if (isRenderingParam(param))
                {
                    return param.getPval().equalsIgnoreCase("no");
                }
            }
        }

        return false;
    }

    /**
     * Determines if the given {@link Participant.Target.Param}
     * is for "rendering," such as "<code>+sip.rendering</code>".
     * @param param {@link Participant.Target.Param} to check
     * @return <code>true</code> if <code>param</code> is for rendering
     */
    private static boolean isRenderingParam(final Participant.Target.Param param)
    {
        final String pname = param.getPname();
        if (pname == null)
        {
            return false;
        }

        final String fixedPName = pname.toLowerCase().trim();
        return fixedPName.matches("[^a-z]*sip\\.rendering");
    }
}
