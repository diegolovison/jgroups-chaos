package com.github.diegolovison;

import static com.github.diegolovison.junit5.JGroupsClusterExtension.builder;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.diegolovison.jgroups.JGroupsCluster;
import com.github.diegolovison.jgroups.Node;
import com.github.diegolovison.jgroups.NodeOr;
import com.github.diegolovison.jgroups.failure.Failure;
import com.github.diegolovison.junit5.JGroupsClusterExtension;

public class JGroupsChaosPartitionTest {

   @RegisterExtension
   JGroupsClusterExtension clusterExtension = builder().build();

   @Test
   void testClusterPartition() {
      JGroupsCluster cluster = clusterExtension.createCluster();

      // Given: 3 nodes
      List<Node> nodes = cluster.createNodes(3);
      Node node1 = nodes.get(0);
      Node node2 = nodes.get(1);
      Node node3 = nodes.get(2);

      // When: there is a network partition
      cluster.createFailure(Failure.NetworkPartition, new Node[]{node1, node2}, new Node[]{node3});

      // Then: two cluster will form
      assertTrue(new NodeOr(node1, node2).isCoordinator());
      assertTrue(node3.isCoordinator());
   }

}
