package com.reservas.canchas.cancha;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CanchaController.class)
class CanchaControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CanchaService service;

	@Test
	void creaCancha() throws Exception {
		when(service.crear(any(CanchaRequest.class)))
				.thenReturn(new CanchaResponse(1L, "Central", "Fútbol", new BigDecimal("100.00"), true));

		mockMvc.perform(post("/canchas")
						.contentType("application/json")
						.content("""
								{"nombre":"Central","tipoDeporte":"Fútbol","precioPorHora":100.00}
								"""))
				.andExpect(status().isCreated());
	}

	@Test
	void rechazaValidacion() throws Exception {
		mockMvc.perform(post("/canchas")
						.contentType("application/json")
						.content("""
								{"nombre":"","tipoDeporte":"","precioPorHora":0}
								"""))
				.andExpect(status().isUnprocessableEntity());
	}

	@Test
	void devuelveNoEncontrada() throws Exception {
		when(service.obtenerPorId(99L))
				.thenThrow(new com.reservas.canchas.common.ResourceNotFoundException("Cancha no encontrada"));

		mockMvc.perform(get("/canchas/99"))
				.andExpect(status().isNotFound());
	}

	@Test
	void listaCanchas() throws Exception {
		mockMvc.perform(get("/canchas"))
				.andExpect(status().isOk());
	}
}
