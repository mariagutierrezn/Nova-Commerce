package com.novacommerce.customer_service.adapter.in.web;

import com.novacommerce.customer_service.adapter.in.web.dto.CustomerDto;
import com.novacommerce.customer_service.adapter.in.web.mapper.CustomerMapper;
import com.novacommerce.customer_service.application.port.in.ManageCustomersUseCase;
import com.novacommerce.customer_service.domain.model.Customer;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

class InternalCustomerResponse {
    public String id;
    public String firstName;
    public String lastName;
    public String email;
    public String phone;
    public String status;
    public String loyaltyLevel;

    static InternalCustomerResponse from(Customer c) {
        InternalCustomerResponse r = new InternalCustomerResponse();
        r.id = c.getId();
        r.firstName = c.getFirstName();
        r.lastName = c.getLastName();
        r.email = c.getEmail();
        r.phone = c.getPhone();
        r.status = c.getStatus() != null ? c.getStatus().name() : null;
        r.loyaltyLevel = c.getLoyaltyLevel() != null ? c.getLoyaltyLevel().name() : null;
        return r;
    }
}

/**
 * Endpoints internos para consumo entre microservicios.
 * Protegidos por InternalApiKeyFilter y abiertos en SecurityConfig.
 */
@Slf4j
@RestController
@RequestMapping("/internal/customers")
public class InternalCustomerController {

    private final ManageCustomersUseCase manageCustomersUseCase;
    private final CustomerMapper mapper;

    public InternalCustomerController(ManageCustomersUseCase manageCustomersUseCase, CustomerMapper mapper) {
        this.manageCustomersUseCase = manageCustomersUseCase;
        this.mapper = mapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<InternalCustomerResponse> getByIdInternal(@PathVariable String id) {
        return manageCustomersUseCase.findById(id)
            .map(c -> ResponseEntity.ok(InternalCustomerResponse.from(c)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<InternalCustomerResponse> createCustomerInternal(@Valid @RequestBody CustomerDto request) {
        log.info("Solicitud de creación de cliente interno: {}", request.getEmail());
        Customer created = manageCustomersUseCase.create(mapper.toDomain(request));
        log.info("Cliente creado exitosamente con ID: {}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(InternalCustomerResponse.from(created));
    }
}
