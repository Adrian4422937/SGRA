    package com.uteq.sgra.sgraapi;

    import com.uteq.sgra.sgraapi.entity.Acceso;
    import com.uteq.sgra.sgraapi.entity.Rol;
    import com.uteq.sgra.sgraapi.entity.Usuario;
    import com.uteq.sgra.sgraapi.entity.UsuarioRol;
    import com.uteq.sgra.sgraapi.repo.AccesoRepo;
    import com.uteq.sgra.sgraapi.repo.RolRepo;
    import com.uteq.sgra.sgraapi.repo.UsuarioRepo;
    import com.uteq.sgra.sgraapi.repo.UsuarioRolRepo;
    import org.springframework.boot.CommandLineRunner;
    import org.springframework.boot.SpringApplication;
    import org.springframework.boot.autoconfigure.SpringBootApplication;
    import org.springframework.context.annotation.Bean;
    import org.springframework.security.crypto.password.PasswordEncoder;

    @SpringBootApplication
    public class SgraApiApplication {

        @Bean
        CommandLineRunner init(
                UsuarioRepo usuarioRepo,
                AccesoRepo accesoRepo,
                RolRepo rolRepo,
                UsuarioRolRepo usuarioRolRepo,
                PasswordEncoder encoder
        ) {
            return args -> {

                // 1) Asegura roles base del sistema
                Rol adminRole = rolRepo.findByRolIgnoreCase("ROLE_ADMIN")
                        .orElseGet(() -> rolRepo.save(Rol.builder().rol("ROLE_ADMIN").estado(true).build()));

                Rol userRole = rolRepo.findByRolIgnoreCase("ROLE_USER")
                        .orElseGet(() -> rolRepo.save(Rol.builder().rol("ROLE_USER").estado(true).build()));

                Rol docenteRole = rolRepo.findByRolIgnoreCase("ROLE_DOCENTE")
                        .orElseGet(() -> rolRepo.save(Rol.builder().rol("ROLE_DOCENTE").estado(true).build()));

                Rol estudianteRole = rolRepo.findByRolIgnoreCase("ROLE_ESTUDIANTE")
                        .orElseGet(() -> rolRepo.save(Rol.builder().rol("ROLE_ESTUDIANTE").estado(true).build()));

                // 2) Upsert ADMIN (si existe, lo re-activa y resetea password)
                Acceso accesoAdmin = accesoRepo.findByNombreUsuarioIgnoreCase("admin")
                        .orElse(null);

                Usuario adminUser;
                if (accesoAdmin != null) {
                    // ya existe acceso admin
                    accesoAdmin.setContrasenia(encoder.encode("admin123"));
                    accesoAdmin.setEstado(true);
                    accesoRepo.save(accesoAdmin);

                    adminUser = accesoAdmin.getUsuario();
                } else {
                    // crear usuario admin + acceso
                    adminUser = usuarioRepo.save(Usuario.builder()
                            .nombres("Admin")
                            .apellidos("SGRA")
                            .identificacion("0000000000")
                            .correo("admin@sgra.local")
                            .telefono("0000000000")
                            .idGenero(null)
                            .perfilUsuario("admin")
                            .build());

                    accesoRepo.save(Acceso.builder()
                            .usuario(adminUser)
                            .nombreUsuario("admin")
                            .contrasenia(encoder.encode("admin123"))
                            .estado(true)
                            .build());
                }

                // 3) Asegura que admin tenga roles activos (soft delete + reactivar)
                usuarioRolRepo.disableByUsuarioId(adminUser.getIdUsuario());

                usuarioRolRepo.save(UsuarioRol.builder()
                        .usuario(adminUser).rol(adminRole).estado(true).build());

                usuarioRolRepo.save(UsuarioRol.builder()
                        .usuario(adminUser).rol(userRole).estado(true).build());
            };
        }

        public static void main(String[] args) {
            SpringApplication.run(SgraApiApplication.class, args);
        }
    }



