package com.github.diegolovison.jgroups;

import java.util.ArrayList;
import java.util.List;

/**
 * All operations should be called from {@link com.github.diegolovison.junit5.ClusterExtension}
 */
class Nodes {

   private List<Node> nodes;

   Nodes() {
      this.nodes = new ArrayList<>(0);
   }

   Node get(int index) {
      return this.nodes.get(index);
   }

   void createNode(NodeConfig nodeConfig) {
      Node node = new Node(nodeConfig);
      this.nodes.add(node);
   }

   int size() {
      return this.nodes.size();
   }

   void remove(Node node) {
      this.nodes.remove(node);
   }
}
