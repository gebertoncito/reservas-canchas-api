package com.reservas.canchas.salud;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SaludController {

	@GetMapping("/salud")
	public Map<String, String> obtenerEstado() {
		return Map.of("estado", "ok");
	}
}
