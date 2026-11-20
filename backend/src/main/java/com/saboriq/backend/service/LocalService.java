package com.saboriq.backend.service;

import com.saboriq.backend.dto.LocalRequest;
import com.saboriq.backend.dto.LocalResponse;
import com.saboriq.backend.entity.Local;
import com.saboriq.backend.entity.Marca;
import com.saboriq.backend.exception.OperacionNoPermitidaException;
import com.saboriq.backend.exception.RecursoDuplicadoException;
import com.saboriq.backend.exception.RecursoNoEncontradoException;
import com.saboriq.backend.repository.LocalRepository;
import com.saboriq.backend.repository.MarcaRepository;
import com.saboriq.backend.repository.MesaRepository;
import com.saboriq.backend.repository.RepartidorRepository;
import com.saboriq.backend.repository.UsuarioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LocalService {

    private final LocalRepository localRepository;
    private final MarcaRepository marcaRepository;
    private final MesaRepository mesaRepository;
    private final RepartidorRepository repartidorRepository;
    private final UsuarioRepository usuarioRepository;

    public LocalService(
            LocalRepository localRepository,
            MarcaRepository marcaRepository,
            MesaRepository mesaRepository,
            RepartidorRepository repartidorRepository,
            UsuarioRepository usuarioRepository) {

        this.localRepository = localRepository;
        this.marcaRepository = marcaRepository;
        this.mesaRepository = mesaRepository;
        this.repartidorRepository = repartidorRepository;
        this.usuarioRepository = usuarioRepository;
    }

    private Local buscarEntidad(Long id) {
        return localRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Local no encontrado con ID: " + id));
    }

    private Marca buscarMarca(Long id) {
        return marcaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Marca no encontrada con ID: " + id));
    }

    private LocalResponse convertir(Local l) {
        return new LocalResponse(
                l.getId(),
                l.getMarca().getId(),
                l.getMarca().getNombre(),
                l.getNombre(),
                l.getDireccion(),
                l.getDistrito(),
                l.getCiudad(),
                l.getTelefono(),
                l.getAforo(),
                l.getPedidoMinimo(),
                l.getCostoEnvio(),
                l.getActivo(),
                l.getDistritosCobertura());
    }

    private List<LocalResponse> convertirLista(List<Local> locales) {
        return locales.stream()
                .map(this::convertir)
                .toList();
    }

    private void copiarDatos(Local local, LocalRequest request, Marca marca) {
        local.setMarca(marca);
        local.setNombre(request.nombre().trim());
        local.setDireccion(request.direccion().trim());
        local.setDistrito(request.distrito().trim());
        local.setCiudad(request.ciudad().trim());
        local.setTelefono(request.telefono());
        local.setAforo(request.aforo());
        local.setPedidoMinimo(request.pedidoMinimo());
        local.setCostoEnvio(request.costoEnvio());
        local.setActivo(request.activo());
        String cob = request.distritosCobertura();
        local.setDistritosCobertura(cob == null || cob.isBlank() ? null : cob.trim());
    }

    @Transactional(readOnly = true)
    public List<LocalResponse> listar() {
        return convertirLista(localRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<LocalResponse> listarPorMarca(Long marcaId) {
        buscarMarca(marcaId);
        return convertirLista(localRepository.findByMarcaId(marcaId));
    }

    @Transactional(readOnly = true)
    public LocalResponse buscarPorId(Long id) {
        return convertir(buscarEntidad(id));
    }

    @Transactional
    public LocalResponse guardar(LocalRequest request) {

        Marca marca = buscarMarca(request.marcaId());
        String nombre = request.nombre().trim();

        if (localRepository.existsByNombreIgnoreCaseAndMarcaId(
                nombre, marca.getId())) {
            throw new RecursoDuplicadoException(
                    "Ya existe un local con ese nombre en esa marca");
        }

        Local local = new Local();
        copiarDatos(local, request, marca);

        return convertir(localRepository.save(local));
    }

    @Transactional
    public LocalResponse actualizar(Long id, LocalRequest request) {

        Local local = buscarEntidad(id);
        Marca marca = buscarMarca(request.marcaId());
        String nombre = request.nombre().trim();

        if (localRepository.existsByNombreIgnoreCaseAndMarcaIdAndIdNot(
                nombre, marca.getId(), id)) {
            throw new RecursoDuplicadoException(
                    "Ya existe un local con ese nombre en esa marca");
        }

        copiarDatos(local, request, marca);

        return convertir(localRepository.save(local));
    }

    @Transactional
    public void eliminar(Long id) {

        Local local = buscarEntidad(id);

        if (mesaRepository.existsByLocalId(id)) {
            throw new OperacionNoPermitidaException(
                    "No se puede eliminar el local porque tiene mesas asociadas");
        }

        if (repartidorRepository.existsByLocalId(id)) {
            throw new OperacionNoPermitidaException(
                    "No se puede eliminar el local porque tiene repartidores asociados");
        }

        if (usuarioRepository.existsByLocalId(id)) {
            throw new OperacionNoPermitidaException(
                    "No se puede eliminar el local porque tiene personal asignado");
        }

        localRepository.delete(local);
    }
}