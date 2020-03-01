package com.example.counters.resource;

import com.example.counters.model.Counter;
import com.example.counters.model.CounterRequest;
import com.example.counters.service.CounterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/counters")
public class CounterResource {
    private final CounterService counterService;

    @GetMapping()
    public Flux<Counter> getAll() {
        return counterService.readAll();
    }

    @GetMapping("/{name}")
    public Mono<ResponseEntity<Counter>> getId(@PathVariable("name") final String name) {
        return counterService.readOne(name)
                .map(counter -> new ResponseEntity<>(counter, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PatchMapping("/{name}/increment")
    public Mono<ResponseEntity<Counter>> increment(@PathVariable("name") final String name) {
        return counterService.incrementCounter(name)
                .map(updatedCounter -> new ResponseEntity<>(updatedCounter, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public Mono<Counter> create(@RequestBody @Valid final CounterRequest counterRequest) {
        return counterService.create(counterRequest.getValue());
    }
}
