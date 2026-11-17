package com.reservas.canchas.reserva;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reservas.canchas.cancha.Cancha;
import com.reservas.canchas.cancha.CanchaRepository;
import com.reservas.canchas.common.ConflictException;
import com.reservas.canchas.common.ResourceNotFoundException;
import com.reservas.canchas.common.ValidationException;

@Service
public class ReservaService {

	private final ReservaRepository reservaRepository;
	private final CanchaRepository canchaRepository;

	public ReservaService(
			ReservaRepository reservaRepository,
			CanchaRepository canchaRepository) {
		this.reservaRepository = reservaRepository;
		this.canchaRepository = canchaRepository;
	}

	@Transactional
	public ReservaResponse crear(ReservaRequest request) {
		Cancha cancha = canchaRepository.findByIdForUpdate(request.canchaId())
				.orElseThrow(() -> new ResourceNotFoundException("Cancha no encontrada"));

		validarFechas(request.fechaInicio(), request.fechaFin());

		if (!cancha.isActiva()) {
			throw new ConflictException("La cancha no está disponible");
		}

		if (reservaRepository.existeTraslape(
				request.canchaId(),
				request.fechaInicio(),
				request.fechaFin())) {
			throw new ConflictException("El horario ya está reservado");
		}

		BigDecimal precioTotal = calcularPrecioTotal(
				cancha.getPrecioPorHora(),
				request.fechaInicio(),
				request.fechaFin());

		Reserva reserva = new Reserva(
				cancha,
				request.nombreCliente(),
				request.fechaInicio(),
				request.fechaFin(),
				precioTotal);
		return ReservaResponse.from(reservaRepository.save(reserva));
	}

	@Transactional(readOnly = true)
	public List<ReservaResponse> listarPorCancha(Long canchaId) {
		if (!canchaRepository.existsById(canchaId)) {
			throw new ResourceNotFoundException("Cancha no encontrada");
		}
		return reservaRepository.findByCanchaIdOrderByFechaInicioAsc(canchaId).stream()
				.map(ReservaResponse::from)
				.toList();
	}

	@Transactional
	public ReservaResponse cancelar(Long id) {
		Reserva reserva = reservaRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

		if (reserva.getEstado() == EstadoReserva.CANCELADA) {
			throw new ConflictException("La reserva ya está cancelada");
		}

		reserva.cancelar();
		return ReservaResponse.from(reservaRepository.save(reserva));
	}

	private void validarFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
		if (fechaFin == null || fechaInicio == null || !fechaFin.isAfter(fechaInicio)) {
			throw new ValidationException("La fechaFin debe ser posterior a fechaInicio");
		}

		long minutos = Duration.between(fechaInicio, fechaFin).toMinutes();
		if (minutos < 30) {
			throw new ValidationException("La reserva debe durar al menos 30 minutos");
		}
		if (minutos > 240) {
			throw new ValidationException("La reserva no puede durar más de 4 horas");
		}
	}

	private BigDecimal calcularPrecioTotal(
			BigDecimal precioPorHora,
			LocalDateTime fechaInicio,
			LocalDateTime fechaFin) {
		long minutos = Duration.between(fechaInicio, fechaFin).toMinutes();
		BigDecimal horas = BigDecimal.valueOf(minutos)
				.divide(BigDecimal.valueOf(60), 4, RoundingMode.HALF_UP);
		return precioPorHora.multiply(horas).setScale(2, RoundingMode.HALF_UP);
	}
}
