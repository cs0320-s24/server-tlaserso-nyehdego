package edu.brown.cs.student.main.builtins.broadband;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/** APIDataCache is the class that holdes the cache object for our broadband methodology */
public class APIDataCache {
  private LoadingCache<String, List<String>> cache;

  /**
   * Constructor for the API Data Cache.
   *
   * @param minutes - The number of minutes after which the data in the cache will expire after
   *     being written in
   */
  public APIDataCache(int minutes, int size) {
    this.cache =
        CacheBuilder.newBuilder()
            // How many entries maximum in the cache?
            .maximumSize(size)
            // How long should entries remain in the cache?
            .expireAfterWrite(minutes, TimeUnit.MINUTES)
            // Keep statistical info around for profiling purposes
            .recordStats()
            .build(
                new CacheLoader<>() {
                  /**
                   * @param statecounty - the state and county name, concatenated together seperated
                   *     by a '.' Ex: California.OrangeCounty.
                   * @return - A list of Strings. This is gotten by making a get request to the
                   *     respective API to retrieve broadband data.
                   */
                  @Override
                  public List<String> load(String statecounty) throws ExecutionException {
                    String[] inputs = statecounty.split("\\.");
                    try {
                      return APICodeSource.getBandWidth(inputs[0], inputs[1]);
                    } catch (DataSourceException e) {
                      throw new ExecutionException(e);
                    }
                  }
                });
  }

  /**
   * Gets the value associated with the key.
   *
   * @param names - a String representing the state and county name, concatenated together seperated
   *     by a '.' Ex: California.Los Angeles
   * @return - the value (data in the form of a List of List of Strings) associated with the key
   * @throws ExecutionException
   */
  public List<String> get(String names) throws ExecutionException {
    return this.cache.get(names);
  }
}
