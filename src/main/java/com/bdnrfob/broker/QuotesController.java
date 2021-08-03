package com.bdnrfob.broker;

import com.bdnrfob.broker.model.Quote;
import com.bdnrfob.broker.store.InMemoryStore;
import com.bdnrfob.broker.store.error.CustomError;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;

import java.util.Optional;

@Controller("/quotes")
public class QuotesController {

    private final InMemoryStore store;

    public QuotesController(InMemoryStore store) {
        this.store = store;
    }

    @Get("/{symbol}")
    public HttpResponse getQuote( @PathVariable String symbol){
        Optional<Quote> quote = store.fetchQuote(symbol);

       if(quote.isEmpty()){
           final HttpStatus statusNotFound = HttpStatus.NOT_FOUND;
           final CustomError notFound = CustomError.builder()
                   .status(statusNotFound.getCode())
                   .error(statusNotFound.name())
                   .message("quote for symbol not available")
                   .path("/quote/"+symbol)
                   .build();
           return HttpResponse.notFound(notFound);
       }

            return HttpResponse.ok(quote.get());
    }
}
