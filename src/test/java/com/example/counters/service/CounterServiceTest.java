package com.example.counters.service;

import com.example.counters.exception.CounterLimitReachedException;
import com.example.counters.model.Counter;
import com.example.counters.repository.CounterRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class CounterServiceTest {

    @Mock
    private CounterRepository counterRepository;

    @Mock
    private ReactiveMongoTemplate reactiveMongoTemplate;

    private CounterService counterService;

    @BeforeEach
    void setup() {
        counterService = new CounterService(counterRepository, reactiveMongoTemplate);
    }

    @Test
    void incrementCounterLimitExceeded() {
        Counter counter = new Counter("C1", Integer.MAX_VALUE);
        Mockito.when(counterRepository.findById(Mockito.anyString())).thenReturn(Mono.just(counter));
        Assertions.assertThrows(CounterLimitReachedException.class, () -> {
            counterService.incrementCounter("C1").block();
        });
    }
}