
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
            @Param("nombre") String nombre
    );

    List<Producto> findByCategoriaId(Long categoriaId);

    List<Producto> findByDisponibleTrue();

    @Query("""
        SELECT p FROM Producto p
        WHERE p.precio BETWEEN :min AND :max
        ORDER BY p.precio ASC
        """)
    List<Producto> buscarPorRangoPrecio(
            @Param("min") BigDecimal min,
            @Param("max") BigDecimal max
    );
}
