package com.bdnrfob.broker.account;

import com.bdnrfob.broker.model.Symbol;
import com.bdnrfob.broker.model.WatchList;
import com.bdnrfob.broker.store.InMemoryAccountStore;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.micronaut.http.HttpRequest.DELETE;
import static io.micronaut.http.HttpRequest.PUT;
import static org.junit.jupiter.api.Assertions.*;


@MicronautTest
public class WatchListControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(WatchListControllerTest.class);
    private static final UUID ACCOUNT_TEST_ID = WatchListController.ACCOUNT_UUID;

    @Inject
    EmbeddedApplication<?> application;

    @Inject
    @Client("/account/watchlist") RxHttpClient client;

    @Inject
    InMemoryAccountStore store;

    @Test
    void returnEmptyListForAccount(){

        final WatchList emptyWatchList = new WatchList();
        store.updateWatchList(ACCOUNT_TEST_ID,emptyWatchList);
        final WatchList result = client.toBlocking().retrieve("/", WatchList.class);
        assertTrue(result.getSymbols().isEmpty());
        assertTrue(store.getWatchList(ACCOUNT_TEST_ID).getSymbols().isEmpty());
    }

    @Test
    void returnWatchListForAccount(){

        final WatchList watchList = returnWatchList();
        store.updateWatchList(ACCOUNT_TEST_ID,watchList);

        final WatchList result = client.toBlocking().retrieve("/",WatchList.class);
        assertEquals(3, result.getSymbols().size());
        assertEquals(3, store.getWatchList(ACCOUNT_TEST_ID).getSymbols().size());
    }

    @Test
    void canUpdateWatchListForAccount(){
        final WatchList watchList = returnWatchList();
        final HttpResponse<Object> added = client.toBlocking().exchange(PUT("/", watchList));
        assertEquals(HttpStatus.OK,added.getStatus());
        assertEquals(watchList,store.getWatchList(ACCOUNT_TEST_ID));
    }

    @Test
    void canDeleteWatchListForAccount(){
        final WatchList watchList = returnWatchList();
        store.updateWatchList(ACCOUNT_TEST_ID,watchList);
        assertFalse(store.getWatchList(ACCOUNT_TEST_ID).getSymbols().isEmpty());
        final HttpResponse<Object> deleted = client.toBlocking().exchange(DELETE("/" + ACCOUNT_TEST_ID));
        assertTrue(store.getWatchList(ACCOUNT_TEST_ID).getSymbols().isEmpty());
        assertEquals(HttpStatus.OK, deleted.getStatus());

    }

    private WatchList returnWatchList(){
        List<Symbol> symbols =  Stream.of("AAPL", "AMZN", "NFLX")
                .map(Symbol::new)
                .collect(Collectors.toList());
        return new WatchList(symbols);
    }

}
