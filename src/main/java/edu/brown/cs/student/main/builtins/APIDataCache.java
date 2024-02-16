package edu.brown.cs.student.main.builtins;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class APIDataCache {
    private LoadingCache<String, List<List<String>>> cache;

    /**
     * Constructor for the API Data Cache.
     *
     * @param minutes - The number of minutes after which the data in the cache will expire after
     *     being written in
     */
    public APIDataCache(int minutes) {
        this.cache =
                CacheBuilder.newBuilder()
                        // How many entries maximum in the cache?
                        .maximumSize(10)
                        // How long should entries remain in the cache?
                        .expireAfterWrite(minutes, TimeUnit.MINUTES)
                        // Keep statistical info around for profiling purposes
                        .recordStats()
                        .build(
                                new CacheLoader<>() {
                                    /**
                                     * @param names - the state and county name, concatenated together seperated by a
                                     *     '.' Ex: California.Los Angeles.
                                     * @return - A list of list of Strings. This is gotten by making a get request to
                                     *     the respective API to retrieve broadband data.
                                     */
                                    @Override
                                    public List<List<String>> load(String names) throws DatasourceException {
                                        return AcsDataSource.pingApi(names);
                                    }
                                }
                                );
    }

    /**
     * Gets the value associated with the key.
     *
     * @param names - a String representing the state and county name, concatenated together seperated
     *     by a '.' Ex: California.Los Angeles
     * @return - the value (data in the form of a List of List of Strings) associated with the key
     * @throws ExecutionException
     */
    public List<List<String>> get(String names) throws ExecutionException {
        return this.cache.get(names);
    }
}

}
