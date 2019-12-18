package com.github.diegolovison;

import static com.github.diegolovison.junit5.InfinispanClusterExtension.builder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.conflict.ConflictManager;
import org.infinispan.conflict.ConflictManagerFactory;
import org.infinispan.conflict.MergePolicy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.diegolovison.infinispan.InfinispanCluster;
import com.github.diegolovison.infinispan.InfinispanNode;
import com.github.diegolovison.jgroups.Node;
import com.github.diegolovison.jgroups.failure.Failure;
import com.github.diegolovison.junit5.InfinispanClusterExtension;

public class InfinispanPartitionHandlingTest {

   @RegisterExtension
   InfinispanClusterExtension clusterExtension = builder().build();

   @Test
   void testMergePolicyRemoveAll() {
      InfinispanCluster cluster = clusterExtension.createCluster();

      // Given: 3 nodes
      final String cacheName = "fooCache";
      final int numberOfNodes = 3;
      List<InfinispanNode> nodes = cluster.createNodes(() -> {
         GlobalConfigurationBuilder globalConfigurationBuilder = GlobalConfigurationBuilder.defaultClusteredBuilder();
         globalConfigurationBuilder.transport();
         return globalConfigurationBuilder;
      }, () -> {
         InfinispanCluster.CacheConfigurationBuilder cacheConfigurationBuilder = new InfinispanCluster.CacheConfigurationBuilder(cacheName);
         cacheConfigurationBuilder.clustering().cacheMode(CacheMode.REPL_SYNC)
            .partitionHandling().mergePolicy(MergePolicy.REMOVE_ALL);

         return cacheConfigurationBuilder;
      }, numberOfNodes);
      InfinispanNode node1 = nodes.get(0);
      InfinispanNode node2 = nodes.get(1);
      InfinispanNode node3 = nodes.get(2);

      // And: 3 caches
      Cache cache1 = node1.getCache(cacheName);
      Cache cache2 = node2.getCache(cacheName);
      Cache cache3 = node3.getCache(cacheName);

      // When: data is added to the cache
      cache1.put("foo", "bar");
      assertNotNull(cache1.get("foo"));
      assertNotNull(cache2.get("foo"));
      assertNotNull(cache3.get("foo"));

      // And: there is a network partition
      cluster.createFailure(Failure.NetworkPartition, new Node[]{node1, node2}, new Node[]{node3});

      // And: data was changed
      cache1.put("foo", "bar_v2");
      cache3.put("foo", "bar_v3");
      assertEquals("bar_v2", cache1.get("foo"));
      assertEquals("bar_v2", cache2.get("foo"));
      assertEquals("bar_v3", cache3.get("foo"));

      // And: the network partition was solved
      cluster.solveFailure(Failure.NetworkPartition, node1, node2, node3);

      // Then: there will be only one cluster
      assertEquals(numberOfNodes, nodes.size());
      assertEquals(numberOfNodes, cluster.size());

      // And: conflict manager was done
      waitTheConflictManager(cache1, cache2, cache3);

      // And: the merge policy will remove all data
      assertNull(cache1.get("foo"));
      assertNull(cache2.get("foo"));
      assertNull(cache3.get("foo"));
   }

   private void waitTheConflictManager(Cache... caches) {
      for (Cache cache : caches) {
         ConflictManager cm = ConflictManagerFactory.get(cache.getAdvancedCache());
         int countConflictResolution = 0;
         while (cm.isConflictResolutionInProgress()) {
            try {
               Thread.sleep(1000);
            } catch (InterruptedException e) {
               throw new IllegalStateException(e);
            }
            if (countConflictResolution++ >= 10) {
               throw new IllegalStateException("Do we need 10 seconds to solve conflict resolution?");
            }
         }
         int countStateTransfer = 0;
         while (cm.isStateTransferInProgress()) {
            try {
               Thread.sleep(1000);
            } catch (InterruptedException e) {
               throw new IllegalStateException(e);
            }
            if (countStateTransfer++ >= 10) {
               throw new IllegalStateException("Do we need 10 seconds to do the state transfer?");
            }
         }
      }
   }
}
