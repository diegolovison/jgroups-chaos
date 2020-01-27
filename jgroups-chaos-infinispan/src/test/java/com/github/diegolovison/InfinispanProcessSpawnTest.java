package com.github.diegolovison;

import static com.github.diegolovison.infinispan.junit5.InfinispanClusterExtension.builder;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.diegolovison.infinispan.InfinispanCluster;
import com.github.diegolovison.infinispan.InfinispanNode;
import com.github.diegolovison.infinispan.junit5.InfinispanClusterExtension;
import com.github.diegolovison.os.ChaosProcessType;

public class InfinispanProcessSpawnTest {

   @RegisterExtension
   InfinispanClusterExtension clusterExtension = builder().processType(ChaosProcessType.SPAWN).build();

   @Test
   void testSpawnServer() {

      // Given: the cluster
      InfinispanCluster cluster = clusterExtension.infinispanCluster();

      // When: the nodes are created
      final int numberOfNodes = 2;
      List<InfinispanNode> nodes = cluster.createNodes("ispn-config/infinispan-base-config.xml", numberOfNodes);

      // Then: the cluster will be ready
      assertEquals(numberOfNodes, nodes.size());
      assertEquals(numberOfNodes, cluster.size());
   }
}
