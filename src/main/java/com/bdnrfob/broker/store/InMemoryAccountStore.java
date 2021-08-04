package com.bdnrfob.broker.store;

import com.bdnrfob.broker.model.WatchList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.UUID;


@Singleton
public class InMemoryAccountStore {

    private final HashMap<UUID, WatchList> watchListPerAccount = new HashMap<>();
    private static final Logger LOG = LoggerFactory.getLogger(InMemoryAccountStore.class);

    public WatchList getWatchList(UUID accountId) {
        WatchList watchList = watchListPerAccount.getOrDefault(accountId,new WatchList());
        LOG.debug("watchList in store => {}", watchList.getSymbols().size());
        return watchList;
    }

    public WatchList updateWatchList(UUID accountId, WatchList watchList) {
        watchListPerAccount.put(accountId,watchList);
        return getWatchList(accountId);
    }

    public void deleteWatchList(UUID accountId) {
        watchListPerAccount.remove(accountId);
    }
}
