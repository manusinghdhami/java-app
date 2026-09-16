package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AppTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void homeReturnsOk() {
        String body = restTemplate.getForObject("/", String.class);
        assertThat(body).contains("Hello from Java CI/CD");
    }
}
