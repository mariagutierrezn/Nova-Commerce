package com.novacommerce.order_service.repository.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "sequences")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SequenceEntity {
    @Id
    private String id;
    private Long sequence;
}
