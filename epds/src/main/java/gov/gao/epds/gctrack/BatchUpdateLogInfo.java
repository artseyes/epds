package gov.gao.epds.gctrack;

import java.util.Map;

public class BatchUpdateLogInfo {
	private int numberOfProcessedCases;
	private int numberOfFailedUpdates;
	private int numberOfSuccessfulUpdates;
	private int numberOfUnchangedCases;
	private Map<String, String> aNoToChangeMade;
	private Map<String, String> aNoToException;

	public Map<String, String> getaNoToException() {
		return aNoToException;
	}

	public void setaNoToException(Map<String, String> aNoToException) {
		this.aNoToException = aNoToException;
	}

	public int getNumberOfProcessedCases() {
		return numberOfProcessedCases;
	}

	public void setNumberOfProcessedCases(int numberOfProcessedCases) {
		this.numberOfProcessedCases = numberOfProcessedCases;
	}

	public int getNumberOfFailedUpdates() {
		return numberOfFailedUpdates;
	}

	public void setNumberOfFailedUpdates(int numberOfFailedUpdates) {
		this.numberOfFailedUpdates = numberOfFailedUpdates;
	}

	public int getNumberOfSuccessfulUpdates() {
		return numberOfSuccessfulUpdates;
	}

	public void setNumberOfSuccessfulUpdates(int numberOfSuccessfulUpdates) {
		this.numberOfSuccessfulUpdates = numberOfSuccessfulUpdates;
	}

	public int getNumberOfUnchangedCases() {
		return numberOfUnchangedCases;
	}

	public void setNumberOfUnchangedCases(int numberOfUnchangedCases) {
		this.numberOfUnchangedCases = numberOfUnchangedCases;
	}

	public Map<String, String> getaNoToChangeMade() {
		return aNoToChangeMade;
	}

	public void setaNoToChangeMade(Map<String, String> aNoToChangeMade) {
		this.aNoToChangeMade = aNoToChangeMade;
	}

}
