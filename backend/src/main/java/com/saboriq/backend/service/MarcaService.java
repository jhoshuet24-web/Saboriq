package com.saboriq.backend.service;

import com.saboriq.backend.dto.MarcaRequest;
import com.saboriq.backend.dto.MarcaResponse;
import com.saboriq.backend.entity.Marca;
import com.saboriq.backend.exception.OperacionNoPermitidaException;
import com.saboriq.backend.exception.RecursoDuplicadoException;
import com.saboriq.backend.exception.RecursoNoEncontradoException;
import com.saboriq.backend.repository.LocalRepository;
import com.saboriq.backend.repository.MarcaRepository;
import com.saboriq.backend.repository.ProductoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MarcaService {

    private final MarcaRepository marcaRepository;
    private final LocalRepository localRepository;
    private final ProductoRepository productoRepository;

    public MarcaService(
            MarcaRepository marcaRepository,
            LocalRepository localRepository,
            ProductoRepository productoRepository) {

        this.marcaRepository = marcaRepository;
        this.localRepository = localRepository;
        this.productoRepository = productoRepository;
    }

    private Marca buscarEntidad(Long id) {
        return marcaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Marca no encontrada con ID: " + id));
    }

    private MarcaResponse convertir(Marca marca) {
        return new MarcaResponse(
                marca.getId(),
                marca.getNombre(),
                marca.getRuc(),
                marca.getActiva());
    }

    @Transactional(readOnly = true)
    public List<MarcaResponse> listar() {
        return marcaRepository.findAll()
                .stream()
                .map(this::convertir)
                .toList();
    }

    @Transactional(readOnly = true)
    public MarcaResponse buscarPorId(Long id) {
        return convertir(buscarEntidad(id));
    }

    @Transactional
    public MarcaResponse guardar(MarcaRequest request) {

        String nombre = request.nombre().trim();

        if (marcaRepository.existsByNombreIgnoreCase(nombre)) {
            throw new RecursoDuplicadoException(
                    "Ya existe una marca con ese nombre");
        }

        if (request.ruc() != null
                && marcaRepository.existsByRuc(request.ruc())) {
            throw new RecursoDuplicadoException(
                    "Ya existe una marca con ese RUC");
        }

        Marca marca = new Marca();
        marca.setNombre(nombre);
        marca.setRuc(request.ruc());
        marca.setActiva(request.activa());

        return convertir(marcaRepository.save(marca));
    }

    @Transactional
    public MarcaResponse actualizar(Long id, MarcaRequest request) {

        Marca marca = buscarEntidad(id);
        String nombre = request.nombre().trim();

        if (marcaRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new RecursoDuplicadoException(
                    "Ya existe una marca con ese nombre");
        }

        if (request.ruc() != null
                && marcaRepository.existsByRucAndIdNot(request.ruc(), id)) {
            throw new RecursoDuplicadoException(
                    "Ya existe una marca con ese RUC");
        }

        marca.setNombre(nombre);
        marca.setRuc(request.ruc());
        marca.setActiva(request.activa());

        return convertir(marcaRepository.save(marca));
    }

    @Transactional
    public void eliminar(Long id) {

        Marca marca = buscarEntidad(id);

        if (localRepository.existsByMarcaId(id)) {
            throw new OperacionNoPermitidaException(
                    "No se puede eliminar la marca porque tiene locales asociados");
        }

        if (productoRepository.existsByMarcaId(id)) {
            throw new OperacionNoPermitidaException(
                    "No se puede eliminar la marca porque tiene productos asociados");
        }

        marcaRepository.delete(marca);
    }
}