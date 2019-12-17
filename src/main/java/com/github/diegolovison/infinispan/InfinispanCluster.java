package com.github.diegolovison.infinispan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.remoting.transport.jgroups.JGroupsTransport;
import org.jgroups.JChannel;

import com.github.diegolovison.jgroups.Cluster;
import com.github.diegolovison.jgroups.Node;
import com.github.diegolovison.jgroups.NodeConfig;

public class InfinispanCluster extends Cluster {

   @Override
   public List<Node> createNodes(int numberOfNodes) {
      throw new UnsupportedOperationException();
   }

   @Override
   public List<Node> createNodes(int numberOfNodes, boolean connect) {
      throw new UnsupportedOperationException();
   }

   public List<Node> createNodes(Supplier<GlobalConfigurationBuilder> globalConfigurationBuilderSupplier, int numberOfNodes) {
      List<EmbeddedCacheManager> embeddedCacheManagers = new ArrayList<>(numberOfNodes);
      for (int i = 0; i < numberOfNodes; i++) {
         EmbeddedCacheManager cacheManager = new DefaultCacheManager(globalConfigurationBuilderSupplier.get().build());
         embeddedCacheManagers.add(cacheManager);
      }
      for (int i = 0; i < numberOfNodes; i++) {
         EmbeddedCacheManager embeddedCacheManager = embeddedCacheManagers.get(i);
         this.nodes.add(new Node(i, new NodeConfig(getJChannel(embeddedCacheManager), embeddedCacheManager.getClusterName())));
      }
      return Collections.unmodifiableList(this.nodes);
   }

   private JChannel getJChannel(EmbeddedCacheManager embeddedCacheManager) {
      return ((JGroupsTransport)embeddedCacheManager.getTransport()).getChannel();
   }
}
