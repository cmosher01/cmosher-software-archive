/* Copyright (c) 2009 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import java.util.List;

import com.ipc.ds.base.security.SecurityContext;
import com.ipc.ds.entity.dto.Device;
import com.ipc.ds.entity.dto.DeviceUDA;
import com.ipc.ds.entity.dto.Enterprise;
import com.ipc.ds.entity.dto.Instance;
import com.ipc.ds.entity.dto.User;
import com.ipc.ds.entity.dto.Zone;
import com.ipc.ds.entity.manager.DeviceManager;
import com.ipc.ds.entity.manager.DeviceUDAManager;
import com.ipc.ds.entity.manager.EnterpriseManager;
import com.ipc.ds.entity.manager.InstanceManager;
import com.ipc.ds.entity.manager.ZoneManager;
import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.types.util.UDAAndDSEntityUtil;

/**
 * This class is responsible for getting the device co0nfiguration details from the database
 * 
 * @author Bhavya Bhat
 * 
 */
public class GetUpdateableDeviceConfigurationQueryImpl extends
        GetUpdateableDeviceConfigurationQuery implements ExecutableWithContext
{
    private UserContext ctx;

    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {
        UpdateableDeviceConfigurationResultType devConfRes = null;

        try
        {
            final SecurityContext basicSecContext = this.ctx.getSecurityContext();
            //final User user = UDAAndDSEntityUtil.getUser(this.ctx);

           // Zone zone = UDAAndDSEntityUtil.GetZoneForTheUser(this.ctx);

            final DeviceManager dsDevMgr = new DeviceManager(basicSecContext);
           /* final List<Device> lstDevices = dsDevMgr.getDevicesFor(zone);
            if (lstDevices == null || lstDevices.size() == 0)
            {
                throw new ExecutionException(
                        "lstDevices from DS is NULL or of Size Zero for the given user"
                                + getUserName());
            }
            */

            final DeviceUDAManager dsDevUDAMgr = new DeviceUDAManager(basicSecContext);
            String deviceID = this.ctx.getCurrentDeviceID();
			Device dev = dsDevMgr.findByDeviceUUIDEqualing(deviceID);
            final DeviceUDA deviceuda = dsDevUDAMgr.getDeviceUDAFor(dev);

            devConfRes = new UpdateableDeviceConfigurationResultType();
            devConfRes.setDeviceUDA(getDSDeviceUDAEntity(deviceuda));

        }
       
        catch (final Throwable e)
        {
            throw new ExecutionException(e);
        }

        if (devConfRes != null)
        {
            final QueryResult qr = new QueryResult();
            qr.setUpdateableDeviceConfigurationResult(devConfRes);
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
     * update the DeviceUdaType using DeviceUDA DB entity
     * 
     * @param devUDA DeviceUDA
     * @return DeviceUdaType
     */
    private DeviceUdaType getDSDeviceUDAEntity(final DeviceUDA devUDA)
    {
        final DeviceUdaType devUDAType = new DeviceUdaType();
        devUDAType.setAudioSpeakerVolume(devUDA.getAudioSpeakerVolume());
        devUDAType.setAudioMicrophoneVolume(devUDA.getAudioMicrophoneVolume());
        devUDAType.setLogFileName(devUDA.getLogFileName());
        devUDAType.setLogFileRotation(devUDA.getLogFileRotation());
        devUDAType.setMaxLogFileSize(devUDA.getMaxLogFileSize());
        devUDAType.setCompressLogFiles(devUDA.getCompressLogFiles());
        devUDAType.setAutoUpdateDownloadLocation(devUDA.getAutoUpdateDownloadLocation());
        devUDAType.setAutoUpdateTimeout(devUDA.getAutoUpdateTimeout());
        devUDAType.setAutoUpdateRetryCount(devUDA.getAutoUpdateRetryCount());
        devUDAType.setAudioNetEQ(devUDA.getAudioNetEQ());
        devUDAType.setAudioNetEQBGN(devUDA.getAudioNetEQBGN());
        devUDAType.setAudioVAD(devUDA.getAudioVAD());
        devUDAType.setAudioNS(devUDA.getAudioNS());
        devUDAType.setAudioAGC(devUDA.getAudioAGC());
        devUDAType.setAudioEC(devUDA.getAudioEC());
        devUDAType.setAudioVQE(devUDA.getAudioVQE());
        devUDAType.setAudioMicrophoneBoost(devUDA.getAudioMicrophoneBoost());
        devUDAType.setAudioStereoPlayout(devUDA.getAudioStereoPlayout());
        devUDAType.setAudioRTCPXR(devUDA.getAudioRTCPXR());
        devUDAType.setRTCP(devUDA.getRTCP());
        devUDAType.setLogLevel(devUDA.getLogLevel());

        return devUDAType;
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
}
