package com.erp.pdv.dto.exceptions;

public class FieldMessage {
	/*
	 * Classe responsável por customizar as mensagens
	 * de exceção das anotações do productDTO
	 */

	private String fieldName;
	private String message;

	public FieldMessage(String fieldName, String message) {
		this.fieldName = fieldName;
		this.message = message;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getMessage() {
		return message;
	}

}
