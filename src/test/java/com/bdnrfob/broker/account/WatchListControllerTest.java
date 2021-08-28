package com.bdnrfob.broker.account;

import com.bdnrfob.broker.model.Symbol;
import com.bdnrfob.broker.model.WatchList;
import com.bdnrfob.broker.store.InMemoryAccountStore;
import io.micronaut.http.*;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
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
public class WatchListControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(WatchListControllerTest.class);
    private static final UUID ACCOUNT_TEST_ID = WatchListController.ACCOUNT_UUID;
    public static final String ACCOUNT_WATCHLIST = "/account/watchlist";

    @Inject
    EmbeddedApplication<?> application;

    @Inject
    @Client("/") RxHttpClient client;

    @Inject
    InMemoryAccountStore store;

    @Test
    void unauthorizedAccessIsForbidden(){
        try {
            client.toBlocking().retrieve(ACCOUNT_WATCHLIST);
            fail("Should fail is no exception is thrown");
        } catch (HttpClientResponseException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
        }
    }

    @Test
    void returnEmptyListForAccount(){

        var request = GET(ACCOUNT_WATCHLIST)
                .accept(MediaType.APPLICATION_JSON)
                .bearerAuth(givenMyUserIsLoggedIn().getAccessToken());

        final WatchList emptyWatchList = new WatchList();
        store.updateWatchList(ACCOUNT_TEST_ID,emptyWatchList);
        final WatchList result = client.toBlocking().retrieve(request, WatchList.class);
        assertTrue(result.getSymbols().isEmpty());
        assertTrue(store.getWatchList(ACCOUNT_TEST_ID).getSymbols().isEmpty());
    }

    @Test
    void returnWatchListForAccount(){

        var request = GET(ACCOUNT_WATCHLIST)
                .accept(MediaType.APPLICATION_JSON)
                .bearerAuth(givenMyUserIsLoggedIn().getAccessToken());

        final WatchList watchList = returnWatchList();
        store.updateWatchList(ACCOUNT_TEST_ID,watchList);

        final WatchList result = client.toBlocking().retrieve(request,WatchList.class);
        assertEquals(3, result.getSymbols().size());
        assertEquals(3, store.getWatchList(ACCOUNT_TEST_ID).getSymbols().size());
    }

    @Test
    void canUpdateWatchListForAccount(){



        final WatchList watchList = returnWatchList();

        var request = HttpRequest.PUT(ACCOUNT_WATCHLIST,watchList)
                .accept(MediaType.APPLICATION_JSON)
                .bearerAuth(givenMyUserIsLoggedIn().getAccessToken());

        final HttpResponse<Object> added = client.toBlocking().exchange(request);
        assertEquals(HttpStatus.OK,added.getStatus());
        assertEquals(watchList,store.getWatchList(ACCOUNT_TEST_ID));
    }

    @Test
    void canDeleteWatchListForAccount(){
        final WatchList watchList = returnWatchList();
        store.updateWatchList(ACCOUNT_TEST_ID,watchList);
        assertFalse(store.getWatchList(ACCOUNT_TEST_ID).getSymbols().isEmpty());

        var request = HttpRequest.DELETE("/account/watchlist/" + ACCOUNT_TEST_ID)
                .accept(MediaType.APPLICATION_JSON)
                .bearerAuth(givenMyUserIsLoggedIn().getAccessToken());

        final HttpResponse<Object> deleted = client.toBlocking().exchange(request);
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
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("my-user","secret");
        MutableHttpRequest<UsernamePasswordCredentials> login = HttpRequest.POST("/login", credentials);
        var response = client.toBlocking().exchange(login, BearerAccessRefreshToken.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        final BearerAccessRefreshToken token = response.body();
        assertEquals("my-user", token.getUsername());
        assertNotNull(token);

        LOG.debug("Login Bearer Token: {} expires in {}", token.getAccessToken(), token.getExpiresIn());

        return token;

    }

}
