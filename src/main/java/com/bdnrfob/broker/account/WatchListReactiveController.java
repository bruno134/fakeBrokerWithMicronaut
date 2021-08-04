package com.bdnrfob.broker.account;

import com.bdnrfob.broker.model.WatchList;
import com.bdnrfob.broker.store.InMemoryAccountStore;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Controller("/account/watchlist-reactive")
public class WatchListReactiveController {

    private final InMemoryAccountStore store;
    static final UUID ACCOUNT_UUID = UUID.randomUUID();
    private static final Logger LOG = LoggerFactory.getLogger(WatchListReactiveController.class);
    private final Scheduler scheduler;

    public WatchListReactiveController(InMemoryAccountStore store, @Named(TaskExecutors.IO) ExecutorService executorService) {
        this.store = store;
        this.scheduler = Schedulers.from(executorService);
    }

    @Get(produces = MediaType.APPLICATION_JSON)
    @ExecuteOn(TaskExecutors.IO)
    public WatchList get(){
        LOG.debug("getWatchListReactiveController(GET) - {}", Thread.currentThread().getName());
        WatchList watchList = store.getWatchList(ACCOUNT_UUID);
        LOG.debug("watchlist in controller => {}", watchList.getSymbols().size());
        return watchList;
    }

    @Get(   value = "/single",
            produces = MediaType.APPLICATION_JSON)
    public Flowable<WatchList> getAsSingle(){
        return Single.fromCallable( () -> {
            LOG.debug("getAsSingle - {}", Thread.currentThread().getName());
                return store.getWatchList(ACCOUNT_UUID);
        }).toFlowable().subscribeOn(scheduler);
    }

    @Put (consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ExecuteOn(TaskExecutors.IO)
    public WatchList update( @Body WatchList watchList){
        LOG.debug("getWatchListReactiveController(PUT) - {}", Thread.currentThread().getName());
        return store.updateWatchList(ACCOUNT_UUID,watchList);
    }

    @Delete(value = "/{accountId}",
            consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON)
    @ExecuteOn(TaskExecutors.IO)
    public void delete(@PathVariable UUID accountId){
        LOG.debug("getWatchListReactiveController(DELETE) - {}", Thread.currentThread().getName());
        store.deleteWatchList(accountId);
    }

}
