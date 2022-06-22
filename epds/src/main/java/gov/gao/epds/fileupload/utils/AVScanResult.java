/**
 * 
 */
package gov.gao.epds.fileupload.utils;

/**
 * @author MHussaini
 *
 */
public class AVScanResult {

	 private Exception exception = null;
	 private Integer exitCode;
	 private String exitStatusDesc;
	 private boolean isSucces;
	
	 
	 public AVScanResult(Integer exitCode, String exitStatusDesc, boolean isSucces) {
			this.exitCode = exitCode;
			this.exitStatusDesc = exitStatusDesc;
			this.isSucces = isSucces;
	}
	 
	 public AVScanResult(Exception exception, Integer exitCode, String exitStatusDesc, boolean isSucces) {
		this.exception = exception;
		this.exitCode = exitCode;
		this.exitStatusDesc = exitStatusDesc;
		this.isSucces = isSucces;
	}

	public AVScanResult() {
		
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	

	public String getExitStatusDesc() {
		return exitStatusDesc;
	}

	public void setExitStatusDesc(String exitStatusDesc) {
		this.exitStatusDesc = exitStatusDesc;
	}

	public boolean isSucces() {
		return isSucces;
	}

	public void setSucces(boolean isSucces) {
		this.isSucces = isSucces;
	}

	public Integer getExitCode() {
		return exitCode;
	}

	public void setExitCode(Integer exitCode) {
		this.exitCode = exitCode;
	}

	@Override
	public String toString() {
		return "AVScanResult [exception=" + exception + ", exitCode=" + exitCode + ", exitStatusDesc=" + exitStatusDesc
				+ ", isSucces=" + isSucces + "]";
	}
	 
	 
	 
	 

	 
	 
}
