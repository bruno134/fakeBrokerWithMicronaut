package com.bdnrfob;

import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
class HelloWorldControllerTest {
    private final static Logger LOG = LoggerFactory.getLogger(HelloWorldControllerTest.class);
    @Inject
    EmbeddedApplication<?> application;

    @Inject
    @Client("/hello")
    RxHttpClient client;

    @Test
    void testItWorks() {
        Assertions.assertTrue(application.isRunning());
    }

    @Test
    void testHelloResponse(){
        final String response = client.toBlocking().retrieve("/");
        assertEquals("Hello World from yml",response);
    }

    @Test
    void testHelloInGerman(){
        final String response = client.toBlocking().retrieve("/de");
        assertEquals("Hallo", response);
    }

    @Test
    void testHelloInEnglish(){
        final String response = client.toBlocking().retrieve("/en");
        assertEquals("Hello", response);
    }

    @Test
    void testHelloAsJson(){

        final String response = client.toBlocking().retrieve("/json");
       LOG.debug("Valor => {}", response);
    }

}
