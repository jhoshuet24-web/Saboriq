package com.saboriq.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "locales")
public class Local {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "marca_id", nullable = false)
    private Marca marca;
    @Column(nullable = false, length = 100)
    private String nombre;
    @Column(nullable = false, length = 200)
    private String direccion;
    @Column(nullable = false, length = 80)
    private String distrito;
    @Column(nullable = false, length = 80)
    private String ciudad;
    @Column(length = 20)
    private String telefono;
    @Column(nullable = false)
    private Integer aforo;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pedidoMinimo = BigDecimal.ZERO;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal costoEnvio = BigDecimal.ZERO;
    @Column(nullable = false)
    private Boolean activo = true;
    @Column(length = 500)
    private String distritosCobertura;

    public Local() {
    }

    public Long getId() {
        return id;
    }

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Integer getAforo() {
        return aforo;
    }

    public void setAforo(Integer aforo) {
        this.aforo = aforo;
    }

    public BigDecimal getPedidoMinimo() {
        return pedidoMinimo;
    }

    public void setPedidoMinimo(BigDecimal pedidoMinimo) {
        this.pedidoMinimo = pedidoMinimo;
    }

    public BigDecimal getCostoEnvio() {
        return costoEnvio;
    }

    public void setCostoEnvio(BigDecimal costoEnvio) {
        this.costoEnvio = costoEnvio;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getDistritosCobertura() {
        return distritosCobertura;
    }

    public void setDistritosCobertura(String distritosCobertura) {
        this.distritosCobertura = distritosCobertura;
    }
}