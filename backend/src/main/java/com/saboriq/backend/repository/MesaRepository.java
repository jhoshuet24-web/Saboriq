package com.saboriq.backend.repository;

import com.saboriq.backend.entity.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MesaRepository extends JpaRepository<Mesa, Long> {

    List<Mesa> findByLocalId(Long localId);

    boolean existsByLocalId(Long localId);

    boolean existsByLocalIdAndNumero(Long localId, Integer numero);

    boolean existsByLocalIdAndNumeroAndIdNot(
            Long localId, Integer numero, Long id);
}