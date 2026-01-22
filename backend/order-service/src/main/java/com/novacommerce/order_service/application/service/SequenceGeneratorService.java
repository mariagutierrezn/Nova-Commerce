package com.novacommerce.order_service.application.service;

import com.novacommerce.order_service.repository.entity.SequenceEntity;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class SequenceGeneratorService {
    
    private final MongoOperations mongoOperations;

    public SequenceGeneratorService(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    public Long generateSequence(String sequenceName) {
        Query query = new Query(Criteria.where("_id").is(sequenceName));
        Update update = new Update().inc("sequence", 1);
        FindAndModifyOptions options = FindAndModifyOptions.options()
                .returnNew(true)
                .upsert(true);
        
        SequenceEntity counter = mongoOperations.findAndModify(
                query, 
                update, 
                options, 
                SequenceEntity.class
        );
        
        return counter != null ? counter.getSequence() : 1001L;
    }
}
