package com.reservas.canchas.reserva;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@WebMvcTest(ReservaController.class)
class ReservaControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ReservaService service;

	@Test
	void creaReserva() throws Exception {
		when(service.crear(any(ReservaRequest.class))).thenReturn(new ReservaResponse(
				1L,
				1L,
				"Ana",
				LocalDateTime.of(2026, 11, 18, 10, 30),
				LocalDateTime.of(2026, 11, 18, 11, 30),
				EstadoReserva.CONFIRMADA,
				new BigDecimal("100.00")));

		mockMvc.perform(post("/reservas")
						.contentType("application/json")
						.content("""
								{"canchaId":1,"nombreCliente":"Ana","fechaInicio":"2026-11-18T10:30:00","fechaFin":"2026-11-18T11:30:00"}
								"""))
				.andExpect(status().isCreated());
	}

	@Test
	void rechazaValidacion() throws Exception {
		mockMvc.perform(post("/reservas")
						.contentType("application/json")
						.content("""
								{"canchaId":null,"nombreCliente":"","fechaInicio":null,"fechaFin":null}
								"""))
				.andExpect(status().isUnprocessableEntity());
	}

	@Test
	void devuelveConflictoPorTraslape() throws Exception {
		when(service.crear(any(ReservaRequest.class))).thenThrow(
				new com.reservas.canchas.common.ConflictException("El horario ya está reservado"));

		mockMvc.perform(post("/reservas")
						.contentType("application/json")
						.content("""
								{"canchaId":1,"nombreCliente":"Ana","fechaInicio":"2026-11-18T10:30:00","fechaFin":"2026-11-18T11:30:00"}
								"""))
				.andExpect(status().isConflict());
	}

	@Test
	void devuelveConflictoAlCancelar() throws Exception {
		when(service.cancelar(5L)).thenThrow(
				new com.reservas.canchas.common.ConflictException("La reserva ya está cancelada"));

		mockMvc.perform(post("/reservas/5/cancelar"))
				.andExpect(status().isConflict());
	}

	@Test
	void cancelaReserva() throws Exception {
		when(service.cancelar(5L)).thenReturn(new ReservaResponse(
				5L,
				1L,
				"Ana",
				LocalDateTime.of(2026, 11, 18, 10, 30),
				LocalDateTime.of(2026, 11, 18, 11, 30),
				EstadoReserva.CANCELADA,
				new BigDecimal("100.00")));

		mockMvc.perform(post("/reservas/5/cancelar"))
				.andExpect(status().isOk());
	}
}
