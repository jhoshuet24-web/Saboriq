package com.saboriq.backend.service;

import com.saboriq.backend.dto.MesaRequest;
import com.saboriq.backend.dto.MesaResponse;
import com.saboriq.backend.entity.Local;
import com.saboriq.backend.entity.Mesa;
import com.saboriq.backend.exception.RecursoDuplicadoException;
import com.saboriq.backend.exception.RecursoNoEncontradoException;
import com.saboriq.backend.repository.LocalRepository;
import com.saboriq.backend.repository.MesaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MesaService {

    private final MesaRepository mesaRepository;
    private final LocalRepository localRepository;

    public MesaService(
            MesaRepository mesaRepository,
            LocalRepository localRepository) {

        this.mesaRepository = mesaRepository;
        this.localRepository = localRepository;
    }

    private Mesa buscarEntidad(Long id) {
        return mesaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Mesa no encontrada con ID: " + id));
    }

    private Local buscarLocal(Long id) {
        return localRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Local no encontrado con ID: " + id));
    }

    private MesaResponse convertir(Mesa m) {
        return new MesaResponse(
                m.getId(),
                m.getLocal().getId(),
                m.getLocal().getNombre(),
                m.getNumero(),
                m.getCapacidad(),
                m.getActiva());
    }

    private List<MesaResponse> convertirLista(List<Mesa> mesas) {
        return mesas.stream()
                .map(this::convertir)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MesaResponse> listar() {
        return convertirLista(mesaRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<MesaResponse> listarPorLocal(Long localId) {
        buscarLocal(localId);
        return convertirLista(mesaRepository.findByLocalId(localId));
    }

    @Transactional(readOnly = true)
    public MesaResponse buscarPorId(Long id) {
        return convertir(buscarEntidad(id));
    }

    @Transactional
    public MesaResponse guardar(MesaRequest request) {

        Local local = buscarLocal(request.localId());

        if (mesaRepository.existsByLocalIdAndNumero(
                request.localId(), request.numero())) {
            throw new RecursoDuplicadoException(
                    "Ya existe la mesa " + request.numero()
                            + " en ese local");
        }

        Mesa mesa = new Mesa();
        mesa.setLocal(local);
        mesa.setNumero(request.numero());
        mesa.setCapacidad(request.capacidad());
        mesa.setActiva(request.activa());

        return convertir(mesaRepository.save(mesa));
    }

    @Transactional
    public MesaResponse actualizar(Long id, MesaRequest request) {

        Mesa mesa = buscarEntidad(id);
        Local local = buscarLocal(request.localId());

        if (mesaRepository.existsByLocalIdAndNumeroAndIdNot(
                request.localId(), request.numero(), id)) {
            throw new RecursoDuplicadoException(
                    "Ya existe la mesa " + request.numero()
                            + " en ese local");
        }

        mesa.setLocal(local);
        mesa.setNumero(request.numero());
        mesa.setCapacidad(request.capacidad());
        mesa.setActiva(request.activa());

        return convertir(mesaRepository.save(mesa));
    }

    @Transactional
    public void eliminar(Long id) {
        Mesa mesa = buscarEntidad(id);
        mesaRepository.delete(mesa);
    }
}