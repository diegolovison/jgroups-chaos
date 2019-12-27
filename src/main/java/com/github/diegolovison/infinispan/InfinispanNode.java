package com.github.diegolovison.infinispan;

import com.github.diegolovison.infinispan.cache.ChaosCache;
import com.github.diegolovison.jgroups.Node;

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
