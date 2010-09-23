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
import com.ipc.uda.types.util.EntityHelper;
import com.ipc.uda.types.util.UDAAndDSEntityUtil;

/**
 * This class is responsible for getting the core configurations from the
 * Database
 * 
 * @author Bhavya Bhat
 * 
 */
public class GetCoreConfigurationQueryImpl extends GetCoreConfigurationQuery
		implements ExecutableWithContext {
	private UserContext ctx;

	@Override
	public Optional<ExecutionResult> execute() throws ExecutionException {
		CoreConfigurationResultType coreConfRes = null;
		try {
			final SecurityContext basicSecContext = this.ctx
					.getSecurityContext();

			//final User user = UDAAndDSEntityUtil.getUser(this.ctx);

			// REVIEW 0 dulicate code - consider refactoring intoUserContext
			//Implemented--moved the code to util
			//Zone zone = UDAAndDSEntityUtil.GetZoneForTheUser(this.ctx);

			final DeviceManager dsDevMgr = new DeviceManager(basicSecContext);
			
			/*final List<Device> lstDevices = dsDevMgr.getDevicesFor(zone);
			if (lstDevices == null || lstDevices.size() == 0) {
				throw new ExecutionException(
						"lstDevices from DS is NULL or of Size Zero for the given user"
								+ getUserName());
			}*/
			
			//Changed the logic to get the device Id as per the latest design
			
			String deviceID = this.ctx.getCurrentDeviceID();

			final DeviceUDAManager dsDevUDAMgr = new DeviceUDAManager(
					basicSecContext);
			Device dev = dsDevMgr.findByDeviceUUIDEqualing(deviceID);
			final DeviceUDA deviceuda = dsDevUDAMgr.getDeviceUDAFor(dev);
			coreConfRes = new CoreConfigurationResultType();

			
			//coreConfRes = EntityHelper.fromDSCoreConfiguration(deviceuda);
			
			populateResultSet(coreConfRes, deviceuda);
			
		} catch (final Throwable e) {
			throw new ExecutionException(e);
		}

		if (coreConfRes != null) {
			final QueryResult qr = new QueryResult();
			qr.setCoreConfigurationResult(coreConfRes);
			return new Optional<ExecutionResult>(qr);
		}

		return new Nothing<ExecutionResult>();
	}

	@Override
	public void setUserContext(final UserContext ctx) {
		this.ctx = ctx;
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
	 * Set the CoreConfigurationResultType with values from DeviceUDA entity
	 * 
	 * @param coreConfRes
	 *            CoreConfigurationResultType
	 * @param deviceUDA
	 *            DeviceUDA
	 * @return coreConfRes CoreConfigurationResultType
	 */
	// REVIEW 0 method should return void because the same reference that is
	// passed in is being returned
	private void populateResultSet(
			final CoreConfigurationResultType coreConfRes,
			final DeviceUDA deviceUDA) {

		coreConfRes.setCompressLogFiles(deviceUDA.getCompressLogFiles());
		coreConfRes.setLogFileName(deviceUDA.getLogFileName());
		coreConfRes.setLogFileRotation(deviceUDA.getLogFileRotation());
		coreConfRes.setLogLevel(deviceUDA.getLogLevel());
		coreConfRes.setMaxLogFileSize(deviceUDA.getMaxLogFileSize());
	}

}
