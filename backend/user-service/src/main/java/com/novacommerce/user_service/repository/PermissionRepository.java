package com.novacommerce.user_service.repository;


import com.novacommerce.user_service.domain.model.Permission;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Permission.
 * Proporciona métodos de acceso a datos para permisos del sistema.
 */
@Repository
public interface PermissionRepository extends MongoRepository<Permission, String> {

    /**
     * Busca un permiso por nombre.
     *
     * @param name el nombre del permiso a buscar
     * @return Optional conteniendo el permiso si existe
     */
    Optional<Permission> findByName(String name);
}
