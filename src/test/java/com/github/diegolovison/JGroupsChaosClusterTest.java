package com.github.diegolovison;

import static com.github.diegolovison.junit5.ClusterExtension.builder;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.diegolovison.jgroups.Cluster;
import com.github.diegolovison.jgroups.Node;
import com.github.diegolovison.junit5.ClusterExtension;

public class JGroupsChaosClusterTest {

   @RegisterExtension
   ClusterExtension clusterExtension = builder().build();

   @Test
   void testClusterFormation() {
      Cluster cluster = clusterExtension.getCluster();

      // Given: two nodes
      int clusterSize = 2;
      cluster.createNodes(clusterSize, false);

      // When: the nodes join the cluster
      cluster.form();

      // Then: cluster have 2 node
      assertEquals(clusterSize, cluster.size());
   }

   @Test
   void testClusterNodeClose() {
      Cluster cluster = clusterExtension.getCluster();

      // Given: two nodes
      cluster.createNodes(2);
      Node node1 = cluster.get(0);
      Node node2 = cluster.get(1);

      assertEquals(2, cluster.size());

      // When: one node leave the cluster
      cluster.close(node1);

      // Then: cluster have 1 node
      assertEquals(1, cluster.size());
   }
}
