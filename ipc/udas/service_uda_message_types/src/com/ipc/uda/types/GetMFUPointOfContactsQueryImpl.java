/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.types;

import com.ipc.ds.base.exception.InvalidContextException;
import com.ipc.ds.base.exception.StorageFailureException;
import com.ipc.ds.base.security.SecurityContext;
import com.ipc.ds.entity.dto.PersonalPointOfContact;
import com.ipc.ds.entity.dto.User;
import com.ipc.ds.entity.manager.MFUBucketManager;
import com.ipc.ds.entity.manager.PersonalPointOfContactManager;
import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.types.util.DataServicesSubscriptionHelper;
import com.ipc.uda.types.util.EntityHelper;
import com.ipc.uda.types.util.UDAAndDSEntityUtil;
import com.ipc.ds.entity.dto.MFUBucket;
import com.ipc.ds.entity.dto.MFUBucket.EnumPOCType;
import com.ipc.uda.service.util.Optional;
import java.util.ArrayList;
import java.util.List;
import com.ipc.uda.service.execution.ExecutionResult;

/**
 * @author parkerj
 * @author Bhavya Bhat
 * 
 *         This class is responsible for getting the most frequently used points
 *         of contact from the database
 * 
 */
public class GetMFUPointOfContactsQueryImpl extends GetMFUPointOfContactsQuery
		implements ExecutableWithContext {
	private UserContext ctx;
	MfuPointOfContactResultType resType = new MfuPointOfContactResultType();
	private final String COUNT = "count";

	@Override
	public Optional<ExecutionResult> execute() throws ExecutionException {

		try {
			User user = null;
			List<String> fieldList = new ArrayList<String>();
			int counter = 0;
			user = UDAAndDSEntityUtil.getUser(this.ctx);
			SecurityContext basicSecContext = this.ctx.getSecurityContext();
			MFUBucketManager mfuBaseMgr = new MFUBucketManager(basicSecContext);
			fieldList.add(COUNT);
			mfuBaseMgr.setFieldSortOrder(fieldList);
			List<MFUBucket> mfuPOCList = mfuBaseMgr.findByUser(user);
			MfuPointOfContactType mfuContactType = new MfuPointOfContactType();
			
			if (mfuPOCList.size() > 25)
				counter = 25;
			else
				counter = mfuPOCList.size();
			
			for (int i = 0; i < counter; i++) {

				mfuContactType = UDAAndDSEntityUtil.mapDStoUDAMFUBucket(mfuPOCList.get(i),this.ctx);
				resType.getMfuContact().add(mfuContactType);
				// MfuPointOfContactType mfuContactType =
				// EntityHelper.fromMFUpocDStoUDA(mfuBucket);
				// resType.getMfuContact().add(mfuContactType);
				DataServicesSubscriptionHelper.createSubscriptionsTo(
						MFUBucket.class.getSimpleName(), mfuPOCList.get(i)
								.getId(), this.ctx);
			}

		} catch (Exception e) {

			throw new ExecutionException(e);
		}

		QueryResult queryRes = new QueryResult();
		queryRes.setMfuPointOfContactResult(resType);
		return new Optional<ExecutionResult>(queryRes);
	}

	@Override
	public void setUserContext(UserContext unused) {
		ctx = unused;
	}


}
