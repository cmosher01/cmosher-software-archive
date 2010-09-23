/* Copyright (c) 2009 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import java.util.Calendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import monfox.toolkit.snmp.agent.modules.SnmpV2Mib.SysOREntry;

import com.ipc.ds.base.exception.InvalidContextException;
import com.ipc.ds.base.exception.StorageFailureException;
import com.ipc.ds.base.security.BasicSecurityContext;
import com.ipc.ds.base.security.SecurityContext;
import com.ipc.ds.entity.dto.Address;
import com.ipc.ds.entity.dto.Codec;
import com.ipc.ds.entity.dto.Device;
import com.ipc.ds.entity.dto.DeviceType;
import com.ipc.ds.entity.dto.DeviceUDA;
import com.ipc.ds.entity.dto.DunkinLocation;
import com.ipc.ds.entity.dto.Enterprise;
import com.ipc.ds.entity.dto.User;
import com.ipc.ds.entity.dto.Zone;
import com.ipc.ds.entity.manager.AddressManager;
import com.ipc.ds.entity.manager.DeviceManager;
import com.ipc.ds.entity.manager.DeviceTypeManager;
import com.ipc.ds.entity.manager.DeviceUDAManager;
import com.ipc.ds.entity.manager.DunkinLocationManager;
import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.types.util.UDAAndDSEntityUtil;

/**
 * @author mordarsd
 * @author Jagannadham Dulipala
 * @author Bhavya Bhat
 * 
 */
public class GetDeviceConfigurationQueryImpl extends
		GetDeviceConfigurationQuery implements ExecutableWithContext {
	private UserContext ctx;

	@Override
	public Optional<ExecutionResult> execute() throws ExecutionException {

		DeviceConfigurationResultType resType = null;

		try {
			final SecurityContext basicSecContext = this.ctx
					.getSecurityContext();

			final User user = UDAAndDSEntityUtil.getUser(this.ctx);

			Enterprise dsEnterprise = UDAAndDSEntityUtil
					.getEnterpriseForUser(this.ctx);
			// Zone zone = UDAAndDSEntityUtil.GetZoneForTheUser(this.ctx);
			final DeviceManager dsDevMgr = new DeviceManager(basicSecContext);
			/*
			 * final List<Device> lstDevices = dsDevMgr.getDevicesFor(zone);
			 * 
			 * if (lstDevices == null || lstDevices.size() == 0) { throw new
			 * ExecutionException(
			 * "lstDevices from DS is NULL or of Size Zero for the given user" +
			 * getUserName()); }
			 */

			String deviceID = this.ctx.getCurrentDeviceID();
			Device dev = dsDevMgr.findByDeviceUUIDEqualing(deviceID);

			final DeviceUDAManager dsDevUDAMgr = new DeviceUDAManager(
					basicSecContext);
			final DeviceUDA deviceuda = dsDevUDAMgr.getDeviceUDAFor(dev);
			final DunkinLocationManager locationManager = new DunkinLocationManager(
					basicSecContext);
			final DunkinLocation dunkinLocation = locationManager
					.getDunkinLocationFor(dev);
			final DeviceTypeManager devTypeManager = new DeviceTypeManager(
					basicSecContext);
			final DeviceType devType = devTypeManager.getDeviceTypeFor(dev);
			resType = new DeviceConfigurationResultType();

			resType.setEnterprise(updateEnterpriseTypeFromDS(dsEnterprise));

			// resType.setInstance(updateInstanceTypeFromDS(instance));
			// resType.setZone(updateZoneTypeFromDS(zone));
			resType.setDevice(updateDeviceTypeFromDS(dev));
			resType.setDeviceUda(getUDADeviceTypefromDSUDADevice(deviceuda));
			resType.setDeviceType(updateDeviceTypeTypeFromDS(devType));
			resType.setLocation(updateDunkinLocationTypeFromDS(dunkinLocation,
					basicSecContext));

		} catch (final ExecutionException e) {
			throw e;
		} catch (final Throwable e) {
			throw new ExecutionException(e);
		}
		if (resType != null) {
			final QueryResult qr = new QueryResult();
			qr.setDeviceConfigurationResult(resType);
			return new Optional<ExecutionResult>(qr);
		}

		return new Nothing<ExecutionResult>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ipc.uda.service.context.HasContext#setUserContext(com.ipc.uda.service
	 * .context.UserContext)
	 */
	@Override
	public void setUserContext(final UserContext ctx) {
		this.ctx = ctx;
	}

	/**
	 * Get getCodecTypefromDSCodec
	 * 
	 * @input Codec (DS)
	 * @return CodecType (UDA Type)
	 */
	private CodecType getCodecTypefromDSCodec(final Codec codec,
			final CodecType codecType) {
		Log.logger().debug("CodecType getCodecTypefromDSCodec(Codec codec)");
		codecType.setType(codec.getCodecType().name());
		codecType.setPacketPeriod(codec.getPacketPeriod());
		codecType.setVAD(codec.getVAD());
		return codecType;
	}

	/**
	 * Get getUDADeviceTypefromDSUDADevice
	 * 
	 * @input DeviceUDA (DS)
	 * @return DeviceUdaType (UDA Type)
	 */
	private DeviceUdaType getUDADeviceTypefromDSUDADevice(
			final DeviceUDA deviceuda) {

		final DeviceUdaType deviceUDAType = new DeviceUdaType();
		deviceUDAType.setAudioAGC(deviceuda.getAudioAGC());
		deviceUDAType.setAudioEC(deviceuda.getAudioEC());
		deviceUDAType.setAudioMicrophoneBoost(deviceuda
				.getAudioMicrophoneBoost());
		deviceUDAType.setAudioMicrophoneVolume(deviceuda
				.getAudioMicrophoneVolume());
		deviceUDAType.setAudioNetEQ(deviceuda.getAudioNetEQ());
		deviceUDAType.setAudioNetEQBGN(deviceuda.getAudioNetEQBGN());
		deviceUDAType.setAudioNS(deviceuda.getAudioNS());
		deviceUDAType.setAudioRTCPXR(deviceuda.getAudioRTCPXR());
		deviceUDAType.setAudioSpeakerVolume(deviceuda.getAudioSpeakerVolume());
		deviceUDAType.setAudioStereoPlayout(deviceuda.getAudioStereoPlayout());
		deviceUDAType.setAudioVAD(deviceuda.getAudioVAD());
		deviceUDAType.setAudioVQE(deviceuda.getAudioVQE());

		deviceUDAType.setAutoUpdateDownloadLocation(deviceuda
				.getAutoUpdateDownloadLocation());
		deviceUDAType.setAutoUpdateRetryCount(deviceuda
				.getAutoUpdateRetryCount());
		deviceUDAType.setRTCP(deviceuda.getRTCP());
		deviceUDAType.setMaxLogFileSize(deviceuda.getMaxLogFileSize());
		deviceUDAType.setLogFileRotation(deviceuda.getLogFileRotation());
		deviceUDAType.setLogFileName(deviceuda.getLogFileName());

		deviceUDAType.setAutoUpdateTimeout(deviceuda.getAutoUpdateTimeout());
		deviceUDAType.setCompressLogFiles(deviceuda.getCompressLogFiles());
		deviceUDAType.setLogLevel(deviceuda.getLogLevel());

		Log
				.logger()
				.debug(
						"DeviceUdaType getUDADeviceTypefromDSUDADevice(DeviceUDA deviceuda): ");

		return deviceUDAType;
	}

	/**
	 * Get User Name of the context
	 * 
	 * @return String
	 */
	private String getUserName() {
		return this.ctx.getUser().getName();
	}

	/**
	 * This method will update the instance type using data from database
	 * 
	 * @param instance
	 * @return
	 */
	/*
	 * private InstanceType updateInstanceTypeFromDS(Instance instance) {
	 * InstanceType instanceType = new InstanceType(); //
	 * instanceType.setName(instance.get) TODO : need to set the value
	 * instanceType.setDomainName(instance.getDomainName());
	 * instanceType.setCliOnFloat(instance.getCLIonFloat());
	 * 
	 * // check needed on this impl EnumHoldQueueType enumHoldQueue =
	 * EnumHoldQueueType.valueOf(instance.getHoldQueue().name());
	 * instanceType.setHoldQueue(enumHoldQueue);
	 * 
	 * instanceType.setHoldToggle(instance.getHoldToggle());
	 * instanceType.setIntercomBroadcast(instance.getIntercomBroadcast());
	 * EnumIntercomSplashType intercomSplash =
	 * EnumIntercomSplashType.valueOf(instance .getIntercomSplash().name());
	 * instanceType.setIntercomSplash(intercomSplash);
	 * instanceType.setIntrusionTone(instance.getIntrusionTone());
	 * instanceType.setOneButtonDivert(instance.getOneButtonDivert());
	 * EnumPresetSimplexType enumPresetSimplex =
	 * EnumPresetSimplexType.valueOf(instance
	 * .getPresetOrSimplexBroadcast().name());
	 * instanceType.setPrefixOrSimplexBroadcast(enumPresetSimplex);
	 * instanceType.setPrivacy(instance.getPrivacy());
	 * 
	 * EnumRecordWarningToneType enumRecordOnDemand =
	 * EnumRecordWarningToneType.valueOf(instance
	 * .getRecordWarningTone().name());
	 * instanceType.setRecordWarningTone(enumRecordOnDemand);
	 * EnumRecordOnDemandType enumRecordWarningTone =
	 * EnumRecordOnDemandType.valueOf(instance .getRecordonDemand().name());
	 * instanceType.setRecordOnDemand(enumRecordWarningTone);
	 * 
	 * return instanceType; }
	 */
	/**
	 * This method is responsible for updating the Zone type with data from
	 * database
	 * 
	 * @param zone
	 * @return zoneType ZoneType
	 * @throws DatatypeConfigurationException
	 */

	/*
	 * private ZoneType updateZoneTypeFromDS(Zone zone) { ZoneType zoneType =
	 * new ZoneType(); zoneType.setName(zone.getName()); return zoneType; }
	 */

	private com.ipc.uda.types.DeviceType updateDeviceTypeFromDS(
			final Device device) throws DatatypeConfigurationException {
		final com.ipc.uda.types.DeviceType devType = new com.ipc.uda.types.DeviceType();
		devType.setId(String.valueOf(device.getId()));
		devType.setName(device.getName());

		devType.setAssignedVersionRevision(device.getAssignedVersionRevision());
		java.util.Date date = device.getConfigUpdatedDate();
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		DatatypeFactory dataFactory = DatatypeFactory.newInstance();
		final XMLGregorianCalendar xmlCal = dataFactory
				.newXMLGregorianCalendar();
		xmlCal.setDay(calendar.get(Calendar.DAY_OF_MONTH));
		xmlCal.setMonth(calendar.get(Calendar.MONTH) + 1);
		xmlCal.setYear(calendar.get(Calendar.YEAR));
		xmlCal.setTime(calendar.get(Calendar.HOUR_OF_DAY), calendar
				.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));

		devType.setConfigUpdatedDate(xmlCal);

		date = device.getDeployedDate();
		calendar.setTime(date);
		xmlCal.setDay(calendar.get(Calendar.DAY_OF_MONTH));
		xmlCal.setMonth(calendar.get(Calendar.MONTH) + 1);
		xmlCal.setYear(calendar.get(Calendar.YEAR));
		xmlCal.setTime(calendar.get(Calendar.HOUR_OF_DAY), calendar
				.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
		devType.setDeployedDate(xmlCal);
		if (device.getDeviceState().name().toUpperCase() != null) {
			final EnumDeviceStateType enumDevStateType = EnumDeviceStateType
					.valueOf(device.getDeviceState().name().toUpperCase());
			devType.setDeviceState(enumDevStateType);
		} else {
			Log.logger().debug("EnumDeviceStateType value is null");
		}

		devType.setDomainName(device.getDomainName());
		devType.setEquipped(device.getEquipped());
		devType.setHttpPort(device.getHTTPPort());
		devType.setHttpsPort(device.getHTTPSPort());
		devType.setIpAddress(device.getIPAddress());
		devType.setIpAddress2(device.getIPAddress2());

		devType.setInventoryInfo(device.getInventoryInfo());

		date = device.getInventoryRegistrationTime();
		calendar.setTime(date);
		xmlCal.setDay(calendar.get(Calendar.DAY_OF_MONTH));
		xmlCal.setMonth(calendar.get(Calendar.MONTH) + 1);
		xmlCal.setYear(calendar.get(Calendar.YEAR));
		xmlCal.setTime(calendar.get(Calendar.HOUR_OF_DAY), calendar
				.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));

		devType.setInventoryRegistrationTime(xmlCal);
		devType.setName(device.getName());
		devType.setNumberOfRTPPorts(device.getNumberOfRTPPorts());

		date = device.getPurchaseDate();
		calendar.setTime(date);
		xmlCal.setDay(calendar.get(Calendar.DAY_OF_MONTH));
		xmlCal.setMonth(calendar.get(Calendar.MONTH) + 1);
		xmlCal.setYear(calendar.get(Calendar.YEAR));
		xmlCal.setTime(calendar.get(Calendar.HOUR_OF_DAY), calendar
				.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));

		devType.setPurchaseDate(xmlCal);

		devType.setRtpPortStart(device.getRTPPortStart());

		devType.setSipPort(device.getSIPPort());
		devType.setSipsPort(device.getSIPSPort());
		devType.setSerialNumber(device.getSerialNumber());
		devType.setSubnetmask(device.getSubnetMask());
		devType.setUsingDhcp(device.getUsingDhcp());
		devType.setWarrantyPeriod(device.getWarrantyPeriod());
		return devType;
	}

	private com.ipc.uda.types.DeviceTypeType updateDeviceTypeTypeFromDS(
			final DeviceType deviceType) throws DatatypeConfigurationException {
		final com.ipc.uda.types.DeviceTypeType devTypeType = new com.ipc.uda.types.DeviceTypeType();

		devTypeType.setName(EnumDeviceTypeName.fromValue(deviceType.getName()
				.name()));

		// devTypeType.setDefaultVersion(deviceType.getDefaultVersion());
		devTypeType.setDescription(String.valueOf(deviceType.getDescription()));
		devTypeType.setValue(String.valueOf(deviceType.getValue()));
		final java.util.Date date = deviceType.getLastModified();
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		DatatypeFactory dataFactory = DatatypeFactory.newInstance();
		final XMLGregorianCalendar xmlCal = dataFactory
				.newXMLGregorianCalendar();
		xmlCal.setDay(calendar.get(Calendar.DAY_OF_MONTH));
		xmlCal.setMonth(calendar.get(Calendar.MONTH) + 1);
		xmlCal.setYear(calendar.get(Calendar.YEAR));
		xmlCal.setTime(calendar.get(Calendar.HOUR_OF_DAY), calendar
				.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));

		devTypeType.setLastModifiedDate(xmlCal);
		return devTypeType;
	}

	private DunkinLocationType updateDunkinLocationTypeFromDS(
			final DunkinLocation dunkinLocation, SecurityContext basicSecCtx)
			throws InvalidContextException, StorageFailureException {
		final DunkinLocationType dunkinLocationType = new DunkinLocationType();

		dunkinLocationType.setName(dunkinLocation.getName());
		dunkinLocationType.setFloor(dunkinLocation.getFloor());

		if (dunkinLocation.getLocationType().name().toUpperCase() != null) {
			final EnumLocationType enumLocType = EnumLocationType
					.valueOf(dunkinLocation.getLocationType().name()
							.toUpperCase());
			dunkinLocationType.setLocationType(enumLocType);
		} else {
			Log.logger().debug("EnumLocationType is null ");
		}
		dunkinLocationType.setTimeZone(Integer.parseInt(dunkinLocation
				.getTimeZone()));
		dunkinLocationType.setTimeZoneDiff(dunkinLocation.getTimeZoneDiffSec());
		// Lastly, set the Address

		final AddressManager addrMgr = new AddressManager(basicSecCtx);
		final Address addr = addrMgr.getAddressFor(dunkinLocation);
		final AddressType udaAddr = new AddressType();
		dunkinLocationType.setAddress(udaAddr);
		return dunkinLocationType;
	}

	/**
	 * This method will update the EnterpriseType using data from database
	 * 
	 * @param enterprise
	 *            Enterprise
	 * @return enterpriseType EnterpriseType
	 */
	private EnterpriseType updateEnterpriseTypeFromDS(
			final Enterprise enterprise) {
		final EnterpriseType enterpriseType = new EnterpriseType();
		enterpriseType.setName(enterprise.getName());
		return enterpriseType;
	}

}
