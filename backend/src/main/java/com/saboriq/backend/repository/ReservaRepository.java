package com.saboriq.backend.repository;

import com.saboriq.backend.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

        List<Reserva> findByUsuarioIdOrderByFechaHoraDesc(Long usuarioId);

        List<Reserva> findByLocalIdAndEstadoInAndFechaHoraBetween(
                        Long localId,
                        Collection<String> estados,
                        LocalDateTime desde,
                        LocalDateTime hasta);

        List<Reserva> findByEstadoAndExpiraEnBefore(
                        String estado, LocalDateTime limite);

        List<Reserva> findByLocalIdOrderByFechaHoraDesc(Long localId);

        @Query("""
                        SELECT l.marca.nombre, COUNT(r),
                               SUM(CASE WHEN r.estado = 'COMPLETADA' THEN 1 ELSE 0 END),
                               SUM(CASE WHEN r.estado = 'NO_SHOW' THEN 1 ELSE 0 END)
                        FROM Reserva r
                        JOIN r.local l
                        GROUP BY l.marca.id, l.marca.nombre
                        """)
        List<Object[]> obtenerResumenReservasPorMarca();

}