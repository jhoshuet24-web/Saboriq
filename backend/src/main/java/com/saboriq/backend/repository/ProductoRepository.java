package com.saboriq.backend.repository;

import com.saboriq.backend.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductoRepository
                extends JpaRepository<Producto, Long> {

        @Query("""
                        SELECT p FROM Producto p
                        WHERE LOWER(p.nombre) LIKE LOWER(
                            CONCAT('%', :nombre, '%'))
                        """)
        List<Producto> buscarPorNombre(
                        @Param("nombre") String nombre);

        List<Producto> findByCategoriaId(Long categoriaId);

        List<Producto> findByMarcaId(Long marcaId);

        boolean existsByCategoriaId(Long categoriaId);

        boolean existsByMarcaId(Long marcaId);

        boolean existsByNombreIgnoreCaseAndCategoriaIdAndMarcaId(
                        String nombre, Long categoriaId, Long marcaId);

        boolean existsByNombreIgnoreCaseAndCategoriaIdAndMarcaIdAndIdNot(
                        String nombre, Long categoriaId, Long marcaId, Long id);

        boolean existsByNombreIgnoreCaseAndCategoriaIdAndMarcaIsNull(
                        String nombre, Long categoriaId);

        boolean existsByNombreIgnoreCaseAndCategoriaIdAndMarcaIsNullAndIdNot(
                        String nombre, Long categoriaId, Long id);

        List<Producto> findByDisponibleTrue();

        @Query("""
                        SELECT p FROM Producto p
                        WHERE p.precio BETWEEN :min AND :max
                        ORDER BY p.precio ASC
                        """)
        List<Producto> buscarPorRangoPrecio(
                        @Param("min") BigDecimal min,
                        @Param("max") BigDecimal max);
}
