package com.github.diegolovison.infinispan;

import com.github.diegolovison.infinispan.cache.ChaosCache;
import com.github.diegolovison.os.ChaosProcess;

public abstract class InfinispanChaosProcess extends ChaosProcess<InfinispanChaosConfig> {

   public abstract ChaosCache getCache(String cacheName);

   public abstract void waitForClusterToForm(int numberOfNodes);
}
