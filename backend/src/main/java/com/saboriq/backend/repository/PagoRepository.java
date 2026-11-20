package com.saboriq.backend.repository;

import com.saboriq.backend.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByReservaId(Long reservaId);

    List<Pago> findByPedidoId(Long pedidoId);

    @Query("""
            SELECT p.reserva.local.marca.nombre, SUM(p.monto)
            FROM Pago p
            WHERE p.estado = 'COMPLETADO' AND p.reserva IS NOT NULL
            GROUP BY p.reserva.local.marca.id, p.reserva.local.marca.nombre
            """)
    List<Object[]> obtenerIngresosPorMarca();

}
