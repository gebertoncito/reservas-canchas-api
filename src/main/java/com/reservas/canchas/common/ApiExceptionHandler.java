package com.reservas.canchas.common;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ProblemDetail manejarValidacion(MethodArgumentNotValidException exception) {
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(
				HttpStatus.UNPROCESSABLE_ENTITY,
				"Uno o más campos tienen valores inválidos");
		problem.setTitle("Error de validación");

		Map<String, String> errores = new LinkedHashMap<>();
		exception.getBindingResult().getFieldErrors()
				.forEach(error -> errores.put(error.getField(), error.getDefaultMessage()));
		problem.setProperty("errors", errores);
		return problem;
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
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(
				HttpStatus.INTERNAL_SERVER_ERROR,
				"Ocurrió un error inesperado");
		problem.setTitle("Error interno del servidor");
		return problem;
	}
}
