package com.uteq.sgra.sgraapi.security;

import com.uteq.sgra.sgraapi.repo.AccesoRepo;
import com.uteq.sgra.sgraapi.repo.UsuarioRolRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class DbUserDetailsService implements UserDetailsService {

    private final AccesoRepo accesoRepo;
    private final UsuarioRolRepo usuarioRolRepo;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String nombreUsuario) throws UsernameNotFoundException {

        var acceso = accesoRepo.findFirstByNombreUsuarioIgnoreCaseAndEstadoTrue(nombreUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Acceso no encontrado o inactivo"));

        Long userId = acceso.getUsuario().getIdUsuario();
        var roles = usuarioRolRepo.findActiveRoleNamesByUsuarioId(userId);

        var auths = roles.stream().map(SimpleGrantedAuthority::new).toList();

        return User.withUsername(acceso.getNombreUsuario())
                .password(acceso.getContrasenia())
                .authorities(auths)
                .build();
    }
}
