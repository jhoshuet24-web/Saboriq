package com.saboriq.backend.service;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.saboriq.backend.dto.DetallePedidoRequest;
import com.saboriq.backend.dto.DetallePedidoResponse;
import com.saboriq.backend.dto.PedidoRequest;
import com.saboriq.backend.dto.PedidoResponse;
import com.saboriq.backend.entity.DetallePedido;
import com.saboriq.backend.entity.Local;
import com.saboriq.backend.entity.Pago;
import com.saboriq.backend.entity.Pedido;
import com.saboriq.backend.entity.Producto;
import com.saboriq.backend.entity.Repartidor;
import com.saboriq.backend.entity.Usuario;
import com.saboriq.backend.exception.OperacionNoPermitidaException;
import com.saboriq.backend.exception.RecursoNoEncontradoException;
import com.saboriq.backend.repository.LocalRepository;
import com.saboriq.backend.repository.PagoRepository;
import com.saboriq.backend.repository.PedidoRepository;
import com.saboriq.backend.repository.ProductoRepository;
import com.saboriq.backend.repository.RepartidorRepository;
import com.saboriq.backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PedidoService {

        private final PedidoRepository pedidoRepository;
        private final UsuarioRepository usuarioRepository;
        private final LocalRepository localRepository;
        private final ProductoRepository productoRepository;
        private final RepartidorRepository repartidorRepository;
        private final PagoRepository pagoRepository;

        private static final Map<String, List<String>> TRANSICIONES = Map.of(
                        "PENDIENTE", List.of("CANCELADO"),
                        "PAGADO", List.of("EN_PREPARACION", "CANCELADO"),
                        "EN_PREPARACION", List.of("EN_CAMINO", "CANCELADO"),
                        "EN_CAMINO", List.of("ENTREGADO"),
                        "ENTREGADO", List.of(),
                        "CANCELADO", List.of());

        public PedidoService(
                        PedidoRepository pedidoRepository,
                        UsuarioRepository usuarioRepository,
                        LocalRepository localRepository,
                        ProductoRepository productoRepository,
                        RepartidorRepository repartidorRepository,
                        PagoRepository pagoRepository) {
                this.pedidoRepository = pedidoRepository;
                this.usuarioRepository = usuarioRepository;
                this.localRepository = localRepository;
                this.productoRepository = productoRepository;
                this.repartidorRepository = repartidorRepository;
                this.pagoRepository = pagoRepository;
        }

        @Transactional
        public PedidoResponse crearPedido(String correo, PedidoRequest request) {
                Usuario usuario = usuarioRepository.findByCorreo(correo)
                                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
                Local local = localRepository.findById(request.localId())
                                .orElseThrow(() -> new RecursoNoEncontradoException("Local no encontrado"));

                if (Boolean.FALSE.equals(local.getActivo())) {
                        throw new OperacionNoPermitidaException("El local no está disponible");
                }

                if (!cubreDistrito(local, request.distritoEntrega())) {
                        throw new OperacionNoPermitidaException(
                                        "El local no reparte en " + request.distritoEntrega()
                                                        + ". Cobertura: " + local.getDistrito()
                                                        + (local.getDistritosCobertura() == null ? ""
                                                                        : ", " + local.getDistritosCobertura()));
                }

                Pedido pedido = new Pedido();
                pedido.setUsuario(usuario);
                pedido.setLocal(local);
                pedido.setDireccionEntrega(request.direccionEntrega());
                pedido.setDistritoEntrega(request.distritoEntrega());
                pedido.setCreadoEn(LocalDateTime.now());
                pedido.setEstado("PENDIENTE");

                BigDecimal subtotal = BigDecimal.ZERO;
                List<DetallePedidoResponse> detallesResponse = new ArrayList<>();

                for (DetallePedidoRequest item : request.items()) {
                        Producto producto = productoRepository.findById(item.productoId())
                                        .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));

                        if (!producto.getDisponible()) {
                                throw new OperacionNoPermitidaException(
                                                "El producto " + producto.getNombre() + " no está disponible.");
                        }
                        if (producto.getMarca() != null
                                        && !producto.getMarca().getId().equals(local.getMarca().getId())) {
                                throw new OperacionNoPermitidaException(
                                                "El producto " + producto.getNombre()
                                                                + " no pertenece a la carta de este local");
                        }

                        BigDecimal precioItem = producto.getPrecio().multiply(BigDecimal.valueOf(item.cantidad()));
                        subtotal = subtotal.add(precioItem);

                        DetallePedido detalle = new DetallePedido();
                        detalle.setProducto(producto);
                        detalle.setCantidad(item.cantidad());
                        detalle.setPrecioUnitario(producto.getPrecio());
                        pedido.agregarDetalle(detalle);
                        detallesResponse.add(new DetallePedidoResponse(producto.getNombre(), item.cantidad(),
                                        producto.getPrecio()));
                }

                if (subtotal.compareTo(local.getPedidoMinimo()) < 0) {
                        throw new OperacionNoPermitidaException("El pedido mínimo es de S/ " + local.getPedidoMinimo());
                }

                BigDecimal total = subtotal.add(local.getCostoEnvio());
                pedido.setSubtotal(subtotal);
                pedido.setCostoEnvio(local.getCostoEnvio());
                pedido.setTotal(total);
                pedidoRepository.save(pedido);

                return new PedidoResponse(pedido.getId(), local.getNombre(), pedido.getDireccionEntrega(),
                                pedido.getDistritoEntrega(), pedido.getSubtotal(), pedido.getCostoEnvio(),
                                pedido.getTotal(),
                                pedido.getEstado(), pedido.getCreadoEn(), detallesResponse, null);
        }

        @Transactional(readOnly = true)
        public List<PedidoResponse> misPedidos(String correo) {
                Usuario usuario = usuarioRepository.findByCorreo(correo)
                                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
                return pedidoRepository.findByUsuarioIdOrderByCreadoEnDesc(usuario.getId()).stream()
                                .map(this::convertir)
                                .toList();
        }

        @Transactional(readOnly = true)
        public List<PedidoResponse> pedidosDelLocal(String correo) {
                Usuario staff = usuarioRepository.findByCorreo(correo)
                                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

                if (staff.getLocal() == null) {
                        throw new RecursoNoEncontradoException("No tienes un local asignado");
                }

                return pedidoRepository.findByLocalIdOrderByCreadoEnDesc(staff.getLocal().getId())
                                .stream().map(this::convertir).toList();
        }

        @Transactional
        public PedidoResponse actualizarEstadoStaff(String correo, Long pedidoId, String nuevoEstado,
                        Long repartidorId) {
                Usuario staff = usuarioRepository.findByCorreo(correo)
                                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

                Pedido pedido = pedidoRepository.findById(pedidoId)
                                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado"));

                if (staff.getLocal() == null || !pedido.getLocal().getId().equals(staff.getLocal().getId())) {
                        throw new OperacionNoPermitidaException("No tienes permiso sobre este pedido");
                }

                if (!TRANSICIONES.containsKey(nuevoEstado)) {
                        throw new OperacionNoPermitidaException("Estado no válido: " + nuevoEstado);
                }

                String actual = pedido.getEstado();

                if ("EN_PREPARACION".equals(nuevoEstado) && "PENDIENTE".equals(actual)) {
                        throw new OperacionNoPermitidaException(
                                        "El pedido no puede pasar a EN_PREPARACION porque su pago no está aprobado");
                }

                List<String> permitidos = TRANSICIONES.getOrDefault(actual, List.of());
                if (!permitidos.contains(nuevoEstado)) {
                        throw new OperacionNoPermitidaException(
                                        "No se puede cambiar un pedido de " + actual + " a " + nuevoEstado);
                }

                if (repartidorId != null) {
                        Repartidor rep = repartidorRepository.findById(repartidorId)
                                        .orElseThrow(() -> new RecursoNoEncontradoException(
                                                        "Repartidor no encontrado"));
                        if (!rep.getLocal().getId().equals(pedido.getLocal().getId())) {
                                throw new OperacionNoPermitidaException(
                                                "El repartidor no pertenece al local de este pedido");
                        }
                        if (Boolean.FALSE.equals(rep.getActivo())) {
                                throw new OperacionNoPermitidaException("El repartidor no está activo");
                        }
                        pedido.setRepartidor(rep);
                }

                if ("CANCELADO".equals(nuevoEstado) && !"PENDIENTE".equals(actual)) {
                        for (Pago p : pagoRepository.findByPedidoId(pedido.getId())) {
                                if ("COMPLETADO".equals(p.getEstado())) {
                                        p.setEstado("REEMBOLSADO");
                                        pagoRepository.save(p);
                                }
                        }
                }

                pedido.setEstado(nuevoEstado);
                return convertir(pedidoRepository.save(pedido));
        }

        private String normalizar(String s) {
                return Normalizer.normalize(s.trim(), Normalizer.Form.NFD)
                                .replaceAll("\\p{M}", "")
                                .toLowerCase(Locale.ROOT);
        }

        private boolean cubreDistrito(Local local, String distrito) {
                String buscado = normalizar(distrito);
                if (normalizar(local.getDistrito()).equals(buscado)) {
                        return true;
                }
                String cob = local.getDistritosCobertura();
                if (cob == null || cob.isBlank()) {
                        return false;
                }
                return Arrays.stream(cob.split(","))
                                .map(this::normalizar)
                                .anyMatch(buscado::equals);
        }

        private PedidoResponse convertir(Pedido p) {
                List<DetallePedidoResponse> detalles = p.getDetalles().stream().map(
                                d -> new DetallePedidoResponse(d.getProducto().getNombre(), d.getCantidad(),
                                                d.getPrecioUnitario()))
                                .toList();

                String repartidorNombre = p.getRepartidor() != null ? p.getRepartidor().getNombre() : null;

                return new PedidoResponse(p.getId(), p.getLocal().getNombre(), p.getDireccionEntrega(),
                                p.getDistritoEntrega(),
                                p.getSubtotal(), p.getCostoEnvio(), p.getTotal(), p.getEstado(), p.getCreadoEn(),
                                detalles, repartidorNombre);
        }
}