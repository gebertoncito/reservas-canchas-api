package com.reservas.canchas.reserva;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.reservas.canchas.cancha.Cancha;

@Entity
@Table(
		name = "reservas",
		indexes = @Index(
				name = "idx_reserva_cancha_fechas",
				columnList = "cancha_id, fecha_inicio, fecha_fin"))
public class Reserva {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "cancha_id", nullable = false)
	private Cancha cancha;

	@NotBlank
	@Size(max = 120)
	@Column(name = "nombre_cliente", nullable = false, length = 120)
	private String nombreCliente;

	@Column(name = "fecha_inicio", nullable = false)
	private LocalDateTime fechaInicio;

	@Column(name = "fecha_fin", nullable = false)
	private LocalDateTime fechaFin;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private EstadoReserva estado;

	@Column(name = "precio_total", nullable = false, precision = 12, scale = 2)
	private BigDecimal precioTotal;

	protected Reserva() {
	}

	public Reserva(
			Cancha cancha,
			String nombreCliente,
			LocalDateTime fechaInicio,
			LocalDateTime fechaFin,
			BigDecimal precioTotal) {
		this.cancha = cancha;
		this.nombreCliente = nombreCliente;
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
		this.estado = EstadoReserva.CONFIRMADA;
		this.precioTotal = precioTotal.setScale(2);
	}

	public Long getId() {
		return id;
	}

	public Cancha getCancha() {
		return cancha;
	}

	public String getNombreCliente() {
		return nombreCliente;
	}

	public LocalDateTime getFechaInicio() {
		return fechaInicio;
	}

	public LocalDateTime getFechaFin() {
		return fechaFin;
	}

	public EstadoReserva getEstado() {
		return estado;
	}

	public BigDecimal getPrecioTotal() {
		return precioTotal;
	}

	public void cancelar() {
		this.estado = EstadoReserva.CANCELADA;
	}
}
