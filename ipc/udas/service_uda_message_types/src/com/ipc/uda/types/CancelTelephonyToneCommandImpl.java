package com.ipc.uda.types;

import com.ipc.ds.entity.dto.Button;
import com.ipc.ds.entity.dto.UserCDI;
import com.ipc.ds.entity.internal.manager.base.ButtonBaseManager;
import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.locator.ServiceLocatorException;
import com.ipc.uda.service.locator.ServiceLocatorProvider;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.types.util.DataServicesSubscriptionHelper;
import com.ipc.uda.types.util.EntityHelper;
import com.ipc.uda.types.util.UDAAndDSEntityUtil;
import com.ipc.va.cti.CtiException;
import com.ipc.va.cti.callControl.AudioDeviceChannelImpl;
import com.ipc.va.cti.callControl.AudioDeviceType;
import com.ipc.va.cti.callControl.HandsetSide;
import com.ipc.va.cti.callControl.services.extensions.CancelTelephonyTonesExtensionsImpl;

public class CancelTelephonyToneCommandImpl extends CancelTelephonyToneCommand implements ExecutableWithContext
{

    private UserContext ctx;

    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {
        final AudioDeviceChannelImpl channel = new AudioDeviceChannelImpl();
        // TODO hard-coding LEFT HANDSET for now
        channel.setHandsetSide(HandsetSide.LEFT);
        final CancelTelephonyTonesExtensionsImpl extensions = new CancelTelephonyTonesExtensionsImpl();
        extensions.setAudioDeviceChannel(channel);
        extensions.setAudioDeviceType(AudioDeviceType.HANDSET);
        extensions.setUserAOR(this.ctx.getUser().getName());
        try
        {
            ServiceLocatorProvider.getCtiServiceLocator().getCallControlService().cancelTelephonyTones(extensions);
        }
        catch (final Throwable e)
        {
            throw new ExecutionException(e);
        }
        return new Nothing<ExecutionResult>();
    }

    @Override
    public void setUserContext(final UserContext ctx)
    {
        this.ctx = ctx;
    }
}
