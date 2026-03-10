package com.erp.pdv.controllers.handlers;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.erp.pdv.dto.exceptions.CustomError;
import com.erp.pdv.dto.exceptions.ValidationError;
import com.erp.pdv.service.exceptions.DatabaseException;
import com.erp.pdv.service.exceptions.ResourceNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ControllerExceptionHandler {

  // classe responsavel por tratar exceções de validação

  /*
   * metodo que vai tratar a exceção ResourceNotFoundException
   * de recurso não encontrado
   * httpServlet- pega a url que deu a exceção para tratar
   * status foi convertido
   */
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<CustomError> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {
    HttpStatus status = HttpStatus.NOT_FOUND;
    // chamo CustomErro passando os atributos obtidos na URL
    CustomError err = new CustomError(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
    return ResponseEntity.status(status).body(err);

  }

  /*
   * metodo que trata da exceção DatabaseException
   * de erro de integridade
   * 
   */
  @ExceptionHandler(DatabaseException.class)
  public ResponseEntity<CustomError> database(DatabaseException e, HttpServletRequest request) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    CustomError err = new CustomError(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
    return ResponseEntity.status(status).body(err);

  }

  /*
   * trata a exceções da anotações do productDto
   * 
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<CustomError> methodArgumentNotValid(MethodArgumentNotValidException e,
      HttpServletRequest request) {
    HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
    ValidationError err = new ValidationError(Instant.now(), status.value(), "Dados inválidos",
        request.getRequestURI());
    /*
     * aqui percorro todos os erros que estiverem
     * na lista de exceção
     */
    for (FieldError f : e.getBindingResult().getFieldErrors()) {
      err.addError(f.getField(), f.getDefaultMessage());
    }
    return ResponseEntity.status(status).body(err);

  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<CustomError> handleIllegalState(
      IllegalStateException e,
      HttpServletRequest request) {

    HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;

    CustomError err = new CustomError(
        Instant.now(),
        status.value(),
        e.getMessage(),
        request.getRequestURI());

    return ResponseEntity.status(status).body(err);
  }

}
