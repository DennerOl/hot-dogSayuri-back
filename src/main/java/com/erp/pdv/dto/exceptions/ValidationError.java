package com.erp.pdv.dto.exceptions;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ValidationError extends CustomError {

	/*
	 * Está classe extende da Custom e cria uma lista de erros
	 * das anotações do ProductDto
	 */
	private List<FieldMessage> errors = new ArrayList<>();

	public ValidationError(Instant timestamp, Integer status, String error, String path) {
		super(timestamp, status, error, path);

	}

	public List<FieldMessage> getErrors() {
		return errors;
	}

	/*
	 * metodo que adiciona mensagem a list criada
	 * 
	 */
	public void addError(String fieldName, String message) {
		// remove um erro que ja esteja na mesma lista ou seja nao deixa duplicar
		errors.removeIf(x -> x.getFieldName().equals(fieldName));
		errors.add(new FieldMessage(fieldName, message));
	}
}
