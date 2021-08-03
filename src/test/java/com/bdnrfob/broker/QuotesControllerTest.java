package com.bdnrfob.broker;

import com.bdnrfob.broker.model.Quote;
import com.bdnrfob.broker.model.Symbol;
import com.bdnrfob.broker.store.InMemoryStore;
import com.bdnrfob.broker.store.error.CustomError;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
public class QuotesControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(QuotesControllerTest.class);

    @Inject
    EmbeddedApplication<?> application;

    @Inject
    @Client("/") RxHttpClient client;

    @Inject
    InMemoryStore store;

    @Test
    void returnQuotesPerSymbol(){

        final Quote apple = initRandomQuote("APPL");
        store.update(apple);

        final Quote appleResult = getQuoteWithAPI("APPL");
        LOG.debug("Result {}", appleResult);
        assertThat(apple).isEqualToComparingFieldByField(appleResult);
    }

    @Test
    void returnsNotFoundOnSupportedSymbol(){
        try{
            //getQuoteWithAPI("UNSUPPORTED");
            final Quote thisQuote = client.toBlocking().retrieve("/quotes/UNSUPPORTED", Quote.class);
        } catch (HttpClientResponseException e){
            assertEquals(HttpStatus.NOT_FOUND, e.getResponse().getStatus());
            final Optional<CustomError> customError = e.getResponse().getBody(CustomError.class);
            assertTrue(customError.isPresent());
            assertEquals(404,customError.get().getStatus());
            assertEquals("NOT_FOUND",customError.get().getError());
            assertEquals("quote for symbol not available",customError.get().getMessage());
            assertEquals("/quote/UNSUPPORTED",customError.get().getPath());
        }
    }

    private BigDecimal randomValue() {
        return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1,100));
    }
    private Quote initRandomQuote(String symbolValue){
        return Quote.builder()
                .symbol(new Symbol(symbolValue))
                .bid(randomValue())
                .ask(randomValue())
                .lastPrice(randomValue())
                .volume(randomValue())
                .build();
    }
    private Quote getQuoteWithAPI(final String symbol){
        return client.toBlocking().retrieve("/quotes/"+symbol, Quote.class);
    }

}
