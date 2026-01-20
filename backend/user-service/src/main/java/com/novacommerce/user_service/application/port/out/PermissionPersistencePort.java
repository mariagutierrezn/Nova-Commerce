package com.novacommerce.user_service.application.port.out;

import com.novacommerce.user_service.domain.model.Permission;
import java.util.Set;

/**
 * Puerto de persistencia para permisos.
 */
public interface PermissionPersistencePort {
    Set<Permission> findAllById(Set<String> ids);
}
