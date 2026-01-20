package com.novacommerce.user_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Entidad de dominio que representa un token JWT.
 * No es un documento de MongoDB, es un concepto del dominio de seguridad.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Token {
    private String value;
    private String username;
    private String authorities;
    private boolean valid;
}
