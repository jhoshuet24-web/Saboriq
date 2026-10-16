
package com.saboriq.backend.controller;

import com.saboriq.backend.dto.*;
import com.saboriq.backend.service.ProductoService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProductoResponse> listar() {
        return service.listar();
    }

    @GetMapping("/buscar")
    public List<ProductoResponse> buscar(
            @RequestParam String nombre) {
        return service.buscarPorNombre(nombre);
    }

    @GetMapping("/categoria/{id}")
    public List<ProductoResponse> porCategoria(
            @PathVariable Long id) {
        return service.buscarPorCategoria(id);
    }

    @GetMapping("/disponibles")
    public List<ProductoResponse> disponibles() {
        return service.listarDisponibles();
    }

    @GetMapping("/precio")
    public List<ProductoResponse> porPrecio(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        return service.buscarPorPrecio(min, max);
    }

    @GetMapping("/{id}")
    public ProductoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoResponse guardar(
            @Valid @RequestBody ProductoRequest request) {
        return service.guardar(request);
    }

    @PutMapping("/{id}")
    public ProductoResponse actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequest request) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }

    @PatchMapping("/{id}/disponibilidad")
    public ProductoResponse disponibilidad(
            @PathVariable Long id,
            @Valid @RequestBody DisponibilidadRequest request) {
        return service.cambiarDisponibilidad(
                id, request.disponible());
    }
}
