/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.diag;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;

import com.ipc.diag.wsagent.AbstractDiagResource;
import com.ipc.diag.wsagent.xml.AttributeType;
import com.ipc.diag.wsagent.xml.AttributesType;
import com.ipc.diag.wsagent.xml.FileInfoListType;
import com.ipc.diag.wsagent.xml.TestParamsType;
import com.ipc.diag.wsagent.xml.TestType;
import com.ipc.uda.event.ExecutableResultQueue;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.context.UserContextManager;
import com.ipc.uda.service.context.UserID;
import com.ipc.uda.service.util.UdaPrincipal;
import com.ipc.uda.types.DiagnosticsRequestEvent;
import com.ipc.uda.types.EnumDiagnosticsActionType;
import com.ipc.uda.types.EnumDiagnosticsComponentType;
import com.ipc.uda.types.Event;

/**
 * This class extends AbstractDiagResource which is default implementation of
 * DiagResource. This will implement the methods for diagnostics
 * 
 * @author Bhavya Bhat
 */

@Path("diag")
public class DiagnosticsDiagResource extends AbstractDiagResource {

	private final String USER_NAME = "userAOR";
	private final String USER_DEVICEID = "deviceID";
	private final String COMPONENT_NAME = "component";
	private final String TARGET = "target";
	private final String TARGET_UDAS = "UDAS";
	private final String TARGET_UDAC = "UDAC";
	private final String SELF_TEST = "SelfTest";

	private String userName = null;
	private String deviceID = null;
	private String componentName = "";
	private String targetUDAC = "";
	private String targetUDAS = "";

	public DiagnosticsDiagResource() {

	}

	@POST
	@Path("test")
	@Produces("application/xml")
	@Consumes("application/xml")
	public Response executeTest(JAXBElement<TestType> test) {

		try {
			queueDiagnosticsRequestEvent(test);
		} catch (DiagExecutionException e) {
			return e.getResponse();
		} catch (Exception e) {
			return DiagExecutionException.getResponse(503);
		}
		return DiagExecutionException.getResponse(200);
	}

	@Override
	public JAXBElement<FileInfoListType> getConfigFiles() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JAXBElement<FileInfoListType> getLogFiles() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * This method is responsible for creating an event object and putting it in
	 * the event queue (ExecutableResultQueue)
	 * 
	 * @param test
	 * @throws DiagExecutionException
	 */
	private void queueDiagnosticsRequestEvent(JAXBElement<TestType> test)
			throws DiagExecutionException {
		// REVIEW don't keep entire logic in one method ; seperate it out in to
		// multiple methods -- done

		try {
			int jobId = test.getValue().getJobId();

			List<TestParamsType> testParams = test.getValue().getTest();

			sendDiagRequestToEventQueue(testParams, jobId);

		} catch (DiagExecutionException d) {
			throw d;
		} catch (Exception e) {
			throw new DiagExecutionException(503,
					"InterNal processing error while executing the test");
		}

	}

	/**
	 * This method is responsible for retrieving the user, device, component and
	 * target information from the test xml
	 * 
	 * @param userDetails
	 * @throws DiagExecutionException
	 */
	private void setUserDetails(TestParamsType userDetails)
			throws DiagExecutionException {

		try {
			if (userDetails == null)
				throw new DiagExecutionException();
			AttributesType atrType = userDetails.getAttributes();
			List<AttributeType> attrList = atrType.getAttribute();
			for (int targetValue = 0; targetValue < attrList.size(); targetValue++) {
				AttributeType attr = attrList.get(targetValue);
				if (TARGET.equalsIgnoreCase(attr.getName())
						&& (TARGET_UDAS.equalsIgnoreCase(attr.getValue()))) {
					targetUDAS = attr.getValue();
					return;
				}
			}
			for (int counter = 0; counter < attrList.size(); counter++) {
				AttributeType attr = attrList.get(counter);
				if (USER_NAME.equalsIgnoreCase(attr.getName())) {

					userName = attr.getValue();
				} else if (USER_DEVICEID.equalsIgnoreCase(attr.getName())) {
					deviceID = attr.getValue();
				} else if (COMPONENT_NAME.equalsIgnoreCase(attr.getName())) {
					componentName = attr.getValue();
				} else if (TARGET.equalsIgnoreCase(attr.getName())) {
					targetUDAC = attr.getValue();
				}
			}
			if (userName == null || deviceID == null || componentName == null
					|| targetUDAC == null)
				throw new DiagExecutionException();
		} catch (final Throwable e) {
			throw new DiagExecutionException(400,
					"Mandatary User Details [aor or DeviceID] not present in the request");
		}
	}

	/**
	 * This method is responsibe for sending the diagnostics request to the
	 * event queue
	 * 
	 * @param testParams
	 * @param jobId
	 * @throws DiagExecutionException
	 */
	private void sendDiagRequestToEventQueue(List<TestParamsType> testParams,
			int jobId) throws DiagExecutionException {
		String testParmTypeName = null;
		TestParamsType userDetails = null;
		try {
			for (int counter = 0; counter < testParams.size(); counter++) {
				TestParamsType param = testParams.get(counter);
				testParmTypeName = param.getName();
				userDetails = param;

				setUserDetails(userDetails);
				// REVIEW Dont harcode. Make a seperate Constant value May be
				// private final variable TARGET_UDAC--done
				if (targetUDAC.equals(TARGET_UDAC)) {

					sendUDACDiagRequestToEventQueue(testParmTypeName, jobId);

				} else {

					// TODO : Need to implement the server diagnostics and write
					// to database
				}
			}
		} catch (DiagExecutionException d) {
			throw d;
		} catch (Exception e) {
			throw new DiagExecutionException(503,
					"InterNal processing error while executing the test");
		}
	}

	/**
	 * This method is responsible for sending the UDAC Diag request to Event
	 * queue
	 * 
	 * @param testParmTypeName
	 * @param jobId
	 * @throws DiagExecutionException
	 */

	private void sendUDACDiagRequestToEventQueue(String testParmTypeName,
			int jobId) throws DiagExecutionException {

		try {
			final Event event = new Event();
			DiagnosticsRequestEvent diagReqEvent = new DiagnosticsRequestEvent();
			EnumDiagnosticsComponentType enumcomponentName = EnumDiagnosticsComponentType
					.valueOf(componentName);
			diagReqEvent.setComponentName(enumcomponentName);
			// REVIEW Dont harcode. Make a seperate Constant value(
			// private final variable).--done
			if (SELF_TEST.equalsIgnoreCase(testParmTypeName)) {
				diagReqEvent.setAction(EnumDiagnosticsActionType.SELF_TEST);
			} else {
				diagReqEvent.setAction(EnumDiagnosticsActionType.OTHER);
			}

			diagReqEvent.setJobID(jobId);

			event.setDiagnosticsRequest(diagReqEvent);

			UserContext ctx = UserContextManager.getInstance()
					.getOrCreateContext(
							new UserID(new UdaPrincipal(userName), deviceID));
			ExecutableResultQueue.<Event> send(event, ctx.getUser(), deviceID);
		} catch (Exception e) {
			throw new DiagExecutionException(503,
					"InterNal processing error while executing the test");
		}
	}
}
