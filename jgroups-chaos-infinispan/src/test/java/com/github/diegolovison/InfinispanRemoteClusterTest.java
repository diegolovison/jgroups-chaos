package com.github.diegolovison;

import static com.github.diegolovison.infinispan.junit5.InfinispanClusterExtension.builder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.diegolovison.base.Node;
import com.github.diegolovison.base.Scheduler;
import com.github.diegolovison.infinispan.InfinispanCluster;
import com.github.diegolovison.infinispan.InfinispanNode;
import com.github.diegolovison.infinispan.cache.ChaosCache;
import com.github.diegolovison.infinispan.junit5.InfinispanClusterExtension;
import com.github.diegolovison.os.ChaosProcessType;
import com.github.diegolovison.os.Eventually;

public class InfinispanRemoteClusterTest {

   @RegisterExtension
   InfinispanClusterExtension clusterExtension = builder().processType(ChaosProcessType.LOCAL_SERVER).build();

   @Test
   public void testClusterFormation() {

      final int numberOfNodes = 3;
      InfinispanCluster cluster = clusterExtension.infinispanCluster();

      List<InfinispanNode> nodes = cluster.createNodes("ispn-config/infinispan-server-config.xml", numberOfNodes);

      // Then: the cluster will be ready
      assertEquals(numberOfNodes, nodes.size());
      assertEquals(numberOfNodes, cluster.size());
   }

   @Test
   void testClusterNodeDisconnect() {
      InfinispanCluster cluster = clusterExtension.infinispanCluster();

      // Given: two nodes
      final int numberOfNodes = 2;
      List<InfinispanNode> nodes = cluster.createNodes("ispn-config/infinispan-server-config.xml", numberOfNodes);
      Node node1 = nodes.get(0);
      Node node2 = nodes.get(1);

      assertEquals(2, cluster.size());

      // When: one node leave the cluster
      cluster.disconnect(node1);

      Eventually.runUntil(() -> {
         return cluster.size() == 1;
      }, 30);

      // Then: cluster have 1 node
      assertEquals(1, cluster.size());
   }

   @Test
   void testMaxIdleExpiration() {

      int numberOfNodes = 3;
      InfinispanCluster cluster = clusterExtension.infinispanCluster();

      List<InfinispanNode> nodes = cluster.createNodes("ispn-config/infinispan-server-config.xml", numberOfNodes);
      InfinispanNode node1 = nodes.get(0);
      InfinispanNode node2 = nodes.get(0);
      InfinispanNode node3 = nodes.get(0);

      ChaosCache cache = node1.getCache("testMaxIdleExpiration");

      Scheduler.schedule(() -> {
         node3.disconnect();
      }, 30_000);

      for (int i = 1; i <= 1000; i++) {
         cache.put(String.valueOf(i), "Test" + i);
      }

      long begin = System.currentTimeMillis();
      long now = System.currentTimeMillis();
      while (now - begin < 60_000) {
         for (int i = 1; i <= 1000; i++) {
            String result = cache.get(String.valueOf(i));
            String message = "Null after " + (System.currentTimeMillis() - begin) + "ms";
            assertNotNull(result, message);
            now = System.currentTimeMillis();
         }
      }
   }
}
