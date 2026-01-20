package com.novacommerce.user_service.adapter.in.web;

import com.novacommerce.user_service.application.port.in.ManageRolesUseCase;
import com.novacommerce.user_service.web.api.dto.request.CreateRoleRequest;
import com.novacommerce.user_service.web.api.dto.response.RoleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
// ...existing imports...

/**
 * Adaptador de entrada REST para gestión de roles.
 * Implementa Clean Architecture delegando en los casos de uso.
 */
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Roles", description = "Endpoints de gestión de roles del sistema")
@SecurityRequirement(name = "bearerAuth")
public class RoleRestController {

    private final ManageRolesUseCase manageRolesUseCase;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Listar roles",
        description = "Obtiene la lista completa de roles. Solo ADMIN puede acceder."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de roles obtenida",
            content = @Content(schema = @Schema(implementation = RoleResponse.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "No tiene permisos (requiere ADMIN)")
    })
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        log.info("Solicitud de listado de roles");
        List<RoleResponse> roles = manageRolesUseCase.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Obtener rol por ID",
        description = "Obtiene los detalles de un rol específico. Solo ADMIN puede acceder."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rol encontrado",
            content = @Content(schema = @Schema(implementation = RoleResponse.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "No tiene permisos (requiere ADMIN)"),
        @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    })
    public ResponseEntity<RoleResponse> getRoleById(@PathVariable String id) {
        log.info("Solicitud de rol: {}", id);
        RoleResponse role = manageRolesUseCase.getRoleById(id);
        return ResponseEntity.ok(role);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Crear rol",
        description = "Crea un nuevo rol con permisos asociados. Solo ADMIN puede acceder."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Rol creado",
            content = @Content(schema = @Schema(implementation = RoleResponse.class))),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "No tiene permisos (requiere ADMIN)"),
        @ApiResponse(responseCode = "409", description = "Rol ya existe")
    })
    public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody CreateRoleRequest createRoleRequest) {
        log.info("Solicitud de creación de rol: {}", createRoleRequest.name());
        RoleResponse createdRole = manageRolesUseCase.createRole(createRoleRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRole);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Eliminar rol",
        description = "Elimina un rol. Solo ADMIN puede acceder."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Rol eliminado"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "No tiene permisos (requiere ADMIN)"),
        @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    })
    public ResponseEntity<Void> deleteRole(@PathVariable String id) {
        log.info("Solicitud de eliminación de rol: {}", id);
        manageRolesUseCase.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}
