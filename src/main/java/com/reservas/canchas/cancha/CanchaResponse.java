package com.reservas.canchas.cancha;

import java.math.BigDecimal;

public record CanchaResponse(
		Long id,
		String nombre,
		String tipoDeporte,
		BigDecimal precioPorHora,
		boolean activa) {

	public static CanchaResponse from(Cancha cancha) {
		return new CanchaResponse(
				cancha.getId(),
				cancha.getNombre(),
				cancha.getTipoDeporte(),
				cancha.getPrecioPorHora(),
				cancha.isActiva());
	}
}
