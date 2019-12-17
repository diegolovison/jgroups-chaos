package com.github.diegolovison;

import static com.github.diegolovison.junit5.ClusterExtension.builder;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.diegolovison.jgroups.JGroupsCluster;
import com.github.diegolovison.jgroups.Node;
import com.github.diegolovison.junit5.ClusterExtension;

public class JGroupsChaosClusterTest {

   @RegisterExtension
   ClusterExtension clusterExtension = builder().build();

   @Test
   void testClusterFormation() {
      JGroupsCluster cluster = clusterExtension.createCluster();

      // Given: two nodes
      List<Node> nodes = cluster.createNodes(2, false);

      // When: the nodes join the cluster
      cluster.form();

      // Then: cluster have 2 node
      assertEquals(nodes.size(), cluster.size());
   }

   @Test
   void testClusterNodeClose() {
      JGroupsCluster cluster = clusterExtension.createCluster();

      // Given: two nodes
      List<Node> nodes = cluster.createNodes(2);
      Node node1 = nodes.get(0);
      Node node2 = nodes.get(1);

      assertEquals(2, cluster.size());

      // When: one node leave the cluster
      cluster.close(node1);

      // Then: cluster have 1 node
      assertEquals(1, cluster.size());
   }
}
