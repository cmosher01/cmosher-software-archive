/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.types;

import com.ipc.ds.base.exception.EntityDoesNotExistException;
import com.ipc.ds.base.exception.InvalidContextException;
import com.ipc.ds.base.exception.InvalidEntityException;
import com.ipc.ds.base.exception.RepeatedSaveInTransactionException;
import com.ipc.ds.base.exception.StorageFailureException;
import com.ipc.ds.base.exception.UninitializedEntityException;
import com.ipc.ds.base.security.BasicSecurityContext;
import com.ipc.ds.base.security.SecurityContext;
import com.ipc.ds.core.loader.InvalidDataException;
import com.ipc.ds.entity.dto.Job;
import com.ipc.ds.entity.dto.JobDetail;
import com.ipc.ds.entity.dto.Job.EnumResult;
import com.ipc.ds.entity.manager.JobDetailManager;
import com.ipc.ds.entity.manager.JobManager;
import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.util.logging.Logger;
import java.util.Date;
import java.util.List;
import com.ipc.uda.service.execution.ExecutionException;

/**
 * @author Bhavya bhat
 * 
 *         This class is responsible for commiting the job details to the
 *         database
 * 
 */
public class SendDiagnosticsResultsCommandImpl extends
		SendDiagnosticsResultsCommand implements ExecutableWithContext {
	private UserContext ctx;

	// Logger _logger = Log.logger();

	@Override
	public void setUserContext(UserContext ctx) {
		this.ctx = ctx;
	}

	@Override
	public Optional<ExecutionResult> execute() throws ExecutionException {

		try {
			SecurityContext secContext = this.ctx.getSecurityContext();
			// REVIEW don't keep entire logic in one method ;
			// you may Divide till preparing jobDetailList in a seperate method;
			// -- done
			JobManager jobManager = new JobManager(secContext);
			Job job = null;

			int jobId = 0;
			List<JobDetail> jobDetailList = null;

			if (jobManager == null) {
				Log.logger().debug("JobManager object is null");
				throw new ExecutionException("JobManager object is null ");
			}
			// Get the job from jobId
			List<DiagnosticsResultType> diagnosticsResType = this
					.getDiagnosticsResult();
			DiagnosticsResultType resType = diagnosticsResType.get(0);
			jobId = resType.getJobID();

			job = jobManager.getById(jobId);

			// Get the job details from the job id
			JobDetailManager jobDetailMgr = new JobDetailManager(secContext);

			if (jobDetailMgr == null) {
				Log.logger().debug("JobDetailManager is null");
				throw new ExecutionException("JobDetailManager object is null ");
			}
			if (job != null) {
				jobDetailList = jobDetailMgr.getByJob(job);
			} else {
				Log.logger().debug("job is null");
				throw new ExecutionException("job object is null for job Id:"
						+ jobId);
			}

			// REVIEW keep the logic of iterating and saving logic in seperate
			// method
			// Check if we need to iterate or retrieve the first from the list--done

			UpdateJobDetailsFortheJob(jobDetailList, job, jobManager,
					jobDetailMgr, resType);

		} catch (final Throwable lException) {
			throw new ExecutionException(lException);
		}

		return new Nothing<ExecutionResult>();
	}

	/**
	 * This method is responsible for updating the JobDetail Entity
	 * 
	 * @param jobDetail
	 *            JobDetail
	 * @param resType
	 * @return JobDetail
	 * @throws UninitializedEntityException
	 * @throws InvalidEntityException
	 */
	private JobDetail updateJobDetailsinDS(JobDetail jobDetail,
			DiagnosticsResultType resType, Job job)
			throws InvalidEntityException, UninitializedEntityException {
		JobDetail.EnumResult jobEnumRes = JobDetail.EnumResult.valueOf(resType
				.getResult().value());
		jobDetail.setJob(job);
		jobDetail.setResult(jobEnumRes);
		jobDetail.setEndTime(new Date());
		jobDetail.setOutput(resType.getOutput());

		return jobDetail;
	}

	/**
	 * This method is responsible for updating/adding job details in database
	 * 
	 * @param jobDetailList
	 * @param job
	 * @param jobManager
	 * @param jobDetailMgr
	 * @param resType
	 * @throws InvalidEntityException
	 * @throws UninitializedEntityException
	 * @throws StorageFailureException
	 * @throws InvalidContextException
	 * @throws EntityDoesNotExistException
	 * @throws RepeatedSaveInTransactionException
	 */

	private void UpdateJobDetailsFortheJob(List<JobDetail> jobDetailList,
			Job job, JobManager jobManager, JobDetailManager jobDetailMgr,
			DiagnosticsResultType resType) throws InvalidEntityException,
			UninitializedEntityException, StorageFailureException,
			InvalidContextException, EntityDoesNotExistException,
			RepeatedSaveInTransactionException {
		JobDetail jobDetail = null;
		if (jobDetailList != null && jobDetailList.size() > 0) {
			jobDetail = jobDetailList.get(0);
		} else {
			jobDetail = jobDetailMgr.NewJobDetail();
		}

		EnumResult enumRes = EnumResult.valueOf(resType.getResult().value());
		job.setResult(enumRes);

		jobDetail = updateJobDetailsinDS(jobDetail, resType, job);

		if (job != null) {
			jobManager.save(job);
		} else {
			Log.logger().debug("Job object is null");
		}

		if (jobDetail != null) {
			jobDetailMgr.save(jobDetail);
		} else {
			Log.logger().debug("Job detail object is null");
		}
	}
}
