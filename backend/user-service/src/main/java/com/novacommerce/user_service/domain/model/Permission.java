package com.novacommerce.user_service.domain.model;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

import jakarta.validation.constraints.NotBlank;
import lombok.*;


/**
 * Entidad que representa un permiso del sistema.
 * Un permiso define una acción específica que puede ser realizada.
 * Los permisos se asignan a roles, y los roles se asignan a usuarios.
 */
@Document(collection = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {

    @Id
    private String id;

    @NotBlank(message = "El nombre del permiso es obligatorio")
    private String name;

    private String description;
}
