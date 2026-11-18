package com.reservas.canchas.reserva;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.reservas.canchas.cancha.Cancha;
import com.reservas.canchas.cancha.CanchaRepository;
import com.reservas.canchas.common.ConflictException;
import com.reservas.canchas.common.ResourceNotFoundException;
import com.reservas.canchas.common.ValidationException;

class ReservaServiceTest {

	private ReservaRepository reservaRepository;
	private CanchaRepository canchaRepository;
	private ReservaService service;
	private Cancha cancha;

	@BeforeEach
	void configurar() {
		reservaRepository = mock(ReservaRepository.class);
		canchaRepository = mock(CanchaRepository.class);
		service = new ReservaService(reservaRepository, canchaRepository);
		cancha = new Cancha("Central", "Fútbol", new BigDecimal("100.00"));
		setId(cancha, 1L);
		when(canchaRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(cancha));
	}

	@Test
	void creaReservaValida() {
		LocalDateTime inicio = LocalDateTime.of(2026, 11, 18, 10, 0);
		LocalDateTime fin = inicio.plusHours(1);
		when(reservaRepository.existeTraslape(1L, inicio, fin)).thenReturn(false);
		when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> invocation.getArgument(0));

		ReservaResponse response = service.crear(new ReservaRequest(1L, "Ana", inicio, fin));

		assertEquals(EstadoReserva.CONFIRMADA, response.estado());
		assertEquals(new BigDecimal("100.00"), response.precioTotal());
		verify(reservaRepository).save(any(Reserva.class));
	}

	@Test
	void rechazaFechaFinAntesOIgual() {
		LocalDateTime inicio = LocalDateTime.of(2026, 11, 18, 10, 0);
		assertThrows(ValidationException.class,
				() -> service.crear(new ReservaRequest(1L, "Ana", inicio, inicio)));
		assertThrows(ValidationException.class,
				() -> service.crear(new ReservaRequest(1L, "Ana", inicio, inicio.minusMinutes(1))));
	}

	@Test
	void rechazaDuracionFueraDeLimites() {
		LocalDateTime inicio = LocalDateTime.of(2026, 11, 18, 10, 0);
		assertThrows(ValidationException.class,
				() -> service.crear(new ReservaRequest(1L, "Ana", inicio, inicio.plusMinutes(29))));
		assertThrows(ValidationException.class,
				() -> service.crear(new ReservaRequest(1L, "Ana", inicio, inicio.plusMinutes(241))));
	}

	@Test
	void rechazaTraslape() {
		LocalDateTime inicio = LocalDateTime.of(2026, 11, 18, 10, 30);
		LocalDateTime fin = inicio.plusHours(1);
		when(reservaRepository.existeTraslape(1L, inicio, fin)).thenReturn(true);

		ConflictException exception = assertThrows(ConflictException.class,
				() -> service.crear(new ReservaRequest(1L, "Ana", inicio, fin)));
		assertEquals("El horario ya está reservado", exception.getMessage());
	}

	@Test
	void aceptaInicioJustoCuandoOtraTermina() {
		LocalDateTime inicio = LocalDateTime.of(2026, 11, 18, 11, 0);
		LocalDateTime fin = inicio.plusHours(1);
		when(reservaRepository.existeTraslape(1L, inicio, fin)).thenReturn(false);
		when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> invocation.getArgument(0));

		ReservaResponse response = service.crear(new ReservaRequest(1L, "Ana", inicio, fin));

		assertEquals(EstadoReserva.CONFIRMADA, response.estado());
	}

	@Test
	void rechazaCanchaInexistenteEInactiva() {
		when(canchaRepository.findByIdForUpdate(99L)).thenReturn(Optional.empty());
		assertThrows(ResourceNotFoundException.class,
				() -> service.crear(new ReservaRequest(99L, "Ana",
						LocalDateTime.of(2026, 11, 18, 10, 0),
						LocalDateTime.of(2026, 11, 18, 11, 0))));

		cancha.setActiva(false);
		assertThrows(ConflictException.class,
				() -> service.crear(new ReservaRequest(1L, "Ana",
						LocalDateTime.of(2026, 11, 18, 10, 0),
						LocalDateTime.of(2026, 11, 18, 11, 0))));
	}

	@Test
	void cancelaConfirmadaYRechazaCancelada() {
		LocalDateTime inicio = LocalDateTime.of(2026, 11, 18, 10, 0);
		Reserva reserva = new Reserva(cancha, "Ana", inicio, inicio.plusHours(1), new BigDecimal("100.00"));
		setReservaId(reserva, 7L);
		when(reservaRepository.findById(7L)).thenReturn(Optional.of(reserva));
		when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> invocation.getArgument(0));

		ReservaResponse cancelada = service.cancelar(7L);
		assertEquals(EstadoReserva.CANCELADA, cancelada.estado());

		ConflictException exception = assertThrows(ConflictException.class, () -> service.cancelar(7L));
		assertEquals("La reserva ya está cancelada", exception.getMessage());
	}

	private void setId(Cancha cancha, Long id) {
		try {
			var field = Cancha.class.getDeclaredField("id");
			field.setAccessible(true);
			field.set(cancha, id);
		} catch (ReflectiveOperationException exception) {
			throw new RuntimeException(exception);
		}
	}

	private void setReservaId(Reserva reserva, Long id) {
		try {
			var field = Reserva.class.getDeclaredField("id");
			field.setAccessible(true);
			field.set(reserva, id);
		} catch (ReflectiveOperationException exception) {
			throw new RuntimeException(exception);
		}
	}
}
