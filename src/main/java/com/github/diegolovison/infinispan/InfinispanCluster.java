package com.github.diegolovison.infinispan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;

import com.github.diegolovison.jgroups.Cluster;
import com.github.diegolovison.jgroups.JGroupsChaosProcess;
import com.github.diegolovison.os.ChaosProcessFactory;
import com.github.diegolovison.os.ChaosProcessFramework;

public class InfinispanCluster extends Cluster<InfinispanNode> {

   private final List<InfinispanChaosProcess> chaosProcesses;
   private String clusterName;

   public InfinispanCluster() {
      this.chaosProcesses = new ArrayList<>();
   }

   public List<InfinispanNode> createNodes(Supplier<GlobalConfigurationBuilder> globalConfigurationBuilderSupplier,
                                 Supplier<CacheConfigurationBuilder> cacheConfigurationBuilderSupplier,
                                 int numberOfNodes) {
      // create managers
      for (int i = 0; i < numberOfNodes; i++) {
         InfinispanChaosProcess chaosProcess = (InfinispanChaosProcess)
               ChaosProcessFactory.createInstance(ChaosProcessFramework.INFINISPAN).run(globalConfigurationBuilderSupplier);
         if (this.clusterName == null) {
            this.clusterName = chaosProcess.getClusterName();
         } else if (!this.clusterName.equals(chaosProcess.getClusterName())) {
            throw new IllegalStateException("The cluster must have the same name");
         }
         this.chaosProcesses.add(chaosProcess);
      }

      // create nodes
      for (int i = 0; i < numberOfNodes; i++) {
         InfinispanChaosProcess chaosProcess = chaosProcesses.get(i);
         CacheConfigurationBuilder cacheConfigurationBuilder = cacheConfigurationBuilderSupplier.get();
         chaosProcess.createCache(cacheConfigurationBuilder.cacheName, cacheConfigurationBuilder);
         this.nodes.add(new InfinispanNode(chaosProcess));
      }

      return Collections.unmodifiableList(this.nodes);
   }

   @Override
   public int size() {
      int size = 0;
      for (InfinispanChaosProcess jGroupsChaosProcess : this.chaosProcesses) {
         if (jGroupsChaosProcess.isRunning() && jGroupsChaosProcess.getClusterName().equals(this.clusterName)) {
            size = jGroupsChaosProcess.getNumberOfMembers();
            break;
         }
      }
      return size;
   }

   public static class CacheConfigurationBuilder extends ConfigurationBuilder {
      private String cacheName;

      public CacheConfigurationBuilder(String cacheName) {
         this.cacheName = cacheName;
      }
   }
}
