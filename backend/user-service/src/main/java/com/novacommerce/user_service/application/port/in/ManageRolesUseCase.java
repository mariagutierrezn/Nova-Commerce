package com.novacommerce.user_service.application.port.in;

import com.novacommerce.user_service.web.api.dto.request.CreateRoleRequest;
import com.novacommerce.user_service.web.api.dto.response.RoleResponse;

import java.util.List;

/**
 * Puerto de entrada para gestión de roles.
 * Define los casos de uso para operaciones CRUD de roles.
 */
public interface ManageRolesUseCase {

    /**
     * Obtiene todos los roles.
     */
    List<RoleResponse> getAllRoles();

    /**
     * Obtiene un rol por su UUID.
     */
    RoleResponse getRoleById(String id);

    /**
     * Crea un nuevo rol.
     */
    RoleResponse createRole(CreateRoleRequest createRoleRequest);

    /**
     * Elimina un rol.
     */
    void deleteRole(String id);
}
