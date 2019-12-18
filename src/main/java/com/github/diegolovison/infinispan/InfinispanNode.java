package com.github.diegolovison.infinispan;

import org.infinispan.Cache;

import com.github.diegolovison.jgroups.Node;

public class InfinispanNode extends Node {

   private InfinispanChaosProcess infinispanChaosProcess;

   public InfinispanNode(InfinispanChaosProcess infinispanChaosProcess) {
      super(infinispanChaosProcess);
      this.infinispanChaosProcess = infinispanChaosProcess;
   }

   public Cache getCache(String cacheName) {
      return this.infinispanChaosProcess.getCache(cacheName);
   }
}
