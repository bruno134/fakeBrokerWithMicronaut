package com.bdnrfob.broker.store;

import com.bdnrfob.broker.model.Quote;
import com.bdnrfob.broker.model.Symbol;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class InMemoryStore {


    private final List<Symbol> symbols;
    private final Map<String, Quote> cachedQuotes = new HashMap<>();
    private final ThreadLocalRandom current = ThreadLocalRandom.current();

    public InMemoryStore() {
        symbols = Stream.of("MGLU3", "AAPL", "AMZN", "PETR4", "FB", "GOOG", "MSFT", "NFLX", "TSLA")
        .map(Symbol::new)
        .collect(Collectors.toList());
        symbols.forEach(symbol -> cachedQuotes.put(symbol.getValue(),randomQuote(symbol)));

    }

    private Quote randomQuote(final Symbol symbol) {
        return Quote.builder().symbol(symbol)
                .bid(randomValue())
                .ask(randomValue())
                .lastPrice(randomValue())
                .volume(randomValue())
                .build();
    }

    public List<Symbol> getAllSymbols() {
        return symbols;
    }

    public Optional<Quote> fetchQuote(final String symbol) {
        return Optional.ofNullable(cachedQuotes.get(symbol));
    }


    public void update(final Quote quote){
        cachedQuotes.put(quote.getSymbol().getValue(),quote);
    }

    private BigDecimal randomValue() {
        return BigDecimal.valueOf(current.nextDouble(1,100));
    }
}
