package com.bdnrfob.broker.account;

import com.bdnrfob.Application;
import com.bdnrfob.broker.model.WatchList;
import com.bdnrfob.broker.store.InMemoryAccountStore;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Controller("/account/watchlist")
public class WatchListController {

    private final InMemoryAccountStore store;
    static final UUID ACCOUNT_UUID = UUID.randomUUID();
    private static final Logger LOG = LoggerFactory.getLogger(WatchListController.class);

    public WatchListController(InMemoryAccountStore store) {
        this.store = store;
    }

    @Get(produces = MediaType.APPLICATION_JSON)
    public WatchList get(){
        LOG.debug("getWatchListController(GET) - {}", Thread.currentThread().getName());
        return store.getWatchList(ACCOUNT_UUID);
    }

    @Put (consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public WatchList update( @Body WatchList watchList){
        LOG.debug("getWatchListController(PUT) - {}", Thread.currentThread().getName());
        return store.updateWatchList(ACCOUNT_UUID,watchList);
    }

    @Delete(value = "/{accountId}",
            consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON)
    public void delete(@PathVariable UUID accountId){
        LOG.debug("getWatchListController(DELETE) - {}", Thread.currentThread().getName());
        store.deleteWatchList(accountId);
    }

}
