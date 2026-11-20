package com.saboriq.backend.service;

import com.saboriq.backend.dto.ReservaRequest;
import com.saboriq.backend.dto.ReservaResponse;
import com.saboriq.backend.entity.Local;
import com.saboriq.backend.entity.Pago;
import com.saboriq.backend.entity.Reserva;
import com.saboriq.backend.entity.Usuario;
import com.saboriq.backend.exception.OperacionNoPermitidaException;
import com.saboriq.backend.exception.RecursoNoEncontradoException;
import com.saboriq.backend.repository.LocalRepository;
import com.saboriq.backend.repository.PagoRepository;
import com.saboriq.backend.repository.ReservaRepository;
import com.saboriq.backend.repository.UsuarioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservaService {

        private static final String PENDIENTE_PAGO = "PENDIENTE_PAGO";
        private static final String CONFIRMADA = "CONFIRMADA";
        private static final String CANCELADA = "CANCELADA";
        private static final String EXPIRADA = "EXPIRADA";

        private static final List<String> ESTADOS_ACTIVOS = List.of(PENDIENTE_PAGO, CONFIRMADA);

        private static final BigDecimal GARANTIA_POR_PERSONA = new BigDecimal("30.00");
        private static final int PERSONAS_SIN_GARANTIA = 4;
        private static final int DURACION_RESERVA_HORAS = 2;
        private static final int ANTICIPACION_MINIMA_HORAS = 2;
        private static final int ANTICIPACION_MAXIMA_DIAS = 30;
        private static final int HORAS_REEMBOLSO_TOTAL = 4;
        private static final int MINUTOS_EXPIRACION = 10;

        private final ReservaRepository reservaRepository;
        private final UsuarioRepository usuarioRepository;
        private final LocalRepository localRepository;
        private final PagoRepository pagoRepository;

        public ReservaService(
                        ReservaRepository reservaRepository,
                        UsuarioRepository usuarioRepository,
                        LocalRepository localRepository,
                        PagoRepository pagoRepository) {
                this.reservaRepository = reservaRepository;
                this.usuarioRepository = usuarioRepository;
                this.localRepository = localRepository;
                this.pagoRepository = pagoRepository;
        }

        private Usuario buscarUsuario(String correo) {
                return usuarioRepository.findByCorreo(correo)
                                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        }

        private ReservaResponse convertir(Reserva r) {
                Local local = r.getLocal();

                return new ReservaResponse(
                                r.getId(),
                                local.getId(),
                                local.getNombre(),
                                local.getMarca().getNombre(),
                                r.getFechaHora(),
                                r.getPersonas(),
                                r.getEstado(),
                                r.getRequiereGarantia(),
                                r.getMontoGarantia(),
                                r.getExpiraEn(),
                                r.getCreadoEn(),
                                r.getCanceladaEn(),
                                r.getGarantiaResultado(),
                                r.getUsuario().getNombre());
        }

        private void expirarVencidas() {
                List<Reserva> vencidas = reservaRepository
                                .findByEstadoAndExpiraEnBefore(
                                                PENDIENTE_PAGO, LocalDateTime.now());

                for (Reserva reserva : vencidas) {
                        reserva.setEstado(EXPIRADA);
                }
        }

        private int personasEnVentana(Long localId, LocalDateTime fechaHora) {

                LocalDateTime desde = fechaHora.minusHours(DURACION_RESERVA_HORAS);
                LocalDateTime hasta = fechaHora.plusHours(DURACION_RESERVA_HORAS);

                return reservaRepository
                                .findByLocalIdAndEstadoInAndFechaHoraBetween(
                                                localId, ESTADOS_ACTIVOS, desde, hasta)
                                .stream()
                                .filter(r -> r.getFechaHora().isAfter(desde)
                                                && r.getFechaHora().isBefore(hasta))
                                .mapToInt(Reserva::getPersonas)
                                .sum();
        }

        @Transactional
        public ReservaResponse crear(String correo, ReservaRequest request) {

                expirarVencidas();

                Usuario usuario = buscarUsuario(correo);

                Local local = localRepository.findById(request.localId())
                                .orElseThrow(() -> new RecursoNoEncontradoException(
                                                "Local no encontrado con ID: " + request.localId()));

                if (Boolean.FALSE.equals(local.getActivo())) {
                        throw new OperacionNoPermitidaException("El local no esta disponible para reservas");
                }

                LocalDateTime ahora = LocalDateTime.now();
                LocalDateTime fechaHora = request.fechaHora();

                if (fechaHora.isBefore(ahora.plusHours(ANTICIPACION_MINIMA_HORAS))) {
                        throw new IllegalArgumentException("La reserva debe hacerse con al menos "
                                        + ANTICIPACION_MINIMA_HORAS + " horas de anticipacion");
                }
                if (fechaHora.isAfter(ahora.plusDays(ANTICIPACION_MAXIMA_DIAS))) {
                        throw new IllegalArgumentException(
                                        "La reserva no puede hacerse con mas de "
                                                        + ANTICIPACION_MAXIMA_DIAS
                                                        + " dias de anticipacion");
                }

                int ocupadas = personasEnVentana(local.getId(), fechaHora);
                int disponibles = Math.max(0, local.getAforo() - ocupadas);

                if (request.personas() > disponibles) {
                        throw new OperacionNoPermitidaException(
                                        "No hay cupo en ese horario. Cupo disponible: " + disponibles + " personas");
                }

                DayOfWeek dia = fechaHora.getDayOfWeek();
                boolean finDeSemana = dia == DayOfWeek.SATURDAY
                                || dia == DayOfWeek.SUNDAY;
                boolean requiereGarantia = request.personas() > PERSONAS_SIN_GARANTIA || finDeSemana;

                Reserva reserva = new Reserva();
                reserva.setUsuario(usuario);
                reserva.setLocal(local);
                reserva.setFechaHora(fechaHora);
                reserva.setPersonas(request.personas());
                reserva.setCreadoEn(ahora);
                reserva.setRequiereGarantia(requiereGarantia);
                if (requiereGarantia) {
                        reserva.setMontoGarantia(
                                        GARANTIA_POR_PERSONA.multiply(
                                                        BigDecimal.valueOf(request.personas())));
                        reserva.setEstado(PENDIENTE_PAGO);
                        reserva.setExpiraEn(ahora.plusMinutes(MINUTOS_EXPIRACION));
                } else {
                        reserva.setMontoGarantia(BigDecimal.ZERO);
                        reserva.setEstado(CONFIRMADA);
                }

                return convertir(reservaRepository.save(reserva));
        }

        @Transactional
        public List<ReservaResponse> misReservas(String correo) {

                expirarVencidas();

                Usuario usuario = buscarUsuario(correo);

                return reservaRepository
                                .findByUsuarioIdOrderByFechaHoraDesc(usuario.getId())
                                .stream()
                                .map(this::convertir)
                                .toList();
        }

        @Transactional
        public ReservaResponse cancelar(String correo, Long id) {
                expirarVencidas();

                Reserva reserva = reservaRepository.findById(id)
                                .orElseThrow(() -> new RecursoNoEncontradoException(
                                                "Reserva no encontrada con ID: " + id));

                if (!reserva.getUsuario().getCorreo().equals(correo)) {
                        throw new RecursoNoEncontradoException("Reserva no encontrada con ID: " + id);
                }

                String estadoPrevio = reserva.getEstado();
                if (!ESTADOS_ACTIVOS.contains(estadoPrevio)) {
                        throw new OperacionNoPermitidaException(
                                        "La reserva no se puede cancelar porque esta " + estadoPrevio);
                }

                LocalDateTime ahora = LocalDateTime.now();
                if (!reserva.getFechaHora().isAfter(ahora)) {
                        throw new OperacionNoPermitidaException("La reserva ya paso y no se puede cancelar");
                }
                reserva.setEstado(CANCELADA);
                reserva.setCanceladaEn(ahora);
                reserva.setExpiraEn(null);
                if (reserva.getRequiereGarantia() && CONFIRMADA.equals(estadoPrevio)) {
                        boolean reembolsoTotal = !ahora
                                        .isAfter(reserva.getFechaHora().minusHours(HORAS_REEMBOLSO_TOTAL));
                        if (reembolsoTotal) {
                                reserva.setGarantiaResultado("REEMBOLSO_TOTAL");
                                List<Pago> pagos = pagoRepository.findByReservaId(id);
                                for (Pago p : pagos) {
                                        p.setEstado("REEMBOLSADO");
                                        pagoRepository.save(p);
                                }
                        } else {
                                reserva.setGarantiaResultado("RETENIDA");
                        }
                }
                return convertir(reservaRepository.save(reserva));
        }

        @Transactional(readOnly = true)
        public List<ReservaResponse> reservasDelLocal(String correo) {
                Usuario staff = buscarUsuario(correo);

                if (staff.getLocal() == null) {
                        throw new RecursoNoEncontradoException("No tienes un local asignado");
                }

                return reservaRepository
                                .findByLocalIdOrderByFechaHoraDesc(staff.getLocal().getId())
                                .stream()
                                .map(this::convertir)
                                .toList();
        }

        @Transactional
        public ReservaResponse cambiarEstadoStaff(String correo, Long idReserva, String nuevoEstado) {
                Usuario staff = buscarUsuario(correo);
                Reserva reserva = reservaRepository.findById(idReserva)
                                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada"));
                if (staff.getLocal() == null || !reserva.getLocal().getId().equals(staff.getLocal().getId())) {
                        throw new OperacionNoPermitidaException("No tienes permiso para modificar esta reserva");
                }
                if (!"CONFIRMADA".equals(reserva.getEstado())) {
                        throw new OperacionNoPermitidaException(
                                        "Solo se pueden modificar reservas en estado CONFIRMADA");
                }
                if (!List.of("COMPLETADA", "NO_SHOW").contains(nuevoEstado)) {
                        throw new IllegalArgumentException("Estado no válido");
                }
                reserva.setEstado(nuevoEstado);
                return convertir(reservaRepository.save(reserva));
        }

}
