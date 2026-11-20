package com.saboriq.backend.service;

import com.saboriq.backend.dto.MiLocalResponse;
import com.saboriq.backend.dto.StaffRequest;
import com.saboriq.backend.dto.StaffResponse;
import com.saboriq.backend.dto.StaffUpdateRequest;
import com.saboriq.backend.entity.Local;
import com.saboriq.backend.entity.Rol;
import com.saboriq.backend.entity.Usuario;
import com.saboriq.backend.exception.RecursoDuplicadoException;
import com.saboriq.backend.exception.RecursoNoEncontradoException;
import com.saboriq.backend.repository.LocalRepository;
import com.saboriq.backend.repository.RolRepository;
import com.saboriq.backend.repository.UsuarioRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class StaffService {

    private static final String ROL_STAFF = "STAFF";

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final LocalRepository localRepository;
    private final PasswordEncoder passwordEncoder;
    private final LocalService localService;
    private final MesaService mesaService;
    private final RepartidorService repartidorService;

    public StaffService(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            LocalRepository localRepository,
            PasswordEncoder passwordEncoder,
            LocalService localService,
            MesaService mesaService,
            RepartidorService repartidorService) {

        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.localRepository = localRepository;
        this.passwordEncoder = passwordEncoder;
        this.localService = localService;
        this.mesaService = mesaService;
        this.repartidorService = repartidorService;
    }

    private Local buscarLocal(Long id) {
        return localRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Local no encontrado con ID: " + id));
    }

    private Usuario buscarStaff(Long id) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Personal no encontrado con ID: " + id));

        if (!ROL_STAFF.equals(usuario.getRol().getNombre())) {
            throw new RecursoNoEncontradoException(
                    "Personal no encontrado con ID: " + id);
        }

        return usuario;
    }

    private StaffResponse convertir(Usuario u) {
        Local local = u.getLocal();

        return new StaffResponse(
                u.getId(),
                u.getNombre(),
                u.getCorreo(),
                local != null ? local.getId() : null,
                local != null ? local.getNombre() : null);
    }

    @Transactional(readOnly = true)
    public List<StaffResponse> listar() {
        return usuarioRepository.findByRolNombre(ROL_STAFF)
                .stream()
                .map(this::convertir)
                .toList();
    }

    @Transactional
    public StaffResponse crear(StaffRequest request) {

        String correo = request.correo()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (usuarioRepository.existsByCorreo(correo)) {
            throw new RecursoDuplicadoException(
                    "El correo ya se encuentra registrado");
        }

        Local local = buscarLocal(request.localId());

        Rol rolStaff = rolRepository.findByNombre(ROL_STAFF)
                .orElseThrow(() -> new IllegalStateException(
                        "El rol STAFF no existe"));

        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre().trim());
        usuario.setCorreo(correo);
        usuario.setPassword(passwordEncoder.encode(request.password()));
        usuario.setRol(rolStaff);
        usuario.setLocal(local);

        return convertir(usuarioRepository.save(usuario));
    }

    @Transactional
    public StaffResponse actualizar(Long id, StaffUpdateRequest request) {

        Usuario usuario = buscarStaff(id);
        Local local = buscarLocal(request.localId());

        usuario.setNombre(request.nombre().trim());
        usuario.setLocal(local);

        return convertir(usuarioRepository.save(usuario));
    }

    @Transactional
    public void eliminar(Long id) {
        Usuario usuario = buscarStaff(id);
        usuarioRepository.delete(usuario);
    }

    @Transactional(readOnly = true)
    public MiLocalResponse miLocal(String correo) {

        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Usuario no encontrado"));

        if (usuario.getLocal() == null) {
            throw new RecursoNoEncontradoException(
                    "No tienes un local asignado");
        }

        Long localId = usuario.getLocal().getId();

        return new MiLocalResponse(
                localService.buscarPorId(localId),
                mesaService.listarPorLocal(localId),
                repartidorService.listarPorLocal(localId));
    }
}