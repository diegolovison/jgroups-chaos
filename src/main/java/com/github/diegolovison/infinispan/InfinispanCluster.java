package com.github.diegolovison.infinispan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.remoting.transport.jgroups.JGroupsTransport;
import org.jgroups.JChannel;

import com.github.diegolovison.jgroups.Cluster;
import com.github.diegolovison.jgroups.NodeConfig;

public class InfinispanCluster extends Cluster<InfinispanNode> {

   public List<InfinispanNode> createNodes(Supplier<GlobalConfigurationBuilder> globalConfigurationBuilderSupplier,
                                 Supplier<CacheConfigurationBuilder> configurationBuilderSupplier,
                                 int numberOfNodes) {
      // create managers
      List<EmbeddedCacheManager> embeddedCacheManagers = new ArrayList<>(numberOfNodes);
      for (int i = 0; i < numberOfNodes; i++) {
         EmbeddedCacheManager cacheManager = new DefaultCacheManager(globalConfigurationBuilderSupplier.get().build());
         embeddedCacheManagers.add(cacheManager);
      }

      // create nodes
      for (int i = 0; i < numberOfNodes; i++) {
         EmbeddedCacheManager embeddedCacheManager = embeddedCacheManagers.get(i);
         Cache cache = createCache(embeddedCacheManager, configurationBuilderSupplier.get());
         this.nodes.add(new InfinispanNode(i, new NodeConfig(getJChannel(embeddedCacheManager), embeddedCacheManager.getClusterName()), cache));
      }

      return Collections.unmodifiableList(this.nodes);
   }

   public static class CacheConfigurationBuilder extends ConfigurationBuilder {
      private String cacheName;

      public CacheConfigurationBuilder(String cacheName) {
         this.cacheName = cacheName;
      }
   }

   private Cache createCache(EmbeddedCacheManager cacheManager, CacheConfigurationBuilder cacheConfigurationBuilder) {
      cacheManager.defineConfiguration(cacheConfigurationBuilder.cacheName, cacheConfigurationBuilder.build());
      return cacheManager.getCache(cacheConfigurationBuilder.cacheName);
   }

   private JChannel getJChannel(EmbeddedCacheManager embeddedCacheManager) {
      return ((JGroupsTransport)embeddedCacheManager.getTransport()).getChannel();
   }
}
