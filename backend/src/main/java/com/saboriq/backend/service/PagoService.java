package com.saboriq.backend.service;

import com.saboriq.backend.dto.PagoRequest;
import com.saboriq.backend.dto.PagoResponse;
import com.saboriq.backend.entity.Pago;
import com.saboriq.backend.entity.Pedido;
import com.saboriq.backend.entity.Reserva;
import com.saboriq.backend.entity.Usuario;
import com.saboriq.backend.exception.OperacionNoPermitidaException;
import com.saboriq.backend.exception.RecursoNoEncontradoException;
import com.saboriq.backend.repository.PagoRepository;
import com.saboriq.backend.repository.PedidoRepository;
import com.saboriq.backend.repository.ReservaRepository;
import com.saboriq.backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PedidoRepository pedidoRepository;

    public PagoService(PagoRepository pagoRepository, ReservaRepository reservaRepository,
            UsuarioRepository usuarioRepository, PedidoRepository pedidoRepository) {
        this.pagoRepository = pagoRepository;
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional
    public PagoResponse pagarReserva(String correo, Long reservaId, PagoRequest request) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada"));

        if (!reserva.getUsuario().getId().equals(usuario.getId())) {
            throw new OperacionNoPermitidaException("No tienes permiso para pagar esta reserva");
        }

        if (!"PENDIENTE_PAGO".equals(reserva.getEstado())) {
            throw new OperacionNoPermitidaException("La reserva no está pendiente de pago");
        }

        if (reserva.getExpiraEn() != null && reserva.getExpiraEn().isBefore(LocalDateTime.now())) {
            throw new OperacionNoPermitidaException("La reserva ha expirado");
        }

        Pago pago = new Pago();
        pago.setUsuario(usuario);
        pago.setReserva(reserva);
        pago.setMonto(reserva.getMontoGarantia());
        pago.setMetodo(request.metodo());
        pago.setEstado("COMPLETADO");
        pago.setReferencia("SIM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        pago.setCreadoEn(LocalDateTime.now());

        pagoRepository.save(pago);

        reserva.setEstado("CONFIRMADA");
        reserva.setExpiraEn(null);
        reservaRepository.save(reserva);

        return new PagoResponse(
                pago.getId(),
                reserva.getId(),
                pago.getMonto(),
                pago.getMetodo(),
                pago.getEstado(),
                pago.getReferencia(),
                pago.getCreadoEn(),
                null);
    }

    @Transactional
    public PagoResponse pagarPedido(String correo, Long pedidoId, PagoRequest request) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado"));

        if (!pedido.getUsuario().getId().equals(usuario.getId())) {
            throw new OperacionNoPermitidaException("No tienes permiso para pagar este pedido");
        }

        if (!"PENDIENTE".equals(pedido.getEstado())) {
            throw new OperacionNoPermitidaException("El pedido no está pendiente de pago");
        }

        Pago pago = new Pago();
        pago.setUsuario(usuario);
        pago.setPedido(pedido);
        pago.setMonto(pedido.getTotal());
        pago.setMetodo(request.metodo());
        pago.setEstado("COMPLETADO");
        pago.setReferencia("SIM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        pago.setCreadoEn(LocalDateTime.now());
        pagoRepository.save(pago);

        pedido.setEstado("PAGADO");
        pedidoRepository.save(pedido);

        return new PagoResponse(
                pago.getId(),
                null,
                pago.getMonto(),
                pago.getMetodo(),
                pago.getEstado(),
                pago.getReferencia(),
                pago.getCreadoEn(),
                pedido.getId());
    }
}