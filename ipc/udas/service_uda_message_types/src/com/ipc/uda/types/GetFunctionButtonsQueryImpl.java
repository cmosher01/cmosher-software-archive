/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.types;

import java.util.ArrayList;
import java.util.Collection;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.ds.entity.dto.Button;
import com.ipc.uda.service.callproc.ButtonSheet;
import com.ipc.uda.service.callproc.UdaButton;
import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.types.util.DataServicesSubscriptionHelper;

/**
 * 
 * This class is responsible for fetching the function buttons ( from usercontext )
 *  
 * 
 *  * @author Bhavya Bhat
 *  
 */
public class GetFunctionButtonsQueryImpl extends GetFunctionButtonsQuery
		implements ExecutableWithContext {
	private UserContext ctx;

	@Override
	public Optional<ExecutionResult> execute() throws ExecutionException 
	{
//		FunctionButtonsResultType resType = new FunctionButtonsResultType();
		// get all buttons from the button sheet application
		final Collection<UdaButton> udaButtonList = new ArrayList<UdaButton>(
				ButtonSheet.MAX_NUM_OF_BUTTONS);
		//this.ctx.getCallContext().initializeButtonSheet();
		this.ctx.getCallContext().getButtonSheet().getAllButtons(udaButtonList);
		if(udaButtonList == null || udaButtonList.size()==0)
		{
			Log.logger().debug("The button list from UDA is null for user:"+ctx.getUserName());
			throw new ExecutionException("The button list from UDA is null");
		}
		
		FunctionButtonsResultType resType = prepareFunctionalButtonsResult (udaButtonList);
		
		QueryResult queryRes = new QueryResult();
		queryRes.setFunctionButtonsResult(resType);

		return new Optional<ExecutionResult>(queryRes);
	}

	/**
	 * This method is responsible for populating the FunctionButtonsResultType result with all the functional buttons populated
	 * 
	 * @param udaButtonList
	 *            Collection
	 *            
	 * @return FunctionButtonsResultType
	 */
	private FunctionButtonsResultType prepareFunctionalButtonsResult(
			Collection<UdaButton> udaButtonList) 
		{
		
		FunctionButtonsResultType resType = new FunctionButtonsResultType();
	
		for (final UdaButton udaButton : udaButtonList) 
		{
			Button.EnumButtonType buttonTypeEnum = udaButton.getDsButton()
					.getButtonType();
			
			if (buttonTypeEnum.name().equals(com.ipc.ds.entity.dto.Button.EnumButtonType.KeySequence.toString())) 
			{
				resType.getFunctionButton().add(
						mapUDAButtonToResultObject(udaButton));
				DataServicesSubscriptionHelper.createSubscriptionsTo(
						Button.class.getSimpleName(), udaButton.getDsButton()
								.getId(), this.ctx);
			}
		}
		return resType;
	}

	@Override
	public void setUserContext(UserContext unused) 
	{
		ctx = unused;
	}

	/**
	 * This method is responsible for populating the FunctionButtonType result
	 * type
	 * 
	 * @param udaButton
	 *            UdaButton
	 * @return FunctionButtonType
	 */

	private FunctionButtonType mapUDAButtonToResultObject(UdaButton udaButton) 
	{
		// TODO : when XSD is changed move this method to Util class
		FunctionButtonType funButtonType = new FunctionButtonType();
		Button dsButton = udaButton.getDsButton();
		UID uid = UID.fromDataServicesID(dsButton.getId());
		funButtonType.setButtonId(uid);
		// TODO : need to check the value to be set to canonical Label
		funButtonType.setCanonicalName(dsButton.getButtonLabel());
		String type = dsButton.getButtonType().toString();
		funButtonType.setType(type);
		return funButtonType;
	}
}
