package com.github.diegolovison.infinispan;

import com.github.diegolovison.base.Node;
import com.github.diegolovison.infinispan.cache.ChaosCache;

public class InfinispanNode extends Node {

   private InfinispanChaosProcess infinispanChaosProcess;

   public InfinispanNode(InfinispanChaosProcess infinispanChaosProcess) {
      super(infinispanChaosProcess);
      this.infinispanChaosProcess = infinispanChaosProcess;
   }

   public ChaosCache getCache(String cacheName) {
      return this.infinispanChaosProcess.getCache(cacheName);
   }
}
