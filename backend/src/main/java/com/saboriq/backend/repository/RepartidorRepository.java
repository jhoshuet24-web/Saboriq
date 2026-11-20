package com.saboriq.backend.repository;

import com.saboriq.backend.entity.Repartidor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepartidorRepository
        extends JpaRepository<Repartidor, Long> {
    List<Repartidor> findByLocalId(Long localId);

    boolean existsByLocalId(Long localId);

    boolean existsByTelefono(String telefono);

    boolean existsByTelefonoAndIdNot(String telefono, Long id);
}