package gov.gao.epds.dto;

import java.io.Serializable;

public class InputValidationError implements Serializable {

	
	private static final long serialVersionUID = -3966719162477317953L;
	
	private Object fieldName;
	private Object invalidValue;
	private Object message;
	
	public Object getFieldName() {
		return fieldName;
	}
	public void setFieldName(Object fieldName) {
		this.fieldName = fieldName;
	}
	public Object getInvalidValue() {
		return invalidValue;
	}
	public void setInvalidValue(Object invalidValue) {
		this.invalidValue = invalidValue;
	}
	public Object getMessage() {
		return message;
	}
	public void setMessage(Object message) {
		this.message = message;
	}
	
	
	

}
