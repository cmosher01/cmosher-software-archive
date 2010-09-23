/* Copyright (c) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import com.ipc.ds.entity.dto.Codec;
import com.ipc.ds.entity.dto.DeviceUDA;
import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;

/**
 * This class is responsible for updating the device configuration to database
 * 
 * @author Bhavya Bhat
 * 
 */
public class UpdateDeviceConfigurationCommandImpl extends UpdateDeviceConfigurationCommand
        implements ExecutableWithContext
{

    //public final static Logger logger = Log.logger();
    private UserContext ctx;

    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {
    	/*
        UserManager userMgr = null;
        EnterpriseManager enterprisepMgr = null;
        InstanceManager instanceManager = null;
        ZoneManager zoneManager = null;
        DeviceManager deviceManager = null;
        DeviceUDAManager deviceUDAManager = null;
        CodecManager codecManager = null;

        SecurityContext basicSecContext = this.ctx.getSecurityContext();

        try
        {

            userMgr = new UserManager(basicSecContext);
            List<User> usersList = userMgr.findByLoginNameEqualing(this.ctx.getUser().getName());
            enterprisepMgr = new EnterpriseManager(basicSecContext);
            instanceManager = new InstanceManager(basicSecContext);
            zoneManager = new ZoneManager(basicSecContext);
            deviceManager = new DeviceManager(basicSecContext);
            deviceUDAManager = new DeviceUDAManager(basicSecContext);
            codecManager = new CodecManager(basicSecContext);

            CodecType codecType = this.getCodec();
            DeviceUdaType deviceUdaType = this.getDeviceUda();

            User user = null;

            if (usersList != null && usersList.size() > 0)
            {

                user = usersList.get(0);// for a given username there is only one user

                List<Enterprise> lstEnterprises = enterprisepMgr.findByUser(user);
                if (lstEnterprises == null || lstEnterprises.size() == 0)
                {
                    throw new ExecutionException(
                            "lstEnterprises from DS is NULL or of Size Zero for the given user");
                }

                Enterprise enterprise = lstEnterprises.get(0);
                List<Instance> lstInstances = instanceManager.getInstanceFor(enterprise);
                if (lstInstances == null || lstInstances.size() == 0)
                {
                    throw new ExecutionException(
                            "lstInstances from DS is NULL or of Size Zero for the given user");
                }
                Instance instance = lstInstances.get(0);
                List<Zone> lstZones = zoneManager.getZonesFor(instance);
                if (lstZones == null || lstZones.size() == 0)
                {
                    throw new ExecutionException(
                            "lstZones from DS is NULL or of Size Zero for the given user");
                }
                Zone zone = lstZones.get(0);

                List<Device> lstDevices = deviceManager.getDeviceFor(zone);
                if (lstDevices == null || lstDevices.size() == 0)
                {
                    throw new ExecutionException(
                            "lstDevices from DS is NULL or of Size Zero for the given user");
                }
                Device dev = lstDevices.get(0);
                DeviceUDA deviceUdaDTO = deviceUDAManager.getDeviceUDAFor(dev);

                List<Codec> lstCodecs = codecManager.getCodecsFor(dev);
                if (lstCodecs == null || lstCodecs.size() == 0)
                {
                    throw new ExecutionException(" lstCodecs from DS is NULL or of Size Zero for the given user");
                }
                Codec codecDTO = lstCodecs.get(0);

                deviceUdaDTO = updateDSDeviceUdaDTO(deviceUdaDTO, deviceUdaType);
                codecDTO = updateDSCodecDTO(codecDTO, codecType);

                deviceUDAManager.save(deviceUdaDTO);
                codecManager.save(codecDTO);
            }
            else
            {
                throw new ExecutionException("Users List from DS is null for the given user");
            }
        }
        catch (final Throwable e)
        {
            throw new ExecutionException(e);
        }
	*/
        return new Nothing<ExecutionResult>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ipc.uda.service.context.HasContext#setUserContext(com.ipc.uda.service.context.UserContext
     * )
     */
    @Override
    public void setUserContext(UserContext ctx)
    {
        this.ctx = ctx;

    }

    /**
     * 
     * @param deviceUdaDTO DeviceUDA
     * @param deviceUdaType DeviceUdaType
     * @return deviceUdaDTO DeviceUDA
     */
    public DeviceUDA updateDSDeviceUdaDTO(DeviceUDA deviceUdaDTO, DeviceUdaType deviceUdaType)
    {
/*
        deviceUdaDTO.setAudioAGC(deviceUdaType.getAudioAGC());
        deviceUdaDTO.setAudioEC(deviceUdaType.getAudioEC());

        deviceUdaDTO.setAudioMicrophoneBoost(deviceUdaType.isAudioMicrophoneBoost());

        deviceUdaDTO.setAudioMicrophoneVolume(deviceUdaType.getAudioMicrophoneVolume());
        deviceUdaDTO.setAudioNetEQ(deviceUdaType.getAudioNetEQ());
        deviceUdaDTO.setAudioNetEQBGN(deviceUdaType.getAudioNetEQBGN());
        deviceUdaDTO.setAudioNS(deviceUdaType.getAudioNS());

        deviceUdaDTO.setAudioRTCPXR(deviceUdaType.isAudioRTCPXR());

        deviceUdaDTO.setAudioSpeakerVolume(deviceUdaType.getAudioSpeakerVolume());

        deviceUdaDTO.setAudioStereoPlayout(deviceUdaType.isAudioStereoPlayout());
        deviceUdaDTO.setAudioVAD(deviceUdaType.getAudioVAD());
        deviceUdaDTO.setAudioVQE(deviceUdaType.getAudioVQE());

        deviceUdaDTO.setAutoUpdateDownloadLocation(deviceUdaType.getAutoUpdateDownloadLocation());

        deviceUdaDTO.setAutoUpdateRetryCount(deviceUdaType.getAutoUpdateRetryCount());
        deviceUdaDTO.setAutoUpdateTimeout(deviceUdaType.getAutoUpdateTimeout());

        deviceUdaDTO.setCompressDiagnosticLogFiles(deviceUdaType.isCompressDiagnosticLogFiles());

        deviceUdaDTO.setDebugFileName(deviceUdaType.getDebugFileName());

        deviceUdaDTO.setDebugLevel(deviceUdaType.getDebugLevel());

        deviceUdaDTO.setDiagnosticFileName(deviceUdaType.getDiagnosticFileName());
        deviceUdaDTO.setLogFileName(deviceUdaType.getLogFileName());
        deviceUdaDTO.setLogFileRotation(deviceUdaType.getLogFileRotation());

        deviceUdaDTO.setRTCP(deviceUdaType.isRTCP());

        deviceUdaDTO.setTraceFileName(deviceUdaType.getTraceFileName());

        return deviceUdaDTO;
        */ 
    	return null;
    }

    /**
     * 
     * @param codec Codec
     * @param codecType CodecType
     * @return codec Codec
     */
    public Codec updateDSCodecDTO(Codec codec, CodecType codecType)
    {
        // codec.set
        codec.setPacketPeriod(codecType.getPacketPeriod());
        codec.setVAD(codecType.isVAD());
        return codec;
    }

}
