package com.github.diegolovison;

import static com.github.diegolovison.infinispan.junit5.InfinispanClusterExtension.builder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.diegolovison.base.Node;
import com.github.diegolovison.infinispan.InfinispanCluster;
import com.github.diegolovison.infinispan.InfinispanNode;
import com.github.diegolovison.infinispan.cache.ChaosCache;
import com.github.diegolovison.base.failure.Failure;
import com.github.diegolovison.infinispan.junit5.InfinispanClusterExtension;

public class InfinispanPartitionHandlingTest {

   @RegisterExtension
   InfinispanClusterExtension clusterExtension = builder().build();

   @Test
   void testMergePolicyRemoveAll() {
      InfinispanCluster cluster = clusterExtension.infinispanCluster();

      // Given: 3 nodes
      final String cacheName = "partitionHandlingCache";
      final int numberOfNodes = 3;
      List<InfinispanNode> nodes = cluster.createNodes("ispn-config/infinispan-base-config.xml", numberOfNodes);
      InfinispanNode node1 = nodes.get(0);
      InfinispanNode node2 = nodes.get(1);
      InfinispanNode node3 = nodes.get(2);

      // And: 3 caches
      ChaosCache cache1 = node1.getCache(cacheName);
      ChaosCache cache2 = node2.getCache(cacheName);
      ChaosCache cache3 = node3.getCache(cacheName);

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
      boolean runOnce = waitTheConflictManager(10, cache1, cache2, cache3);

      // And: the merge policy will remove all data
      assertNull(cache1.get("foo"));
      assertNull(cache2.get("foo"));
      assertNull(cache3.get("foo"));
      assertTrue(runOnce);
   }

   private boolean waitTheConflictManager(int times, ChaosCache... caches) {
      boolean runOnce = false;
      for (ChaosCache cache : caches) {
         int countConflictResolution = 0;
         while (cache.isConflictResolutionInProgress() || cache.isStateTransferInProgress()) {
            if (!runOnce) {
               runOnce = true;
            }
            try {
               Thread.sleep(1000);
            } catch (InterruptedException e) {
               throw new IllegalStateException(e);
            }
            if (countConflictResolution++ >= times) {
               throw new IllegalStateException(String.format("Do we need %d seconds to solve conflict resolution?", times));
            }
         }
      }
      return runOnce;
   }
}
