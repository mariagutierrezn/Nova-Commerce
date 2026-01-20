package com.novacommerce.user_service.application.port.out;

import com.novacommerce.user_service.domain.model.Role;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Puerto de salida para persistencia de roles.
 * Define las operaciones de repositorio necesarias para roles.
 */
public interface RolePersistencePort {

    /**
     * Encuentra todos los roles.
     */
    List<Role> findAll();

    /**
     * Encuentra un rol por su ID.
     */
    Optional<Role> findById(String id);

    /**
     * Encuentra un rol por su nombre.
     */
    Optional<Role> findByName(String name);

    /**
     * Encuentra roles por sus IDs.
     */
    Set<Role> findAllById(Set<String> ids);

    /**
     * Verifica si existe un rol con el nombre dado.
     */
    boolean existsByName(String name);

    /**
     * Guarda un rol.
     */
    Role save(Role role);

    /**
     * Elimina un rol por su ID.
     */
    void deleteById(String id);
}
