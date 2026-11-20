package com.saboriq.backend.service;

import com.saboriq.backend.dto.CategoriaRequest;
import com.saboriq.backend.dto.CategoriaResponse;
import com.saboriq.backend.entity.Categoria;
import com.saboriq.backend.exception.OperacionNoPermitidaException;
import com.saboriq.backend.exception.RecursoDuplicadoException;
import com.saboriq.backend.exception.RecursoNoEncontradoException;
import com.saboriq.backend.repository.CategoriaRepository;
import com.saboriq.backend.repository.ProductoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository repository;
    private final ProductoRepository productoRepository;

    public CategoriaService(
            CategoriaRepository repository,
            ProductoRepository productoRepository) {

        this.repository = repository;
        this.productoRepository = productoRepository;
    }

    private CategoriaResponse convertirRespuesta(Categoria categoria) {
        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getDescripcion());
    }

    private Categoria buscarEntidad(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Categoria no encontrada con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
        return repository.findAll()
                .stream()
                .map(this::convertirRespuesta)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponse buscarPorId(Long id) {
        return convertirRespuesta(buscarEntidad(id));
    }

    @Transactional
    public CategoriaResponse guardar(CategoriaRequest request) {

        String nombre = request.nombre().trim();

        if (repository.existsByNombreIgnoreCase(nombre)) {
            throw new RecursoDuplicadoException(
                    "Ya existe una categoria con ese nombre");
        }

        Categoria categoria = new Categoria(
                nombre,
                request.descripcion());

        return convertirRespuesta(repository.save(categoria));
    }

    @Transactional
    public CategoriaResponse actualizar(
            Long id, CategoriaRequest request) {

        Categoria categoria = buscarEntidad(id);
        String nombre = request.nombre().trim();

        if (repository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new RecursoDuplicadoException(
                    "Ya existe una categoria con ese nombre");
        }

        categoria.setNombre(nombre);
        categoria.setDescripcion(request.descripcion());

        return convertirRespuesta(repository.save(categoria));
    }

    @Transactional
    public void eliminar(Long id) {

        Categoria categoria = buscarEntidad(id);

        if (productoRepository.existsByCategoriaId(id)) {
            throw new OperacionNoPermitidaException(
                    "No se puede eliminar la categoria porque tiene productos asociados");
        }

        repository.delete(categoria);
    }
}
