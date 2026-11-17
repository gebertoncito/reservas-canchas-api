package com.reservas.canchas.reserva;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

	private final ReservaService service;

	public ReservaController(ReservaService service) {
		this.service = service;
	}

	@PostMapping
	public ResponseEntity<ReservaResponse> crear(@Valid @RequestBody ReservaRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
	}

	@GetMapping
	public List<ReservaResponse> listar(@RequestParam Long canchaId) {
		return service.listarPorCancha(canchaId);
	}

	@PostMapping("/{id}/cancelar")
	public ReservaResponse cancelar(@PathVariable Long id) {
		return service.cancelar(id);
	}
}
