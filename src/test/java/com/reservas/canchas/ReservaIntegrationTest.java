package com.reservas.canchas;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class ReservaIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void flujoReservarTraslaparCancelarYReservar() throws Exception {
		MvcResult canchaResult = mockMvc.perform(post("/canchas")
						.contentType("application/json")
						.content("""
								{"nombre":"Central","tipoDeporte":"Fútbol","precioPorHora":100.00}
								"""))
				.andExpect(status().isCreated())
				.andReturn();

		JsonNode cancha = objectMapper.readTree(canchaResult.getResponse().getContentAsString());
		long canchaId = cancha.get("id").asLong();

		String primeraReserva = """
				{"canchaId":%d,"nombreCliente":"Ana","fechaInicio":"2026-11-18T10:00:00","fechaFin":"2026-11-18T11:00:00"}
				""".formatted(canchaId);
		MvcResult reservaResult = mockMvc.perform(post("/reservas")
						.contentType("application/json")
						.content(primeraReserva))
				.andExpect(status().isCreated())
				.andReturn();

		long reservaId = objectMapper.readTree(reservaResult.getResponse().getContentAsString())
				.get("id").asLong();

		mockMvc.perform(post("/reservas")
						.contentType("application/json")
						.content("""
								{"canchaId":%d,"nombreCliente":"Luis","fechaInicio":"2026-11-18T10:30:00","fechaFin":"2026-11-18T11:30:00"}
								""".formatted(canchaId)))
				.andExpect(status().isConflict());

		mockMvc.perform(post("/reservas/" + reservaId + "/cancelar"))
				.andExpect(status().isOk());

		mockMvc.perform(post("/reservas")
						.contentType("application/json")
						.content("""
								{"canchaId":%d,"nombreCliente":"Luis","fechaInicio":"2026-11-18T10:30:00","fechaFin":"2026-11-18T11:30:00"}
								""".formatted(canchaId)))
				.andExpect(status().isCreated());
	}
}
