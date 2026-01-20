package com.novacommerce.user_service.application.service;

import com.novacommerce.user_service.application.port.in.ManageUsersUseCase;
import com.novacommerce.user_service.application.port.in.ValidateUserCredentialsUseCase;
import com.novacommerce.user_service.application.port.out.PasswordEncoderPort;
import com.novacommerce.user_service.application.port.out.RolePersistencePort;
import com.novacommerce.user_service.application.port.out.UserPersistencePort;
import com.novacommerce.user_service.domain.model.Role;
import com.novacommerce.user_service.domain.model.User;
import com.novacommerce.user_service.service.mapper.UserMapper;
import com.novacommerce.user_service.web.api.dto.request.CreateUserRequest;
import com.novacommerce.user_service.web.api.dto.response.InternalUserValidationResponse;
import com.novacommerce.user_service.web.api.dto.response.UserResponse;
import com.novacommerce.user_service.web.rest.exceptions.DuplicateResourceException;
import com.novacommerce.user_service.web.rest.exceptions.InvalidCredentialsException;
import com.novacommerce.user_service.web.rest.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
// ...existing imports...
import java.util.stream.Collectors;

/**
 * Servicio de aplicación que implementa los casos de uso de gestión de usuarios.
 * Orquesta las operaciones usando los puertos definidos (Clean Architecture).
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserService implements ManageUsersUseCase, ValidateUserCredentialsUseCase {

    private final UserPersistencePort userPersistencePort;
    private final RolePersistencePort rolePersistencePort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        log.info("Obteniendo usuarios con paginación");
        return userPersistencePort.findAll(pageable)
            .map(userMapper::userToUserResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(String id) {
        log.info("Obteniendo usuario: {}", id);
        return userPersistencePort.findById(id)
            .map(userMapper::userToUserResponse)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    @Override
    public UserResponse createUser(CreateUserRequest createUserRequest) {
        log.info("Creando usuario: {}", createUserRequest.username());

        if (userPersistencePort.existsByUsername(createUserRequest.username())) {
            throw new DuplicateResourceException("User", "username", createUserRequest.username());
        }

        if (userPersistencePort.existsByEmail(createUserRequest.email())) {
            throw new DuplicateResourceException("User", "email", createUserRequest.email());
        }

        String encryptedPassword = passwordEncoderPort.encode(createUserRequest.password());
        Set<String> roleIds = createUserRequest.roleIds();
        Set<Role> roles = loadRoles(roleIds);
        
        // Si no se especifican roles, asignar rol USER por defecto
        if (roles.isEmpty()) {
            roles = new HashSet<>();
            Role defaultUserRole = rolePersistencePort.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Rol USER no encontrado en el sistema"));
            roles.add(defaultUserRole);
            log.debug("Rol USER asignado por defecto al usuario: {}", createUserRequest.username());
        }

        User user = User.builder()
            .username(createUserRequest.username())
            .email(createUserRequest.email())
            .password(encryptedPassword)
            .roleIds(roles.stream().map(Role::getId).collect(Collectors.toSet()))
            .customerId(createUserRequest.customerId())
            .build();

        User savedUser = userPersistencePort.save(user);
        log.info("Usuario creado exitosamente: {}", savedUser.getId());

        return userMapper.userToUserResponse(savedUser);
    }

    @Override
    public UserResponse updateUser(String id, CreateUserRequest updateUserRequest) {
        log.info("Actualizando usuario: {}", id);

        User user = userPersistencePort.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        if (!user.getUsername().equals(updateUserRequest.username()) && userPersistencePort.existsByUsername(updateUserRequest.username())) {
            throw new DuplicateResourceException("User", "username", updateUserRequest.username());
        }
        if (!user.getEmail().equals(updateUserRequest.email()) && userPersistencePort.existsByEmail(updateUserRequest.email())) {
            throw new DuplicateResourceException("User", "email", updateUserRequest.email());
        }

        user.setUsername(updateUserRequest.username());
        user.setEmail(updateUserRequest.email());
        user.setPassword(passwordEncoderPort.encode(updateUserRequest.password()));
        Set<String> roleIds = updateUserRequest.roleIds();
        Set<Role> roles = loadRoles(roleIds);
        user.setRoleIds(roles.stream().map(Role::getId).collect(Collectors.toSet()));

        User updatedUser = userPersistencePort.save(user);
        log.info("Usuario actualizado exitosamente: {}", id);

        return userMapper.userToUserResponse(updatedUser);
    }

    @Override
    public void deleteUser(String id) {
        log.info("Eliminando usuario: {}", id);

        if (!userPersistencePort.findById(id).isPresent()) {
            throw new ResourceNotFoundException("User", "id", id);
        }

        userPersistencePort.deleteById(id);
        log.info("Usuario eliminado exitosamente: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCurrentUser(String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String currentUsername = authentication.getName();
        User user = userPersistencePort.findById(id).orElse(null);

        return user != null && user.getUsername().equals(currentUsername);
    }
    
    @Override
    public void updateCustomerId(String userId, String customerId) {
        log.info("Actualizando customerId {} para usuario: {}", customerId, userId);
        
        User user = userPersistencePort.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        user.setCustomerId(customerId);
        userPersistencePort.save(user);
        
        log.info("CustomerId actualizado exitosamente para usuario: {}", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public InternalUserValidationResponse validateCredentials(String userIdentifier, String password) {
        if (!StringUtils.hasText(userIdentifier) || !StringUtils.hasText(password)) {
            log.warn("Intento de validación con credenciales vacías");
            throw new InvalidCredentialsException("Username/Email y contraseña son requeridos");
        }

        log.debug("Validando credenciales para: {}", userIdentifier);

        User user = userPersistencePort.findByUsernameOrEmail(userIdentifier)
            .orElseThrow(() -> {
                log.warn("Usuario no encontrado: {}", userIdentifier);
                return new InvalidCredentialsException("Credenciales inválidas");
            });

        if (!passwordEncoderPort.matches(password, user.getPassword())) {
            log.warn("Contraseña incorrecta para usuario: {}", userIdentifier);
            throw new InvalidCredentialsException("Credenciales inválidas");
        }

        if (Boolean.FALSE.equals(user.getEnabled())) {
            log.warn("Intento de login con usuario deshabilitado: {}", user.getUsername());
            throw new InvalidCredentialsException("Usuario deshabilitado");
        }
        if (Boolean.TRUE.equals(user.getLocked())) {
            log.warn("Intento de login con usuario bloqueado: {}", user.getUsername());
            throw new InvalidCredentialsException("Usuario bloqueado");
        }

        user.setLastLogin(LocalDateTime.now());
        userPersistencePort.save(user);

        Set<String> roleNames = new HashSet<>();
        Set<String> permissions = new HashSet<>();
        if (user.getRoleIds() != null && !user.getRoleIds().isEmpty()) {
            Set<Role> userRoles = rolePersistencePort.findAllById(user.getRoleIds());
            roleNames = userRoles.stream().map(Role::getName).collect(Collectors.toSet());
            permissions = userRoles.stream()
                .flatMap(role -> {
                    if (role.getPermissionIds() != null && !role.getPermissionIds().isEmpty()) {
                        // You may want to fetch Permission objects here if needed
                        return role.getPermissionIds().stream();
                    } else {
                        return new HashSet<String>().stream();
                    }
                })
                .collect(Collectors.toSet());
        }

        log.info("Credenciales validadas exitosamente para: {}", user.getUsername());

        return InternalUserValidationResponse.builder()
            .username(user.getUsername())
            .email(user.getEmail())
            .roles(roleNames)
            .permissions(permissions)
            .enabled(user.getEnabled())
            .locked(user.getLocked())
            .customerId(user.getCustomerId())
            .build();
    }

    private Set<Role> loadRoles(Set<String> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Set.of();
        }
        Set<Role> roles = rolePersistencePort.findAllById(roleIds);
        if (roles.size() != roleIds.size()) {
            Set<String> foundIds = roles.stream()
                .map(Role::getId)
                .collect(Collectors.toSet());
            Set<String> notFoundIds = roleIds.stream()
                .filter(id -> !foundIds.contains(id))
                .collect(Collectors.toSet());
            log.warn("Roles no encontrados: {}", notFoundIds);
            notFoundIds.forEach(id ->
                log.warn("Rol no encontrado: {}", id)
            );
        }
        return roles;
    }
}
