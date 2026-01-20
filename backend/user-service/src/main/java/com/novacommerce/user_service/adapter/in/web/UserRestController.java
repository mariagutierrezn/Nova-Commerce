package com.novacommerce.user_service.adapter.in.web;

import com.novacommerce.user_service.application.port.in.ManageUsersUseCase;
import com.novacommerce.user_service.web.api.dto.request.CreateUserRequest;
import com.novacommerce.user_service.web.api.dto.response.UserResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// ...existing imports...

/**
 * Adaptador de entrada REST para gestión de usuarios.
 * Implementa Clean Architecture delegando en los casos de uso.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Users", description = "Endpoints de gestión de usuarios del sistema")
@SecurityRequirement(name = "bearerAuth")
public class UserRestController {

    private final ManageUsersUseCase manageUsersUseCase;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Listar usuarios",
        description = "Obtiene una lista paginada de usuarios. Solo ADMIN puede acceder."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida",
            content = @Content(schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "No tiene permisos (requiere ADMIN)")
    })
    public ResponseEntity<Page<UserResponse>> getAllUsers(Pageable pageable) {
        log.info("Solicitud de listado de usuarios");
        Page<UserResponse> users = manageUsersUseCase.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.isCurrentUser(#id)")
    @Operation(
        summary = "Obtener usuario por ID",
        description = "Obtiene los detalles de un usuario específico. ADMIN puede ver cualquier usuario, otros solo a sí mismos."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado",
            content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "No tiene permisos"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        log.info("Solicitud de usuario: {}", id);
        UserResponse user = manageUsersUseCase.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Crear usuario",
        description = "Crea un nuevo usuario. Solo ADMIN puede acceder."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario creado",
            content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "No tiene permisos (requiere ADMIN)"),
        @ApiResponse(responseCode = "409", description = "Usuario ya existe")
    })
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        log.info("Solicitud de creación de usuario: {}", createUserRequest.username());
        UserResponse createdUser = manageUsersUseCase.createUser(createUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.isCurrentUser(#id)")
    @Operation(
        summary = "Actualizar usuario",
        description = "Actualiza los datos de un usuario. ADMIN puede actualizar cualquier usuario, otros solo a sí mismos."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario actualizado",
            content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "No tiene permisos"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "409", description = "Username/Email ya existe")
    })
    public ResponseEntity<UserResponse> updateUser(
        @PathVariable String id,
        @Valid @RequestBody CreateUserRequest updateUserRequest
    ) {
        log.info("Solicitud de actualización de usuario: {}", id);
        UserResponse updatedUser = manageUsersUseCase.updateUser(id, updateUserRequest);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Eliminar usuario",
        description = "Elimina un usuario. Solo ADMIN puede acceder."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuario eliminado"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "No tiene permisos (requiere ADMIN)"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        log.info("Solicitud de eliminación de usuario: {}", id);
        manageUsersUseCase.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
