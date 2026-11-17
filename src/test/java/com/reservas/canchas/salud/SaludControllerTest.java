package com.reservas.canchas.salud;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SaludController.class)
class SaludControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void obtieneEstadoSalud() throws Exception {
		mockMvc.perform(get("/salud"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.estado").value("ok"));
	}
}
