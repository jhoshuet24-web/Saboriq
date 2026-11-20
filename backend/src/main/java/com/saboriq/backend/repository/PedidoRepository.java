package com.saboriq.backend.repository;

import com.saboriq.backend.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByUsuarioIdOrderByCreadoEnDesc(Long usuarioId);

    List<Pedido> findByLocalIdOrderByCreadoEnDesc(Long localId);
}