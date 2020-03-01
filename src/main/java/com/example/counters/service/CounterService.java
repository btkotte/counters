package com.example.counters.service;

import com.example.counters.exception.CounterLimitReachedException;
import com.example.counters.model.Counter;
import com.example.counters.repository.CounterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CounterService {

    private final CounterRepository counterRepository;
    final ReactiveMongoTemplate template;

    public Mono<Counter> create(Integer value) {
        return counterRepository.save(Counter.builder()
                .name(UUID.randomUUID().toString())
                .value(value)
                .build());
    }

    public Mono<Counter> readOne(String id) {
        return counterRepository.findById(id);
    }

    public Flux<Counter> readAll() {
        return counterRepository.findAll();
    }

    public Mono<Counter> incrementCounter(String id) {
        return counterRepository.findById(id)
                .flatMap(counter -> {
                    if (counter.getValue() == Integer.MAX_VALUE) {
                        return Mono.error(new CounterLimitReachedException("Counter already reached the maximum value"));
                    }
                    Query query = new Query();
                    query.addCriteria(Criteria.where("name").is(id));
                    Update update = new Update();
                    update.inc("value");
                    return template.findAndModify(query, update, Counter.class);
                });
    }

}
