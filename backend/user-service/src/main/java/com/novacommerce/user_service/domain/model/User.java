package com.novacommerce.user_service.domain.model;


import com.novacommerce.user_service.domain.enums.UserStatusEnum;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad que representa un usuario del sistema.
 * Los usuarios son cuentas de acceso para administrar la plataforma.
 * NO representa empleados, clientes u otras entidades de negocio.
 * Utiliza relación MANY-TO-MANY con Role.
 */
@Document
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    private String id;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String username;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
// @AllArgsConstructor
    @Builder.Default
    private UserStatusEnum status = UserStatusEnum.ACTIVE;

    @Builder.Default
    private Boolean enabled = true;

    @Builder.Default
    private Boolean locked = false;

    private String customerId;

    // Eliminado campo duplicado
        @Builder.Default
        private Set<String> roleIds = new HashSet<>(); // Referencia a los ids de roles en MongoDB

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLogin;

    /**
     * Agrega el id de un rol al usuario.
     *
     * @param roleId el id del rol a agregar
     */
    public void addRole(String roleId) {
        if (roleId != null && !roleId.isBlank()) {
            this.roleIds.add(roleId);
        }
    }

    /**
     * Remueve el id de un rol del usuario.
     *
     * @param roleId el id del rol a remover
     */
    public void removeRole(String roleId) {
        if (roleId != null && !roleId.isBlank()) {
            this.roleIds.remove(roleId);
        }
    }

    /**
     * Verifica si el usuario está activo y disponible para autenticación.
     *
     * @return true si el usuario está activo, no bloqueado y habilitado
     */
    public Boolean isAccountActive() {
        return UserStatusEnum.ACTIVE.equals(this.status) && this.enabled && !this.locked;
    }

    /**
     * Bloquea la cuenta del usuario.
     */
    public void lockAccount() {
        this.locked = true;
    }

    /**
     * Desbloquea la cuenta del usuario.
     */
    public void unlockAccount() {
        this.locked = false;
    }
}
