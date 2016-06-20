package com.xiaoaitouch.mom.module;

public class SymptomBean {
	private String symptom;
	private int isOk;
	private long symptomId;
	

	public long getSymptomId() {
		return symptomId;
	}

	public void setSymptomId(long symptomId) {
		this.symptomId = symptomId;
	}

	/**
	 * @return the symptom
	 */
	public String getSymptom() {
		return symptom;
	}

	/**
	 * @param symptom
	 *            the symptom to set
	 */
	public void setSymptom(String symptom) {
		this.symptom = symptom;
	}

	/**
	 * @return the isOk
	 */
	public int getIsOk() {
		return isOk;
	}

	/**
	 * @param isOk
	 *            the isOk to set
	 */
	public void setIsOk(int isOk) {
		this.isOk = isOk;
	}

}
