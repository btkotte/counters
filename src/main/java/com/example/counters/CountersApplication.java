package com.example.counters;

import com.example.counters.service.CounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableReactiveMongoRepositories
public class CountersApplication {

    @Autowired
    CounterService counterService;

    public static void main(String[] args) {
        SpringApplication.run(CountersApplication.class, args);
    }

}
