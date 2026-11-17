package com.reservas.canchas.cancha;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CanchaRequest(
		@NotBlank @Size(max = 100) String nombre,
		@NotBlank @Size(max = 50) String tipoDeporte,
		@DecimalMin("0.01") @Digits(integer = 10, fraction = 2) BigDecimal precioPorHora) {
}
