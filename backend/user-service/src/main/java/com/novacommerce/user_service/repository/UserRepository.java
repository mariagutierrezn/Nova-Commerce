package com.novacommerce.user_service.repository;

import com.novacommerce.user_service.domain.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.Query;
import java.util.Optional;

/**
 * Repositorio para la entidad User.
 * Proporciona métodos de acceso a datos para usuarios del sistema.
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    /**
     * Busca un usuario por nombre de usuario o email.
     * Importante para el endpoint de validación de auth-service.
     *
     * @param userIdentifier nombre de usuario o email
     * @return Optional con el usuario si existe
     */
    @Query("{ '$or': [ { 'username': ?0 }, { 'email': ?0 } ] }")
    Optional<User> findByUsernameOrEmail(String identifier);
}
