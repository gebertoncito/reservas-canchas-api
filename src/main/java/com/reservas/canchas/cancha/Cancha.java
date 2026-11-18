package com.reservas.canchas.cancha;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "canchas")
public class Cancha {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Size(max = 100)
	@Column(nullable = false, length = 100)
	private String nombre;

	@NotBlank
	@Size(max = 50)
	@Column(name = "tipo_deporte", nullable = false, length = 50)
	private String tipoDeporte;

	@DecimalMin("0.01")
	@Column(name = "precio_por_hora", nullable = false, precision = 12, scale = 2)
	private BigDecimal precioPorHora;

	@Column(nullable = false)
	private boolean activa = true;

	protected Cancha() {
	}

	public Cancha(String nombre, String tipoDeporte, BigDecimal precioPorHora) {
		this.nombre = nombre;
		this.tipoDeporte = tipoDeporte;
		this.precioPorHora = precioPorHora.setScale(2);
		this.activa = true;
	}

	public Long getId() {
		return id;
	}

	public String getNombre() {
		return nombre;
	}

	public String getTipoDeporte() {
		return tipoDeporte;
	}

	public BigDecimal getPrecioPorHora() {
		return precioPorHora;
	}

	public boolean isActiva() {
		return activa;
	}

	public void setActiva(boolean activa) {
		this.activa = activa;
	}
}
