package com.example.counters.service;

import com.example.counters.exception.CounterLimitReachedException;
import com.example.counters.model.Counter;
import com.example.counters.repository.CounterRepository;
import lombok.RequiredArgsConstructor;
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

    public Mono<Counter> incrementCounter(Counter counter) {
        int value = counter.getValue();
        if (value == Integer.MAX_VALUE) {
            return Mono.error(new CounterLimitReachedException("Counter already reached the maximum value"));
        }
        counter.setValue(value + 1);
        return counterRepository.save(counter);
    }
}
