package com.github.diegolovison;

import static com.github.diegolovison.junit5.ClusterExtension.builder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.diegolovison.jgroups.Cluster;
import com.github.diegolovison.jgroups.Node;
import com.github.diegolovison.junit5.ClusterExtension;

public class JGroupsChaosTest {

   @RegisterExtension
   ClusterExtension clusterExtension = builder().build();

   @Test
   void testCluster() {
      Cluster cluster = clusterExtension.getCluster();

      // Given: two nodes
      cluster.createNodes(2);
      Node node1 = cluster.get(0);
      Node node2 = cluster.get(1);

      // When: one node leave the cluster
      cluster.discard(node1);

      // Then: cluster have 1 node
      assert cluster.size() == 1;
   }
}
