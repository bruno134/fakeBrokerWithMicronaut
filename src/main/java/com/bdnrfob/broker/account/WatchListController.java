package com.bdnrfob.broker.account;

import com.bdnrfob.broker.model.WatchList;
import com.bdnrfob.broker.store.InMemoryAccountStore;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;

import java.util.UUID;

@Controller("/account/watchlist")
public class WatchListController {

    private final InMemoryAccountStore store;
    static final UUID ACCOUNT_UUID = UUID.randomUUID();

    public WatchListController(InMemoryAccountStore store) {
        this.store = store;
    }

    @Get(produces = MediaType.APPLICATION_JSON)
    public WatchList get(){

        return store.getWatchList(ACCOUNT_UUID);
    }
    @Put (consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public WatchList update( @Body WatchList watchList){
        return store.updateWatchList(ACCOUNT_UUID,watchList);
    }

    @Delete(value = "/{accountId}",
            consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON)
    public void delete(@PathVariable UUID accountId){
        store.deleteWatchList(accountId);
    }

}
