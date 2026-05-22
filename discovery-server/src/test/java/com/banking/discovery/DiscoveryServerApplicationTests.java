package com.banking.discovery;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "eureka.server.enable-self-preservation=false"
})
class DiscoveryServerApplicationTests {

    @Test
    void contextLoads() {
        // Verifies Eureka server auto-configuration starts without errors
    }
}
