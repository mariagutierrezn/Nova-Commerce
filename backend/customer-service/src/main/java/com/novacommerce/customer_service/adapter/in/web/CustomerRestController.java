package com.novacommerce.customer_service.adapter.in.web;

import com.novacommerce.customer_service.adapter.in.web.dto.CustomerDto;
import com.novacommerce.customer_service.adapter.in.web.mapper.CustomerMapper;
import com.novacommerce.customer_service.application.port.in.ManageCustomersUseCase;
import com.novacommerce.customer_service.domain.model.Customer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customers", description = "Endpoints de gestión de clientes del sistema")
public class CustomerRestController {

    private final ManageCustomersUseCase manageCustomersUseCase;
    private final CustomerMapper mapper;

    public CustomerRestController(ManageCustomersUseCase manageCustomersUseCase, CustomerMapper mapper) {
        this.manageCustomersUseCase = manageCustomersUseCase;
        this.mapper = mapper;
    }

    @GetMapping
    // @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_READ')") // TEMPORAL: Comentado para permitir acceso público
    @Operation(summary = "Listar clientes", description = "Obtiene la lista completa de clientes")
    public ResponseEntity<List<CustomerDto>> getAll() {
        List<CustomerDto> list = manageCustomersUseCase.findAll()
                .stream().map(mapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    // @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_READ')") // TEMPORAL: Comentado para permitir acceso público
    @Operation(summary = "Obtener cliente por ID", description = "Retorna el cliente correspondiente al identificador proporcionado")
    public ResponseEntity<CustomerDto> getById(@PathVariable String id) {
        Optional<Customer> opt = manageCustomersUseCase.findById(id);
        return opt.map(customer -> ResponseEntity.ok(mapper.toDto(customer)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CREATE')")
    @Operation(summary = "Crear cliente", description = "Crea un nuevo cliente en el sistema")
    public ResponseEntity<CustomerDto> create(@Valid @RequestBody CustomerDto dto) {
        Customer created = manageCustomersUseCase.create(mapper.toDomain(dto));
        return new ResponseEntity<>(mapper.toDto(created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    // @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_UPDATE')") // TEMPORAL: Comentado para permitir actualización pública
    @Operation(summary = "Actualizar cliente", description = "Actualiza los datos de un cliente existente")
    public ResponseEntity<CustomerDto> update(@PathVariable String id, @RequestBody CustomerDto dto) {
        Customer updated = manageCustomersUseCase.update(id, mapper.toDomain(dto));
        return ResponseEntity.ok(mapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_DELETE')")
    @Operation(summary = "Eliminar cliente", description = "Elimina un cliente por su identificador")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        manageCustomersUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
