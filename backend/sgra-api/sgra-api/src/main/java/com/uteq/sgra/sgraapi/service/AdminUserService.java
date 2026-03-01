package com.uteq.sgra.sgraapi.service;

import com.uteq.sgra.sgraapi.dto.CreateUserRequest;
import com.uteq.sgra.sgraapi.dto.UpdateRolesRequest;
import com.uteq.sgra.sgraapi.dto.UserDto;
import com.uteq.sgra.sgraapi.entity.Acceso;
import com.uteq.sgra.sgraapi.entity.Rol;
import com.uteq.sgra.sgraapi.entity.Usuario;
import com.uteq.sgra.sgraapi.entity.UsuarioRol;
import com.uteq.sgra.sgraapi.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UsuarioRepo usuarioRepo;
    private final AccesoRepo accesoRepo;
    private final RolRepo rolRepo;
    private final UsuarioRolRepo usuarioRolRepo;
    private final PasswordEncoder encoder;
    private final GeneroRepo generoRepo;

    @Transactional(readOnly = true)
    public List<UserDto> list() {
        return usuarioRepo.findAll().stream()
                .map(u -> {
                    var acceso = accesoRepo.findFirstByUsuario_IdUsuarioAndEstadoTrue(u.getIdUsuario()).orElse(null);
                    if (acceso == null) return null;

                    var roles = new HashSet<>(usuarioRolRepo.findActiveRoleNamesByUsuarioId(u.getIdUsuario()));
                    return new UserDto(
                            u.getIdUsuario(),
                            acceso.getNombreUsuario(),
                            u.getNombres(),
                            u.getApellidos(),
                            u.getCorreo(),
                            u.getPerfilUsuario(),
                            roles
                    );
                })
                .filter(Objects::nonNull)
                .toList();
    }

    @Transactional
    public UserDto create(CreateUserRequest req) {
        Long idGen = req.idGenero();
        if (idGen != null && idGen <= 0) idGen = null;

        if (idGen != null && !generoRepo.existsById(idGen)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "IdGenero no existe: " + idGen);
        }

        if (usuarioRepo.existsByCorreoIgnoreCase(req.correo()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe correo: " + req.correo());

        if (usuarioRepo.existsByIdentificacion(req.identificacion()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe identificación: " + req.identificacion());

        if (accesoRepo.existsByNombreUsuarioIgnoreCase(req.nombreUsuario()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe usuario login: " + req.nombreUsuario());

        Usuario u = usuarioRepo.save(Usuario.builder()
                .nombres(req.nombres())
                .apellidos(req.apellidos())
                .identificacion(req.identificacion())
                .correo(req.correo())
                .telefono(req.telefono())
                .idGenero(idGen)
                .perfilUsuario(req.perfilUsuario())
                .build());

        Acceso acceso = accesoRepo.save(Acceso.builder()
                .usuario(u)
                .nombreUsuario(req.nombreUsuario())
                .contrasenia(encoder.encode(req.password()))
                .estado(true)
                .build());

        Set<String> roles;
        if (req.roles() == null || req.roles().isEmpty()) {
            roles = defaultRolesForPerfil(req.perfilUsuario());
        } else {
            roles = normalizeRoles(req.roles());
        }

        for (String roleName : roles) {
            Rol rol = rolRepo.findByRolIgnoreCase(roleName)
                    .orElseGet(() -> rolRepo.save(Rol.builder().rol(roleName).estado(true).build()));

            usuarioRolRepo.save(UsuarioRol.builder()
                    .usuario(u)
                    .rol(rol)
                    .estado(true)
                    .build());
        }


        return new UserDto(
                u.getIdUsuario(),
                acceso.getNombreUsuario(),
                u.getNombres(),
                u.getApellidos(),
                u.getCorreo(),
                u.getPerfilUsuario(),
                roles
        );
    }
    private Set<String> defaultRolesForPerfil(String perfilUsuario) {
        String p = (perfilUsuario == null) ? "" : perfilUsuario.trim().toLowerCase();

        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");

        if (p.equals("admin")) roles.add("ROLE_ADMIN");
        if (p.equals("docente")) roles.add("ROLE_DOCENTE");
        if (p.equals("estudiante")) roles.add("ROLE_ESTUDIANTE");

        return roles;
    }

    @Transactional
    public UserDto updateRoles(Long userId, UpdateRolesRequest req) {
        Usuario u = usuarioRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no existe"));

        var acceso = accesoRepo.findFirstByUsuario_IdUsuarioAndEstadoTrue(userId).orElse(null);

        Set<String> roles = normalizeRoles(req.roles());

        // desactiva TODO lo anterior (soft)
        usuarioRolRepo.disableByUsuarioId(userId);

        // activa/crea los nuevos
        for (String roleName : roles) {
            Rol rol = rolRepo.findByRolIgnoreCase(roleName)
                    .orElseGet(() -> rolRepo.save(Rol.builder().rol(roleName).estado(true).build()));

            UsuarioRol ur = usuarioRolRepo
                    .findFirstByUsuario_IdUsuarioAndRol_RolIgnoreCase(userId, roleName)
                    .orElseGet(() -> UsuarioRol.builder().usuario(u).rol(rol).build());

            ur.setEstado(true);
            usuarioRolRepo.save(ur);
        }

        return new UserDto(
                u.getIdUsuario(),
                acceso != null ? acceso.getNombreUsuario() : null,
                u.getNombres(),
                u.getApellidos(),
                u.getCorreo(),
                u.getPerfilUsuario(),
                roles
        );
    }

    @Transactional
    public void delete(Long userId) {
        int n = accesoRepo.disableByUsuarioId(userId);
        usuarioRolRepo.disableByUsuarioId(userId);

        if (n == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe acceso para el usuario: " + userId);
        }
    }

    private Set<String> normalizeRoles(Set<String> roles) {
        Set<String> out = new HashSet<>();
        if (roles != null) {
            for (String r : roles) {
                if (r == null || r.isBlank()) continue;
                String rr = r.trim().toUpperCase();
                if (!rr.startsWith("ROLE_")) rr = "ROLE_" + rr;
                out.add(rr);
            }
        }
        if (out.isEmpty()) out.add("ROLE_USER");
        return out;
    }
}