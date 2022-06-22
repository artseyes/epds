package gov.gao.epds.rest.auth.services;

public class ServiceResponse {
	private boolean isSuccess;
	private Object data;
	private String message;
	private String exception;
	private String stackTraceDetail;
	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getStackTraceDetail() {
		return stackTraceDetail;
	}

	public void setStackTraceDetail(String stackTraceDetail) {
		this.stackTraceDetail = stackTraceDetail;
	}

	public boolean getIsSuccess() {
		return isSuccess;
	}

	public void setIsSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

}
