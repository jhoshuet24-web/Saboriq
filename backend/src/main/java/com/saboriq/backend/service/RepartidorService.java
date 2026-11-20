package com.saboriq.backend.service;

import com.saboriq.backend.dto.RepartidorRequest;
import com.saboriq.backend.dto.RepartidorResponse;
import com.saboriq.backend.entity.Local;
import com.saboriq.backend.entity.Repartidor;
import com.saboriq.backend.exception.RecursoDuplicadoException;
import com.saboriq.backend.exception.RecursoNoEncontradoException;
import com.saboriq.backend.repository.LocalRepository;
import com.saboriq.backend.repository.RepartidorRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RepartidorService {

    private final RepartidorRepository repartidorRepository;
    private final LocalRepository localRepository;

    public RepartidorService(
            RepartidorRepository repartidorRepository,
            LocalRepository localRepository) {

        this.repartidorRepository = repartidorRepository;
        this.localRepository = localRepository;
    }

    private Repartidor buscarEntidad(Long id) {
        return repartidorRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Repartidor no encontrado con ID: " + id));
    }

    private Local buscarLocal(Long id) {
        return localRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Local no encontrado con ID: " + id));
    }

    private RepartidorResponse convertir(Repartidor r) {
        return new RepartidorResponse(
                r.getId(),
                r.getLocal().getId(),
                r.getLocal().getNombre(),
                r.getNombre(),
                r.getTelefono(),
                r.getVehiculo(),
                r.getActivo()
        );
    }

    private List<RepartidorResponse> convertirLista(
            List<Repartidor> repartidores) {

        return repartidores.stream()
                .map(this::convertir)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RepartidorResponse> listar() {
        return convertirLista(repartidorRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<RepartidorResponse> listarPorLocal(Long localId) {
        buscarLocal(localId);
        return convertirLista(repartidorRepository.findByLocalId(localId));
    }

    @Transactional(readOnly = true)
    public RepartidorResponse buscarPorId(Long id) {
        return convertir(buscarEntidad(id));
    }

    @Transactional
    public RepartidorResponse guardar(RepartidorRequest request) {

        Local local = buscarLocal(request.localId());

        if (repartidorRepository.existsByTelefono(request.telefono())) {
            throw new RecursoDuplicadoException(
                    "Ya existe un repartidor con ese telefono");
        }

        Repartidor repartidor = new Repartidor();
        repartidor.setLocal(local);
        repartidor.setNombre(request.nombre().trim());
        repartidor.setTelefono(request.telefono());
        repartidor.setVehiculo(request.vehiculo());
        repartidor.setActivo(request.activo());

        return convertir(repartidorRepository.save(repartidor));
    }

    @Transactional
    public RepartidorResponse actualizar(
            Long id, RepartidorRequest request) {

        Repartidor repartidor = buscarEntidad(id);
        Local local = buscarLocal(request.localId());

        if (repartidorRepository.existsByTelefonoAndIdNot(
                request.telefono(), id)) {
            throw new RecursoDuplicadoException(
                    "Ya existe un repartidor con ese telefono");
        }

        repartidor.setLocal(local);
        repartidor.setNombre(request.nombre().trim());
        repartidor.setTelefono(request.telefono());
        repartidor.setVehiculo(request.vehiculo());
        repartidor.setActivo(request.activo());

        return convertir(repartidorRepository.save(repartidor));
    }

    @Transactional
    public void eliminar(Long id) {
        Repartidor repartidor = buscarEntidad(id);
        repartidorRepository.delete(repartidor);
    }
}
