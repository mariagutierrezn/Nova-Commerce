package com.novacommerce.customer_service.adapter.in.web;

import com.novacommerce.customer_service.adapter.in.web.dto.CustomerDto;
import com.novacommerce.customer_service.adapter.in.web.mapper.CustomerMapper;
import com.novacommerce.customer_service.application.port.in.ManageCustomersUseCase;
import com.novacommerce.customer_service.domain.model.Customer;
import com.novacommerce.customer_service.domain.model.enums.CustomerStatus;
import com.novacommerce.customer_service.domain.model.enums.LoyaltyLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerRestController Tests")
class CustomerRestControllerTest {

    @Mock
    private ManageCustomersUseCase manageCustomersUseCase;

    @Mock
    private CustomerMapper mapper;

    @InjectMocks
    private CustomerRestController controller;

    @Test
    @DisplayName("givenCustomers_whenGetAll_thenReturnList")
    void givenCustomers_whenGetAll_thenReturnList() {
        // GIVEN
        Customer customer1 = new Customer("1", "Juan", "Pérez", "juan@example.com", "123", CustomerStatus.ACTIVE, LoyaltyLevel.GOLD);
        Customer customer2 = new Customer("2", "Ana", "García", "ana@example.com", "456", CustomerStatus.ACTIVE, LoyaltyLevel.SILVER);
        
        CustomerDto dto1 = new CustomerDto();
        dto1.setId("1");
        dto1.setFirstName("Juan");
        
        CustomerDto dto2 = new CustomerDto();
        dto2.setId("2");
        dto2.setFirstName("Ana");
        
        when(manageCustomersUseCase.findAll()).thenReturn(List.of(customer1, customer2));
        when(mapper.toDto(customer1)).thenReturn(dto1);
        when(mapper.toDto(customer2)).thenReturn(dto2);

        // WHEN
        ResponseEntity<List<CustomerDto>> response = controller.getAll();

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(manageCustomersUseCase).findAll();
        verify(mapper, times(2)).toDto(any(Customer.class));
    }

    @Test
    @DisplayName("givenExistingId_whenGetById_thenReturnCustomer")
    void givenExistingId_whenGetById_thenReturnCustomer() {
        // GIVEN
        String id = "1";
        Customer customer = new Customer(id, "Juan", "Pérez", "juan@example.com", "123", CustomerStatus.ACTIVE, LoyaltyLevel.GOLD);
        CustomerDto dto = new CustomerDto();
        dto.setId(id);
        
        when(manageCustomersUseCase.findById(id)).thenReturn(Optional.of(customer));
        when(mapper.toDto(customer)).thenReturn(dto);

        // WHEN
        ResponseEntity<CustomerDto> response = controller.getById(id);

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(id, response.getBody().getId());
        verify(manageCustomersUseCase).findById(id);
        verify(mapper).toDto(customer);
    }

    @Test
    @DisplayName("givenNonExistingId_whenGetById_thenReturnNotFound")
    void givenNonExistingId_whenGetById_thenReturnNotFound() {
        // GIVEN
        String id = "999";
        when(manageCustomersUseCase.findById(id)).thenReturn(Optional.empty());

        // WHEN
        ResponseEntity<CustomerDto> response = controller.getById(id);

        // THEN
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(manageCustomersUseCase).findById(id);
        verify(mapper, never()).toDto(any(Customer.class));
    }

    @Test
    @DisplayName("givenValidDto_whenCreate_thenReturnCreated")
    void givenValidDto_whenCreate_thenReturnCreated() {
        // GIVEN
        CustomerDto inputDto = new CustomerDto();
        inputDto.setFirstName("Juan");
        inputDto.setLastName("Pérez");
        inputDto.setEmail("juan@example.com");
        
        Customer domainCustomer = new Customer(null, "Juan", "Pérez", "juan@example.com", null, CustomerStatus.ACTIVE, LoyaltyLevel.BRONZE);
        Customer createdCustomer = new Customer("1", "Juan", "Pérez", "juan@example.com", null, CustomerStatus.ACTIVE, LoyaltyLevel.BRONZE);
        
        CustomerDto outputDto = new CustomerDto();
        outputDto.setId("1");
        outputDto.setFirstName("Juan");
        
        when(mapper.toDomain(inputDto)).thenReturn(domainCustomer);
        when(manageCustomersUseCase.create(domainCustomer)).thenReturn(createdCustomer);
        when(mapper.toDto(createdCustomer)).thenReturn(outputDto);

        // WHEN
        ResponseEntity<CustomerDto> response = controller.create(inputDto);

        // THEN
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("1", response.getBody().getId());
        verify(mapper).toDomain(inputDto);
        verify(manageCustomersUseCase).create(domainCustomer);
        verify(mapper).toDto(createdCustomer);
    }

    @Test
    @DisplayName("givenValidDto_whenUpdate_thenReturnUpdated")
    void givenValidDto_whenUpdate_thenReturnUpdated() {
        // GIVEN
        String id = "1";
        CustomerDto inputDto = new CustomerDto();
        inputDto.setFirstName("Juan Updated");
        inputDto.setLastName("Pérez");
        inputDto.setEmail("juan@example.com");
        
        Customer domainCustomer = new Customer(null, "Juan Updated", "Pérez", "juan@example.com", null, CustomerStatus.ACTIVE, LoyaltyLevel.GOLD);
        Customer updatedCustomer = new Customer(id, "Juan Updated", "Pérez", "juan@example.com", null, CustomerStatus.ACTIVE, LoyaltyLevel.GOLD);
        
        CustomerDto outputDto = new CustomerDto();
        outputDto.setId(id);
        outputDto.setFirstName("Juan Updated");
        
        when(mapper.toDomain(inputDto)).thenReturn(domainCustomer);
        when(manageCustomersUseCase.update(eq(id), eq(domainCustomer))).thenReturn(updatedCustomer);
        when(mapper.toDto(updatedCustomer)).thenReturn(outputDto);

        // WHEN
        ResponseEntity<CustomerDto> response = controller.update(id, inputDto);

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(id, response.getBody().getId());
        assertEquals("Juan Updated", response.getBody().getFirstName());
        verify(mapper).toDomain(inputDto);
        verify(manageCustomersUseCase).update(id, domainCustomer);
        verify(mapper).toDto(updatedCustomer);
    }

    @Test
    @DisplayName("givenId_whenDelete_thenReturnNoContent")
    void givenId_whenDelete_thenReturnNoContent() {
        // GIVEN
        String id = "1";
        doNothing().when(manageCustomersUseCase).delete(id);

        // WHEN
        ResponseEntity<Void> response = controller.delete(id);

        // THEN
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(manageCustomersUseCase).delete(id);
    }

    @Test
    @DisplayName("givenEmptyList_whenGetAll_thenReturnEmptyList")
    void givenEmptyList_whenGetAll_thenReturnEmptyList() {
        // GIVEN
        when(manageCustomersUseCase.findAll()).thenReturn(List.of());

        // WHEN
        ResponseEntity<List<CustomerDto>> response = controller.getAll();

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(manageCustomersUseCase).findAll();
    }
}
