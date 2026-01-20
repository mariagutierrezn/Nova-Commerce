package com.novacommerce.user_service.adapter.in.web;

import com.novacommerce.user_service.application.port.in.ManageUsersUseCase;
import com.novacommerce.user_service.application.port.in.ValidateUserCredentialsUseCase;
import com.novacommerce.user_service.web.api.dto.request.CreateUserRequest;
import com.novacommerce.user_service.web.api.dto.request.InternalUserValidationRequest;
import com.novacommerce.user_service.web.api.dto.response.InternalUserValidationResponse;
import com.novacommerce.user_service.web.api.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// ...existing imports...

/**
 * Adaptador de entrada REST para endpoints internos de usuarios.
 * Implementa Clean Architecture delegando en los casos de uso.
 * 
 * IMPORTANTE: Este controlador debe estar protegido con API Key
 * para que solo auth-service pueda acceder.
 */
@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Internal User Management", description = "Endpoints internos para gestión de usuarios")
@Hidden
public class InternalUserRestController {

    private final ValidateUserCredentialsUseCase validateUserCredentialsUseCase;
    private final ManageUsersUseCase manageUsersUseCase;

    @PostMapping("/validate")
    @Operation(
        summary = "Validar credenciales de usuario",
        description = "Valida las credenciales de un usuario. Endpoint interno protegido con API Key, llamado por auth-service.",
        security = {}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Credenciales válidas",
            content = @Content(schema = @Schema(implementation = InternalUserValidationResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas o API Key inválida")
    })
    public ResponseEntity<InternalUserValidationResponse> validateCredentials(
        @Valid @RequestBody InternalUserValidationRequest request) {
        
        log.info("Validación de credenciales solicitada para: {}", request.userIdentifier());
        
        InternalUserValidationResponse response = validateUserCredentialsUseCase.validateCredentials(
            request.userIdentifier(),
            request.password()
        );
        
        log.info("Credenciales validadas exitosamente para: {}", request.userIdentifier());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(
        summary = "Crear usuario (interno)",
        description = "Crea un nuevo usuario desde auth-service. Endpoint interno protegido con API Key.",
        security = {}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Usuario creado",
            content = @Content(schema = @Schema(implementation = UserResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "API Key inválida"),
        @ApiResponse(responseCode = "409", description = "Usuario ya existe")
    })
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("Solicitud de creación de usuario interno: {}", request.username());
        UserResponse createdUser = manageUsersUseCase.createUser(request);
        log.info("Usuario creado exitosamente: {}", createdUser.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
    
    @PutMapping("/{userId}/customer/{customerId}")
    @Operation(
        summary = "Actualizar customerId de usuario (interno)",
        description = "Asocia un customerId a un usuario existente. Endpoint interno protegido con API Key.",
        security = {}
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "CustomerId actualizado"),
        @ApiResponse(responseCode = "401", description = "API Key inválida"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<Void> updateCustomerId(
        @PathVariable String userId,
        @PathVariable String customerId) {
        
        log.info("Actualizando customerId {} para usuario: {}", customerId, userId);
        manageUsersUseCase.updateCustomerId(userId, customerId);
        log.info("CustomerId actualizado exitosamente para usuario: {}", userId);
        return ResponseEntity.noContent().build();
    }
}
