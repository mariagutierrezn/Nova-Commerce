package com.novacommerce.user_service.config;


import com.novacommerce.user_service.domain.enums.UserStatusEnum;
import com.novacommerce.user_service.domain.model.Permission;
import com.novacommerce.user_service.domain.model.Role;
import com.novacommerce.user_service.domain.model.User;
import com.novacommerce.user_service.repository.PermissionRepository;
import com.novacommerce.user_service.repository.RoleRepository;
import com.novacommerce.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Inicializador de datos del sistema (seed).
 * Crea permisos, roles y usuario administrador iniciales.
 * Se ejecuta solo si app.seed.enabled=true.
 * Es idempotente: no duplica datos existentes.
 */
@Component
@ConditionalOnProperty(prefix = "app.seed", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SeedProperties seedProperties;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("=".repeat(80));
        log.info("Iniciando seed de datos del sistema auth-service");
        log.info("=".repeat(80));

        createPermissions();
        createRoles();
        createAdminUser();

        log.info("=".repeat(80));
        log.info("Seed de datos completado exitosamente");
        log.info("=".repeat(80));
    }

    /**
     * Crea los permisos base del sistema.
     * Solo crea los que no existen.
     */
    private void createPermissions() {
        log.info("Creando permisos del sistema...");

        List<PermissionData> permissionsData = List.of(
            // Permisos de usuarios
            new PermissionData("USER_READ", "Permite leer información de usuarios"),
            new PermissionData("USER_CREATE", "Permite crear nuevos usuarios"),
            new PermissionData("USER_UPDATE", "Permite actualizar usuarios existentes"),
            new PermissionData("USER_DELETE", "Permite eliminar usuarios"),

            // Permisos de roles
            new PermissionData("ROLE_READ", "Permite leer información de roles"),
            new PermissionData("ROLE_CREATE", "Permite crear nuevos roles"),
            new PermissionData("ROLE_UPDATE", "Permite actualizar roles existentes"),
            new PermissionData("ROLE_DELETE", "Permite eliminar roles"),

            // Permisos de autenticación
            new PermissionData("AUTH_VALIDATE", "Permite validar tokens JWT")
        );

        permissionsData.forEach(data -> {
            Optional<Permission> existingPermission = permissionRepository.findByName(data.name);

            if (existingPermission.isEmpty()) {
                Permission permission = Permission.builder()
                    .name(data.name)
                    .description(data.description)
                    .build();

                permissionRepository.save(permission);
                log.info("✓ Permiso creado: {}", data.name);
            } else {
                log.info("○ Permiso ya existe: {}", data.name);
            }
        });

        log.info("Permisos procesados: {} total", permissionsData.size());
    }

    /**
     * Crea los roles base del sistema con sus permisos.
     * Solo crea los que no existen.
     */
    private void createRoles() {
        log.info("Creando roles del sistema...");

        createAdminRole();
        createHrRole();
        createSalesRole();
        createUserRole();

        log.info("Roles procesados exitosamente");
    }

    /**
     * Crea el rol ADMIN con todos los permisos.
     */
    private void createAdminRole() {
        String roleName = "ADMIN";

        if (roleRepository.findByName(roleName).isEmpty()) {
            Set<String> allPermissionIds = new HashSet<>();
            permissionRepository.findAll().forEach(p -> allPermissionIds.add(p.getId()));

            Role adminRole = Role.builder()
                .name(roleName)
                .description("Administrador del sistema con acceso completo")
                .permissionIds(allPermissionIds)
                .build();

            roleRepository.save(adminRole);
            log.info("✓ Rol creado: {} con {} permisos", roleName, allPermissionIds.size());
        } else {
            log.info("○ Rol ya existe: {}", roleName);
        }
    }

    /**
     * Crea el rol HR (Recursos Humanos).
     */
    private void createHrRole() {
        String roleName = "HR";

        if (roleRepository.findByName(roleName).isEmpty()) {
            Set<String> permissionIds = findPermissionIdsByNames("USER_READ");

            Role hrRole = Role.builder()
                .name(roleName)
                .description("Recursos Humanos con acceso de lectura a usuarios")
                .permissionIds(permissionIds)
                .build();

            roleRepository.save(hrRole);
            log.info("✓ Rol creado: {} con {} permisos", roleName, permissionIds.size());
        } else {
            log.info("○ Rol ya existe: {}", roleName);
        }
    }

    /**
     * Crea el rol SALES (Ventas).
     */
    private void createSalesRole() {
        String roleName = "SALES";

        if (roleRepository.findByName(roleName).isEmpty()) {
            Set<String> permissionIds = findPermissionIdsByNames("USER_READ");

            Role salesRole = Role.builder()
                .name(roleName)
                .description("Ventas con acceso de lectura a usuarios")
                .permissionIds(permissionIds)
                .build();

            roleRepository.save(salesRole);
            log.info("✓ Rol creado: {} con {} permisos", roleName, permissionIds.size());
        } else {
            log.info("○ Rol ya existe: {}", roleName);
        }
    }

    /**
     * Crea el rol USER básico con permisos de lectura.
     */
    private void createUserRole() {
        String roleName = "USER";

        if (roleRepository.findByName(roleName).isEmpty()) {
            Set<String> permissionIds = findPermissionIdsByNames("USER_READ");

            Role userRole = Role.builder()
                .name(roleName)
                .description("Usuario estándar del sistema con acceso de lectura")
                .permissionIds(permissionIds)
                .build();

            roleRepository.save(userRole);
            log.info("✓ Rol creado: {} con {} permisos", roleName, permissionIds.size());
        } else {
            log.info("○ Rol ya existe: {}", roleName);
        }
    }

    /**
     * Crea el usuario administrador inicial.
     */
    private void createAdminUser() {
        log.info("Creando usuario administrador...");

        String adminUsername = seedProperties.getAdmin().getUsername();
        String adminEmail = seedProperties.getAdmin().getEmail();
        String adminPassword = seedProperties.getAdmin().getPassword();

        if (userRepository.existsByUsername(adminUsername)) {
            log.info("○ Usuario administrador ya existe: {}", adminUsername);
            return;
        }

        if (userRepository.existsByEmail(adminEmail)) {
            log.info("○ Email de administrador ya está en uso: {}", adminEmail);
            return;
        }

        Optional<Role> adminRole = roleRepository.findByName("ADMIN");

        if (adminRole.isEmpty()) {
            log.warn("⚠ No se puede crear usuario administrador: rol ADMIN no existe");
            return;
        }

        String encryptedPassword = passwordEncoder.encode(adminPassword);

        User admin = User.builder()
            .username(adminUsername)
            .email(adminEmail)
            .password(encryptedPassword)
            .status(UserStatusEnum.ACTIVE)
            .enabled(true)
            .locked(false)
            .roleIds(Set.of(adminRole.get().getId()))
            .build();

        userRepository.save(admin);
        log.info("✓ Usuario administrador creado: {}", adminUsername);
        log.info("  Email: {}", adminEmail);
        log.info("  ⚠ IMPORTANTE: Cambie la contraseña después del primer login");
    }

    /**
     * Busca permisos por nombres.
     *
     * @param permissionNames nombres de permisos
     * @return conjunto de permisos encontrados
     */
    private Set<String> findPermissionIdsByNames(String... permissionNames) {
        Set<String> permissionIds = new HashSet<>();
        for (String name : permissionNames) {
            permissionRepository.findByName(name).ifPresent(p -> permissionIds.add(p.getId()));
        }
        return permissionIds;
    }

    /**
     * Record para datos de permiso.
     */
    private record PermissionData(String name, String description) {}
}
