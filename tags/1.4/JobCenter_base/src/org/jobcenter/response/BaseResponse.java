package org.jobcenter.response;

public abstract class BaseResponse {




	public static final int ERROR_CODE_NO_ERRORS = 0;


	public static final int ERROR_CODE_GENERAL_ERROR = 1;

	/**
	 * Node Name not found or not valid for network address
	 */
	public static final int ERROR_CODE_NODE_NAME_INVALID_FOR_NETWORK_ADDRESS = 2;


	public static final int ERROR_CODE_DATABASE_NOT_UPDATED = 3;

	public static final int ERROR_CODE_JOB_TYPE_NAME_NOT_FOUND = 4;

	public static final int ERROR_CODE_JOB_TYPE_NAME_DISABLED = 5;

	public static final int ERROR_CODE_REQUEST_TYPE_NAME_NOT_FOUND = 6;

	public static final int ERROR_CODE_REQUEST_ID_NOT_FOUND_FOR_GIVEN_REQUEST_TYPE_NAME = 7;



	private boolean errorResponse;

	private int errorCode = ERROR_CODE_NO_ERRORS;


	private String clientIPAddressAtServer;
	private String clientNodeName;


	/**
	 * The server will populate this with the Remote Address of the client as seen by the server
	 * @return
	 */
	public String getClientIPAddressAtServer() {
		return clientIPAddressAtServer;
	}

	public void setClientIPAddressAtServer(String clientIPAddressAtServer) {
		this.clientIPAddressAtServer = clientIPAddressAtServer;
	}

	public boolean isErrorResponse() {
		return errorResponse;
	}

	public void setErrorResponse(boolean errorResponse) {
		this.errorResponse = errorResponse;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}


	/**
	 * @return
	 */
	public String getErrorCodeDescription() {

		switch ( errorCode ) {

		case ERROR_CODE_GENERAL_ERROR:
			return "\"general error\"";

		case ERROR_CODE_NODE_NAME_INVALID_FOR_NETWORK_ADDRESS:
			return "\"node name invalid for network address\"";

		case ERROR_CODE_DATABASE_NOT_UPDATED:
			return "\"database not updated\"";

		case ERROR_CODE_JOB_TYPE_NAME_NOT_FOUND:
			return "\"job type name not found\"";

		case ERROR_CODE_JOB_TYPE_NAME_DISABLED:
			return "\"job type name disabled\"";

		case ERROR_CODE_REQUEST_TYPE_NAME_NOT_FOUND:
			return "\"request type name not found\"";

		case ERROR_CODE_REQUEST_ID_NOT_FOUND_FOR_GIVEN_REQUEST_TYPE_NAME:
			return "\"request id not found for given request type name\"";

		default:
			return "UNKNOWN ERROR";

		}
	}

	public String getClientNodeName() {
		return clientNodeName;
	}

	public void setClientNodeName(String clientNodeName) {
		this.clientNodeName = clientNodeName;
	}

}
