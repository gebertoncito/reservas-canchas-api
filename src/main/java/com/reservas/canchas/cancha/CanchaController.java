package com.reservas.canchas.cancha;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/canchas")
public class CanchaController {

	private final CanchaService service;

	public CanchaController(CanchaService service) {
		this.service = service;
	}

	@PostMapping
	public ResponseEntity<CanchaResponse> crear(@Valid @RequestBody CanchaRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
	}

	@GetMapping
	public List<CanchaResponse> listar() {
		return service.listarActivas();
	}

	@GetMapping("/{id}")
	public CanchaResponse obtener(@PathVariable Long id) {
		return service.obtenerPorId(id);
	}
}
