package com.novacommerce.user_service.domain.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Entidad que representa un rol en el sistema.
 * Un rol es un conjunto de permisos que pueden ser asignados a usuarios.
 * Utiliza relación MANY-TO-MANY con Permission.
 */
@Document(collection = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    private String id;

    @NotBlank(message = "El nombre del rol es obligatorio")
    private String name;

    private String description;

    @Builder.Default
    private Set<String> permissionIds = new HashSet<>();

    /**
     * Agrega el id de un permiso al rol.
     *
     * @param permissionId el id del permiso a agregar
     */
    public void addPermission(String permissionId) {
        if (permissionId != null && !permissionId.isBlank()) {
            this.permissionIds.add(permissionId);
        }
    }

    /**
     * Remueve el id de un permiso del rol.
     *
     * @param permissionId el id del permiso a remover
     */
    public void removePermission(String permissionId) {
        if (permissionId != null && !permissionId.isBlank()) {
            this.permissionIds.remove(permissionId);
        }
    }
}
