package com.novacommerce.user_service.application.port.in;

import com.novacommerce.user_service.web.api.dto.request.CreateUserRequest;
import com.novacommerce.user_service.web.api.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


/**
 * Puerto de entrada para gestión de usuarios.
 * Define los casos de uso para operaciones CRUD de usuarios.
 */
public interface ManageUsersUseCase {

    /**
     * Obtiene todos los usuarios con paginación.
     */
    Page<UserResponse> getAllUsers(Pageable pageable);

    /**
     * Obtiene un usuario por su UUID.
     */
    UserResponse getUserById(String id);

    /**
     * Crea un nuevo usuario.
     */
    UserResponse createUser(CreateUserRequest createUserRequest);

    /**
     * Actualiza un usuario existente.
     */
    UserResponse updateUser(String id, CreateUserRequest updateUserRequest);

    /**
     * Elimina un usuario.
     */
    void deleteUser(String id);
    
    /**
     * Actualiza el customerId de un usuario.
     */
    void updateCustomerId(String userId, String customerId);

    /**
     * Verifica si un usuario es el usuario actual autenticado.
     */
    boolean isCurrentUser(String id);
}
