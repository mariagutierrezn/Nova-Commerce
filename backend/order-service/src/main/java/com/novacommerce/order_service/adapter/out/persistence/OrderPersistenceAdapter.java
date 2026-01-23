package com.novacommerce.order_service.adapter.out.persistence;

import com.novacommerce.order_service.application.port.out.OrderPersistencePort;
import com.novacommerce.order_service.domain.model.Order;
import com.novacommerce.order_service.repository.OrderRepository;
import com.novacommerce.order_service.repository.entity.OrderEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de persistencia para órdenes.
 * Implementa el puerto de salida usando MongoDB.
 */
@Component
@RequiredArgsConstructor
public class OrderPersistenceAdapter implements OrderPersistencePort {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    public Order save(Order order) {
        OrderEntity entity = orderMapper.toEntity(order);
        entity.setTimestamps();
        OrderEntity saved = orderRepository.save(entity);
        return orderMapper.toDomain(saved);
    }

    @Override
    public Optional<Order> findById(String id) {
        return orderRepository.findById(id)
                .map(orderMapper::toDomain);
    }

    @Override
    public List<Order> findByCustomerId(String customerId) {
        // Retorna órdenes ordenadas por fecha descendente (más reciente primero)
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId).stream()
                .map(orderMapper::toDomain)
                .toList();
    }

    @Override
    public List<Order> findAll() {
        // Retorna todas las órdenes ordenadas por fecha descendente (más reciente primero)
        return orderRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(orderMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(String id) {
        orderRepository.deleteById(id);
    }
}
