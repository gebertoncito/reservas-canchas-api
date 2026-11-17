package com.reservas.canchas.cancha;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CanchaRepository extends JpaRepository<Cancha, Long> {

	List<Cancha> findByActivaTrue();

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select c from Cancha c where c.id = :id")
	Optional<Cancha> findByIdForUpdate(@Param("id") Long id);
}
