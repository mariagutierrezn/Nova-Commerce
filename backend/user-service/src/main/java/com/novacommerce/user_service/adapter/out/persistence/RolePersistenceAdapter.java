package com.novacommerce.user_service.adapter.out.persistence;

import com.novacommerce.user_service.application.port.out.RolePersistencePort;
import com.novacommerce.user_service.domain.model.Role;
import com.novacommerce.user_service.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
// ...existing imports...

/**
 * Adaptador de salida para persistencia de roles.
 * Implementa RolePersistencePort usando Spring Data MongoDB.
 */
@Component
@RequiredArgsConstructor
public class RolePersistenceAdapter implements RolePersistencePort {

    private final RoleRepository roleRepository;

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    public Optional<Role> findById(String id) {
        return roleRepository.findById(id);
    }

    @Override
    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }

    @Override
    public Set<Role> findAllById(Set<String> ids) {
        return new HashSet<>(roleRepository.findAllById(ids));
    }

    @Override
    public boolean existsByName(String name) {
        return roleRepository.existsByName(name);
    }

    @Override
    public Role save(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public void deleteById(String id) {
        roleRepository.deleteById(id);
    }
}
