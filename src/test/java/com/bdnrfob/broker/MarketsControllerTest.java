package com.bdnrfob.broker;

import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;

import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.List;

//import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
public class MarketsControllerTest {
    @Inject
    EmbeddedApplication<?> application;

    @Inject
    @Client("/")
    RxHttpClient client;

    @Test
    void returnListofMarkets(){

        final List<LinkedHashMap<String,String>> result = client.toBlocking().retrieve("/markets", List.class);
        assertEquals(9,result.size());
        assertThat(result)
                .extracting(entry -> entry.get("value"))
                .containsExactlyInAnyOrder("MGLU3", "AAPL", "AMZN", "PETR4", "FB", "GOOG", "MSFT", "NFLX", "TSLA");
    }

}
