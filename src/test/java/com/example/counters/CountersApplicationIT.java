package com.example.counters;

import com.example.counters.model.Counter;
import com.example.counters.model.CounterRequest;
import com.example.counters.repository.CounterRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "30000")
public class CountersApplicationIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private CounterRepository counterRepository;

    @Test
    public void createValidCounter_Then200Response() {
        int counterValue = 100;
        postRequestForCreatingCounter(counterValue)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.name").isNotEmpty()
                .jsonPath("$.value").isEqualTo(counterValue);
    }

    @Test
    public void createInvalidCounter_Then400Response() {
        webTestClient.post()
                .uri("/counters")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just("{" +
                        "\"value\": \"abc\"" +
                        "}"), String.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.reason").isEqualTo("Invalid Request");
    }

    @Test
    public void getValidCounterByName_Then200Response() {
        Counter c1 = counterRepository.save(new Counter("C1", 1)).block();

        getRequestForCounterByName("C1")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Counter.class)
                .consumeWith(response -> Assert.assertEquals(c1, response.getResponseBody()));

        counterRepository.delete(c1).block();
    }

    @Test
    public void getNonExistingCounterByName_Then404Response() {
        getRequestForCounterByName("C1")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void getAllCounters_Then200Response() {
        Counter c1 = new Counter("C1", 1);
        Counter c2 = new Counter("C2", 2);
        Counter c3 = new Counter("C3", 3);

        counterRepository.save(c1);
        counterRepository.save(c2);
        counterRepository.save(c3);

        List<Counter> countersList = counterRepository.findAll().collectList().block();

        webTestClient.get().uri("/counters")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Counter.class)
                .consumeWith(list -> Assert.assertEquals(countersList, list.getResponseBody()));

        counterRepository.delete(c1).block();
        counterRepository.delete(c2).block();
        counterRepository.delete(c3).block();
    }

    @Test
    public void incrementValidCounterByName_Then200Response() {
        Counter c1 = counterRepository.save(new Counter("C1", 1)).block();

        webTestClient.patch()
                .uri("/counters/{name}/increment", "C1")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Counter.class)
                .consumeWith(response -> Assert.assertEquals(Integer.valueOf(2), response.getResponseBody().getValue()));

        counterRepository.delete(c1).block();
    }

    @Test
    public void incrementInvalidCounterByName_Then404Response() {
        webTestClient.patch()
                .uri("/counters/{name}/increment", "C1")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void incrementCounterByName_WhenLimitReached_Then400Response() {
        Counter c1 = counterRepository.save(new Counter("C1", Integer.MAX_VALUE)).block();

        webTestClient.patch()
                .uri("/counters/{name}/increment", "C1")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.reason").isEqualTo("Invalid Request")
                .jsonPath("$.message").isEqualTo("Counter already reached the maximum value");

        counterRepository.delete(c1).block();
    }

    private WebTestClient.RequestHeadersSpec<?> getRequestForCounterByName(String name) {
        return webTestClient.get()
                .uri("/counters/{name}", name)
                .accept(MediaType.APPLICATION_JSON);
    }

    private WebTestClient.RequestHeadersSpec<?> postRequestForCreatingCounter(int value) {
        CounterRequest counterRequest = new CounterRequest(value);
        return webTestClient.post()
                .uri("/counters")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(counterRequest), CounterRequest.class);
    }
}
