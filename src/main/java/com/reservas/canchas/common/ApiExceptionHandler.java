package com.reservas.canchas.common;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

	@Override
	protected org.springframework.http.ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException exception,
			HttpHeaders headers,
			HttpStatusCode status,
			WebRequest request) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(
				HttpStatus.UNPROCESSABLE_ENTITY,
				"Uno o más campos tienen valores inválidos");
		problem.setTitle("Error de validación");

		Map<String, String> errores = new LinkedHashMap<>();
		exception.getBindingResult().getFieldErrors()
				.forEach(error -> errores.put(error.getField(), error.getDefaultMessage()));
		problem.setProperty("errors", errores);
		return handleExceptionInternal(
				exception,
				problem,
				headers,
				HttpStatus.UNPROCESSABLE_ENTITY,
				request);
	}

	@Override
	protected org.springframework.http.ResponseEntity<Object> handleHttpMessageNotReadable(
			HttpMessageNotReadableException exception,
			HttpHeaders headers,
			HttpStatusCode status,
			WebRequest request) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(
				HttpStatus.UNPROCESSABLE_ENTITY,
				"El cuerpo JSON no es válido");
		problem.setTitle("Error de validación");
		return handleExceptionInternal(
				exception,
				problem,
				headers,
				HttpStatus.UNPROCESSABLE_ENTITY,
				request);
	}

	@ExceptionHandler(ValidationException.class)
	public ProblemDetail manejarReglaDeValidacion(ValidationException exception) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(
				HttpStatus.UNPROCESSABLE_ENTITY,
				exception.getMessage());
		problem.setTitle("Error de validación");
		return problem;
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ProblemDetail manejarNoEncontrado(ResourceNotFoundException exception) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(
				HttpStatus.NOT_FOUND,
				exception.getMessage());
		problem.setTitle("Recurso no encontrado");
		return problem;
	}

	@ExceptionHandler(ConflictException.class)
	public ProblemDetail manejarConflicto(ConflictException exception) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(
				HttpStatus.CONFLICT,
				exception.getMessage());
		problem.setTitle("Conflicto");
		return problem;
	}

	@ExceptionHandler(Exception.class)
	public ProblemDetail manejarErrorInesperado(Exception exception) {
		LOGGER.error("Error inesperado al procesar la solicitud", exception);
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(
				HttpStatus.INTERNAL_SERVER_ERROR,
				"Ocurrió un error inesperado");
		problem.setTitle("Error interno del servidor");
		return problem;
	}
}
