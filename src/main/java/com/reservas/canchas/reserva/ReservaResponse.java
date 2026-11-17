package com.reservas.canchas.reserva;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReservaResponse(
		Long id,
		Long canchaId,
		String nombreCliente,
		LocalDateTime fechaInicio,
		LocalDateTime fechaFin,
		EstadoReserva estado,
		BigDecimal precioTotal) {

	public static ReservaResponse from(Reserva reserva) {
		return new ReservaResponse(
				reserva.getId(),
				reserva.getCancha().getId(),
				reserva.getNombreCliente(),
				reserva.getFechaInicio(),
				reserva.getFechaFin(),
				reserva.getEstado(),
				reserva.getPrecioTotal());
	}
}
