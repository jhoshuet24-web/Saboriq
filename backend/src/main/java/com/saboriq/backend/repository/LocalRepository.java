package com.saboriq.backend.repository;

import com.saboriq.backend.entity.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocalRepository extends JpaRepository<Local, Long> {

        List<Local> findByMarcaId(Long marcaId);

        boolean existsByMarcaId(Long marcaId);

        boolean existsByNombreIgnoreCaseAndMarcaId(
                        String nombre, Long marcaId);

        boolean existsByNombreIgnoreCaseAndMarcaIdAndIdNot(
                        String nombre, Long marcaId, Long id);
}