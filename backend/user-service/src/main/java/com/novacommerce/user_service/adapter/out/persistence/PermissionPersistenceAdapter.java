package com.novacommerce.user_service.adapter.out.persistence;

import com.novacommerce.user_service.application.port.out.PermissionPersistencePort;
import com.novacommerce.user_service.domain.model.Permission;
import com.novacommerce.user_service.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Adaptador de salida para persistencia de permisos.
 * Implementa PermissionPersistencePort usando Spring Data MongoDB.
 */
@Component
@RequiredArgsConstructor
public class PermissionPersistenceAdapter implements PermissionPersistencePort {

    private final PermissionRepository permissionRepository;

    @Override
    public Set<Permission> findAllById(Set<String> ids) {
        return new HashSet<>(permissionRepository.findAllById(ids));
    }
}
