package com.github.diegolovison;

import static com.github.diegolovison.junit5.JGroupsClusterExtension.builder;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.diegolovison.jgroups.JGroupsCluster;
import com.github.diegolovison.jgroups.Node;
import com.github.diegolovison.junit5.JGroupsClusterExtension;

public class JGroupsChaosClusterTest {

   @RegisterExtension
   JGroupsClusterExtension clusterExtension = builder().build();

   @Test
   void testClusterFormation() {
      JGroupsCluster cluster = clusterExtension.jGroupsCluster();

      // Given: two nodes
      int numberOfNodes = 2;
      List<Node> nodes = cluster.createNodes(numberOfNodes, false);

      // When: the nodes join the cluster
      cluster.form(numberOfNodes);

      // Then: cluster have 2 nodes
      assertEquals(numberOfNodes, cluster.size());
      assertEquals(numberOfNodes, nodes.size());
   }

   @Test
   void testClusterNodeDisconnect() {
      JGroupsCluster cluster = clusterExtension.jGroupsCluster();

      // Given: two nodes
      List<Node> nodes = cluster.createNodes(2);
      Node node1 = nodes.get(0);
      Node node2 = nodes.get(1);

      assertEquals(2, cluster.size());

      // When: one node leave the cluster
      cluster.disconnect(node1);

      // Then: cluster have 1 node
      assertEquals(1, cluster.size());
   }
}
