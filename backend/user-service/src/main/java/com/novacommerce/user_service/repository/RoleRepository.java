package com.novacommerce.user_service.repository;


import com.novacommerce.user_service.domain.model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Role.
 * Proporciona métodos de acceso a datos para roles del sistema.
 */
@Repository
public interface RoleRepository extends MongoRepository<Role, String> {

    /**
     * Busca un rol por nombre.
     *
     * @param name el nombre del rol a buscar
     * @return Optional conteniendo el rol si existe
     */
    Optional<Role> findByName(String name);

    /**
     * Verifica si existe un rol con el nombre proporcionado.
     *
     * @param name el nombre del rol a verificar
     * @return true si el rol existe, false en caso contrario
     */
    boolean existsByName(String name);
}
