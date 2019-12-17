package com.github.diegolovison;

import static com.github.diegolovison.junit5.InfinispanClusterExtension.builder;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.diegolovison.infinispan.InfinispanCluster;
import com.github.diegolovison.jgroups.Node;
import com.github.diegolovison.jgroups.NodeOr;
import com.github.diegolovison.jgroups.failure.Failure;
import com.github.diegolovison.junit5.InfinispanClusterExtension;

public class InfinispanPartitionHandlingTest {

   @RegisterExtension
   InfinispanClusterExtension clusterExtension = builder().build();

   @Test
   void testMergePolicyRemoveAll() {
      InfinispanCluster cluster = clusterExtension.createCluster();

      // Given: 3 nodes
      List<Node> nodes = cluster.createNodes(() -> {
         GlobalConfigurationBuilder globalConfigurationBuilder = GlobalConfigurationBuilder.defaultClusteredBuilder();
         globalConfigurationBuilder.transport();
         return globalConfigurationBuilder;
      }, 3);
      Node node1 = nodes.get(0);
      Node node2 = nodes.get(1);
      Node node3 = nodes.get(2);

      // When: there is a cluster split
      cluster.createFailure(Failure.NetworkPartition, new Node[]{node1, node2}, new Node[]{node3});

      // Then: cluster will be splitted
      assertTrue(new NodeOr(node1, node2).isCoordinator());
      assertTrue(node3.isCoordinator());
   }
}
