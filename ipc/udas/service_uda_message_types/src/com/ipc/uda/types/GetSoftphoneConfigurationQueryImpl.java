/* Copyright (c) 2009 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;



import java.util.List;

import com.ipc.ds.base.security.SecurityContext;
import com.ipc.ds.entity.dto.Codec;
import com.ipc.ds.entity.dto.Device;
import com.ipc.ds.entity.dto.DeviceUDA;
import com.ipc.ds.entity.dto.User;
import com.ipc.ds.entity.dto.Zone;
import com.ipc.ds.entity.manager.DeviceManager;
import com.ipc.ds.entity.manager.DeviceUDAManager;
import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.types.util.UDAAndDSEntityUtil;



/**
 * This class is responsible for getting the softphone configuration from DB
 * 
 * @author Bhavya Bhat
 * 
 */
// REVIEW 5903 Fix Formatting ; Break up the execute() method. its doing so many things.--done
public class GetSoftphoneConfigurationQueryImpl extends GetSoftphoneConfigurationQuery implements ExecutableWithContext
{
    private UserContext ctx;

    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {
        SoftphoneConfigurationResultType softphoneRes = new SoftphoneConfigurationResultType();

        try
        {
            final SecurityContext basicSecContext = this.ctx.getSecurityContext();

            final User user = UDAAndDSEntityUtil.getUser(this.ctx);

            // REVIEW 5903 Zone need to be final -- done
            //final Zone zone = UDAAndDSEntityUtil.GetZoneForTheUser(this.ctx);
            // TODO : Need to change the Logic to get the device associated with
            // the User
            // REVIEW 5903 Remove the TODO if the implementation in place.
            final DeviceManager dsDevMgr = new DeviceManager(basicSecContext);
           /* final List<Device> lstDevices = dsDevMgr.getDevicesFor(zone);
            if (lstDevices == null || lstDevices.size() == 0)
            {
                throw new ExecutionException("lstDevices from DS is NULL or of Size Zero for the given user" + getUserName());
            }
            final Device dev = lstDevices.get(0);*/

            final DeviceUDAManager dsDevUDAMgr = new DeviceUDAManager(basicSecContext);

			String deviceID = this.ctx.getCurrentDeviceID();
			Device dev = dsDevMgr.findByDeviceUUIDEqualing(deviceID);
            final DeviceUDA deviceuda = dsDevUDAMgr.getDeviceUDAFor(dev);

            // REVIEW Codec information is passed from the MediaServer via the SOA model
            // REVIEW The following method was no longer needed, and also did nothing because the
            //        softphoneRes was being reassinged on the very next line..
            //
            // updateSoftphoneResultwithCodec(dev,ctx,softphoneRes);
            //
            softphoneRes = updateSoftphoneResultFromDS(deviceuda);

        }
        catch (final ExecutionException e)
        {
            throw e;
        }
        catch (final Throwable e)
        {
            throw new ExecutionException(e);
        }

        if (softphoneRes != null)
        {
            final QueryResult qr = new QueryResult();
            qr.setSoftphoneConfigurationResult(softphoneRes);
            return new Optional<ExecutionResult>(qr);
        }

        return new Nothing<ExecutionResult>();
    }

    @Override
    public void setUserContext(final UserContext ctx)
    {
        this.ctx = ctx;
    }

    /**
     * Get getCodecTypefromDSCodec
     * 
     * @input Codec (DS)
     * @return CodecType (UDA Type)
     */
    private CodecType getCodecTypefromDSCodec(final Codec codec, final CodecType codecType)
    {
        Log.logger().debug("CodecType getCodecTypefromDSCodec(Codec codec)");
        codecType.setType(codec.getCodecType().name());
        codecType.setPacketPeriod(codec.getPacketPeriod());
        codecType.setVAD(codec.getVAD());
        return codecType;
    }

    /**
     * Get User Name of the context
     * 
     * @return String
     */
    private String getUserName()
    {
        return this.ctx.getUser().getName();
    }

    private SoftphoneConfigurationResultType updateSoftphoneResultFromDS(final DeviceUDA devUDA)
    {
        final SoftphoneConfigurationResultType softphoneRes = new SoftphoneConfigurationResultType();
        softphoneRes.setAudioAGC(devUDA.getAudioAGC());
        softphoneRes.setAudioEC(devUDA.getAudioEC());
        softphoneRes.setAudioMicBoost(devUDA.getAudioMicrophoneBoost());
        softphoneRes.setAudioMicVolume(devUDA.getAudioMicrophoneVolume());
        softphoneRes.setAudioNS(devUDA.getAudioNS());
        softphoneRes.setAudioNetEQ(devUDA.getAudioNetEQ());
        softphoneRes.setAudioNetEQBGN(devUDA.getAudioNetEQBGN());
        softphoneRes.setAudioRTCPXR(devUDA.getAudioRTCPXR());
        softphoneRes.setAudioSpeakerVolume(devUDA.getAudioSpeakerVolume());
        softphoneRes.setAudioStereoPlayout(devUDA.getAudioStereoPlayout());
        softphoneRes.setAudioVAD(devUDA.getAudioVAD());
        softphoneRes.setAudioVQE(devUDA.getAudioVQE());
        softphoneRes.setRTCP(devUDA.getRTCP());

        return softphoneRes;
    }

}
