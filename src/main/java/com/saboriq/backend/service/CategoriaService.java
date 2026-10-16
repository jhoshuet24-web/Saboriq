
package com.saboriq.backend.service;

import com.saboriq.backend.dto.CategoriaRequest;
import com.saboriq.backend.dto.CategoriaResponse;
import com.saboriq.backend.entity.Categoria;
import com.saboriq.backend.exception.RecursoNoEncontradoException;
import com.saboriq.backend.repository.CategoriaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository repository;

    public CategoriaService(CategoriaRepository repository) {
        this.repository = repository;
    }

    private CategoriaResponse convertirRespuesta(Categoria categoria) {
        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getDescripcion()
        );
    }

    private Categoria buscarEntidad(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Categoria no encontrada con ID: " + id
                        )
                );
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
        Categoria categoria = new Categoria(
                request.nombre(),
                request.descripcion()
        );

        return convertirRespuesta(repository.save(categoria));
    }

    @Transactional
    public CategoriaResponse actualizar(
            Long id, CategoriaRequest request) {

        Categoria categoria = buscarEntidad(id);

        categoria.setNombre(request.nombre());
        categoria.setDescripcion(request.descripcion());

        return convertirRespuesta(repository.save(categoria));
    }

    @Transactional
    public void eliminar(Long id) {
        Categoria categoria = buscarEntidad(id);
        repository.delete(categoria);
    }
}
