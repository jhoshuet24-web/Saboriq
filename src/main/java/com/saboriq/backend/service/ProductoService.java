
package com.saboriq.backend.service;

import com.saboriq.backend.dto.*;
import com.saboriq.backend.entity.*;
import com.saboriq.backend.exception.RecursoNoEncontradoException;
import com.saboriq.backend.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoService(
            ProductoRepository productoRepository,
            CategoriaRepository categoriaRepository) {

        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    private Producto buscarEntidad(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Producto no encontrado: " + id));
    }

    private ProductoResponse convertir(Producto p) {
        return new ProductoResponse(
                p.getId(),
                p.getNombre(),
                p.getDescripcion(),
                p.getPrecio(),
                p.getDisponible(),
                p.getCategoria().getId(),
                p.getCategoria().getNombre()
        );
    }

    private List<ProductoResponse> convertirLista(
            List<Producto> productos) {

        return productos.stream()
                .map(this::convertir)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listar() {
        return convertirLista(productoRepository.findAll());
    }

    @Transactional(readOnly = true)
    public ProductoResponse buscarPorId(Long id) {
        return convertir(buscarEntidad(id));
    }

    @Transactional
    public ProductoResponse guardar(ProductoRequest request) {

        Categoria categoria = categoriaRepository
                .findById(request.categoriaId())
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Categoria no encontrada"));

        Producto producto = new Producto();

        producto.setNombre(request.nombre());
        producto.setDescripcion(request.descripcion());
        producto.setPrecio(request.precio());
        producto.setDisponible(request.disponible());
        producto.setCategoria(categoria);

        return convertir(productoRepository.save(producto));
    }

    @Transactional
    public ProductoResponse actualizar(
            Long id, ProductoRequest request) {

        Producto producto = buscarEntidad(id);

        Categoria categoria = categoriaRepository
                .findById(request.categoriaId())
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Categoria no encontrada"));

        producto.setNombre(request.nombre());
        producto.setDescripcion(request.descripcion());
        producto.setPrecio(request.precio());
        producto.setDisponible(request.disponible());
        producto.setCategoria(categoria);

        return convertir(productoRepository.save(producto));
    }

    @Transactional
    public void eliminar(Long id) {
        Producto producto = buscarEntidad(id);
        productoRepository.delete(producto);
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> buscarPorNombre(String nombre) {
        return convertirLista(
                productoRepository.buscarPorNombre(nombre));
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> buscarPorCategoria(Long id) {

        if (!categoriaRepository.existsById(id)) {
            throw new RecursoNoEncontradoException(
                    "Categoria no encontrada");
        }

        return convertirLista(
                productoRepository.findByCategoriaId(id));
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listarDisponibles() {
        return convertirLista(
                productoRepository.findByDisponibleTrue());
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> buscarPorPrecio(
            BigDecimal min, BigDecimal max) {

        if (min == null || max == null
                || min.signum() < 0
                || min.compareTo(max) > 0) {
            throw new IllegalArgumentException(
                    "El rango de precios es invalido");
        }

        return convertirLista(
                productoRepository.buscarPorRangoPrecio(min, max));
    }

    @Transactional
    public ProductoResponse cambiarDisponibilidad(
            Long id, Boolean disponible) {

        Producto producto = buscarEntidad(id);
        producto.setDisponible(disponible);

        return convertir(producto);
    }
}
