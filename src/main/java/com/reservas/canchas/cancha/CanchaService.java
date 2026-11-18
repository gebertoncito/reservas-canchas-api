package com.reservas.canchas.cancha;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reservas.canchas.common.ResourceNotFoundException;

@Service
public class CanchaService {

	private final CanchaRepository repository;

	public CanchaService(CanchaRepository repository) {
		this.repository = repository;
	}

	@Transactional
	public CanchaResponse crear(CanchaRequest request) {
		Cancha cancha = new Cancha(
				request.nombre(),
				request.tipoDeporte(),
				request.precioPorHora());
		return CanchaResponse.from(repository.save(cancha));
	}

	@Transactional(readOnly = true)
	public List<CanchaResponse> listarActivas() {
		return repository.findByActivaTrue().stream()
				.map(CanchaResponse::from)
				.toList();
	}

	@Transactional(readOnly = true)
	public CanchaResponse obtenerPorId(Long id) {
		Cancha cancha = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Cancha no encontrada"));
		return CanchaResponse.from(cancha);
	}
}
