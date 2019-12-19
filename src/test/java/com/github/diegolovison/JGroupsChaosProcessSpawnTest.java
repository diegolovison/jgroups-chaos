package com.github.diegolovison;

import static com.github.diegolovison.junit5.JGroupsClusterExtension.builder;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.diegolovison.jgroups.JGroupsCluster;
import com.github.diegolovison.jgroups.Node;
import com.github.diegolovison.junit5.JGroupsClusterExtension;
import com.github.diegolovison.os.ChaosProcessType;

public class JGroupsChaosProcessSpawnTest {

   @RegisterExtension
   JGroupsClusterExtension clusterExtension = builder().processType(ChaosProcessType.SPAWN).build();

   @Test
   void testSpawnServer() {
      // Given: the cluster
      JGroupsCluster cluster = clusterExtension.jGroupsCluster();

      // When: the nodes are created
      int numberOfNodes = 2;
      List<Node> nodes = cluster.createNodes(numberOfNodes);

      // Then: the cluster will be ready
      assertEquals(numberOfNodes, nodes.size());
      assertEquals(numberOfNodes, cluster.size());
   }
}
