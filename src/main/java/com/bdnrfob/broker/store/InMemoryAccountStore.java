package com.bdnrfob.broker.store;

import com.bdnrfob.broker.model.WatchList;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.UUID;

@Singleton
public class InMemoryAccountStore {

    private final HashMap<UUID, WatchList> watchListPerAccount = new HashMap<>();

    public WatchList getWatchList(UUID accountId) {
        return watchListPerAccount.getOrDefault(accountId,new WatchList());
    }

    public WatchList updateWatchList(UUID accountId, WatchList watchList) {
        watchListPerAccount.put(accountId,watchList);
        return getWatchList(accountId);
    }

    public void deleteWatchList(UUID accountId) {
        watchListPerAccount.remove(accountId);
    }
}
