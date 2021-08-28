package com.bdnrfob.broker.account;

import com.bdnrfob.broker.model.Symbol;
import com.bdnrfob.broker.model.WatchList;
import com.bdnrfob.broker.store.InMemoryAccountStore;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.micronaut.http.HttpRequest.*;
import static org.junit.jupiter.api.Assertions.*;


@MicronautTest
public class WatchListReactiveControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(WatchListReactiveControllerTest.class);
    private static final UUID ACCOUNT_TEST_ID = WatchListReactiveController.ACCOUNT_UUID;

    @Inject
    EmbeddedApplication<?> application;

    @Inject
    @Client("/") JWTWatchListClient client;

    @Inject
    InMemoryAccountStore store;

    @Test
    void returnEmptyListForAccount(){


        final WatchList emptyWatchList = new WatchList();
        store.updateWatchList(ACCOUNT_TEST_ID,emptyWatchList);

        final WatchList result = client.retrieveWatchList(getAuthorization()).singleOrError().blockingGet();
        assertTrue(result.getSymbols().isEmpty());
        assertTrue(store.getWatchList(ACCOUNT_TEST_ID).getSymbols().isEmpty());
    }



    @Test
    void returnWatchListForAccount(){

        final WatchList watchList = returnWatchList();
        store.updateWatchList(ACCOUNT_TEST_ID,watchList);
       //validar
        final WatchList result = client.retrieveWatchListAsSingle(getAuthorization()).blockingGet();
        LOG.debug("store => {}", store.getWatchList(ACCOUNT_TEST_ID).getSymbols().size());
        LOG.debug("result ==> {}", result.getSymbols());
        assertEquals(3, store.getWatchList(ACCOUNT_TEST_ID).getSymbols().size());
        assertEquals(3, result.getSymbols().size());
    }

    @Test
    void canUpdateWatchListForAccount(){
        final WatchList watchList = returnWatchList();
        final HttpResponse<WatchList> added = client.updateWatchList(getAuthorization(),watchList);
        assertEquals(HttpStatus.OK,added.getStatus());
        assertEquals(watchList,store.getWatchList(ACCOUNT_TEST_ID));
    }

    @Test
    void canDeleteWatchListForAccount(){
        final WatchList watchList = returnWatchList();
        store.updateWatchList(ACCOUNT_TEST_ID,watchList);
        assertFalse(store.getWatchList(ACCOUNT_TEST_ID).getSymbols().isEmpty());
        final HttpResponse<WatchList> deleted = client.deleteWatchList(getAuthorization(), ACCOUNT_TEST_ID);
        assertTrue(store.getWatchList(ACCOUNT_TEST_ID).getSymbols().isEmpty());
        assertEquals(HttpStatus.OK, deleted.getStatus());

    }

    private WatchList returnWatchList(){
        List<Symbol> symbols =  Stream.of("AAPL", "AMZN", "NFLX")
                .map(Symbol::new)
                .collect(Collectors.toList());
        return new WatchList(symbols);
    }

    private BearerAccessRefreshToken givenMyUserIsLoggedIn(){
        return client.login(new UsernamePasswordCredentials("my-user", "secret"));
    }

    private String getAuthorization() {
        return "Bearer " + givenMyUserIsLoggedIn().getAccessToken();
    }

}
