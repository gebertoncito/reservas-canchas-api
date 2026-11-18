package com.reservas.canchas.reserva;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

	@Query("""
			select case when count(r) > 0 then true else false end
			from Reserva r
			where r.cancha.id = :canchaId
			  and r.estado = com.reservas.canchas.reserva.EstadoReserva.CONFIRMADA
			  and r.fechaInicio < :fechaFin
			  and r.fechaFin > :fechaInicio
			""")
	boolean existeTraslape(
			@Param("canchaId") Long canchaId,
			@Param("fechaInicio") LocalDateTime fechaInicio,
			@Param("fechaFin") LocalDateTime fechaFin);

	List<Reserva> findByCanchaIdOrderByFechaInicioAsc(Long canchaId);
}
