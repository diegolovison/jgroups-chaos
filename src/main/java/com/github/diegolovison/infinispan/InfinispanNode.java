package com.github.diegolovison.infinispan;

import org.infinispan.Cache;

import com.github.diegolovison.jgroups.Node;
import com.github.diegolovison.jgroups.NodeConfig;

public class InfinispanNode extends Node {

   private final Cache cache;

   public InfinispanNode(int nodeIndex, NodeConfig nodeConfig, Cache cache) {
      super(nodeIndex, nodeConfig);
      this.cache = cache;
   }

   public Cache getCache() {
      return cache;
   }
}
