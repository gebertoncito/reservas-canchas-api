package com.reservas.canchas.reserva;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReservaRequest(
		@NotNull Long canchaId,
		@NotBlank @Size(max = 120) String nombreCliente,
		@NotNull LocalDateTime fechaInicio,
		@NotNull LocalDateTime fechaFin) {
}
