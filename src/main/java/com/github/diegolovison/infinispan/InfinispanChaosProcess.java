package com.github.diegolovison.infinispan;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.ConfigurationBuilder;

import com.github.diegolovison.os.ChaosProcess;

public abstract class InfinispanChaosProcess<T> extends ChaosProcess<T> {

   public abstract void createCache(String cacheName, ConfigurationBuilder cacheConfigurationBuilder);

   public abstract Cache getCache(String cacheName);
}
